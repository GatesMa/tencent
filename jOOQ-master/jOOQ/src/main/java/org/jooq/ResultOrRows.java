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
package org.jooq;

import java.sql.Statement;

import org.jooq.conf.Settings;
import org.jooq.conf.ThrowExceptions;
import org.jooq.exception.DataAccessException;

/**
 * A type that contains either a {@link Result}, an update count, or an
 * exception.
 *
 * @author Lukas Eder
 */
public interface ResultOrRows {

    /**
     * The result or <code>null</code> if there was no result.
     *
     * @see Statement#getResultSet()
     */
    Result<Record> result();

    /**
     * The update count if applicable, or the number of rows in
     * {@link #result()}.
     *
     * @see Statement#getUpdateCount()
     */
    int rows();

    /**
     * The exception if applicable or <code>null</code> if there was no
     * exception.
     * <p>
     * Exceptions are made available through this API only if
     * {@link Settings#getThrowExceptions()} is set to
     * {@link ThrowExceptions#THROW_NONE}. In all other cases, a batch execution
     * is aborted and exceptions are thrown as ordinary Java exceptions.
     */
    DataAccessException exception();
}
