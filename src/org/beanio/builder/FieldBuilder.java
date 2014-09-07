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

import org.beanio.internal.config.FieldConfig;
import org.beanio.types.TypeHandler;

/**
 * Builds a new field configuration.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public class FieldBuilder extends PropertyBuilderSupport<FieldBuilder> {

    protected FieldConfig config = new FieldConfig();
    
    /**
     * Constructs a new FieldBuilder.
     * @param name the field name
     */
    public FieldBuilder(String name) {
        config.setName(name);
        config.setBound(true);
    }
    
    @Override
    protected FieldBuilder me() {
        return this;
    }

    @Override
    protected FieldConfig getConfig() {
        return config;
    }
    
    /**
     * Indicates this field is used to identify the record.
     * @return this
     */
    public FieldBuilder rid() {
        config.setIdentifier(true);
        return this;
    }
    
    /**
     * Sets the position of the field.
     * @param at the position
     * @return this
     */
    public FieldBuilder at(int at) {
        config.setPosition(at);
        return this;
    }
    
    /**
     * Sets the maximum position of this field if it repeats an
     * indeterminate number of times
     * @param until the maximum position
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder until(int until) {
        config.setUntil(until);
        return this;
    }
    
    /**
     * Indicates the field text should be trimmed before validation and type conversion.
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder trim() {
        config.setTrim(true);
        return this;
    }
    
    /**
     * Indicates this field is required and must contain at least one character.
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder required() {
        config.setRequired(true);
        return this;
    }
    
    /**
     * Indicates the number of occurrences of this field is governed by another field.
     * @param ref the name of the field that governs the occurrences of this field
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder occursRef(String ref) {
        config.setOccursRef(ref);
        return this;
    }
    
    /**
     * Sets the minimum expected length of the field text.
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder minLength(int n) {
        config.setMinLength(n);
        return this;
    }
    
    /**
     * Sets the maximum expected length of the field text.
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder maxLength(int n) {
        config.setMaxLength(n);
        return this;
    }
    
    /**
     * Sets the regular expression the field text must match.
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder regex(String pattern) {
        config.setRegex(pattern);
        return this;
    }
    
    /**
     * Sets the literal text the field text must match.
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder literal(String literal) {
        config.setLiteral(literal);
        return this;
    }
    
    /**
     * Sets the default value of this field.
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder defaultValue(String value) {
        config.setDefault(value);
        return this;
    }
    
    /**
     * Sets the pattern used to format this field by the type handler.
     * @param pattern the pattern
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder format(String pattern) {
        config.setFormat(pattern);
        return this;
    }
    
    /**
     * Indicates this field is not bound to a property of the class assigned
     * to its parent record or segment.
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder ignore() {
        config.setBound(false);
        return this;
    }
    
    /**
     * Sets the padded length of this field.
     * @param length the length
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder length(int length) {
        config.setLength(length);
        return this;
    }
    
    /**
     * Sets the character used to pad this field.  Defaults to a space.
     * @param c the padding character
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder padding(char c) {
        config.setPadding(c);
        return this;
    }
    
    /**
     * Indicates this field should not be unpadded during unmarshalling.
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder keepPadding() {
        config.setKeepPadding(true);
        return this;
    }
    
    /**
     * Indicates the padding length should not be enforced for this field.  Only
     * applies to fixed length formatted streams.
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder lenientPadding() {
        config.setLenientPadding(true);
        return this;
    }
    
    /**
     * Sets the alignment or justification of this field if padded.
     * @param align the alignment
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder align(Align align) {
        config.setJustify(align == null ? null : align.toString().toLowerCase());
        return this;
    }
    
    /**
     * Indicates this field is nillable for XML formatted streams.
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder nillable() {
        config.setNillable(true);
        return this;
    }
    
    /**
     * Sets the type handler used for parsing and formatting field text.
     * @param name the type handler name
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder typeHandler(String name) {
        config.setTypeHandler(name);
        return this;
    }
    
    /**
     * Sets the type handler used for parsing and formatting field text.
     * @param handler the type handler class
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder typeHandler(Class<? extends TypeHandler> handler) {
        config.setTypeHandler(handler == null ? null : handler.getName());
        return this;
    }
    
    /**
     * Sets the type handler used for parsing and formatting field text.
     * @param handler the {@link TypeHandler}
     * @return this {@link FieldBuilder}
     */
    public FieldBuilder typeHandler(TypeHandler handler) {
        config.setTypeHandlerInstance(handler);
        return this;
    }
    
    /**
     * Builds this field.
     * @return the field configuration
     */
    public FieldConfig build() {
        return config;
    }
}
