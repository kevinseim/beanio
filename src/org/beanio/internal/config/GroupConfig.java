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

/**
 * A group is used to organize records and the overall structure of a stream.  A group
 * may contain records and/or other groups.  In many cases, a group does not map to any
 * physical aspect of a stream.
 * 
 * <p>The <tt>minSize</tt>, <tt>maxSize</tt>, and <tt>nillable</tt> attributes do 
 * not apply to groups.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class GroupConfig extends PropertyConfig implements SelectorConfig {

    private Integer order;
    private String key;
    private String target;
    
    /**
     * Constructs a new <tt>GroupConfig</tt>.
     */
    public GroupConfig() { }
    
    @Override
    public char getComponentType() { 
        return GROUP;
    }
    
    /**
     * Returns the order this group must appear within the context of its
     * parent group component.  Records and groups assigned the same order 
     * number may appear in any order.
     * @return the order of this record
     */
    @Override
    public Integer getOrder() {
        return order;
    }

    /**
     * Sets the order this group must appear within the context of its
     * parent group component.  Records and groups assigned the same order 
     * number may appear in any order.
     * @param order the order of this record
     */
    @Override
    public void setOrder(Integer order) {
        this.order = order;
    }
    
    /**
     * Returns the name of the property descendant to use for the
     * Map key when <tt>collection</tt> is set to <tt>map</tt>.
     * @return the key property name
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * Sets the name of the property descendant to use for the
     * Map key when <tt>collection</tt> is set to <tt>map</tt>.
     * @param key the key property name
     */
    public void setKey(String key) {
        this.key = key;
    }
    
    /**
     * Returns the name of the target property for this group.
     * @return the target property name
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the name of the target property for this group, which can be used
     * in lieu of <tt>class</tt> to return descendant property types.
     * @param target the target property name
     */
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    protected boolean isSupportedChild(ComponentConfig child) {
        switch (child.getComponentType()) {
            case ComponentConfig.GROUP:
            case ComponentConfig.RECORD:
                return true;
            default:
                return false;
        }
    }
}
