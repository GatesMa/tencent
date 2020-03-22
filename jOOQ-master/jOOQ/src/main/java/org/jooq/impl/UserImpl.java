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

import static org.jooq.Clause.USER;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.User;

/**
 * A common implementation of the User type.
 *
 * @author Timur Shaidullin
 */
final class UserImpl extends AbstractQueryPart implements User {

    /**
     * Generated UID
     */
    private static final long     serialVersionUID = 1169948119720063466L;
    private static final Clause[] CLAUSES          = { USER };
    private final Name            name;

    UserImpl(Name name) {
        this.name = name;
    }

    // ------------------------------------------------------------------------
    // XXX: QueryPart API
    // ------------------------------------------------------------------------

    @Override
    public final void accept(Context<?> ctx) {
        ctx.visit(name);
    }

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    // ------------------------------------------------------------------------
    // XXX: User API
    // ------------------------------------------------------------------------

    @Override
    public final String getName() {
        return name.last();
    }
}
