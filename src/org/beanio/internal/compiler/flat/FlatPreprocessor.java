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
package org.beanio.internal.compiler.flat;

import java.util.*;

import org.beanio.BeanIOConfigurationException;
import org.beanio.internal.compiler.Preprocessor;
import org.beanio.internal.config.*;

/**
 * Base class for {@link Preprocessor} implementations for flat stream formats 
 * (i.e. CSV, delimited, and fixed length).
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FlatPreprocessor extends Preprocessor {

    // the current default field position
    private int defaultPosition = 0;
    // position must be set for all fields or for no fields, this attribute
    // is set when the first field is processed and all other fields must adhere to it
    private Boolean positionRequired;
    
    // the field or segment that requires a maximum position set on it to test for more occurrences
    // of an indefinitely repeating field or segment
    private PropertyConfig unboundedComponent = null;
    // the component that immediately follows the unbounded component
    private PropertyConfig unboundedComponentFollower = null;
    // the list of components at the end of the record following the unbounded component
    private List<PropertyConfig> endComponents = new ArrayList<>();
    /* stack of non-record segments */
    private LinkedList<SegmentConfig> segmentStack = new LinkedList<>();
    /* list of field components belonging to a record, used for validating dynamic occurrences */
    private List<FieldConfig> fieldComponents = new ArrayList<>();
    
    /**
     * Constructs a new <tt>FlatPreprocessor</tt>.
     * @param stream
     */
    public FlatPreprocessor(StreamConfig stream) {
        super(stream);
    }
    
    @Override
    protected void initializeRecord(RecordConfig record) {
        super.initializeRecord(record);
        
        defaultPosition = 0;
        positionRequired = null;
        unboundedComponent = null;
        unboundedComponentFollower = null;
        endComponents.clear();
        fieldComponents.clear();
    }
    
    @Override
    protected void finalizeRecord(RecordConfig record) {
        super.finalizeRecord(record);
        
        boolean minSet = record.getMinLength() != null;
        if (stream.isStrict()) {
            if (record.getMinLength() == null) {
                record.setMinLength(record.getMinSize());
            }
            if (record.getMaxLength() == null) {
                record.setMaxLength(record.getMaxSize());
            }
        }
        else {
            if (record.getMinLength() == null) {
                record.setMinLength(0);
            }
            if (record.getMaxLength() == null) {
                record.setMaxLength(Integer.MAX_VALUE);
            }
        }
        // validate maximum record length is not less than the minimum record length
        if (record.getMaxLength() < record.getMinLength()) {
            if (minSet) {
                throw new BeanIOConfigurationException(
                    "Maximum record length cannot be less than minimum record length");                    
            }
            else {
                throw new BeanIOConfigurationException(
                    "Maximum record length must be at least " + record.getMinLength());
            }
        }
        
        // if there is an unbounded component in the middle of the record, we need to
        // set the end position on it
        if (unboundedComponent != null && unboundedComponentFollower != null) {
            setEndPosition(unboundedComponent, unboundedComponentFollower.getPosition());
        }
    }
    
    private void setEndPosition(ComponentConfig config, int end) {
        switch (config.getComponentType()) {
        case ComponentConfig.SEGMENT:
            for (ComponentConfig child : ((SegmentConfig)config).getChildren()) {
                setEndPosition(child, end);
            }
            break;
        case ComponentConfig.FIELD:
            ((FieldConfig)config).setUntil(end);
            break;
        }
    }
    
    @Override
    protected void initializeSegment(SegmentConfig segment) throws BeanIOConfigurationException {
        if (segment.getComponentType() == ComponentConfig.SEGMENT) {
            segmentStack.push(segment);
        }
        
        super.initializeSegment(segment);
        
        if (segment.getOccursRef() != null) {
            if (!segment.isCollection()) {
                throw new BeanIOConfigurationException("Collection required when 'occursRef' is set");
            }
            segment.setMinOccursRef(segment.getMinOccurs());
            segment.setMaxOccursRef(segment.getMaxOccurs());
            segment.setMinOccurs(1);
            segment.setMaxOccurs(1);
        }
    }

    @Override
    protected void finalizeSegment(SegmentConfig segment) {
        super.finalizeSegment(segment);
        
        PropertyConfig first = null;
        PropertyConfig last = null;
        int position = 0;
        int minSize = 0;
        int maxSize = -1;
        
        // by default, a segment is not constant
        segment.setConstant(false);
        
        boolean isRecord = segment.getComponentType() == ComponentConfig.RECORD;
        boolean isVariableSized = 
            (segment.getMaxOccurs().equals(Integer.MAX_VALUE)) ||
            (segment.isRepeating() && !segment.getMinOccurs().equals(segment.getMaxOccurs()));
        
        if (isVariableSized && !isRecord && defaultPosition == Integer.MAX_VALUE) {
            throw new BeanIOConfigurationException("A segment of indeterminate size may not " +
                "follow another component of indeterminate size");
        }
        
        // calculate the maximum size and position of the segment
        for (PropertyConfig config : segment.getPropertyList()) {
            if (config.getComponentType() == PropertyConfig.CONSTANT) {
                continue;
            }
            if (config.getComponentType() == ComponentConfig.SEGMENT &&
                ((SegmentConfig)config).isConstant()) {
                continue;
            }
            if (!isRecord && 
                segment.isRepeating() && 
                config.getMinOccurs().equals(0)) {
                throw new BeanIOConfigurationException("A repeating segment may not contain " +
                    "components where minOccurs=0");
            }
            if (config.getMaxSize() == Integer.MAX_VALUE) {
                maxSize = Integer.MAX_VALUE;
            }
            int n = config.getPosition();
            if (first == null || comparePosition(n, first.getPosition()) < 0) {
                first = config;
            }
            if (last == null || comparePosition(n, last.getPosition()) > 0) {
                last = config;
            }
        }
        if (last == null) {
            if (segment.getComponentType() == PropertyConfig.RECORD) {
                maxSize = Integer.MAX_VALUE;
            }
            else {
                segment.setConstant(true);
                maxSize = 0;
            }
        }
        else if (maxSize < 0) {
            position = first.getPosition();
            if (last.getPosition() < 0 && first.getPosition() >= 0) {
                if (!isRecord && segment.isRepeating()) {
                    throw new BeanIOConfigurationException("A repeating segment may not contain " +
                        "components of indeterminate size");
                }
                maxSize = Integer.MAX_VALUE;
                isVariableSized = true;
            }
            else if (last.getMaxOccurs() == Integer.MAX_VALUE) {
                maxSize = Integer.MAX_VALUE;
            }
            else {
                maxSize = Math.abs(last.getPosition() - first.getPosition()) + last.getMaxSize() * last.getMaxOccurs();
            }
        }

        // calculate the minimum size of the segment
        if (last != null) {
            first = null;
            last = null;
            
            for (PropertyConfig config : segment.getPropertyList()) {
                if (config.getComponentType() == PropertyConfig.CONSTANT) {
                    continue;
                }
                
                minSize += config.getMinSize() * config.getMinOccurs();
                
                int n = config.getPosition();
                if (first == null || comparePosition(n, first.getPosition()) < 0) {
                    first = config;
                }
                if (config.getMinOccurs() > 0) {
                    if (last == null || comparePosition(n, last.getPosition()) > 0) {
                        last = config;
                    }
                }
            }
         
            if (last == null) {
                last = first;
            }
            
            if (first.getPosition() >= 0 && last.getPosition() < 0) {
                // go with counted min size
            }
            else {
                minSize = Math.abs(last.getPosition() - first.getPosition()) + last.getMaxSize() * last.getMinOccurs();
            }
        }
        
        segment.setPosition(position);
        segment.setMaxSize(maxSize);
        segment.setMinSize(minSize);
        
        // calculate the next position
        if (!isRecord && Boolean.FALSE.equals(positionRequired)) {
            if (defaultPosition == Integer.MAX_VALUE) {
                // if the unbound component is a descendant of this segment, it should
                // not affect the next default position
                if (!segment.isDescendant(unboundedComponent)) {
                    int offset = 0 - maxSize * (segment.getMaxOccurs() - 1);
                    for (PropertyConfig c : endComponents) {
                        c.setPosition(c.getPosition() + offset);
                    }
                    segment.setPosition(offset + segment.getPosition());
                    endComponents.add(segment);
                    
                    if (unboundedComponentFollower == null) {
                        unboundedComponentFollower = segment;
                    }
                }
            }
            else if (
                (segment.isRepeating() && !segment.getMinOccurs().equals(segment.getMaxOccurs())) ||
                segment.getMaxOccurs().equals(Integer.MAX_VALUE) ||
                segment.getMaxSize() == Integer.MAX_VALUE) {
                    
                if (unboundedComponent == null) {
                    unboundedComponent = segment;
                }
                defaultPosition = Integer.MAX_VALUE;
            }
            else {
                defaultPosition = segment.getPosition() + segment.getMaxSize() * segment.getMaxOccurs();
            }
        }
        
        // determine the default existence of the segment
        boolean defaultExistence = true;
        for (PropertyConfig child : segment.getPropertyList()) {
            if (child.getComponentType() == PropertyConfig.CONSTANT) {
                continue;
            }
            if (child.getComponentType() == PropertyConfig.SEGMENT) {
                if (((SegmentConfig)child).getDefaultExistence()) {
                    continue;
                }
            }
            defaultExistence = false;
        }
        segment.setDefaultExistence(defaultExistence);
        
        if (segment.getDefaultExistence() && !segment.getMinOccurs().equals(segment.getMaxOccurs())) {
            throw new BeanIOConfigurationException("Repeating segments without any child " +
                "field component must have minOccurs=maxOccurs");
        }
        
        handleOccursRef(segment);
        
        if (segment.getComponentType() == ComponentConfig.SEGMENT) {
            segmentStack.pop();
        }
    }
    
    //  1,  0 returns 1 (greater than)
    // -1, -2 returns 1 (greater than)
    //  5, -1 returns -1 (less than)
    private int comparePosition(Integer p1, Integer p2) {
        if (p1 > 0 && p2 < 0) {
            return -1;
        }
        else if (p1 < 0 && p2 >= 0) {
            return 1;
        }
        else {
            return p1.compareTo(p2);
        }
    }
    
    @Override
    protected void handleField(FieldConfig field) {
        super.handleField(field);

        if (field.getOccursRef() != null) {
            if (!field.isCollection()) {
                throw new BeanIOConfigurationException("Collection required when 'occursRef' is set");
            }
            field.setMinOccursRef(field.getMinOccurs());
            field.setMaxOccursRef(field.getMaxOccurs());
            field.setMinOccurs(1);
            field.setMaxOccurs(1);
        }
        
        // validate and configure padding
        if (isFixedLength()) {
            // if a literal is set and length is not
            if (field.getLiteral() != null) {
                if (field.getLength() == null) {
                    field.setLength(field.getLiteral().length());
                }
                else if (field.getLiteral().length() > field.getLength()) {
                    throw new BeanIOConfigurationException("literal size exceeds the field length");
                }
            }
            else if (field.getLength() == null) {
                throw new BeanIOConfigurationException("length required for fixed length fields");
            }
        }
        else {
            if (Integer.valueOf(-1).equals(field.getLength())) {
                field.setLength(null);
            }
        }
        // default the padding character to a single space
        if (field.getLength() != null) {
            if (field.getPadding() == null) {
                field.setPadding(' ');
            }
        }
        
        // calculate the size of the field
        int size = getSize(field);
        if (size == -1) {
            field.setMinSize(0);
            field.setMaxSize(Integer.MAX_VALUE);
        }
        else {
            field.setMaxSize(size);
            field.setMinSize(size);
        }
        
        // calculate the position of this field (size must be calculated first)
        if (positionRequired == null) {
            positionRequired = field.getPosition() != null;
        }
        else if (positionRequired ^ (field.getPosition() != null)) {
            throw new BeanIOConfigurationException("position must be declared for all the fields " +
                "in a record, or for none of them (in which case, all fields must be configured in the " +
                "order they will appear in the stream)");
        }
        if (field.getPosition() != null) {
            field.setPosition(field.getPosition() + getSegmentOffset());
        }
        if (field.getPosition() == null) {
            calculateDefaultPosition(field);
        }
        else if (field.getUntil() != null) {
            if (!isVariableSized(field)) {
                throw new BeanIOConfigurationException("until should not be specified for " +
                    "fields of determinate occurences and length");
            }
            if (field.getUntil() >= 0) {
                throw new BeanIOConfigurationException("until must be less than 0 (i.e. " +
                    "a position relative to the end of the record)");
            }
        }
        
        handleOccursRef(field);
        
        fieldComponents.add(field);
    }
    
    private void handleOccursRef(PropertyConfig config) {
        if (config.getOccursRef() != null) {
            // search in reverse to find the most recent field in case multiple
            // fields share the same name (gc0080)
            FieldConfig occurs = null;
            for (int i = fieldComponents.size() - 1; i >= 0; i--) {
                FieldConfig fc = fieldComponents.get(i);
                if (fc.getName().equals(config.getOccursRef())) {
                    occurs = fc;
                    break;
                }
            }
            if (occurs == null) {
                throw new BeanIOConfigurationException("Referenced field '" + config.getOccursRef() +
                    "' not found");
            }
            if (occurs.getCollection() != null) {
                throw new BeanIOConfigurationException("Referenced field '" + config.getOccursRef() +
                    "' may not repeat");
            }
            if (occurs.getPosition() >= config.getPosition()) {
                throw new BeanIOConfigurationException("Referenced field '" + config.getOccursRef() +
                    "' must precede this field");
            }
            // default occurs to an Integer if not set...
            if (occurs.getType() == null && 
                occurs.getTypeHandler() == null &&
                occurs.getTypeHandlerInstance() == null) {
                occurs.setType(Integer.class.getName());
            }
            if (occurs.isRef() && !occurs.isBound()) {
                throw new BeanIOConfigurationException("Unbound field '" + occurs.getName() +
                    "' cannot be referenced more than once");
            }
            occurs.setRef(true);
        }
    }
    
    private int getSegmentOffset() {
        int offset = 0;
        for (SegmentConfig s : segmentStack) {
            if (s.getPosition() != null) {
                offset += s.getPosition();
            }
        }
        return offset;
    }
    
    private boolean isVariableSized(FieldConfig config) {
        return
            (config.getMaxOccurs().equals(Integer.MAX_VALUE)) ||
            (isFixedLength() && config.getMaxSize() == Integer.MAX_VALUE) ||
            (config.isRepeating() && !config.getMinOccurs().equals(config.getMaxOccurs()));
    }
    
    /**
     * Calculates and sets the default field position.
     * @param config the field configuration to calculate the position for
     */
    private void calculateDefaultPosition(FieldConfig config) {
        
        boolean isVariableSized = isVariableSized(config);
        
        if (defaultPosition == Integer.MAX_VALUE) {
            if (isVariableSized)  {
                String error = "Cannot determine field position, field is preceded by " +
                    "another component with indeterminate occurrences";
            
                if (isFixedLength()) {
                    error += "or unbounded length";
                }
                
                throw new BeanIOConfigurationException(error);
            }
            
            int offset = 0 - (config.getMaxSize() * config.getMaxOccurs());
            for (PropertyConfig c : endComponents) {
                c.setPosition(c.getPosition() + offset);
            }
            config.setPosition(offset);
            endComponents.add(config);
            
            if (unboundedComponentFollower == null) {
                unboundedComponentFollower = config;
            }
        }
        else {
            config.setPosition(defaultPosition);
            
            if (isVariableSized) {
                defaultPosition = Integer.MAX_VALUE;
                unboundedComponent = config;
            }
            else {
                defaultPosition = config.getPosition() + config.getMaxSize() * config.getMaxOccurs();
            }
        }
        
        /*
        if (defaultPosition == Integer.MAX_VALUE) {
            String error = "Cannot determine field position, field is preceded by " +
                "a component with indeterminate occurences";
            
            if (isFixedLength()) {
                error += "or unbounded length";
            }
            
            throw new BeanIOConfigurationException(error);
        }
        
        // set the next default position to MAX_VALUE if the occurrences of this field is unbounded
        if (config.getMaxOccurs().equals(Integer.MAX_VALUE)) {
            defaultPosition = Integer.MAX_VALUE;
        }
        else if (isFixedLength() && config.getMaxSize() == Integer.MAX_VALUE) {
            defaultPosition = Integer.MAX_VALUE;
        }
        // or if the number of occurrence is indeterminate
        else if (config.isRepeating() && !config.getMinOccurs().equals(config.getMaxOccurs())) {
            defaultPosition = Integer.MAX_VALUE;
        }
        else {
            defaultPosition = config.getPosition() + config.getMaxSize() * config.getMaxOccurs();
        }
        */
    }
    
    /**
     * Returns the size of a field.
     * @param field the field to size
     * @return the field size
     */
    protected int getSize(FieldConfig field) {
        return isFixedLength() ? field.getLength() : 1;
    }
    
    /**
     * Returns whether the stream format is fixed length.
     * @return true if fixed length, false otherwise
     */
    protected boolean isFixedLength() {
        return false;
    }
}
