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
package org.jooq.exception;


/**
 * An error occurred while handling data types. This typically happens when
 * converting types
 *
 * @author Lukas Eder
 */
public class DataTypeException extends DataAccessException {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = -6460945824599280420L;

    /**
     * Constructor for DataTypeException.
     *
     * @param message the detail message
     */
    public DataTypeException(String message) {
        super(message);
    }

    /**
     * Constructor for DataTypeException.
     *
     * @param message the detail message
     * @param cause the root cause (usually from using a underlying data access
     *            API such as JDBC)
     */
    public DataTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
