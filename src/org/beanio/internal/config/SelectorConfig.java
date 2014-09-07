/*
 * Copyright 2011 Kevin Seim
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

/**
 * This interface is implemented by configuration components used to select
 * a record for marshalling or unmarshalling, namely {@link RecordConfig} and
 * {@link GroupConfig} components.
 *  
 * @author Kevin Seim
 * @since 2.0
 */
public interface SelectorConfig {
    
    /**
     * Returns the component type of this selector.
     * @return either {@link ComponentConfig#RECORD} or {@link ComponentConfig#GROUP}
     */
    public char getComponentType();
    
    /**
     * Returns the name of this component.
     * @return the component name.
     */
    public String getName();
    
    /**
     * Returns the minimum occurrences of this component.
     * @return the minimum occurrences
     */
    public Integer getMinOccurs();
    
    /**
     * Returns the maximum occurrences of this component.
     * @return the maximum occurrences
     */
    public Integer getMaxOccurs();
    
    /**
     * Returns the order of this component within the context of
     * its parent group.  Records and groups assigned the same order 
     * number may appear in any order.
     * @return the component order (starting at 1)
     */
    public Integer getOrder();
    
    /**
     * Sets the order of this component within the context of
     * its parent group.  Records and groups assigned the same order 
     * number may appear in any order.
     * @param order the component order (starting at 1)
     */
    public void setOrder(Integer order);
    
}
