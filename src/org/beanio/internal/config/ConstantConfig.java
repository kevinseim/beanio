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
 * A constant component is used to add a property value to a bean object that is
 * not bound to any field in a stream.  During marshalling, constants can be
 * used to identify the record mapping for a bean object if <tt>identifier</tt>
 * is set to true.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class ConstantConfig extends SimplePropertyConfig {

    private String value;
    
    /**
     * Constructs a new <tt>ConstantConfig</tt>.
     */
    public ConstantConfig() { }
    
    @Override
    public char getComponentType() {
        return CONSTANT;
    }
    
    /**
     * Returns the textual representation of this fixed property value.
     * @return the property value text
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the textual representation of this fixed property value.
     * @param value the property value text
     */
    public void setValue(String value) {
        this.value = value;
    }
}
