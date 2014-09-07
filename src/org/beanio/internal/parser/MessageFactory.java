/*
 * Copyright 2010 Kevin Seim
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
package org.beanio.internal.parser;

/**
 * A <tt>MessageFactory</tt> implementation is used to generate localized error
 * messages for record and field level errors.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public interface MessageFactory {

    /**
     * Returns the localized label for a record.
     * @param recordName the name of the record
     * @return the record label, or <tt>null</tt> if no label was found
     */
    public String getRecordLabel(String recordName);
    
    /**
     * Returns the localized label for a field.
     * @param recordName the name of the record the field belongs to
     * @param fieldName the name of the field
     * @return the field label, or <tt>null</tt> if no label was found
     */
    public String getFieldLabel(String recordName, String fieldName);
    
    /**
     * Returns a field level error message.
     * @param recordName the name of the record
     * @param fieldName the name of the field
     * @param rule the name of the validation rule
     * @return the error message, or <tt>null</tt> if no message was configured
     */
    public String getFieldErrorMessage(String recordName, String fieldName, String rule);
    
    /**
     * Returns a record level error message.
     * @param recordName the name of the record
     * @param rule the name of the validation rule
     * @return the error message, or <tt>null</tt> if no message was configured
     */
    public String getRecordErrorMessage(String recordName, String rule);
    
}
