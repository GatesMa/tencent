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
import static org.jooq.Clause.CONDITION_BETWEEN;
import static org.jooq.Clause.CONDITION_BETWEEN_SYMMETRIC;
import static org.jooq.Clause.CONDITION_NOT_BETWEEN;
import static org.jooq.Clause.CONDITION_NOT_BETWEEN_SYMMETRIC;
// ...
// ...
// ...
import static org.jooq.SQLDialect.CUBRID;
// ...
import static org.jooq.SQLDialect.DERBY;
import static org.jooq.SQLDialect.FIREBIRD;
import static org.jooq.SQLDialect.H2;
// ...
// ...
import static org.jooq.SQLDialect.MARIADB;
// ...
import static org.jooq.SQLDialect.MYSQL;
// ...
// ...
// ...
import static org.jooq.SQLDialect.SQLITE;
// ...
// ...
// ...
// ...
import static org.jooq.impl.DSL.nullSafe;
import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.val;
import static org.jooq.impl.Keywords.K_AND;
import static org.jooq.impl.Keywords.K_BETWEEN;
import static org.jooq.impl.Keywords.K_NOT;
import static org.jooq.impl.Keywords.K_SYMMETRIC;
import static org.jooq.impl.Tools.embeddedFields;
import static org.jooq.impl.Tools.isEmbeddable;

import java.util.Set;

import org.jooq.BetweenAndStep;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.QueryPartInternal;
import org.jooq.RowN;
import org.jooq.SQLDialect;

/**
 * @author Lukas Eder
 */
final class BetweenCondition<T> extends AbstractCondition implements BetweenAndStep<T> {

    private static final long                serialVersionUID              = -4666251100802237878L;
    private static final Clause[]            CLAUSES_BETWEEN               = { CONDITION, CONDITION_BETWEEN };
    private static final Clause[]            CLAUSES_BETWEEN_SYMMETRIC     = { CONDITION, CONDITION_BETWEEN_SYMMETRIC };
    private static final Clause[]            CLAUSES_NOT_BETWEEN           = { CONDITION, CONDITION_NOT_BETWEEN };
    private static final Clause[]            CLAUSES_NOT_BETWEEN_SYMMETRIC = { CONDITION, CONDITION_NOT_BETWEEN_SYMMETRIC };
    private static final Set<SQLDialect>     NO_SUPPORT_SYMMETRIC          = SQLDialect.supportedBy(CUBRID, DERBY, FIREBIRD, H2, MARIADB, MYSQL, SQLITE);

    private final boolean                    symmetric;
    private final boolean                    not;
    private final Field<T>                   field;
    private final Field<T>                   minValue;
    private Field<T>                         maxValue;

    BetweenCondition(Field<T> field, Field<T> minValue, boolean not, boolean symmetric) {
        this.field = field;
        this.minValue = minValue;
        this.not = not;
        this.symmetric = symmetric;
    }

    @Override
    public final Condition and(T value) {
        return and(val(value, field.getDataType()));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public final Condition and(Field f) {
        if (maxValue == null) {
            this.maxValue = nullSafe(f, field.getDataType());
            return this;
        }
        else
            return super.and(f);
    }

    @Override
    public final void accept(Context<?> ctx) {
        ctx.visit(delegate(ctx.configuration()));
    }

    @Override // Avoid AbstractCondition implementation
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    private final QueryPartInternal delegate(Configuration configuration) {
        if (isEmbeddable(field) && isEmbeddable(minValue) && isEmbeddable(maxValue)) {
            RowN f = row(embeddedFields(field));
            RowN min = row(embeddedFields(minValue));
            RowN max = row(embeddedFields(maxValue));

            return (QueryPartInternal) (not
                 ? symmetric
                     ? f.notBetweenSymmetric(min).and(max)
                     : f.notBetween(min).and(max)
                 : symmetric
                     ? f.betweenSymmetric(min).and(max)
                     : f.between(min).and(max));
        }
        else if (symmetric && NO_SUPPORT_SYMMETRIC.contains(configuration.family()))
            return (QueryPartInternal) (not
                ? field.notBetween(minValue, maxValue).and(field.notBetween(maxValue, minValue))
                : field.between(minValue, maxValue).or(field.between(maxValue, minValue)));
        else
            return new Native();
    }

    private class Native extends AbstractQueryPart {

        /**
         * Generated UID
         */
        private static final long serialVersionUID = 2915703568738921575L;

        @Override
        public final void accept(Context<?> ctx) {
                           ctx.visit(field);
            if (not)       ctx.sql(' ').visit(K_NOT);
                           ctx.sql(' ').visit(K_BETWEEN);
            if (symmetric) ctx.sql(' ').visit(K_SYMMETRIC);
                           ctx.sql(' ').visit(minValue);
                           ctx.sql(' ').visit(K_AND);
                           ctx.sql(' ').visit(maxValue);
        }

        @Override
        public final Clause[] clauses(Context<?> ctx) {
            return not ? symmetric ? CLAUSES_NOT_BETWEEN_SYMMETRIC
                                   : CLAUSES_NOT_BETWEEN
                       : symmetric ? CLAUSES_BETWEEN_SYMMETRIC
                                   : CLAUSES_BETWEEN;
        }
    }
}
