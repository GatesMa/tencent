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
// ...
import static org.jooq.SQLDialect.POSTGRES;
// ...
// ...

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.jooq.exception.DataAccessException;
import org.jooq.util.xml.jaxb.InformationSchema;

/**
 * A wrapping object for {@link DatabaseMetaData} or for other sources of
 * database meta information (e.g. {@link InformationSchema})
 * <p>
 * This object can be obtained through {@link DSLContext#meta()} in order to
 * provide convenient access to your database meta data. This abstraction has
 * two purposes:
 * <p>
 * <ol>
 * <li>To increase API convenience, as no checked {@link SQLException} is
 * thrown, only the unchecked {@link DataAccessException}</li>
 * <li>To increase API convenience, as the returned objects are always jOOQ
 * objects, not JDBC {@link ResultSet} objects with hard-to-remember API
 * constraints</li>
 * </ol>
 *
 * @author Lukas Eder
 */
public interface Meta extends Scope {

    /**
     * Get all catalog objects from the underlying meta data source.
     * <p>
     * For those databases that don't really support JDBC meta data catalogs, a
     * single empty catalog (named <code>""</code>) will be returned. In other
     * words, there is always at least one catalog in a database.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support
    List<Catalog> getCatalogs() throws DataAccessException;

    /**
     * Get a catalog object by name from the underlying meta data source, or
     * <code>null</code> if no such object exists.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support
    Catalog getCatalog(String name) throws DataAccessException;

    /**
     * Get a catalog object by name from the underlying meta data source, or
     * <code>null</code> if no such object exists.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support
    Catalog getCatalog(Name name) throws DataAccessException;

    /**
     * Get all schema objects from the underlying meta data source.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support
    List<Schema> getSchemas() throws DataAccessException;

    /**
     * Get all schema objects by name from the underlying meta data source.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support
    List<Schema> getSchemas(String name) throws DataAccessException;

    /**
     * Get all schema objects by name from the underlying meta data source.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support
    List<Schema> getSchemas(Name name) throws DataAccessException;

    /**
     * Get all table objects from the underlying meta data source.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support
    List<Table<?>> getTables() throws DataAccessException;

    /**
     * Get all table objects by name from the underlying meta data source.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support
    List<Table<?>> getTables(String name) throws DataAccessException;

    /**
     * Get all table objects by name from the underlying meta data source.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support
    List<Table<?>> getTables(Name name) throws DataAccessException;

    /**
     * Get all sequence objects from the underlying meta data source.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support({ CUBRID, DERBY, FIREBIRD, H2, HSQLDB, POSTGRES })
    List<Sequence<?>> getSequences() throws DataAccessException;

    /**
     * Get all sequence objects by name from the underlying meta data source.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support({ CUBRID, DERBY, FIREBIRD, H2, HSQLDB, POSTGRES })
    List<Sequence<?>> getSequences(String name) throws DataAccessException;

    /**
     * Get all sequence objects by name from the underlying meta data source.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support({ CUBRID, DERBY, FIREBIRD, H2, HSQLDB, POSTGRES })
    List<Sequence<?>> getSequences(Name name) throws DataAccessException;

    /**
     * Get all primary keys from the underlying meta data source.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support
    List<UniqueKey<?>> getPrimaryKeys() throws DataAccessException;

    /**
     * Get all indexes from the underlying meta data sources.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    @Support
    List<Index> getIndexes() throws DataAccessException;

    /**
     * Generate a creation script for the entire meta data.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    Queries ddl() throws DataAccessException;

    /**
     * Generate a creation script for the entire meta data.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    Queries ddl(DDLExportConfiguration configuration) throws DataAccessException;

    /**
     * Apply a migration to this meta to produce a new {@link Meta}.
     *
     * @see Parser#parse(String)
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    Meta apply(String migration) throws DataAccessException;

    /**
     * Apply a migration to this meta to produce a new {@link Meta}.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    Meta apply(Query... migration) throws DataAccessException;

    /**
     * Apply a migration to this meta to produce a new {@link Meta}.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    Meta apply(Collection<? extends Query> migration) throws DataAccessException;

    /**
     * Apply a migration to this meta to produce a new {@link Meta}.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    Meta apply(Queries migration) throws DataAccessException;

    /**
     * Generate a migration script to get from this meta data to another one.
     * <p>
     * See {@link #migrateTo(Meta, MigrationConfiguration)} for more details.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     * @see #migrateTo(Meta, MigrationConfiguration)
     */
    Queries migrateTo(Meta other) throws DataAccessException;

    /**
     * Generate a migration script to get from this meta data to another one.
     * <p>
     * To some extent, some database migrations can be generated automatically
     * by comparing two versions of a schema. This is what
     * <code>migrateTo()</code> does. It supports:
     * <p>
     * <ul>
     * <li>Schema additions / removals</li>
     * <li>Table additions / removals</li>
     * <li>Column additions / removals</li>
     * <li>Column data type changes</li>
     * <li>Constraint additions / removals</li>
     * <li>Index additions / removals</li>
     * <li>Sequence additions / removals</li>
     * <li>Comment additions / removals</li>
     * </ul>
     * <p>
     * More complex, structural changes, such as moving some columns from one
     * table to another, or turning a to-one relationship into a to-many
     * relationship, as well as data migrations, can currently not be detected
     * automatically.
     *
     * @throws DataAccessException If something went wrong fetching the meta
     *             objects
     */
    Queries migrateTo(Meta other, MigrationConfiguration configuration) throws DataAccessException;

    /**
     * Export to the {@link InformationSchema} format.
     * <p>
     * This allows for serialising schema meta information as XML using JAXB.
     * See also {@link Constants#XSD_META} for details.
     */
    InformationSchema informationSchema() throws DataAccessException;
}