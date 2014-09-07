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
 * Exception thrown when a record does not adhere to the expected syntax of
 * the stream format.  Subsequent calls to {@link BeanReader#read()} are not
 * likely to be successful.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class MalformedRecordException extends BeanReaderException {

    private static final long serialVersionUID = 2L;

    /**
     * Constructs a new <tt>MalformedRecordException</tt>.
     * @param context the record context for the malformed record
     * @param message the error message
     */
    public MalformedRecordException(RecordContext context, String message) {
        this(context, message, null);
    }

    /**
     * Constructs a new <tt>MalformedRecordException</tt>.
     * @param context the record context for the malformed record
     * @param message the error message
     * @param cause the root cause
     */
    public MalformedRecordException(RecordContext context, String message, Throwable cause) {
        super(message, cause);
        setRecordContext(context);
    }
}
