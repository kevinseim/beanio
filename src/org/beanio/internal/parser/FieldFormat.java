/*
 * Copyright 2011-2012 Kevin Seim
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
 * A <tt>FieldFormat</tt> provides format specific processing for a {@link Field} parser.
 * 
 * <p>Implementations of this interface must be thread-safe.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface FieldFormat {

    /**
     * Extracts the field text from a record.  Returns <tt>null</tt> if the
     * field was not present in the record.
     * 
     * <p>May return {@link Value#INVALID} if the field is invalid, or {@link Value#NIL}
     * if the field is explicitly set to nil or null such as in an XML or JSON formatted
     * stream.</p>
     * 
     * <p>Implementations should also remove any field padding before returning the text.</p>
     * 
     * @param context the {@link UnmarshallingContext} holding the record
     * @return the field text
     */
    public String extract(UnmarshallingContext context, boolean reportErrors);

    /**
     * Inserts a value into a record.  This method is called before type conversion.
     * If the method returns false, type conversion is invoked and {@link #insertField(MarshallingContext, String)}
     * is called.  If the method returns true, {@link #insertField(MarshallingContext, String)}
     * is not invoked.
     * @param context the {@link MarshallingContext}
     * @param value the value to insert into the record
     * @return true if type conversion is required and {@link #insertField(MarshallingContext, String)}
     *   must be invoked, false otherwise
     */
    public boolean insertValue(MarshallingContext context, Object value);
    
    /**
     * Inserts field text into a record.
     * @param context the {@link MarshallingContext} holding the record
     * @param text the field text to insert into the record
     */
    public void insertField(MarshallingContext context, String text);
    
    /**
     * Returns the size of the field.  Fixed length formats should return the 
     * field length, while other formats should simply return 1.
     * @return the size of the field
     */
    public int getSize();
    
    /**
     * Returns whether this field is nillable.
     * @return true if nillable, false otherwise
     */
    public boolean isNillable();
    
    /**
     * TODO rename isLazy to something better??
     * Returns whether this field is optionally present in the record.
     * @return true if lazy, false otherwise
     */
    public boolean isLazy();
    
}
