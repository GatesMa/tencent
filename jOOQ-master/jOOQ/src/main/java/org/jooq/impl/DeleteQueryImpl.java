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

import static org.jooq.Clause.DELETE;
import static org.jooq.Clause.DELETE_DELETE;
import static org.jooq.Clause.DELETE_RETURNING;
import static org.jooq.Clause.DELETE_WHERE;
// ...
// ...
// ...
// ...
// ...
import static org.jooq.SQLDialect.CUBRID;
// ...
import static org.jooq.SQLDialect.DERBY;
import static org.jooq.SQLDialect.FIREBIRD;
import static org.jooq.SQLDialect.H2;
// ...
import static org.jooq.SQLDialect.HSQLDB;
// ...
// ...
import static org.jooq.SQLDialect.MARIADB;
// ...
import static org.jooq.SQLDialect.MYSQL;
// ...
import static org.jooq.SQLDialect.POSTGRES;
// ...
// ...
import static org.jooq.SQLDialect.SQLITE;
// ...
// ...
// ...
// ...
import static org.jooq.conf.SettingsTools.getExecuteDeleteWithoutWhere;
import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.Keywords.K_DELETE;
import static org.jooq.impl.Keywords.K_FROM;
import static org.jooq.impl.Keywords.K_LIMIT;
import static org.jooq.impl.Keywords.K_ORDER_BY;
import static org.jooq.impl.Keywords.K_USING;
import static org.jooq.impl.Keywords.K_WHERE;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.OrderField;
import org.jooq.Param;
// ...
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableLike;

/**
 * @author Lukas Eder
 */
final class DeleteQueryImpl<R extends Record> extends AbstractDMLQuery<R> implements DeleteQuery<R> {

    private static final long            serialVersionUID         = -1943687511774150929L;
    private static final Clause[]        CLAUSES                  = { DELETE };
    private static final Set<SQLDialect> SPECIAL_DELETE_AS_SYNTAX = SQLDialect.supportedBy(MARIADB, MYSQL);
    private static final Set<SQLDialect> NO_SUPPORT_LIMIT         = SQLDialect.supportedBy(CUBRID, DERBY, FIREBIRD, H2, HSQLDB, POSTGRES, SQLITE);

    private final TableList              using;
    private final ConditionProviderImpl  condition;
    private final SortFieldList          orderBy;
    private Param<? extends Number>      limit;

    DeleteQueryImpl(Configuration configuration, WithImpl with, Table<R> table) {
        super(configuration, with, table);

        this.using = new TableList();
        this.condition = new ConditionProviderImpl();
        this.orderBy = new SortFieldList();
    }

    final Condition getWhere() {
        return condition.getWhere();
    }

    final boolean hasWhere() {
        return condition.hasWhere();
    }

    @Override
    public final void addUsing(Collection<? extends TableLike<?>> f) {
        for (TableLike<?> provider : f)
            using.add(provider.asTable());
    }

    @Override
    public final void addUsing(TableLike<?> f) {
        using.add(f.asTable());
    }

    @Override
    public final void addUsing(TableLike<?>... f) {
        for (TableLike<?> provider : f)
            using.add(provider.asTable());
    }

    @Override
    public final void addConditions(Collection<? extends Condition> conditions) {
        condition.addConditions(conditions);
    }

    @Override
    public final void addConditions(Condition conditions) {
        condition.addConditions(conditions);
    }

    @Override
    public final void addConditions(Condition... conditions) {
        condition.addConditions(conditions);
    }

    @Override
    public final void addConditions(Operator operator, Condition conditions) {
        condition.addConditions(operator, conditions);
    }

    @Override
    public final void addConditions(Operator operator, Condition... conditions) {
        condition.addConditions(operator, conditions);
    }

    @Override
    public final void addConditions(Operator operator, Collection<? extends Condition> conditions) {
        condition.addConditions(operator, conditions);
    }

    @Override
    public final void addOrderBy(OrderField<?>... fields) {
        addOrderBy(Arrays.asList(fields));
    }

    @Override
    public final void addOrderBy(Collection<? extends OrderField<?>> fields) {
        orderBy.addAll(Tools.sortFields(fields));
    }

    @Override
    public final void addLimit(Number numberOfRows) {
        addLimit(DSL.val(numberOfRows));
    }

    @Override
    public final void addLimit(Param<? extends Number> numberOfRows) {
        limit = numberOfRows;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    final void accept0(Context<?> ctx) {
        boolean declare = ctx.declareTables();

        ctx.start(DELETE_DELETE)
           .visit(K_DELETE).sql(' ');

        // [#2464] MySQL supports a peculiar multi-table DELETE syntax for aliased tables:
        // DELETE t1 FROM my_table AS t1
        if (SPECIAL_DELETE_AS_SYNTAX.contains(ctx.family())) {

            // [#2579] [#6304] TableAlias discovery
            if (Tools.alias(table()) != null)
                ctx.visit(table())
                   .sql(' ');
        }

        ctx.visit(K_FROM).sql(' ')
           .declareTables(true)
           .visit(table(ctx));

        if (!using.isEmpty())
            ctx.formatSeparator()
               .visit(K_USING)
               .sql(' ')
               .visit(using);

        ctx.declareTables(declare)
           .end(DELETE_DELETE);





        // [#2059] MemSQL does not support DELETE ... ORDER BY
        if (limit != null && NO_SUPPORT_LIMIT.contains(ctx.family()) ) {
            Field<?>[] keyFields =
                  table().getKeys().isEmpty()
                ? new Field[] { table().rowid() }
                : (table().getPrimaryKey() != null
                    ? table().getPrimaryKey()
                    : table().getKeys().get(0)).getFieldsArray();

            ctx.start(DELETE_WHERE)
               .formatSeparator()
               .visit(K_WHERE).sql(' ');

            if (keyFields.length == 1)
                ctx.visit(keyFields[0].in(select((Field) keyFields[0]).from(table()).where(getWhere()).orderBy(orderBy).limit(limit)));
            else
                ctx.visit(row(keyFields).in(select(keyFields).from(table()).where(getWhere()).orderBy(orderBy).limit(limit)));

            ctx.end(DELETE_WHERE);
        }
        else {
            ctx.start(DELETE_WHERE);

            if (hasWhere())
                ctx.formatSeparator()
                   .visit(K_WHERE).sql(' ')
                   .visit(getWhere());

            ctx.end(DELETE_WHERE);

            if (!orderBy.isEmpty())
                ctx.formatSeparator()
                   .visit(K_ORDER_BY).sql(' ')
                   .visit(orderBy);

            if (limit != null)
                ctx.formatSeparator()
                   .visit(K_LIMIT).sql(' ')
                   .visit(limit);
        }

        ctx.start(DELETE_RETURNING);
        toSQLReturning(ctx);
        ctx.end(DELETE_RETURNING);
    }

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override
    public final boolean isExecutable() {

        // [#6771] Take action when DELETE query has no WHERE clause
        if (!condition.hasWhere())
            executeWithoutWhere("DELETE without WHERE", getExecuteDeleteWithoutWhere(configuration().settings()));

        return super.isExecutable();
    }










}
