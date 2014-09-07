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

import org.beanio.internal.config.SegmentConfig;

/**
 * Builds a new segment configuration.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public class SegmentBuilder extends SegmentBuilderSupport<SegmentBuilder> {

    protected SegmentConfig config = new SegmentConfig();
    
    public SegmentBuilder(String name) {
        config.setName(name);
    }
    
    @Override
    protected SegmentConfig getConfig() {
        return config;
    }

    @Override
    protected SegmentBuilder me() {
        return this;
    }
    
    /**
     * Indicates the number of occurrences of this segment is governed by another field.
     * @param ref the name of the field that governs the occurrences of this segment
     * @return this
     */
    public SegmentBuilder occursRef(String ref) {
        config.setOccursRef(ref);
        return this;
    }
    
    /**
     * Indicates the XML element is nillable.
     * @return this
     */
    public SegmentBuilder nillable() {
        config.setNillable(true);
        return this;
    }
    
    /**
     * Builds the segment configuration.
     * @return the segment configuration
     */
    public SegmentConfig build() {
        return config;
    }
}
