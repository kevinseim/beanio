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
 * The <tt>Property</tt> interface is implemented by parser components capable
 * of storing a property value.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface Property {

    /** The simple property type that cannot hold other properties */
    public static final int SIMPLE = 0;
    /** The bean object property type with simple properties and other bean objects for attributes */
    public static final int COMPLEX = 1;
    /** The collection property type used to create a collection of other properties */
    public static final int COLLECTION = 2;
    /** The map property type */
    public static final int MAP = 3;
    /** The array property type */
    public static final int AGGREGATION_ARRAY = 4;
    /** The collection property type used to aggregate multiple occurrences of a single property */
    public static final int AGGREGATION_COLLECTION = 5;
    /** The map property type used to aggregate multiple occurrences of key/value pairs */
    public static final int AGGREGATION_MAP = 6;
    
    /**
     * Returns the property type.
     * @return {@link #SIMPLE}, {@link #COMPLEX}, {@link #AGGREGATION_ARRAY}, {@link #COLLECTION}, 
     *   {@link #AGGREGATION_COLLECTION}, or {@link #MAP}
     */
    public int type();
    
    /**
     * Returns the property name as configured in the mapping file.  The returned property 
     * name is used only for error messages and does not necessarily match the attribute name
     * of a parent bean.
     * @return the property name
     */
    public String getName();
    
    /**
     * Clears the property value.  A subsequent call to {@link #getValue(ParsingContext)} should
     * return null, or {@link Value#MISSING} for lazy property values.
     * @param context the {@link ParsingContext}
     */
    public void clearValue(ParsingContext context);
    
    /**
     * Creates the property value and returns it.
     * @param context the {@link ParsingContext}
     * @return the property value
     */
    public Object createValue(ParsingContext context);
    
    /**
     * Returns the value of this property.
     *   
     * <p>When unmarshalling, this method should return {@link Value#MISSING} if the field
     * was not present in the stream.  Or if present, but has no value, null should be returned.  
     * 
     * <p>When marshalling, this method should return {@link Value#MISSING} for any optional
     * segment bound to a bean object, or null if required.  Null field properties should 
     * always return {@link Value#MISSING}.
     * 
     * @param context  the {@link ParsingContext}
     * @return the property value,
     *   or {@link Value#MISSING} if not present in the stream,
     *   or {@link Value#INVALID} if the field was invalid
     */
    public Object getValue(ParsingContext context);
    
    /**
     * Sets the property value (before marshalling).
     * @param context the {@link ParsingContext}
     * @param value the property value
     */
    public void setValue(ParsingContext context, Object value);
    
    public boolean defines(Object value);
    
    /**
     * Returns whether this property or any of its descendants are used to 
     * identify a bean object. 
     * @return <tt>true</tt> if this property identifies a bean
     */
    public boolean isIdentifier();
    public void setIdentifier(boolean identifier);
    
    public PropertyAccessor getAccessor();
    public void setAccessor(PropertyAccessor accessor);
    
    public Class<?> getType();
    public void setType(Class<?> type);
}
