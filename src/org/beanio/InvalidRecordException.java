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

import java.util.*;

/**
 * Exception thrown when a record or one of its fields does not pass validation
 * during unmarshalling.
 * 
 * <p>An invalid record does not affect the state of a {@link BeanReader}, and
 * subsequent calls to <tt>read()</tt> are not affected.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class InvalidRecordException extends BeanReaderException {

    private static final long serialVersionUID = 2L;

    /**
     * Constructs a new <tt>InvalidRecordException</tt>.
     * @param message the error message
     */
    protected InvalidRecordException(String message) {
        this(null, message);
    }
    
    /**
     * Constructs a new <tt>InvalidRecordException</tt>.
     * @param context the {@link RecordContext} that caused the exception
     * @param message the error message
     */
    public InvalidRecordException(RecordContext context, String message) {
        super(message);
        setRecordContext(context);
    }
    
    /**
     * Returns the name of the record or group that failed validation.
     * @return the record or group name
     * @since 2.0
     */
    public String getRecordName() {
        RecordContext ctx = getRecordContext();
        return ctx != null ? ctx.getRecordName() : null;
    }
    
    @Override
    public String toString() {
        String message = super.toString();
        if (getRecordCount() == 0) {
            return message;
        }
        else {
            StringBuilder s = new StringBuilder(message);
            appendMessageDetails(s);
            return s.toString();
        }
    }
    
    /**
     * Called by {@link #toString()} to append record context details to the
     * error message.
     * @param s the message to append
     */
    protected void appendMessageDetails(StringBuilder s) {
        RecordContext context = getRecordContext();
        
        if (context.hasRecordErrors()) {
            for (String error : context.getRecordErrors()) {
                s.append("\n ==> ");
                s.append(error);
            }
        }
        if (context.hasFieldErrors()) {
            for (Map.Entry<String, Collection<String>> entry : context.getFieldErrors().entrySet()) {
                String fieldName = entry.getKey();
                for (String error : entry.getValue()) {
                    s.append("\n ==> Invalid '");
                    s.append(fieldName);
                    s.append("':  ");
                    s.append(error);
                }
            }
        }
    }
}
