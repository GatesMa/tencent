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
package org.jooq.codegen;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.jooq.tools.StringUtils.defaultIfNull;
import static org.jooq.tools.StringUtils.defaultString;
import static org.jooq.tools.StringUtils.isBlank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.jooq.Constants;
import org.jooq.Log.Level;
import org.jooq.meta.CatalogVersionProvider;
import org.jooq.meta.Database;
import org.jooq.meta.Databases;
import org.jooq.meta.Definition;
import org.jooq.meta.SchemaVersionProvider;
import org.jooq.meta.jaxb.CatalogMappingType;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.ForcedType;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Logging;
import org.jooq.meta.jaxb.Matchers;
import org.jooq.meta.jaxb.OnError;
import org.jooq.meta.jaxb.Property;
import org.jooq.meta.jaxb.SchemaMappingType;
import org.jooq.meta.jaxb.Strategy;
import org.jooq.meta.jaxb.Target;
// ...
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;
import org.jooq.tools.jdbc.JDBCUtils;
import org.jooq.util.jaxb.tools.MiniJAXB;


/**
 * The GenerationTool takes care of generating Java code from a database schema.
 * <p>
 * It takes its configuration parameters from an XML file passed in either as a
 * JAXB-annotated {@link Configuration} object, or from the file system when
 * passed as an argument to {@link #main(String[])}.
 * <p>
 * See <a href="http://www.jooq.org/xsd/">http://www.jooq.org/xsd/</a> for the
 * latest XSD specification.
 *
 * @author Lukas Eder
 */
public class GenerationTool {

    public static final String      DEFAULT_TARGET_ENCODING    = "UTF-8";
    public static final String      DEFAULT_TARGET_DIRECTORY   = "target/generated-sources/jooq";
    public static final String      DEFAULT_TARGET_PACKAGENAME = "org.jooq.generated";

    private static final JooqLogger log = JooqLogger.getLogger(GenerationTool.class);

    private ClassLoader             loader;
    private DataSource              dataSource;
    private Connection              connection;
    private Boolean                 autoCommit;
    private boolean                 close;

    /**
     * The class loader to use with this generation tool.
     * <p>
     * If set, all classes are loaded with this class loader
     */
    public void setClassLoader(ClassLoader loader) {
        this.loader = loader;
    }

    /**
     * The JDBC connection to use with this generation tool.
     * <p>
     * If set, the configuration XML's <code>&lt;jdbc/&gt;</code> configuration is
     * ignored, and this connection is used for meta data inspection, instead.
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * The JDBC data source to use with this generation tool.
     * <p>
     * If set, the configuration XML's <code>&lt;jdbc/&gt;</code> configuration is
     * ignored, and this connection is used for meta data inspection, instead.
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) throws Exception {
        String[] files;

        if (args.length > 0) {
            files = args;
        }
        else {
            String property = System.getProperty("jooq.codegen.configurationFile");

            if (property != null) {
                files = new String[] { property };
            }
            else {
                log.error("Usage : GenerationTool <configuration-file>");
                System.exit(-1);
                return;
            }
        }

        for (String file : files) {
            InputStream in = GenerationTool.class.getResourceAsStream(file);

            try {

                // [#2932] Retry loading the file, if it wasn't found. This may be helpful
                // to some users who were unaware that this file is loaded from the classpath
                if (in == null && !file.startsWith("/"))
                    in = GenerationTool.class.getResourceAsStream("/" + file);

                // [#3668] Also check the local file system for configuration files
                if (in == null && new File(file).exists())
                    in = new FileInputStream(new File(file));

                if (in == null) {
                    log.error("Cannot find " + file + " on classpath, or in directory " + new File(".").getCanonicalPath());
                    log.error("-----------");
                    log.error("Please be sure it is located");
                    log.error("  - on the classpath and qualified as a classpath location.");
                    log.error("  - in the local directory or at a global path in the file system.");

                    System.exit(-1);
                    return;
                }

                log.info("Initialising properties", file);
                generate(load(in));
            }
            catch (Exception e) {
                log.error("Cannot read " + file + ". Error : " + e.getMessage(), e);

                System.exit(-1);
                return;
            }
            finally {
                if (in != null)
                    in.close();
            }
        }
    }

    /**
     * @deprecated - Use {@link #generate(Configuration)} instead
     */
    @Deprecated
    public static void main(Configuration configuration) throws Exception {
        new GenerationTool().run(configuration);
    }

    public static void generate(String xml) throws Exception {
        new GenerationTool().run(load(new ByteArrayInputStream(xml.getBytes(DEFAULT_TARGET_ENCODING))));
    }

    public static void generate(Configuration configuration) throws Exception {
        new GenerationTool().run(configuration);
    }

    public void run(Configuration configuration) throws Exception {
        try {
            run0(configuration);
        }
        catch (Exception e) {
            OnError onError = configuration.getOnError();
            if (onError == null) {
                onError = OnError.FAIL;
            }
            switch (onError) {
                case SILENT:
                    break;
                case LOG:
                    log.warn("Code generation failed", e);
                    break;
                case FAIL:
                    throw e;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void run0(Configuration configuration) throws Exception {
        if (Boolean.getBoolean("jooq.codegen.skip")) {
            log.info("Skipping jOOQ code generation");
            return;
        }

        if (configuration.getLogging() != null) {
            switch (configuration.getLogging()) {
                case TRACE:
                    JooqLogger.globalThreshold(Level.TRACE);
                    break;
                case DEBUG:
                    JooqLogger.globalThreshold(Level.DEBUG);
                    break;
                case INFO:
                    JooqLogger.globalThreshold(Level.INFO);
                    break;
                case WARN:
                    JooqLogger.globalThreshold(Level.WARN);
                    break;
                case ERROR:
                    JooqLogger.globalThreshold(Level.ERROR);
                    break;
                case FATAL:
                    JooqLogger.globalThreshold(Level.FATAL);
                    break;
            }
        }
        else {
            String property = System.getProperty("jooq.codegen.logging");

            if (property != null) {
                try {
                    Logging.valueOf(property);
                }
                catch (IllegalArgumentException e) {
                    log.error("Unsupported property", "Unsupported value for system property jooq.codegen.logging: " + property + ". Supported values include: " + Arrays.asList(Logging.values()));
                }
            }
        }

        if (log.isDebugEnabled())
            log.debug("Input configuration", "" + configuration);

        // [#9727] The Maven plugin will have set the basedir to Maven's ${basedir}.
        //         Standalone code generation should use the JVM's working dir as basedir, by default.
        if (configuration.getBasedir() == null)
            configuration.setBasedir(new File(".").getAbsolutePath());

        Jdbc j = configuration.getJdbc();
        org.jooq.meta.jaxb.Generator g = configuration.getGenerator();
        if (g == null)
            throw new GeneratorException("The <generator/> tag is mandatory. For details, see " + Constants.NS_CODEGEN);

        org.jooq.meta.jaxb.Database d = defaultIfNull(g.getDatabase(), new org.jooq.meta.jaxb.Database());
        String databaseName = trim(d.getName());

        // [#1394] The <generate/> element and some others should be optional
        if (g.getGenerate() == null)
            g.setGenerate(new Generate());
        if (g.getStrategy() == null)
            g.setStrategy(new Strategy());
        if (g.getTarget() == null)
            g.setTarget(new Target());

        Database database = null;

        try {

            // Initialise connection
            // ---------------------
            if (connection == null) {
                close = true;

                if (dataSource != null) {
                    connection = dataSource.getConnection();
                }
                else {
                    String url = System.getProperty("jooq.codegen.jdbc.url");

                    if (url != null) {
                        j = defaultIfNull(j, new Jdbc());

                        if (j.getDriver() == null)
                            j.setDriver(System.getProperty("jooq.codegen.jdbc.driver"));
                        if (j.getUrl() == null)
                            j.setUrl(url);
                        if (j.getUser() == null)
                            j.setUser(System.getProperty("jooq.codegen.jdbc.user"));
                        if (j.getUsername() == null)
                            j.setUsername(System.getProperty("jooq.codegen.jdbc.username"));
                        if (j.getPassword() == null)
                            j.setPassword(System.getProperty("jooq.codegen.jdbc.password"));

                        if (j.isAutoCommit() == null) {
                            String a = System.getProperty("jooq.codegen.jdbc.autoCommit");

                            if (a != null)
                                j.setAutoCommit(Boolean.valueOf(a));
                        }
                    }

                    if (j != null) {
                        try {
                            Class<? extends Driver> driver = (Class<? extends Driver>) loadClass(driverClass(j));

                            Properties properties = properties(j.getProperties());
                            if (!properties.containsKey("user"))
                                properties.put("user", defaultString(defaultString(j.getUser(), j.getUsername())));
                            if (!properties.containsKey("password"))
                                properties.put("password", defaultString(j.getPassword()));

                            connection = driver.newInstance().connect(defaultString(j.getUrl()), properties);
                        }
                        catch (Exception e) {
                            if (databaseName != null)
                                if (databaseName.contains("DDLDatabase") || databaseName.contains("XMLDatabase") || databaseName.contains("JPADatabase"))
                                    log.warn("Error while connecting to database. Note that file based database implementations do not need a <jdbc/> configuration in the code generator.", e);

                            throw e;
                        }
                    }
                }
            }

            j = defaultIfNull(j, new Jdbc());

            if (connection != null && j.isAutoCommit() != null) {
                autoCommit = connection.getAutoCommit();
                connection.setAutoCommit(j.isAutoCommit());
            }

            // Initialise generator
            // --------------------
            Class<Generator> generatorClass = (Class<Generator>) (!isBlank(g.getName())
                ? loadClass(trim(g.getName()))
                : JavaGenerator.class);
            Generator generator = generatorClass.newInstance();

            GeneratorStrategy strategy;

            Matchers matchers = g.getStrategy().getMatchers();
            if (matchers != null) {
                strategy = new MatcherStrategy(matchers);

                if (g.getStrategy().getName() != null) {

                    // [#7416] Depending on who is unmarshalling the Configuration (Maven / JAXB),
                    //         the XSD's default value might apply, which we can safely ignore.
                    if (!DefaultGeneratorStrategy.class.getName().equals(g.getStrategy().getName()))
                        log.warn("WARNING: Matchers take precedence over custom strategy. Strategy ignored: " +
                            g.getStrategy().getName());

                    g.getStrategy().setName(null);
                }
            }
            else {
                Class<GeneratorStrategy> strategyClass = (Class<GeneratorStrategy>) (!isBlank(g.getStrategy().getName())
                    ? loadClass(trim(g.getStrategy().getName()))
                    : DefaultGeneratorStrategy.class);
                strategy = strategyClass.newInstance();
            }

            generator.setStrategy(strategy);

            Class<? extends Database> databaseClass = !isBlank(databaseName)
                ? (Class<? extends Database>) loadClass(databaseName)
                : connection != null
                ? databaseClass(connection)
                : databaseClass(j);
            database = databaseClass.newInstance();
            database.setBasedir(configuration.getBasedir());
            database.setProperties(properties(d.getProperties()));
            database.setOnError(configuration.getOnError());

            List<CatalogMappingType> catalogs = d.getCatalogs();
            List<SchemaMappingType> schemata = d.getSchemata();

            boolean catalogsEmpty = catalogs.isEmpty();
            boolean schemataEmpty = schemata.isEmpty();

            // For convenience, the catalog configuration can be set also directly in the <database/> element
            if (catalogsEmpty) {
                CatalogMappingType catalog = new CatalogMappingType();
                catalog.setInputCatalog(trim(d.getInputCatalog()));
                catalog.setOutputCatalog(trim(d.getOutputCatalog()));
                catalog.setOutputCatalogToDefault(d.isOutputCatalogToDefault());
                catalogs.add(catalog);

                if (!StringUtils.isBlank(catalog.getInputCatalog()))
                    catalogsEmpty = false;

                // For convenience and backwards-compatibility, the schema configuration can be set also directly
                // in the <database/> element
                if (schemataEmpty) {
                    SchemaMappingType schema = new SchemaMappingType();
                    schema.setInputSchema(trim(d.getInputSchema()));
                    schema.setOutputSchema(trim(d.getOutputSchema()));
                    schema.setOutputSchemaToDefault(d.isOutputSchemaToDefault());
                    catalog.getSchemata().add(schema);

                    if (!StringUtils.isBlank(schema.getInputSchema()))
                        schemataEmpty = false;
                }
                else {
                    catalog.getSchemata().addAll(schemata);

                    if (!StringUtils.isBlank(d.getInputSchema()))
                        log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/inputSchema and /configuration/generator/database/schemata");
                    if (!StringUtils.isBlank(d.getOutputSchema()))
                        log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/outputSchema and /configuration/generator/database/schemata");
                }
            }
            else {
                if (!StringUtils.isBlank(d.getInputCatalog()))
                    log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/inputCatalog and /configuration/generator/database/catalogs");
                if (!StringUtils.isBlank(d.getOutputCatalog()))
                    log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/outputCatalog and /configuration/generator/database/catalogs");
                if (!StringUtils.isBlank(d.getInputSchema()))
                    log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/inputSchema and /configuration/generator/database/catalogs");
                if (!StringUtils.isBlank(d.getOutputSchema()))
                    log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/outputSchema and /configuration/generator/database/catalogs");
                if (!schemataEmpty)
                    log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/catalogs and /configuration/generator/database/schemata");
            }

            for (CatalogMappingType catalog : catalogs) {
                if ("".equals(catalog.getOutputCatalog()))
                    log.warn("WARNING: Empty <outputCatalog/> should not be used to model default outputCatalogs. Use <outputCatalogToDefault>true</outputCatalogToDefault>, instead. See also: https://github.com/jOOQ/jOOQ/issues/3018");

                // [#3018] If users want the output catalog to be "" then, ignore the actual <outputCatalog/> configuration
                if (TRUE.equals(catalog.isOutputCatalogToDefault()))
                    catalog.setOutputCatalog("");
                else if (catalog.getOutputCatalog() == null)
                    catalog.setOutputCatalog(trim(catalog.getInputCatalog()));






                for (SchemaMappingType schema : catalog.getSchemata()) {
                    if (catalogsEmpty && schemataEmpty && StringUtils.isBlank(schema.getInputSchema())) {
                        if (!StringUtils.isBlank(j.getSchema()))
                            log.warn("WARNING: The configuration property jdbc.Schema is deprecated and will be removed in the future. Use /configuration/generator/database/inputSchema instead");

                        schema.setInputSchema(trim(j.getSchema()));
                    }

                    // [#3018] Prior to <outputSchemaToDefault/>, empty <outputSchema/> elements meant that
                    // the outputSchema should be the default schema. This is a bit too clever, and doesn't
                    // work when Maven parses the XML configurations.
                    if ("".equals(schema.getOutputSchema()))
                        log.warn("WARNING: Empty <outputSchema/> should not be used to model default outputSchemas. Use <outputSchemaToDefault>true</outputSchemaToDefault>, instead. See also: https://github.com/jOOQ/jOOQ/issues/3018");

                    // [#3018] If users want the output schema to be "" then, ignore the actual <outputSchema/> configuration
                    if (TRUE.equals(schema.isOutputSchemaToDefault()))
                        schema.setOutputSchema("");
                    else if (schema.getOutputSchema() == null)
                        schema.setOutputSchema(trim(schema.getInputSchema()));











                }
            }

            if (catalogsEmpty)
                log.info("No <inputCatalog/> was provided. Generating ALL available catalogs instead.");
            if (catalogsEmpty && schemataEmpty)
                log.info("No <inputSchema/> was provided. Generating ALL available schemata instead.");

            database.setConnection(connection);
            database.setConfiguredCatalogs(catalogs);
            database.setConfiguredSchemata(schemata);
            database.setIncludes(new String[] { defaultString(d.getIncludes()) });
            database.setExcludes(new String[] { defaultString(d.getExcludes()) });
            database.setIncludeExcludeColumns(TRUE.equals(d.isIncludeExcludeColumns()));
            database.setIncludeForeignKeys(!FALSE.equals(d.isIncludeForeignKeys()));
            database.setIncludePackages(!FALSE.equals(d.isIncludePackages()));
            database.setIncludePackageRoutines(!FALSE.equals(d.isIncludePackageRoutines()));
            database.setIncludePackageUDTs(!FALSE.equals(d.isIncludePackageUDTs()));
            database.setIncludePackageConstants(!FALSE.equals(d.isIncludePackageConstants()));
            database.setIncludeIndexes(!FALSE.equals(d.isIncludeIndexes()));
            database.setIncludeCheckConstraints(!FALSE.equals(d.isIncludeCheckConstraints()));
            database.setIncludeSystemIndexes(TRUE.equals(d.isIncludeSystemIndexes()));
            database.setIncludeSystemCheckConstraints(TRUE.equals(d.isIncludeSystemCheckConstraints()));
            database.setIncludeInvisibleColumns(!FALSE.equals(d.isIncludeInvisibleColumns()));
            database.setIncludePrimaryKeys(!FALSE.equals(d.isIncludePrimaryKeys()));
            database.setIncludeRoutines(!FALSE.equals(d.isIncludeRoutines()));
            database.setIncludeSequences(!FALSE.equals(d.isIncludeSequences()));
            database.setIncludeTables(!FALSE.equals(d.isIncludeTables()));
            database.setIncludeEmbeddables(!FALSE.equals(d.isIncludeEmbeddables()));
            database.setIncludeTriggerRoutines(TRUE.equals(d.isIncludeTriggerRoutines()));
            database.setIncludeUDTs(!FALSE.equals(d.isIncludeUDTs()));
            database.setIncludeUniqueKeys(!FALSE.equals(d.isIncludeUniqueKeys()));
            database.setForceIntegerTypesOnZeroScaleDecimals(!FALSE.equals(d.isForceIntegerTypesOnZeroScaleDecimals()));
            database.setRecordVersionFields(new String[] { defaultString(d.getRecordVersionFields()) });
            database.setRecordTimestampFields(new String[] { defaultString(d.getRecordTimestampFields()) });
            database.setSyntheticPrimaryKeys(new String[] { defaultString(d.getSyntheticPrimaryKeys()) });
            database.setOverridePrimaryKeys(new String[] { defaultString(d.getOverridePrimaryKeys()) });
            database.setSyntheticIdentities(new String[] { defaultString(d.getSyntheticIdentities()) });
            database.setConfiguredCustomTypes(d.getCustomTypes());
            database.setConfiguredEnumTypes(d.getEnumTypes());
            database.setConfiguredForcedTypes(d.getForcedTypes());
            database.setConfiguredEmbeddables(d.getEmbeddables());
            database.setLogSlowQueriesAfterSeconds(defaultIfNull(g.getDatabase().getLogSlowQueriesAfterSeconds(), 5));
            database.setLogSlowResultsAfterSeconds(defaultIfNull(g.getDatabase().getLogSlowResultsAfterSeconds(), 5));

            if (d.getRegexFlags() != null) {
                database.setRegexFlags(d.getRegexFlags());

                if (strategy instanceof MatcherStrategy) {
                    ((MatcherStrategy) strategy).getPatterns().setRegexFlags(d.getRegexFlags());
                }
            }
            database.setRegexMatchesPartialQualification(!FALSE.equals(d.isRegexMatchesPartialQualification()));
            database.setSqlMatchesPartialQualification(!FALSE.equals(d.isSqlMatchesPartialQualification()));

            SchemaVersionProvider svp = null;
            CatalogVersionProvider cvp = null;

            if (!StringUtils.isBlank(d.getSchemaVersionProvider())) {
                try {
                    svp = (SchemaVersionProvider) Class.forName(d.getSchemaVersionProvider()).newInstance();
                    log.info("Using custom schema version provider : " + svp);
                }
                catch (Exception ignore) {
                    if (d.getSchemaVersionProvider().toLowerCase().startsWith("select")) {
                        svp = new SQLSchemaVersionProvider(connection, d.getSchemaVersionProvider());
                        log.info("Using SQL schema version provider : " + d.getSchemaVersionProvider());
                    }
                    else {
                        svp = new ConstantSchemaVersionProvider(d.getSchemaVersionProvider());
                    }
                }
            }

            if (!StringUtils.isBlank(d.getCatalogVersionProvider())) {
                try {
                    cvp = (CatalogVersionProvider) Class.forName(d.getCatalogVersionProvider()).newInstance();
                    log.info("Using custom catalog version provider : " + cvp);
                }
                catch (Exception ignore) {
                    if (d.getCatalogVersionProvider().toLowerCase().startsWith("select")) {
                        cvp = new SQLCatalogVersionProvider(connection, d.getCatalogVersionProvider());
                        log.info("Using SQL catalog version provider : " + d.getCatalogVersionProvider());
                    }
                    else {
                        cvp = new ConstantCatalogVersionProvider(d.getCatalogVersionProvider());
                    }
                }
            }

            if (svp == null)
                svp = new ConstantSchemaVersionProvider(null);
            if (cvp == null)
                cvp = new ConstantCatalogVersionProvider(null);

            database.setSchemaVersionProvider(svp);
            database.setCatalogVersionProvider(cvp);

            if (!StringUtils.isBlank(d.getOrderProvider())) {
                Class<?> orderProvider = Class.forName(d.getOrderProvider());

                if (Comparator.class.isAssignableFrom(orderProvider))
                    database.setOrderProvider((Comparator<Definition>) orderProvider.newInstance());
                else
                    log.warn("Order provider must be of type java.util.Comparator: " + orderProvider);
            }

            if (d.getEnumTypes().size() > 0)
                log.warn("DEPRECATED", "The configuration property /configuration/generator/database/enumTypes is experimental and deprecated and will be removed in the future.");
            if (Boolean.TRUE.equals(d.isDateAsTimestamp()))
                log.warn("DEPRECATED", "The configuration property /configuration/generator/database/dateAsTimestamp is deprecated as it is superseded by custom bindings and converters. It will thus be removed in the future.");

            if (d.isDateAsTimestamp() != null)
                database.setDateAsTimestamp(d.isDateAsTimestamp());
            if (g.getGenerate().isJavaTimeTypes() != null)
                database.setJavaTimeTypes(g.getGenerate().isJavaTimeTypes());
            if (d.isUnsignedTypes() != null)
                database.setSupportsUnsignedTypes(d.isUnsignedTypes());
            if (d.isIntegerDisplayWidths() != null)
                database.setIntegerDisplayWidths(d.isIntegerDisplayWidths());
            if (d.isIgnoreProcedureReturnValues() != null)
                database.setIgnoreProcedureReturnValues(d.isIgnoreProcedureReturnValues());

            if (Boolean.TRUE.equals(d.isIgnoreProcedureReturnValues()))
                log.warn("DEPRECATED", "The <ignoreProcedureReturnValues/> flag is deprecated and used for backwards-compatibility only. It will be removed in the future.");

            if (StringUtils.isBlank(g.getTarget().getPackageName()))
                g.getTarget().setPackageName(DEFAULT_TARGET_PACKAGENAME);
            if (StringUtils.isBlank(g.getTarget().getDirectory()))
                g.getTarget().setDirectory(DEFAULT_TARGET_DIRECTORY);
            if (StringUtils.isBlank(g.getTarget().getEncoding()))
                g.getTarget().setEncoding(DEFAULT_TARGET_ENCODING);

            // [#2887] [#9727] Patch relative paths to take plugin execution basedir into account
            if (!new File(g.getTarget().getDirectory()).isAbsolute())
                g.getTarget().setDirectory(new File(configuration.getBasedir(), g.getTarget().getDirectory()).getCanonicalPath());

            generator.setTargetPackage(g.getTarget().getPackageName());
            generator.setTargetDirectory(g.getTarget().getDirectory());
            generator.setTargetEncoding(g.getTarget().getEncoding());

            if (g.getTarget().isClean() != null)
                generator.setTargetClean(g.getTarget().isClean());

            if (g.getGenerate().isIndexes() != null)
                generator.setGenerateIndexes(g.getGenerate().isIndexes());
            if (g.getGenerate().isRelations() != null)
                generator.setGenerateRelations(g.getGenerate().isRelations());
            if (g.getGenerate().isImplicitJoinPathsToOne() != null)
                generator.setGenerateImplicitJoinPathsToOne(g.getGenerate().isImplicitJoinPathsToOne());
            if (g.getGenerate().isDeprecated() != null)
                generator.setGenerateDeprecated(g.getGenerate().isDeprecated());
            if (g.getGenerate().isDeprecationOnUnknownTypes() != null)
                generator.setGenerateDeprecationOnUnknownTypes(g.getGenerate().isDeprecationOnUnknownTypes());
            if (g.getGenerate().isInstanceFields() != null)
                generator.setGenerateInstanceFields(g.getGenerate().isInstanceFields());
            if (g.getGenerate().isGeneratedAnnotation() != null)
                generator.setGenerateGeneratedAnnotation(g.getGenerate().isGeneratedAnnotation());
            if (g.getGenerate().getGeneratedAnnotationType() != null)
                generator.setGenerateGeneratedAnnotationType(g.getGenerate().getGeneratedAnnotationType());
            if (g.getGenerate().isNonnullAnnotation() != null)
                generator.setGenerateNonnullAnnotation(g.getGenerate().isNonnullAnnotation());
            if (g.getGenerate().getNonnullAnnotationType() != null)
                generator.setGeneratedNonnullAnnotationType(g.getGenerate().getNonnullAnnotationType());
            if (g.getGenerate().isNullableAnnotation() != null)
                generator.setGenerateNullableAnnotation(g.getGenerate().isNullableAnnotation());
            if (g.getGenerate().getNullableAnnotationType() != null)
                generator.setGeneratedNullableAnnotationType(g.getGenerate().getNullableAnnotationType());
            if (g.getGenerate().isRoutines() != null)
                generator.setGenerateRoutines(g.getGenerate().isRoutines());
            if (g.getGenerate().isSequences() != null)
                generator.setGenerateSequences(g.getGenerate().isSequences());
            if (g.getGenerate().isSequenceFlags() != null)
                generator.setGenerateSequenceFlags(g.getGenerate().isSequenceFlags());
            if (g.getGenerate().isUdts() != null)
                generator.setGenerateUDTs(g.getGenerate().isUdts());
            if (g.getGenerate().isTables() != null)
                generator.setGenerateTables(g.getGenerate().isTables());
            if (g.getGenerate().isEmbeddables() != null)
                generator.setGenerateEmbeddables(g.getGenerate().isEmbeddables());
            if (g.getGenerate().isRecords() != null)
                generator.setGenerateRecords(g.getGenerate().isRecords());
            if (g.getGenerate().isRecordsImplementingRecordN() != null)
                generator.setGenerateRecordsImplementingRecordN(g.getGenerate().isRecordsImplementingRecordN());
            if (g.getGenerate().isPojos() != null)
                generator.setGeneratePojos(g.getGenerate().isPojos());
            if (g.getGenerate().isImmutablePojos() != null)
                generator.setGenerateImmutablePojos(g.getGenerate().isImmutablePojos());
            if (g.getGenerate().isSerializablePojos() != null)
                generator.setGenerateSerializablePojos(g.getGenerate().isSerializablePojos());
            if (g.getGenerate().isInterfaces() != null)
                generator.setGenerateInterfaces(g.getGenerate().isInterfaces());
            if (g.getGenerate().isImmutableInterfaces() != null)
                generator.setGenerateImmutableInterfaces(g.getGenerate().isImmutableInterfaces());
            if (g.getGenerate().isSerializableInterfaces() != null)
                generator.setGenerateSerializableInterfaces(g.getGenerate().isSerializableInterfaces());
            if (g.getGenerate().isDaos() != null)
                generator.setGenerateDaos(g.getGenerate().isDaos());
            if (g.getGenerate().isJpaAnnotations() != null)
                generator.setGenerateJPAAnnotations(g.getGenerate().isJpaAnnotations());
            if (g.getGenerate().getJpaVersion() != null)
                generator.setGenerateJPAVersion(g.getGenerate().getJpaVersion());
            if (g.getGenerate().isValidationAnnotations() != null)
                generator.setGenerateValidationAnnotations(g.getGenerate().isValidationAnnotations());
            if (g.getGenerate().isSpringAnnotations() != null)
                generator.setGenerateSpringAnnotations(g.getGenerate().isSpringAnnotations());
            if (g.getGenerate().isQueues() != null)
                generator.setGenerateQueues(g.getGenerate().isQueues());
            if (g.getGenerate().isLinks() != null)
                generator.setGenerateLinks(g.getGenerate().isLinks());
            if (g.getGenerate().isKeys() != null)
                generator.setGenerateKeys(g.getGenerate().isKeys());
            if (g.getGenerate().isGlobalObjectReferences() != null)
                generator.setGenerateGlobalObjectReferences(g.getGenerate().isGlobalObjectReferences());
            if (g.getGenerate().isGlobalCatalogReferences() != null)
                generator.setGenerateGlobalCatalogReferences(g.getGenerate().isGlobalCatalogReferences());
            if (g.getGenerate().isGlobalSchemaReferences() != null)
                generator.setGenerateGlobalSchemaReferences(g.getGenerate().isGlobalSchemaReferences());
            if (g.getGenerate().isGlobalRoutineReferences() != null)
                generator.setGenerateGlobalRoutineReferences(g.getGenerate().isGlobalRoutineReferences());
            if (g.getGenerate().isGlobalSequenceReferences() != null)
                generator.setGenerateGlobalSequenceReferences(g.getGenerate().isGlobalSequenceReferences());
            if (g.getGenerate().isGlobalTableReferences() != null)
                generator.setGenerateGlobalTableReferences(g.getGenerate().isGlobalTableReferences());
            if (g.getGenerate().isGlobalUDTReferences() != null)
                generator.setGenerateGlobalUDTReferences(g.getGenerate().isGlobalUDTReferences());
            if (g.getGenerate().isGlobalQueueReferences() != null)
                generator.setGenerateGlobalQueueReferences(g.getGenerate().isGlobalQueueReferences());
            if (g.getGenerate().isGlobalLinkReferences() != null)
                generator.setGenerateGlobalLinkReferences(g.getGenerate().isGlobalLinkReferences());
            if (g.getGenerate().isGlobalKeyReferences() != null)
                generator.setGenerateGlobalKeyReferences(g.getGenerate().isGlobalKeyReferences());
            if (g.getGenerate().isGlobalIndexReferences() != null)
                generator.setGenerateGlobalIndexReferences(g.getGenerate().isGlobalIndexReferences());
            if (g.getGenerate().isJavadoc() != null)
                generator.setGenerateJavadoc(g.getGenerate().isJavadoc());
            if (g.getGenerate().isComments() != null)
                generator.setGenerateComments(g.getGenerate().isComments());
            if (g.getGenerate().isCommentsOnAttributes() != null)
                generator.setGenerateCommentsOnAttributes(g.getGenerate().isCommentsOnAttributes());
            if (g.getGenerate().isCommentsOnCatalogs() != null)
                generator.setGenerateCommentsOnCatalogs(g.getGenerate().isCommentsOnCatalogs());
            if (g.getGenerate().isCommentsOnColumns() != null)
                generator.setGenerateCommentsOnColumns(g.getGenerate().isCommentsOnColumns());
            if (g.getGenerate().isCommentsOnKeys() != null)
                generator.setGenerateCommentsOnKeys(g.getGenerate().isCommentsOnKeys());
            if (g.getGenerate().isCommentsOnLinks() != null)
                generator.setGenerateCommentsOnLinks(g.getGenerate().isCommentsOnLinks());
            if (g.getGenerate().isCommentsOnPackages() != null)
                generator.setGenerateCommentsOnPackages(g.getGenerate().isCommentsOnPackages());
            if (g.getGenerate().isCommentsOnParameters() != null)
                generator.setGenerateCommentsOnParameters(g.getGenerate().isCommentsOnParameters());
            if (g.getGenerate().isCommentsOnQueues() != null)
                generator.setGenerateCommentsOnQueues(g.getGenerate().isCommentsOnQueues());
            if (g.getGenerate().isCommentsOnRoutines() != null)
                generator.setGenerateCommentsOnRoutines(g.getGenerate().isCommentsOnRoutines());
            if (g.getGenerate().isCommentsOnSchemas() != null)
                generator.setGenerateCommentsOnSchemas(g.getGenerate().isCommentsOnSchemas());
            if (g.getGenerate().isCommentsOnSequences() != null)
                generator.setGenerateCommentsOnSequences(g.getGenerate().isCommentsOnSequences());
            if (g.getGenerate().isCommentsOnTables() != null)
                generator.setGenerateCommentsOnTables(g.getGenerate().isCommentsOnTables());
            if (g.getGenerate().isCommentsOnUDTs() != null)
                generator.setGenerateCommentsOnUDTs(g.getGenerate().isCommentsOnUDTs());
            if (g.getGenerate().isSources() != null)
                generator.setGenerateSources(g.getGenerate().isSources());
            if (g.getGenerate().isSourcesOnViews() != null)
                generator.setGenerateSourcesOnViews(g.getGenerate().isSourcesOnViews());
            if (g.getGenerate().isFluentSetters() != null)
                generator.setGenerateFluentSetters(g.getGenerate().isFluentSetters());
            if (g.getGenerate().isJavaBeansGettersAndSetters() != null)
                generator.setGenerateJavaBeansGettersAndSetters(g.getGenerate().isJavaBeansGettersAndSetters());
            if (g.getGenerate().isVarargSetters() != null)
                generator.setGenerateVarargsSetters(g.getGenerate().isVarargSetters());
            if (g.getGenerate().isPojosEqualsAndHashCode() != null)
                generator.setGeneratePojosEqualsAndHashCode(g.getGenerate().isPojosEqualsAndHashCode());
            if (g.getGenerate().isPojosToString() != null)
                generator.setGeneratePojosToString(g.getGenerate().isPojosToString());
            if (g.getGenerate().getFullyQualifiedTypes() != null)
                generator.setGenerateFullyQualifiedTypes(g.getGenerate().getFullyQualifiedTypes());
            if (g.getGenerate().isJavaTimeTypes() != null)
                generator.setGenerateJavaTimeTypes(g.getGenerate().isJavaTimeTypes());
            if (g.getGenerate().isEmptyCatalogs() != null)
                generator.setGenerateEmptyCatalogs(g.getGenerate().isEmptyCatalogs());
            if (g.getGenerate().isEmptySchemas() != null)
                generator.setGenerateEmptySchemas(g.getGenerate().isEmptySchemas());
            if (g.getGenerate().isPrimaryKeyTypes() != null)
                generator.setGeneratePrimaryKeyTypes(g.getGenerate().isPrimaryKeyTypes());
            if (g.getGenerate().getNewline() != null)
                generator.setGenerateNewline(g.getGenerate().getNewline());
            if (g.getGenerate().getIndentation() != null)
                generator.setGenerateIndentation(g.getGenerate().getIndentation());


            // [#3669] Optional Database element
            if (g.getDatabase() == null)
                g.setDatabase(new org.jooq.meta.jaxb.Database());
            if (!StringUtils.isBlank(g.getDatabase().getSchemaVersionProvider()))
                generator.setUseSchemaVersionProvider(true);
            if (!StringUtils.isBlank(g.getDatabase().getCatalogVersionProvider()))
                generator.setUseCatalogVersionProvider(true);
            if (g.getDatabase().isTableValuedFunctions() != null)
                generator.setGenerateTableValuedFunctions(g.getDatabase().isTableValuedFunctions());
            else {
                generator.setGenerateTableValuedFunctions(true);







            }

            // Generator properties that should in fact be strategy properties
            strategy.setInstanceFields(generator.generateInstanceFields());
            strategy.setJavaBeansGettersAndSetters(generator.generateJavaBeansGettersAndSetters());















            generator.generate(database);

            if (!database.getUnusedForcedTypes().isEmpty()) {
                log.info("Unused ForcedTypes", "There are unused forced types, which have not been used by this generation run. This can be because of misconfigurations (e.g. bad regular expressions, which do not take into account case sensitivity or object qualification) or because the forced type is obsolete.");

                for (ForcedType f : database.getUnusedForcedTypes())
                    log.info("Unused ForcedType", f);
            }
        }
        finally {
            if (database != null)
                try {
                    database.close();
                }
                catch (Exception e) {
                    log.error("Error while closing database", e);
                }

            // Close connection only if it was created by the GenerationTool
            if (connection != null) {
                if (close)
                    connection.close();
                else if (autoCommit != null)
                    connection.setAutoCommit(autoCommit);
            }
        }
    }

    private Properties properties(List<Property> properties) {
        Properties result = new Properties();

        for (Property p : properties)
            result.put(p.getKey(), p.getValue());

        return result;
    }

    private String driverClass(Jdbc j) {
        String result = j.getDriver();

        if (result == null) {
            result = JDBCUtils.driver(j.getUrl());
            log.info("Database", "Inferring driver " + result + " from URL " + j.getUrl());
        }

        return result;
    }

    private Class<? extends Database> databaseClass(Jdbc j) {
        return databaseClass(j.getUrl());
    }

    private Class<? extends Database> databaseClass(Connection c) {
        try {
            return databaseClass(c.getMetaData().getURL());
        }
        catch (SQLException e) {
            throw new GeneratorException("Error when reading URL from JDBC connection", e);
        }
    }

    private Class<? extends Database> databaseClass(String url) {
        if (isBlank(url))
            throw new GeneratorException("No JDBC URL configured.");

        Class<? extends Database> result = Databases.databaseClass(JDBCUtils.dialect(url));
        log.info("Database", "Inferring database " + result.getName() + " from URL " + url);
        return result;
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException {
        try {

            // [#2283] If no explicit class loader was provided try loading the class
            // with "default" techniques
            if (loader == null) {
                return loadClass0(className);
            }

            // Prefer the explicit class loader if available
            else {
                return loader.loadClass(className);
            }
        }

        catch (ClassNotFoundException e) {
            String message = null;

            // [#7556] [#8781]
            if (className.startsWith("org.jooq.util.")) {
                String alternative = null;

                alternativeLoop:
                for (String pkg : new String[] { "org.jooq.meta", "org.jooq.meta.extensions", "org.jooq.codegen", "org.jooq.codegen.maven" }) {
                    try {
                        alternative = loadClass0(className.replace("org.jooq.util", pkg)).getName();
                        break alternativeLoop;
                    }
                    catch (ClassNotFoundException ignore) {}
                }

                log.warn("Type not found", message =
                    "Your configured " + className + " type was not found.\n"
                  + (alternative != null ? ("Did you mean " + alternative + "?\n") : "")
                  + "Do note that in jOOQ 3.11, jOOQ-meta and jOOQ-codegen packages have been renamed. New package names are:\n"
                  + "- org.jooq.meta\n"
                  + "- org.jooq.meta.extensions\n"
                  + "- org.jooq.codegen\n"
                  + "- org.jooq.codegen.maven\n"
                  + "See https://github.com/jOOQ/jOOQ/issues/7419 for details");
            }

            // [#2801] [#4620]
            else if (className.startsWith("org.jooq.meta.") && className.endsWith("Database")) {
                log.warn("Type not found", message =
                      "Your configured database type was not found. This can have several reasons:\n"
                    + "- You want to use a commercial jOOQ Edition, but you pulled the Open Source Edition from Maven Central.\n"
                    + "- You have mis-typed your class name.");
            }

            if (message == null)
                throw e;
            else
                throw new ClassNotFoundException(message, e);
        }
    }

    private Class<?> loadClass0(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
    }

    private static String trim(String string) {
        return (string == null ? null : string.trim());
    }

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since Commons IO 1.3
     */
    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Load a jOOQ codegen configuration file from an input stream
     */
    public static Configuration load(InputStream in) throws IOException {
        // [#1149] If there is no namespace defined, add the default one
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyLarge(in, out);
        String xml = out.toString();

        // TODO [#1201] Add better error handling here
        xml = xml.replaceAll(
            "<(\\w+:)?configuration xmlns(:\\w+)?=\"http://www.jooq.org/xsd/jooq-codegen-\\d+\\.\\d+\\.\\d+.xsd\">",
            "<$1configuration xmlns$2=\"" + Constants.NS_CODEGEN + "\">");

        return MiniJAXB.unmarshal(xml, Configuration.class);
    }
}
