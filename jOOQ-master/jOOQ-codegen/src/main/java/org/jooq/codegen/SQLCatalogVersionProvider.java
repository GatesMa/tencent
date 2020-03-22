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
package org.jooq.codegen;

import static org.jooq.conf.StatementType.STATIC_STATEMENT;
import static org.jooq.impl.DSL.param;

import java.sql.Connection;

import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.meta.CatalogDefinition;
import org.jooq.meta.CatalogVersionProvider;

/**
 * @author Lukas Eder
 */
class SQLCatalogVersionProvider implements CatalogVersionProvider {

    private Connection connection;
    private String     sql;

    SQLCatalogVersionProvider(Connection connection, String sql) {
        this.connection = connection;
        this.sql = sql;
    }

    @Override
    public String version(CatalogDefinition catalog) {
        return "" +
            new DefaultConfiguration()
                .set(connection)
                .set(new Settings().withStatementType(STATIC_STATEMENT))
                .dsl()
                .fetchValue(
                    // [#2906] TODO Plain SQL statements do not yet support named parameters
                    sql.replace(":catalog_name", "?"), param("catalog_name", catalog.getInputName())
                );
    }
}
