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

import static org.jooq.impl.DSL.name;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.DDLExportConfiguration;
import org.jooq.Index;
import org.jooq.Meta;
import org.jooq.Name;
import org.jooq.Named;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.util.xml.jaxb.InformationSchema;

/**
 * @author Lukas Eder
 */
abstract class AbstractMeta extends AbstractScope implements Meta, Serializable {

    private static final long            serialVersionUID = 910484713008245977L;

    // [#9010] TODO: Allow for opting out of this cache
    private Map<Name, Catalog>           cachedCatalogs;
    private Map<Name, Schema>            cachedQualifiedSchemas;
    private Map<Name, Table<?>>          cachedQualifiedTables;
    private Map<Name, Sequence<?>>       cachedQualifiedSequences;
    private Map<Name, List<Schema>>      cachedUnqualifiedSchemas;
    private Map<Name, List<Table<?>>>    cachedUnqualifiedTables;
    private Map<Name, List<Sequence<?>>> cachedUnqualifiedSequences;
    private List<UniqueKey<?>>           cachedPrimaryKeys;
    private List<Index>                  cachedIndexes;

    protected AbstractMeta(Configuration configuration) {
        super(configuration);
    }

    @Override
    public final Catalog getCatalog(String name) {
        return getCatalog(name(name));
    }

    @Override
    public final Catalog getCatalog(Name name) {
        initCatalogs();
        return cachedCatalogs.get(name);
    }

    @Override
    public final List<Catalog> getCatalogs() {
        initCatalogs();
        return Collections.unmodifiableList(new ArrayList<>(cachedCatalogs.values()));
    }

    private final void initCatalogs() {
        if (cachedCatalogs == null) {
            cachedCatalogs = new LinkedHashMap<>();
            for (Catalog catalog : getCatalogs0())
                cachedCatalogs.put(catalog.getQualifiedName(), catalog);
        }
    }

    protected abstract List<Catalog> getCatalogs0();

    @Override
    public final List<Schema> getSchemas(String name) {
        return getSchemas(name(name));
    }

    @Override
    public final List<Schema> getSchemas(Name name) {
        initSchemas();
        return get(name, new Iterable<Schema>() {
            @Override
            public Iterator<Schema> iterator() {
                return getSchemas0().iterator();
            }
        }, cachedQualifiedSchemas, cachedUnqualifiedSchemas);
    }

    @Override
    public final List<Schema> getSchemas() {
        initSchemas();
        return Collections.unmodifiableList(new ArrayList<>(cachedQualifiedSchemas.values()));
    }

    private final void initSchemas() {
        if (cachedQualifiedSchemas == null) {
            cachedQualifiedSchemas = new LinkedHashMap<>();
            cachedUnqualifiedSchemas = new LinkedHashMap<>();
            get(name(""), new Iterable<Schema>() {
                @Override
                public Iterator<Schema> iterator() {
                    return getSchemas0().iterator();
                }
            }, cachedQualifiedSchemas, cachedUnqualifiedSchemas);
        }
    }

    protected List<Schema> getSchemas0() {
        List<Schema> result = new ArrayList<>();
        for (Catalog catalog : getCatalogs())
            result.addAll(catalog.getSchemas());
        return result;
    }

    @Override
    public final List<Table<?>> getTables(String name) {
        return getTables(name(name));
    }

    @Override
    public final List<Table<?>> getTables(Name name) {
        initTables();
        return get(name, new Iterable<Table<?>>() {
            @Override
            public Iterator<Table<?>> iterator() {
                return getTables().iterator();
            }
        }, cachedQualifiedTables, cachedUnqualifiedTables);
    }

    @Override
    public final List<Table<?>> getTables() {
        initTables();
        return Collections.unmodifiableList(new ArrayList<>(cachedQualifiedTables.values()));
    }

    private final void initTables() {
        if (cachedQualifiedTables == null) {
            cachedQualifiedTables = new LinkedHashMap<>();
            cachedUnqualifiedTables = new LinkedHashMap<>();
            get(name(""), new Iterable<Table<?>>() {
                @Override
                public Iterator<Table<?>> iterator() {
                    return getTables0().iterator();
                }
            }, cachedQualifiedTables, cachedUnqualifiedTables);
        }
    }

    protected List<Table<?>> getTables0() {
        List<Table<?>> result = new ArrayList<>();
        for (Schema schema : getSchemas())
            result.addAll(schema.getTables());
        return result;
    }

    @Override
    public final List<Sequence<?>> getSequences(String name) {
        return getSequences(name(name));
    }

    @Override
    public final List<Sequence<?>> getSequences(Name name) {
        initSequences();
        return get(name, new Iterable<Sequence<?>>() {
            @Override
            public Iterator<Sequence<?>> iterator() {
                return getSequences().iterator();
            }
        }, cachedQualifiedSequences, cachedUnqualifiedSequences);
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        initSequences();
        return Collections.unmodifiableList(new ArrayList<>(cachedQualifiedSequences.values()));
    }

    private final void initSequences() {
        if (cachedQualifiedSequences == null) {
            cachedQualifiedSequences = new LinkedHashMap<>();
            cachedUnqualifiedSequences = new LinkedHashMap<>();
            get(name(""), new Iterable<Sequence<?>>() {
                @Override
                public Iterator<Sequence<?>> iterator() {
                    return getSequences0().iterator();
                }
            }, cachedQualifiedSequences, cachedUnqualifiedSequences);
        }
    }

    protected List<Sequence<?>> getSequences0() {
        List<Sequence<?>> result = new ArrayList<>();
        for (Schema schema : getSchemas())
            result.addAll(schema.getSequences());
        return result;
    }

    @Override
    public final List<UniqueKey<?>> getPrimaryKeys() {
        initPrimaryKeys();
        return Collections.unmodifiableList(cachedPrimaryKeys);
    }

    private final void initPrimaryKeys() {
        if (cachedPrimaryKeys == null)
            cachedPrimaryKeys = new ArrayList<>(getPrimaryKeys0());
    }

    protected List<UniqueKey<?>> getPrimaryKeys0() {
        List<UniqueKey<?>> result = new ArrayList<>();

        for (Table<?> table : getTables())
            if (table.getPrimaryKey() != null)
                result.add(table.getPrimaryKey());

        return result;
    }

    @Override
    public final List<Index> getIndexes() {
        initIndexes();
        return Collections.unmodifiableList(cachedIndexes);
    }

    private final void initIndexes() {
        if (cachedIndexes == null)
            cachedIndexes = new ArrayList<>(getIndexes0());
    }

    protected List<Index> getIndexes0() {
        List<Index> result = new ArrayList<>();

        for (Table<?> table : getTables())
            result.addAll(table.getIndexes());

        return result;
    }

    private final <T extends Named> List<T> get(Name name, Iterable<T> i, Map<Name, T> qualified, Map<Name, List<T>> unqualified) {
        if (qualified.isEmpty()) {
            for (T object : i) {
                Name q = object.getQualifiedName();
                Name u = object.getUnqualifiedName();

                qualified.put(q, object);

                List<T> list = unqualified.get(u);
                if (list == null) {
                    list = new ArrayList<>();
                    unqualified.put(u, list);
                }

                list.add(object);
            }
        }

        T object = qualified.get(name);
        if (object != null)
            return Collections.singletonList(object);

        List<T> list = unqualified.get(name);
        if (list == null)
            return Collections.emptyList();
        else
            return Collections.unmodifiableList(list);
    }

    @Override
    public final Queries ddl() {
        return ddl(new DDLExportConfiguration());
    }

    // [#9396] TODO Fix this. Subclasses should not need to override this to get
    //         correct results
    @Override
    public /* non-final */ Queries ddl(DDLExportConfiguration exportConfiguration) {
        return new DDL(dsl(), exportConfiguration).queries(this);
    }

    @Override
    public final Meta apply(String migration) {
        return apply(dsl().parser().parse(migration));
    }

    @Override
    public final Meta apply(Query... migration) {
        return apply(dsl().queries(migration));
    }

    @Override
    public final Meta apply(Collection<? extends Query> migration) {
        return apply(dsl().queries(migration));
    }

    @Override
    public final Meta apply(Queries migration) {
        return dsl().meta(ddl().concat(migration).queries());
    }

    @Override
    public final Queries migrateTo(Meta other) {
        return migrateTo(other, new org.jooq.MigrationConfiguration());
    }

    @Override
    public final Queries migrateTo(Meta other, org.jooq.MigrationConfiguration c) {
        return new Diff(configuration(), c, this, other).queries();
    }

    // [#9396] TODO Fix this. Subclasses should not need to override this to get
    //         correct results
    @Override
    public /* non-final */ InformationSchema informationSchema() {
        return InformationSchemaExport.exportCatalogs(configuration(), getCatalogs());
    }

    @Override
    public int hashCode() {
        return ddl().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Meta)
            return ddl().equals(((Meta) obj).ddl());

        return false;
    }

    @Override
    public String toString() {
        return ddl().toString();
    }
}
