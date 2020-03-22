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

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.one;
import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.select;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.QueryPartInternal;
import org.jooq.Select;
import org.jooq.Table;

/**
 * @author Knut Wannheden
 */
final class UniqueCondition extends AbstractCondition {

    private static final long     serialVersionUID   = -5560973283201522844L;

    private final Select<?>       query;
    private final boolean         unique;

    UniqueCondition(Select<?> query, boolean unique) {
        this.query = query;
        this.unique = unique;
    }

    @Override
    public final void accept(Context<?> ctx) {
        ctx.visit(delegate(ctx));
    }

    @SuppressWarnings("unused")
    private final QueryPartInternal delegate(Context<?> ctx) {
        Table<?> queryTable = query.asTable("t");
        Field<?>[] queryFields = queryTable.fields();
        Select<?> subquery = select(one())
            .from(queryTable)
            .where(row(queryFields).isNotNull())
            .groupBy(queryFields)
            .having(count().gt(one()));

        return (QueryPartInternal) (unique ? notExists(subquery) : exists(subquery));
    }

}
