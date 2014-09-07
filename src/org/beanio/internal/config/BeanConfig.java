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
package org.beanio.internal.config;

import java.util.Properties;

/**
 * Stores bean information.  A <tt>Bean</tt> object is used to instantiate 
 * configurable components such as a type handler, record reader factory
 * or record writer factory.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class BeanConfig<T> {

    private String className;
    private Properties properties;
    private T instance;
    
    /**
     * Returns the fully qualified class name of the bean.
     * @return the bean class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the fully qualified class name of the bean.
     * @param className the bean class name
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Returns bean properties.
     * @return the bean properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets bean properties.
     * @param properties the new bean properties
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Returns the configured instance of this bean.
     * @return the instance
     */
    public T getInstance() {
        return instance;
    }

    /**
     * Sets the configured instance of this bean.
     * @param instance the bean
     */
    public void setInstance(T instance) {
        this.instance = instance;
    }
}

