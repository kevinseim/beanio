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
package org.beanio.internal.util;

import java.math.*;
import java.net.URL;
import java.util.*;

/**
 * Utility class for working with Java types supported by BeanIO.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class TypeUtil {

    /** 
     * Alias for a <tt>java.util.Date</tt> that includes both date and time information.
     * If a type handler is registered using this alias, the registered type handler will
     * become the default type handler for all <tt>Date</tt> classes.
     */
    public static final String DATETIME_ALIAS = "datetime";
    /** Alias for the <tt>java.util.Date</tt> class that includes only date information */
    public static final String DATE_ALIAS = "date";
    /** Alias for a <tt>java.util.Date</tt> that includes only time information */
    public static final String TIME_ALIAS = "time";
    /** 
     * Alias for a <tt>java.util.Calendar</tt> that includes both date and time information. 
     * If a type handler is registered using this alias, the registered type handler will
     * become the default type handler for all {@link Calendar} classes.
     */
    public static final String CALENDAR_DATETIME_ALIAS = "calendar-datetime";
    /** Alias for a <tt>java.util.Calendar</tt> that includes only time information */
    public static final String CALENDAR_TIME_ALIAS = "calendar-time";
    /** Alias for a <tt>java.util.Calendar</tt> that includes only date information */
    public static final String CALENDAR_DATE_ALIAS = "calendar-date";
    
    /** Class type used to indicate a Java array */
    public static final Class<? extends Collection<Object>> ARRAY_TYPE = ArrayCollection.class;
    
    /**
     * Cannot instantiate.
     */
    private TypeUtil() { }
    
    /**
     * Returns <tt>true</tt> if <tt>to.isAssignableFrom(from)</tt> after converting
     * primitive values of <tt>to</tt> to its object counterpart.
     * @param to the class or primitive to test assignability to
     * @param from the class to test assignability from
     * @return <tt>true</tt> if <tt>to</tt> is assignable from <tt>from</tt>
     */
    public static boolean isAssignable(Class<?> to, Class<?> from) {
        return toWrapperClass(to).isAssignableFrom(from);
    }
    
    /**
     * Converts primitive types to their wrapper counterparts. 
     * @param type the class type to convert
     * @return the wrapper equivalent for the primitive type, or if <tt>type</tt>
     *   was not a primitive, its returned as is
     */
    public static Class<?> toWrapperClass(Class<?> type) {
        if (!type.isPrimitive())
            return type;
        else if (int.class.equals(type))
            return Integer.class;
        else if (double.class.equals(type))
            return Double.class;
        else if (char.class.equals(type))
            return Character.class;
        else if (boolean.class.equals(type))
            return Boolean.class;
        else if (long.class.equals(type))
            return Long.class;
        else if (float.class.equals(type))
            return Float.class;
        else if (short.class.equals(type))
            return Short.class;
        else if (byte.class.equals(type))
            return Byte.class;
        else
            throw new IllegalArgumentException("Primitive type not supported: " + type.getName());
    }
    
    /**
     * Returns the <tt>Class</tt> object for a class name or type alias.  A type alias is not
     * case sensitive.  The following type aliases are supported:
     * <table summary="">
     * <tr><th>Alias</th><th>Class or Primitive</th></tr>
     * <tr><td>string</td><td>java.lang.String</td></tr>
     * <tr><td>boolean</td><td>java.lang.Boolean</td></tr>
     * <tr><td>byte</td><td>java.lang.Byte</td></tr>
     * <tr><td>int</td><td>java.lang.Integer</td></tr>
     * <tr><td>integer</td><td>java.lang.Integer</td></tr>
     * <tr><td>short</td><td>java.lang.Short</td></tr>
     * <tr><td>char</td><td>java.lang.Character</td></tr>
     * <tr><td>character</td><td>java.lang.Character</td></tr>
     * <tr><td>long</td><td>java.lang.Long</td></tr>
     * <tr><td>float</td><td>java.lang.Float</td></tr>
     * <tr><td>double</td><td>java.lang.Double</td></tr>
     * <tr><td>bigdecimal</td><td>java.math.BigDecimal</td></tr>
     * <tr><td>decimal</td><td>java.math.BigDecimal</td></tr>
     * <tr><td>biginteger</td><td>java.math.BigInteger</td></tr>
     * <tr><td>uuid</td><td>java.util.UUID</td></tr>
     * <tr><td>url</td><td>java.net.URL</td></tr>
     * <tr><td>date</td><td>java.util.Date</td></tr>
     * <tr><td>time</td><td>java.util.Date</td></tr>
     * <tr><td>datetime</td><td>java.util.Date</td></tr>
     * <tr><td>calendar</td><td>java.util.Calendar</td></tr>
     * </table>
     * 
     * @param classLoader the {@link ClassLoader} for loading classes
     * @param type the fully qualified class name or type alias
     * @return the class, or null if the type name is invalid
     */
    public static Class<?> toType(ClassLoader classLoader, String type) {
        if ("string".equalsIgnoreCase(type))
            return String.class;
        else if ("boolean".equalsIgnoreCase(type))
            return Boolean.class;
        else if ("byte".equalsIgnoreCase(type))
            return Byte.class;
        else if ("char".equalsIgnoreCase(type))
            return Character.class;
        else if ("character".equalsIgnoreCase(type))
            return Character.class;
        else if ("short".equalsIgnoreCase(type))
            return Short.class;
        else if ("int".equalsIgnoreCase(type))
            return Integer.class;
        else if ("Integer".equalsIgnoreCase(type))
            return Integer.class;
        else if ("long".equalsIgnoreCase(type))
            return Long.class;
        else if ("float".equalsIgnoreCase(type))
            return Float.class;
        else if ("double".equalsIgnoreCase(type))
            return Double.class;
        else if ("bigdecimal".equalsIgnoreCase(type))
            return BigDecimal.class;
        else if ("decimal".equalsIgnoreCase(type))
            return BigDecimal.class;
        else if ("biginteger".equalsIgnoreCase(type))
            return BigInteger.class;
        else if ("uuid".equalsIgnoreCase(type))
            return UUID.class;
        else if ("url".equalsIgnoreCase(type))
            return URL.class;
        else if ("calendar".equalsIgnoreCase(type) ||
            CALENDAR_DATE_ALIAS.equalsIgnoreCase(type) ||
            CALENDAR_TIME_ALIAS.equalsIgnoreCase(type) ||
            CALENDAR_DATETIME_ALIAS.equalsIgnoreCase(type))
            return Calendar.class;
        else if (
            DATE_ALIAS.equalsIgnoreCase(type) ||
            TIME_ALIAS.equalsIgnoreCase(type) ||
            DATETIME_ALIAS.equalsIgnoreCase(type))
            return Date.class;
        
        try {
            return classLoader.loadClass(type);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    /**
     * Returns <tt>true</tt> if the type alias is not used to register a
     * type handler for its associated class.
     * @param alias the type alias to check
     * @return <tt>true</tt> if the type alias is only an alias
     */
    public static boolean isAliasOnly(String alias) {
        return DATE_ALIAS.equalsIgnoreCase(alias) || 
            TIME_ALIAS.equalsIgnoreCase(alias) ||
            CALENDAR_DATE_ALIAS.equalsIgnoreCase(alias) ||
            CALENDAR_TIME_ALIAS.equalsIgnoreCase(alias);
    }
    
    /**
     * Returns the collection <tt>Class</tt> object for a collection class name or type alias.  
     * A type alias is not case sensitive.  The following collection type aliases are supported:
     * <table summary="">
     * <tr><th>Alias</th><th>Class or Primitive</th></tr>
     * <tr><td>array</td><td>Java Array</td></tr>
     * <tr><td>list</td><td>java.util.ArrayList</td></tr>
     * <tr><td>set</td><td>java.util.HashSet</td></tr>
     * </table>
     * 
     * @param type the fully qualified class name or type alias of the collection
     * @return the collection class, or {@link #ARRAY_TYPE} for array, 
     *   or <tt>null</tt> if the type name is invalid
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends Collection<Object>> toCollectionType(String type) {
        if ("array".equalsIgnoreCase(type))
            return ARRAY_TYPE;
        else if ("collection".equalsIgnoreCase(type))
            return (Class<? extends Collection<Object>>)(Class<?>) Collection.class;
        else if ("list".equalsIgnoreCase(type))
            return (Class<? extends Collection<Object>>)(Class<?>) List.class;
        else if ("set".equalsIgnoreCase(type))
            return (Class<? extends Collection<Object>>)(Class<?>) Set.class;

        try {
            Class<?> clazz = Class.forName(type);
            if (!Collection.class.isAssignableFrom(clazz)) {
                return null;
            }
            return (Class<? extends Collection<Object>>) clazz;
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
    }
    
    public static Class<?> toAggregationType(String type) {
        if ("array".equalsIgnoreCase(type))
            return ARRAY_TYPE;
        else if ("collection".equalsIgnoreCase(type))
            return Collection.class;
        else if ("list".equalsIgnoreCase(type))
            return List.class;
        else if ("set".equalsIgnoreCase(type))
            return Set.class;
        else if ("map".equalsIgnoreCase(type))
            return Map.class;
        
        try {
            Class<?> clazz = Class.forName(type);
            if (Collection.class.isAssignableFrom(clazz))
                return clazz;
            else if (Map.class.isAssignableFrom(clazz))
                return clazz;
            else
                return null;
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
    }
    
    public static Class<?> toBeanType(ClassLoader classLoader, String type) {
        // determine the bean class associated with this record
        Class<?> beanClass = null;
        if (type != null) {
            if ("map".equals(type)) {
                beanClass = LinkedHashMap.class;
            }
            else if ("list".equals(type) || "collection".equals(type)) {
                beanClass = ArrayList.class;
            }
            else if ("set".equals(type)) {
                beanClass = LinkedHashSet.class;
            }
            else {
                try {
                    beanClass = classLoader.loadClass(type);
                }
                catch (ClassNotFoundException ex) { }
            }
        }
        return beanClass;
    }
    
    private static interface ArrayCollection extends Collection<Object> { }
}
