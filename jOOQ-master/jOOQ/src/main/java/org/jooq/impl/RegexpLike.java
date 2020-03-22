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
package org.jooq.impl;

import static org.jooq.Clause.CONDITION;
import static org.jooq.Clause.CONDITION_COMPARISON;
import static org.jooq.impl.DSL.keyword;
import static org.jooq.impl.Keywords.K_LIKE_REGEX;
import static org.jooq.impl.Keywords.K_REGEXP;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Field;

/**
 * @author Lukas Eder
 */
final class RegexpLike extends AbstractCondition {

    /**
     * Generated UID
     */
    private static final long     serialVersionUID = 3162855665213654276L;
    private static final Clause[] CLAUSES          = { CONDITION, CONDITION_COMPARISON };

    private final Field<?>        search;
    private final Field<String>   pattern;

    RegexpLike(Field<?> search, Field<String> pattern) {
        this.search = search;
        this.pattern = pattern;
    }

    @Override
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {

            // [#620] These databases are compatible with the MySQL syntax





            case CUBRID:
            case H2:
            case MARIADB:
            case MYSQL:
            case SQLITE: {
                ctx.visit(search)
                   .sql(' ')
                   .visit(K_REGEXP)
                   .sql(' ')
                   .visit(pattern);

                break;
            }

            // [#620] HSQLDB has its own syntax
            case HSQLDB: {

                ctx.visit(keyword("regexp_matches")).sql('(').visit(search).sql(", ").visit(pattern).sql(')');
                break;
            }

            // [#620] Postgres has its own syntax





            case POSTGRES: {

                ctx.sql('(').visit(search).sql(" ~ ").visit(pattern).sql(')');
                break;
            }











            // Render the SQL standard for those databases that do not support
            // regular expressions







            case DERBY:
            case FIREBIRD:
            default: {
                ctx.sql('(')
                   .visit(search)
                   .sql(' ')
                   .visit(K_LIKE_REGEX)
                   .sql(' ')
                   .visit(pattern)
                   .sql(')');

                break;
            }
        }
    }

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }
}
