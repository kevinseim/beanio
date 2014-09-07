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
 * Provides information about a record parsed by a {@link BeanReader} or {@link Unmarshaller}.  
 * 
 * <p>Depending on the current state of the <tt>BeanReader</tt> or <tt>Unmarshaller</tt>, some 
 * information may not be available.</p>
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface RecordContext {

    /**
     * Returns the line number of this record, or 0 if the stream does not use
     * new lines to terminate records.
     * @return the line number of this record
     */
    public int getLineNumber();

    /**
     * Returns the line number of this record, or 0 if the stream does not use
     * new lines to terminate records.
     * @return the line number of this record
     * @deprecated use {@link #getLineNumber()}
     */
    public int getRecordLineNumber();
    
    /**
     * Returns the raw text of the parsed record.  Record text is not supported
     * by XML stream formats, and <tt>null</tt> is returned instead.
     * @return the raw text of the parser record
     */
    public String getRecordText();

    /**
     * Returns the name of the record from the stream configuration.  The record name
     * may be null if was not determined before an exception was thrown.
     * @return the name of the record from the stream configuration
     */
    public String getRecordName();

    /**
     * Returns whether this record has any record or field level errors.
     * @return true if this record has any errors, false otherwise
     */
    public boolean hasErrors();
    
    /**
     * Returns whether there are one or more record level errors.
     * @return true if there are one or more record level error, false otherwise
     */
    public boolean hasRecordErrors();

    /**
     * Returns a collection of record level error messages.
     * @return the {@link Collection} of record level error messages
     */
    public Collection<String> getRecordErrors();

    /**
     * Returns the number of times the given field was present in the stream.
     * @param fieldName the name of the field
     * @return the number of times the field was present in the stream
     */
    public int getFieldCount(String fieldName);
    
    /**
     * Returns the raw text of a field found in this record.  Field text may be null
     * under the following circumstances:
     * <ul>
     * <li>A record level exception was thrown before a field was parsed</li>
     * <li><tt>fieldName</tt> was not declared in the mapping file</li>
     * <li>The field was not present in the record</li>
     * </ul>
     * <p>If the field repeats in the stream, this method returns the field text for
     * the first occurrence of the field.</p>
     * @param fieldName the name of the field
     * @return the unparsed field text
     */
    public String getFieldText(String fieldName);

    /**
     * Returns the raw text of a field found in this record.  Field text may be null
     * under the following circumstances:
     * <ul>
     * <li>A record level exception was thrown before a field was parsed</li>
     * <li><tt>fieldName</tt> was not declared in the mapping file</li>
     * <li>The field was not present in the record</li>
     * </ul>
     * @param fieldName the name of the field to get the text for
     * @param index the index of the field (beginning at 0), for repeating fields
     * @return the unparsed field text
     */
    public String getFieldText(String fieldName, int index);
    
    /**
     * Returns whether there are one or more field level errors.
     * @return <tt>true</tt> if there are one or more field level errors, false otherwise
     */
    public boolean hasFieldErrors();

    /**
     * Returns a {@link Map} of all field errors.  The name of the field is used for the
     * <tt>Map</tt> key, and the value is a {@link Collection} of field error messages.
     * @return a {@link Map} of all field errors
     */
    public Map<String, Collection<String>> getFieldErrors();

    /**
     * Returns the field errors for a given field.
     * @param fieldName the name of the field
     * @return the {@link Collection} of field errors, or null if no errors were
     *   reported for the field
     */
    public Collection<String> getFieldErrors(String fieldName);

}
