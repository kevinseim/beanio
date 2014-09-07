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
package org.beanio.internal.config;

import java.io.*;
import java.util.*;

import org.beanio.BeanIOConfigurationException;

/**
 * A <tt>ConfigurationLoader</tt> is used to load BeanIO mapping configurations from
 * an input stream.
 * 
 * <p>Implementations must be thread safe.</p>
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public interface ConfigurationLoader {

    /**
     * Loads a BeanIO configuration from an input stream.
     * @param in the input stream to read the configuration from
     * @param properties the {@link Properties} for expansion in the mapping file
     * @return a collection of loaded BeanIO configurations
     * @throws IOException if an I/O error occurs
     * @throws BeanIOConfigurationException if the configuration is invalid or malformed
     * @since 1.2.1
     */
    public Collection<BeanIOConfig> loadConfiguration(InputStream in, Properties properties) 
        throws IOException, BeanIOConfigurationException;

}
