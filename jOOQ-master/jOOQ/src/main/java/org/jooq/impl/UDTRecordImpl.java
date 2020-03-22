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

import static org.jooq.impl.DefaultExecuteContext.localConfiguration;
import static org.jooq.impl.DefaultExecuteContext.localData;

import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.util.Map;

import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Row;
import org.jooq.UDT;
import org.jooq.UDTRecord;

/**
 * A record implementation for a record originating from a single UDT
 * <p>
 * This type is for JOOQ INTERNAL USE only. Do not reference directly
 *
 * @author Lukas Eder
 */
@org.jooq.Internal
public class UDTRecordImpl<R extends UDTRecord<R>> extends AbstractRecord implements UDTRecord<R> {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = 5671315498175872799L;
    private final UDT<R>      udt;

    public UDTRecordImpl(UDT<R> udt) {
        super(udt.fields());

        this.udt = udt;
    }

    @Override
    public final UDT<R> getUDT() {
        return udt;
    }

    /*
     * Subclasses may override this method
     */
    @Override
    public Row fieldsRow() {
        return fields;
    }

    /*
     * Subclasses may override this method
     */
    @Override
    public Row valuesRow() {
        return Tools.row0(Tools.fields(intoArray(), fields.fields.fields()));
    }

    @Override
    public final String getSQLTypeName() throws SQLException {

        // [#1693] This needs to return the fully qualified SQL type name, in
        // case the connected user is not the owner of the UDT
        Configuration configuration = localConfiguration();
        return Tools.getMappedUDTName(configuration, this);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public final void readSQL(SQLInput stream, String typeName) throws SQLException {
        Configuration configuration = localConfiguration();
        Map<Object, Object> data = localData();
        Field<?>[] f = getUDT().fields();

        for (int i = 0; i < f.length; i++) {
            Field field = f[i];
            DefaultBindingGetSQLInputContext out = new DefaultBindingGetSQLInputContext(configuration, data, stream);
            field.getBinding().get(out);
            set(i, field, out.value());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public final void writeSQL(SQLOutput stream) throws SQLException {
        Configuration configuration = localConfiguration();
        Map<Object, Object> data = localData();
        Field<?>[] f = getUDT().fields();

        for (int i = 0; i < f.length; i++) {
            Field field = f[i];
            field.getBinding().set(new DefaultBindingSetSQLOutputContext(configuration, data, stream, get(i)));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> R with(Field<T> field, T value) {
        return (R) super.with(field, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T, U> R with(Field<T> field, U value, Converter<? extends T, ? super U> converter) {
        return (R) super.with(field, value, converter);
    }

    @Override
    public String toString() {
        return DSL.using(configuration()).renderInlined(DSL.inline(this));
    }
}
