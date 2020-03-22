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
// ...
import static org.jooq.SQLDialect.HSQLDB;
import static org.jooq.SQLDialect.MARIADB;
// ...
import static org.jooq.SQLDialect.MYSQL;
// ...
import static org.jooq.SQLDialect.POSTGRES;
// ...
import static org.jooq.SQLDialect.SQLITE;
// ...

import java.io.Serializable;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

import org.jooq.exception.DataTypeException;
import org.jooq.impl.SQLDataType;
import org.jooq.tools.Convert;
import org.jooq.types.DayToSecond;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;

/**
 * A common interface to all dialect-specific data types.
 * <p>
 * jOOQ provides built in data types through {@link SQLDataType}. Users can
 * define their own data types programmatically by calling
 * {@link #asConvertedDataType(Converter)} or
 * {@link #asConvertedDataType(Binding)}, for example. Custom data types can
 * also be defined on generated code using the <code>&lt;forcedType/&gt;</code>
 * configuration, see <a href=
 * "https://www.jooq.org/doc/latest/manual/code-generation/codegen-advanced/codegen-config-database/codegen-database-forced-types/">the manual for more details</a>
 *
 * @param <T> The Java type associated with this SQL data type
 * @author Lukas Eder
 */
public interface DataType<T> extends Serializable {

    /**
     * Get the standard SQL data type of this (dialect-specific) data type if
     * available.
     */
    DataType<T> getSQLDataType();

    /**
     * The dialect-specific data type representing this data type.
     */
    DataType<T> getDataType(Configuration configuration);

    /**
     * Get JDBC {@link Types} value.
     */
    int getSQLType();

    /**
     * Get the dialect-specific JDBC {@link Types} value.
     */
    int getSQLType(Configuration configuration);

    /**
     * Get the data type binding associated with this data type.
     */
    Binding<?, T> getBinding();

    /**
     * Get the converter associated with this data type.
     */
    Converter<?, T> getConverter();

    /**
     * Retrieve the Java type associated with this data type.
     */
    Class<T> getType();

    /**
     * Retrieve the Java type associated with ARRAYs of this data type.
     */
    Class<T[]> getArrayType();

    /**
     * Retrieve the data type for an ARRAY of this data type.
     */
    DataType<T[]> getArrayDataType();









    /**
     * Retrieve the data type for a given enum data type.
     */
    <E extends EnumType> DataType<E> asEnumDataType(Class<E> enumDataType);

    /**
     * Retrieve the data type for a given converter.
     */
    <U> DataType<U> asConvertedDataType(Converter<? super T, U> converter);

    /**
     * Retrieve the data type for a given binding.
     */
    <U> DataType<U> asConvertedDataType(Binding<? super T, U> binding);

    /**
     * Retrieve the dialect-specific type name associated with this data type.
     */
    String getTypeName();

    /**
     * Retrieve the dialect-specific type name associated with this data type.
     */
    String getTypeName(Configuration configuration);

    /**
     * Retrieve the dialect-specific type name associated with this data type
     * used for casting.
     * <p>
     * This is useful for some dialects that have specialised type names for
     * cast expressions. Other dialects require type-length binding when
     * casting, (e.g. VARCHAR(32767))
     */
    String getCastTypeName();

    /**
     * Retrieve the dialect-specific type name associated with this data type
     * used for casting.
     * <p>
     * This is useful for some dialects that have specialised type names for
     * cast expressions. Other dialects require type-length binding when
     * casting, (e.g. VARCHAR(32767))
     */
    String getCastTypeName(Configuration configuration);

    /**
     * Retrieve the underlying {@link SQLDialect}.
     */
    SQLDialect getDialect();

    /**
     * Convert an arbitrary object into <code>&lt;T&gt;</code>.
     * <p>
     * See {@link Convert#convert(Object, Class)} for details about conversion rules.
     *
     * @param object The object to be converted
     * @return The converted object
     * @throws DataTypeException If conversion fails.
     */
    T convert(Object object);

    /**
     * Convert an arbitrary set of objects into <code>&lt;T&gt;</code>.
     * <p>
     * See {@link Convert#convert(Object, Class)} for details about conversion rules.
     *
     * @param objects The objects to be converted
     * @return The converted objects
     * @throws DataTypeException If conversion fails.
     */
    T[] convert(Object... objects);

    /**
     * Convert an arbitrary set of objects into <code>&lt;T&gt;</code>.
     * <p>
     * See {@link Convert#convert(Object, Class)} for details about conversion rules.
     *
     * @param objects The objects to be converted
     * @return The converted objects
     * @throws DataTypeException If conversion fails.
     */
    List<T> convert(Collection<?> objects);

    /**
     * Return a new data type like this, with a new nullability.
     * <p>
     * [#5709] A <code>nullable</code> column cannot have an {@link #identity()}.
     *
     * @param nullability The new nullability
     * @return The new data type
     */
    @Support
    DataType<T> nullability(Nullability nullability);

    /**
     * Get the nullability of this data type.
     *
     * @return The nullability
     */
    Nullability nullability();

    /**
     * Return a new data type like this, with a new nullability.
     * <p>
     * This is the same as calling {@link #nullability(Nullability)} with any of
     * {@link Nullability#NULL} or {@link Nullability#NOT_NULL} as an argument.
     * <p>
     * [#5709] A <code>nullable</code> column cannot have an
     * {@link #identity()}.
     *
     * @param nullable The new nullability
     * @return The new data type
     */
    @Support
    DataType<T> nullable(boolean nullable);

    /**
     * Get the nullability of this data type.
     * <p>
     * This returns <code>true</code> by default, i.e. if {@link #nullability()}
     * is {@link Nullability#DEFAULT}.
     *
     * @return The nullability
     */
    boolean nullable();

    /**
     * Return a new data type like this, with a new collation.
     */
    @Support({ HSQLDB, MARIADB, MYSQL, POSTGRES, SQLITE })
    DataType<T> collation(Collation collation);

    /**
     * Get the collation of this data type, or <code>null</code> if there is no
     * collation, or if the default collation applies.
     */
    Collation collation();

    /**
     * Return a new data type like this, with a new character set.
     */
    @Support({ MARIADB, MYSQL })
    DataType<T> characterSet(CharacterSet characterSet);

    /**
     * Get the character set of this data type, or <code>null</code> if there is
     * no character set, or if the default character set applies.
     */
    CharacterSet characterSet();

    /**
     * Return a new data type like this, with a new identity flag.
     * <p>
     * [#5709] The IDENTITY flag imposes a NOT NULL constraint, and removes all
     * DEFAULT values.
     *
     * @param identity The new identity flag
     * @return The new data type
     */
    @Support // TODO: List the correct dialects that support identities
    DataType<T> identity(boolean identity);

    /**
     * Get the identity flag of this data type.
     *
     * @return The identity flag.
     */
    boolean identity();

    /**
     * Specify an expression to be applied as the <code>DEFAULT</code> value for
     * this data type.
     * <p>
     * [#5709] A <code>defaulted</code> column cannot have an {@link #identity()}.
     * <p>
     * This is an alias for {@link #default_(Object)}.
     *
     * @see #defaultValue(Field)
     */
    @Support
    DataType<T> defaultValue(T defaultValue);

    /**
     * Specify an expression to be applied as the <code>DEFAULT</code> value for
     * this data type.
     * <p>
     * A default value of a data type applies to DDL statements, such as
     * <ul>
     * <li><code>CREATE TABLE</code></li>
     * <li><code>ALTER TABLE</code></li>
     * </ul>
     * <p>
     * The distinct types of possible <code>DEFAULT</code> expressions is
     * defined by the underlying database. Please refer to your database manual
     * to learn what expressions are possible.
     * <p>
     * This is an alias for {@link #default_(Field)}.
     */
    DataType<T> defaultValue(Field<T> defaultValue);

    /**
     * The expression to be applied as the <code>DEFAULT</code> value for this
     * data type.
     * <p>
     * This is an alias for {@link #default_()}.
     *
     * @return The default value if present, or <code>null</code> if no default
     *         value is specified for this data type.
     * @see #defaultValue(Field)
     */
    Field<T> defaultValue();

    /**
     * Specify an expression to be applied as the <code>DEFAULT</code> value for
     * this data type.
     * <p>
     * [#5709] A <code>defaulted</code> column cannot have an {@link #identity()}.
     *
     * @see #defaultValue(Field)
     */
    @Support
    DataType<T> default_(T defaultValue);

    /**
     * Specify an expression to be applied as the <code>DEFAULT</code> value for
     * this data type.
     * <p>
     * A default value of a data type applies to DDL statements, such as
     * <ul>
     * <li><code>CREATE TABLE</code></li>
     * <li><code>ALTER TABLE</code></li>
     * </ul>
     * <p>
     * The distinct types of possible <code>DEFAULT</code> expressions is
     * defined by the underlying database. Please refer to your database manual
     * to learn what expressions are possible.
     */
    @Support
    DataType<T> default_(Field<T> defaultValue);

    /**
     * The expression to be applied as the <code>DEFAULT</code> value for this
     * data type.
     *
     * @return The default value if present, or <code>null</code> if no default
     *         value is specified for this data type.
     * @see #defaultValue(Field)
     */
    Field<T> default_();

    /**
     * Return a new data type like this, with a new defaultability.
     *
     * @param defaulted The new defaultability
     * @return The new data type
     *
     * @deprecated - [#3852] - 3.8.0 - Use {@link #defaultValue(Field)} instead.
     */
    @Deprecated
    DataType<T> defaulted(boolean defaulted);

    /**
     * Get the defaultability of this data type.
     *
     * @return The defaultability
     */
    boolean defaulted();

    /**
     * Return a new data type like this, with a new precision value.
     * <p>
     * This will have no effect if {@link #hasPrecision()} is <code>false</code>
     * <p>
     * This is the same as calling {@link #precision(int, int)} with
     * <code>scale == 0</code>
     *
     * @param precision The new precision value
     * @return The new data type
     */
    @Support
    DataType<T> precision(int precision);

    /**
     * Return a new data type like this, with a new precision and scale value.
     * <p>
     * This will have no effect if {@link #hasPrecision()} is <code>false</code>
     * , or if <code>scale &gt; 0</code> and {@link #hasScale()} is
     * <code>false</code>
     *
     * @param precision The new precision value
     * @param scale The new scale value
     * @return The new data type
     */
    @Support
    DataType<T> precision(int precision, int scale);

    /**
     * Get the precision of this data type.
     *
     * @return The precision of this data type
     */
    int precision();

    /**
     * Whether this data type has a precision.
     *
     * @return Whether this data type has a precision
     */
    boolean hasPrecision();

    /**
     * Whether the precision returned by {@link #precision()} is defined.
     * <p>
     * The default precision is <code>0</code> for all data types. If a data
     * type does not have a precision (see {@link #hasPrecision()}), or if it
     * was initialised without precision (e.g. {@link SQLDataType#TIMESTAMP}),
     * then the precision is not defined.
     */
    boolean precisionDefined();

    /**
     * Return a new data type like this, with a new scale value.
     * <p>
     * This will have no effect if {@link #hasScale()} is <code>false</code>
     *
     * @param scale The new scale value
     * @return The new data type
     */
    @Support
    DataType<T> scale(int scale);

    /**
     * Get the scale of this data type.
     *
     * @return The scale of this data type
     */
    int scale();

    /**
     * Whether this data type has a scale.
     *
     * @return Whether this data type has a scale
     */
    boolean hasScale();

    /**
     * Whether the precision returned by {@link #scale()} is defined.
     * <p>
     * The default scale is <code>0</code> for all data types. If a data type
     * does not have a scale (see {@link #hasScale()}), or if it was initialised
     * without scale (e.g. {@link SQLDataType#TIMESTAMP}), then the scale is not
     * defined.
     */
    boolean scaleDefined();

    /**
     * Return a new data type like this, with a new length value.
     * <p>
     * This will have no effect if {@link #hasLength()} is <code>false</code>
     *
     * @param length The new length value
     * @return The new data type
     */
    @Support
    DataType<T> length(int length);

    /**
     * Get the length of this data type.
     *
     * @return The length of this data type
     */
    int length();

    /**
     * Whether this data type has a length.
     *
     * @return Whether this data type has a length
     */
    boolean hasLength();

    /**
     * Whether the precision returned by {@link #length()} is defined.
     * <p>
     * The default length is <code>0</code> for all data types. If a data type
     * does not have a length (see {@link #hasLength()}), or if it was initialised
     * without length (e.g. {@link SQLDataType#TIMESTAMP}), then the length is not
     * defined.
     */
    boolean lengthDefined();

    /**
     * Whether this data type is any numeric data type.
     * <p>
     * This applies to any of these types:
     * <ul>
     * <li> {@link SQLDataType#TINYINT}</li>
     * <li> {@link SQLDataType#SMALLINT}</li>
     * <li> {@link SQLDataType#INTEGER}</li>
     * <li> {@link SQLDataType#BIGINT}</li>
     * <li> {@link SQLDataType#FLOAT}</li>
     * <li> {@link SQLDataType#DOUBLE}</li>
     * <li> {@link SQLDataType#REAL}</li>
     * <li> {@link SQLDataType#DECIMAL}</li>
     * <li> {@link SQLDataType#DECIMAL_INTEGER}</li>
     * <li> {@link SQLDataType#NUMERIC}</li>
     * </ul>
     *
     * @see #isNumeric()
     */
    boolean isNumeric();

    /**
     * Whether this data type is any integer data type.
     * <p>
     * This applies to any of these types:
     * <ul>
     * <li> {@link SQLDataType#TINYINT}</li>
     * <li> {@link SQLDataType#SMALLINT}</li>
     * <li> {@link SQLDataType#INTEGER}</li>
     * <li> {@link SQLDataType#BIGINT}</li>
     * </ul>
     */
    boolean isInteger();

    /**
     * Whether this data type is any character data type.
     * <p>
     * This applies to any of these types:
     * <ul>
     * <li> {@link SQLDataType#CHAR}</li>
     * <li> {@link SQLDataType#CLOB}</li>
     * <li> {@link SQLDataType#LONGNVARCHAR}</li>
     * <li> {@link SQLDataType#LONGVARCHAR}</li>
     * <li> {@link SQLDataType#NCHAR}</li>
     * <li> {@link SQLDataType#NCLOB}</li>
     * <li> {@link SQLDataType#NVARCHAR}</li>
     * <li> {@link SQLDataType#VARCHAR}</li>
     * </ul>
     */
    boolean isString();

    /**
     * Whether this data type is any date or time type.
     * <p>
     * This applies to any of these types.
     * <ul>
     * <li> {@link SQLDataType#DATE}</li>
     * <li> {@link SQLDataType#TIME}</li>
     * <li> {@link SQLDataType#TIMESTAMP}</li>
     * <li> {@link SQLDataType#LOCALDATE}</li>
     * <li> {@link SQLDataType#LOCALTIME}</li>
     * <li> {@link SQLDataType#LOCALDATETIME}</li>
     * <li> {@link SQLDataType#OFFSETTIME}</li>
     * <li> {@link SQLDataType#OFFSETDATETIME}</li>
     * <li> {@link SQLDataType#INSTANT}</li>
     * </ul>
     *
     * @see #isDate()
     */
    boolean isDateTime();

    /**
     * Whether this data type is any date type.
     * <p>
     * This applies to any of these types.
     * <ul>
     * <li>{@link SQLDataType#DATE}</li>
     * <li>{@link SQLDataType#LOCALDATE}</li>
     * </ul>
     */
    boolean isDate();

    /**
     * Whether this data type is any timestamp type.
     * <p>
     * This applies to any of these types.
     * <ul>
     * <li>{@link SQLDataType#TIMESTAMP}</li>
     * <li>{@link SQLDataType#LOCALDATETIME}</li>
     * </ul>
     */
    boolean isTimestamp();

    /**
     * Whether this data type is any time type.
     * <p>
     * This applies to any of these types.
     * <ul>
     * <li>{@link SQLDataType#TIME}</li>
     * <li>{@link SQLDataType#LOCALTIME}</li>
     * </ul>
     */
    boolean isTime();

    /**
     * Whether this data type is any date or time type.
     * <p>
     * This applies to any of these types.
     * <ul>
     * <li> {@link SQLDataType#DATE}</li>
     * <li> {@link SQLDataType#TIME}</li>
     * <li> {@link SQLDataType#TIMESTAMP}</li>
     * <li> {@link SQLDataType#LOCALDATE}</li>
     * <li> {@link SQLDataType#LOCALTIME}</li>
     * <li> {@link SQLDataType#LOCALDATETIME}</li>
     * <li> {@link SQLDataType#OFFSETTIME}</li>
     * <li> {@link SQLDataType#OFFSETDATETIME}</li>
     * <li> {@link YearToSecond}</li>
     * <li> {@link YearToMonth}</li>
     * <li> {@link DayToSecond}</li>
     * </ul>
     * <p>
     * This is a combination of {@link #isDateTime()} or {@link #isInterval()}
     */
    boolean isTemporal();

    /**
     * Whether this data type is any interval type.
     * <p>
     * This applies to any of these types.
     * <ul>
     * <li> {@link YearToSecond}</li>
     * <li> {@link YearToMonth}</li>
     * <li> {@link DayToSecond}</li>
     * </ul>
     */
    boolean isInterval();

    /**
     * Whether this data type is any binary type.
     * <p>
     * This applies to any of these types.
     * <ul>
     * <li> {@link SQLDataType#BINARY}</li>
     * <li> {@link SQLDataType#BLOB}</li>
     * <li> {@link SQLDataType#LONGVARBINARY}</li>
     * <li> {@link SQLDataType#VARBINARY}</li>
     * </ul>
     */
    boolean isBinary();

    /**
     * Whether this data type is best deserialised as a <code>LOB</code>.
     * <p>
     * This applies to any of these types.
     * <ul>
     * <li> {@link SQLDataType#BLOB}</li>
     * <li> {@link SQLDataType#CLOB}</li>
     * <li> {@link SQLDataType#NCLOB}</li>
     * </ul>
     */
    boolean isLob();

    /**
     * Whether this data type is an array type.
     */
    boolean isArray();

    /**
     * Whether this data type is a UDT type.
     */
    boolean isUDT();

    /**
     * Whether this data type is an enum type.
     */
    boolean isEnum();
}
