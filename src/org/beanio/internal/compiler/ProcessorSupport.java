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
package org.beanio.internal.compiler;

import java.util.LinkedList;

import org.beanio.BeanIOConfigurationException;
import org.beanio.internal.config.*;

/**
 * A base class for configuration processors.  The class provides support for traversing
 * a tree of stream configuration components and generates a "stack trace" if any overridden
 * method throws a {@link BeanIOConfigurationException}. 
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class ProcessorSupport {

    private LinkedList<ComponentConfig> configStack = new LinkedList<>();
    
    /**
     * Constructs a new <tt>ProcessorSupport</tt>.
     */
    public ProcessorSupport() { }
    
    /**
     * Processes a stream configuration.
     * @throws BeanIOConfigurationException if the configuration is invalid
     */
    protected void process(StreamConfig stream) throws BeanIOConfigurationException {
        try {
            handleComponent(stream);
        }
        catch (BeanIOConfigurationException ex) {
            StringBuilder message = new StringBuilder();
            
            message.append("Invalid ");
            int index = 0;
            for (ComponentConfig node : configStack) {
                
                String type = null;
                switch (node.getComponentType()) {
                case ComponentConfig.FIELD:
                    type = "field";
                    break;
                case ComponentConfig.SEGMENT:
                    type = "segment";
                    break;
                case ComponentConfig.RECORD:
                    type = "record";
                    break;
                case ComponentConfig.GROUP:
                    type = "group";
                    break;
                case ComponentConfig.STREAM:
                    type = "stream";
                    break;
                case ComponentConfig.CONSTANT:
                    type = "property";
                    break;
                case ComponentConfig.WRAPPER:
                    type = "wrapper";
                    break;
                }
                ++index;
                
                if (index > 1) {
                    message.append(", in ");
                }
                
                message.append(type)
                    .append(" '")
                    .append(node.getName())
                    .append("'");
            }
            message.append(": ");
            message.append(ex.getMessage());
            
            throw new BeanIOConfigurationException(message.toString(), ex);
        }
    }
    
    /**
     * Recursively preprocesses a component and its descendants.
     * @param component the component to preprocess
     */
    protected void handleComponent(ComponentConfig component) {
        configStack.addFirst(component);
        
        switch (component.getComponentType()) {
        
        case ComponentConfig.STREAM:
            initializeStream((StreamConfig)component);
            for (ComponentConfig child : component) {
                handleComponent(child);
            }
            finalizeStream((StreamConfig)component);
            break;
            
        case ComponentConfig.GROUP:
            initializeGroup((GroupConfig)component);
            for (ComponentConfig child : component) {
                handleComponent(child);
            }
            finalizeGroup((GroupConfig)component);
            break;

        case ComponentConfig.RECORD:
            initializeRecord((RecordConfig)component);
            for (ComponentConfig child : component) {
                handleComponent(child);
            }
            finalizeRecord((RecordConfig) component);
            break;

        case ComponentConfig.SEGMENT:
            initializeSegment((SegmentConfig) component);
            for (ComponentConfig child : component) {
                handleComponent(child);
            }
            finalizeSegment((SegmentConfig) component);
            break;

        case ComponentConfig.FIELD:
            handleField((FieldConfig)component);
            break;
            
        case ComponentConfig.CONSTANT:
            handleConstant((ConstantConfig)component);
            break;
        }    

        configStack.removeFirst();
    }
    
    /**
     * Returns the parent component for the component being processed.
     * @return the parent component, or null if the component does
     *   not have a parent
     */
    protected ComponentConfig getParent() {
        if (configStack.size() > 1) {
            return (ComponentConfig) configStack.get(1);
        }
        return null;
    }

    /**
     * Initializes a stream configuration before its children have been processed.
     * @param stream the stream configuration to process
     */
    protected void initializeStream(StreamConfig stream) throws BeanIOConfigurationException { }
    
    /**
     * Finalizes a stream configuration after its children have been processed.
     * @param stream the stream configuration to finalize
     */
    protected void finalizeStream(StreamConfig stream) throws BeanIOConfigurationException { }
    
    /**
     * Initializes a group configuration before its children have been processed.
     * @param group the group configuration to process
     */
    protected void initializeGroup(GroupConfig group) throws BeanIOConfigurationException { }
    
    /**
     * Finalizes a group configuration after its children have been processed.
     * @param group the group configuration to finalize
     */
    protected void finalizeGroup(GroupConfig group) throws BeanIOConfigurationException { }
    
    /**
     * Initializes a record configuration before its children have been processed.
     * @param record the record configuration to process
     */
    protected void initializeRecord(RecordConfig record) throws BeanIOConfigurationException { }
    
    /**
     * Finalizes a record configuration after its children have been processed.
     * @param record the record configuration to process
     */
    protected void finalizeRecord(RecordConfig record) throws BeanIOConfigurationException { }
    
    /**
     * Initializes a segment configuration before its children have been processed.
     * @param segment the segment configuration to process
     */
    protected void initializeSegment(SegmentConfig segment) throws BeanIOConfigurationException { }
    
    /**
     * Finalizes a segment configuration after its children have been processed.
     * @param segment the segment configuration to process
     */
    protected void finalizeSegment(SegmentConfig segment) throws BeanIOConfigurationException { }

    /**
     * Processes a field configuration.
     * @param field the field configuration to process
     */
    protected void handleField(FieldConfig field) throws BeanIOConfigurationException { }
    
    /**
     * Processes a constant configuration.
     * @param constant the constant configuration to process
     */
    protected void handleConstant(ConstantConfig constant) throws BeanIOConfigurationException { }
    
}
