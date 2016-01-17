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
package org.beanio.internal.parser.format.flat;

import org.beanio.internal.parser.*;
import org.beanio.internal.parser.format.FieldPadding;
import org.beanio.internal.util.DebugUtil;

/**
 * Base class for {@link FlatFieldFormat} implementations.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class FlatFieldFormatSupport implements FlatFieldFormat {

    private String name;
    // measured in fields / characters from the beginning of the record (starting at 0)
    private int position;
    // measured in fields / characters from the end of the record
    private int until = 0; 
    private boolean lazy = false;
    private FieldPadding padding = null;
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#insertValue(org.beanio.internal.parser.MarshallingContext, java.lang.Object)
     */
    @Override
    public boolean insertValue(MarshallingContext context, Object value) {
        return false;
    }

    /**
     * Inserts field text into a record.
     * @param context the {@link MarshallingContext} holding the record
     * @param text the field text to insert into the record
     */
    @Override
    public void insertField(MarshallingContext context, String text) {
        boolean commit = text != null || !isLazy();
        
        if (padding != null) {
            text = padding.pad(text);
        }
        else if (text == null) {
            text = "";
        }
        
        insertFieldText(context, text, commit);
    }
    
    protected abstract void insertFieldText(MarshallingContext context, String text, boolean commit);
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#extract(org.beanio.internal.parser.UnmarshallingContext, boolean)
     */
    @Override
    public String extract(UnmarshallingContext context, boolean reporting) {
        String text = extractFieldText(context, reporting);
        
        if (padding != null) {
            if (text == null) {
                return null;
            }
            else if (text.length() == 0) {
                // this will either cause a required validation error or map
                // to a null value depending on the value of 'required'
                return "";
            }
            else if (text.length() != padding.getLength()) {
                if (reporting) {
                    context.addFieldError(name, text, "length", padding.getLength());
                }
                return Value.INVALID;
            }
            else {
                return padding.unpad(text);
            }
        }
        else {
            return text;
        }
    }
    
    protected abstract String extractFieldText(UnmarshallingContext context, boolean reporting);
    
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
     * @see org.beanio.internal.parser.format.flat.FlatFieldFormat#getPosition()
     */
    @Override
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
   
    public int getUntil() {
        return until;
    }

    public void setUntil(int until) {
        this.until = until;
    }

    @Override
    public int getSize() {
        return 1;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#isNillable()
     */
    @Override
    public boolean isNillable() {
        return false;
    }
    
    /**
     * Returns the field padding.
     * @return the {@link FieldPadding}
     */
    public FieldPadding getPadding() {
        return padding;
    }

    /**
     * Sets the field padding.
     * @param padding the {@link FieldPadding}
     */
    public void setPadding(FieldPadding padding) {
        this.padding = padding;
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + 
            "[at=" + position +
            (until != 0 ? ", until=" + until : "")  +
            ", " + DebugUtil.formatOption("optional", lazy) +
            DebugUtil.formatPadding(padding) +
            "]";
    }
}
