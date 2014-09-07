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
import org.beanio.internal.parser.format.FieldPadding;
import org.beanio.internal.parser.format.flat.FlatFieldFormatSupport;

/**
 * A {@link FieldFormat} implementation for a field in a fixed length formatted stream.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FixedLengthFieldFormat extends FlatFieldFormatSupport implements FieldFormat {
    
    private boolean keepPadding;
    private boolean lenientPadding;
    
    /**
     * Constructs a new <tt>FixedLengthFieldFormat</tt>.
     */
    public FixedLengthFieldFormat() { }
    
    @Override
    public String extract(UnmarshallingContext context, boolean reportErrors) {
        String text = extractFieldText(context, reportErrors);
        if (text == null) {
            return null;
        }
        
        FieldPadding padding = getPadding();
        if (padding.getLength() >= 0 && text.length() != padding.getLength() && !lenientPadding) {
            if (reportErrors) {
                context.addFieldError(getName(), text, "length", padding.getLength());
            }
            return Value.INVALID;
        }
        else if (keepPadding) {
            // return empty string for required fields to trigger the field validation
            if (!padding.isOptional()) {
                String s = padding.unpad(text);
                if (s.length() == 0) {
                    return s;
                }
            }
            
            return text;
        }
        else {
            return padding.unpad(text);
        }
    }
    
    @Override
    public String extractFieldText(UnmarshallingContext context, boolean reporting) {
        FixedLengthUnmarshallingContext ctx = ((FixedLengthUnmarshallingContext)context);
        return ctx.getFieldText(getName(), getPosition(), getSize(), getUntil());
    }

    @Override
    public void insertFieldText(MarshallingContext context, String fieldText, boolean commit) {
        FixedLengthMarshallingContext ctx = ((FixedLengthMarshallingContext)context);
        ctx.setFieldText(getPosition(), fieldText, commit);
    }

    @Override
    public int getSize() {
        return getPadding().getLength();
    }    
    
    /**
     * Set to true to keep field padding during unmarshalling.
     * @param keepPadding true to keep padding
     * @since 2.0.2
     */
    public void setKeepPadding(boolean keepPadding) {
        this.keepPadding = keepPadding;
    }
    
    /**
     * Sets whether the padding length is enforced.
     * @param lenientPadding true if not enforced, false otherwise
     * @since 2.1.0
     */
    public void setLenientPadding(boolean lenientPadding) {
        this.lenientPadding = lenientPadding;
    }
}
