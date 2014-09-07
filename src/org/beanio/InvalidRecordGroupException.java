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
package org.beanio;

import java.util.*;

/**
 * Exception thrown when one or more records fail validation while unmarshalling
 * a record group.
 * 
 * <p>The {@link #getRecordName()} method will return the name of the group (from
 * the mapping file) that failed validation.
 * 
 * <p>An invalid record group does not affect the state of a {@link BeanReader}, and
 * subsequent calls to <tt>read()</tt> are not affected.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class InvalidRecordGroupException extends InvalidRecordException {

    private static final long serialVersionUID = 1L;
    
    private String groupName;
    
    /**
     * Constructs a new <tt>InvalidRecordGroupException</tt>.
     * @param context one or more record contexts that make up the group
     * @param message the error message
     * @param groupName the group name
     */
    public InvalidRecordGroupException(RecordContext[] context, String message, String groupName) {
        super(message);
        this.groupName = groupName;
        setRecordContext(context);
    }

    @Override
    public String getRecordName() {
        return groupName;
    }
    
    @Override
    protected void appendMessageDetails(StringBuilder s) {
        for (int i=0, j=getRecordCount(); i<j; i++) {
            RecordContext context = getRecordContext(i);
            if (!context.hasErrors()) {
                continue;
            }
        
            s.append("\n ==> Invalid '")
                .append(context.getRecordName())
                .append("' record at line ")
                .append(context.getLineNumber());
            
            if (context.hasRecordErrors()) {
                for (String error : context.getRecordErrors()) {
                    s.append("\n     - ");
                    s.append(error);
                }
            }
            if (context.hasFieldErrors()) {
                for (Map.Entry<String, Collection<String>> entry : context.getFieldErrors().entrySet()) {
                    String fieldName = entry.getKey();
                    for (String error : entry.getValue()) {
                        s.append("\n     - Invalid '");
                        s.append(fieldName);
                        s.append("':  ");
                        s.append(error);
                    }
                }
            }
        }
    }
}
