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

import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.DSL;
import org.jooq.tools.Convert;

/**
 * A named parameter and/or bind value.
 * <p>
 * A lot of jOOQ API accepts user input values, such as for example when
 * creating a {@link Condition} using {@link Field#eq(Object)}, where a column
 * expression is being compared with a value.
 * <p>
 * Behind the scenes, jOOQ wraps the value in a bind value expression using
 * {@link DSL#val(Object)}. The generated SQL of such an expression depends on
 * things like {@link Settings#getStatementType()} or {@link ParamType} being
 * passed to configurations or {@link Query#getSQL(ParamType)} calls, etc. By
 * default, a parameter marker <code>?</code> is generated.
 * <p>
 * Users can create parameters explicitly using {@link DSL} API, which is useful
 * in a few cases where the value cannot be passed to jOOQ directly, e.g.
 * <ul>
 * <li>When the value is at the left hand side of an operator</li>
 * <li>When {@link Field} references and {@link Param} values are mixed</li>
 * </ul>
 * <p>
 * <strong>Example:</strong>
 * <p>
 * <code><pre>
 * // Assuming import static org.jooq.impl.DSL.*;
 *
 * // The bind value is the first operand of an expression, in case of which it
 * // needs to be wrapped in a Param explicitly
 * using(configuration)
 *    .select()
 *    .from(RENTALS)
 *    .where(val(LocalDateTime.now()).between(RENTALS.RENTAL_DATE).and(RENTALS.DUE_DATE))
 *    .fetch();
 *
 * // The bind value is mixed with other types of Field expressions in a statement
 * using(configuration)
 *    .insertInto(ACTOR)
 *    .columns(ACTOR.FIRST_NAME, ACTOR.LAST_NAME, ACTOR.LAST_UPDATE)
 *    .values(val("John"), val("Doe"), currentTimestamp())
 *    .execute();
 * </pre></code>
 * <p>
 * Instances can be created using {@link DSL#param(String, Object)},
 * {@link DSL#val(Object)}, {@link DSL#inline(Object)} and respective overloads.
 *
 * @author Lukas Eder
 * @see DSL#param(String, Object)
 */
public interface Param<T> extends Field<T> {

    /**
     * {@inheritDoc}
     * <hr/>
     * The <code>Param</code>'s value for {@link #getName()} coincides with
     * {@link #getParamName()}
     */
    @Override
    String getName();

    /**
     * The parameter name. This name is useful for two things:
     * <ul>
     * <li>Named parameters in frameworks that support them, such as Spring's
     * <code>JdbcTemplate</code></li>
     * <li>Accessing the parameter from the {@link Query} API, with
     * {@link Query#getParam(String)}, {@link Query#getParams()}</li>
     * </ul>
     */
    String getParamName();

    /**
     * Get the parameter's underlying value. This returns <code>null</code> if
     * no value has been set yet.
     */
    T getValue();

    /**
     * Set the parameter's underlying value. This is the same as
     * {@link #setConverted(Object)}, but ensures generic type-safety.
     *
     * @see #setConverted(Object)
     * @deprecated - 3.8.0 - [#4991] In jOOQ 4.0, {@link Param} will be made
     *             immutable. Modifying {@link Param} values is strongly
     *             discouraged.
     */
    @Deprecated
    void setValue(T value);

    /**
     * Sets a converted value, using this {@link Param}'s underlying
     * {@link DataType}, obtained from {@link #getDataType()}
     *
     * @see DataType#convert(Object)
     * @see Convert#convert(Object, Class)
     * @throws DataTypeException If <code>value</code> cannot be converted into
     *             this parameter's data type.
     * @deprecated - 3.8.0 - [#4991] In jOOQ 4.0, {@link Param} will be made
     *             immutable. Modifying {@link Param} values is strongly
     *             discouraged.
     */
    @Deprecated
    void setConverted(Object value) throws DataTypeException;

    /**
     * A flag on the bind value to force it to be inlined in rendered SQL
     *
     * @deprecated - 3.8.0 - [#4991] In jOOQ 4.0, {@link Param} will be made
     *             immutable. Modifying {@link Param} values is strongly
     *             discouraged.
     */
    @Deprecated
    void setInline(boolean inline);

    /**
     * A flag on the bind value to force it to be inlined in rendered SQL
     */
    boolean isInline();

    /**
     * The parameter type.
     */
    ParamType getParamType();

    /**
     * The parameter mode.
     */
    ParamMode getParamMode();
}
