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
import static org.jooq.Clause.FIELD;
import static org.jooq.Clause.FIELD_FUNCTION;
// ...
import static org.jooq.SQLDialect.FIREBIRD;
// ...
import static org.jooq.SQLDialect.POSTGRES;
// ...
// ...
import static org.jooq.XMLFormat.RecordFormat.COLUMN_NAME_ELEMENTS;
import static org.jooq.conf.ThrowExceptions.THROW_NONE;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.function;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.param;
import static org.jooq.impl.DSL.sql;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.using;
import static org.jooq.impl.DSL.val;
import static org.jooq.impl.Keywords.K_BEGIN;
import static org.jooq.impl.Keywords.K_BOOLEAN;
import static org.jooq.impl.Keywords.K_CASE;
import static org.jooq.impl.Keywords.K_COLUMNS;
import static org.jooq.impl.Keywords.K_DECLARE;
import static org.jooq.impl.Keywords.K_ELSE;
import static org.jooq.impl.Keywords.K_END;
import static org.jooq.impl.Keywords.K_FALSE;
import static org.jooq.impl.Keywords.K_FOR;
import static org.jooq.impl.Keywords.K_FROM;
import static org.jooq.impl.Keywords.K_FUNCTION;
import static org.jooq.impl.Keywords.K_IS;
import static org.jooq.impl.Keywords.K_NOT;
import static org.jooq.impl.Keywords.K_NULL;
import static org.jooq.impl.Keywords.K_OPEN;
import static org.jooq.impl.Keywords.K_PASSING;
import static org.jooq.impl.Keywords.K_RECORD;
import static org.jooq.impl.Keywords.K_RETURN;
import static org.jooq.impl.Keywords.K_SELECT;
import static org.jooq.impl.Keywords.K_THEN;
import static org.jooq.impl.Keywords.K_TRUE;
import static org.jooq.impl.Keywords.K_TYPE;
import static org.jooq.impl.Keywords.K_WHEN;
import static org.jooq.impl.Keywords.K_XMLTABLE;
import static org.jooq.impl.SQLDataType.INTEGER;
import static org.jooq.impl.SQLDataType.NUMERIC;
import static org.jooq.impl.Tools.EMPTY_FIELD;
import static org.jooq.impl.Tools.executeStatementAndGetFirstResultSet;
import static org.jooq.impl.Tools.settings;
import static org.jooq.impl.Tools.DataKey.DATA_TOP_LEVEL_CTE;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jooq.AggregateFunction;
// ...
import org.jooq.BindContext;
import org.jooq.Binding;
import org.jooq.Catalog;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Converter;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Package;
import org.jooq.Param;
import org.jooq.Parameter;
// ...
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.RenderContext;
import org.jooq.Result;
import org.jooq.Results;
import org.jooq.Routine;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Statement;
import org.jooq.UDT;
import org.jooq.UDTField;
import org.jooq.UDTRecord;
import org.jooq.XMLFormat;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.ControlFlowSignal;
import org.jooq.exception.MappingException;
import org.jooq.impl.ResultsImpl.ResultOrRowsImpl;
import org.jooq.tools.Convert;
import org.jooq.tools.reflect.Reflect;

/**
 * A common base class for stored procedures
 * <p>
 * This type is for JOOQ INTERNAL USE only. Do not reference directly
 *
 * @author Lukas Eder
 */
@org.jooq.Internal
public abstract class AbstractRoutine<T> extends AbstractNamed implements Routine<T> {

    /**
     * Generated UID
     */
    private static final long                 serialVersionUID = 6330037113167106443L;
    private static final Clause[]             CLAUSES          = { FIELD, FIELD_FUNCTION };

    // ------------------------------------------------------------------------
    // Meta-data attributes (the same for every call)
    // ------------------------------------------------------------------------

    private final Schema                      schema;




    private final List<Parameter<?>>          allParameters;
    private final List<Parameter<?>>          inParameters;
    private final List<Parameter<?>>          outParameters;




    private final DataType<T>                 type;
    private Parameter<T>                      returnParameter;
    private ResultsImpl                       results;
    private boolean                           overloaded;
    private boolean                           hasUnnamedParameters;














    // ------------------------------------------------------------------------
    // Call-data attributes (call-specific)
    // ------------------------------------------------------------------------

    private final Map<Parameter<?>, Field<?>> inValues;
    private final Set<Parameter<?>>           inValuesDefaulted;
    private final Set<Parameter<?>>           inValuesNonDefaulted;
    private transient Field<T>                function;

    private Configuration                     configuration;
    private final Map<Parameter<?>, Object>   outValues;
    private final Map<Parameter<?>, Integer>  resultIndexes;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    protected AbstractRoutine(String name, Schema schema) {
        this(name, schema, null, null, null, null);
    }

    protected AbstractRoutine(String name, Schema schema, Package pkg) {
        this(name, schema, pkg, null, null, null);
    }

    protected AbstractRoutine(String name, Schema schema, DataType<T> type) {
        this(name, schema, null, type, null, null);
    }

    protected <X> AbstractRoutine(String name, Schema schema, DataType<X> type, Converter<X, T> converter) {
        this(name, schema, null, type, converter, null);
    }

    protected <X> AbstractRoutine(String name, Schema schema, DataType<X> type, Binding<X, T> binding) {
        this(name, schema, null, type, null, binding);
    }


    protected <X, Y> AbstractRoutine(String name, Schema schema, DataType<X> type, Converter<Y, T> converter, Binding<X, Y> binding) {
        this(name, schema, null, type, converter, binding);
    }

    protected AbstractRoutine(String name, Schema schema, Package pkg, DataType<T> type) {
        this(name, schema, pkg, type, null, null);
    }

    protected <X> AbstractRoutine(String name, Schema schema, Package pkg, DataType<X> type, Converter<X, T> converter) {
        this(name, schema, pkg, type, converter, null);
    }

    protected <X> AbstractRoutine(String name, Schema schema, Package pkg, DataType<X> type, Binding<X, T> binding) {
        this(name, schema, pkg, type, null, binding);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <X, Y> AbstractRoutine(String name, Schema schema, Package pkg, DataType<X> type, Converter<Y, T> converter, Binding<X, Y> binding) {
        super(qualify(pkg != null ? pkg : schema, DSL.name(name)), CommentImpl.NO_COMMENT);

        this.resultIndexes = new HashMap<>();

        this.schema = schema;



        this.allParameters = new ArrayList<>();
        this.inParameters = new ArrayList<>();
        this.outParameters = new ArrayList<>();



        this.results = new ResultsImpl(null);
        this.inValues = new HashMap<>();
        this.inValuesDefaulted = new HashSet<>();
        this.inValuesNonDefaulted = new HashSet<>();
        this.outValues = new HashMap<>();
        this.type = converter == null && binding == null
            ? (DataType<T>) type
            : type.asConvertedDataType(DefaultBinding.newBinding((Converter) converter, type, binding));
    }

    // ------------------------------------------------------------------------
    // Initialise a routine call
    // ------------------------------------------------------------------------

    protected final <N extends Number> void setNumber(Parameter<N> parameter, Number value) {
        setValue(parameter, Convert.convert(value, parameter.getType()));
    }

    protected final void setNumber(Parameter<? extends Number> parameter, Field<? extends Number> value) {
        setField(parameter, value);
    }

    @Override
    public final <Z> void setValue(Parameter<Z> parameter, Z value) {
        set(parameter, value);
    }

    @Override
    public final <Z> void set(Parameter<Z> parameter, Z value) {
        setField(parameter, val(value, parameter.getDataType()));
    }

    /*
     * #326 - Avoid overloading setValue()
     */
    protected final void setField(Parameter<?> parameter, Field<?> value) {
        // Be sure null is correctly represented as a null field
        if (value == null) {
            setField(parameter, val(null, parameter.getDataType()));
        }

        // [#1183] [#3533] Add the field to the in-values and mark them as non-defaulted
        else {
            inValues.put(parameter, value);
            inValuesDefaulted.remove(parameter);
            inValuesNonDefaulted.add(parameter);
        }
    }

    // ------------------------------------------------------------------------
    // Call the routine
    // ------------------------------------------------------------------------

    @Override
    public final void attach(Configuration c) {
        configuration = c;
    }

    @Override
    public final void detach() {
        attach(null);
    }

    @Override
    public final Configuration configuration() {
        return configuration;
    }

    @Override
    public final int execute(Configuration c) {

        // Ensure that all depending Attachables are attached
        Configuration previous = configuration();
        try {
            attach(c);
            return execute();
        }
        finally {
            attach(previous);
        }
    }

    @Override
    public final int execute() {
        SQLDialect family = configuration.family();

        results.clear();
        outValues.clear();

        // [#4254] In PostgreSQL, there are only functions, no procedures. Some
        // functions cannot be called using a CallableStatement, e.g. those with
        // DEFAULT parameters
        if ( family == POSTGRES) {
            return executeSelectFromPOSTGRES();
        }

        // Procedures (no return value) are always executed as CallableStatement
        else if (type == null) {
            return executeCallableStatement();
        }










        else {
            switch (family) {

                // [#852] Some RDBMS don't allow for using JDBC procedure escape
                // syntax for functions. Select functions from DUAL instead
                case HSQLDB:

                    // [#692] HSQLDB cannot SELECT f() FROM [...] when f()
                    // returns a cursor. Instead, SELECT * FROM table(f()) works
                    if (SQLDataType.RESULT.equals(type.getSQLDataType())) {
                        return executeSelectFromHSQLDB();
                    }

                    // Fall through
                    else {
                    }

                case H2:











                    return executeSelect();

                // [#773] If JDBC escape syntax is available for functions, use
                // it to prevent transactional issues when functions issue
                // DML statements
                default:
                    return executeCallableStatement();
            }
        }
    }































    private final int executeSelectFromHSQLDB() {
        DSLContext create = create(configuration);
        Result<?> result = create.selectFrom(table(asField())).fetch();
        outValues.put(returnParameter, result);
        return 0;
    }

    private final int executeSelectFromPOSTGRES() {
        DSLContext create = create(configuration);

        List<Field<?>> fields = new ArrayList<>(1 + outParameters.size());
        if (returnParameter != null)
            fields.add(DSL.field(DSL.name(getName()), returnParameter.getDataType()));
        for (Parameter<?> p : outParameters)
            fields.add(DSL.field(DSL.name(p.getName()), p.getDataType()));

        Result<?> result = create.select(fields).from("{0}", asField()).fetch();

        int i = 0;

        if (returnParameter != null)
            outValues.put(returnParameter, returnParameter.getDataType().convert(result.getValue(0, i++)));
        for (Parameter<?> p : outParameters)
            outValues.put(p, p.getDataType().convert(result.getValue(0, i++)));

        return 0;
    }

    private final int executeSelect() {
        final Field<T> field = asField();
        outValues.put(returnParameter, create(configuration).select(field).fetchOne(field));
        return 0;
    }

    private final int executeCallableStatement() {
        ExecuteContext ctx = new DefaultExecuteContext(configuration, this);
        ExecuteListener listener = ExecuteListeners.get(ctx);

        try {
            // [#8968] Keep start() event inside of lifecycle management
            listener.start(ctx);

            Connection connection = ctx.connection();

            listener.renderStart(ctx);
            // [#1520] TODO: Should the number of bind values be checked, here?
            ctx.sql(create(configuration).render(this));
            listener.renderEnd(ctx);

            listener.prepareStart(ctx);
            ctx.statement(connection.prepareCall(ctx.sql()));
            Tools.setFetchSize(ctx, 0);
            // [#1856] TODO: Add Statement flags like timeout here
            listener.prepareEnd(ctx);

            // [#9295] use query timeout from settings
            int t = SettingsTools.getQueryTimeout(0, ctx.settings());
            if (t != 0)
                ctx.statement().setQueryTimeout(t);

            listener.bindStart(ctx);
            using(configuration).bindContext(ctx.statement()).visit(this);
            registerOutParameters(ctx);
            listener.bindEnd(ctx);

            SQLException e = execute0(ctx, listener);






            // [#2925] Jaybird currently doesn't like fetching OUT parameters and consuming ResultSets
            //         http://tracker.firebirdsql.org/browse/JDBC-350
            if (ctx.family() != FIREBIRD)
                Tools.consumeResultSets(ctx, listener, results, null, e);

            listener.outStart(ctx);
            fetchOutParameters(ctx);
            listener.outEnd(ctx);

            return 0;
        }

        // [#3427] ControlFlowSignals must not be passed on to ExecuteListners
        catch (ControlFlowSignal e) {
            throw e;
        }
        catch (RuntimeException e) {
            ctx.exception(e);
            listener.exception(ctx);
            throw ctx.exception();
        }
        catch (SQLException e) {
            ctx.sqlException(e);
            listener.exception(ctx);
            throw ctx.exception();
        }
        finally {
            Tools.safeClose(listener, ctx);
        }
    }

    private final SQLException execute0(ExecuteContext ctx, ExecuteListener listener) throws SQLException {
        listener.executeStart(ctx);
        SQLException e = executeStatementAndGetFirstResultSet(ctx, 0);
        listener.executeEnd(ctx);

        if (e != null)
            results.resultsOrRows().add(new ResultOrRowsImpl(Tools.translate(ctx.sql(), e)));

        return e;
    }

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override
    public void accept(Context<?> ctx) {
        if (ctx instanceof RenderContext)
            toSQL0((RenderContext) ctx);
        else
            bind0((BindContext) ctx);
    }

    final void bind0(BindContext context) {















































        for (Parameter<?> parameter : getParameters()) {

            // [#1183] [#3533] Skip defaulted parameters
            if (getInParameters().contains(parameter) && inValuesDefaulted.contains(parameter))
                continue;

            bind1(context, parameter, getInValues().get(parameter) != null, resultParameter(parameter));
        }
    }

    private final void bind1(BindContext context, Parameter<?> parameter, boolean bindAsIn, boolean bindAsOut) {
        int index = context.peekIndex();

        if (bindAsOut) {
            resultIndexes.put(parameter, index);





        }

        if (bindAsIn) {





















            context.visit(getInValues().get(parameter));

            // [#391] This happens when null literals are used as IN/OUT
            // parameters. They're not bound as in value, but they need to
            // be registered as OUT parameter
            if (index == context.peekIndex() && bindAsOut)
                context.nextIndex();




        }






        // Skip one index for OUT parameters
        else {
            context.nextIndex();
        }
    }

    final void toSQL0(RenderContext context) {
        toSQLDeclare(context);
        toSQLBegin(context);

        if (getReturnParameter() != null)
            toSQLAssign(context);

        toSQLCall(context);
        context.sql(' ');

//        


        context.sql('(');

        String separator = "";
        List<Parameter<?>> parameters = getParameters();
        Map<Integer, Parameter<?>> indexes = new LinkedHashMap<>();
        for (int i = 0; i < parameters.size(); i++) {
            Parameter<?> parameter = parameters.get(i);

            // The return value has already been written
            if (parameter.equals(getReturnParameter()))
                continue;

            // [#1183] [#3533] Omit defaulted parameters
            else if (inValuesDefaulted.contains(parameter))
                continue;

            // Ordinary IN, INOUT, and OUT parameters
            else
                indexes.put(i, parameter);
        }

        boolean indent = false;






        if (indent)
            context.formatIndentStart()
                   .formatNewLine();

        int i = 0;
        for (Entry<Integer, Parameter<?>> entry : indexes.entrySet()) {
            Parameter<?> parameter = entry.getValue();
            int index = entry.getKey();
            context.sql(separator);

            if (indent && i++ > 0)
                context.formatNewLine();

            // OUT and IN OUT parameters are always written as a '?' bind variable
            if (getOutParameters().contains(parameter))
                toSQLOutParam(context, parameter, index);

            // IN parameters are rendered normally
            else
                toSQLInParam(context, parameter, index, getInValues().get(parameter));

            separator = ", ";
        }

        if (indent)
            context.formatIndentEnd().formatNewLine();

//        


        context.sql(')');
        toSQLEnd(context);
    }

    private final void toSQLEnd(RenderContext context) {
































        {
            context.sql(" }");
        }
    }

    private final void toSQLDeclare(RenderContext context) {






































































    }




















    private final void toSQLBegin(RenderContext context) {

























        {
            context.sql("{ ");
        }
    }



































































































































































    private final void toSQLAssign(RenderContext context) {













        {
            context.sql("? = ");
        }
    }

    private final void toSQLCall(RenderContext context) {





        {
            context.sql("call ");
        }

        toSQLQualifiedName(context);
    }

    private final void toSQLOutParam(RenderContext ctx, Parameter<?> parameter, int index) {



















        ctx.sql('?');
    }

    private final void toSQLInParam(RenderContext ctx, Parameter<?> parameter, int index, Field<?> value) {



















        ctx.visit(value);
    }

    private final void toSQLQualifiedName(Context<?> ctx) {
        if (ctx.qualify()) {
            Schema mapped = Tools.getMappedSchema(ctx.configuration(), getSchema());

            if (mapped != null && !"".equals(mapped.getName()))
                ctx.visit(mapped)
                   .sql('.');











        }

        ctx.literal(getName());
    }

    private final void fetchOutParameters(ExecuteContext ctx) throws SQLException {
        for (Parameter<?> parameter : getParameters())
            if (resultParameter(parameter))
                try {
                    fetchOutParameter(ctx, parameter);
                }
                catch (SQLException e) {

                    // [#6413] There may be prior exceptions that weren't propagated, in case of which
                    //         we should ignore any exceptions arising from fetching OUT parameters
                    if (ctx.settings().getThrowExceptions() != THROW_NONE)
                        throw e;
                }
    }

    private final <U> void fetchOutParameter(ExecuteContext ctx, Parameter<U> parameter) throws SQLException {










        {
            DefaultBindingGetStatementContext<U> out = new DefaultBindingGetStatementContext<>(
                ctx.configuration(),
                ctx.data(),
                (CallableStatement) ctx.statement(),
                resultIndexes.get(parameter)
            );

            parameter.getBinding().get(out);
            outValues.put(parameter, out.value());
        }
    }

    private final void registerOutParameters(ExecuteContext ctx) throws SQLException {
        Configuration c = ctx.configuration();
        Map<Object, Object> data = ctx.data();
        CallableStatement statement = (CallableStatement) ctx.statement();

        // Register all out / inout parameters according to their position
        // Note that some RDBMS do not support binding by name very well
        for (Parameter<?> parameter : getParameters())
            if (resultParameter(parameter))
                registerOutParameter(c, data, statement, parameter);
    }

    private final <U> void registerOutParameter(Configuration c, Map<Object, Object> data, CallableStatement statement, Parameter<U> parameter) throws SQLException {






        parameter.getBinding().register(new DefaultBindingRegisterContext<>(c, data, statement, resultIndexes.get(parameter)));
    }

    // ------------------------------------------------------------------------
    // Fetch routine results
    // ------------------------------------------------------------------------

    @Override
    public final T getReturnValue() {
        if (returnParameter != null)
            return getValue(returnParameter);

        return null;
    }

    @Override
    public final Results getResults() {
        return results;
    }

    @Override
    public final <Z> Z getValue(Parameter<Z> parameter) {
        return get(parameter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <Z> Z get(Parameter<Z> parameter) {
        return (Z) outValues.get(parameter);
    }

    protected final Map<Parameter<?>, Field<?>> getInValues() {
        return inValues;
    }

    // ------------------------------------------------------------------------
    // Access to routine configuration objects
    // ------------------------------------------------------------------------

    @Override
    public final List<Parameter<?>> getOutParameters() {
        return Collections.unmodifiableList(outParameters);
    }

    @Override
    public final List<Parameter<?>> getInParameters() {
        return Collections.unmodifiableList(inParameters);
    }

    @Override
    public final List<Parameter<?>> getParameters() {
        return Collections.unmodifiableList(allParameters);
    }

    @Override
    public final Catalog getCatalog() {
        return getSchema() == null ? null : getSchema().getCatalog();
    }

    @Override
    public final Schema getSchema() {
        return schema;
    }









    @Override
    public final Parameter<T> getReturnParameter() {
        return returnParameter;
    }

    protected final void setOverloaded(boolean overloaded) {
        this.overloaded = overloaded;
    }

    protected final boolean isOverloaded() {
        return overloaded;
    }

    private final boolean pgArgNeedsCasting(Parameter<?> parameter) {
        // [#5264] Overloaded methods always need casting for overload resolution
        //         Some data types also need casting because expressions are automatically promoted to a "higher" type
        return isOverloaded() || parameter.getType() == Byte.class || parameter.getType() == Short.class;
    }







































    private final boolean hasUnnamedParameters() {
        return hasUnnamedParameters;
    }

    private final void addParameter(Parameter<?> parameter) {
        allParameters.add(parameter);
        hasUnnamedParameters |= parameter.isUnnamed();






    }






































    private final boolean resultParameter(Parameter<?> parameter) {
        return parameter.equals(getReturnParameter()) || getOutParameters().contains(parameter);
    }

    protected final void addInParameter(Parameter<?> parameter) {
        addParameter(parameter);
        inParameters.add(parameter);

        // IN parameters are initialised with null by default
        inValues.put(parameter, val(null, parameter.getDataType()));

        // [#1183] [#3533] defaulted parameters are marked as such
        if (parameter.isDefaulted())
            inValuesDefaulted.add(parameter);
        else
            inValuesNonDefaulted.add(parameter);
    }


    protected final void addInOutParameter(Parameter<?> parameter) {
        addInParameter(parameter);
        outParameters.add(parameter);
    }

    protected final void addOutParameter(Parameter<?> parameter) {
        addParameter(parameter);
        outParameters.add(parameter);
    }

    protected final void setReturnParameter(Parameter<T> parameter) {
        addParameter(parameter);
        returnParameter = parameter;
    }









    public final Field<T> asField() {
        if (function == null) {
            function = new RoutineField();
        }

        return function;
    }

    public final Field<T> asField(String alias) {
        return asField().as(alias);
    }

    public final AggregateFunction<T> asAggregateFunction() {
        Field<?>[] array = new Field<?>[getInParameters().size()];

        int i = 0;
        for (Parameter<?> p : getInParameters()) {
            array[i] = getInValues().get(p);
            i++;
        }

        // [#2393] Fully qualify custom aggregate functions.
        return new DefaultAggregateFunction<>(false, getQualifiedName(), type, array);
    }

    /**
     * Subclasses may call this method to create {@link UDTField} objects that
     * are linked to this table.
     *
     * @param name The name of the field (case-sensitive!)
     * @param type The data type of the field
     *
     * @deprecated - Please, re-generate your routine code.
     */
    @Deprecated
    protected static final <T> Parameter<T> createParameter(String name, DataType<T> type) {
        return createParameter(name, type, false, null, null);
    }

    /**
     * Subclasses may call this method to create {@link UDTField} objects that
     * are linked to this table.
     *
     * @param name The name of the field (case-sensitive!)
     * @param type The data type of the field
     * @param isDefaulted Whether the parameter is defaulted (see
     *            {@link Parameter#isDefaulted()}
     *
     * @deprecated - Please, re-generate your routine code.
     */
    @Deprecated
    protected static final <T> Parameter<T> createParameter(String name, DataType<T> type, boolean isDefaulted) {
        return createParameter(name, type, isDefaulted, null, null);
    }

    /**
     * Subclasses may call this method to create {@link UDTField} objects that
     * are linked to this table.
     *
     * @param name The name of the field (case-sensitive!)
     * @param type The data type of the field
     * @param isDefaulted Whether the parameter is defaulted (see
     *            {@link Parameter#isDefaulted()}
     *
     * @deprecated - Please, re-generate your routine code.
     */
    @Deprecated
    protected static final <T, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, Converter<T, U> converter) {
        return createParameter(name, type, isDefaulted, converter, null);
    }

    /**
     * Subclasses may call this method to create {@link UDTField} objects that
     * are linked to this table.
     *
     * @param name The name of the field (case-sensitive!)
     * @param type The data type of the field
     * @param isDefaulted Whether the parameter is defaulted (see
     *            {@link Parameter#isDefaulted()}
     *
     * @deprecated - Please, re-generate your routine code.
     */
    @Deprecated
    protected static final <T, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, Binding<T, U> binding) {
        return createParameter(name, type, isDefaulted, null, binding);
    }

    /**
     * Subclasses may call this method to create {@link UDTField} objects that
     * are linked to this table.
     *
     * @param name The name of the field (case-sensitive!)
     * @param type The data type of the field
     * @param isDefaulted Whether the parameter is defaulted (see
     *            {@link Parameter#isDefaulted()}
     *
     * @deprecated - Please, re-generate your routine code.
     */
    @Deprecated
    protected static final <T, X, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, Converter<X, U> converter, Binding<T, X> binding) {
        return createParameter(name, type, isDefaulted, false, converter, binding);
    }

    /**
     * Subclasses may call this method to create {@link UDTField} objects that
     * are linked to this table.
     *
     * @param name The name of the field (case-sensitive!)
     * @param type The data type of the field
     * @param isDefaulted Whether the parameter is defaulted (see
     *            {@link Parameter#isDefaulted()}
     * @param isUnnamed Whether the parameter is unnamed (see
     *            {@link Parameter#isUnnamed()}.
     *
     * @deprecated - Please, re-generate your routine code.
     */
    @Deprecated
    protected static final <T> Parameter<T> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed) {
        return createParameter(name, type, isDefaulted, isUnnamed, null, null);
    }

    /**
     * Subclasses may call this method to create {@link UDTField} objects that
     * are linked to this table.
     *
     * @param name The name of the field (case-sensitive!)
     * @param type The data type of the field
     * @param isDefaulted Whether the parameter is defaulted (see
     *            {@link Parameter#isDefaulted()}
     * @param isUnnamed Whether the parameter is unnamed (see
     *            {@link Parameter#isUnnamed()}.
     *
     * @deprecated - Please, re-generate your routine code.
     */
    @Deprecated
    protected static final <T, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed, Converter<T, U> converter) {
        return createParameter(name, type, isDefaulted, isUnnamed, converter, null);
    }

    /**
     * Subclasses may call this method to create {@link UDTField} objects that
     * are linked to this table.
     *
     * @param name The name of the field (case-sensitive!)
     * @param type The data type of the field
     * @param isDefaulted Whether the parameter is defaulted (see
     *            {@link Parameter#isDefaulted()}
     * @param isUnnamed Whether the parameter is unnamed (see
     *            {@link Parameter#isUnnamed()}.
     *
     * @deprecated - Please, re-generate your routine code.
     */
    @Deprecated
    protected static final <T, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed, Binding<T, U> binding) {
        return createParameter(name, type, isDefaulted, isUnnamed, null, binding);
    }

    /**
     * Subclasses may call this method to create {@link UDTField} objects that
     * are linked to this table.
     *
     * @param name The name of the field (case-sensitive!)
     * @param type The data type of the field
     * @param isDefaulted Whether the parameter is defaulted (see
     *            {@link Parameter#isDefaulted()}
     * @param isUnnamed Whether the parameter is unnamed (see
     *            {@link Parameter#isUnnamed()}.
     *
     * @deprecated - Please, re-generate your routine code.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    protected static final <T, X, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed, Converter<X, U> converter, Binding<T, X> binding) {
        final Binding<T, U> actualBinding = DefaultBinding.newBinding(converter, type, binding);
        final DataType<U> actualType = converter == null && binding == null
            ? (DataType<U>) type
            : type.asConvertedDataType(actualBinding);

        return new ParameterImpl<>(name, actualType, actualBinding, isDefaulted, isUnnamed);
    }

    /**
     * The {@link Field} representation of this {@link Routine}
     */
    private class RoutineField extends AbstractField<T> {

        /**
         * Generated UID
         */
        private static final long serialVersionUID = -5730297947647252624L;

        @SuppressWarnings("unchecked")
        RoutineField() {

            // [#5997] TODO Retain quotation flag
            super(DSL.name(AbstractRoutine.this.getName()),
                  AbstractRoutine.this.type == null

                  // [#4254] PostgreSQL may have stored functions that don't
                  // declare an explicit return type. Those function's return
                  // type is in fact a RECORD type, consisting of OUT paramterers
                  ? (DataType<T>) SQLDataType.RESULT
                  : AbstractRoutine.this.type);
        }


















































































        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public void accept(Context<?> ctx) {
            SQLDialect family = ctx.family();

            String name;
            DataType<?> returnType;
            List<Field<?>> fields = new ArrayList<>(getInParameters().size());






            returnType = getDataType();



















            {
                RenderContext local = create(ctx).renderContext();
                toSQLQualifiedName(local);
                name = local.render();
            }

            for (Parameter<?> parameter : getInParameters()) {

                // [#1183] [#3533] Skip defaulted parameters
                if (inValuesDefaulted.contains(parameter))
                    continue;

                // Disambiguate overloaded function signatures
                if ( family == POSTGRES)

                    // [#4920] In case there are any unnamed parameters, we mustn't
                    if (hasUnnamedParameters())
                        if (pgArgNeedsCasting(parameter))
                            fields.add(new Cast(getInValues().get(parameter), parameter.getDataType()));
                        else
                            fields.add(getInValues().get(parameter));
                    else
                        if (pgArgNeedsCasting(parameter))
                            fields.add(DSL.field("{0} := {1}", name(parameter.getName()), new Cast(getInValues().get(parameter), parameter.getDataType())));
                        else
                            fields.add(DSL.field("{0} := {1}", name(parameter.getName()), getInValues().get(parameter)));














                else
                    fields.add(getInValues().get(parameter));
            }

            Field<?> result = function(name, returnType, fields.toArray(EMPTY_FIELD));

            // [#3592] Decrease SQL -> PL/SQL context switches with Oracle Scalar Subquery Caching
            if (TRUE.equals(settings(ctx.configuration()).isRenderScalarSubqueriesForStoredFunctions()))
                result = DSL.select(result).asField();

            ctx.visit(result);
        }





























    }
}
