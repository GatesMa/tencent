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

import org.jooq.Context;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
final class HintedTable<R extends Record> extends AbstractTable<R> {

    /**
     * Generated UID
     */
    private static final long         serialVersionUID = -3905775637768497535L;

    private final AbstractTable<R>    delegate;
    private final Keyword             keywords;
    private final QueryPartList<Name> arguments;

    HintedTable(AbstractTable<R> delegate, String keywords, String... arguments) {
        this(delegate, keywords, new QueryPartList<>(Tools.names(arguments)));
    }

    HintedTable(AbstractTable<R> delegate, String keywords, QueryPartList<Name> arguments) {
        this(delegate, DSL.keyword(keywords), arguments);
    }

    HintedTable(AbstractTable<R> delegate, Keyword keywords, String... arguments) {
        this(delegate, keywords, new QueryPartList<>(Tools.names(arguments)));
    }

    HintedTable(AbstractTable<R> delegate, Keyword keywords, QueryPartList<Name> arguments) {
        super(delegate.getOptions(), delegate.getQualifiedName(), delegate.getSchema());

        this.delegate = delegate;
        this.keywords = keywords;
        this.arguments = arguments;
    }

    @Override
    public final boolean declaresTables() {
        return true;
    }

    @Override
    public final void accept(Context<?> ctx) {
        ctx.visit(delegate)
            .sql(' ').visit(keywords)
            .sql(" (").visit(arguments)
            .sql(')');
    }

    @Override
    public final Class<? extends R> getRecordType() {
        return delegate.getRecordType();
    }

    @Override
    public final Table<R> as(Name alias) {
        return new HintedTable<>(new TableAlias<>(delegate, alias), keywords, arguments);
    }

    @Override
    public final Table<R> as(Name alias, Name... fieldAliases) {
        return new HintedTable<>(new TableAlias<>(delegate, alias, fieldAliases), keywords, arguments);
    }

    @Override
    final Fields<R> fields0() {
        return delegate.fields0();
    }
}
