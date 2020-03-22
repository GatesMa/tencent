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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.stream.Stream;

import org.xml.sax.InputSource;

/**
 * The <code>Loader</code> API is used for configuring data loads.
 * <p>
 * The step in constructing the {@link Loader} object where you can specify the
 * load type and data source.
 *
 * @author Lukas Eder
 * @author Johannes Bühler
 */
public interface LoaderSourceStep<R extends Record> {

    /**
     * Load in-memory data.
     * <p>
     * Feed a set of array representations of records to the loader API. Each
     * array's elements are matched with the subsequent
     * {@link LoaderRowsStep#fields(Field...)} specification, by index. The
     * values in each array are converted to the matching field's
     * {@link DataType} via {@link DataType#convert(Object)}. The matching is
     * similar to that of {@link Record#fromArray(Object[], Field...)}.
     */
    @Support
    LoaderRowsStep<R> loadArrays(Object[]... arrays);

    /**
     * Load in-memory data.
     * <p>
     * Like {@link #loadArrays(Object[][])}, providing the possibility of lazy
     * materialisation of the input arrays.
     *
     * @see #loadArrays(Object[][])
     * @see Record#fromArray(Object[], Field...)
     */
    @Support
    LoaderRowsStep<R> loadArrays(Iterable<? extends Object[]> arrays);

    /**
     * Load in-memory data.
     * <p>
     * Like {@link #loadArrays(Object[][])}, providing the possibility of lazy
     * materialisation of the input arrays.
     *
     * @see #loadArrays(Object[][])
     * @see Record#fromArray(Object[], Field...)
     */
    @Support
    LoaderRowsStep<R> loadArrays(Iterator<? extends Object[]> arrays);



    /**
     * Load in-memory data.
     * <p>
     * Like {@link #loadArrays(Object[][])}, providing the possibility of lazy
     * materialisation of the input arrays.
     *
     * @see #loadArrays(Object[][])
     * @see Record#fromArray(Object[], Field...)
     */
    @Support
    LoaderRowsStep<R> loadArrays(Stream<? extends Object[]> arrays);



    /**
     * Load in-memory data.
     */
    @Support
    LoaderRowsStep<R> loadRecords(Record... records);

    /**
     * Load in-memory data.
     *
     * @see #loadRecords(Record...)
     */
    @Support
    LoaderRowsStep<R> loadRecords(Iterable<? extends Record> records);

    /**
     * Load in-memory data.
     *
     * @see #loadRecords(Record...)
     */
    @Support
    LoaderRowsStep<R> loadRecords(Iterator<? extends Record> records);



    /**
     * Load in-memory data.
     *
     * @see #loadRecords(Record...)
     */
    @Support
    LoaderRowsStep<R> loadRecords(Stream<? extends Record> records);



    /**
     * Load CSV data.
     */
    @Support
    LoaderCSVStep<R> loadCSV(File file);

    /**
     * Load CSV data.
     */
    @Support
    LoaderCSVStep<R> loadCSV(File file, String charsetName);

    /**
     * Load CSV data.
     */
    @Support
    LoaderCSVStep<R> loadCSV(File file, Charset cs);

    /**
     * Load CSV data.
     */
    @Support
    LoaderCSVStep<R> loadCSV(File file, CharsetDecoder dec);

    /**
     * Load CSV data.
     */
    @Support
    LoaderCSVStep<R> loadCSV(String data);

    /**
     * Load CSV data.
     */
    @Support
    LoaderCSVStep<R> loadCSV(InputStream stream);

    /**
     * Load CSV data.
     */
    @Support
    LoaderCSVStep<R> loadCSV(InputStream stream, String charsetName);

    /**
     * Load CSV data.
     */
    @Support
    LoaderCSVStep<R> loadCSV(InputStream stream, Charset cs);

    /**
     * Load CSV data.
     */
    @Support
    LoaderCSVStep<R> loadCSV(InputStream stream, CharsetDecoder dec);

    /**
     * Load CSV data.
     */
    @Support
    LoaderCSVStep<R> loadCSV(Reader reader);

    /**
     * Load CSV data.
     */
    @Support
    LoaderCSVStep<R> loadCSV(Source source);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(File file);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(File file, String charsetName);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(File file, Charset cs);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(File file, CharsetDecoder dec);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(String data);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(InputStream stream);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(InputStream stream, String charsetName);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(InputStream stream, Charset cs);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(InputStream stream, CharsetDecoder dec);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(Reader reader);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(InputSource source);

    /**
     * Load XML data.
     */
    @Support
    LoaderXMLStep<R> loadXML(Source source);

    /**
     * Load JSON data.
     */
    @Support
    LoaderJSONStep<R> loadJSON(File file);

    /**
     * Load JSON data.
     */
    @Support
    LoaderJSONStep<R> loadJSON(File file, String charsetName);

    /**
     * Load JSON data.
     */
    @Support
    LoaderJSONStep<R> loadJSON(File file, Charset cs);

    /**
     * Load JSON data.
     */
    @Support
    LoaderJSONStep<R> loadJSON(File file, CharsetDecoder dec);

    /**
     * Load JSON data.
     */
    @Support
    LoaderJSONStep<R> loadJSON(String data);

    /**
     * Load JSON data.
     */
    @Support
    LoaderJSONStep<R> loadJSON(InputStream stream);

    /**
     * Load JSON data.
     */
    @Support
    LoaderJSONStep<R> loadJSON(InputStream stream, String charsetName);

    /**
     * Load JSON data.
     */
    @Support
    LoaderJSONStep<R> loadJSON(InputStream stream, Charset cs);

    /**
     * Load JSON data.
     */
    @Support
    LoaderJSONStep<R> loadJSON(InputStream stream, CharsetDecoder dec);

    /**
     * Load JSON data.
     */
    @Support
    LoaderJSONStep<R> loadJSON(Reader reader);

    /**
     * Load JSON data.
     */
    @Support
    LoaderJSONStep<R> loadJSON(Source source);

}
