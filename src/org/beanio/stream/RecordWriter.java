/*
 * Copyright 2010-2011 Kevin Seim
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

/**
 * A <tt>RecordWriter</tt> is used to write records to an output stream.
 * The class used to represent a <i>record</i> is implementation specific and
 * dependent on the format of the output stream.  For example, a delimited stream
 * may use <tt>String[]</tt> objects to define records, while a fixed length based
 * stream may simply use <tt>String</tt>. 
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public interface RecordWriter {

    /**
     * Writes a record object to this output stream.
     * @param record the record object to write
     * @throws IOException if an I/O error occurs writing the record to the stream
     */
    public void write(Object record) throws IOException;

    /**
     * Flushes the output stream.
     * @throws IOException if an I/O error occurs flushing the stream
     */
    public void flush() throws IOException;

    /**
     * Closes the output stream.
     * @throws IOException if an I/O error occurs closing the stream
     */
    public void close() throws IOException;
}
