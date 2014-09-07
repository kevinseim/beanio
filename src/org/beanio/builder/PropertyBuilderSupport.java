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

import java.util.*;

import org.beanio.BeanIOConfigurationException;
import org.beanio.internal.config.*;

/**
 * Support for property configuration builders.
 * 
 * <p>Methods may throw a {@link BeanIOConfigurationException} if an 
 * invalid setting is configured.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 * @param <T> the builder subclass
 */
public abstract class PropertyBuilderSupport<T extends PropertyBuilderSupport<T>> {

    PropertyBuilderSupport() { }
    
    /**
     * Returns this.
     * @return this
     */
    protected abstract T me();
    
    /**
     * Returns the configuration settings.
     * @return the configuration
     */
    protected abstract PropertyConfig getConfig();
    
    /**
     * Sets the minimum occurrences of this component.
     * @param min the minimum occurrences
     * @return this
     */
    public T minOccurs(int min) {
        getConfig().setMinOccurs(min);
        return me();
    }
    
    /**
     * Sets the maximum occurrences of this component.
     * @param max the maximum occurrences
     * @return this
     */
    public T maxOccurs(int max) {
        max = max < 0 ? Integer.MAX_VALUE : max;
        getConfig().setMaxOccurs(max);
        return me();
    }

    /**
     * Sets the exact occurrences of this component.
     * @param n the number of occurrences
     * @return this
     */
    public T occurs(int n) {
        return occurs(n, n);
    }
    
    /**
     * Sets the minimum and maximum occurrences of this component.
     * @param min the minimum occurrences
     * @param max the maximum occurrences or -1 for unbounded
     * @return this
     */
    public T occurs(int min, int max) {
        max = max < 0 ? Integer.MAX_VALUE : max;
        getConfig().setMinOccurs(min);
        getConfig().setMaxOccurs(max);
        return me();
    }
    
    /**
     * Sets the class bound to this component.
     * @param type the class
     * @return this
     */
    public T type(Class<?> type) {
        getConfig().setType(type.getName());
        return me();
    }
    
    /**
     * Sets the collection type bound to this component.
     * @param type the {@link Collection} or {@link Map} class or subclass
     * @return this
     */
    public T collection(Class<?> type) {
        getConfig().setCollection(type.getName());
        return me();
    }
    
    /**
     * Sets the getter method for getting this component from its parent.
     * @param getter the getter method name
     * @return this
     */
    public T getter(String getter) {
        getConfig().setGetter(getter);
        return me();
    }
    
    /**
     * Sets the setter method for setting this component on its parent.
     * @param setter the setter method name
     * @return this
     */
    public T setter(String setter) {
        getConfig().setSetter(setter);
        return me();
    }
    
    /**
     * Indicates this component should not be instantiated if this component
     * or all of its children are null or the empty String.
     * @return this
     */
    public T lazy() {
        getConfig().setLazy(true);
        return me();
    }
    
    /**
     * Sets the XML type of this component.
     * @param xmlType the {@link XmlType}
     * @return this
     */
    public T xmlType(XmlType xmlType) {
        getConfig().setXmlType(xmlType == null ? null : xmlType.toValue());
        return me();
    }
    
    /**
     * Sets the XML namespace prefix.
     * @param xmlPrefix the prefix
     * @return this
     */
    public T xmlPrefix(String xmlPrefix) {
        getConfig().setXmlPrefix(xmlPrefix);
        return me();
    }
    
    /**
     * Sets the XML element or attribute name.
     * @param xmlName the name
     * @return this
     */
    public T xmlName(String xmlName) {
        getConfig().setXmlName(xmlName);
        return me();
    }
    
    /**
     * Sets the XML namespace.
     * @param xmlNamespace the namespace
     * @return this
     */
    public T xmlNamespace(String xmlNamespace) {
        getConfig().setXmlNamespace(xmlNamespace);
        return me();
    }
}
