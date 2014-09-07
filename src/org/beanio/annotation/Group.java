package org.beanio.annotation;

import java.lang.annotation.*;

import org.beanio.builder.XmlType;

/**
 * Group annotation for classes, and for fields and methods in a class
 * annotated by a parent Group.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface Group {

    /**
     * The group name.
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
     * The group type, if it cannot be determined from the annotated
     * field or method declaration.
     * @return the class type
     */
    Class<?> type() default Void.class;
    
    /**
     * The collection type for repeating group bound to a parent group class, if
     * it cannot be determined from the annotated field or method declaration.
     * @return the collection type
     */
    Class<?> collection() default Void.class;
    
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
