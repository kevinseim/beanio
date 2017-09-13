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
package org.beanio.internal.compiler;

import java.io.*;
import java.util.*;

import org.beanio.BeanIOConfigurationException;
import org.beanio.builder.StreamBuilder;
import org.beanio.internal.config.*;
import org.beanio.internal.config.xml.XmlConfigurationLoader;
import org.beanio.internal.parser.Stream;
import org.beanio.internal.util.*;
import org.beanio.types.TypeHandler;

/**
 * Compiles a mapping file read from an {@link InputStream} into a collection of 
 * {@link Stream} parsers.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class StreamCompiler {

    private ClassLoader classLoader;
    private ConfigurationLoader configurationLoader;
    private ConfigurationLoader defaultConfigurationLoader;

    /**
     * Constructs a new <tt>MappingFactory</tt>.
     * @param classLoader the {@link ClassLoader} to use for resolving class names
     */
    public StreamCompiler(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.defaultConfigurationLoader = new XmlConfigurationLoader(classLoader);
    }
    
    /**
     * Creates a new Stream from its configuration.
     * @param config the {@link StreamConfig}
     * @return the built {@link Stream} definition
     * @throws BeanIOConfigurationException
     * @since 2.0.5
     */
    public Stream build(StreamConfig config) throws BeanIOConfigurationException {
        TypeHandlerFactory typeHandlerFactory = createTypeHandlerFactory(
            TypeHandlerFactory.getDefault(), config.getHandlerList());
        
        ParserFactory factory = createParserFactory(config.getFormat());
        factory.setClassLoader(classLoader);
        factory.setTypeHandlerFactory(typeHandlerFactory);
        
        return factory.createStream(config);
    }

    /**
     * Loads a mapping file.
     * @param in the {@link InputStream} to load the mapping file from
     * @param properties the {@link Properties}
     * @return the {@link Stream} parsers configured in the loaded mapping file
     * @throws IOException if an I/O error occurs reading the mapping file
     * @throws BeanIOConfigurationException if the mapping file is invalid
     */
    public Collection<Stream> loadMapping(InputStream in, Properties properties) throws IOException,
        BeanIOConfigurationException {
        ConfigurationLoader loader = configurationLoader;
        if (loader == null) {
            loader = getDefaultConfigurationLoader();
        }
        
        Collection<BeanIOConfig> configList = loader.loadConfiguration(in, properties);
        if (configList.isEmpty()) {
            return Collections.emptyList();
        }
        
        // check for duplicate stream names...
        HashSet<String> set = new HashSet<>();
        for (BeanIOConfig config : configList) {
            for (StreamConfig streamConfig : config.getStreamList()) {
                if (!set.add(streamConfig.getName())) {
                    throw new BeanIOConfigurationException("Duplicate stream name '" + 
                        streamConfig.getName() + "'");
                }
            }
        }
        set = null;
        
        // create the stream definitions
        if (configList.size() == 1) {
            return createStreamDefinitions(configList.iterator().next());
        }
        else {
            List<Stream> list = new ArrayList<>();
            for (BeanIOConfig config : configList) {
                list.addAll(createStreamDefinitions(config));
            }
            return list;
        }
    }
    
    /**
     * Returns the default mapping configuration loader implementation.
     * @return the default mapping configuration
     */
    protected ConfigurationLoader getDefaultConfigurationLoader() {
        return defaultConfigurationLoader;
    }

    /**
     * Creates stream definitions from a BeanIO stream mapping configuration.
     * @param config the BeanIO stream mapping configuration
     * @return the collection of stream definitions
     * @throws BeanIOConfigurationException if a configuration setting is invalid
     */
    protected Collection<Stream> createStreamDefinitions(BeanIOConfig config)
        throws BeanIOConfigurationException {
        if (config == null) {
            throw new BeanIOConfigurationException("null configuration");
        }
        
        TypeHandlerFactory parent = createTypeHandlerFactory(TypeHandlerFactory.getDefault(), 
            config.getTypeHandlerList());
        
        Collection<StreamConfig> streamConfigList = config.getStreamList();
        Collection<Stream> streamDefinitionList = new ArrayList<>(streamConfigList.size());
        
        for (StreamConfig streamConfig : streamConfigList) {
            
            TypeHandlerFactory typeHandlerFactory = createTypeHandlerFactory(parent, streamConfig.getHandlerList());
            
            ParserFactory factory = createParserFactory(streamConfig.getFormat());
            factory.setClassLoader(classLoader);
            factory.setTypeHandlerFactory(typeHandlerFactory);
            
            try {
                streamDefinitionList.add(factory.createStream(streamConfig));
            }
            catch (BeanIOConfigurationException ex) {
                if (config.getSource() != null) {
                    throw new BeanIOConfigurationException("Invalid mapping file '" +
                        config.getSource() + "': " + ex.getMessage());
                }
                else {
                    throw ex;
                }
            }
        }
        return streamDefinitionList;
    }

    /**
     * Instantiates the factory implementation to create the stream definition.
     * @param format the stream format
     * @return the stream definition factory
     */
    protected ParserFactory createParserFactory(String format) {
        String clazz = Settings.getInstance().getProperty(
            "org.beanio." + format + ".streamDefinitionFactory");

        if (clazz == null) {
            throw new BeanIOConfigurationException("A stream definition factory " +
                " is not configured for format '" + format + "'");
        }

        Object factory = BeanUtil.createBean(classLoader, clazz);
        if (!ParserFactory.class.isAssignableFrom(factory.getClass())) {
            throw new BeanIOConfigurationException("Configured stream definition factory '" +
                clazz + "' does not implement '" + ParserFactory.class.getName() + "'");
        }
        
        return (ParserFactory) factory;
    }

    /**
     * Creates a type handler factory for a list of configured type handlers. 
     * @param parent the parent {@link TypeHandlerFactory}
     * @param configList the list of type handler configurations
     * @return the new {@link TypeHandlerFactory}, or <tt>parent</tt> if the configuration list was empty
     * @since 2.0
     */
    private TypeHandlerFactory createTypeHandlerFactory(TypeHandlerFactory parent, List<TypeHandlerConfig> configList) {
        if (configList == null || configList.isEmpty()) {
            return parent;
        }
        
        TypeHandlerFactory factory = new TypeHandlerFactory(classLoader, parent);

        // parse global type handlers
        for (TypeHandlerConfig hc : configList) {
            if (hc.getName() == null && hc.getType() == null)
                throw new BeanIOConfigurationException(
                    "Type handler must specify either 'type' or 'name'");

            TypeHandler h = hc.getInstance();
            
            if (h == null) {
                Object bean;
                try {
                    bean = BeanUtil.createBean(classLoader, hc.getClassName(), hc.getProperties());
                }
                catch (BeanIOConfigurationException ex) {
                    if (hc.getName() != null) {
                        throw new BeanIOConfigurationException(
                            "Failed to create type handler named '" + hc.getName() + "'", ex);
                    }
                    else {
                        throw new BeanIOConfigurationException(
                            "Failed to create type handler for type '" + hc.getType() + "'", ex);
                    }
                }
    
                // validate the configured class is assignable to the target class
                if (!TypeHandler.class.isAssignableFrom(bean.getClass())) {
                    throw new BeanIOConfigurationException("Type handler class '" + hc.getClassName() +
                        "' does not implement TypeHandler interface");
                }
                
                h = (TypeHandler) bean;
            }

            if (hc.getName() != null) {
                factory.registerHandler(hc.getName(), h);
            }

            if (hc.getType() != null) {
                try {
                    // type handlers configured for java types may be registered for a specific stream format
                    factory.registerHandlerFor(hc.getType(), h, hc.getFormat());
                }
                catch (IllegalArgumentException ex) {
                    throw new BeanIOConfigurationException("Invalid type handler configuration", ex);
                }
            }
        }
        
        return factory;
    }

    /**
     * Returns the mapping configuration loader.
     * @return the mapping configuration loader 
     */
    public ConfigurationLoader getConfigurationLoader() {
        return configurationLoader;
    }

    /**
     * Sets the mapping configuration loader.
     * @param configurationLoader the mapping configuration loader
     */
    public void setConfigurationLoader(ConfigurationLoader configurationLoader) {
        this.configurationLoader = configurationLoader;
    }
}
