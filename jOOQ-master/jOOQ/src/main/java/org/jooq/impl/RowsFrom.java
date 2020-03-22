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

import static org.jooq.impl.Keywords.K_ROWS_FROM;
import static org.jooq.impl.Names.N_ROWSFROM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableOptions;

/**
 * @author Lukas Eder
 */
final class RowsFrom extends AbstractTable<Record> {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = 693765524746506586L;

    private final TableList tables;

    RowsFrom(Table<?>... tables) {
        super(TableOptions.expression(), N_ROWSFROM);

        this.tables = new TableList(Arrays.asList(tables));
    }

    @Override
    public final Class<? extends Record> getRecordType() {
        // TODO: [#4695] Calculate the correct Record[B] type
        return RecordImplN.class;
    }

    @Override
    public final Table<Record> as(Name alias) {
        return new TableAlias<>(this, alias);
    }

    @Override
    public final Table<Record> as(Name alias, Name... fieldAliases) {
        return new TableAlias<>(this, alias, fieldAliases);
    }

    @Override
    final Fields<Record> fields0() {
        List<Field<?>> fields = new ArrayList<>();

        for (Table<?> table : tables)
            for (Field<?> field : table.fields())
                fields.add(DSL.field(DSL.name(field.getName()), field.getDataType()));

        return new Fields<>(fields);
    }

    @Override
    public final void accept(Context<?> ctx) {
        boolean declareTables = ctx.declareTables();

        ctx.visit(K_ROWS_FROM)
           .sql(" (")
           .declareTables(true)
           .visit(tables)
           .declareTables(declareTables)
           .sql(')');
    }
}
