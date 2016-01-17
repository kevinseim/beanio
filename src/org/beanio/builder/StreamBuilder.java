/*
 * Copyright 2013 Kevin Seim
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
package org.beanio.builder;

import org.beanio.BeanIOConfigurationException;
import org.beanio.internal.config.*;
import org.beanio.stream.RecordParserFactory;
import org.beanio.types.TypeHandler;

/**
 * Builds a new stream configuration.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public class StreamBuilder extends GroupBuilderSupport<StreamBuilder> {

    protected StreamConfig config;
    
    /**
     * Constructs a new StreamBuilder.
     * @param name the stream name
     */
    public StreamBuilder(String name) {
        config = new StreamConfig();
        config.setName(name);
    }
    
    /**
     * Constructs a new StreamBuilder.
     * @param name the stream name
     * @param format the stream format
     */
    public StreamBuilder(String name, String format) {
        config = new StreamConfig();
        config.setName(name);
        config.setFormat(format);
    }

    @Override
    protected StreamBuilder me() {
        return this;
    }
    
    @Override
    protected GroupConfig getConfig() {
        return config;
    }
    
    /**
     * Sets the stream format.
     * @param format the format (e.g. csv, delimited, fixedlength, xml)
     * @return this
     */
    public StreamBuilder format(String format) {
        config.setFormat(format);
        return this;
    }
    
    /**
     * Sets the parser for this stream.
     * @param parser the stream parser factory
     * @return this
     */
    public StreamBuilder parser(RecordParserFactory parser) {
        BeanConfig<RecordParserFactory> bc = new BeanConfig<>();
        bc.setInstance(parser);
        config.setParserFactory(bc);
        return this;
    }
    
    /**
     * Sets the parser for this stream.
     * @param parser the {@link ParserBuilder}
     * @return this
     */
    public StreamBuilder parser(ParserBuilder parser) {
        config.setParserFactory(parser.build());
        return this;
    }
    
    /**
     * Adds a type handler 
     * @param name the name of the type handler
     * @param type the class parsed by the type handler 
     * @param handler the type handler
     * @return this
     */
    public StreamBuilder addTypeHandler(String name, Class<?> type, TypeHandler handler) {
        TypeHandlerConfig thc = new TypeHandlerConfig();
        thc.setName(name);
        if (type != null) {
            thc.setType(type.getName());
        }
        thc.setInstance(handler);
        config.addHandler(thc);
        return this;
    }
    
    /**
     * Adds a type handler 
     * @param name the name of the type handler
     * @param handler the type handler
     * @return this
     */
    public StreamBuilder addTypeHandler(String name, TypeHandler handler) {
        return addTypeHandler(name, null, handler);
    }
    
    /**
     * Adds a type handler 
     * @param type the class parsed by the type handler 
     * @param handler the type handler
     * @return this
     */
    public StreamBuilder addTypeHandler(Class<?> type, TypeHandler handler) {
        return addTypeHandler(null, type, handler);
    }
    
    /**
     * Not supported.
     */
    @Override
    public StreamBuilder type(Class<?> type) {
        throw new BeanIOConfigurationException("type not supported by StreamBuilder");
    }
    
    /**
     * Not supported.
     */
    @Override
    public StreamBuilder collection(Class<?> type) {
        throw new BeanIOConfigurationException("collection not supported by StreamBuilder");
    }
    
    /**
     * Indicates this stream configuration is only used for unmarshalling.
     * @return this
     */
    public StreamBuilder readOnly() {
        config.setMode(StreamConfig.READ_ONLY_MODE);
        return this;
    }
    
    /**
     * Indicates this stream configuration is only used for marshalling.
     * @return this
     */
    public StreamBuilder writeOnly() {
        config.setMode(StreamConfig.WRITE_ONLY_MODE);
        return this;
    }
    
    public StreamBuilder resourceBundle(String name) {
        config.setResourceBundle(name);
        return this;
    }
    
    /**
     * Indicates this stream should be strictly validated.
     * @return this
     */
    public StreamBuilder strict() {
        config.setStrict(true);
        return this;
    }
    
    /**
     * Indicates unidentified records should be ignored during unmarshalling.
     * @return this
     */
    public StreamBuilder ignoreUnidentifiedRecords() {
        config.setIgnoreUnidentifiedRecords(true);
        return this;
    }
    
    /**
     * Builds the stream configuration.
     * @return the stream configuration
     */
    public StreamConfig build() {
        return this.config;
    }
}
