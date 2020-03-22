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

import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.val;

import java.util.Arrays;
import java.util.Collection;

import org.jooq.BetweenAndStep13;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.QuantifiedSelect;
import org.jooq.Record;
import org.jooq.Record13;
import org.jooq.Row;
import org.jooq.Row13;
import org.jooq.Result;
import org.jooq.Select;

/**
 * @author Lukas Eder
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) 
final class RowImpl13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> extends AbstractRow implements Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = -929427349071556318L;
    
    RowImpl13(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        super(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    // ------------------------------------------------------------------------
    // XXX: Row accessor API
    // ------------------------------------------------------------------------

    @Override
    public final Field<T1> field1() {
        return (Field<T1>) fields.field(0);
    }

    @Override
    public final Field<T2> field2() {
        return (Field<T2>) fields.field(1);
    }

    @Override
    public final Field<T3> field3() {
        return (Field<T3>) fields.field(2);
    }

    @Override
    public final Field<T4> field4() {
        return (Field<T4>) fields.field(3);
    }

    @Override
    public final Field<T5> field5() {
        return (Field<T5>) fields.field(4);
    }

    @Override
    public final Field<T6> field6() {
        return (Field<T6>) fields.field(5);
    }

    @Override
    public final Field<T7> field7() {
        return (Field<T7>) fields.field(6);
    }

    @Override
    public final Field<T8> field8() {
        return (Field<T8>) fields.field(7);
    }

    @Override
    public final Field<T9> field9() {
        return (Field<T9>) fields.field(8);
    }

    @Override
    public final Field<T10> field10() {
        return (Field<T10>) fields.field(9);
    }

    @Override
    public final Field<T11> field11() {
        return (Field<T11>) fields.field(10);
    }

    @Override
    public final Field<T12> field12() {
        return (Field<T12>) fields.field(11);
    }

    @Override
    public final Field<T13> field13() {
        return (Field<T13>) fields.field(12);
    }

    // ------------------------------------------------------------------------
    // Generic comparison predicates
    // ------------------------------------------------------------------------

    @Override
    public final Condition compare(Comparator comparator, Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return new RowCondition(this, row, comparator);
    }

    @Override
    public final Condition compare(Comparator comparator, Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return new RowCondition(this, record.valuesRow(), comparator);
    }

    @Override
    public final Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return compare(comparator, row(val(t1, (DataType<T1>) dataType(0)), val(t2, (DataType<T2>) dataType(1)), val(t3, (DataType<T3>) dataType(2)), val(t4, (DataType<T4>) dataType(3)), val(t5, (DataType<T5>) dataType(4)), val(t6, (DataType<T6>) dataType(5)), val(t7, (DataType<T7>) dataType(6)), val(t8, (DataType<T8>) dataType(7)), val(t9, (DataType<T9>) dataType(8)), val(t10, (DataType<T10>) dataType(9)), val(t11, (DataType<T11>) dataType(10)), val(t12, (DataType<T12>) dataType(11)), val(t13, (DataType<T13>) dataType(12))));
    }

    @Override
    public final Condition compare(Comparator comparator, Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return compare(comparator, row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13));
    }

    @Override
    public final Condition compare(Comparator comparator, Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override
    public final Condition compare(Comparator comparator, QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    // ------------------------------------------------------------------------
    // Equal / Not equal comparison predicates
    // ------------------------------------------------------------------------

    @Override
    public final Condition equal(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return compare(Comparator.EQUALS, row);
    }

    @Override
    public final Condition equal(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return compare(Comparator.EQUALS, record);
    }

    @Override
    public final Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return compare(Comparator.EQUALS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition equal(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return compare(Comparator.EQUALS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition eq(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return equal(row);
    }

    @Override
    public final Condition eq(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return equal(record);
    }

    @Override
    public final Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return equal(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition eq(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return equal(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition notEqual(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return compare(Comparator.NOT_EQUALS, row);
    }

    @Override
    public final Condition notEqual(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return compare(Comparator.NOT_EQUALS, record);
    }

    @Override
    public final Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return compare(Comparator.NOT_EQUALS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition notEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return compare(Comparator.NOT_EQUALS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition ne(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return notEqual(row);
    }

    @Override
    public final Condition ne(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return notEqual(record);
    }

    @Override
    public final Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return notEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition ne(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return notEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    // ------------------------------------------------------------------------
    // Ordering comparison predicates
    // ------------------------------------------------------------------------

    @Override
    public final Condition lessThan(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return compare(Comparator.LESS, row);
    }

    @Override
    public final Condition lessThan(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return compare(Comparator.LESS, record);
    }

    @Override
    public final Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return compare(Comparator.LESS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition lessThan(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return compare(Comparator.LESS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition lt(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return lessThan(row);
    }

    @Override
    public final Condition lt(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return lessThan(record);
    }

    @Override
    public final Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return lessThan(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition lt(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return lessThan(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition lessOrEqual(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return compare(Comparator.LESS_OR_EQUAL, row);
    }

    @Override
    public final Condition lessOrEqual(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return compare(Comparator.LESS_OR_EQUAL, record);
    }

    @Override
    public final Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return compare(Comparator.LESS_OR_EQUAL, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition lessOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return compare(Comparator.LESS_OR_EQUAL, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition le(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return lessOrEqual(row);
    }

    @Override
    public final Condition le(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return lessOrEqual(record);
    }

    @Override
    public final Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return lessOrEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition le(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return lessOrEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition greaterThan(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return compare(Comparator.GREATER, row);
    }

    @Override
    public final Condition greaterThan(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return compare(Comparator.GREATER, record);
    }

    @Override
    public final Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return compare(Comparator.GREATER, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition greaterThan(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return compare(Comparator.GREATER, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition gt(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return greaterThan(row);
    }

    @Override
    public final Condition gt(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return greaterThan(record);
    }

    @Override
    public final Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return greaterThan(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition gt(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return greaterThan(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition greaterOrEqual(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return compare(Comparator.GREATER_OR_EQUAL, row);
    }

    @Override
    public final Condition greaterOrEqual(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return compare(Comparator.GREATER_OR_EQUAL, record);
    }

    @Override
    public final Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return compare(Comparator.GREATER_OR_EQUAL, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition greaterOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return compare(Comparator.GREATER_OR_EQUAL, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition ge(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return greaterOrEqual(row);
    }

    @Override
    public final Condition ge(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return greaterOrEqual(record);
    }

    @Override
    public final Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return greaterOrEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override
    public final Condition ge(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return greaterOrEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    // ------------------------------------------------------------------------
    // [NOT] BETWEEN predicates
    // ------------------------------------------------------------------------

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return between(row(val(t1, (DataType) dataType(0)), val(t2, (DataType) dataType(1)), val(t3, (DataType) dataType(2)), val(t4, (DataType) dataType(3)), val(t5, (DataType) dataType(4)), val(t6, (DataType) dataType(5)), val(t7, (DataType) dataType(6)), val(t8, (DataType) dataType(7)), val(t9, (DataType) dataType(8)), val(t10, (DataType) dataType(9)), val(t11, (DataType) dataType(10)), val(t12, (DataType) dataType(11)), val(t13, (DataType) dataType(12))));
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> between(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return between(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13));
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> between(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return new RowBetweenCondition<>(this, row, false, false);
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> between(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return between(record.valuesRow());
    }

    @Override
    public final Condition between(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> minValue, Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override
    public final Condition between(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> minValue, Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return betweenSymmetric(row(val(t1, (DataType) dataType(0)), val(t2, (DataType) dataType(1)), val(t3, (DataType) dataType(2)), val(t4, (DataType) dataType(3)), val(t5, (DataType) dataType(4)), val(t6, (DataType) dataType(5)), val(t7, (DataType) dataType(6)), val(t8, (DataType) dataType(7)), val(t9, (DataType) dataType(8)), val(t10, (DataType) dataType(9)), val(t11, (DataType) dataType(10)), val(t12, (DataType) dataType(11)), val(t13, (DataType) dataType(12))));
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> betweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return betweenSymmetric(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13));
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> betweenSymmetric(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return new RowBetweenCondition<>(this, row, false, true);
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> betweenSymmetric(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return betweenSymmetric(record.valuesRow());
    }

    @Override
    public final Condition betweenSymmetric(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> minValue, Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override
    public final Condition betweenSymmetric(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> minValue, Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return notBetween(row(val(t1, (DataType) dataType(0)), val(t2, (DataType) dataType(1)), val(t3, (DataType) dataType(2)), val(t4, (DataType) dataType(3)), val(t5, (DataType) dataType(4)), val(t6, (DataType) dataType(5)), val(t7, (DataType) dataType(6)), val(t8, (DataType) dataType(7)), val(t9, (DataType) dataType(8)), val(t10, (DataType) dataType(9)), val(t11, (DataType) dataType(10)), val(t12, (DataType) dataType(11)), val(t13, (DataType) dataType(12))));
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> notBetween(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return notBetween(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13));
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> notBetween(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return new RowBetweenCondition<>(this, row, true, false);
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> notBetween(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return notBetween(record.valuesRow());
    }

    @Override
    public final Condition notBetween(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> minValue, Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override
    public final Condition notBetween(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> minValue, Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return notBetweenSymmetric(row(val(t1, (DataType) dataType(0)), val(t2, (DataType) dataType(1)), val(t3, (DataType) dataType(2)), val(t4, (DataType) dataType(3)), val(t5, (DataType) dataType(4)), val(t6, (DataType) dataType(5)), val(t7, (DataType) dataType(6)), val(t8, (DataType) dataType(7)), val(t9, (DataType) dataType(8)), val(t10, (DataType) dataType(9)), val(t11, (DataType) dataType(10)), val(t12, (DataType) dataType(11)), val(t13, (DataType) dataType(12))));
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> notBetweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return notBetweenSymmetric(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13));
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> notBetweenSymmetric(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return new RowBetweenCondition<>(this, row, true, true);
    }

    @Override
    public final BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> notBetweenSymmetric(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return notBetweenSymmetric(record.valuesRow());
    }

    @Override
    public final Condition notBetweenSymmetric(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> minValue, Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override
    public final Condition notBetweenSymmetric(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> minValue, Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    // ------------------------------------------------------------------------
    // [NOT] DISTINCT predicates
    // ------------------------------------------------------------------------

    @Override
    public final Condition isNotDistinctFrom(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return new RowIsDistinctFrom(this, row, true);
    }

    @Override
    public final Condition isNotDistinctFrom(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return isNotDistinctFrom(record.valuesRow());
    }

    @Override
    public final Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return isNotDistinctFrom(val(t1, (DataType<T1>) dataType(0)), val(t2, (DataType<T2>) dataType(1)), val(t3, (DataType<T3>) dataType(2)), val(t4, (DataType<T4>) dataType(3)), val(t5, (DataType<T5>) dataType(4)), val(t6, (DataType<T6>) dataType(5)), val(t7, (DataType<T7>) dataType(6)), val(t8, (DataType<T8>) dataType(7)), val(t9, (DataType<T9>) dataType(8)), val(t10, (DataType<T10>) dataType(9)), val(t11, (DataType<T11>) dataType(10)), val(t12, (DataType<T12>) dataType(11)), val(t13, (DataType<T13>) dataType(12)));
    }

    @Override
    public final Condition isNotDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return isNotDistinctFrom(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13));
    }

    @Override
    public final Condition isDistinctFrom(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row) {
        return new RowIsDistinctFrom(this, row, false);
    }

    @Override
    public final Condition isDistinctFrom(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return isDistinctFrom(record.valuesRow());
    }

    @Override
    public final Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return isDistinctFrom(val(t1, (DataType<T1>) dataType(0)), val(t2, (DataType<T2>) dataType(1)), val(t3, (DataType<T3>) dataType(2)), val(t4, (DataType<T4>) dataType(3)), val(t5, (DataType<T5>) dataType(4)), val(t6, (DataType<T6>) dataType(5)), val(t7, (DataType<T7>) dataType(6)), val(t8, (DataType<T8>) dataType(7)), val(t9, (DataType<T9>) dataType(8)), val(t10, (DataType<T10>) dataType(9)), val(t11, (DataType<T11>) dataType(10)), val(t12, (DataType<T12>) dataType(11)), val(t13, (DataType<T13>) dataType(12)));
    }

    @Override
    public final Condition isDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return isDistinctFrom(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13));
    }

    // ------------------------------------------------------------------------
    // [NOT] IN predicates
    // ------------------------------------------------------------------------

    @Override
    public final Condition in(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>... rows) {
        return in(Arrays.asList(rows));
    }

    @Override
    public final Condition in(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();

        for (Record record : records)
            rows.add(record.valuesRow());

        return new RowInCondition(this, rows, false);
    }

    @Override
    public final Condition notIn(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>... rows) {
        return notIn(Arrays.asList(rows));
    }

    @Override
    public final Condition notIn(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();

        for (Record record : records)
            rows.add(record.valuesRow());

        return new RowInCondition(this, rows, true);
    }

    @Override
    public final Condition in(Collection<? extends Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> rows) {
        return new RowInCondition(this, new QueryPartList<Row>(rows), false);
    }

    @Override
    public final Condition in(Result<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> result) {
        return new RowInCondition(this, new QueryPartList<Row>(Tools.rows(result)), false);
    }

    @Override
    public final Condition notIn(Collection<? extends Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> rows) {
        return new RowInCondition(this, new QueryPartList<Row>(rows), true);
    }

    @Override
    public final Condition notIn(Result<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> result) {
        return new RowInCondition(this, new QueryPartList<Row>(Tools.rows(result)), true);
    }

    // ------------------------------------------------------------------------
    // Predicates involving subqueries
    // ------------------------------------------------------------------------

    @Override
    public final Condition equal(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override
    public final Condition equal(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override
    public final Condition eq(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return equal(select);
    }

    @Override
    public final Condition eq(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return equal(select);
    }

    @Override
    public final Condition notEqual(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override
    public final Condition notEqual(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override
    public final Condition ne(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return notEqual(select);
    }

    @Override
    public final Condition ne(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return notEqual(select);
    }

    @Override
    public final Condition greaterThan(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override
    public final Condition greaterThan(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override
    public final Condition gt(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return greaterThan(select);
    }

    @Override
    public final Condition gt(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return greaterThan(select);
    }

    @Override
    public final Condition greaterOrEqual(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override
    public final Condition greaterOrEqual(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override
    public final Condition ge(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return greaterOrEqual(select);
    }

    @Override
    public final Condition ge(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return greaterOrEqual(select);
    }

    @Override
    public final Condition lessThan(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override
    public final Condition lessThan(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override
    public final Condition lt(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return lessThan(select);
    }

    @Override
    public final Condition lt(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return lessThan(select);
    }

    @Override
    public final Condition lessOrEqual(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override
    public final Condition lessOrEqual(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override
    public final Condition le(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return lessOrEqual(select);
    }

    @Override
    public final Condition le(QuantifiedSelect<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return lessOrEqual(select);
    }

    @Override
    public final Condition in(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.IN, select);
    }

    @Override
    public final Condition notIn(Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        return compare(Comparator.NOT_IN, select);
    }
}
