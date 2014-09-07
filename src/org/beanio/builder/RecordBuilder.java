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
 * Builds a new record configuration.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public class RecordBuilder extends SegmentBuilderSupport<RecordBuilder> {

    private RecordConfig config;
    
    /**
     * Constructs a new {@link RecordBuilder}.
     * @param name the record name
     */
    public RecordBuilder(String name) {
        this(name, null);
    }

    /**
     * Constructs a new {@link RecordBuilder}.
     * @param name the record name
     * @param type the record type
     */
    public RecordBuilder(String name, Class<?> type) {
        config = new RecordConfig();
        config.setName(name);
        if (type != null) {
            type(type);
        }
    }
    
    @Override
    protected RecordBuilder me() {
        return this;
    }

    @Override
    protected SegmentConfig getConfig() {
        return config;
    }
    
    /**
     * Sets the order of this record relative to other children
     * of the same parent.
     * @param order the order
     * @return this
     */
    public RecordBuilder order(int order) {
        config.setOrder(order);
        return this;
    }
    
    /**
     * Sets the minimum length of the record (i.e the number of fields
     * in a delimited record, or the number of characters in a fixed length
     * record).
     * @param min the minimum length
     * @return this
     */
    public RecordBuilder minLength(int min) {
        config.setMinLength(min);
        return this;
    }    
    
    /**
     * Sets the minimum length of the record (i.e the number of fields
     * in a delimited record, or the number of characters in a fixed length
     * record).
     * @param max the maximum length, or -1 if unbounded
     * @return this
     */
    public RecordBuilder maxLength(int max) {
        max = max < 0 ? Integer.MAX_VALUE : max;
        config.setMaxLength(max);
        return this;
    }

    /**
     * Sets the length of the record (i.e the number of fields in a delimited 
     * record, or the number of characters in a fixed length record).
     * @param n the length
     * @return this
     */
    public RecordBuilder length(int n) {
        return length(n, n);
    }
    
    /**
     * Sets the minimum and maximum length of the record (i.e the number of fields
     * in a delimited record, or the number of characters in a fixed length
     * record).
     * @param min the minimum length
     * @param max the maximum length, or -1 if unbounded
     * @return this
     */
    public RecordBuilder length(int min, int max) {
        minLength(min);
        maxLength(max);
        return this;
    }

    /**
     * Sets the length of the record for identification.
     * @param n the length
     * @return this
     */
    public RecordBuilder ridLength(int n) {
        return ridLength(n, n);
    }
    
    /**
     * Sets the minimum and maximum length of the record for identification.
     * @param min the minimum length
     * @param max the maximum length, or -1 if unbounded
     * @return this
     */
    public RecordBuilder ridLength(int min, int max) {
        max = max < 0 ? Integer.MAX_VALUE : max;
        config.setMinMatchLength(min);
        config.setMaxMatchLength(max);
        return this;
    }
    
    /**
     * Builds the record configuration.
     * @return the record configuration
     */
    public RecordConfig build() {
        return config;
    }
}
