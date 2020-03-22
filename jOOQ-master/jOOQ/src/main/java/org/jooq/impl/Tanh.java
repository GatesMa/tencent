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

import static org.jooq.impl.DSL.one;
import static org.jooq.impl.DSL.two;
import static org.jooq.impl.Keywords.F_TANH;
import static org.jooq.impl.Names.N_TANH;
import static org.jooq.impl.SQLDataType.NUMERIC;

import java.math.BigDecimal;

import org.jooq.Context;
import org.jooq.Field;

/**
 * @author Lukas Eder
 */
final class Tanh extends AbstractField<BigDecimal> {

    /**
     * Generated UID
     */
    private static final long             serialVersionUID = -7273879239726265322L;

    private final Field<? extends Number> argument;

    Tanh(Field<? extends Number> argument) {
        super(N_TANH, NUMERIC);

        this.argument = argument;
    }

    @Override
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {














            case CUBRID:
            case HSQLDB:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
                ctx.visit(DSL.exp(argument.mul(two())).sub(one()).div(DSL.exp(argument.mul(two())).add(one())));
                break;

            default:
                ctx.visit(F_TANH).sql('(').visit(argument).sql(')');
                break;
        }
    }
}
