/*
 * Copyright 2012 Kevin Seim
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beanio.stream;

import java.io.*;

import org.beanio.*;
import org.w3c.dom.Document;

/**
 * Factory interface for creating record parsers.
 * 
 * <p>The Java object bound to a <i>record</i> depends on the stream format.
 * The following table shows the object used for each format:</p>
 * <table summary="">
 * <tr>
 *   <th>Format</th>
 *   <th>Record Type</th>
 * </tr>
 *  <tr>
 *   <td>Fixed Length</td>
 *   <td><tt>String</tt></td>
 * </tr>
 * <tr>
 *   <td>CSV, Delimited</td>
 *   <td><tt>String[]</tt></td>
 * </tr>
 * <tr>
 *   <td>XML</td>
 *   <td>{@link Document}</td>
 * </tr>
 * </table>
 * 
 * <p>The following table shows the method invoked for a requested BeanIO interface.</p>
 * <table summary="">
 * <tr>
 *   <th>Requests For</th>
 *   <th>Invokes</th>
 * </tr>
 * <tr>
 *   <td>{@link BeanReader}</td>
 *   <td>{@link #createReader(Reader)}</td>
 * </tr>
 * <tr>
 *   <td>{@link BeanWriter}</td>
 *   <td>{@link #createWriter(Writer)}</td>
 * </tr>
 * <tr>
 *   <td>{@link Unmarshaller}</td>
 *   <td>{@link #createUnmarshaller()}</td>
 * </tr>
 * <tr>
 *   <td>{@link Marshaller}</td>
 *   <td>{@link #createMarshaller()}</td>
 * </tr>
 * </table>
 * 
 * <p>A <tt>RecordParserFactory</tt> implementation must be thread safe (after all of its properties have been set).</p>
 * 
 * @author Kevin Seim
 * @since 2.0
 * @see RecordReader
 * @see RecordWriter
 * @see RecordMarshaller
 * @see RecordUnmarshaller
 */
public interface RecordParserFactory {

    /**
     * Initializes the factory.  This method is called when a mapping file is loaded after
     * all parser properties have been set, and is therefore ideally used to preemptively
     * validate parser configuration settings.
     * @throws IllegalArgumentException if the parser configuration is invalid
     */
    public void init() throws IllegalArgumentException;
    
    /**
     * Creates a parser for reading records from an input stream.
     * @param in the input stream to read from
     * @return the created {@link RecordReader}
     * @throws IllegalArgumentException if this factory is improperly configured
     *   and a {@link RecordReader} cannot be created
     */
    public RecordReader createReader(Reader in) throws IllegalArgumentException;
    
    /**
     * Creates a parser for writing records to an output stream.
     * @param out the output stream to write to
     * @return the new {@link RecordWriter}
     * @throws IllegalArgumentException if this factory is improperly configured
     *   and a {@link RecordWriter} cannot be created
     */
    public RecordWriter createWriter(Writer out) throws IllegalArgumentException;
    
    /**
     * Creates a parser for marshalling records.
     * @return the created {@link RecordMarshaller}
     * @throws IllegalArgumentException if this factory is improperly configured and
     *   a {@link RecordMarshaller} cannot be created
     */
    public RecordMarshaller createMarshaller() throws IllegalArgumentException;

    /**
     * Creates a parser for unmarshalling records.
     * @return the created {@link RecordUnmarshaller}
     * @throws IllegalArgumentException if this factory is improperly configured and
     *   a {@link RecordUnmarshaller} cannot be created
     */
    public RecordUnmarshaller createUnmarshaller() throws IllegalArgumentException;
    
}
