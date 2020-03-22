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

package org.jooq.meta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
public class DefaultMetaTableDefinition extends AbstractTableDefinition {

    private final Table<?> table;

    public DefaultMetaTableDefinition(SchemaDefinition schema, Table<?> table) {
        super(schema, table.getName(), "");

        this.table = table;
    }

    @Override
    public List<ColumnDefinition> getElements0() throws SQLException {
        List<ColumnDefinition> result = new ArrayList<>();

        int ordinal = 0;
        for (Field<?> field : table.fields()) {
            DataType<?> dataType = field.getDataType();

            DataTypeDefinition type = new DefaultDataTypeDefinition(
                getDatabase(),
                getSchema(),
                dataType.getTypeName(),
                dataType.lengthDefined() ? dataType.length() : null,
                dataType.precisionDefined() ? dataType.precision() : null,
                dataType.scaleDefined() ? dataType.scale() : null,
                dataType.nullable(),
                create().renderInlined(dataType.defaultValue()),
                (Name) null
            );

            ColumnDefinition column = new DefaultColumnDefinition(
                getDatabase().getTable(getSchema(), getName()),
                field.getName(),
                ordinal,
                type,
                false,
                null
            );

            result.add(column);

            ordinal++;
        }

        return result;
    }
}
