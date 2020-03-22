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

import static org.jooq.impl.Keywords.K_CATALOG;
import static org.jooq.impl.Keywords.K_SET;
import static org.jooq.impl.Keywords.K_USE;

import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.Context;

/**
 * @author Lukas Eder
 */
final class SetCatalog extends AbstractRowCountQuery {

    private static final long serialVersionUID = -3996953205762741746L;
    private final Catalog     catalog;

    SetCatalog(Configuration configuration, Catalog catalog) {
        super(configuration);

        this.catalog = catalog;
    }

    @Override
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {














            case DERBY:
            case H2:
            case HSQLDB:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
                ctx.visit(DSL.setSchema(catalog.getName()));
                break;

            default:
                ctx.visit(K_SET).sql(' ').visit(K_CATALOG).sql(' ').visit(catalog);
                break;
        }
    }
}
