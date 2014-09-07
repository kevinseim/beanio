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

import org.beanio.builder.XmlType;

/**
 * Record annotation for classes, and for fields and methods in a class
 * annotated by Group.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface Record {

    /**
     * The record name.
     * @return the record name
     */
    String name() default "";
    
    /**
     * The getter method on its parent group class.
     * @return the method name
     */
    String getter() default "";
    
    /**
     * The setter method on its parent group class.
     * @return the method name
     */
    String setter() default "";
    
    /**
     * The record type, if it cannot be determined from the annotated
     * field or method declaration.
     * @return the class type
     */
    Class<?> type() default Void.class;
    
    /**
     * The collection type for repeating records bound to a group class, if
     * it cannot be determined from the annotated field or method declaration.
     * @return the collection type
     */
    Class<?> collection() default Void.class;
    
    /**
     * The minimum length of this record used to identify it.
     * @return the minimum length
     */
    int minRidLength() default Integer.MIN_VALUE;
    
    /**
     * The maximum length of this record used to identify it.
     * @return the maximum length
     */
    int maxRidLength() default Integer.MIN_VALUE;
    
    /**
     * The validated minimum length of the record.
     * @return the minimum length
     */
    int minLength() default Integer.MIN_VALUE;
    
    /**
     * The validated maximum length of the record.
     * @return the maximum length
     */
    int maxLength() default Integer.MIN_VALUE;
    
    /**
     * The minimum occurrences of the record
     * @return the minimum occurrences
     */
    int minOccurs() default Integer.MIN_VALUE;
    
    /**
     * The maximum occurrences of the record.
     * @return the maximum occurrences
     */
    int maxOccurs() default Integer.MIN_VALUE;
    
    /**
     * The order of this record within its parent group.
     * @return the order
     */
    int order() default Integer.MIN_VALUE;
    
    /**
     * The name of child component to use for the value of this record in
     * lieu of a type.
     * @return the component name of the value
     */
    String value() default "";
    
    /**
     * The XML type of this record.
     * @return the {@link XmlType}
     */
    XmlType xmlType() default XmlType.DEFAULT;
    
    /**
     * The XML attribute or element name.
     * @return the XML name
     */
    String xmlName() default AnnotationConstants.UNDEFINED;
    
    /**
     * The XML namespace prefix of this record.
     * @return the namespace prefix
     */
    String xmlPrefix() default AnnotationConstants.UNDEFINED;
    
    /**
     * The XML namespace URI of this record.
     * @return the namespace URI
     */
    String xmlNamespace() default AnnotationConstants.UNDEFINED;
}
