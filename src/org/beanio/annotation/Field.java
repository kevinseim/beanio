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
package org.beanio.annotation;

import java.lang.annotation.*;

import org.beanio.builder.*;
import org.beanio.types.TypeHandler;

/**
 * Field annotation applied to class attributes, methods or constructor parameters.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Field {
    
    /**
     * The field name.
     * @return the field name
     */
    String name() default "";
    
    /**
     * Sets the absolute position of the field.
     * @return the absolute position
     */
    int at() default Integer.MIN_VALUE;
    
    /**
     * Sets the maximum position of a field that repeats for
     * an indeterminate number of times.
     * @return the maximum position
     */
    int until() default Integer.MIN_VALUE;

    /**
     * The relative position of the field.
     * @return the relative position
     */
    int ordinal() default Integer.MIN_VALUE;
    
    /**
     * The padded length of the field.
     * @return the length
     */
    int length() default Integer.MIN_VALUE;
    
    /**
     * The character used to pad the field.
     * @return the character
     */
    int padding() default Integer.MIN_VALUE;
    
    /**
     * Whether to keep the field padding during unmarshalling.  Only
     * applies to fixed length formatted streams.
     * @return true to keep padding, false otherwise
     */
    boolean keepPadding() default false;
    
    /**
     * Whether to enforce the padding length during unmarshalling.  Only
     * applies to fixed length formatted streams.
     * @return true if not enforced, false otherwise
     */
    boolean lenientPadding() default false;
    
    /**
     * The alignment of a padded field.
     * @return {@link Align}
     */
    Align align() default Align.LEFT;
    
    /**
     * The getter method.
     * @return the getter method name
     */
    String getter() default "";
    
    /**
     * The setter method.
     * @return the setter method name
     */
    String setter() default "";
    
    /**
     * The field type, if it can not be detected from the method
     * or field declaration.
     * @return the field type
     */
    Class<?> type() default Void.class;
    
    /**
     * The {@link TypeHandler} implementation class for this field.
     * @return the type handler class
     */
    Class<?> handlerClass() default Void.class;
    
    /**
     * The name of a registered {@link TypeHandler}.
     * @return the type handler name
     */
    String handlerName() default "";
    
    /**
     * The format passed to the {@link TypeHandler}.
     * @return the format pattern
     */
    String format() default "";
    
    /**
     * Whether to trim the field text before validation and type handling.
     * @return true to trim, false otherwise
     */
    boolean trim() default false;
    
    /**
     * Whether the field is used to identify the record.
     * @return true if it identifies the record, false otherwise
     */
    boolean rid() default false;
    
    /**
     * The regular expression for validating and/or matching field text.
     * @return the pattern
     */
    String regex() default "";
    
    /**
     * The literal text for validating or matching field text.
     * @return the expected literal text
     */
    String literal() default "";
    
    /**
     * Whether field text is required.
     * @return true if field text must be at least one character (after trimming if enabled),
     *   or false otherwise
     */
    boolean required() default false;
    
    /**
     * The default value for this field.  The value is parsed into a Java object
     * using the assigned type handler.
     */
    String defaultValue() default "";
    
    /**
     * The minimum length of the field text (after trimming if enabled).
     * @return the minimum length
     */
    int minLength() default Integer.MIN_VALUE;
    
    /**
     * The maximum length of the field text (after trimming if enabled).
     * @return the maximum length
     */
    int maxLength() default Integer.MIN_VALUE;
    
    /**
     * The collection type for repeating fields, if it cannot be detected from
     * the field or method declaration.
     * @return the collection type
     */
    Class<?> collection() default Void.class;
    
    /**
     * Whether an empty string should be converted to null, or null
     * returned for an empty collection.
     * @return whether the field collection is lazily created
     */
    boolean lazy() default false;
    
    /**
     * The minimum occurrences of the field.
     * @return the minimum occurrences
     */
    int minOccurs() default Integer.MIN_VALUE;
    
    /**
     * The maximum occurrences of the field if it repeats.
     * @return the maximum occurrences, or -1 if unbounded
     */
    int maxOccurs() default Integer.MIN_VALUE;
    
    /**
     * The name of a preceding field that governs the number of occurrences 
     * of this field.  Does not apply to XML formatted streams.
     * @return the name of the field
     */
    String occursRef() default "";
    
    /**
     * The XML type of this field.
     * @return the {@link XmlType}
     */
    XmlType xmlType() default XmlType.DEFAULT;
    
    /**
     * The XML attribute or element name.
     * @return the XML name
     */
    String xmlName() default AnnotationConstants.UNDEFINED;
    
    /**
     * The XML namespace prefix of this field.
     * @return the namespace prefix
     */
    String xmlPrefix() default AnnotationConstants.UNDEFINED;
    
    /**
     * The XML namespace URI of this field.
     * @return the namespace URI
     */
    String xmlNamespace() default AnnotationConstants.UNDEFINED;
    
    /**
     * Whether the element is nillable.
     * @return true if nillable, false otherwise
     */
    boolean nillable() default false;
}
