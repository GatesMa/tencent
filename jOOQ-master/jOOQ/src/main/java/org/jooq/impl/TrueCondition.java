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

import static org.jooq.Clause.CONDITION;
import static org.jooq.Clause.CONDITION_COMPARISON;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.True;

/**
 * @author Lukas Eder
 */
final class TrueCondition extends AbstractCondition implements True {

    private static final long     serialVersionUID = 775364624704563687L;
    private static final Clause[] CLAUSES          = { CONDITION, CONDITION_COMPARISON };
    static final TrueCondition    INSTANCE         = new TrueCondition();

    @Override
    public final void accept(Context<?> ctx) {
        ctx.sql("1 = 1");
    }

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    private TrueCondition() {}
}
