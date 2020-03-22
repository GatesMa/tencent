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

import static org.jooq.impl.Keywords.K_LATERAL;

import org.jooq.Context;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableOptions;

/**
 * @author Lukas Eder
 */
final class Lateral<R extends Record> extends AbstractTable<R> {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = -3665347156501299297L;
    private final Table<R>    table;

    Lateral(Table<R> table) {
        super(TableOptions.expression(), table.getQualifiedName(), table.getSchema());

        this.table = table;
    }

    @Override
    public final boolean declaresTables() {
        return true;
    }

    @Override
    public final Class<? extends R> getRecordType() {
        return table.getRecordType();
    }

    @Override
    public final Table<R> as(Name alias) {
        return new Lateral<>(table.as(alias));
    }

    @Override
    public final Table<R> as(Name alias, Name... fieldAliases) {
        return new Lateral<>(table.as(alias, fieldAliases));
    }

    @Override
    public final void accept(Context<?> ctx) {
        ctx.visit(K_LATERAL).sql(' ').visit(table);
    }

    @Override
    final Fields<R> fields0() {
        return new Fields<>(table.fields());
    }
}
