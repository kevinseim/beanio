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
package org.beanio.internal.parser.format.fixedlength;

import org.beanio.internal.parser.format.FieldPadding;

/**
 * {@link FieldPadding} implementation for a fixed length field.
 * 
 * <p>Fixed length padding differs from other field padding in that a completely blank
 * optional field (i.e. all spaces) is formatted as the empty string regardless of the filler
 * character, thus allowing for optional numeric fields.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FixedLengthFieldPadding extends FieldPadding {

    /**
     * Constructs a new <tt>FixedLengthFieldPadding</tt>.
     */
    public FixedLengthFieldPadding() { }
    
    @Override
    public void init() {
        super.init();
        
        if (getLength() > 0) {
            StringBuilder s = new StringBuilder(getLength());
            for (int i=0,j=getLength(); i<j; i++) {
                s.append(' ');
            }
            setPaddedNull(s.toString());
        }
    }
    
    @Override
    public String unpad(String fieldText) {
        // return empty string if the field is all spaces, to allow for optional 
        // zero padded fields
        if (isOptional() && isBlank(fieldText)) {
            return "";
        }
        
        return super.unpad(fieldText);
    }

    private boolean isBlank(String s) {
        for (int i=0,j=s.length(); i<j; i++) {
            if (s.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }
}
