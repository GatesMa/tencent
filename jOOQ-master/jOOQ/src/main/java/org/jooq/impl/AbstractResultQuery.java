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

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_SCROLL_SENSITIVE;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
// ...
// ...
import static org.jooq.SQLDialect.CUBRID;
import static org.jooq.SQLDialect.POSTGRES;
// ...
// ...
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.Tools.blocking;
import static org.jooq.impl.Tools.consumeResultSets;
import static org.jooq.impl.Tools.executeStatementAndGetFirstResultSet;
import static org.jooq.impl.Tools.BooleanDataKey.DATA_LOCK_ROWS_FOR_UPDATE;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
// ...
import java.util.concurrent.Future;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record11;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record14;
import org.jooq.Record15;
import org.jooq.Record16;
import org.jooq.Record17;
import org.jooq.Record18;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record22;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.RecordHandler;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Results;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.conf.SettingsTools;
import org.jooq.tools.Convert;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.jdbc.MockResultSet;

/**
 * A query that returns a {@link Result}
 *
 * @author Lukas Eder
 */
abstract class AbstractResultQuery<R extends Record> extends AbstractQuery implements ResultQuery<R> {

    /**
     * Generated UID
     */
    private static final long                serialVersionUID                  = -5588344253566055707L;
    private static final JooqLogger          log                               = JooqLogger.getLogger(AbstractResultQuery.class);

    private static final Set<SQLDialect>     NO_SUPPORT_FOR_UPDATE             = SQLDialect.supportedBy(CUBRID);
    private static final Set<SQLDialect>     REPORT_FETCH_SIZE_WITH_AUTOCOMMIT = SQLDialect.supportedBy(POSTGRES);

    private int                              maxRows;
    private int                              fetchSize;
    private int                              resultSetConcurrency;
    private int                              resultSetType;
    private int                              resultSetHoldability;
    private Table<?>                         coerceTable;
    private Collection<? extends Field<?>>   coerceFields;
    private transient boolean                lazy;
    private transient boolean                many;
    private transient Cursor<R>              cursor;
    private transient boolean                autoclosing           = true;
    private Result<R>                        result;
    private ResultsImpl                      results;

    // Some temp variables for String interning
    private final Intern                     intern                = new Intern();

    AbstractResultQuery(Configuration configuration) {
        super(configuration);
    }

    /**
     * Get a list of fields provided a result set.
     */
    protected abstract Field<?>[] getFields(ResultSetMetaData rs) throws SQLException;

    @SuppressWarnings("unchecked")
    @Override
    public final ResultQuery<R> bind(String param, Object value) {
        return (ResultQuery<R>) super.bind(param, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final ResultQuery<R> bind(int index, Object value) {
        return (ResultQuery<R>) super.bind(index, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final ResultQuery<R> poolable(boolean poolable) {
        return (ResultQuery<R>) super.poolable(poolable);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final ResultQuery<R> queryTimeout(int timeout) {
        return (ResultQuery<R>) super.queryTimeout(timeout);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final ResultQuery<R> keepStatement(boolean k) {
        return (ResultQuery<R>) super.keepStatement(k);
    }

    @Override
    public final ResultQuery<R> maxRows(int rows) {
        this.maxRows = rows;
        return this;
    }

    @Override
    public final ResultQuery<R> fetchSize(int rows) {
        this.fetchSize = rows;
        return this;
    }

    @Override
    public final ResultQuery<R> resultSetConcurrency(int concurrency) {
        this.resultSetConcurrency = concurrency;
        return this;
    }

    @Override
    public final ResultQuery<R> resultSetType(int type) {
        this.resultSetType = type;
        return this;
    }

    @Override
    public final ResultQuery<R> resultSetHoldability(int holdability) {
        this.resultSetHoldability = holdability;
        return this;
    }

    @Override
    public final ResultQuery<R> intern(Field<?>... fields) {
        intern.internFields = fields;
        return this;
    }

    @Override
    public final ResultQuery<R> intern(int... fieldIndexes) {
        intern.internIndexes = fieldIndexes;
        return this;
    }

    @Override
    public final ResultQuery<R> intern(String... fieldNameStrings) {
        intern.internNameStrings = fieldNameStrings;
        return this;
    }

    @Override
    public final ResultQuery<R> intern(Name... fieldNames) {
        intern.internNames = fieldNames;
        return this;
    }

    @Override
    protected final void prepare(ExecuteContext ctx) throws SQLException {

        // [#1846] [#2265] [#2299] Users may explicitly specify how ResultSets
        // created by jOOQ behave. This will override any other default behaviour
        if (resultSetConcurrency != 0 || resultSetType != 0 || resultSetHoldability != 0) {
            int type = resultSetType != 0 ? resultSetType : ResultSet.TYPE_FORWARD_ONLY;
            int concurrency = resultSetConcurrency != 0 ? resultSetConcurrency : ResultSet.CONCUR_READ_ONLY;

            // Sybase doesn't support holdability. Avoid setting it!
            if (resultSetHoldability == 0) {
                ctx.statement(ctx.connection().prepareStatement(ctx.sql(), type, concurrency));
            }
            else {
                ctx.statement(ctx.connection().prepareStatement(ctx.sql(), type, concurrency, resultSetHoldability));
            }
        }

        // [#1296] These dialects do not implement FOR UPDATE. But the same
        // effect can be achieved using ResultSet.CONCUR_UPDATABLE
        else if (isForUpdate() && NO_SUPPORT_FOR_UPDATE.contains(ctx.family())) {
            ctx.data(DATA_LOCK_ROWS_FOR_UPDATE, true);
            ctx.statement(ctx.connection().prepareStatement(ctx.sql(), TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE));
        }

        // Regular behaviour
        else {
            ctx.statement(ctx.connection().prepareStatement(ctx.sql()));
        }

        Tools.setFetchSize(ctx, fetchSize);

        // [#1854] [#4753] Set the max number of rows for this result query
        int m = SettingsTools.getMaxRows(maxRows, ctx.settings());
        if (m != 0) {
            ctx.statement().setMaxRows(m);
        }
    }

    @Override
    protected final int execute(ExecuteContext ctx, ExecuteListener listener) throws SQLException {
        listener.executeStart(ctx);

        // [#4511] [#4753] PostgreSQL doesn't like fetchSize with autoCommit == true
        int f = SettingsTools.getFetchSize(fetchSize, ctx.settings());
        if (REPORT_FETCH_SIZE_WITH_AUTOCOMMIT.contains(ctx.dialect()) && f != 0 && ctx.connection().getAutoCommit())
            log.info("Fetch Size", "A fetch size of " + f + " was set on a auto-commit PostgreSQL connection, which is not recommended. See http://jdbc.postgresql.org/documentation/head/query.html#query-with-cursor");

        SQLException e = executeStatementAndGetFirstResultSet(ctx, rendered.skipUpdateCounts);
        listener.executeEnd(ctx);

        // Fetch a single result set
        notManyIf:
        if (!many) {

            // [#6413] If the first execution yielded an exception, rather than an update count or result set
            //         and that exception is not thrown because of Settings.throwExceptions == THROW_NONE, we can stop
            if (e != null)
                break notManyIf;

            // [#5617] This may happen when using plain SQL API or a MockConnection and expecting a result set where
            //         there is none. The cursor / result is patched into the ctx only for single result sets, where
            //         access to the cursor / result is possible.
            // [#5818] It may also happen in case we're fetching from a batch and the first result is an update count,
            //         not a result set.
            if (ctx.resultSet() == null) {
                DSLContext dsl = DSL.using(ctx.configuration());
                Field<Integer> c = field(name("UPDATE_COUNT"), int.class);
                Result<Record1<Integer>> r = dsl.newResult(c);
                r.add(dsl.newRecord(c).values(ctx.rows()));
                ctx.resultSet(new MockResultSet(r));
            }

            Field<?>[] fields = getFields(ctx.resultSet().getMetaData());
            cursor = new CursorImpl<>(ctx, listener, fields, intern.internIndexes(fields), keepStatement(), keepResultSet(), getRecordType(), SettingsTools.getMaxRows(maxRows, ctx.settings()), autoclosing);

            if (!lazy) {
                result = cursor.fetch();
                cursor = null;
            }
        }

        // Fetch several result sets
        else {
            results = new ResultsImpl(ctx.configuration());
            consumeResultSets(ctx, listener, results, intern, e);
        }

        return result != null ? result.size() : 0;
    }

    @Override
    protected final boolean keepResultSet() {
        return lazy;
    }

    /**
     * Subclasses should indicate whether they want an updatable {@link ResultSet}
     */
    abstract boolean isForUpdate();

    final Collection<? extends Field<?>> coerce() {
        return coerceFields;
    }

    @Override
    public final Result<R> fetch() {
        execute();
        return result;
    }

    @Override
    public final ResultSet fetchResultSet() {
        return fetchLazy().resultSet();
    }

    @Override
    public final Iterator<R> iterator() {
        return fetch().iterator();
    }












    @Override
    public final void subscribe(org.reactivestreams.Subscriber<? super R> subscriber) {
        subscriber.onSubscribe(new org.reactivestreams.Subscription() {
            Cursor<R> c;
            ArrayDeque<R> buffer;

            @Override
            public void request(long n) {
                int i = (int) Math.min(n, Integer.MAX_VALUE);

                try {
                    if (c == null)
                        c = fetchLazyNonAutoClosing();

                    if (buffer == null)
                        buffer = new ArrayDeque<>();

                    if (buffer.size() < i)
                        buffer.addAll(c.fetchNext(i - buffer.size()));

                    boolean complete = buffer.size() < i;
                    while (!buffer.isEmpty()) {
                        subscriber.onNext(buffer.pollFirst());
                    }

                    if (complete)
                        doComplete();
                }
                catch (Throwable t) {
                    subscriber.onError(t);
                    doComplete();
                }
            }

            private void doComplete() {
                close();
                subscriber.onComplete();
            }

            private void close() {
                if (c != null)
                    c.close();
            }

            @Override
            public void cancel() {
                close();
            }
        });
    }

    @Override
    public final CompletionStage<Result<R>> fetchAsync() {
        return fetchAsync(Tools.configuration(this).executorProvider().provide());
    }

    @Override
    public final CompletionStage<Result<R>> fetchAsync(Executor executor) {
        return ExecutorProviderCompletionStage.of(CompletableFuture.supplyAsync(blocking(this::fetch), executor), () -> executor);
    }

    @Override
    public final Stream<R> fetchStream() {
        return Stream.of(1).flatMap(i -> fetchLazy().stream());
    }

    @Override
    public final <E> Stream<E> fetchStreamInto(Class<? extends E> type) {
        return fetchStream().map(r -> r.into(type));
    }

    @Override
    public final <Z extends Record> Stream<Z> fetchStreamInto(Table<Z> table) {
        return fetchStream().map(r -> r.into(table));
    }

    @Override
    public final Stream<R> stream() {
        return fetchStream();
    }

    @Override
    public final <X, A> X collect(Collector<? super R, A, X> collector) {
        try (Cursor<R> c = fetchLazyNonAutoClosing()) {
            return c.collect(collector);
        }
    }



    @Override
    public final Cursor<R> fetchLazy() {
        return fetchLazy(fetchSize);
    }

    /**
     * When we manage the lifecycle of a returned {@link Cursor} internally in
     * jOOQ, then the cursor must not be auto-closed.
     */
    final Cursor<R> fetchLazyNonAutoClosing() {
        final boolean previousAutoClosing = autoclosing;

        // [#3515] TODO: Avoid modifying a Query's per-execution state
        autoclosing = false;

        try {
            return fetchLazy();
        }
        finally {
            autoclosing = previousAutoClosing;
        }
    }

    @Override
    @Deprecated
    public final Cursor<R> fetchLazy(int size) {
        final int previousFetchSize = fetchSize;

        // [#3515] TODO: Avoid modifying a Query's per-execution state
        lazy = true;
        fetchSize = size;

        try {
            execute();
        }
        finally {
            lazy = false;
            fetchSize = previousFetchSize;
        }

        return cursor;
    }

    @Override
    public final Results fetchMany() {

        // [#3515] TODO: Avoid modifying a Query's per-execution state
        many = true;

        try {
            execute();
        }
        finally {
            many = false;
        }

        return results;
    }

    @Override
    public final <T> List<T> fetch(Field<T> field) {
        return fetch().getValues(field);
    }

    @Override
    public final <T> List<T> fetch(Field<?> field, Class<? extends T> type) {
        return fetch().getValues(field, type);
    }

    @Override
    public final <T, U> List<U> fetch(Field<T> field, Converter<? super T, ? extends U> converter) {
        return fetch().getValues(field, converter);
    }

    @Override
    public final List<?> fetch(int fieldIndex) {
        return fetch().getValues(fieldIndex);
    }

    @Override
    public final <T> List<T> fetch(int fieldIndex, Class<? extends T> type) {
        return fetch().getValues(fieldIndex, type);
    }

    @Override
    public final <U> List<U> fetch(int fieldIndex, Converter<?, ? extends U> converter) {
        return fetch().getValues(fieldIndex, converter);
    }

    @Override
    public final List<?> fetch(String fieldName) {
        return fetch().getValues(fieldName);
    }

    @Override
    public final <T> List<T> fetch(String fieldName, Class<? extends T> type) {
        return fetch().getValues(fieldName, type);
    }

    @Override
    public final <U> List<U> fetch(String fieldName, Converter<?, ? extends U> converter) {
        return fetch().getValues(fieldName, converter);
    }

    @Override
    public final List<?> fetch(Name fieldName) {
        return fetch().getValues(fieldName);
    }

    @Override
    public final <T> List<T> fetch(Name fieldName, Class<? extends T> type) {
        return fetch().getValues(fieldName, type);
    }

    @Override
    public final <U> List<U> fetch(Name fieldName, Converter<?, ? extends U> converter) {
        return fetch().getValues(fieldName, converter);
    }

    @Override
    public final <T> T fetchOne(Field<T> field) {
        R record = fetchOne();
        return record == null ? null : record.get(field);
    }

    @Override
    public final <T> T fetchOne(Field<?> field, Class<? extends T> type) {
        return Convert.convert(fetchOne(field), type);
    }

    @Override
    public final <T, U> U fetchOne(Field<T> field, Converter<? super T, ? extends U> converter) {
        return Convert.convert(fetchOne(field), converter);
    }

    @Override
    public final Object fetchOne(int fieldIndex) {
        R record = fetchOne();
        return record == null ? null : record.get(fieldIndex);
    }

    @Override
    public final <T> T fetchOne(int fieldIndex, Class<? extends T> type) {
        return Convert.convert(fetchOne(fieldIndex), type);
    }

    @Override
    public final <U> U fetchOne(int fieldIndex, Converter<?, ? extends U> converter) {
        return Convert.convert(fetchOne(fieldIndex), converter);
    }

    @Override
    public final Object fetchOne(String fieldName) {
        R record = fetchOne();
        return record == null ? null : record.get(fieldName);
    }

    @Override
    public final <T> T fetchOne(String fieldName, Class<? extends T> type) {
        return Convert.convert(fetchOne(fieldName), type);
    }

    @Override
    public final <U> U fetchOne(String fieldName, Converter<?, ? extends U> converter) {
        return Convert.convert(fetchOne(fieldName), converter);
    }

    @Override
    public final Object fetchOne(Name fieldName) {
        R record = fetchOne();
        return record == null ? null : record.get(fieldName);
    }

    @Override
    public final <T> T fetchOne(Name fieldName, Class<? extends T> type) {
        return Convert.convert(fetchOne(fieldName), type);
    }

    @Override
    public final <U> U fetchOne(Name fieldName, Converter<?, ? extends U> converter) {
        return Convert.convert(fetchOne(fieldName), converter);
    }

    @Override
    public final R fetchOne() {
        return Tools.fetchOne(fetchLazyNonAutoClosing(), hasLimit1());
    }

    @Override
    public final <E> E fetchOne(RecordMapper<? super R, E> mapper) {
        R record = fetchOne();
        return record == null ? null : mapper.map(record);
    }

    @Override
    public final Map<String, Object> fetchOneMap() {
        R record = fetchOne();
        return record == null ? null : record.intoMap();
    }

    @Override
    public final Object[] fetchOneArray() {
        R record = fetchOne();
        return record == null ? null : record.intoArray();
    }

    @Override
    public final <E> E fetchOneInto(Class<? extends E> type) {
        R record = fetchOne();
        return record == null ? null : record.into(type);
    }

    @Override
    public final <Z extends Record> Z fetchOneInto(Table<Z> table) {
        R record = fetchOne();
        return record == null ? null : record.into(table);
    }

    @Override
    public final <T> T fetchSingle(Field<T> field) {
        return fetchSingle().get(field);
    }

    @Override
    public final <T> T fetchSingle(Field<?> field, Class<? extends T> type) {
        return Convert.convert(fetchSingle(field), type);
    }

    @Override
    public final <T, U> U fetchSingle(Field<T> field, Converter<? super T, ? extends U> converter) {
        return Convert.convert(fetchSingle(field), converter);
    }

    @Override
    public final Object fetchSingle(int fieldIndex) {
        return fetchSingle().get(fieldIndex);
    }

    @Override
    public final <T> T fetchSingle(int fieldIndex, Class<? extends T> type) {
        return Convert.convert(fetchSingle(fieldIndex), type);
    }

    @Override
    public final <U> U fetchSingle(int fieldIndex, Converter<?, ? extends U> converter) {
        return Convert.convert(fetchSingle(fieldIndex), converter);
    }

    @Override
    public final Object fetchSingle(String fieldName) {
        return fetchSingle().get(fieldName);
    }

    @Override
    public final <T> T fetchSingle(String fieldName, Class<? extends T> type) {
        return Convert.convert(fetchSingle(fieldName), type);
    }

    @Override
    public final <U> U fetchSingle(String fieldName, Converter<?, ? extends U> converter) {
        return Convert.convert(fetchSingle(fieldName), converter);
    }

    @Override
    public final Object fetchSingle(Name fieldName) {
        return fetchSingle().get(fieldName);
    }

    @Override
    public final <T> T fetchSingle(Name fieldName, Class<? extends T> type) {
        return Convert.convert(fetchSingle(fieldName), type);
    }

    @Override
    public final <U> U fetchSingle(Name fieldName, Converter<?, ? extends U> converter) {
        return Convert.convert(fetchSingle(fieldName), converter);
    }

    @Override
    public final R fetchSingle() {
        return Tools.fetchSingle(fetchLazyNonAutoClosing(), hasLimit1());
    }

    @Override
    public final <E> E fetchSingle(RecordMapper<? super R, E> mapper) {
        return mapper.map(fetchSingle());
    }

    @Override
    public final Map<String, Object> fetchSingleMap() {
        return fetchSingle().intoMap();
    }

    @Override
    public final Object[] fetchSingleArray() {
        return fetchSingle().intoArray();
    }

    @Override
    public final <E> E fetchSingleInto(Class<? extends E> type) {
        return fetchSingle().into(type);
    }

    @Override
    public final <Z extends Record> Z fetchSingleInto(Table<Z> table) {
        return fetchSingle().into(table);
    }


    @Override
    public final <T> Optional<T> fetchOptional(Field<T> field) {
        return Optional.ofNullable(fetchOne(field));
    }

    @Override
    public final <T> Optional<T> fetchOptional(Field<?> field, Class<? extends T> type) {
        return Optional.ofNullable(fetchOne(field, type));
    }

    @Override
    public final <T, U> Optional<U> fetchOptional(Field<T> field, Converter<? super T, ? extends U> converter) {
        return Optional.ofNullable(fetchOne(field, converter));
    }

    @Override
    public final Optional<?> fetchOptional(int fieldIndex) {
        return Optional.ofNullable(fetchOne(fieldIndex));
    }

    @Override
    public final <T> Optional<T> fetchOptional(int fieldIndex, Class<? extends T> type) {
        return Optional.ofNullable(fetchOne(fieldIndex, type));
    }

    @Override
    public final <U> Optional<U> fetchOptional(int fieldIndex, Converter<?, ? extends U> converter) {
        return Optional.ofNullable(fetchOne(fieldIndex, converter));
    }

    @Override
    public final Optional<?> fetchOptional(String fieldName) {
        return Optional.ofNullable(fetchOne(fieldName));
    }

    @Override
    public final <T> Optional<T> fetchOptional(String fieldName, Class<? extends T> type) {
        return Optional.ofNullable(fetchOne(fieldName, type));
    }

    @Override
    public final <U> Optional<U> fetchOptional(String fieldName, Converter<?, ? extends U> converter) {
        return Optional.ofNullable(fetchOne(fieldName, converter));
    }

    @Override
    public final Optional<?> fetchOptional(Name fieldName) {
        return Optional.ofNullable(fetchOne(fieldName));
    }

    @Override
    public final <T> Optional<T> fetchOptional(Name fieldName, Class<? extends T> type) {
        return Optional.ofNullable(fetchOne(fieldName, type));
    }

    @Override
    public final <U> Optional<U> fetchOptional(Name fieldName, Converter<?, ? extends U> converter) {
        return Optional.ofNullable(fetchOne(fieldName, converter));
    }

    @Override
    public final Optional<R> fetchOptional() {
        return Optional.ofNullable(fetchOne());
    }

    @Override
    public final <E> Optional<E> fetchOptional(RecordMapper<? super R, E> mapper) {
        return Optional.ofNullable(fetchOne(mapper));
    }

    @Override
    public final Optional<Map<String, Object>> fetchOptionalMap() {
        return Optional.ofNullable(fetchOneMap());
    }

    @Override
    public final Optional<Object[]> fetchOptionalArray() {
        return Optional.ofNullable(fetchOneArray());
    }

    @Override
    public final <E> Optional<E> fetchOptionalInto(Class<? extends E> type) {
        return Optional.ofNullable(fetchOneInto(type));
    }

    @Override
    public final <Z extends Record> Optional<Z> fetchOptionalInto(Table<Z> table) {
        return Optional.ofNullable(fetchOneInto(table));
    }


    @Override
    public final <T> T fetchAny(Field<T> field) {
        R record = fetchAny();
        return record == null ? null : record.get(field);
    }

    @Override
    public final <T> T fetchAny(Field<?> field, Class<? extends T> type) {
        return Convert.convert(fetchAny(field), type);
    }

    @Override
    public final <T, U> U fetchAny(Field<T> field, Converter<? super T, ? extends U> converter) {
        return Convert.convert(fetchAny(field), converter);
    }

    @Override
    public final Object fetchAny(int fieldIndex) {
        R record = fetchAny();
        return record == null ? null : record.get(fieldIndex);
    }

    @Override
    public final <T> T fetchAny(int fieldIndex, Class<? extends T> type) {
        return Convert.convert(fetchAny(fieldIndex), type);
    }

    @Override
    public final <U> U fetchAny(int fieldIndex, Converter<?, ? extends U> converter) {
        return Convert.convert(fetchAny(fieldIndex), converter);
    }

    @Override
    public final Object fetchAny(String fieldName) {
        R record = fetchAny();
        return record == null ? null : record.get(fieldName);
    }

    @Override
    public final <T> T fetchAny(String fieldName, Class<? extends T> type) {
        return Convert.convert(fetchAny(fieldName), type);
    }

    @Override
    public final <U> U fetchAny(String fieldName, Converter<?, ? extends U> converter) {
        return Convert.convert(fetchAny(fieldName), converter);
    }

    @Override
    public final Object fetchAny(Name fieldName) {
        R record = fetchAny();
        return record == null ? null : record.get(fieldName);
    }

    @Override
    public final <T> T fetchAny(Name fieldName, Class<? extends T> type) {
        return Convert.convert(fetchAny(fieldName), type);
    }

    @Override
    public final <U> U fetchAny(Name fieldName, Converter<?, ? extends U> converter) {
        return Convert.convert(fetchAny(fieldName), converter);
    }

    @Override
    public final R fetchAny() {
        Cursor<R> c = fetchLazyNonAutoClosing();

        try {
            return c.fetchNext();
        }
        finally {
            c.close();
        }
    }

    @Override
    public final <E> E fetchAny(RecordMapper<? super R, E> mapper) {
        R record = fetchAny();
        return record == null ? null : mapper.map(record);
    }

    @Override
    public final Map<String, Object> fetchAnyMap() {
        R record = fetchAny();
        return record == null ? null : record.intoMap();
    }

    @Override
    public final Object[] fetchAnyArray() {
        R record = fetchAny();
        return record == null ? null : record.intoArray();
    }

    @Override
    public final <E> E fetchAnyInto(Class<? extends E> type) {
        R record = fetchAny();
        return record == null ? null : record.into(type);
    }

    @Override
    public final <Z extends Record> Z fetchAnyInto(Table<Z> table) {
        R record = fetchAny();
        return record == null ? null : record.into(table);
    }

    @Override
    public final <K> Map<K, R> fetchMap(Field<K> key) {
        return fetch().intoMap(key);
    }

    @Override
    public final Map<?, R> fetchMap(int keyFieldIndex) {
        return fetch().intoMap(keyFieldIndex);
    }

    @Override
    public final Map<?, R> fetchMap(String keyFieldName) {
        return fetch().intoMap(keyFieldName);
    }

    @Override
    public final Map<?, R> fetchMap(Name keyFieldName) {
        return fetch().intoMap(keyFieldName);
    }

    @Override
    public final <K, V> Map<K, V> fetchMap(Field<K> key, Field<V> value) {
        return fetch().intoMap(key, value);
    }

    @Override
    public final Map<?, ?> fetchMap(int keyFieldIndex, int valueFieldIndex) {
        return fetch().intoMap(keyFieldIndex, valueFieldIndex);
    }

    @Override
    public final Map<?, ?> fetchMap(String keyFieldName, String valueFieldName) {
        return fetch().intoMap(keyFieldName, valueFieldName);
    }

    @Override
    public final Map<?, ?> fetchMap(Name keyFieldName, Name valueFieldName) {
        return fetch().intoMap(keyFieldName, valueFieldName);
    }

    @Override
    public final <K, E> Map<K, E> fetchMap(Field<K> key, Class<? extends E> type) {
        return fetch().intoMap(key, type);
    }

    @Override
    public final <E> Map<?, E> fetchMap(int keyFieldIndex, Class<? extends E> type) {
        return fetch().intoMap(keyFieldIndex, type);
    }

    @Override
    public final <E> Map<?, E> fetchMap(String keyFieldName, Class<? extends E> type) {
        return fetch().intoMap(keyFieldName, type);
    }

    @Override
    public final <E> Map<?, E> fetchMap(Name keyFieldName, Class<? extends E> type) {
        return fetch().intoMap(keyFieldName, type);
    }

    @Override
    public final <K, E> Map<K, E> fetchMap(Field<K> key, RecordMapper<? super R, E> mapper) {
        return fetch().intoMap(key, mapper);
    }

    @Override
    public final <E> Map<?, E> fetchMap(int keyFieldIndex, RecordMapper<? super R, E> mapper) {
        return fetch().intoMap(keyFieldIndex, mapper);
    }

    @Override
    public final <E> Map<?, E> fetchMap(String keyFieldName, RecordMapper<? super R, E> mapper) {
        return fetch().intoMap(keyFieldName, mapper);
    }

    @Override
    public final <E> Map<?, E> fetchMap(Name keyFieldName, RecordMapper<? super R, E> mapper) {
        return fetch().intoMap(keyFieldName, mapper);
    }

    @Override
    public final Map<Record, R> fetchMap(Field<?>[] keys) {
        return fetch().intoMap(keys);
    }

    @Override
    public final Map<Record, R> fetchMap(int[] keyFieldIndexes) {
        return fetch().intoMap(keyFieldIndexes);
    }

    @Override
    public final Map<Record, R> fetchMap(String[] keyFieldNames) {
        return fetch().intoMap(keyFieldNames);
    }

    @Override
    public final Map<Record, R> fetchMap(Name[] keyFieldNames) {
        return fetch().intoMap(keyFieldNames);
    }

    @Override
    public final Map<Record, Record> fetchMap(Field<?>[] keys, Field<?>[] values) {
        return fetch().intoMap(keys, values);
    }

    @Override
    public final Map<Record, Record> fetchMap(int[] keyFieldIndexes, int[] valueFieldIndexes) {
        return fetch().intoMap(keyFieldIndexes, valueFieldIndexes);
    }

    @Override
    public final Map<Record, Record> fetchMap(String[] keyFieldNames, String[] valueFieldNames) {
        return fetch().intoMap(keyFieldNames, valueFieldNames);
    }

    @Override
    public final Map<Record, Record> fetchMap(Name[] keyFieldNames, Name[] valueFieldNames) {
        return fetch().intoMap(keyFieldNames, valueFieldNames);
    }

    @Override
    public final <E> Map<List<?>, E> fetchMap(Field<?>[] keys, Class<? extends E> type) {
        return fetch().intoMap(keys, type);
    }

    @Override
    public final <E> Map<List<?>, E> fetchMap(int[] keyFieldIndexes, Class<? extends E> type) {
        return fetch().intoMap(keyFieldIndexes, type);
    }

    @Override
    public final <E> Map<List<?>, E> fetchMap(String[] keyFieldNames, Class<? extends E> type) {
        return fetch().intoMap(keyFieldNames, type);
    }

    @Override
    public final <E> Map<List<?>, E> fetchMap(Name[] keyFieldNames, Class<? extends E> type) {
        return fetch().intoMap(keyFieldNames, type);
    }

    @Override
    public final <E> Map<List<?>, E> fetchMap(Field<?>[] keys, RecordMapper<? super R, E> mapper) {
        return fetch().intoMap(keys, mapper);
    }

    @Override
    public final <E> Map<List<?>, E> fetchMap(int[] keyFieldIndexes, RecordMapper<? super R, E> mapper) {
        return fetch().intoMap(keyFieldIndexes, mapper);
    }

    @Override
    public final <E> Map<List<?>, E> fetchMap(String[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return fetch().intoMap(keyFieldNames, mapper);
    }

    @Override
    public final <E> Map<List<?>, E> fetchMap(Name[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return fetch().intoMap(keyFieldNames, mapper);
    }

    @Override
    public final <K> Map<K, R> fetchMap(Class<? extends K> keyType) {
        return fetch().intoMap(keyType);
    }

    @Override
    public final <K, V> Map<K, V> fetchMap(Class<? extends K> keyType, Class<? extends V> valueType) {
        return fetch().intoMap(keyType, valueType);
    }

    @Override
    public final <K, V> Map<K, V> fetchMap(Class<? extends K> keyType, RecordMapper<? super R, V> valueMapper) {
        return fetch().intoMap(keyType, valueMapper);
    }

    @Override
    public final <K> Map<K, R> fetchMap(RecordMapper<? super R, K> keyMapper) {
        return fetch().intoMap(keyMapper);
    }

    @Override
    public final <K, V> Map<K, V> fetchMap(RecordMapper<? super R, K> keyMapper, Class<V> valueType) {
        return fetch().intoMap(keyMapper, valueType);
    }

    @Override
    public final <K, V> Map<K, V> fetchMap(RecordMapper<? super R, K> keyMapper, RecordMapper<? super R, V> valueMapper) {
        return fetch().intoMap(keyMapper, valueMapper);
    }

    @Override
    public final <S extends Record> Map<S, R> fetchMap(Table<S> table) {
        return fetch().intoMap(table);
    }

    @Override
    public final <S extends Record, T extends Record> Map<S, T> fetchMap(Table<S> keyTable, Table<T> valueTable) {
        return fetch().intoMap(keyTable, valueTable);
    }

    @Override
    public final <E, S extends Record> Map<S, E> fetchMap(Table<S> table, Class<? extends E> type) {
        return fetch().intoMap(table, type);
    }

    @Override
    public final <E, S extends Record> Map<S, E> fetchMap(Table<S> table, RecordMapper<? super R, E> mapper) {
        return fetch().intoMap(table, mapper);
    }

    @Override
    public final List<Map<String, Object>> fetchMaps() {
        return fetch().intoMaps();
    }

    @Override
    public final <K> Map<K, Result<R>> fetchGroups(Field<K> key) {
        return fetch().intoGroups(key);
    }

    @Override
    public final Map<?, Result<R>> fetchGroups(int keyFieldIndex) {
        return fetch().intoGroups(keyFieldIndex);
    }

    @Override
    public final Map<?, Result<R>> fetchGroups(String keyFieldName) {
        return fetch().intoGroups(keyFieldName);
    }

    @Override
    public final Map<?, Result<R>> fetchGroups(Name keyFieldName) {
        return fetch().intoGroups(keyFieldName);
    }

    @Override
    public final <K, V> Map<K, List<V>> fetchGroups(Field<K> key, Field<V> value) {
        return fetch().intoGroups(key, value);
    }

    @Override
    public final Map<?, List<?>> fetchGroups(int keyFieldIndex, int valueFieldIndex) {
        return fetch().intoGroups(keyFieldIndex, valueFieldIndex);
    }

    @Override
    public final Map<?, List<?>> fetchGroups(String keyFieldName, String valueFieldName) {
        return fetch().intoGroups(keyFieldName, valueFieldName);
    }

    @Override
    public final Map<?, List<?>> fetchGroups(Name keyFieldName, Name valueFieldName) {
        return fetch().intoGroups(keyFieldName, valueFieldName);
    }

    @Override
    public final <K, E> Map<K, List<E>> fetchGroups(Field<K> key, Class<? extends E> type) {
        return fetch().intoGroups(key, type);
    }

    @Override
    public final <E> Map<?, List<E>> fetchGroups(int keyFieldIndex, Class<? extends E> type) {
        return fetch().intoGroups(keyFieldIndex, type);
    }

    @Override
    public final <E> Map<?, List<E>> fetchGroups(String keyFieldName, Class<? extends E> type) {
        return fetch().intoGroups(keyFieldName, type);
    }

    @Override
    public final <E> Map<?, List<E>> fetchGroups(Name keyFieldName, Class<? extends E> type) {
        return fetch().intoGroups(keyFieldName, type);
    }

    @Override
    public final <K, E> Map<K, List<E>> fetchGroups(Field<K> key, RecordMapper<? super R, E> mapper) {
        return fetch().intoGroups(key, mapper);
    }

    @Override
    public final <E> Map<?, List<E>> fetchGroups(int keyFieldIndex, RecordMapper<? super R, E> mapper) {
        return fetch().intoGroups(keyFieldIndex, mapper);
    }

    @Override
    public final <E> Map<?, List<E>> fetchGroups(String keyFieldName, RecordMapper<? super R, E> mapper) {
        return fetch().intoGroups(keyFieldName, mapper);
    }

    @Override
    public final <E> Map<?, List<E>> fetchGroups(Name keyFieldName, RecordMapper<? super R, E> mapper) {
        return fetch().intoGroups(keyFieldName, mapper);
    }

    @Override
    public final Map<Record, Result<R>> fetchGroups(Field<?>[] keys) {
        return fetch().intoGroups(keys);
    }

    @Override
    public final Map<Record, Result<R>> fetchGroups(int[] keyFieldIndexes) {
        return fetch().intoGroups(keyFieldIndexes);
    }

    @Override
    public final Map<Record, Result<R>> fetchGroups(String[] keyFieldNames) {
        return fetch().intoGroups(keyFieldNames);
    }

    @Override
    public final Map<Record, Result<R>> fetchGroups(Name[] keyFieldNames) {
        return fetch().intoGroups(keyFieldNames);
    }

    @Override
    public final Map<Record, Result<Record>> fetchGroups(Field<?>[] keys, Field<?>[] values) {
        return fetch().intoGroups(keys, values);
    }

    @Override
    public final Map<Record, Result<Record>> fetchGroups(int[] keyFieldIndexes, int[] valueFieldIndexes) {
        return fetch().intoGroups(keyFieldIndexes, valueFieldIndexes);
    }

    @Override
    public final Map<Record, Result<Record>> fetchGroups(String[] keyFieldNames, String[] valueFieldNames) {
        return fetch().intoGroups(keyFieldNames, valueFieldNames);
    }

    @Override
    public final Map<Record, Result<Record>> fetchGroups(Name[] keyFieldNames, Name[] valueFieldNames) {
        return fetch().intoGroups(keyFieldNames, valueFieldNames);
    }

    @Override
    public final <E> Map<Record, List<E>> fetchGroups(Field<?>[] keys, Class<? extends E> type) {
        return fetch().intoGroups(keys, type);
    }

    @Override
    public final <E> Map<Record, List<E>> fetchGroups(int[] keyFieldIndexes, Class<? extends E> type) {
        return fetch().intoGroups(keyFieldIndexes, type);
    }

    @Override
    public final <E> Map<Record, List<E>> fetchGroups(String[] keyFieldNames, Class<? extends E> type) {
        return fetch().intoGroups(keyFieldNames, type);
    }

    @Override
    public final <E> Map<Record, List<E>> fetchGroups(Name[] keyFieldNames, Class<? extends E> type) {
        return fetch().intoGroups(keyFieldNames, type);
    }

    @Override
    public final <E> Map<Record, List<E>> fetchGroups(int[] keyFieldIndexes, RecordMapper<? super R, E> mapper) {
        return fetch().intoGroups(keyFieldIndexes, mapper);
    }

    @Override
    public final <E> Map<Record, List<E>> fetchGroups(String[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return fetch().intoGroups(keyFieldNames, mapper);
    }

    @Override
    public final <E> Map<Record, List<E>> fetchGroups(Name[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return fetch().intoGroups(keyFieldNames, mapper);
    }

    @Override
    public final <E> Map<Record, List<E>> fetchGroups(Field<?>[] keys, RecordMapper<? super R, E> mapper) {
        return fetch().intoGroups(keys, mapper);
    }

    @Override
    public final <K> Map<K, Result<R>> fetchGroups(Class<? extends K> keyType) {
        return fetch().intoGroups(keyType);
    }

    @Override
    public final <K, V> Map<K, List<V>> fetchGroups(Class<? extends K> keyType, Class<? extends V> valueType) {
        return fetch().intoGroups(keyType, valueType);
    }

    @Override
    public final <K, V> Map<K, List<V>> fetchGroups(Class<? extends K> keyType, RecordMapper<? super R, V> valueMapper) {
        return fetch().intoGroups(keyType, valueMapper);
    }

    @Override
    public final <K> Map<K, Result<R>> fetchGroups(RecordMapper<? super R, K> keyMapper) {
        return fetch().intoGroups(keyMapper);
    }

    @Override
    public final <K, V> Map<K, List<V>> fetchGroups(RecordMapper<? super R, K> keyMapper, Class<V> valueType) {
        return fetch().intoGroups(keyMapper, valueType);
    }

    @Override
    public final <K, V> Map<K, List<V>> fetchGroups(RecordMapper<? super R, K> keyMapper, RecordMapper<? super R, V> valueMapper) {
        return fetch().intoGroups(keyMapper, valueMapper);
    }

    @Override
    public final <S extends Record> Map<S, Result<R>> fetchGroups(Table<S> table) {
        return fetch().intoGroups(table);
    }

    @Override
    public final <S extends Record, T extends Record> Map<S, Result<T>> fetchGroups(Table<S> keyTable, Table<T> valueTable) {
        return fetch().intoGroups(keyTable, valueTable);
    }

    @Override
    public final <E, S extends Record> Map<S, List<E>> fetchGroups(Table<S> table, Class<? extends E> type) {
        return fetch().intoGroups(table, type);
    }

    @Override
    public final <E, S extends Record> Map<S, List<E>> fetchGroups(Table<S> table, RecordMapper<? super R, E> mapper) {
        return fetch().intoGroups(table, mapper);
    }

    @Override
    public final Object[][] fetchArrays() {
        return fetch().intoArrays();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final R[] fetchArray() {
        Result<R> r = fetch();
        return r.toArray((R[]) Array.newInstance(getRecordType(), r.size()));
    }

    @Override
    public final Object[] fetchArray(int fieldIndex) {
        return fetch().intoArray(fieldIndex);
    }

    @Override
    public final <T> T[] fetchArray(int fieldIndex, Class<? extends T> type) {
        return fetch().intoArray(fieldIndex, type);
    }

    @Override
    public final <U> U[] fetchArray(int fieldIndex, Converter<?, ? extends U> converter) {
        return fetch().intoArray(fieldIndex, converter);
    }

    @Override
    public final Object[] fetchArray(String fieldName) {
        return fetch().intoArray(fieldName);
    }

    @Override
    public final <T> T[] fetchArray(String fieldName, Class<? extends T> type) {
        return fetch().intoArray(fieldName, type);
    }

    @Override
    public final <U> U[] fetchArray(String fieldName, Converter<?, ? extends U> converter) {
        return fetch().intoArray(fieldName, converter);
    }

    @Override
    public final Object[] fetchArray(Name fieldName) {
        return fetch().intoArray(fieldName);
    }

    @Override
    public final <T> T[] fetchArray(Name fieldName, Class<? extends T> type) {
        return fetch().intoArray(fieldName, type);
    }

    @Override
    public final <U> U[] fetchArray(Name fieldName, Converter<?, ? extends U> converter) {
        return fetch().intoArray(fieldName, converter);
    }

    @Override
    public final <T> T[] fetchArray(Field<T> field) {
        return fetch().intoArray(field);
    }

    @Override
    public final <T> T[] fetchArray(Field<?> field, Class<? extends T> type) {
        return fetch().intoArray(field, type);
    }

    @Override
    public final <T, U> U[] fetchArray(Field<T> field, Converter<? super T, ? extends U> converter) {
        return fetch().intoArray(field, converter);
    }

    @Override
    public final <E> Set<E> fetchSet(RecordMapper<? super R, E> mapper) {
        return fetch().intoSet(mapper);
    }

    @Override
    public final Set<?> fetchSet(int fieldIndex) {
        return fetch().intoSet(fieldIndex);
    }

    @Override
    public final <T> Set<T> fetchSet(int fieldIndex, Class<? extends T> type) {
        return fetch().intoSet(fieldIndex, type);
    }

    @Override
    public final <U> Set<U> fetchSet(int fieldIndex, Converter<?, ? extends U> converter) {
        return fetch().intoSet(fieldIndex, converter);
    }

    @Override
    public final Set<?> fetchSet(String fieldName) {
        return fetch().intoSet(fieldName);
    }

    @Override
    public final <T> Set<T> fetchSet(String fieldName, Class<? extends T> type) {
        return fetch().intoSet(fieldName, type);
    }

    @Override
    public final <U> Set<U> fetchSet(String fieldName, Converter<?, ? extends U> converter) {
        return fetch().intoSet(fieldName, converter);
    }

    @Override
    public final Set<?> fetchSet(Name fieldName) {
        return fetch().intoSet(fieldName);
    }

    @Override
    public final <T> Set<T> fetchSet(Name fieldName, Class<? extends T> type) {
        return fetch().intoSet(fieldName, type);
    }

    @Override
    public final <U> Set<U> fetchSet(Name fieldName, Converter<?, ? extends U> converter) {
        return fetch().intoSet(fieldName, converter);
    }

    @Override
    public final <T> Set<T> fetchSet(Field<T> field) {
        return fetch().intoSet(field);
    }

    @Override
    public final <T> Set<T> fetchSet(Field<?> field, Class<? extends T> type) {
        return fetch().intoSet(field, type);
    }

    @Override
    public final <T, U> Set<U> fetchSet(Field<T> field, Converter<? super T, ? extends U> converter) {
        return fetch().intoSet(field, converter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Class<? extends R> getRecordType() {
        if (coerceTable != null)
            return (Class<? extends R>) coerceTable.getRecordType();

        return getRecordType0();
    }

    abstract Class<? extends R> getRecordType0();

    @Override
    public final <T> List<T> fetchInto(Class<? extends T> type) {
        return fetch().into(type);
    }

    @Override
    public final <Z extends Record> Result<Z> fetchInto(Table<Z> table) {
        return fetch().into(table);
    }

    @Override
    public final <H extends RecordHandler<? super R>> H fetchInto(H handler) {
        return fetch().into(handler);
    }

    @Override
    public final <E> List<E> fetch(RecordMapper<? super R, E> mapper) {
        return fetch().map(mapper);
    }

    @Override
    @Deprecated
    public final org.jooq.FutureResult<R> fetchLater() {
        ExecutorService executor = newSingleThreadExecutor();
        Future<Result<R>> future = executor.submit(new ResultQueryCallable());
        return new FutureResultImpl<>(future, executor);
    }

    @Override
    @Deprecated
    public final org.jooq.FutureResult<R> fetchLater(ExecutorService executor) {
        Future<Result<R>> future = executor.submit(new ResultQueryCallable());
        return new FutureResultImpl<>(future);
    }

    @Override
    public final Result<R> getResult() {
        return result;
    }

    /**
     * A wrapper for the {@link ResultQuery#fetch()} method
     */
    private final class ResultQueryCallable implements Callable<Result<R>> {

        @Override
        public final Result<R> call() throws Exception {
            return fetch();
        }
    }

    @SuppressWarnings("rawtypes")
    private final boolean hasLimit1() {
        if (this instanceof SelectQueryImpl) {
            Limit l = ((SelectQueryImpl) this).getLimit();
            return !l.withTies() && !l.percent() && l.limitOne();
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <X extends Record> ResultQuery<X> coerce(Table<X> table) {
        this.coerceTable = table;
        return (ResultQuery<X>) coerce(Arrays.asList(table.fields()));
    }

    @Override
    public final ResultQuery<Record> coerce(Field<?>... fields) {
        return coerce(Arrays.asList(fields));
    }

    @SuppressWarnings("unchecked")
    @Override
    public final ResultQuery<Record> coerce(Collection<? extends Field<?>> fields) {
        this.coerceFields = fields;
        return (ResultQuery<Record>) this;
    }



    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1> ResultQuery<Record1<T1>> coerce(Field<T1> field1) {
        return (ResultQuery) coerce(new Field[] { field1 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2> ResultQuery<Record2<T1, T2>> coerce(Field<T1> field1, Field<T2> field2) {
        return (ResultQuery) coerce(new Field[] { field1, field2 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3> ResultQuery<Record3<T1, T2, T3>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4> ResultQuery<Record4<T1, T2, T3, T4>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5> ResultQuery<Record5<T1, T2, T3, T4, T5>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6> ResultQuery<Record6<T1, T2, T3, T4, T5, T6>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7> ResultQuery<Record7<T1, T2, T3, T4, T5, T6, T7>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8> ResultQuery<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> ResultQuery<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> ResultQuery<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> ResultQuery<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> ResultQuery<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> ResultQuery<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> ResultQuery<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> ResultQuery<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> ResultQuery<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> ResultQuery<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> ResultQuery<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> ResultQuery<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> ResultQuery<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> ResultQuery<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21 });
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> ResultQuery<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> coerce(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return (ResultQuery) coerce(new Field[] { field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22 });
    }



}
