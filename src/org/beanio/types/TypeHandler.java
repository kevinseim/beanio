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
package org.beanio.types;

/**
 * A <tt>TypeHandler</tt> is used to convert field text into a Java object and vice versa.
 * <p>
 * Implementations should be thread-safe if multiple threads may concurrently process the 
 * same stream type.  All included BeanIO type handlers are thread safe.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public interface TypeHandler {

    /** 
     * This constant can be returned from {@link #format(Object)} for XML formatted streams to indicate
     * a nillable element should be set to nil even if the field's minimum occurrences is zero.  
     * In all other cases, if NIL is returned, the formatted value is treated as <tt>null</tt>. 
     */
    public final static String NIL = new String("");
    
    /**
     * Parses field text into a Java object.
     * @param text the field text to parse, which may be null if the field was not passed in the record
     * @return the parsed Java object
     * @throws TypeConversionException if the text cannot be parsed
     */
    public Object parse(String text) throws TypeConversionException;

    /**
     * Formats a Java object into field text.
     * @param value the Java object to format, which may be null
     * @return the formatted field text, or <tt>null</tt> to indicate the value is not present, 
     *   or {@link #NIL} for XML formatted streams
     */
    public String format(Object value);

    /**
     * Returns the class type supported by this handler.  Primitive types should not be
     * returned by this method- use the object equivalent instead.
     * @return the class type supported by this handler
     */
    public Class<?> getType();
}
