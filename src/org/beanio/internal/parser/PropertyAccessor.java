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
package org.beanio.internal.parser;

/**
 * A PropertyAccessor provides access to a bean property.
 * 
 * <p>Implementations must be thread safe.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface PropertyAccessor {

    /**
     * Returns the property value from a bean object. 
     * @param bean the bean object to get the property from
     * @return the property value
     */
    public Object getValue(Object bean);
    
    /**
     * Sets the property value on a bean object.
     * @param bean the bean object to set the property
     * @param value the property value
     */
    public void setValue(Object bean, Object value);
    
    /**
     * Returns whether this property is a constructor argument.
     * @return true if this property is a constructor argument, false otherwise
     */
    public boolean isConstructorArgument();
    
    /**
     * Returns the constructor argument index, or -1 if this property is
     * not a constructor argument.
     * @return the constructor argument index
     */
    public int getConstructorArgumentIndex();
}
