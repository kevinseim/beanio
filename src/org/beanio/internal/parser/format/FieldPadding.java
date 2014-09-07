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
package org.beanio.internal.parser.format;

import org.beanio.internal.util.TypeUtil;

/**
 * Provides field padding functionality.  By default, padded fields are 
 * left justified and padded using a space.
 * 
 * <p>The method {@link #init()} must be called after all properties are set.
 * 
 * <p>If <tt>optional</tt> is set to true, the field text may be padded with spaces
 * regardless of the configured <tt>filler</tt> when a value does not exist.
 * 
 * <p>Once configured, a <tt>FieldPadding</tt> object is thread-safe.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FieldPadding {

    /** Right justification */
    public static final char RIGHT = 'R';
    /** Left justification */
    public static final char LEFT = 'L';
    
    private char filler = ' ';
    private char justify = LEFT;
    private int length = 0; // 0 if padding is disabled
    private String defaultText = "";
    private String paddedNull = "";
    private boolean optional;
    private Class<?> propertyType;
    
    /**
     * Constructs a new <tt>FieldPadding</tt>.
     */
    public FieldPadding() { }
    
    /**
     * Initializes padding settings.  This method must be invoked before
     * {@link #pad(String)} or {@link #unpad(String)} is called.
     */
    public void init() {
        // property type may be null if the field was not bound to a bean
        if (propertyType == null) {
            defaultText = "";
            optional = false;
        }
        else {
            propertyType = TypeUtil.toWrapperClass(propertyType);
            if (Character.class.isAssignableFrom(propertyType)) {
                defaultText = Character.toString(filler);
                optional = false;
            }
            else if (Number.class.isAssignableFrom(propertyType)) {
                if (Character.isDigit(filler)) {
                    defaultText = Character.toString(filler);
                }
            }
        }
    }
    
    /**
     * Formats field text.  If <tt>text.length()</tt> exceeds the padding length,
     * the text will be truncated, otherwise it will be padded with <tt>filler</tt>. 
     * @param text the field text to format
     * @return the formatted field text
     */
    public String pad(String text) {
        int currentLength;
        if (text == null) {
            // optional fields are padded with spaces
            if (optional) {
                return paddedNull;
            }
            
            text = "";
            currentLength = 0;
        }
        else if (length < 0) {
            return text;
        }
        else {
            currentLength = text.length();
            if (currentLength == length) {
                return text;
            }
            else if (currentLength > length) {
                return text.substring(0, length);
            }
        }
    
        int remaining = length - currentLength;
        StringBuilder s = new StringBuilder(length);
        if (justify == FieldPadding.LEFT) {
            s.append(text);
            for (int i = 0; i < remaining; i++) {
                s.append(filler);
            }
        }
        else {
            for (int i = 0; i < remaining; i++) {
                s.append(filler);
            }
            s.append(text);
        }
        return s.toString();
    }
    
    /**
     * Removes padding from the field text.
     * @param fieldText the field text to remove padding
     * @return the unpadded field text
     */
    public String unpad(String fieldText) {        
        int length = fieldText.length();
        
        if (justify == FieldPadding.LEFT) {
            int index = fieldText.length();
            while (true) {
                --index;
                
                if (index < 0) {
                    return defaultText;
                }
                else if (fieldText.charAt(index) != filler) {
                    if (index == (length - 1))
                        return fieldText;
                    else
                        return fieldText.substring(0, index + 1);
                }
            }
        }
        else {
            int index = 0;
            while (index < length) {
                if (fieldText.charAt(index) != filler) {
                    if (index == 0)
                        return fieldText;
                    else
                        return fieldText.substring(index, length);
                }
                index++;
            }
            return defaultText;
        }
    }
        
    /**
     * Returns the character used to pad field text.
     * @return the filler character
     */
    public char getFiller() {
        return filler;
    }
    
    /**
     * Sets the character used to pad field text.
     * @param filler the filler character
     */
    public void setFiller(char filler) {
        this.filler = filler;
    }
    
    /**
     * Returns the padded length of the field.
     * @return the padded length
     */
    public int getLength() {
        return length;
    }
    
    /**
     * Sets the padded length of the field.
     * @param length the padded length
     */
    public void setLength(int length) {
        this.length = length;
    }
    
    /**
     * Returns the justification of the field text within its padding.
     * @return either {@link #LEFT} or {@link #RIGHT}
     */
    public char getJustify() {
        return justify;
    }
    
    /**
     * Sets the justification of the field text within its padding.
     * @param justify either {@link #LEFT} or {@link #RIGHT}
     */
    public void setJustify(char justify) {
        this.justify = justify;
    }
    
    /**
     * Returns whether the field is optional.
     * @return true if optional, false otherwise
     */
    public boolean isOptional() {
        return optional;
    }
    
    /**
     * Sets whether the field is optional.
     * @param required true if optional, false otherwise
     */
    public void setOptional(boolean required) {
        this.optional = required;
    }
    
    /**
     * Returns the property type of the field, or null if the field is not
     * bound to a bean object.
     * @return the property type
     */
    public Class<?> getPropertyType() {
        return propertyType;
    }
    
    /**
     * Sets the property type of the field.
     * @param type the property type
     */
    public void setPropertyType(Class<?> type) {
        this.propertyType = type;
    }

    /**
     * Sets the padded field text for a null value.  Defaults to
     * the empty string.
     * @param paddedNull the field text for a null value
     */
    protected void setPaddedNull(String paddedNull) {
        this.paddedNull = paddedNull;
    }
}
