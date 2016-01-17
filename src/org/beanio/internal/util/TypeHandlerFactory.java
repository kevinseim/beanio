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
import java.text.DateFormat;
import java.util.*;

import org.beanio.BeanIOConfigurationException;
import org.beanio.types.*;
import org.beanio.types.xml.*;

/**
 * A factory class used to get a <tt>TypeHandler</tt> for parsing field text 
 * into field objects, and for formatting field objects into field text.
 * <p>
 * A <tt>TypeHandler</tt> is registered and retrieved by class, type alias, or name.  If a stream
 * format is specified when registering a type handler by class or type alias, the type handler
 * will only be returned when the same format is queried for.
 * In most cases, registering a type handler by type alias has the same effect as registering the
 * type handler using the target class associated with the alias.  There are two exceptions: 
 * type handlers can be specifically registered for '<tt>date</tt>' and '<tt>time</tt>' type aliases
 * without overriding the default Date type handler, which is registered for the class 
 * <tt>java.util.Date</tt> and type alias '<tt>datetime</tt>'.
 * <p>
 * If a registered type handler implements the <tt>ConfigurableTypeHandler</tt> interface, 
 * handler properties can be overridden using a <tt>Properties</tt> object.  When the type handler
 * is retrieved, the factory calls {@link ConfigurableTypeHandler#newInstance(Properties)} to 
 * allow the type handler to return a customized version of itself.
 * <p>
 * By default, a <tt>TypeHandlerFactory</tt> holds a reference to a parent
 * factory.  If a factory cannot find a type handler, its parent will be checked
 * recursively until there is no parent left to check.
 * 
 * @author Kevin Seim
 * @since 1.0
 * @see TypeHandler
 * @see ConfigurableTypeHandler
 */
public class TypeHandlerFactory {

    private TypeHandlerFactory parent;
    private ClassLoader classLoader;
    private Map<String, TypeHandler> handlerMap = new HashMap<>();

    private static final String NAME_KEY = "name:";
    private static final String TYPE_KEY = "type:";
    
    /* The default type handler factory */
    private final static TypeHandlerFactory defaultFactory;
    static {
        defaultFactory = new TypeHandlerFactory(TypeHandlerFactory.class.getClassLoader());
        defaultFactory.registerHandlerFor(Character.class, new CharacterTypeHandler());
        defaultFactory.registerHandlerFor(String.class,  new StringTypeHandler());
        defaultFactory.registerHandlerFor(Byte.class, new ByteTypeHandler());
        defaultFactory.registerHandlerFor(Short.class, new ShortTypeHandler());
        defaultFactory.registerHandlerFor(Integer.class, new IntegerTypeHandler());
        defaultFactory.registerHandlerFor(Long.class, new LongTypeHandler());
        defaultFactory.registerHandlerFor(Float.class, new FloatTypeHandler());
        defaultFactory.registerHandlerFor(Double.class, new DoubleTypeHandler());
        defaultFactory.registerHandlerFor(BigDecimal.class, new BigDecimalTypeHandler());
        defaultFactory.registerHandlerFor(BigInteger.class, new BigIntegerTypeHandler());
        defaultFactory.registerHandlerFor(Boolean.class, new BooleanTypeHandler());
        defaultFactory.registerHandlerFor(UUID.class, new UUIDTypeHandler());
        defaultFactory.registerHandlerFor(URL.class, new URLTypeHandler());
        
        Settings settings = Settings.getInstance();
        defaultFactory.registerHandlerFor(TypeUtil.DATETIME_ALIAS, new DateTypeHandler(
            settings.getProperty(Settings.DEFAULT_DATETIME_FORMAT)));
        defaultFactory.registerHandlerFor(TypeUtil.DATE_ALIAS, new DateTypeHandler(
            settings.getProperty(Settings.DEFAULT_DATE_FORMAT)) {
            @Override
            protected DateFormat createDefaultDateFormat() {
                return DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
            }
        });
        defaultFactory.registerHandlerFor(TypeUtil.TIME_ALIAS, new DateTypeHandler(
            settings.getProperty(Settings.DEFAULT_TIME_FORMAT)) {
            @Override
            protected DateFormat createDefaultDateFormat() {
                return DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
            }
        });
        
        defaultFactory.registerHandlerFor(TypeUtil.CALENDAR_DATETIME_ALIAS, new CalendarTypeHandler(
            settings.getProperty(Settings.DEFAULT_DATETIME_FORMAT)));
        defaultFactory.registerHandlerFor(TypeUtil.CALENDAR_DATE_ALIAS, new CalendarTypeHandler(
            settings.getProperty(Settings.DEFAULT_DATE_FORMAT)) {
            @Override
            protected DateFormat createDefaultDateFormat() {
                return DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
            }
        });
        defaultFactory.registerHandlerFor(TypeUtil.CALENDAR_TIME_ALIAS, new CalendarTypeHandler(
            settings.getProperty(Settings.DEFAULT_TIME_FORMAT)) {
            @Override
            protected DateFormat createDefaultDateFormat() {
                return DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
            }
        });
        
        // xml specific formats...
        defaultFactory.registerHandlerFor(TypeUtil.DATE_ALIAS, new XmlDateTypeHandler(), "xml");
        defaultFactory.registerHandlerFor(TypeUtil.DATETIME_ALIAS, new XmlDateTimeTypeHandler(), "xml");
        defaultFactory.registerHandlerFor(TypeUtil.TIME_ALIAS, new XmlTimeTypeHandler(), "xml");
        defaultFactory.registerHandlerFor(TypeUtil.CALENDAR_DATE_ALIAS, new XmlCalendarDateTypeHandler(), "xml");
        defaultFactory.registerHandlerFor(TypeUtil.CALENDAR_DATETIME_ALIAS, new XmlCalendarDateTimeTypeHandler(), "xml");
        defaultFactory.registerHandlerFor(TypeUtil.CALENDAR_TIME_ALIAS, new XmlCalendarTimeTypeHandler(), "xml");
        defaultFactory.registerHandlerFor(Boolean.class, new XmlBooleanTypeHandler(), "xml");
    }

    /**
     * Constructs a new <tt>TypeHandlerFactory</tt> using the default type handler factory
     * for its parent and the same {@link ClassLoader} that loaded this class.
     */
    public TypeHandlerFactory() {
        this(TypeHandlerFactory.class.getClassLoader());
    }

    /**
     * Constructs a new <tt>TypeHandlerFactory</tt> using the default type handler factory
     * for its parent.
     * @param classLoader the {@link ClassLoader} for resolving unrecognized types
     * @since 2.0
     */
    public TypeHandlerFactory(ClassLoader classLoader) {
        setParent(getDefault());
        this.classLoader = classLoader;
    }
    
    /**
     * Constructs a new <tt>TypeHandlerFactory</tt>.
     * @param classLoader the {@link ClassLoader} for resolving unrecognized types
     * @param parent the parent <tt>TypeHandlerFactory</tt>
     * @since 2.0
     */
    public TypeHandlerFactory(ClassLoader classLoader, TypeHandlerFactory parent) {
        setParent(parent);
        this.classLoader = classLoader;
    }
    
    /**
     * Returns a named type handler, or <tt>null</tt> if there is no type handler configured
     * for the given name in this factory or any of its ancestors.
     * @param name the name of type handler was registered under
     * @return the type handler, or <tt>null</tt> if there is no configured type handler
     *    registered for the name
     */
    public TypeHandler getTypeHandler(String name) {
        return getTypeHandler(name, null);
    }

    /**
     * Returns a named type handler, or <tt>null</tt> if there is no type handler configured
     * for the given name in this factory or any of its ancestors.
     * @param name the name the type handler was registered under
     * @param properties the custom properties for configuring the type handler
     * @return the type handler, or <tt>null</tt> if there is no configured type handler
     *    registered for the name
     * @throws IllegalArgumentException if a custom property value was invalid
     */
    public TypeHandler getTypeHandler(String name, Properties properties) throws IllegalArgumentException {
        if (name == null) {
            throw new NullPointerException();
        }
        return getHandler(NAME_KEY + name, null, properties);
    }

    /**
     * Returns the type handler for the given type, or <tt>null</tt> if there is no type 
     * handler configured for the type in this factory or any of its ancestors.
     * @param type the class name or type alias
     * @return the type handler, or <tt>null</tt> if there is no configured type handler
     *    registered for the type
     */
    public TypeHandler getTypeHandlerFor(String type) {
        return getTypeHandlerFor(type, (String) null, (Properties) null);
    }

    /**
     * Returns the type handler for the given type and format, or <tt>null</tt> if there is no type 
     * handler configured for the type in this factory or any of its ancestors.
     * @param type the class name or type alias
     * @param format the stream format, or if null, format specific handlers will not be returned
     * @return the type handler, or <tt>null</tt> if there is no configured type handler
     *    registered for the type
     * @since 2.0
     */
    public TypeHandler getTypeHandlerFor(String type, String format) {
        return getTypeHandlerFor(type, format, (Properties) null);
    }
    
    /**
     * Returns the type handler for the given type, or <tt>null</tt> if there is no type 
     * handler configured for the type in this factory or any of its ancestors.
     * @param type the property type
     * @param format the stream format, or if null, format specific handlers will not be returned
     * @param properties the custom properties for configuring the type handler
     * @return the type handler, or <tt>null</tt> if there is no configured type handler
     *    registered for the type
     * @throws IllegalArgumentException if a custom property value was invalid
     * @since 2.0
     */
    public TypeHandler getTypeHandlerFor(String type, String format, Properties properties) throws IllegalArgumentException {
        if (type == null) {
            throw new NullPointerException();
        }
        
        if (TypeUtil.isAliasOnly(type)) {
            return getHandler(TYPE_KEY + type.toLowerCase(), format, properties);
        }
        else {
            Class<?> clazz = TypeUtil.toType(classLoader, type);
            if (clazz == null) {
                return null;
            }
            return getTypeHandlerFor(clazz, format, properties);
        }
    }

    /**
     * Returns a type handler for a class, or <tt>null</tt> if there is no type 
     * handler configured for the class in this factory or any of its ancestors
     * @param clazz the target class to find a type handler for
     * @return the type handler, or null if the class is not supported
     */
    public TypeHandler getTypeHandlerFor(Class<?> clazz) {
        return getTypeHandlerFor(clazz, null, null);
    }
    
    /**
     * Returns a type handler for a class, or <tt>null</tt> if there is no type 
     * handler configured for the class in this factory or any of its ancestors
     * @param clazz the target class to find a type handler for
     * @param format the stream format, or if null, format specific handlers will not be returned
     * @param properties the custom properties for configuring the type handler
     * @return the type handler, or null if the class is not supported
     * @throws IllegalArgumentException if a custom property value was invalid
     * @since 2.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public TypeHandler getTypeHandlerFor(Class<?> clazz, String format, Properties properties) throws IllegalArgumentException {
        if (clazz == null) {
            throw new NullPointerException();
        }
        clazz = TypeUtil.toWrapperClass(clazz);
        
        TypeHandler handler = getHandler(TYPE_KEY + clazz.getName(), format, properties);
        if (handler == null && Enum.class.isAssignableFrom(clazz)) {
            return getEnumHandler((Class<Enum>) clazz, properties);
        }
        
        return handler;
    }
    
    @SuppressWarnings("rawtypes")
    private TypeHandler getEnumHandler(Class<Enum> clazz, Properties properties) {
        String format = null;
        if (properties != null) {
            format = properties.getProperty("format");
        }
        if (format == null || "name".equals(format)) {
            return new EnumTypeHandler((Class<Enum>) clazz);
        }
        else if ("toString".equals(format)) {
            return new ToStringEnumTypeHandler((Class<Enum>) clazz);
        }
        else {
            throw new BeanIOConfigurationException("Invalid format '" + format + "', " +
                "expected 'toString' or 'name' (default)");
        }
    }

    private TypeHandler getHandler(String key, String format, Properties properties) throws IllegalArgumentException {
        TypeHandler handler = null;
        TypeHandlerFactory factory = this;
        while (factory != null) {
            // query for format specific handler first
            if (format != null) {
                handler = factory.handlerMap.get(format + "." + key);
                if (handler != null) {
                    return getHandler(handler, properties);
                }
            }
            handler = factory.handlerMap.get(key);
            if (handler != null) {
                return getHandler(handler, properties);
            }
            factory = factory.parent;
        }
        return null;
    }

    private TypeHandler getHandler(TypeHandler handler, Properties properties) throws IllegalArgumentException {
        if (properties != null && !properties.isEmpty()) {
            if (handler instanceof ConfigurableTypeHandler) {
                handler = ((ConfigurableTypeHandler) handler).newInstance(properties);
            }
            else {
                String property = properties.keys().nextElement().toString();
                throw new IllegalArgumentException("'" + property + "' setting not supported by type handler");
            }
        }
        return handler;
    }

    /**
     * Registers a type handler in this factory.
     * @param name the name to register the type handler under
     * @param handler the type handler to register
     */
    public void registerHandler(String name, TypeHandler handler) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (handler == null) {
            throw new NullPointerException();
        }
        handlerMap.put(NAME_KEY + name, handler);
    }

    /**
     * Registers a type handler in this factory by class type for all stream formats 
     * @param type the fully qualified class name or type alias to register the type handler for
     * @param handler the type handler to register
     * @throws IllegalArgumentException if the type name is invalid or if the handler type is not 
     *   assignable from the type
     */
    public void registerHandlerFor(String type, TypeHandler handler) throws IllegalArgumentException {
        registerHandlerFor(type, handler, null);
    }

    /**
     * Registers a type handler in this factory by class type for a specific stream format.
     * @param type the fully qualified class name or type alias to register the type handler for
     * @param handler the type handler to register
     * @param format the stream format to register the type handler for, or if null the type handler
     *   may be returned for any format
     * @throws IllegalArgumentException if the type name is invalid or if the handler type is not 
     *   assignable from the type
     * @since 2.0
     */
    public void registerHandlerFor(String type, TypeHandler handler, String format) throws IllegalArgumentException {
        if (type == null) {
            throw new NullPointerException();
        }
        
        Class<?> clazz = TypeUtil.toType(classLoader, type);
        if (clazz == null) {
            throw new IllegalArgumentException("Invalid type or type alias '" + type + "'");
        }
        if (TypeUtil.isAliasOnly(type)) {
            type = type.toLowerCase();
            registerHandlerFor(format, type, clazz, handler);
        }
        else {
            registerHandlerFor(format, clazz.getName(), clazz, handler);
        }
    }

    /**
     * Registers a type handler in this factory for any stream format.
     * @param clazz the target class to register the type handler for
     * @param handler the type handler to register
     * @throws IllegalArgumentException if the handler type is not assignable from
     *   the registered class type
     */
    public void registerHandlerFor(Class<?> clazz, TypeHandler handler) throws IllegalArgumentException {
        registerHandlerFor(clazz, handler, null);
    }

    /**
     * Registers a type handler in this factory for a specific stream format.
     * @param clazz the target class to register the type handler for
     * @param handler the type handler to register
     * @param format the stream format to register the type handler for, or if null the type handler
     *   may be returned for any format
     * @throws IllegalArgumentException if the handler type is not assignable from
     *   the registered class type
     */
    public void registerHandlerFor(Class<?> clazz, TypeHandler handler, String format) throws IllegalArgumentException {
        if (clazz == null) {
            throw new NullPointerException();
        }
        clazz = TypeUtil.toWrapperClass(clazz);
        registerHandlerFor(format, clazz.getName(), clazz, handler);
    }
    
    private void registerHandlerFor(String format, String type, Class<?> expectedClass, TypeHandler handler) {
        if (!TypeUtil.isAssignable(expectedClass, handler.getType())) {
            throw new IllegalArgumentException("Type handler type '" +
                handler.getType().getName() + "' is not assignable from configured " +
                "type '" + expectedClass.getName() + "'");
        }
        if (format != null) {
            handlerMap.put(format + "." + TYPE_KEY + type, handler);
        }
        else {
            handlerMap.put(TYPE_KEY + type, handler);
        }
    }

    /**
     * Returns the default <tt>TypeHandlerFactory</tt>.
     * @return the default <tt>TypeHandlerFactory</tt>
     */
    public static TypeHandlerFactory getDefault() {
        return defaultFactory;
    }
    
    /**
     * Sets the parent <tt>TypeHandlerFactory</tt>.
     * @param parent the parent <tt>TypeHandlerFactory</tt>
     */
    private void setParent(TypeHandlerFactory parent) {
        this.parent = parent;
    }
}
