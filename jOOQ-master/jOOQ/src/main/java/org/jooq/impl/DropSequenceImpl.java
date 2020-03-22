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

import static org.jooq.Clause.DROP_SEQUENCE;
import static org.jooq.Clause.DROP_SEQUENCE_SEQUENCE;
// ...
// ...
import static org.jooq.SQLDialect.CUBRID;
// ...
import static org.jooq.SQLDialect.DERBY;
import static org.jooq.SQLDialect.FIREBIRD;
// ...
// ...
// ...
import static org.jooq.impl.Keywords.K_DROP;
import static org.jooq.impl.Keywords.K_IF_EXISTS;
import static org.jooq.impl.Keywords.K_RESTRICT;
import static org.jooq.impl.Keywords.K_SEQUENCE;
import static org.jooq.impl.Keywords.K_SERIAL;

import java.util.Set;

import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DropSequenceFinalStep;
import org.jooq.SQLDialect;
import org.jooq.Sequence;

/**
 * @author Lukas Eder
 */
final class DropSequenceImpl extends AbstractRowCountQuery implements

    // Cascading interface implementations for DROP SEQUENCE behaviour
    DropSequenceFinalStep {

    /**
     * Generated UID
     */
    private static final long            serialVersionUID     = 8904572826501186329L;
    private static final Clause[]        CLAUSES              = { DROP_SEQUENCE };
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedBy(DERBY, FIREBIRD);

    private final Sequence<?>            sequence;
    private final boolean                ifExists;

    DropSequenceImpl(Configuration configuration, Sequence<?> sequence) {
        this(configuration, sequence, false);
    }

    DropSequenceImpl(Configuration configuration, Sequence<?> sequence, boolean ifExists) {
        super(configuration);

        this.sequence = sequence;
        this.ifExists = ifExists;
    }

    final Sequence<?> $sequence() { return sequence; }
    final boolean     $ifExists() { return ifExists; }

    // ------------------------------------------------------------------------
    // XXX: QueryPart API
    // ------------------------------------------------------------------------

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.family());
    }

    @Override
    public final void accept(Context<?> ctx) {
        if (ifExists && !supportsIfExists(ctx)) {
            Tools.beginTryCatch(ctx, DDLStatementType.DROP_SEQUENCE);
            accept0(ctx);
            Tools.endTryCatch(ctx, DDLStatementType.DROP_SEQUENCE);
        }
        else {
            accept0(ctx);
        }
    }

    private void accept0(Context<?> ctx) {
        ctx.start(DROP_SEQUENCE_SEQUENCE)
           .visit(K_DROP)
           .sql(' ')
           .visit(ctx.family() == CUBRID ? K_SERIAL : K_SEQUENCE)
           .sql(' ');

        if (ifExists && supportsIfExists(ctx))
            ctx.visit(K_IF_EXISTS).sql(' ');

        switch (ctx.family()) {












            default: {
                ctx.visit(sequence);
                break;
            }
        }

        if (ctx.family() == DERBY)
            ctx.sql(' ').visit(K_RESTRICT);

        ctx.end(DROP_SEQUENCE_SEQUENCE);
    }

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }
}
