/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.tools.jdbc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Source;
import org.jooq.exception.MockFileDatabaseException;
import org.jooq.impl.DSL;
import org.jooq.tools.JooqLogger;

/**
 * A file-based {@link MockDataProvider}.
 * <p>
 * This data provider reads a database model from a text file, as documented in
 * the below sample file: <code><pre>
 * # Comments start off with a hash
 *
 * # Statement strings have no prefix and should be ended with a semi-colon
 * select 'A' from dual;
 * # Statements may be followed by results, using &gt;
 * &gt; A
 * &gt; -
 * &gt; A
 * # Statements should be followed by "&#64; rows: [N]" indicating the update count
 * &#64; rows: 1
 *
 * # New statements can be listed int his file
 * select 'A', 'B' from dual;
 * &gt; A B
 * &gt; - -
 * &gt; A B
 * &#64; rows: 1
 *
 * # Beware of the exact syntax (e.g. using quotes)
 * select "TABLE1"."ID1", "TABLE1"."NAME1" from "TABLE1";
 * &gt; ID1 NAME1
 * &gt; --- -----
 * &gt; 1   X
 * &gt; 2   Y
 * &#64; rows: 2
 *
 * # Statements can return several results
 * &gt; F1  F2  F3 is a bit more complex
 * &gt; --- --  ----------------------------
 * &gt; 1   2   and a string containing data
 * &gt; 1.1 x   another string
 * &#64; rows: 2
 *
 * &gt; A B "C D"
 * &gt; - - -----
 * &gt; x y z
 * &#64; rows: 1
 * </pre></code>
 * <p>
 * Results can be loaded using several techniques:
 * <ul>
 * <li>When results are prefixed with <code>&gt;</code>, then
 * {@link DSLContext#fetchFromTXT(String)} is used</li>
 * <li>In the future, other types of result sources will be supported, such as
 * CSV, XML, JSON</li>
 * </ul>
 *
 * @author Lukas Eder
 * @author Samy Deghou
 */
public class MockFileDatabase implements MockDataProvider {

    private static final JooqLogger              log = JooqLogger.getLogger(MockFileDatabase.class);

    private final MockFileDatabaseConfiguration  configuration;
    private final Map<String, List<MockResult>>  matchExactly;
    private final Map<Pattern, List<MockResult>> matchPattern;
    private final DSLContext                     create;

    @Deprecated
    private String                               nullLiteral;

    public MockFileDatabase(File file) throws IOException {
        this(new MockFileDatabaseConfiguration().source(file));
    }

    public MockFileDatabase(File file, String encoding) throws IOException {
        this(new MockFileDatabaseConfiguration().source(file, encoding));
    }

    public MockFileDatabase(InputStream stream) throws IOException {
        this(new MockFileDatabaseConfiguration().source(stream));
    }

    public MockFileDatabase(InputStream stream, String encoding) throws IOException {
        this(new MockFileDatabaseConfiguration().source(stream, encoding));
    }

    public MockFileDatabase(Reader reader) throws IOException {
        this(new MockFileDatabaseConfiguration().source(reader));
    }

    public MockFileDatabase(String string) throws IOException {
        this(new MockFileDatabaseConfiguration().source(string));
    }

    public MockFileDatabase(Source source) throws IOException {
        this(new MockFileDatabaseConfiguration().source(source));
    }

    /**
     * Specify the <code>null</code> literal, i.e. the string that should be
     * parsed as a <code>null</code> reference, rather than as the string
     * itself.
     *
     * @see DSLContext#fetchFromTXT(String, String)
     * @deprecated - Use
     *             {@link MockFileDatabaseConfiguration#nullLiteral(String)}
     *             instead.
     */
    @Deprecated
    public MockFileDatabase nullLiteral(String literal) {
        this.nullLiteral = literal;
        return this;
    }

    public MockFileDatabase(MockFileDatabaseConfiguration configuration) throws IOException {
        this.configuration = configuration;
        this.matchExactly = new LinkedHashMap<>();
        this.matchPattern = new LinkedHashMap<>();
        this.create = DSL.using(SQLDialect.DEFAULT);

        load();
    }

    private static final Pattern END_OF_STATEMENT = Pattern.compile("^(.*?);[ \t]*$");

    private void load() throws FileNotFoundException, IOException {

        // Wrap the below code in a local scope
        new Object() {
            private StringBuilder    currentSQL    = new StringBuilder();
            private StringBuilder    currentResult = new StringBuilder();
            private String           previousSQL   = null;

            private void load() throws FileNotFoundException, IOException {
                try {
                    while (true) {
                        Matcher matcher;
                        String line = readLine();

                        // End of file reached
                        if (line == null) {

                            // The file was ended, but the previous data was
                            // not yet terminated
                            if (currentResult.length() > 0) {
                                loadOneResult("");
                                currentResult = new StringBuilder();
                            }

                            break;
                        }

                        // Comments are ignored
                        else if (line.startsWith("#")) {
                            continue;
                        }

                        // A line of result data
                        else if (line.startsWith(">")) {
                            currentResult.append(line.substring(2));
                            currentResult.append("\n");
                        }

                        // A result data termination literal
                        else if (line.startsWith("@")) {
                            loadOneResult(line);
                            currentResult = new StringBuilder();
                        }

                        // A terminated line of SQL
                        else if ((matcher = END_OF_STATEMENT.matcher(line)).matches()) {

                            // [#5921] Preserve newlines
                            if (currentSQL.length() > 0)
                                currentSQL.append('\n');

                            currentSQL.append(matcher.group(1));

                            if (previousSQL != null)
                                if (!matchExactly.containsKey(previousSQL))
                                    matchExactly.put(previousSQL, null);

                            previousSQL = currentSQL.toString();
                            currentSQL = new StringBuilder();

                            if (log.isDebugEnabled())
                                log.debug("Loaded SQL", previousSQL);
                        }

                        // A non-terminated line of SQL
                        else {

                            // A new SQL statement is created, but the previous
                            // data was not yet terminated
                            if (currentResult.length() > 0) {
                                loadOneResult("");
                                currentResult = new StringBuilder();
                            }

                            // [#5921] Preserve newlines
                            if (currentSQL.length() > 0)
                                currentSQL.append('\n');

                            currentSQL.append(line);
                        }
                    }
                }
                finally {
                    if (configuration.in != null)
                        configuration.in.close();
                }
            }

            private void loadOneResult(String line) {
                List<MockResult> results = matchExactly.get(previousSQL);

                if (results == null) {
                    results = new ArrayList<>();

                    if (configuration.patterns) {
                        try {
                            Pattern p = Pattern.compile(previousSQL);
                            matchPattern.put(p, results);
                        }
                        catch (PatternSyntaxException e) {
                            throw new MockFileDatabaseException("Not a pattern: " + previousSQL, e);
                        }
                    }
                    else {
                        matchExactly.put(previousSQL, results);
                    }
                }

                MockResult mock = parse(line);
                results.add(mock);

                if (mock.data != null && log.isDebugEnabled()) {
                    String comment = "Loaded Result";

                    for (String l : mock.data.format(5).split("\n")) {
                        log.debug(comment, l);
                        comment = "";
                    }
                }
            }

            private MockResult parse(String rowString) {
                int rows = 0;
                SQLException exception = null;

                if (rowString.startsWith("@ rows:"))
                    rows = Integer.parseInt(rowString.substring(7).trim());
                if (rowString.startsWith("@ exception:"))
                    exception = new SQLException(rowString.substring(12).trim());

                String resultText = currentResult.toString();
                String trimmed = resultText.trim();
                MockResult result =
                      exception != null
                    ? new MockResult(exception)
                    : resultText.isEmpty()
                    ? new MockResult(rows)
                    : trimmed.startsWith("<")
                    ? new MockResult(rows, create.fetchFromXML(resultText))
                    : trimmed.startsWith("{") || trimmed.startsWith("[")
                    ? new MockResult(rows, create.fetchFromJSON(resultText))
                    : new MockResult(rows,
                          configuration.nullLiteral == null && nullLiteral == null
                        ? create.fetchFromTXT(resultText)
                        : configuration.nullLiteral != null
                        ? create.fetchFromTXT(resultText, configuration.nullLiteral)
                        : create.fetchFromTXT(resultText, nullLiteral)
                      );

                if (result.data != null && rows != result.data.size())
                    throw new MockFileDatabaseException("Rows mismatch. Declared: " + rows + ". Actual: " + result.data.size() + ".");

                return result;
            }

            private String readLine() throws IOException {
                while (true) {
                    String line = configuration.in.readLine();

                    if (line == null)
                        return line;

                    // Skip empty lines
                    if (line.length() > 0 && line.trim().length() > 0)
                        return line;
                }
            }
        }.load();
    }

    /**
     * @deprecated - Experimental: Do not use. Subject to change.
     */
    @Deprecated
    public Map<String, List<MockResult>> queries() {
        return matchExactly;
    }

    @Override
    public MockResult[] execute(MockExecuteContext ctx) throws SQLException {
        if (ctx.batch()) {
            throw new SQLFeatureNotSupportedException("Not yet supported");
        }
        else {
            String sql = ctx.sql();
            String inlined = null;

            // Check for an exact match
            List<MockResult> list = matchExactly.get(sql);

            // Check again, with inlined bind values
            if (list == null) {
                inlined = create.query(sql, ctx.bindings()).toString();
                list = matchExactly.get(inlined);
            }

            // Check for the first pattern match
            if (list == null) {

                patternLoop:
                for (Entry<Pattern, List<MockResult>> entry : matchPattern.entrySet()) {
                    if (    entry.getKey().matcher(sql).matches()
                         || entry.getKey().matcher(inlined).matches()) {
                        list = entry.getValue();
                        break patternLoop;
                    }
                }
            }

            // [#9078] Listing possible reasons for this to happen
            if (list == null)
                throw new SQLException("Invalid SQL: " + sql
                    + "\nPossible reasons include: "
                    + "\n  Your regular expressions are case sensitive."
                    + "\n  Your regular expressions use constant literals (e.g. 'Hello'), but the above SQL string uses bind variable placeholders (e.g. ?)."
                    + "\n  Your regular expressions did not quote special characters (e.g. \\?)."
                    + "\n  Your regular expressions' whitespace doesn't match the input SQL's whitespace.");

            return list.toArray(new MockResult[list.size()]);
        }
    }
}
