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

import org.jooq.impl.DSL;

/**
 * An expression to be used exclusively in <code>GROUP BY</code> clauses.
 * <p>
 * The <code>SELECT .. GROUP BY</code> clause accepts a variety of expressions,
 * mostly ordinary {@link Field} expressions, but also some special expressions
 * usable only in the <code>GROUP BY</code> clause, such as
 * {@link DSL#groupingSets(Field[][])}.
 * <p>
 * <strong>Example:</strong>
 * <p>
 * <code><pre>
 * // Assuming import static org.jooq.impl.DSL.*;
 *
 * using(configuration)
 *    .select(ACTOR.LAST_NAME)
 *    .from(ACTOR)
 *    .groupBy(ACTOR.LAST_NAME) // GroupField reference here
 *    .fetch();
 * </pre></code>
 * <p>
 * Instances can be created using {@link DSL#groupingSets(Field[][])} and
 * related methods, or by creating a subtype.
 *
 * @author Lukas Eder
 */
public interface GroupField extends QueryPart {

}
