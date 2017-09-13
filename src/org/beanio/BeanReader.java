/*
 * Copyright 2010-2013 Kevin Seim
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
package org.beanio;

import java.io.IOException;

import org.beanio.internal.util.Debuggable;

/**
 * Interface for unmarshalling bean objects from an input stream.
 * 
 * <p>A <tt>BeanReader</tt> is created using a {@link StreamFactory} and a mapping file.</p>
 * 
 * @author Kevin Seim
 * @since 1.0
 * @see StreamFactory
 */
public interface BeanReader extends Debuggable, AutoCloseable {

	/**
	 * Reads a single bean from the input stream.  If the end of the stream is
	 * reached, null is returned.
	 * @return the Java bean read, or null if the end of the stream was reached
	 * @throws BeanReaderIOException if the underlying input stream throws an
	 *   {@link IOException} or this reader was closed
	 * @throws MalformedRecordException if the underlying input stream is malformed
	 *   and the record could not be accurately read
	 * @throws UnidentifiedRecordException if the record type could not be identified
	 * @throws UnexpectedRecordException if the record type is out of sequence
	 * @throws InvalidRecordException if the record was identified and failed record
	 *   or field level validations (including field type conversion errors)
	 */
	public Object read() throws BeanReaderIOException, MalformedRecordException,
		UnidentifiedRecordException, UnexpectedRecordException, InvalidRecordException;
	
	/**
	 * Skips ahead in the input stream.  Record validation errors are ignored, but
	 * a malformed record, unidentified record, or record out of sequence,
	 * will cause an exception that halts stream reading.  Exceptions thrown by this
	 * method are not passed to the error handler.
	 * @param count the number of bean objects to skip over that would have been returned
	 *   by calling {@link #read()}
	 * @return the number of skipped bean objects, which may be less than <tt>count</tt>
	 *   if the end of the stream was reached
     * @throws BeanReaderIOException if the underlying input stream throws an
     *   {@link IOException} or this reader was closed
     * @throws MalformedRecordException if the underlying input stream is malformed
     *   and a record could not be accurately skipped
     * @throws UnidentifiedRecordException if a record could not be identified
     * @throws UnexpectedRecordException if a record is out of sequence
     * @since 1.2
	 */
	public int skip(int count) throws BeanReaderIOException, MalformedRecordException,
        UnidentifiedRecordException, UnexpectedRecordException;
	
	/**
	 * Returns the record or group name of the most recent bean object
	 * read from this reader, or null if the end of the stream was reached.
	 * @return the record or group name
	 */
	public String getRecordName();
	
	/**
	 * Returns the starting line number of the first record for the most recent bean
	 * object read from this reader, or -1 when the end of the stream is reached.  
	 * The line number may be zero if new lines are not used to separate characters.  
	 * @return the line number
	 */
	public int getLineNumber();
	
	/**
	 * Returns the number of records read from the underlying input stream for the
	 * most recent bean object read from this reader.  This typically returns 1
	 * unless a bean object was mapped to a record group which may span
	 * multiple records.
	 * @return the record count
	 * @since 2.0
	 */
	public int getRecordCount();
	
	/**
	 * Returns record information for the most recent bean object read from this reader.
	 * If a bean object can span multiple records, {@link #getRecordCount()} can be used
	 * to determine how many records were read from the stream.
	 * @param index the index of the record, starting at 0
	 * @return the {@link RecordContext}
	 * @throws IndexOutOfBoundsException if there is no record for the given index
	 * @see #getRecordCount()
	 * @since 2.0
	 */
	public RecordContext getRecordContext(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Closes the underlying input stream.
	 * @throws BeanReaderIOException if the underlying input stream throws an
     *   {@link IOException} or this reader was already closed
	 */
    @Override
	public void close() throws BeanReaderIOException;

    /**
     * Sets the error handler to handle exceptions thrown by {@link #read()}.
     * @param errorHandler the {@link BeanReaderErrorHandler}
     */
    public void setErrorHandler(BeanReaderErrorHandler errorHandler);
}
