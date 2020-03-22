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

import org.jooq.BindContext;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.QueryPartInternal;
import org.jooq.RenderContext;

/**
 * A set of markers for use with the {@link ScopeStack}.
 */
enum ScopeMarkers implements QueryPartInternal {

    BEFORE_FIRST_TOP_LEVEL_CTE,
    AFTER_LAST_TOP_LEVEL_CTE;

    @Override
    public final void accept(Context<?> ctx) {}

    @Override
    public final void toSQL(RenderContext ctx) {}

    @Override
    public final void bind(BindContext ctx) {}

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    @Override
    public final boolean declaresFields() {
        return false;
    }

    @Override
    public final boolean declaresTables() {
        return false;
    }

    @Override
    public final boolean declaresWindows() {
        return false;
    }

    @Override
    public final boolean declaresCTE() {
        return false;
    }

    @Override
    public final boolean generatesCast() {
        return false;
    }
}
