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

import static org.jooq.impl.DSL.falseCondition;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.one;
import static org.jooq.impl.DSL.using;
import static org.jooq.impl.Names.N_ARRAY_TABLE;
import static org.jooq.impl.Names.N_COLUMN_VALUE;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableOptions;

/**
 * Essentially, this is the same as <code>ArrayTable</code>, except that it simulates
 * unnested arrays using <code>UNION ALL</code>
 *
 * @author Lukas Eder
 */
final class ArrayTableEmulation extends AbstractTable<Record> {

    /**
     * Generated UID
     */
    private static final long       serialVersionUID = 2392515064450536343L;

    private final Object[]          array;
    private final Fields<Record>    field;
    private final Name              alias;
    private final Name              fieldAlias;

    private transient Table<Record> table;

    ArrayTableEmulation(Object[] array) {
        this(array, N_ARRAY_TABLE, null);
    }

    ArrayTableEmulation(Object[] array, Name alias) {
        this(array, alias, null);
    }

    ArrayTableEmulation(Object[] array, Name alias, Name fieldAlias) {
        super(TableOptions.expression(), alias);

        this.array = array;
        this.alias = alias;
        this.fieldAlias = fieldAlias == null ? N_COLUMN_VALUE : fieldAlias;
        this.field = new Fields<>(DSL.field(name(alias.last(), this.fieldAlias.last()), DSL.getDataType(array.getClass().getComponentType())));
    }

    @Override
    public final Class<? extends Record> getRecordType() {
        return RecordImplN.class;
    }

    @Override
    public final Table<Record> as(Name as) {
        return new ArrayTableEmulation(array, as);
    }

    @Override
    public final Table<Record> as(Name as, Name... fieldAliases) {
        if (fieldAliases == null)
            return new ArrayTableEmulation(array, as);
        else if (fieldAliases.length == 1)
            return new ArrayTableEmulation(array, as, fieldAliases[0]);

        throw new IllegalArgumentException("Array table simulations can only have a single field alias");
    }

    @Override
    public final boolean declaresTables() {

        // [#1055] Always true, because unnested tables are always aliased.
        // This is particularly important for simulated unnested arrays
        return true;
    }

    @Override
    public final void accept(Context<?> ctx) {
        ctx.visit(table(ctx.configuration()));
    }

    @Override
    final Fields<Record> fields0() {
        return field;
    }

    private final Table<Record> table(Configuration configuration) {
        if (table == null) {
            Select<Record> select = null;

            for (Object element : array) {

                // [#1081] Be sure to get the correct cast type also for null
                Field<?> val = DSL.val(element, field.fields[0].getDataType());
                Select<Record> subselect = using(configuration).select(val.as("COLUMN_VALUE")).select();

                if (select == null) {
                    select = subselect;
                }
                else {
                    select = select.unionAll(subselect);
                }
            }

            // Empty arrays should result in empty tables
            if (select == null) {
                select = using(configuration).select(one().as("COLUMN_VALUE")).select().where(falseCondition());
            }

            table = DSL.table(select).as(alias);
        }

        return table;
    }
}
