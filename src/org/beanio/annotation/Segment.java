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
import java.util.Map;

import org.beanio.builder.XmlType;

/**
 * Segment annotation applied to class members or methods.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Segment {

    /**
     * The segment name.
     * @return the segment name
     */
    String name() default "";
    
    /**
     * The absolute position of the segment.
     * @return the absolute position
     */
    int at() default Integer.MIN_VALUE;
    
    /**
     * The maximum position of a segment that repeats for
     * an indeterminate number of times.
     * @return the maximum position
     */
    int until() default Integer.MIN_VALUE;
    
    /**
     * The relative position of the segment.
     * @return the relative position
     */
    int ordinal() default Integer.MIN_VALUE;
    
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
     * The class bound to this segment, if one cannot be derived
     * from the annotated field or method.
     * @return the class
     */
    Class<?> type() default Void.class;
    
    /**
     * Whether the class bound to this segment should be instantiated
     * if all child fields are null or empty strings.  Also causes empty
     * collections to returned as null.
     * @return whether the segment is lazily created
     */
    boolean lazy() default false;
    
    /**
     * The collection class bound to this segment, if one cannot be derived
     * from the annotated field or method.
     * @return the collection class
     */
    Class<?> collection() default Void.class;
    
    /**
     * The minimum occurrences.
     * @return the minimum occurrences
     */
    int minOccurs() default Integer.MIN_VALUE;
    
    /**
     * The maximum occurrences.
     * @return the maximum occurrences, or -1 if unbounded
     */
    int maxOccurs() default Integer.MIN_VALUE;
    
    /**
     * The name of a preceding field that governs the number of occurrences 
     * of this segment.  Does not apply to XML formatted streams.
     * @return the name of the field
     */
    String occursRef() default "";
    
    /**
     * The name of a child component to use for the key value if this segment
     * is bound to a {@link Map}.
     * @return the component name of the key
     */
    String key() default "";
    
    /**
     * The name of a child component to use for the value of this segment in
     * lieu of a type.
     * @return the component name of the value
     */
    String value() default "";
    
    /**
     * The XML type of this segment.
     * @return the {@link XmlType}
     */
    XmlType xmlType() default XmlType.DEFAULT;
    
    /**
     * The XML attribute or element name.
     * @return the XML name
     */
    String xmlName() default AnnotationConstants.UNDEFINED;
    
    /**
     * The XML namespace prefix of this segment.
     * @return the namespace prefix
     */
    String xmlPrefix() default AnnotationConstants.UNDEFINED;
    
    /**
     * The XML namespace URI of this segment.
     * @return the namespace URI
     */
    String xmlNamespace() default AnnotationConstants.UNDEFINED;
    
    /**
     * Whether the element is nillable.
     * @return true if nillable, false otherwise
     */
    boolean nillable() default false;
}
