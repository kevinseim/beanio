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

/**
 * A record is a segment that is bound to a record in a stream.  The physical
 * representation of a record is dependent on the type of stream.  A typical
 * example might be a line in a fixed length or delimited flat file.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class RecordConfig extends SegmentConfig implements SelectorConfig {

    private Integer order;
    private Integer minLength;
    private Integer maxLength;
    private Integer minMatchLength;
    private Integer maxMatchLength;
    
    /**
     * Constructs a new <tt>RecordConfig</tt>.
     */
    public RecordConfig() { }
    
    @Override
    public char getComponentType() {
        return RECORD;
    }
    
    /**
     * Returns the minimum length of the record.  Depending on the type
     * of stream, the length may refer to the number of fields or the 
     * number of characters.
     * @return the minimum record length, or <tt>null</tt> if not set
     */
    public Integer getMinLength() {
        return minLength;
    }

    /**
     * Sets the minimum length of the record.  Depending on the type
     * of stream, the length may refer to the number of fields or the 
     * number of characters.
     * @param minLength the minimum record length, or <tt>null</tt> if not set
     */
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * Returns the maximum length of the record.  Depending on the type of
     * stream, the length may refer to the number of fields or the number
     * of characters.
     * @return the maximum record length, or <tt>null</tt> if not set
     */
    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the maximum length of the record.  Depending on the type of
     * stream, the length may refer to the number of fields or the number
     * of characters.
     * @param maxLength the maximum record length, or <tt>null</tt> if not set
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
    
    /**
     * Returns the minimum record length for identifying this record.
     * @return the minimum record length, or null if no minimum length applies
     */
    public Integer getMinMatchLength() {
        return minMatchLength;
    }

    /**
     * Sets the minimum record length for identifying this record.
     * @param minMatchLength the minimum record length, or null if no minimum length applies
     */
    public void setMinMatchLength(Integer minMatchLength) {
        this.minMatchLength = minMatchLength;
    }

    /**
     * Returns the maximum record length for identifying this record.
     * @return the maximum record length, or null if no maximum length applies
     */
    public Integer getMaxMatchLength() {
        return maxMatchLength;
    }

    /**
     * Sets the maximum record length for identifying this record.
     * @param maxMatchLength the maximum record length, or null if no maximum length applies
     */
    public void setMaxMatchLength(Integer maxMatchLength) {
        this.maxMatchLength = maxMatchLength;
    }

    /**
     * Returns the order this record must appear within the context of its
     * parent group component.  Records and groups assigned the same order 
     * number may appear in any order.
     * @return the order of this record
     */
    @Override
    public Integer getOrder() {
        return order;
    }

    /**
     * Sets the order this record must appear within the context of its
     * parent group component.  Records and groups assigned the same order 
     * number may appear in any order.
     * @param order the order of this record
     */
    @Override
    public void setOrder(Integer order) {
        this.order = order;
    }
}
