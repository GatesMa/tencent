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

import org.jooq.Name;

/**
 * The operator used in <code>Expression</code>
 *
 * @author Lukas Eder
 */
enum ExpressionOperator {

    /**
     * Concatenation
     */
    CONCAT("||"),

    /**
     * Addition
     */
    ADD("+"),

    /**
     * Subtraction
     */
    SUBTRACT("-"),

    /**
     * Multiplication
     */
    MULTIPLY("*"),

    /**
     * Division
     */
    DIVIDE("/"),

    /**
     * Modulo
     */
    MODULO("%"),

    /**
     * Bitwise not
     */
    BIT_NOT("~"),

    /**
     * Bitwise and
     */
    BIT_AND("&"),

    /**
     * Bitwise or
     */
    BIT_OR("|"),

    /**
     * Bitwise xor
     */
    BIT_XOR("^"),

    /**
     * Bitwise and
     */
    BIT_NAND("~&"),

    /**
     * Bitwise or
     */
    BIT_NOR("~|"),

    /**
     * Bitwise xor
     */
    BIT_XNOR("~^"),

    /**
     * Bitwise shift left
     */
    SHL("<<"),

    /**
     * Bitwise shift right
     */
    SHR(">>"),

    ;

    private final String sql;
    private final Name name;

    private ExpressionOperator(String sql) {
        this.sql = sql;
        this.name = DSL.name(name().toLowerCase());
    }

    public String toSQL() {
        return sql;
    }

    public Name toName() {
        return name;
    }
}
