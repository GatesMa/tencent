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

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.jooq.Clause.TABLE;
import static org.jooq.Clause.TABLE_JOIN;
import static org.jooq.Clause.TABLE_JOIN_ANTI_LEFT;
import static org.jooq.Clause.TABLE_JOIN_CROSS;
import static org.jooq.Clause.TABLE_JOIN_CROSS_APPLY;
import static org.jooq.Clause.TABLE_JOIN_INNER;
import static org.jooq.Clause.TABLE_JOIN_NATURAL;
import static org.jooq.Clause.TABLE_JOIN_NATURAL_OUTER_FULL;
import static org.jooq.Clause.TABLE_JOIN_NATURAL_OUTER_LEFT;
import static org.jooq.Clause.TABLE_JOIN_NATURAL_OUTER_RIGHT;
import static org.jooq.Clause.TABLE_JOIN_ON;
import static org.jooq.Clause.TABLE_JOIN_OUTER_APPLY;
import static org.jooq.Clause.TABLE_JOIN_OUTER_FULL;
import static org.jooq.Clause.TABLE_JOIN_OUTER_LEFT;
import static org.jooq.Clause.TABLE_JOIN_OUTER_RIGHT;
import static org.jooq.Clause.TABLE_JOIN_PARTITION_BY;
import static org.jooq.Clause.TABLE_JOIN_SEMI_LEFT;
import static org.jooq.Clause.TABLE_JOIN_STRAIGHT;
import static org.jooq.Clause.TABLE_JOIN_USING;
import static org.jooq.JoinType.CROSS_APPLY;
import static org.jooq.JoinType.CROSS_JOIN;
import static org.jooq.JoinType.FULL_OUTER_JOIN;
import static org.jooq.JoinType.JOIN;
import static org.jooq.JoinType.LEFT_ANTI_JOIN;
import static org.jooq.JoinType.LEFT_OUTER_JOIN;
import static org.jooq.JoinType.LEFT_SEMI_JOIN;
import static org.jooq.JoinType.NATURAL_FULL_OUTER_JOIN;
import static org.jooq.JoinType.NATURAL_JOIN;
import static org.jooq.JoinType.NATURAL_LEFT_OUTER_JOIN;
import static org.jooq.JoinType.NATURAL_RIGHT_OUTER_JOIN;
import static org.jooq.JoinType.OUTER_APPLY;
import static org.jooq.JoinType.RIGHT_OUTER_JOIN;
// ...
// ...
// ...
// ...
import static org.jooq.SQLDialect.CUBRID;
// ...
import static org.jooq.SQLDialect.H2;
// ...
// ...
// ...
import static org.jooq.SQLDialect.POSTGRES;
// ...
// ...
// ...
// ...
import static org.jooq.impl.DSL.condition;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.selectOne;
import static org.jooq.impl.Keywords.K_CROSS_JOIN_LATERAL;
import static org.jooq.impl.Keywords.K_LEFT_JOIN_LATERAL;
import static org.jooq.impl.Keywords.K_LEFT_OUTER_JOIN_LATERAL;
import static org.jooq.impl.Keywords.K_ON;
import static org.jooq.impl.Keywords.K_PARTITION_BY;
import static org.jooq.impl.Keywords.K_USING;
import static org.jooq.impl.Names.N_JOIN;
import static org.jooq.impl.Tools.BooleanDataKey.DATA_COLLECT_SEMI_ANTI_JOIN;
import static org.jooq.impl.Tools.DataKey.DATA_COLLECTED_SEMI_ANTI_JOIN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.JoinType;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.Operator;
// ...
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableOnConditionStep;
import org.jooq.TableOptionalOnStep;
import org.jooq.TableOptions;
import org.jooq.TableOuterJoinStep;
import org.jooq.TablePartitionByStep;
import org.jooq.conf.RenderOptionalKeyword;
import org.jooq.exception.DataAccessException;

/**
 * A table consisting of two joined tables and possibly a join condition
 *
 * @author Lukas Eder
 */
final class JoinTable extends AbstractTable<Record>
implements
    TableOuterJoinStep<Record>,
    TableOptionalOnStep<Record>,
    TablePartitionByStep<Record>,
    TableOnConditionStep<Record> {

    /**
     * Generated UID
     */
    private static final long             serialVersionUID           = 8377996833996498178L;
    private static final Clause[]         CLAUSES                    = { TABLE, TABLE_JOIN };




    private static final Set<SQLDialect>  EMULATE_NATURAL_JOIN       = SQLDialect.supportedBy(CUBRID);
    private static final Set<SQLDialect>  EMULATE_NATURAL_OUTER_JOIN = SQLDialect.supportedBy(CUBRID, H2);
    private static final Set<SQLDialect>  EMULATE_JOIN_USING         = SQLDialect.supportedBy(CUBRID, H2);
    private static final Set<SQLDialect>  EMULATE_APPLY              = SQLDialect.supportedBy(POSTGRES);

    final Table<?>                        lhs;
    final Table<?>                        rhs;







    final JoinType                        type;
    final ConditionProviderImpl           condition;
    final QueryPartList<Field<?>>         using;

    JoinTable(TableLike<?> lhs, TableLike<?> rhs, JoinType type) {






        super(TableOptions.expression(), N_JOIN);

        this.lhs = lhs.asTable();
        this.rhs = rhs.asTable();






        this.type = type;

        this.condition = new ConditionProviderImpl();
        this.using = new QueryPartList<>();
    }

    // ------------------------------------------------------------------------
    // Table API
    // ------------------------------------------------------------------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public final List<ForeignKey<Record, ?>> getReferences() {
        List<? extends ForeignKey<?, ?>> lhsReferences = lhs.getReferences();
        List<? extends ForeignKey<?, ?>> rhsReferences = rhs.getReferences();
        List<ForeignKey<?, ?>> result = new ArrayList<>(lhsReferences.size() + rhsReferences.size());
        result.addAll(lhsReferences);
        result.addAll(rhsReferences);
        return (List) result;
    }

    @Override
    public final void accept(Context<?> ctx) {
        JoinType translatedType = translateType(ctx);
        Clause translatedClause = translateClause(translatedType);
        Keyword keyword = translateKeyword(ctx, translatedType);

        toSQLTable(ctx, lhs);













        switch (translatedType) {
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
                if (TRUE.equals(ctx.data(DATA_COLLECT_SEMI_ANTI_JOIN))) {

                    @SuppressWarnings("unchecked")
                    List<Condition> semiAntiJoinPredicates = (List<Condition>) ctx.data(DATA_COLLECTED_SEMI_ANTI_JOIN);

                    if (semiAntiJoinPredicates == null) {
                        semiAntiJoinPredicates = new ArrayList<>();
                        ctx.data(DATA_COLLECTED_SEMI_ANTI_JOIN, semiAntiJoinPredicates);
                    }

                    Condition c = !using.isEmpty() ? usingCondition() : condition;
                    switch (translatedType) {
                        case LEFT_SEMI_JOIN:
                            semiAntiJoinPredicates.add(exists(selectOne().from(rhs).where(c)));
                            break;

                        case LEFT_ANTI_JOIN:
                            semiAntiJoinPredicates.add(notExists(selectOne().from(rhs).where(c)));
                            break;
                    }

                    return;
                }
        }

        ctx.formatIndentStart()
           .formatSeparator()
           .start(translatedClause)
           .visit(keyword)
           .sql(' ');

        toSQLTable(ctx, rhs);














        // CROSS JOIN and NATURAL JOIN do not have any condition clauses
        if (!asList(CROSS_JOIN,
                    NATURAL_JOIN,
                    NATURAL_LEFT_OUTER_JOIN,
                    NATURAL_RIGHT_OUTER_JOIN,
                    NATURAL_FULL_OUTER_JOIN,
                    CROSS_APPLY,
                    OUTER_APPLY).contains(translatedType)) {
            ctx.formatIndentStart();
            toSQLJoinCondition(ctx);
            ctx.formatIndentEnd();
        }
        else if (OUTER_APPLY == translatedType && EMULATE_APPLY.contains(ctx.family())) {
            ctx.formatIndentStart()
               .formatSeparator()
               .start(TABLE_JOIN_ON)
               .visit(K_ON)
               .sql(" 1 = 1")
               .end(TABLE_JOIN_ON)
               .formatIndentEnd();
        }

        ctx.end(translatedClause)
           .formatIndentEnd();
    }

    private final Keyword translateKeyword(Context<?> ctx, JoinType translatedType) {
        Keyword keyword;

        switch (translatedType) {
            case JOIN:
            case NATURAL_JOIN:
                if (ctx.settings().getRenderOptionalInnerKeyword() == RenderOptionalKeyword.ON)
                    keyword = translatedType.toKeyword(true);







                else
                    keyword = translatedType.toKeyword();

                break;

            case LEFT_OUTER_JOIN:
            case NATURAL_LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
            case NATURAL_RIGHT_OUTER_JOIN:
            case FULL_OUTER_JOIN:
            case NATURAL_FULL_OUTER_JOIN:
                if (ctx.settings().getRenderOptionalOuterKeyword() == RenderOptionalKeyword.OFF)
                    keyword = translatedType.toKeyword(false);







                else
                    keyword = translatedType.toKeyword();

                break;

            default:
                keyword = translatedType.toKeyword();
                break;
        }

        if (translatedType == CROSS_APPLY && EMULATE_APPLY.contains(ctx.family()))
            keyword = K_CROSS_JOIN_LATERAL;
        else if (translatedType == OUTER_APPLY && EMULATE_APPLY.contains(ctx.family()))
            if (ctx.settings().getRenderOptionalOuterKeyword() == RenderOptionalKeyword.OFF)
                keyword = K_LEFT_JOIN_LATERAL;
            else
                keyword = K_LEFT_OUTER_JOIN_LATERAL;


        return keyword;
    }

    private void toSQLTable(Context<?> ctx, Table<?> table) {

        // [#671] Some databases formally require nested JOINS on the right hand
        // side of the join expression to be wrapped in parentheses (e.g. MySQL).
        // In other databases, it's a good idea to wrap them all
        boolean wrap = table instanceof JoinTable && (table == rhs



            );

        if (wrap)
            ctx.sql('(')
               .formatIndentStart()
               .formatNewLine();

        ctx.visit(table);

        if (wrap)
            ctx.formatIndentEnd()
               .formatNewLine()
               .sql(')');
    }

    /**
     * Translate the join type into a join clause
     */
    final Clause translateClause(JoinType translatedType) {
        switch (translatedType) {
            case JOIN:                     return TABLE_JOIN_INNER;
            case CROSS_JOIN:               return TABLE_JOIN_CROSS;
            case NATURAL_JOIN:             return TABLE_JOIN_NATURAL;
            case LEFT_OUTER_JOIN:          return TABLE_JOIN_OUTER_LEFT;
            case RIGHT_OUTER_JOIN:         return TABLE_JOIN_OUTER_RIGHT;
            case FULL_OUTER_JOIN:          return TABLE_JOIN_OUTER_FULL;
            case NATURAL_LEFT_OUTER_JOIN:  return TABLE_JOIN_NATURAL_OUTER_LEFT;
            case NATURAL_RIGHT_OUTER_JOIN: return TABLE_JOIN_NATURAL_OUTER_RIGHT;
            case NATURAL_FULL_OUTER_JOIN:  return TABLE_JOIN_NATURAL_OUTER_FULL;
            case CROSS_APPLY:              return TABLE_JOIN_CROSS_APPLY;
            case OUTER_APPLY:              return TABLE_JOIN_OUTER_APPLY;
            case LEFT_SEMI_JOIN:           return TABLE_JOIN_SEMI_LEFT;
            case LEFT_ANTI_JOIN:           return TABLE_JOIN_ANTI_LEFT;
            case STRAIGHT_JOIN:            return TABLE_JOIN_STRAIGHT;
            default: throw new IllegalArgumentException("Bad join type: " + translatedType);
        }
    }

    /**
     * Translate the join type for SQL rendering
     */
    final JoinType translateType(Context<?> context) {
        if (emulateCrossJoin(context))
            return JOIN;
        else if (emulateNaturalJoin(context))
            return JOIN;
        else if (emulateNaturalLeftOuterJoin(context))
            return LEFT_OUTER_JOIN;
        else if (emulateNaturalRightOuterJoin(context))
            return RIGHT_OUTER_JOIN;
        else if (emulateNaturalFullOuterJoin(context))
            return FULL_OUTER_JOIN;
        else
            return type;
    }

    private final boolean emulateCrossJoin(Context<?> context) {
        return false



            ;
    }

    private final boolean emulateNaturalJoin(Context<?> context) {
        return type == NATURAL_JOIN && EMULATE_NATURAL_JOIN.contains(context.family());
    }

    private final boolean emulateNaturalLeftOuterJoin(Context<?> context) {
        return type == NATURAL_LEFT_OUTER_JOIN && EMULATE_NATURAL_OUTER_JOIN.contains(context.family());
    }

    private final boolean emulateNaturalRightOuterJoin(Context<?> context) {
        return type == NATURAL_RIGHT_OUTER_JOIN && EMULATE_NATURAL_OUTER_JOIN.contains(context.family());
    }

    private final boolean emulateNaturalFullOuterJoin(Context<?> context) {
        return type == NATURAL_FULL_OUTER_JOIN && EMULATE_NATURAL_OUTER_JOIN.contains(context.family());
    }

    private final void toSQLJoinCondition(Context<?> context) {
        if (!using.isEmpty()) {

            // [#582] Some dialects don't explicitly support a JOIN .. USING
            // syntax. This can be emulated with JOIN .. ON
            if (EMULATE_JOIN_USING.contains(context.family())) {
                toSQLJoinCondition(context, usingCondition());
            }

            // Native supporters of JOIN .. USING
            else {
                context.formatSeparator()
                       .start(TABLE_JOIN_USING)
                       .visit(K_USING)
                       .sql(" (");
                Tools.fieldNames(context, using);
                context.sql(')')
                       .end(TABLE_JOIN_USING);
            }
        }

        // [#577] If any NATURAL JOIN syntax needs to be emulated, find out
        // common fields in lhs and rhs of the JOIN clause
        else if (emulateNaturalJoin(context) ||
                 emulateNaturalLeftOuterJoin(context) ||
                 emulateNaturalRightOuterJoin(context) ||
                 emulateNaturalFullOuterJoin(context)) {

            toSQLJoinCondition(context, naturalCondition());
        }

        // Regular JOIN condition
        else {
            toSQLJoinCondition(context, condition);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    final Condition naturalCondition() {
        List<Condition> conditions = new ArrayList<>(using.size());

        for (Field<?> field : lhs.fields()) {
            Field<?> other = rhs.field(field);

            if (other != null)
                conditions.add(field.eq((Field) other));
        }

        return DSL.and(conditions);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    final Condition usingCondition() {
        List<Condition> conditions = new ArrayList<>(using.size());

        for (Field<?> field : using)
            conditions.add(Tools.qualify(lhs, field).eq((Field) Tools.qualify(rhs, field)));

        return DSL.and(conditions);
    }

    private final void toSQLJoinCondition(Context<?> context, Condition c) {
        context.formatSeparator()
               .start(TABLE_JOIN_ON)
               .visit(K_ON)
               .sql(' ')
               .visit(c)
               .end(TABLE_JOIN_ON);
    }

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override
    public final Table<Record> as(Name alias) {
        return new TableAlias<>(this, alias, true);
    }

    @Override
    public final Table<Record> as(Name alias, Name... fieldAliases) {
        return new TableAlias<>(this, alias, fieldAliases, true);
    }

    @Override
    public final Class<? extends Record> getRecordType() {
        // TODO: [#4695] Calculate the correct Record[B] type
        return RecordImplN.class;
    }

    @Override
    final Fields<Record> fields0() {
        if (type == LEFT_SEMI_JOIN || type == LEFT_ANTI_JOIN) {
            return new Fields<>(lhs.asTable().fields());
        }
        else {
            Field<?>[] l = lhs.asTable().fields();
            Field<?>[] r = rhs.asTable().fields();
            Field<?>[] all = new Field[l.length + r.length];

            System.arraycopy(l, 0, all, 0, l.length);
            System.arraycopy(r, 0, all, l.length, r.length);

            return new Fields<>(all);
        }
    }

    @Override
    public final boolean declaresTables() {
        return true;
    }

    // ------------------------------------------------------------------------
    // Join API
    // ------------------------------------------------------------------------
















    @Override
    public final JoinTable on(Condition conditions) {
        condition.addConditions(conditions);
        return this;
    }

    @Override
    public final JoinTable on(Condition... conditions) {
        condition.addConditions(conditions);
        return this;
    }

    @Override
    public final JoinTable on(Field<Boolean> c) {
        return on(condition(c));
    }

    @Override
    public final JoinTable on(Boolean c) {
        return on(condition(c));
    }

    @Override
    public final JoinTable on(SQL sql) {
        and(sql);
        return this;
    }

    @Override
    public final JoinTable on(String sql) {
        and(sql);
        return this;
    }

    @Override
    public final JoinTable on(String sql, Object... bindings) {
        and(sql, bindings);
        return this;
    }

    @Override
    public final JoinTable on(String sql, QueryPart... parts) {
        and(sql, parts);
        return this;
    }

    @Override
    public final JoinTable onKey() throws DataAccessException {
        List<?> leftToRight = lhs.getReferencesTo(rhs);
        List<?> rightToLeft = rhs.getReferencesTo(lhs);

        if (leftToRight.size() == 1 && rightToLeft.size() == 0) {
            return onKey((ForeignKey<?, ?>) leftToRight.get(0), lhs, rhs);
        }
        else if (rightToLeft.size() == 1 && leftToRight.size() == 0) {
            return onKey((ForeignKey<?, ?>) rightToLeft.get(0), rhs, lhs);
        }

        if (rightToLeft.isEmpty() && leftToRight.isEmpty())
            throw onKeyException(OnKeyExceptionReason.NOT_FOUND, leftToRight, rightToLeft);
        else
            throw onKeyException(OnKeyExceptionReason.AMBIGUOUS, null, null);
    }

    @Override
    public final JoinTable onKey(TableField<?, ?>... keyFields) throws DataAccessException {
        if (keyFields != null && keyFields.length > 0) {
            if (search(lhs, keyFields[0].getTable()) != null) {
                for (ForeignKey<?, ?> key : lhs.getReferences()) {
                    if (key.getFields().containsAll(asList(keyFields))) {
                        return onKey(key);
                    }
                }
            }
            else if (search(rhs, keyFields[0].getTable()) != null) {
                for (ForeignKey<?, ?> key : rhs.getReferences()) {
                    if (key.getFields().containsAll(asList(keyFields))) {
                        return onKey(key);
                    }
                }
            }
        }

        throw onKeyException(OnKeyExceptionReason.NOT_FOUND, null, null);
    }

    @Override
    public final JoinTable onKey(ForeignKey<?, ?> key) {
        if (search(lhs, key.getTable()) != null)
            return onKey(key, lhs, rhs);
        else if (search(rhs, key.getTable()) != null)
            return onKey(key, rhs, lhs);

        throw onKeyException(OnKeyExceptionReason.NOT_FOUND, null, null);
    }

    private final Table<?> search(Table<?> tree, Table<?> search) {

        // [#6304] Improved alias discovery
        Alias<? extends Table<?>> alias = Tools.alias(tree);
        if (alias != null) {
            return search(alias.wrapped(), search);
        }
        else if (tree instanceof JoinTable) {
            JoinTable t = (JoinTable) tree;

            Table<?> r = search(t.lhs, search);
            if (r == null)
                r = search(t.rhs, search);

            return r;
        }
        else {
            return search.equals(tree) ? tree : null;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private final JoinTable onKey(ForeignKey<?, ?> key, Table<?> fk, Table<?> pk) {
        JoinTable result = this;

        TableField<?, ?>[] references = key.getFieldsArray();
        TableField<?, ?>[] referenced = key.getKey().getFieldsArray();

        for (int i = 0; i < references.length; i++) {
            Field f1 = fk.field(references[i]);
            Field f2 = pk.field(referenced[i]);

            // [#2870] TODO: If lhs or rhs are aliased tables, extract the appropriate fields from them
            result.and(f1.equal(f2));
        }

        return result;
    }

    private enum OnKeyExceptionReason {
        AMBIGUOUS, NOT_FOUND
    }

    private final DataAccessException onKeyException(OnKeyExceptionReason reason, List<?> leftToRight, List<?> rightToLeft) {
        switch (reason) {
            case AMBIGUOUS:
                return new DataAccessException("Key ambiguous between tables [" + lhs + "] and [" + rhs + "]. Found: " + leftToRight + " and " + rightToLeft);
            case NOT_FOUND:
            default:
                return new DataAccessException("No matching Key found between tables [" + lhs + "] and [" + rhs + "]");
        }
    }

    @Override
    public final JoinTable using(Field<?>... fields) {
        return using(asList(fields));
    }

    @Override
    public final JoinTable using(Collection<? extends Field<?>> fields) {
        using.addAll(fields);
        return this;
    }

    @Override
    public final JoinTable and(Condition c) {
        condition.addConditions(c);
        return this;
    }

    @Override
    public final JoinTable and(Field<Boolean> c) {
        return and(condition(c));
    }

    @Override
    public final JoinTable and(Boolean c) {
        return and(condition(c));
    }

    @Override
    public final JoinTable and(SQL sql) {
        return and(condition(sql));
    }

    @Override
    public final JoinTable and(String sql) {
        return and(condition(sql));
    }

    @Override
    public final JoinTable and(String sql, Object... bindings) {
        return and(condition(sql, bindings));
    }

    @Override
    public final JoinTable and(String sql, QueryPart... parts) {
        return and(condition(sql, parts));
    }

    @Override
    public final JoinTable andNot(Condition c) {
        return and(c.not());
    }

    @Override
    public final JoinTable andNot(Field<Boolean> c) {
        return andNot(condition(c));
    }

    @Override
    public final JoinTable andNot(Boolean c) {
        return andNot(condition(c));
    }

    @Override
    public final JoinTable andExists(Select<?> select) {
        return and(exists(select));
    }

    @Override
    public final JoinTable andNotExists(Select<?> select) {
        return and(notExists(select));
    }

    @Override
    public final JoinTable or(Condition c) {
        condition.addConditions(Operator.OR, c);
        return this;
    }

    @Override
    public final JoinTable or(Field<Boolean> c) {
        return or(condition(c));
    }

    @Override
    public final JoinTable or(Boolean c) {
        return or(condition(c));
    }

    @Override
    public final JoinTable or(SQL sql) {
        return or(condition(sql));
    }

    @Override
    public final JoinTable or(String sql) {
        return or(condition(sql));
    }

    @Override
    public final JoinTable or(String sql, Object... bindings) {
        return or(condition(sql, bindings));
    }

    @Override
    public final JoinTable or(String sql, QueryPart... parts) {
        return or(condition(sql, parts));
    }

    @Override
    public final JoinTable orNot(Condition c) {
        return or(c.not());
    }

    @Override
    public final JoinTable orNot(Field<Boolean> c) {
        return orNot(condition(c));
    }

    @Override
    public final JoinTable orNot(Boolean c) {
        return orNot(condition(c));
    }

    @Override
    public final JoinTable orExists(Select<?> select) {
        return or(exists(select));
    }

    @Override
    public final JoinTable orNotExists(Select<?> select) {
        return or(notExists(select));
    }
}
