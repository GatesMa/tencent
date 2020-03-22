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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultForeignKeyDefinition extends AbstractConstraintDefinition implements ForeignKeyDefinition {

    private final List<ColumnDefinition> keyColumns;
    private final UniqueKeyDefinition    uniqueKey;

    public DefaultForeignKeyDefinition(SchemaDefinition schema, String name, TableDefinition table, UniqueKeyDefinition uniqueKey) {
        this(schema, name, table, uniqueKey, true);
    }

    public DefaultForeignKeyDefinition(SchemaDefinition schema, String name, TableDefinition table, UniqueKeyDefinition uniqueKey, boolean enforced) {
        super(schema, table, name, enforced);

        this.keyColumns = new ArrayList<>();
        this.uniqueKey = uniqueKey;
    }

    @Override
    public TableDefinition getKeyTable() {
        return getTable();
    }

    @Override
    public List<ColumnDefinition> getKeyColumns() {
        return keyColumns;
    }

    @Override
    public UniqueKeyDefinition getReferencedKey() {
        return uniqueKey;
    }

    @Override
    public TableDefinition getReferencedTable() {
        return uniqueKey.getTable();
    }

    @Override
    public List<ColumnDefinition> getReferencedColumns() {
        return uniqueKey.getKeyColumns();
    }

    @Override
    public int countSimilarReferences() {
        Set<String> keys = new HashSet<>();

        for (ForeignKeyDefinition key : getDatabase().getRelations().getForeignKeys(getTable()))
            if (key.getReferencedTable().equals(getReferencedTable()))
                keys.add(key.getName());

        return keys.size();
    }
}
