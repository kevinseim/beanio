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
package org.beanio.internal.util;

import java.beans.*;
import java.lang.reflect.Method;
import java.util.*;

import org.beanio.BeanIOConfigurationException;
import org.beanio.types.*;

/**
 * Utility class for instantiating configurable bean classes.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class BeanUtil {

    private static final boolean NULL_ESCAPING_ENABLED = "true".equalsIgnoreCase(
        Settings.getInstance().getProperty(Settings.NULL_ESCAPING_ENABLED));
    
    private final static TypeHandlerFactory typeHandlerFactory;
    static {
        typeHandlerFactory = new TypeHandlerFactory(BeanUtil.class.getClassLoader());
        
        // string arrays allowed for setting 'comments' on a csv/delimited/fixedlength reader
        typeHandlerFactory.registerHandlerFor(String[].class, new StringArrayTypeHandler());
        
        // override string and character type handlers is property escaping is enabled
        if ("true".equals(Settings.getInstance().getProperty(Settings.PROPERTY_ESCAPING_ENABLED))) {
            typeHandlerFactory.registerHandlerFor(String.class, new EscapedStringTypeHandler());
            typeHandlerFactory.registerHandlerFor(Character.class, new EscapedCharacterTypeHandler());
        }
    }
    
    private BeanUtil() { }

    /**
     * Instantiates a bean class.
     * @param classLoader the {@link ClassLoader} to use to resolve <tt>className</tt>
     * @param className the fully qualified name of the bean class to create
     * @param props the bean properties to set on the instantiated object
     * @return the created bean object
     */
    public static Object createBean(ClassLoader classLoader, String className, Properties props) {
        Object bean = createBean(classLoader, className);
        configure(bean, props);
        return bean;
    }
    
    /**
     * Instantiates a bean class using its class name.
     * @param className the fully qualified name of the class to instantiate
     * @return the created bean object
     */
    public static Object createBean(ClassLoader classLoader, String className) {
        if (className == null) {
            throw new BeanIOConfigurationException("Class not set");
        }
        
        // use our own class loader for BeanIO classes
        if (className.startsWith("org.beanio.")) {
            classLoader = BeanUtil.class.getClassLoader();
        }
        
        Class<?> clazz = null;
        try {
            // load the class
            clazz = classLoader.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new BeanIOConfigurationException(
                "Class not found '" + className + "'", e);
        }

        try {
            // instantiate an instance of the class
            return clazz.newInstance();
        }
        catch (Exception e) {
            throw new BeanIOConfigurationException(
                "Cound not instantiate class '" + clazz + "'", e);
        }
    }

    /**
     * Sets properties on a bean object using default type handlers.
     * @param bean the object to set the properties on
     * @param props the bean properties to set on the object
     */
    public static void configure(Object bean, Properties props) {
        // if no properties, we're done...
        if (props == null || props.isEmpty()) {
            return;
        }

        Class<?> clazz = bean.getClass();

        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(clazz);
        }
        catch (IntrospectionException e) {
            throw new BeanIOConfigurationException(e);
        }

        PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {

            String name = (String) entry.getKey();
            PropertyDescriptor descriptor = null;
            for (int i = 0, j = descriptors.length; i < j; i++) {
                if (name.equals(descriptors[i].getName())) {
                    descriptor = descriptors[i];
                    break;
                }
            }
            if (descriptor == null) {
                throw new BeanIOConfigurationException(
                    "Property '" + name + "' not found on class '" + clazz + "'");
            }

            Method method = descriptor.getWriteMethod();
            if (method == null) {
                throw new BeanIOConfigurationException(
                    "Property '" + name + "' is not writeable on class '" + clazz + "'");
            }

            String valueText = (String) entry.getValue();

            Class<?> propertyClass = descriptor.getPropertyType();
            TypeHandler typeHandler = typeHandlerFactory.getTypeHandlerFor(propertyClass);
            if (typeHandler == null) {
                throw new BeanIOConfigurationException("Property type '" + propertyClass +
                    "' not supported for property '" + name + "' on class '" + clazz + "'");
            }

            try {
                Object value = typeHandler.parse(valueText);
                if (value != null || !propertyClass.isPrimitive()) {
                    method.invoke(bean, new Object[] { value });
                }
            }
            catch (TypeConversionException e) {
                throw new BeanIOConfigurationException("Type conversion failed for property '" +
                    name + "' on class '" + clazz + "': " + e.getMessage(), e);
            }
            catch (Exception e) {
                throw new BeanIOConfigurationException("Failed to invoke '" + method +
                    "' on class '" + clazz + "'", e);
            }
        }
    }

    
    /*
     * Type handler implementation for allowing escaped line feeds, carriage returns,
     * tabs and form feeds using a backslash character.
     */
    private static class EscapedCharacterTypeHandler implements TypeHandler {

        /*
         * (non-Javadoc)
         * @see org.beanio.types.TypeHandler#parse(java.lang.String)
         */
        @Override
        public Character parse(String text) throws TypeConversionException {
            if (text == null) {
                return null;
            }
            
            if (text.length() == 1) {
                return text.charAt(0);
            }
            else if ("\\".equals(text)) {
                return '\\';
            }
            else if ("\\n".equals(text)) {
                return '\n';
            }
            else if ("\\r".equals(text)) {
                return '\r';
            }
            else if ("\\t".equals(text)) {
                return '\t';
            }
            else if ("\\f".equals(text)) {
                return '\f';
            }
            else if (NULL_ESCAPING_ENABLED && "\\0".equals(text)) {
                return '\0';
            }
            
            throw new TypeConversionException("Invalid character '" + text + "'");
        }

        /*
         * (non-Javadoc)
         * @see org.beanio.types.TypeHandler#format(java.lang.Object)
         */
        @Override
        public String format(Object value) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see org.beanio.types.TypeHandler#getType()
         */
        @Override
        public Class<?> getType() {
            return Character.class;
        }
    }
    
    /*
     * Type handler implementation for allowing escaped line feeds, carriage returns,
     * tabs and form feeds using a backslash character.
     */
    private static class EscapedStringTypeHandler implements TypeHandler {

        /*
         * (non-Javadoc)
         * @see org.beanio.types.TypeHandler#parse(java.lang.String)
         */
        @Override
        public String parse(String text) throws TypeConversionException {
            if (text == null) {
                return text;
            }
            
            int n = text.indexOf('\\');
            if (n < 0) {
                return text;
            }
            
            int len = text.length();
            boolean escaped = false;
            StringBuilder value = new StringBuilder(len).append(text.substring(0, n));
            
            for (int i=n; i<len; i++) {
                char c = text.charAt(i);
                
                if (escaped) {
                    switch (c) {
                    case 'n':
                        value.append('\n');
                        break;
                    case 'r':
                        value.append('\r');
                        break;
                    case 't':
                        value.append('\t');
                        break;
                    case 'f':
                        value.append('\f');
                        break;
                    case '0':
                        if (NULL_ESCAPING_ENABLED) {
                            value.append('\0');
                        }
                        else {
                            value.append(c);
                        }
                        break;
                       
                    default:
                        value.append(c);
                        break;
                    }
                    escaped = false;
                }
                else if (c == '\\') {
                    escaped = true;
                }
                else {
                    value.append(c);
                }
            }
            
            return value.toString();
        }

        /*
         * (non-Javadoc)
         * @see org.beanio.types.TypeHandler#format(java.lang.Object)
         */
        @Override
        public String format(Object value) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see org.beanio.types.TypeHandler#getType()
         */
        @Override
        public Class<?> getType() {
            return String.class;
        }
    }
    
    /*
     * Type handler implementation for String arrays.  Values must be comma delimited,
     * and a comma can be escaped using a backslash.  All whitespace is preserved.
     */
    private static class StringArrayTypeHandler implements TypeHandler {
        
        /*
         * (non-Javadoc)
         * @see org.beanio.types.TypeHandler#parse(java.lang.String)
         */
        @Override
        public String[] parse(String text) throws TypeConversionException {
            if (text == null || "".equals(text)) {
                return null;
            }
            
            int pos = text.indexOf(',');
            if (pos < 0) {
                return new String[] { text };
            }
            
            boolean escaped = false;
            StringBuilder item = new StringBuilder();
            List<String> list = new ArrayList<>();
            
            char[] ca = text.toCharArray();
            for (char c : ca) {
                if (escaped) {
                    item.append(c);
                    escaped = false;
                }
                else if (c == '\\') {
                    escaped = true;
                }
                else if (c == ',') {
                    list.add(item.toString());
                    item = new StringBuilder();
                }
                else {
                    item.append(c);
                }
            }
            list.add(item.toString());
            
            String [] result = new String[list.size()];
            list.toArray(result);
            return result;
        }

        /*
         * (non-Javadoc)
         * @see org.beanio.types.TypeHandler#format(java.lang.Object)
         */
        @Override
        public String format(Object value) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see org.beanio.types.TypeHandler#getType()
         */
        @Override
        public Class<?> getType() {
            return String[].class;
        }
    }
}
