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

import java.io.IOException;

/**
 * A <tt>RecordReader</tt> is used to divide an input stream into records.
 * The Java class used to represent a <i>record</i> is implementation specific and
 * dependent on the format of the input stream.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public interface RecordReader {

    /**
     * Reads a single record from this input stream.  The type of object
     * returned depends on the format of the stream.
     * @return the record value, or null if the end of the stream was reached.
     * @throws IOException if an I/O error occurs reading from the stream
     * @throws RecordIOException if the record is malformed and cannot
     * 	  be parsed, but subsequent reads may still be possible
     */
    public Object read() throws IOException, RecordIOException;

    /**
     * Closes this input stream.
     * @throws IOException if an I/O error occurs closing the stream
     */
    public void close() throws IOException;

    /**
     * Returns the line number of the last record from this input stream.  If a
     * record spans multiple lines, the line number at the beginning of the
     * record is returned.  May return -1 if the end of the stream was reached,
     * or 0 if new lines are not used to terminate records.
     * @return the beginning line number of the last record read
     */
    public int getRecordLineNumber();

    /**
     * Returns the unparsed record text of the last record read.
     * @return the unparsed text of the last record read
     */
    public String getRecordText();

}
