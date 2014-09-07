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
package org.beanio.internal.compiler;

import org.beanio.BeanIOConfigurationException;
import org.beanio.internal.config.StreamConfig;
import org.beanio.internal.parser.Stream;
import org.beanio.internal.util.TypeHandlerFactory;

/**
 * A <tt>ParserFactory</tt> is used to convert a stream configuration (i.e. {@link StreamConfig})
 * into a stream parser (i.e. {@link Stream}).
 * 
 * <p>A new parser factory is used to parse each stream configuration, thus implementations
 * need not worry about thread safety.
 * 
 * <p>All properties (e.g. <tt>classLoader</tt>) are set before {@link #createStream(StreamConfig)} is invoked.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface ParserFactory {

    /**
     * Sets the {@link ClassLoader} to use for resolving configured class names.
     * @param classLoader the {@link ClassLoader}
     */
    public void setClassLoader(ClassLoader classLoader);
    
    /**
     * Sets the type handler factory to use for resolving type handlers.
     * @param typeHandlerFactory the {@link TypeHandlerFactory}
     */
    public void setTypeHandlerFactory(TypeHandlerFactory typeHandlerFactory);
    
    /**
     * Creates a new stream parser from a given stream configuration.
     * @param config the stream configuration
     * @return the create {@link Stream}
     * @throws BeanIOConfigurationException if the configuration is invalid
     */
    public Stream createStream(StreamConfig config) throws BeanIOConfigurationException;

}