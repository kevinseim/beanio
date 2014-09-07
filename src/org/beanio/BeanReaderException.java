/*
 * Copyright 2010-2012 Kevin Seim
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

/**
 * Exception thrown by a {@link BeanReader} or {@link Unmarshaller}.
 * 
 * <p>In most cases, a subclass of this exception is thrown.  In a few (but rare) fatal cases, 
 * this exception may be thrown directly.
 * 
 * @author Kevin Seim
 * @since 1.0
 * @see BeanReader
 */
public class BeanReaderException extends BeanIOException {

    private static final long serialVersionUID = 2L;

    private RecordContext[] recordContext;
    
    /**
     * Constructs a new <tt>BeanReaderException</tt>.
     * @param message the error message
     */
    public BeanReaderException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new <tt>BeanReaderException</tt>.
     * @param message the error message
     * @param cause the root cause
     */
    public BeanReaderException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Returns the number of unmarshalled records with context information
     * available if {@link #getRecordContext(int)} is called.
     * @return the unmarshalled record count
     */
    public int getRecordCount() {
        return recordContext == null ? 0 : recordContext.length;
    }
    
    /**
     * Returns the record context that caused the error.  May be null if there is no
     * context information associated with the exception.  If there is more than one
     * record context, this method returns the context of the first record and is
     * equivalent to calling getRecordContext(0).
     * @return the {@link RecordContext}
     * @since 2.0
     * @deprecated use {@link #getRecordContext()}
     */
    public RecordContext getContext() {
        return getRecordContext();
    }
    
    /**
     * Returns the record context that caused the error.  May be null if there is no
     * context information associated with the exception.  If there is more than one
     * record context, this method returns the context of the first record and is
     * equivalent to calling getRecordContext(0).
     * @return the {@link RecordContext}
     * @since 2.0
     * @see #getRecordContext(int)
     */
    public RecordContext getRecordContext() {
        return getRecordCount() > 0 ? getRecordContext(0) : null;
    }

    /**
     * Returns the record context for a given record index.  The first record uses index 0.
     * @param index the record index
     * @return the {@link RecordContext}
     * @throws IndexOutOfBoundsException if there is no record for the given index
     * @since 2.0
     * @see #getRecordCount() 
     */
    public RecordContext getRecordContext(int index) throws IndexOutOfBoundsException {
        return recordContext[index];
    }
    
    /**
     * Sets the record context that caused the exception.  This method simply wraps
     * the supplied context in an array and invokes {@link #setRecordContext(RecordContext[])}.
     * @param recordContext the {@link RecordContext}
     * @since 2.0
     */
    protected void setRecordContext(RecordContext recordContext) {
        setRecordContext(new RecordContext[] { recordContext });
    }
    
    /**
     * Sets the record context(s) that caused the exception.
     * @param recordContext the array of {@link RecordContext}
     * @since 2.0
     */
    protected void setRecordContext(RecordContext[] recordContext) {
        this.recordContext = recordContext;
    }
}
