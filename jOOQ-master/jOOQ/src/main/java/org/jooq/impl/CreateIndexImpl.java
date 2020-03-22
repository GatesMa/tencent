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

import static java.util.Arrays.asList;
import static org.jooq.Clause.CREATE_INDEX;
// ...
// ...
// ...
// ...
// ...
import static org.jooq.SQLDialect.DERBY;
import static org.jooq.SQLDialect.FIREBIRD;
// ...
// ...
// ...
import static org.jooq.SQLDialect.POSTGRES;
// ...
// ...
// ...
// ...
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.Keywords.K_CREATE;
import static org.jooq.impl.Keywords.K_IF_NOT_EXISTS;
import static org.jooq.impl.Keywords.K_INCLUDE;
import static org.jooq.impl.Keywords.K_INDEX;
import static org.jooq.impl.Keywords.K_ON;
import static org.jooq.impl.Keywords.K_STORING;
import static org.jooq.impl.Keywords.K_UNIQUE;
import static org.jooq.impl.Keywords.K_WHERE;
import static org.jooq.impl.Tools.EMPTY_FIELD;
import static org.jooq.impl.Tools.EMPTY_NAME;
import static org.jooq.impl.Tools.EMPTY_ORDERFIELD;
import static org.jooq.impl.Tools.EMPTY_SORTFIELD;
import static org.jooq.impl.Tools.EMPTY_STRING;

import java.util.Collection;
import java.util.Set;

import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.CreateIndexIncludeStep;
import org.jooq.CreateIndexStep;
import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.SortField;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
final class CreateIndexImpl extends AbstractRowCountQuery implements

    // Cascading interface implementations for CREATE INDEX behaviour
    CreateIndexStep,
    CreateIndexIncludeStep {

    /**
     * Generated UID
     */
    private static final long                serialVersionUID         = 8904572826501186329L;
    private static final Clause[]            CLAUSES                  = { CREATE_INDEX };
    private static final Set<SQLDialect>     NO_SUPPORT_IF_NOT_EXISTS = SQLDialect.supportedBy(DERBY, FIREBIRD);
    private static final Set<SQLDialect>     SUPPORT_UNNAMED_INDEX    = SQLDialect.supportedBy(POSTGRES);
    private static final Set<SQLDialect>     SUPPORT_INCLUDE          = SQLDialect.supportedBy(POSTGRES);

    private final Index                      index;
    private final boolean                    unique;
    private final boolean                    ifNotExists;
    private Table<?>                         table;
    private SortField<?>[]                   sortFields;
    private Field<?>[]                       include;
    private Condition                        where;

    CreateIndexImpl(Configuration configuration, Index index, boolean unique, boolean ifNotExists) {
        super(configuration);

        this.index = index;
        this.unique = unique;
        this.ifNotExists = ifNotExists;

        if (index != null) {
            this.table = index.getTable();
            this.sortFields = index.getFields().toArray(EMPTY_SORTFIELD);
            this.where = index.getWhere();
        }
    }

    final Index          $index()       { return index; }
    final boolean        $unique()      { return unique; }
    final boolean        $ifNotExists() { return ifNotExists; }
    final Table<?>       $table()       { return table; }
    final SortField<?>[] $sortFields()  { return sortFields; }
    final Field<?>[]     $include()     { return include; }

    // ------------------------------------------------------------------------
    // XXX: DSL API
    // ------------------------------------------------------------------------

    @Override
    public final CreateIndexImpl on(Table<?> t, OrderField<?>... f) {
        this.table = t;
        this.sortFields = Tools.sortFields(f);

        return this;
    }

    @Override
    public final CreateIndexImpl on(Table<?> t, Collection<? extends OrderField<?>> f) {
        return on(t, f.toArray(EMPTY_ORDERFIELD));
    }

    @Override
    public final CreateIndexImpl on(Name tableName, Name... fieldNames) {
        return on(table(tableName), Tools.fieldsByName(fieldNames));
    }

    @Override
    public final CreateIndexImpl on(Name tableName, Collection<? extends Name> fieldNames) {
        return on(tableName, fieldNames.toArray(EMPTY_NAME));
    }

    @Override
    public final CreateIndexImpl on(String tableName, String... fieldNames) {
        return on(table(name(tableName)), Tools.fieldsByName(fieldNames));
    }

    @Override
    public final CreateIndexImpl on(String tableName, Collection<? extends String> fieldNames) {
        return on(tableName, fieldNames.toArray(EMPTY_STRING));
    }

    @Override
    public final CreateIndexImpl include(Field<?>... f) {
        this.include = f;
        return this;
    }

    @Override
    public final CreateIndexImpl include(Name... f) {
        return include(Tools.fieldsByName(f));
    }

    @Override
    public final CreateIndexImpl include(String... f) {
        return include(Tools.fieldsByName(f));
    }

    @Override
    public final CreateIndexImpl include(Collection<? extends Field<?>> f) {
        return include(f.toArray(EMPTY_FIELD));
    }

    @Override
    public final CreateIndexImpl where(Condition condition) {
        where = condition;
        return this;
    }

    @Override
    public final CreateIndexImpl where(Condition... conditions) {
        where = DSL.and(conditions);
        return this;
    }

    @Override
    public final CreateIndexImpl where(Collection<? extends Condition> conditions) {
        where = DSL.and(conditions);
        return this;
    }

    @Override
    public final CreateIndexImpl where(Field<Boolean> field) {
        return where(DSL.condition(field));
    }

    @Override
    public final CreateIndexImpl where(SQL sql) {
        return where(DSL.condition(sql));
    }

    @Override
    public final CreateIndexImpl where(String sql) {
        return where(DSL.condition(sql));
    }

    @Override
    public final CreateIndexImpl where(String sql, Object... bindings) {
        return where(DSL.condition(sql, bindings));
    }

    @Override
    public final CreateIndexImpl where(String sql, QueryPart... parts) {
        return where(DSL.condition(sql, parts));
    }

    // ------------------------------------------------------------------------
    // XXX: QueryPart API
    // ------------------------------------------------------------------------

    private final boolean supportsIfNotExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_NOT_EXISTS.contains(ctx.family());
    }

    @Override
    public final void accept(Context<?> ctx) {
        if (ifNotExists && !supportsIfNotExists(ctx)) {
            Tools.beginTryCatch(ctx, DDLStatementType.CREATE_INDEX);
            accept0(ctx);
            Tools.endTryCatch(ctx, DDLStatementType.CREATE_INDEX);
        }
        else {
            accept0(ctx);
        }
    }

    private final void accept0(Context<?> ctx) {
        ctx.visit(K_CREATE);

        if (unique)
            ctx.sql(' ')
               .visit(K_UNIQUE);

        ctx.sql(' ')
           .visit(K_INDEX)
           .sql(' ');

        if (ifNotExists && supportsIfNotExists(ctx))
            ctx.visit(K_IF_NOT_EXISTS)
               .sql(' ');

        if (index != null)
            ctx.visit(index)
               .sql(' ');
        else if (!SUPPORT_UNNAMED_INDEX.contains(ctx.family()))
            ctx.visit(generatedName())
               .sql(' ');

        boolean supportsInclude = SUPPORT_INCLUDE.contains(ctx.dialect());
        boolean supportsFieldsBeforeTable = false ;

        QueryPartList<QueryPart> list = new QueryPartList<>();
        list.addAll(asList(sortFields));

        if (!supportsInclude && include != null)
            list.addAll(asList(include));










        ctx.visit(K_ON)
           .sql(' ')
           .visit(table);




            ctx.sql('(')
               .qualify(false)
               .visit(list)
               .qualify(true)
               .sql(')');

        if (supportsInclude && include != null) {
            Keyword keyword = K_INCLUDE;






            ctx.formatSeparator()
               .visit(keyword)
               .sql(" (")
               .qualify(false)
               .visit(new QueryPartList<>(include))
               .qualify(true)
               .sql(')');
        }

        if (where != null && ctx.configuration().data("org.jooq.ddl.ignore-storage-clauses") == null)
            ctx.formatSeparator()
               .visit(K_WHERE)
               .sql(' ')
               .qualify(false)
               .visit(where)
               .qualify(true);
    }

    private final Name generatedName() {
        Name t = table.getQualifiedName();

        StringBuilder sb = new StringBuilder(table.getName());
        for (SortField<?> f : sortFields)
            sb.append('_').append(f.getName());
        sb.append("_idx");

        if (t.qualified())
            return t.qualifier().append(sb.toString());
        else
            return name(sb.toString());
    }

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }
}
