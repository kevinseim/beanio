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
package org.beanio.internal.config;

/**
 * A base class for configuration components that can be bound to a property
 * of a bean object. 
 *
 * <p>The following attributes are set during compilation, and are meant for 
 * internal use only:
 * <ul>
 * <li>minSize</li>
 * <li>maxSize</li>
 * </ul>
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class PropertyConfig extends ComponentConfig {
    
    public static final String JSON_TYPE_NONE = "none";
    public static final String JSON_TYPE_ARRAY = "array";
    public static final String JSON_TYPE_OBJECT = "object";
    public static final String JSON_TYPE_BOOLEAN = "boolean";
    public static final String JSON_TYPE_NUMBER = "number";
    public static final String JSON_TYPE_STRING = "string";
    
    private String label;
    private String type;
    private String getter;
    private String setter;
    private boolean bound;
    private boolean identifier;
    private boolean lazy;
    
    private Integer position;
    private Integer until;
    private Integer minOccurs;
    private Integer maxOccurs;
    private String occursRef;
    private String collection;
    
    /* attributes specific to xml */
    private String xmlType;
    private boolean nillable;
    
    /* attributes specific to JSON */
    private String jsonName;
    private String jsonType;
    private boolean jsonArray = false; // derived
    private int jsonArrayIndex = -1; // derived
    
    /* derived attributes */
    private int minSize;
    private int maxSize;
    private Integer minOccursRef;
    private Integer maxOccursRef;
    
    /**
     * Constructs a new <tt>PropertyConfig</tt>.
     */
    public PropertyConfig() { }

    /**
     * Returns the component name used for identification in error handling.
     * Defaults to getName() if not set.
     * @return the component name
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the component name used for identification in error handling.
     * @param label the component name
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Returns the fully qualified class name or type alias of this property.
     * By default, <tt>null</tt> is returned and the property value type
     * is detected through bean introspection.
     * @return the class name of this property value
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the fully qualified class name or type alias of this property.
     * If set to <tt>null</tt>, the property value type will be detected 
     * through bean introspection if possible.
     * @param type the class name of this property value
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Returns the name of the getter method for retrieving this property's
     * value from its parent bean object during marshalling.
     * @return the getter method for this property
     */
    public String getGetter() {
        return getter;
    }

    /**
     * Sets the name of the getter method for retrieving this property's
     * value from it parent bean object during marshalling.  If <tt>null</tt>,
     * the getter method may be discovered through bean introspection if possible.
     * @param getter the getter method for this property
     */
    public void setGetter(String getter) {
        this.getter = getter;
    }

    /**
     * Returns the name of the setter method to use when setting this property's
     * value on its parent bean object during unmarshalling.
     * @return the setter method for this property
     */
    public String getSetter() {
        return setter;
    }

    /**
     * Sets the name of the setter method to use when setting this property's
     * value on its parent bean object during unmarshalling.  If <tt>null</tt>,
     * the setter method may be discovered through bean introspection if possible.
     * @param setter the setter method for this property
     */
    public void setSetter(String setter) {
        this.setter = setter;
    }

    /**
     * Returns whether this property is bound to its parent bean object.
     * @return true if bound, false otherwise
     */
    public boolean isBound() {
        return bound;
    }

    /**
     * Sets whether this property is bound to its parent bean object.
     * @param bound true if bound, false otherwise
     */
    public void setBound(boolean bound) {
        this.bound = bound;
    }
    
    /**
     * Returns whether the class assigned to this segment should only be instantiated
     * if at least one child element is not null or the empty string, or if null should
     * be returned for an empty collection.
     * @return whether this component is lazy
     */
    public boolean isLazy() {
        return lazy;
    }

    /**
     * Sets whether the property assigned to this segment should only be instantiated
     * if at least one child element is not null or the empty string, or if null should
     * be returned instead for an empty collection.
     * @param lazy the new lazy value
     */
    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }
    
    /**
     * Returns the position of this component.  A negative number is
     * counted from the end of the record (e.g. -1 is the last field
     * in the record).  
     * 
     * <p>For delimited record formats,
     * the position is the index (beginning at 0) of this component in the 
     * record.  For fixed length record formats, the position is the index
     * of the first character in the component.
     * 
     * <p>A negative number is counted from the end of the record.
     * 
     * @return the field position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * Sets the position of this component.  A negative number is counted 
     * from the end of the record (e.g. -1 is the last field
     * in the record).  
     * 
     * <p>For delimited record formats,
     * the position is the index (beginning at 0) of this component in the 
     * record.  For fixed length record formats, the position is the index
     * of the first character in the component.
     * 
     * @param position the field position
     */
    public void setPosition(Integer position) {
        this.position = position;
    }
    
    /**
     * Returns the excluded maximum position of this component which may be 
     * specified for components that repeat indefinitely,  A negative
     * number is counted from the end of the record.
     * @return the maximum position 
     */
    public Integer getUntil() {
        return until;
    }

    /**
     * Sets the excluded maximum position of this component which may be 
     * specified for components that repeat indefinitely,  A negative
     * number is counted from the end of the record.
     * @param until the maximum position
     */
    public void setUntil(Integer until) {
        this.until = until;
    }

    /**
     * Returns the collection type, or <tt>null</tt> if this component
     * is not bound to a collection or array. 
     * @return the collection type
     */
    public String getCollection() {
        return collection;
    }

    /**
     * Sets the collection type.  Set to <tt>null</tt> (default) to indicate
     * this component is not bound to a collection or array.  The value may be set to the 
     * fully qualified class name of a <tt>java.util.Collection</tt> subclass or a 
     * collection type alias, or the value "<tt>array</tt>" to indicate a Java array.
     * @param collection the collection type
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    /**
     * Returns the minimum number of times this component must appear in the stream.
     * @return the minimum occurrences of this component
     */
    public Integer getMinOccurs() {
        return minOccurs;
    }

    /**
     * Sets the minimum number of times this component must consecutively appear in a
     * stream.  If set to any value greater than one, a collection type is expected.  
     * Must be 0 or greater.
     * @param minOccurs the minimum occurrences of this component
     */
    public void setMinOccurs(Integer minOccurs) {
        this.minOccurs = minOccurs;
    }

    /**
     * Returns the maximum number of times this component may consecutively appear in
     * a stream.  If <tt>null</tt>, one occurrence is assumed.
     * @return the maximum occurrences of this component, or <tt>-1</tt> to 
     *   indicate no limit
     */
    public Integer getMaxOccurs() {
        return maxOccurs;
    }

    /**
     * Sets the maximum number of times this component may consecutively appear in
     * a stream.  If set to <tt>null</tt>, one occurrence is assumed.  If set to
     * any value greater than one, a collection type is expected.  Must be greater
     * than the minimum occurrences, or set to <tt>-1</tt> to indicate the limit is
     * unbounded.
     * @param maxOccurs the maximum occurrences of this component, or <tt>-1</tt> to
     *   indicate no limit
     */
    public void setMaxOccurs(Integer maxOccurs) {
        this.maxOccurs = maxOccurs;
    }
    
    /**
     * Returns the name of a field in the same record that indicates the number
     * of occurrences for this component.
     * @return the field name
     */
    public String getOccursRef() {
        return occursRef;
    }

    /**
     * Sets the name of a field in the same record that indicates the number
     * of occurrences for this component.
     * @param occursRef the field name
     */
    public void setOccursRef(String occursRef) {
        this.occursRef = occursRef;
    }

    /**
     * Returns the minimum required value of the referenced occurs field.
     * @return the minimum value
     */
    public Integer getMinOccursRef() {
        return minOccursRef;
    }

    /**
     * Sets the minimum required value of the referenced occurs field.
     * @param minOccursRef the minimum value
     */
    public void setMinOccursRef(Integer minOccursRef) {
        this.minOccursRef = minOccursRef;
    }

    /**
     * Returns the maximum allowed value of the referenced occurs field.
     * @return the maximum value
     */
    public Integer getMaxOccursRef() {
        return maxOccursRef;
    }

    /**
     * Sets the maximum allowed value of the referenced occurs field.
     * @param maxOccursRef the maximum value
     */
    public void setMaxOccursRef(Integer maxOccursRef) {
        this.maxOccursRef = maxOccursRef;
    }

    /**
     * Returns the XML node type of this component.
     * @return the XML node type
     * @see XmlTypeConstants
     */
    public String getXmlType() {
        return xmlType;
    }

    /**
     * Sets the XML node type of this component.
     * @param xmlType the XML node type
     * @see XmlTypeConstants
     */
    public void setXmlType(String xmlType) {
        this.xmlType = xmlType;
    }
    
    /**
     * Returns whether this component is nillable.
     * @return <tt>true</tt> if this component is nillable
     */
    public boolean isNillable() {
        return nillable;
    }

    /**
     * Sets whether this component is nillable.
     * @param nillable <tt>true</tt> if this component is nillable
     */
    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }
    
    /**
     * Returns the JSON field name if different that the property name.
     * Ignored if its parent is a JSON array.
     * @return the JSON field name
     */
    public String getJsonName() {
        return jsonName;
    }

    /**
     * Sets the JSON field name.
     * @param jsonName the JSON field name
     */
    public void setJsonName(String jsonName) {
        this.jsonName = jsonName;
    }

    /**
     * Returns the JSON type.
     * @return the JSON type
     */
    public String getJsonType() {
        return jsonType;
    }

    /**
     * Sets the JSON type.
     * @param jsonType the JSON type
     */
    public void setJsonType(String jsonType) {
        this.jsonType = jsonType;
    }
    
    /**
     * Returns whether the property is mapped to a JSON array.  Set internally
     * by BeanIO.
     * @return true if this property is mapped to a JSON array, false otherwise
     */
    public boolean isJsonArray() {
        return jsonArray;
    }

    /**
     * Sets whether this property is mapped to a JSON array.  Set internally by
     * BeanIO.
     * @param jsonArray true if this property is mapped to a JSON array, false otherwise
     */
    public void setJsonArray(boolean jsonArray) {
        this.jsonArray = jsonArray;
    }

    /**
     * Returns the index of this property in its parent JSON array.
     * @return the array index, or -1 if not applicable
     */
    public int getJsonArrayIndex() {
        return jsonArrayIndex;
    }

    /**
     * Sets the index of this property in its parent JSON array.
     * @param jsonArrayIndex the array index, or -1 if not applicable
     */
    public void setJsonArrayIndex(int jsonArrayIndex) {
        this.jsonArrayIndex = jsonArrayIndex;
    }

    /**
     * Returns whether this component is used to identify a record during
     * unmarshalling or a bean during marshalling.  If this component is
     * a record or segment, true is returned if any descendent is used for
     * identification.
     * @return <tt>true</tt> if this component is used for identification
     */
    public boolean isIdentifier() {
        return identifier;
    }

    /**
     * Sets whether this component is used to identify a record during
     * unmarshalling or a bean during marshalling.
     * @param b <tt>true</tt> if this component is used for identification
     */
    public void setIdentifier(boolean b) {
        this.identifier = b;
    }

    /**
     * Returns the minimum size of this component (based on its field length
     * or the field length of its descendants).
     * @return the minimum size of this component
     */
    public int getMinSize() {
        return minSize;
    }

    /**
     * Sets the minimum size of this component (based on its field length
     * or the field length of its descendants).
     * @param minSize the minimum size of this component
     */
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }
    
    /**
     * Returns the maximum size of one occurrence of this component (based on its field length
     * or the field length of its descendants).
     * @return the maximum size of this component
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the maximum size of one occurrence of this component (based on its field length
     * or the field length of its descendants).
     * @param maxSize the maximum size of this component
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    /**
     * Returns whether this component is bound to a collection or array.
     * @return true if this component is bound to a collection or array
     */
    public boolean isCollection() {
        return collection != null;
    }
    
    /**
     * Returns whether this component repeats in a stream.  The component
     * is assumed to repeat if bound to a collection or the maximum
     * occurrences is greater than one.
     * @return true if this component repeats
     */
    public boolean isRepeating() {
        return isCollection() || (maxOccurs != null && maxOccurs > 1);
    }
    
    /**
     * Returns the name of the property descendant to use for the
     * Map key when <tt>collection</tt> is set to <tt>map</tt>.
     * @return the key property name
     */
    public String getKey() {
        return null;
    }
    
    @Override
    protected boolean isSupportedChild(ComponentConfig child) {
        return false;
    }
}
