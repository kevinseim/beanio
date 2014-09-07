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
package org.beanio.internal.compiler.xml;

import org.beanio.BeanIOConfigurationException;
import org.beanio.internal.compiler.Preprocessor;
import org.beanio.internal.config.*;
import org.beanio.internal.util.Settings;

/**
 * Configuration {@link Preprocessor} for an XML stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlPreprocessor extends Preprocessor {
    
    /**
     * Constructs a new <tt>XmlPreprocessor</tt>.
     * @param stream the stream configuration to pre-process
     */
    public XmlPreprocessor(StreamConfig stream) {
        super(stream);
    }
    
    @Override
    protected void initializeGroup(GroupConfig group) {
        super.initializeGroup(group);
        
        if (group.getXmlName() == null) {
            group.setXmlName(group.getName());
        }
        
        String type = group.getXmlType();
        if (type == null) {
            group.setXmlType(XmlTypeConstants.XML_TYPE_ELEMENT);
        }
        else {
            if (!XmlTypeConstants.XML_TYPE_NONE.equals(type) &&
                !XmlTypeConstants.XML_TYPE_ELEMENT.equals(type)) {
                throw new BeanIOConfigurationException("Invalid xmlType '" + type + "'");
            }
        }
        
        if (group.getXmlNamespace() == null) {
            ComponentConfig parent = getParent();
            if (parent != null) {
                group.setXmlPrefix(parent.getXmlPrefix());
                group.setXmlNamespace(parent.getXmlNamespace());
                group.setXmlNamespaceAware(parent.isXmlNamespaceAware());
            }
            else {
                group.setXmlPrefix(null);
                group.setXmlNamespace(null);
                group.setXmlNamespaceAware(false);
            }
        }
        else if ("*".equals(group.getXmlNamespace())) {
            group.setXmlPrefix(null);
            group.setXmlNamespace(null);
            group.setXmlNamespaceAware(false);      
        }
        else if ("".equals(group.getXmlNamespace())) {
            group.setXmlPrefix(null);
            group.setXmlNamespace(null);
            group.setXmlNamespaceAware(true);
        }
        else {
            group.setXmlNamespaceAware(true);
        }
    }

    @Override
    protected void initializeSegment(SegmentConfig segment) {
        super.initializeSegment(segment);

        if (segment.getXmlName() == null) {
            segment.setXmlName(segment.getName());
        }
        
        String type = segment.getXmlType();
        if (type == null) {
            segment.setXmlType(XmlTypeConstants.XML_TYPE_ELEMENT);
        }
        else {
            if (!XmlTypeConstants.XML_TYPE_NONE.equals(type) &&
                !XmlTypeConstants.XML_TYPE_ELEMENT.equals(type)) {
                throw new BeanIOConfigurationException("Invalid xmlType '" + type + "'");
            }
        }
        
        if (segment.getXmlPrefix() != null) {
            if (segment.getXmlNamespace() == null) {
                throw new BeanIOConfigurationException("Missing namespace for configured XML prefix");
            }
        }
        
        if (segment.getXmlNamespace() == null) {
            ComponentConfig parent = getParent();
            segment.setXmlPrefix(parent.getXmlPrefix());
            segment.setXmlNamespace(parent.getXmlNamespace());
            segment.setXmlNamespaceAware(parent.isXmlNamespaceAware());
        }
        else if ("*".equals(segment.getXmlNamespace())) {
            segment.setXmlPrefix(null);
            segment.setXmlNamespace(null);
            segment.setXmlNamespaceAware(false);      
        }
        else if ("".equals(segment.getXmlNamespace())) {
            segment.setXmlPrefix(null);
            segment.setXmlNamespace(null);
            segment.setXmlNamespaceAware(true);
        }
        else {
            segment.setXmlNamespaceAware(true);
        }
    }
    
    @Override
    protected void handleField(FieldConfig field) {
        
        // default the xml name to the field name
        if (field.getXmlName() == null) {
            field.setXmlName(field.getName());
        }
        
        String type = field.getXmlType();
        if (type == null) {
            type = Settings.getInstance().getProperty(Settings.DEFAULT_XML_TYPE);
            field.setXmlType(type);
        }
        if (!XmlTypeConstants.XML_TYPE_NONE.equals(type) &&
            !XmlTypeConstants.XML_TYPE_ELEMENT.equals(type) &&
            !XmlTypeConstants.XML_TYPE_ATTRIBUTE.equals(type) &&
            !XmlTypeConstants.XML_TYPE_TEXT.equals(type)) {
            throw new BeanIOConfigurationException("Invalid xmlType '" + type + "'");
        }
        // repeating fields must be of type 'element'
        if (field.isRepeating() && !XmlTypeConstants.XML_TYPE_ELEMENT.equals(type)) {
            throw new BeanIOConfigurationException("Repeating fields must have xmlType 'element'");
        }

        if (field.getXmlNamespace() != null &&
            !XmlTypeConstants.XML_TYPE_ELEMENT.equals(type) &&
            !XmlTypeConstants.XML_TYPE_ATTRIBUTE.equals(type)) {
            throw new BeanIOConfigurationException(
                "XML namespace is not applicable for xmlType '" + type + "'");
        }
        // if the bean/field/record is nillable, it must be of type 'element'
        if (field.isNillable() && !XmlTypeConstants.XML_TYPE_ELEMENT.equals(type))  {
            throw new BeanIOConfigurationException("xmlType '" + type + "' is not nillable");
        }
        
        // validate a namespace is set if a prefix is set
        if (field.getXmlPrefix() != null) {
            if (field.getXmlNamespace() == null) {
                throw new BeanIOConfigurationException("Missing namespace for configured XML prefix");
            }
        }
        
        final boolean isAttribute = XmlTypeConstants.XML_TYPE_ATTRIBUTE.equals(type);
        
        if (field.getXmlNamespace() == null) {
            ComponentConfig parent = getParent();
            if (isAttribute) {
                field.setXmlPrefix(null);
                field.setXmlNamespace(null);
                field.setXmlNamespaceAware(false);
            }
            else {
                field.setXmlPrefix(parent.getXmlPrefix());
                field.setXmlNamespace(parent.getXmlNamespace());
                field.setXmlNamespaceAware(parent.isXmlNamespaceAware());
            }
        }
        else if ("*".equals(field.getXmlNamespace())) {
            field.setXmlPrefix(null);
            field.setXmlNamespace(null);
            field.setXmlNamespaceAware(false);      
        }
        else if ("".equals(field.getXmlNamespace())) {
            field.setXmlPrefix(null);
            field.setXmlNamespace(null);
            field.setXmlNamespaceAware(true);
        }
        else {
            field.setXmlNamespaceAware(true);
        }
        
        // default minOccurs for an attribute is 0
        if (isAttribute) {
            if (field.getMinOccurs() == null) {
                field.setMinOccurs(0);
            }
        }
        
        super.handleField(field);
    }

    /*
     * An XML record identifying field of type element or attribute does not need a literal
     * or regular expression configured to identify the record (since the presence of the named
     * field may sufficiently identify the record).
     */
    @Override
    protected void validateRecordIdentifyingCriteria(FieldConfig field) {
        if (XmlTypeConstants.XML_TYPE_TEXT.equals(field.getXmlType())) {
            super.validateRecordIdentifyingCriteria(field);
        }
    }
}
