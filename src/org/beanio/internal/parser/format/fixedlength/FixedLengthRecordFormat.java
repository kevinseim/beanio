/*
 * Copyright 2011-2013 Kevin Seim
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
package org.beanio.internal.parser.format.fixedlength;

import org.beanio.internal.parser.*;

/**
 * A {@link RecordFormat} implementation for a fixed length formatted record.
 * 
 * <p>A fixed length record may be configured to validate record length by
 * setting a minimum and maximum length.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FixedLengthRecordFormat implements RecordFormat {

    private int minLength = 0;
    private int maxLength = Integer.MAX_VALUE;
    
    private int minMatchLength = 0;
    private int maxMatchLength = Integer.MAX_VALUE;
    
    /**
     * Constructs a new <tt>FixedLengthRecordFormat</tt>.
     */
    public FixedLengthRecordFormat() { }
   
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.RecordFormat#matches(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public boolean matches(UnmarshallingContext context) {
        int length = ((FixedLengthUnmarshallingContext)context).getRecordLength();
        return !(length < minMatchLength || length > maxMatchLength);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.RecordFormat#validate(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public void validate(UnmarshallingContext context) {
        int length = ((FixedLengthUnmarshallingContext)context).getRecordLength();
        
        if (length < minLength) {
            context.addRecordError("minLength", minLength, maxLength);
        }
        if (length > maxLength) {
            context.addRecordError("maxLength", minLength, maxLength);
        }
    }

    /**
     * Returns the minimum length of the record in characters.  Defaults to 0.
     * @return the minimum record length
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Sets the minimum length of the record in characters.
     * @param minLength the minimum record length
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    /**
     * Returns the maximum length of the record in characters.  Deafults to
     * {@link Integer#MAX_VALUE}.
     * @return the maximum length of the record
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the maximum length of the record in characters.
     * @param maxLength the maximum length of the record
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    
    /**
     * Returns the minimum record length for identifying a record.
     * @return the minimum number of characters
     */
    public int getMinMatchLength() {
        return minMatchLength;
    }

    /**
     * Sets the minimum record length for identifying a record.
     * @param minMatchLength the minimum number of characters
     */
    public void setMinMatchLength(int minMatchLength) {
        this.minMatchLength = minMatchLength;
    }

    /**
     * Returns the maximum record length for identifying a record.
     * @return the maximum number of characters
     */
    public int getMaxMatchLength() {
        return maxMatchLength;
    }

    /**
     * Sets the maximum record length for identifying a record.
     * @param maxMatchLength the maximum number of characters
     */
    public void setMaxMatchLength(int maxMatchLength) {
        this.maxMatchLength = maxMatchLength;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getClass().getSimpleName()).append("[");
        s.append("recordLength=").append(minLength).append('-').append(maxLength);
        s.append("]");
        return s.toString();
    }
}
