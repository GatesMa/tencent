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

import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

/**
 * A SQL keyword.
 * <p>
 * A <code>Keyword</code> is a {@link QueryPart} that renders a SQL keyword
 * according to the settings specified in
 * {@link Settings#getRenderKeywordCase()}. It is useful mostly in jOOQ's
 * internals and for users who wish to make extensive use of "plain SQL
 * templating".
 * <p>
 * <strong>Example:</strong>
 * <p>
 * <code><pre>
 * // Assuming import static org.jooq.impl.DSL.*;
 *
 * Field&lt;Integer&gt; field = field(
 *     "{0}({1} {2} {3})",
 *     SQLDataType.INTEGER,
 *     keyword("extract"), keyword("year"), keyword("from"), ACTOR.LAST_UPDATE
 * );
 * </pre></code>
 * <p>
 * Instances can be created using {@link DSL#keyword(String)} and overloads.
 *
 * @author Lukas Eder
 */
public interface Keyword extends QueryPart {

}
