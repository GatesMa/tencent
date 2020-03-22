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
package org.jooq;

/**
 * The step in the {@link Constraint} construction DSL API that allows for
 * matching a <code>FOREIGN KEY</code> clause with a <code>REFERENCES</code>
 * clause.
 *
 * @author Lukas Eder
 */
public interface ConstraintForeignKeyReferencesStep18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> {

    /**
     * Add a <code>REFERENCES</code> clause to the <code>CONSTRAINT</code>,
     * implicitly referencing the primary key.
     */
    @Support
    ConstraintForeignKeyOnStep references(String table);

    /**
     * Add a <code>REFERENCES</code> clause to the <code>CONSTRAINT</code>,
     * referencing a key by column names.
     */
    @Support
    ConstraintForeignKeyOnStep references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18);

    /**
     * Add a <code>REFERENCES</code> clause to the <code>CONSTRAINT</code>,
     * implicitly referencing the primary key.
     */
    @Support
    ConstraintForeignKeyOnStep references(Name table);

    /**
     * Add a <code>REFERENCES</code> clause to the <code>CONSTRAINT</code>,
     * referencing a key by column names.
     */
    @Support
    ConstraintForeignKeyOnStep references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18);

    /**
     * Add a <code>REFERENCES</code> clause to the <code>CONSTRAINT</code>,
     * implicitly referencing the primary key.
     */
    @Support
    ConstraintForeignKeyOnStep references(Table<?> table);

    /**
     * Add a <code>REFERENCES</code> clause to the <code>CONSTRAINT</code>,
     * referencing a key by column names.
     */
    @Support
    ConstraintForeignKeyOnStep references(Table<?> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);
}
