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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jooq.BindContext;
import org.jooq.Configuration;
import org.jooq.Field;

/**
 * @author Lukas Eder
 */
final class DefaultBindContext extends AbstractBindContext {

    DefaultBindContext(Configuration configuration, PreparedStatement stmt) {
        super(configuration, stmt);
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    protected final BindContext bindValue0(Object value, Field<?> field) throws SQLException {
        int nextIndex = nextIndex();

        try {
            ((Field<Object>) field).getBinding().set(
                new DefaultBindingSetStatementContext<>(configuration(), data(), stmt, nextIndex, value)
            );
        }
        catch (Exception e) {
            throw new SQLException("Error while writing value at JDBC bind index: " + nextIndex ,e);
        }

        return this;
    }
}
