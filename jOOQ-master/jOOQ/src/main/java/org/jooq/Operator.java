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

/**
 * An operator used for combining conditions
 *
 * @author Lukas Eder
 */
public enum Operator {

    /**
     * The and operator
     */
    @Support
    AND("and"),

    /**
     * The or operator
     */
    @Support
    OR("or");

    private final String sql;

    private Operator(String sql) {
        this.sql = sql;
    }

    /**
     * A SQL rendition of this operator.
     */
    public String toSQL() {
        return sql;
    }
}
