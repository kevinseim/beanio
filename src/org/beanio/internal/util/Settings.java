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

import java.io.*;
import java.net.*;
import java.util.Properties;

import org.beanio.BeanIOException;

/**
 * <tt>Settings</tt> is used to load and store BeanIO configuration settings.  All settings are
 * global within the JVM (or actually the class loader).
 * <p>
 * Default BeanIO settings can be overridden using a property file named <tt>beanio.properties</tt>
 * The file will be loaded from the current working directory or from anywhere on the classpath.  
 * The default configuration filename can be overridden using the System property 
 * <tt>org.beanio.configuration</tt>.
 * <p>
 * Configuration settings can be further overridden by any System property of the same name when
 * the configuration file is loaded.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class Settings {

    /** This property is set to the fully qualified class name of the default stream factory implementation  */
    public static final String STREAM_FACTORY_CLASS = "org.beanio.streamFactory";

    /** The default locale used by type handlers */
    public static final String DEFAULT_LOCALE = "org.beanio.defaultTypeHandlerLocale";
    /** The default date format pattern for fields assigned type alias <tt>Date</tt> */
    public static final String DEFAULT_DATE_FORMAT = "org.beanio.defaultDateFormat";
    /** The default date format pattern for fields assigned type alias <tt>DateTime</tt> or of type <tt>java.util.Date</tt> */
    public static final String DEFAULT_DATETIME_FORMAT = "org.beanio.defaultDateTimeFormat";
    /** The default date format pattern for fields assigned type alias <tt>Time</tt> */
    public static final String DEFAULT_TIME_FORMAT = "org.beanio.defaultTimeFormat";
    
    /** 
     * Whether property values support the following escape sequences:  
     * <ul>
     *   <li><tt>\\</tt> - Backslash</li>
     *   <li><tt>\n</tt> - Line Feed</li>
     *   <li><tt>\r</tt> - Carriage Return</li>
     *   <li><tt>\t</tt> - Tab</li>
     *   <li><tt>\f</tt> - Form Feed</li>
     *   <li><tt>\0</tt> - Null</li>
     * </ul>
     * <p>A backslash preceding any other character is ignored.
     * <p>Set to <tt>false</tt> to disable.
     * @since 1.2
     */
    public static final String PROPERTY_ESCAPING_ENABLED = "org.beanio.propertyEscapingEnabled";
    /** Whether the null character can be escaped using <tt>\0</tt> when property escaping is enabled. */
    public static final String NULL_ESCAPING_ENABLED = "org.beanio.propertyEscapingEnabled";
    /** Whether property substitution is enabled for mapping files */
    public static final String PROPERTY_SUBSTITUTION_ENABLED = "org.beanio.propertySubstitutionEnabled";
    /** The default XML type for a field definition, set to <tt>element</tt> or <tt>attribute</tt>. */
    public static final String DEFAULT_XML_TYPE = "org.beanio.xml.defaultXmlType";
    /** The default namespace prefix for 'http://www.w3.org/2001/XMLSchema-instance' */
    public static final String DEFAULT_XSI_NAMESPACE_PREFIX = "org.beanio.xml.xsiNamespacePrefix";
    /** 
     * Used for Spring Batch integration.  Set to 'true' to have the XmlWriter only update the execution context (state)
     * with changes since the last update.  At the time of writing, it's not known whether Spring Batch will create a new
     * ExecutionContext every time state is updated, or if the current context is used.  Disabled by default until proven
     * the optimization will not impact state updates. 
     */
    public static final String XML_WRITER_UPDATE_STATE_USING_DELTA = "org.beanio.stream.xml.XmlWriter.deltaEnabled";
    /** 
     * Whether a configured field default is marshalled for null property values.  The default configuration
     * sets this property to <tt>true</tt>.
     * @since 1.2.2
     */
    public static final String DEFAULT_MARSHALLING_ENABLED = "org.beanio.marshalDefaultEnabled";
    
    /** The default minOccurs setting for a group. */
    public static final String DEFAULT_GROUP_MIN_OCCURS = "org.beanio.group.minOccurs";
    /** The default minOccurs setting for a record. */
    public static final String DEFAULT_RECORD_MIN_OCCURS = "org.beanio.record.minOccurs";
    /** The default minOccurs setting for a field (after appending the stream format) */
    public static final String DEFAULT_FIELD_MIN_OCCURS = "org.beanio.field.minOccurs";
    /** The method of property access to use, 'reflection' (default) or 'asm' is supported */
    public static final String PROPERTY_ACCESSOR_METHOD = "org.beanio.propertyAccessorFactory";
    /**
     * Whether version 2.0.0 style unmarshalling should be supported which instantiates bean objects
     * for missing fields and records during unmarshalling.  This behavior is not recommended.
     */
    public static final String CREATE_MISSING_BEANS = "org.beanio.createMissingBeans";
    /** 
     * Whether objects are lazily instantiated if Strings are empty, rather than just null.
     * @since 2.1.0  
     */
    public static final String LAZY_IF_EMPTY = "org.beanio.lazyIfEmpty";
    /**
     * Whether null field values should throw an exception if bound to a primitive
     * @since 2.1.0 
     */
    public static final String ERROR_IF_NULL_PRIMITIVE = "org.beanio.errorIfNullPrimitive";
    /** 
     * Whether default field values apply to missing fields
     * @since 2.1.0 
     */
    public static final String USE_DEFAULT_IF_MISSING = "org.beanio.useDefaultIfMissing";
    /**
     * Whether XML components should be sorted by position.  Helpful for use with annotations
     * where fields and methods may not be ordered by their position in the stream.
     * @since 2.1.0
     */
    public static final String SORT_XML_COMPONENTS_BY_POSITION = "org.beanio.xml.sorted";
    /**
     * Whether non-public fields and methods may be made accessible.
     */
    public static final String ALLOW_PROTECTED_PROPERTY_ACCESS = "org.beanio.allowProtectedAccess";
    
    private static final String DEFAULT_CONFIGURATION_PATH = "org/beanio/internal/config/beanio.properties";
    private static final String DEFAULT_CONFIGURATION_FILENAME = "beanio.properties";
    private static final String CONFIGURATION_PROPERTY = "org.beanio.configuration";
    
    private Properties properties;
    private static Settings settings;

    /**
     * Constructs a new <tt>Settings</tt>.
     * @param props the properties to expose as BeanIO settings
     */
    private Settings(Properties props) {
        this.properties = props;
    }

    /**
     * Returns a BeanIO configuration setting.
     * @param key the name of the setting
     * @return the value of the setting, or null if the name is invalid
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Returns the boolean value of a BeanIO configuration setting.
     * @param key the property key
     * @return true if the property value is "true" (case insensitive), 
     *   or false if the property is any other value
     */
    public boolean getBoolean(String key) {
        return "true".equalsIgnoreCase(getProperty(key));
    }
    
    /**
     * Returns a BeanIO configuration setting as an integer.
     * @param key the property key
     * @param defaultValue the default value if the setting wasn't
     *   configured or invalid
     * @return the <tt>int</tt> property value or <tt>defaultValue</tt>
     */
    public int getInt(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            if (value != null) {
                return Integer.parseInt(value);
            }
        }
        catch (NumberFormatException ex) { }
        
        return defaultValue;
    }

    /**
     * Returns the <tt>Settings</tt> instance.
     * @return the Settings
     */
    public synchronized static Settings getInstance() {
        return getInstance(null);
    }
    
    /**
     * Returns the <tt>Settings</tt> instance.
     * @param classLoader the {@link ClassLoader} to use for loading classpath resources
     * @return the Settings
     * @since 2.0
     */
    public synchronized static Settings getInstance(ClassLoader classLoader) {
        if (settings != null) {
            return settings;
        }
        
        if (classLoader == null) {
            classLoader = Settings.class.getClassLoader();
        }
        
        Properties props = new Properties();

        // load default configuration settings
        loadProperties(Settings.class.getClassLoader().getResource(DEFAULT_CONFIGURATION_PATH), props);

        boolean required = false;
        String location = System.getProperty(CONFIGURATION_PROPERTY);

        URL configurationUrl = null;
        if (location != null) {
            // check working directory for configuration file
            configurationUrl = getFileURL(location);

            // otherwise check the classpath
            if (configurationUrl == null) {
                if (location.startsWith("/")) {
                    location = location.substring(1);
                }
                configurationUrl = classLoader.getResource(location); 
            }

            required = true;
        }
        else {
            configurationUrl = getFileURL(DEFAULT_CONFIGURATION_FILENAME);

            // otherwise check the classpath
            if (configurationUrl == null) {
                configurationUrl = classLoader.getResource(DEFAULT_CONFIGURATION_FILENAME);
            }
        }

        // load user configuration settings
        if (configurationUrl != null) {
            loadProperties(configurationUrl, props);
        }
        else if (required) {
            throw new BeanIOException("BeanIO configuration settings not found at '" + location + "'");
        }

        // allow System properties to override file settings
        for (Object key : props.keySet()) {
            String value = System.getProperty((String) key);
            if (value != null) {
                props.put(key, value);
            }
        }

        settings = new Settings(props);
        return settings;
    }

    /*
     * Returns a URL for a file name, or null if the file doesn't exist.
     */
    private static URL getFileURL(String location) {
        File file = new File(location);
        if (file.exists()) {
            try {
                return file.toURI().toURL();
            }
            catch (MalformedURLException ex) {
                throw new BeanIOException("Invalid configuration location: " + location, ex);
            }
        }
        return null;
    }

    /*
     * Loads a property file at the given URL.
     */
    private static void loadProperties(URL url, Properties props) {
        InputStream in = null;
        try {
            in = url.openStream();
            props.load(in);
        }
        catch (IOException ex) {
            throw new BeanIOException("IOException caught reading configuration file: " + url, ex);
        }
        finally {
            IOUtil.closeQuietly(in);
        }
    }
}
