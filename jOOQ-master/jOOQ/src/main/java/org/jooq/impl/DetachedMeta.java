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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Comment;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Meta;
import org.jooq.MetaProvider;
import org.jooq.Name;
import org.jooq.Package;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UDT;
import org.jooq.UDTRecord;
import org.jooq.UniqueKey;
import org.jooq.exception.DataAccessException;

/**
 * An implementation of {@code Meta} which can be used to create fully
 * self-contained copies of other {@code Meta} objects.
 *
 * @author Knut Wannheden
 */
final class DetachedMeta extends AbstractMeta {

    private static final long serialVersionUID = 5561057000510740144L;
    private Meta              delegate;

    static Meta detach(MetaProvider provider) {
        return new DetachedMeta(provider.provide());
    }

    private DetachedMeta(Meta meta) {
        super(meta.configuration());

        delegate = meta;
        getCatalogs();
        delegate = null;
        resolveReferences();
    }

    private final void resolveReferences() {
        for (Catalog catalog : getCatalogs())
            ((DetachedCatalog) catalog).resolveReferences(this);
    }

    @Override
    protected final List<Catalog> getCatalogs0() throws DataAccessException {
        List<Catalog> result = new ArrayList<>();
        for (Catalog catalog : delegate.getCatalogs())
            result.add(DetachedCatalog.copyOf(catalog));
        return result;
    }

    private static class DetachedCatalog extends CatalogImpl {
        private static final long serialVersionUID = 7979890261252183486L;

        private final List<Schema> schemas = new ArrayList<>();

        private DetachedCatalog(String name, String comment) {
            super(name, comment);
        }

        private final void resolveReferences(Meta meta) {
            for (Schema schema : schemas)
                ((DetachedSchema) schema).resolveReferences(meta);
        }

        static DetachedCatalog copyOf(Catalog catalog) {
            DetachedCatalog result = new DetachedCatalog(catalog.getName(), catalog.getComment());

            for (Schema schema : catalog.getSchemas())
                result.schemas.add(DetachedSchema.copyOf(schema, result));

            return result;
        }

        @Override
        public final List<Schema> getSchemas() {
            return Collections.unmodifiableList(schemas);
        }
    }

    private static class DetachedSchema extends SchemaImpl {
        private static final long serialVersionUID = -95755926444275258L;

        private final List<Table<?>> tables = new ArrayList<>();
        private final List<Sequence<?>> sequences = new ArrayList<>();
        private final List<UDT<?>> udts = new ArrayList<>();

        private DetachedSchema(String name, Catalog owner, String comment) {
            super(name, owner, comment);
        }

        static DetachedSchema copyOf(Schema schema, Catalog owner) {
            DetachedSchema result = new DetachedSchema(schema.getName(), owner, schema.getComment());

            for (Table<?> table : schema.getTables())
                result.tables.add(DetachedTable.copyOf(table, result));
            for (Sequence<?> sequence : schema.getSequences())
                result.sequences.add(DetachedSequence.copyOf(sequence, result));
            for (UDT<?> udt : schema.getUDTs())
                result.udts.add(DetachedUDT.copyOf(udt, result));

            return result;
        }

        final void resolveReferences(Meta meta) {
            for (Table<?> table : tables)
                ((DetachedTable<?>) table).resolveReferences(meta);
        }

        @Override
        public final List<Table<?>> getTables() {
            return Collections.unmodifiableList(tables);
        }

        @Override
        public final List<Sequence<?>> getSequences() {
            return Collections.unmodifiableList(sequences);
        }

        @Override
        public final List<UDT<?>> getUDTs() {
            return Collections.unmodifiableList(udts);
        }
    }

    private static class DetachedTable<R extends Record> extends TableImpl<R> {
        private static final long serialVersionUID = -6070726881709997500L;

        private final List<Index> indexes = new ArrayList<>();
        private final List<UniqueKey<R>> keys = new ArrayList<>();
        private UniqueKey<R> primaryKey;
        private final List<ForeignKey<R, ?>> references = new ArrayList<>();

        private DetachedTable(Name name, Schema owner, Comment comment) {
            super(name, owner, null, null, comment);
        }

        @SuppressWarnings("unchecked")
        @Deprecated
        private final TableField<R, ?>[] fields(TableField<R, ?>[] tableFields) {

            // TODO: [#9456] This auxiliary method should not be necessary
            //               We should be able to call TableLike.fields instead.
            TableField<R, ?>[] result = new TableField[tableFields.length];
            for (int i = 0; i < tableFields.length; i++)
                result[i] = (TableField<R, ?>) field(tableFields[i].getName());
            return result;
        }

        static <R extends Record> DetachedTable<R> copyOf(Table<R> table, Schema owner) {
            DetachedTable<R> result = new DetachedTable<>(table.getUnqualifiedName(), owner, DSL.comment(table.getComment()));

            for (Field<?> field : table.fields())
                DetachedTable.createField(field.getName(), field.getDataType(), result, field.getComment());
            for (Index index : table.getIndexes()) {
                List<SortField<?>> indexFields = index.getFields();
                SortField<?>[] copiedFields = new SortField[indexFields.size()];
                for (int i = 0; i < indexFields.size(); i++) {
                    SortField<?> field = indexFields.get(i);
                    copiedFields[i] = result.field(field.getName()).sort(field.getOrder());
                    // [#9009] TODO NULLS FIRST / NULLS LAST
                }
                result.indexes.add(org.jooq.impl.Internal.createIndex(index.getName(), result, copiedFields, index.getUnique()));
            }
            for (UniqueKey<R> key : table.getKeys())
                result.keys.add(org.jooq.impl.Internal.createUniqueKey(result, key.getName(), result.fields(key.getFieldsArray())));
            UniqueKey<R> pk = table.getPrimaryKey();
            if (pk != null)
                result.primaryKey = org.jooq.impl.Internal.createUniqueKey(result, pk.getName(), result.fields(pk.getFieldsArray()));
            result.references.addAll(table.getReferences());
            return result;
        }

        final void resolveReferences(Meta meta) {

            // TODO: Is there a better way than temporarily keeping the wrong
            //       ReferenceImpl in this list until we "know better"?
            for (int i = 0; i < references.size(); i++) {
                ForeignKey<R, ?> ref = references.get(i);
                Name name = ref.getKey().getTable().getQualifiedName();
                Table<?> table = resolveTable(name, meta);
                UniqueKey<?> pk = table == null ? null : table.getPrimaryKey();
                references.set(i, org.jooq.impl.Internal.createForeignKey(pk, this, ref.getName(), fields(ref.getFieldsArray())));
            }
        }

        private final Table<?> resolveTable(Name tableName, Meta meta) {
            List<Table<?>> list = meta.getTables(tableName);
            return list.isEmpty() ? null : list.get(0);
        }

        @Override
        public final List<Index> getIndexes() {
            return Collections.unmodifiableList(indexes);
        }

        @Override
        public final List<UniqueKey<R>> getKeys() {
            return Collections.unmodifiableList(keys);
        }

        @Override
        public final UniqueKey<R> getPrimaryKey() {
            return primaryKey;
        }

        @Override
        public final List<ForeignKey<R, ?>> getReferences() {
            return Collections.unmodifiableList(references);
        }
    }

    private static class DetachedSequence<T extends Number> extends SequenceImpl<T> {
        private static final long serialVersionUID = -1607062195966296849L;

        private DetachedSequence(String name, Schema owner, DataType<T> dataType) {
            super(name, owner, dataType);
        }

        static DetachedSequence<?> copyOf(Sequence<?> sequence, Schema owner) {
            return new DetachedSequence<>(sequence.getName(), owner, sequence.getDataType());
        }
    }

    private static class DetachedUDT<R extends UDTRecord<R>> extends UDTImpl<R> {
        private static final long serialVersionUID = -5732449514562314202L;

        private DetachedUDT(String name, Schema owner, Package package_, boolean synthetic) {
            super(name, owner, package_, synthetic);
        }

        static DetachedUDT<?> copyOf(UDT<?> udt, Schema owner) {
            Package package_ = null;



            return new DetachedUDT<>(udt.getName(), owner, package_, udt.isSynthetic());
        }
    }
}