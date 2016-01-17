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

import org.beanio.internal.config.*;

/**
 * Support for segment configuration builders.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public abstract class SegmentBuilderSupport<T extends SegmentBuilderSupport<T>> extends PropertyBuilderSupport<T> {

    SegmentBuilderSupport() { }
    
    @Override
    protected abstract SegmentConfig getConfig();
    
    /**
     * Sets the name of a child component to use as the key for an
     * inline map bound to this record or segment.
     * @param name the component name
     * @return this
     */
    public T key(String name) {
        getConfig().setKey(name);
        return me();
    }
    
    /**
     * Sets the name of a child component to return as the value for this
     * record or segment in lieu of a bound class.
     * @param name the component name
     * @return this
     */
    public T value(String name) {
        getConfig().setTarget(name);
        return me();
    }
    
    /**
     * Adds a segment to this component.
     * @param segment the segment to add
     * @return this
     */
    public T addSegment(SegmentBuilder segment) {
        getConfig().add(segment.build());
        return me();
    }
    
    /**
     * Adds a field to this component.
     * @param field the field to add
     * @return this
     */
    public T addField(FieldBuilder field) {
        getConfig().add(field.build());
        return me();
    }
}
