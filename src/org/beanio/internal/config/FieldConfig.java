/*
 * Copyright 2010-2013 Kevin Seim
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
 * A field is used to define the smallest physical part of a stream.  Fields are
 * combined to form segments and records.  
 * 
 * <p>Unless <tt>bound</tt> is set to false, a field is bound to a property of 
 * its closest parent bean object.
 * 
 * <p>Position must be set for all fields in record, or for none of them.  If not
 * set, position is determined based on the order in which the fields are added to
 * the record.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FieldConfig extends SimplePropertyConfig {

    /** Left justification setting */
    public static final String LEFT = "left";
    /** Right justification setting */
    public static final String RIGHT = "right";

    private String regex;
    private String literal;
    private Integer minLength;
    private Integer maxLength; // set to -1 for unbounded
    private boolean trim;
    private boolean required;
    private boolean ref; // derived during pre-processing
    
    private Integer length;
    private String defaultValue;
    private Character padding;
    private String justify = LEFT;
    private boolean keepPadding;
    private boolean lenientPadding;
    
    /**
     * Constructs a new <tt>FieldConfig</tt>.
     */
    public FieldConfig() { }
    
    @Override
    public char getComponentType() { 
        return FIELD;
    }

    /**
     * Returns the textual representation of the default value for
     * this field when the field is not present or empty during unmarshalling.
     * May be <tt>null</tt>.
     * @return the default value (as text)
     */
    public String getDefault() {
        return defaultValue;
    }

    /**
     * Sets the textual representation of the default value of
     * this field when the field is not present or empty during unmarshalling.
     * May be <tt>null</tt>.
     * @param text the default value (as text)
     */
    public void setDefault(String text) {
        this.defaultValue = text;
    }

    /**
     * Returns the minimum length of this field in characters, or <tt>null</tt>
     * if a minimum length should not be enforced.
     * @return the minimum length, or <tt>null</tt> if not enforced
     */
    public Integer getMinLength() {
        return minLength;
    }

    /**
     * Sets the minimum length of this field in characters, or <tt>null</tt>
     * if a minimum length should not be enforced.
     * @param minLength the minimum length, or <tt>null</tt> if not enforced
     */
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * Returns the maximum length of this field in characters.  Returns
     * <tt>null</tt> if a maximum length will not be enforced.
     * @return the maximum length, or <tt>null</tt> if not enforced
     */
    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the maximum length of this field in characters.  Set to 
     * <tt>null</tt> if a maximum length should not be enforced.
     * @param maxLength the maximum length, or <tt>null</tt> if not enforced
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Returns whether field text should be trimmed before validation
     * and type conversion.
     * @return <tt>true</tt> if field text should be trimmed
     */
    public boolean isTrim() {
        return trim;
    }

    /**
     * Sets whether field text should be trimmed before validation
     * and type conversion.
     * @param trim <tt>true</tt> if field text should be trimmed
     */
    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    /**
     * Returns the static text for this field, or <tt>null</tt> if
     * the field text is not static.  If set, unmarshalled field text must
     * match the literal text, and likewise, the literal text is always marshalled
     * for this field.
     * @return the literal text of the field
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * Sets the static text for this field.  If set, unmarshalled field text must
     * match the literal text, and likewise, the literal text is always marshalled
     * for this field.
     * @param literal the literal text of the field
     */
    public void setLiteral(String literal) {
        this.literal = literal;
    }

    /**
     * Returns the regular expression pattern for validating the field text
     * during unmarshalling.  Field text is only validated using the regular
     * expression after trimming (if enabled) and when its not the empty string. 
     * @return the regular expression pattern
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Sets the regular expression pattern for validating the field text
     * during unmarshalling.  Field text is only validated using the regular
     * expression after trimming (if enabled) and when its not the empty string. 
     * @param pattern the regular expression pattern
     */
    public void setRegex(String pattern) {
        this.regex = pattern;
    }

    /**
     * Returns <tt>true</tt> if this field is required when unmarshalled.  
     * Required fields must have at least one character (after
     * trimming, if enabled).  If this field is not required and no text
     * is parsed from the input stream, no further validations are performed. 
     * @return <tt>true</tt> if this field is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Set to <tt>true</tt> if this field is required when unmarshalled.
     * Required fields must have at least one character (after
     * trimming, if enabled).  If this field is not required and no text
     * is parsed from the input stream, no further validations are performed. 
     * @param required <tt>true</tt> if this field is required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Returns the length of this field in characters.  Applies to fixed
     * length and padded fields.  May return -1 (aka 'unbounded'), for the
     * last field in a fixed length record to indicate it is not padded and
     * of variable length.
     * @return the length of this field
     */
    public Integer getLength() {
        return length;
    }

    /**
     * Sets the length of this field in characters.  Applies to fixed
     * length and padded fields.
     * @param length the length of this field
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * Returns the character used to pad this field.  Defaults to a space
     * when padding is enabled.
     * @return the character used to pad this field
     */
    public Character getPadding() {
        return padding;
    }

    /**
     * Sets the character used to pad this field.  Defaults to a space
     * when padding is enabled.
     * @param padding the character used to pad this field
     */
    public void setPadding(Character padding) {
        this.padding = padding;
    }

    /**
     * Returns the justification of this field.  Defaults to <tt>left</tt>.  
     * Applies to fixed length and padded fields.
     * @return {@link #LEFT} or {@link #RIGHT}
     */
    public String getJustify() {
        return justify;
    }

    /**
     * Sets the justification of this field.  Applies to fixed length 
     * and padded fields.
     * @param justify {@link #LEFT} or {@link #RIGHT}
     */
    public void setJustify(String justify) {
        this.justify = justify;
    }

    /**
     * Returns whether a fixed length field should keep its padding
     * when unmarshalled.  Defaults to false.
     * @return true to keep padding, false otherwise
     */
    public boolean isKeepPadding() {
        return keepPadding;
    }

    /**
     * Sets whether a fixed length field should keep its padding when unmarshalled.
     * @param keepPadding true to keep field padding, false otherwise
     */
    public void setKeepPadding(boolean keepPadding) {
        this.keepPadding = keepPadding;
    }
    
    /**
     * Sets whether padding length is enforced for fixed length formatted streams.
     * @return true if not enforced, false otherwise
     * @since 2.1.0
     */
    public boolean isLenientPadding() {
        return lenientPadding;
    }

    /**
     * Sets whether the padding length is enforced for fixed length formatted streams.
     * @param lenientPadding true if not enforced, false otherwise
     * @since 2.1.0
     */
    public void setLenientPadding(boolean lenientPadding) {
        this.lenientPadding = lenientPadding;
    }
    
    /**
     * Returns true if this field is referenced by another component. 
     * @return true if referenced
     */
    public boolean isRef() {
        return ref;
    }

    /**
     * Sets whether this field is referenced by another component.
     * @param ref true if referenced
     */
    public void setRef(boolean ref) {
        this.ref = ref;
    }
}
