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
package org.beanio.internal.compiler;

import java.util.Comparator;

import org.beanio.BeanIOConfigurationException;
import org.beanio.internal.config.*;
import org.beanio.internal.util.Settings;

/**
 * A Preprocesser is responsible for validating a stream configuration, setting
 * default configuration values, and populating any calculated values before the
 * {@link ParserFactorySupport} compiles the configuration into parser components.  
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class Preprocessor extends ProcessorSupport {

    private static final Settings settings = Settings.getInstance();
    private static final boolean SORT_XML_COMPONENTS = settings.getBoolean(Settings.SORT_XML_COMPONENTS_BY_POSITION);
    
    protected StreamConfig stream;
    protected PropertyConfig propertyRoot;
    private boolean recordIgnored;
    
    /**
     * Constructs a new <tt>Preprocessor</tt>.
     * @param stream the stream configuration to preprocess
     */
    public Preprocessor(StreamConfig stream) {
        this.stream = stream;
    }
    
    /**
     * Initializes a stream configuration before its children have been processed.
     * @param stream the stream configuration to process
     */
    @Override
    protected void initializeStream(StreamConfig stream) throws BeanIOConfigurationException { 
        if (stream.getMinOccurs() == null) {
            stream.setMinOccurs(0);
        }
        if (stream.getMaxOccurs() == null) {
            stream.setMaxOccurs(1);
        }
        if (stream.getMaxOccurs() <= 0) {
            throw new BeanIOConfigurationException("Maximum occurrences must be greater than 0");
        }
        
        initializeGroup(stream);
    }
    
    /**
     * Finalizes a stream configuration after its children have been processed.
     * @param stream the stream configuration to finalize
     */
    @Override
    protected void finalizeStream(StreamConfig stream) throws BeanIOConfigurationException { 
        finalizeGroup(stream);
        
        boolean sorted = true;
        if ("xml".equals(stream.getFormat()) && !SORT_XML_COMPONENTS) {
            sorted = false;
        }
        
        if (sorted) {
            stream.sort(new Comparator<ComponentConfig>() {
                @Override
                public int compare(ComponentConfig c1, ComponentConfig c2) {
                    Integer p1 = getPosition(c1);
                    Integer p2 = getPosition(c2);
                    
                    if (p1 == null) {
                        if (p2 == null) {
                            return 0;
                        }
                        else {
                            return 1;
                        }
                    }
                    else if (p2 == null) {
                        return -1;
                    }
                    else {
                        return p1.compareTo(p2);
                    }
                }
                
                private Integer getPosition(ComponentConfig c) {
                    Integer p = null;
                    switch (c.getComponentType()) {
                    case ComponentConfig.FIELD:
                    case ComponentConfig.SEGMENT:
                        p = ((PropertyConfig)c).getPosition();
                        break;
                    case ComponentConfig.RECORD:
                        p = ((RecordConfig)c).getOrder();
                        break;
                    case ComponentConfig.GROUP:
                        p = ((GroupConfig)c).getOrder();
                        break;
                    }
                    if (p != null && p.compareTo(0) < 0) {
                        p = Integer.MAX_VALUE + p;
                    }
                    return p;
                }
            });
        }
    }
    
    /**
     * Initializes a group configuration before its children have been processed.
     * @param group the group configuration to process
     */
    @Override
    protected void initializeGroup(GroupConfig group) throws BeanIOConfigurationException {
        
        if (group.getMinOccurs() == null) {
            group.setMinOccurs(settings.getInt(Settings.DEFAULT_GROUP_MIN_OCCURS, 0));
        }
        if (group.getMaxOccurs() == null) {
            group.setMaxOccurs(Integer.MAX_VALUE);
        }
        if (group.getMaxOccurs() <= 0) {
            throw new BeanIOConfigurationException("Maximum occurrences must be greater than 0");
        }
        // validate occurrences
        if (group.getMaxOccurs() < group.getMinOccurs()) {
            throw new BeanIOConfigurationException("Maximum occurences cannot be less than mininum occurences");
        }
        
        // validate both 'class' and 'target' aren't set
        if (group.getType() != null && group.getTarget() != null) {
            throw new BeanIOConfigurationException("Cannot set both 'class' and 'value'");
        }

        if (propertyRoot != null) {
            group.setBound(true);
            
            if (group.getCollection() != null && group.getType() == null) {
                throw new BeanIOConfigurationException("Class required if collection is set");
            }
            if (group.getType() != null && 
                group.getMaxOccurs() > 1 &&
                group.getCollection() == null) {
                throw new BeanIOConfigurationException("Collection required when maxOccurs is greater than 1 and class is set");
            }
            if (group.isRepeating() && group.getCollection() == null) {
                group.setBound(false);
            }            
        }
        
        if (propertyRoot == null && (group.getType() != null || group.getTarget() != null)) {
            propertyRoot = group;
        }
    }
    
    /**
     * Finalizes a group configuration after its children have been processed.
     * @param group the group configuration to finalize
     */
    @Override
    protected void finalizeGroup(GroupConfig group) throws BeanIOConfigurationException {
        
        // order must be set for all group children, or for none of them
        // if order is specified...
        //   -validate group children are in ascending order
        // otherwise if order is not specified...
        //   -if strict, all children have current order incremented
        //   -if not, all children have order set to 1
        
        int lastOrder = 0;
        Boolean orderSet = null;
        for (ComponentConfig node : group.getChildren()) {
            SelectorConfig child = (SelectorConfig)node;
            
            String typeDescription = child.getComponentType() == ComponentConfig.RECORD ? "record" : "group";

            if (child.getOrder() != null && child.getOrder() < 0) {
                throw new BeanIOConfigurationException("Order must be 1 or greater");
            }
            
            if (orderSet == null) {
                orderSet = child.getOrder() != null;
            }
            else if (orderSet ^ (child.getOrder() != null)) {
                throw new BeanIOConfigurationException(
                    "Order must be set all children at a group level, or none at all");                
            }
            
            if (orderSet) {
                if (child.getOrder() < lastOrder) {
                    throw new BeanIOConfigurationException("'" + child.getName() + 
                        "' " + typeDescription + " configuration is out of order");   
                }
                lastOrder = child.getOrder();
            }
            else {
                if (stream.isStrict()) {
                    child.setOrder(++lastOrder);
                }
                else {
                    child.setOrder(1);
                }
            }
        }
        
        if (propertyRoot == group) {
            propertyRoot = null;
        }
    }
    
    /**
     * Initializes a record configuration before its children have been processed.
     * @param record the record configuration to process
     */
    @Override
    protected void initializeRecord(RecordConfig record) throws BeanIOConfigurationException {
        
        // a record is ignored if a 'class' was not set and the property root is null
        // or the record repeats
        recordIgnored = false;
        if (record.getType() == null && record.getTarget() == null) {
            if (propertyRoot == null || record.isRepeating()) {
                recordIgnored = true;
            }
        }
        
        // assign default min and max occurs
        if (record.getMinOccurs() == null) {
            record.setMinOccurs(settings.getInt(Settings.DEFAULT_RECORD_MIN_OCCURS, 0));
        }
        if (record.getMaxOccurs() == null) {
            record.setMaxOccurs(Integer.MAX_VALUE);
        }
        if (record.getMaxOccurs() <= 0) {
            throw new BeanIOConfigurationException("Maximum occurrences must be greater than 0");
        }
        
        if (propertyRoot == null) {
            propertyRoot = record;
            
            if (record.isLazy()) {
            	throw new BeanIOConfigurationException("Lazy cannot be true for unbound records");
            }
        }
        
        initializeSegment(record);
    }
    
    /**
     * Finalizes a record configuration after its children have been processed.
     * @param record the record configuration to process
     */
    @Override
    protected void finalizeRecord(RecordConfig record) throws BeanIOConfigurationException {
        finalizeSegment(record);
        
        if (propertyRoot == record) {
            propertyRoot = null;
        }
    }
    
    /**
     * Initializes a segment configuration before its children have been processed.
     * @param segment the segment configuration to process
     */
    @Override
    protected void initializeSegment(SegmentConfig segment) throws BeanIOConfigurationException {

        if (segment.getName() == null) {
            throw new BeanIOConfigurationException("name must be set");
        }
        if (segment.getLabel() == null) {
            segment.setLabel(segment.getName());
        }
        
        // validate both 'class' and 'target' aren't set
        if (segment.getType() != null && segment.getTarget() != null) {
            throw new BeanIOConfigurationException("Cannot set both 'class' and 'value'");
        }
        
        // set default occurrences and validate
        if (segment.getMinOccurs() == null) {
            segment.setMinOccurs(segment.getOccursRef() != null ? 0 : 1);
        }
        if (segment.getMaxOccurs() == null) {
            segment.setMaxOccurs(segment.getOccursRef() != null ? Integer.MAX_VALUE : 1);
        }
        if (segment.getMaxOccurs() <= 0) {
            throw new BeanIOConfigurationException("Maximum occurrences must be greater than 0");
        }
        if (segment.getMaxOccurs() < segment.getMinOccurs()) {
            throw new BeanIOConfigurationException("Maximum occurrences cannot be less than mininum occurrences");
        }
        
        if (segment.getKey() != null && segment.getCollection() == null) {
            throw new BeanIOConfigurationException("Unexpected key value when collection not set");
        }
        if (segment.getCollection() != null && segment.getType() == null && segment.getTarget() == null) {
            throw new BeanIOConfigurationException("Class or value required if collection is set");
        }
        
        if (propertyRoot == null || propertyRoot != segment) {
            segment.setBound(true);
            
            if (segment.getMaxOccurs() > 1 && segment.getCollection() == null) {
            	if (segment.getType() != null) {
            		throw new BeanIOConfigurationException("Collection required when maxOccurs is greater than 1 and class is set");
            	}
            	if (segment.getTarget() != null) {
            		throw new BeanIOConfigurationException("Collection required when maxOccurs is greater than 1 and value is set");
            	}
            }

            if (segment.getComponentType() == ComponentConfig.RECORD &&
                segment.isRepeating() &&
                segment.getType() == null &&
                segment.getTarget() == null) {
                segment.setBound(false);
            }
        }
        else {
            if (segment.getCollection() != null) {
                throw new BeanIOConfigurationException("Collection cannot be set on unbound record or segment.");
            }
        }
    }
    
    /**
     * Finalizes a segment configuration after its children have been processed.
     * @param segment the segment configuration to process
     */
    @Override
    protected void finalizeSegment(SegmentConfig segment) throws BeanIOConfigurationException {
        for (PropertyConfig child : segment.getPropertyList()) {
            if (child.isIdentifier()) {
                segment.setIdentifier(true);
                break;
            }
        }
    }
    
    /**
     * Processes a field configuration.
     * @param field the field configuration to process
     */
    @Override
    protected void handleField(FieldConfig field) throws BeanIOConfigurationException {
        // ignore fields that belong to ignored records
        if (recordIgnored) {
            field.setBound(false);
        }
        
        if (field.getName() == null) {
            throw new BeanIOConfigurationException("name is required");
        }
        if (field.getLabel() == null) {
            field.setLabel(field.getName());
        }
        
        // set and validate occurrences
        if (field.getMinOccurs() == null) {
            field.setMinOccurs(field.getOccursRef() != null ? 0 : settings.getInt(Settings.DEFAULT_FIELD_MIN_OCCURS + 
                "." + stream.getFormat(), 0));
        }
        if (field.getMaxOccurs() == null) {
            field.setMaxOccurs(field.getOccursRef() != null ? Integer.MAX_VALUE : Math.max(field.getMinOccurs(), 1));
        }
        if (field.getMaxOccurs() <= 0) {
            throw new BeanIOConfigurationException("Maximum occurrences must be greater than 0");
        }
        if (field.getMaxOccurs() < field.getMinOccurs()) {
            throw new BeanIOConfigurationException("Maximum occurrences cannot be less than minimum occurrences");
        }
        
        // set and validate min and max length
        if (field.getMinLength() == null) {
            field.setMinLength(0);
        }
        if (field.getMaxLength() == null) {
            field.setMaxLength(Integer.MAX_VALUE);
        }
        if (field.getMaxLength() < field.getMinLength()) {
            throw new BeanIOConfigurationException("maxLength must be greater than or equal to minLength");
        }
        if (field.getLiteral() != null) {
            int literalLength = field.getLiteral().length();
            if (literalLength < field.getMinLength()) {
                throw new BeanIOConfigurationException("literal text length is less than minLength");
            }
            if (literalLength > field.getMaxLength()) {
                throw new BeanIOConfigurationException("literal text length is greater than maxLength");
            }
        }
        
        if (field.isRepeating() && field.isIdentifier()) {
            throw new BeanIOConfigurationException("repeating fields cannot be " +
                "used as identifiers");   
        }
        
        if (field.isBound() && field.isRepeating() && field.getCollection() == null) {
            throw new BeanIOConfigurationException("collection not set");
        }
        
        if (field.isIdentifier()) {
            validateRecordIdentifyingCriteria(field);
        }
    }
    
    /**
     * Processes a constant configuration.
     * @param constant the constant configuration to process
     */
    @Override
    protected void handleConstant(ConstantConfig constant) throws BeanIOConfigurationException {
        constant.setBound(true);
        
        if (constant.getName() == null) {
            throw new BeanIOConfigurationException("Missing property name");
        }
    }
    
    /**
     * This method validates a record identifying field has a literal or regular expression
     * configured for identifying a record.
     * @param field the record identifying field configuration to validate
     */
    protected void validateRecordIdentifyingCriteria(FieldConfig field) throws BeanIOConfigurationException {
        // validate regex or literal is configured for record identifying fields
        if (field.getLiteral() == null && field.getRegex() == null) {
            throw new BeanIOConfigurationException("Literal or regex pattern required " +
                "for identifying fields");
        }
    }
}
