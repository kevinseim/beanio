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
package org.beanio.internal.parser.format.xml;

import org.beanio.internal.parser.*;
import org.beanio.internal.parser.format.FieldPadding;
import org.beanio.internal.util.DebugUtil;

/**
 * Base class for XML {@link FieldFormat} implementations.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class XmlFieldFormat implements FieldFormat, XmlNode {

    private String name;
    private boolean lazy;
    private FieldPadding padding;

    /**
     * Constructs a new <tt>XmlFieldFormat</tt>.
     */
    public XmlFieldFormat() { }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#insertValue(org.beanio.internal.parser.MarshallingContext, java.lang.Object)
     */
    @Override
    public boolean insertValue(MarshallingContext context, Object value) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#insertField(org.beanio.internal.parser.MarshallingContext, java.lang.String)
     */
    @Override
    public void insertField(MarshallingContext context, String fieldText) {
        XmlMarshallingContext ctx = (XmlMarshallingContext) context;
        
        if (padding != null && fieldText != null) {
            fieldText = padding.pad(fieldText);
        }
        
        insertText(ctx, fieldText);
    }
    
    /**
     * Inserts a field into the record during marshalling.
     * @param context the {@link XmlMarshallingContext} holding the record
     * @param text the field text to insert
     */
    protected abstract void insertText(XmlMarshallingContext context, String text);
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#extract(org.beanio.internal.parser.UnmarshallingContext, boolean)
     */
    @Override
    public String extract(UnmarshallingContext context, boolean reportErrors) {
        XmlUnmarshallingContext ctx = (XmlUnmarshallingContext) context;
        
        String fieldText = extractText(ctx);
        ctx.setFieldText(getName(), fieldText == Value.NIL ? null : fieldText);
        
        if (padding != null && fieldText != null) {
            int length = fieldText.length();
            if (length == 0) {
                // this will either cause a required validation error or map
                // to a null value depending on the value of 'required'
                return "";
            }
            else if (length != padding.getLength()) {
                if (reportErrors) {
                    context.addFieldError(getName(), fieldText, "length", padding.getLength());
                }
                fieldText = Value.INVALID;
            }
            else {
                fieldText = padding.unpad(fieldText);
            }
        }
        
        return fieldText;
    }
    
    /**
     * Extracts a field from a record during unmarshalling.
     * @param context the {@link XmlUnmarshallingContext} holding the record
     * @return the extracted field text
     */
    protected abstract String extractText(XmlUnmarshallingContext context);

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#getSize()
     */
    @Override
    public int getSize() {
        return 1;
    }
    
    /**
     * Returns the field name.
     * @return the field name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the field name.
     * @param name the field name
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#isLazy()
     */
    @Override
    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    /**
     * Returns the field padding, or null if the field text is not padded.
     * @return the field padding or null
     */
    public FieldPadding getPadding() {
        return padding;
    }

    /**
     * Sets the field padding.
     * @param padding the field padding
     */
    public void setPadding(FieldPadding padding) {
        this.padding = padding;
    }

    /**
     * Called by {@link #toString()} to append attributes of this field.
     * @param s the text to append
     */
    protected void toParamString(StringBuilder s) {
        s.append(DebugUtil.formatOption("optional", lazy));
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getClass().getSimpleName());
        s.append("[");
        toParamString(s);
        s.append("]");
        return s.toString();
    }
}
