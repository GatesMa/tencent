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
package org.jooq;

/**
 * A qualified asterisk.
 * <p>
 * Asterisks (qualified and unqualified) are expressions that can be used
 * exclusively in <code>SELECT</code> clauses and a few other clauses that
 * explicitly allow for asterisks, including <code>RETURNING</code> on DML
 * statements. Asterisks are syntax sugar in SQL, which are expanded to a column
 * list by the parser once all the columns in the <code>FROM</code> clause are
 * known.
 * <p>
 * <strong>Example:</strong>
 * <p>
 * <code><pre>
 * // Assuming import static org.jooq.impl.DSL.*;
 *
 * using(configuration)
 *    .select(ACTOR.asterisk())
 *    .from(ACTOR)
 *    .fetch();
 * </pre></code>
 * <p>
 * Instances can be created using {@link Table#asterisk()}.
 *
 * @author Lukas Eder
 */
public interface QualifiedAsterisk extends SelectFieldOrAsterisk {

    /**
     * The qualifier.
     */
    Table<?> qualifier();

    /**
     * The qualified asterisk (<code>t.* EXCEPT (fields)</code>) expression to
     * be used in <code>SELECT</code> clauses.
     * <p>
     * This expression is a convenient way to select "all but some fields". Some
     * dialects (e.g. {@link SQLDialect#H2}) implement this feature natively. In
     * other dialects, jOOQ expands the asterisk if possible.
     */
    @Support
    QualifiedAsterisk except(String... fieldNames);

    /**
     * The qualified asterisk (<code>t.* EXCEPT (fields)</code>) expression to
     * be used in <code>SELECT</code> clauses.
     * <p>
     * This expression is a convenient way to select "all but some fields". Some
     * dialects (e.g. {@link SQLDialect#H2}) implement this feature natively. In
     * other dialects, jOOQ expands the asterisk if possible.
     */
    @Support
    QualifiedAsterisk except(Name... fieldNames);

    /**
     * The qualified asterisk (<code>t.* EXCEPT (fields)</code>) expression to
     * be used in <code>SELECT</code> clauses.
     * <p>
     * This expression is a convenient way to select "all but some fields". Some
     * dialects (e.g. {@link SQLDialect#H2}) implement this feature natively. In
     * other dialects, jOOQ expands the asterisk if possible.
     */
    @Support
    QualifiedAsterisk except(Field<?>... fields);

}
