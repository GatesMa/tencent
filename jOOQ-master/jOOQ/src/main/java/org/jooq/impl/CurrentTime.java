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

import static org.jooq.impl.Keywords.F_CONVERT;
import static org.jooq.impl.Keywords.F_CURRENT_TIME;
import static org.jooq.impl.Keywords.F_CURRENT_TIMESTAMP;
import static org.jooq.impl.Keywords.K_CURRENT;
import static org.jooq.impl.Keywords.K_HOUR_TO_SECOND;
import static org.jooq.impl.Keywords.K_TIME;
import static org.jooq.impl.Names.N_CURRENT_TIME;

import org.jooq.Context;
import org.jooq.DataType;

/**
 * @author Lukas Eder
 */
final class CurrentTime<T> extends AbstractField<T> {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = -7273879239726265322L;

    CurrentTime(DataType<T> type) {
        super(N_CURRENT_TIME, type);
    }

    @Override
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {





















            case DERBY:
            case FIREBIRD:
            case H2:
            case HSQLDB:
            case POSTGRES:
            case SQLITE:
                ctx.visit(F_CURRENT_TIME);
                break;











            default:
                ctx.visit(F_CURRENT_TIME).sql("()");
                break;
        }
    }
}
