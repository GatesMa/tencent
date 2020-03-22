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

// ...
// ...
// ...
// ...
import static org.jooq.SQLDialect.CUBRID;
// ...
// ...
import static org.jooq.SQLDialect.MARIADB;
// ...
import static org.jooq.SQLDialect.MYSQL;
// ...
// ...
import static org.jooq.SQLDialect.SQLITE;
// ...
// ...
// ...
import static org.jooq.impl.DSL.nvl2;
import static org.jooq.impl.DSL.one;
import static org.jooq.impl.DSL.zero;
import static org.jooq.impl.Keywords.K_NULLS_FIRST;
import static org.jooq.impl.Keywords.K_NULLS_LAST;

import java.util.Set;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.SortField;
import org.jooq.SortOrder;

final class SortFieldImpl<T> extends AbstractQueryPart implements SortField<T> {

    /**
     * Generated UID
     */
    private static final long            serialVersionUID = 1223739398544155873L;

    // DB2 supports NULLS FIRST/LAST only in OLAP (window) functions
    private static final Set<SQLDialect> NO_SUPPORT_NULLS = SQLDialect.supportedUntil(CUBRID, MARIADB, MYSQL);

    private final Field<T>               field;
    private final SortOrder              order;
    private boolean                      nullsFirst;
    private boolean                      nullsLast;

    SortFieldImpl(Field<T> field, SortOrder order) {
        this.field = field;
        this.order = order;
    }

    @Override
    public final String getName() {
        return field.getName();
    }

    @Override
    public final SortOrder getOrder() {
        return order;
    }

    final Field<T> getField() {
        return field;
    }

    final boolean getNullsFirst() {
        return nullsFirst;
    }

    final boolean getNullsLast() {
        return nullsLast;
    }

    @Override
    public final SortField<T> nullsFirst() {
        nullsFirst = true;
        nullsLast = false;
        return this;
    }

    @Override
    public final SortField<T> nullsLast() {
        nullsFirst = false;
        nullsLast = true;
        return this;
    }

    @Override
    public final void accept(Context<?> ctx) {
        if (nullsFirst || nullsLast) {
            if (NO_SUPPORT_NULLS.contains(ctx.dialect())) {
                Field<Integer> ifNull = nullsFirst ? zero() : one();
                Field<Integer> ifNotNull = nullsFirst ? one() : zero();

                ctx.visit(nvl2(field, ifNotNull, ifNull))
                   .sql(", ");

                acceptFieldAndOrder(ctx, false);
            }
            else {
                acceptFieldAndOrder(ctx, true);
            }
        }
        else {
            acceptFieldAndOrder(ctx, false);
        }
    }

    private final void acceptFieldAndOrder(Context<?> ctx, boolean includeNulls) {
        String separator = "";

        for (Field<?> f : Tools.flatten(field)) {
            ctx.sql(separator).visit(f);

            if (order != SortOrder.DEFAULT)
               ctx.sql(' ')
                  .visit(order.toKeyword());

            if (includeNulls)
                if (nullsFirst)
                    ctx.sql(' ').visit(K_NULLS_FIRST);
                else
                    ctx.sql(' ').visit(K_NULLS_LAST);

            separator = ", ";
        }
    }
}
