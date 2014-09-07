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
import org.beanio.internal.compiler.*;
import org.beanio.internal.config.*;
import org.beanio.internal.parser.*;
import org.beanio.internal.parser.format.FieldPadding;
import org.beanio.internal.parser.format.xml.*;
import org.beanio.stream.RecordParserFactory;
import org.beanio.stream.xml.XmlRecordParserFactory;

/**
 * A {@link ParserFactory} for the XML stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlParserFactory extends ParserFactorySupport {

    /**
     * Constructs a new <tt>XmlParserFactory</tt>.
     */
    public XmlParserFactory() { }
    
    // the current depth of the parser tree
    private int groupDepth = 0;
    private int maxGroupDepth = 0;
    
    @Override
    protected Preprocessor createPreprocessor(StreamConfig config) {
        return new XmlPreprocessor(config);
    }
    
    @Override
    public Stream createStream(StreamConfig config) throws BeanIOConfigurationException {
        Stream stream =  super.createStream(config);
        ((XmlStreamFormat)stream.getFormat()).setLayout(stream.getLayout());
        ((XmlStreamFormat)stream.getFormat()).setGroupDepth(maxGroupDepth);
        return stream;
    }
    
    @Override
    protected void initializeGroupMain(GroupConfig config, Property bean) {
        if (!XmlTypeConstants.XML_TYPE_NONE.equals(config.getXmlType())) {
            XmlSelectorWrapper wrapper = new XmlSelectorWrapper();
            wrapper.setName(config.getName());
            wrapper.setLocalName(config.getXmlName());
            wrapper.setNamespace(config.getXmlNamespace());
            wrapper.setNamespaceAware(config.isXmlNamespaceAware());
            wrapper.setPrefix(config.getXmlPrefix());
            wrapper.setGroup(true);
            wrapper.setDepth(groupDepth++);
            pushParser(wrapper);
            
            maxGroupDepth = Math.max(groupDepth, maxGroupDepth);
        }
        super.initializeGroupMain(config, bean);
    }
    
    @Override
    protected Property finalizeGroupMain(GroupConfig config) throws BeanIOConfigurationException {
        Property property = super.finalizeGroupMain(config);
        if (!XmlTypeConstants.XML_TYPE_NONE.equals(config.getXmlType())) {
            popParser();
            --groupDepth;
        }
        return property;
    }

    @Override
    protected void initializeRecordMain(RecordConfig config, Property bean) {
        // a record is always mapped to an XML element
        
        XmlSelectorWrapper wrapper = new XmlSelectorWrapper();
        wrapper.setName(config.getName());
        wrapper.setLocalName(config.getXmlName());
        wrapper.setNamespace(config.getXmlNamespace());
        wrapper.setNamespaceAware(config.isXmlNamespaceAware());
        wrapper.setPrefix(config.getXmlPrefix());
        wrapper.setGroup(false);
        wrapper.setDepth(groupDepth);
        pushParser(wrapper);
        
        super.initializeRecordMain(config, bean);
    }
    
    @Override
    protected Property finalizeRecordMain(RecordConfig config) throws BeanIOConfigurationException {
        Property property = super.finalizeRecordMain(config);
        popParser();
        return property;
    }
    
    @Override
    protected void finalizeRecord(RecordConfig config, Record record) {
        super.finalizeRecord(config, record);
        record.setExistencePredetermined(true);
    }
    
    @Override
    protected boolean isSegmentRequired(SegmentConfig config) {
        if (config.isConstant()) {
            return false;
        }
        if (config.getType() != null) {
            return true;
        }
        if (!XmlTypeConstants.XML_TYPE_ELEMENT.equals(config.getXmlType())) {
            return false;
        }
        if (config.getChildren().size() > 1) {
            return true;
        }
        return false;
    }
    
    @Override
    protected void initializeSegmentMain(SegmentConfig config, Property property) {
        if (isWrappingRequired(config)) {
            XmlWrapper wrapper = new XmlWrapper();
            wrapper.setName(config.getName());
            wrapper.setLocalName(config.getXmlName());
            wrapper.setNamespace(config.getXmlNamespace());
            wrapper.setNamespaceAware(config.isXmlNamespaceAware());
            wrapper.setPrefix(config.getXmlPrefix());
            wrapper.setNillable(config.isNillable());
            wrapper.setRepeating(config.isRepeating());
            wrapper.setLazy(config.getMinOccurs().equals(0));
            pushParser(wrapper);
        }
        super.initializeSegmentMain(config, property);
    }
    
    @Override
    protected Property finalizeSegmentMain(SegmentConfig config) throws BeanIOConfigurationException {
        Property property = super.finalizeSegmentMain(config);
        if (isWrappingRequired(config)) {
            popParser(); // pop the wrapper
        }
        return property;
    }
    private boolean isWrappingRequired(SegmentConfig config) {
        return XmlTypeConstants.XML_TYPE_ELEMENT.equals(config.getXmlType()) && !config.isConstant();
    }

    @Override
    protected void finalizeSegment(SegmentConfig config, Segment segment) {
        super.finalizeSegment(config, segment);
        
        // if the segment is wrapped, laziness is checked by the wrapper
        if (XmlTypeConstants.XML_TYPE_ELEMENT.equals(config.getXmlType())) {
            segment.setOptional(false);
        }
        
        segment.setExistencePredetermined(true);
    }

    @Override
    public StreamFormat createStreamFormat(StreamConfig config) {
        XmlStreamFormat format = new XmlStreamFormat();
        format.setName(config.getName());
        format.setRecordParserFactory(createRecordParserFactory(config));
        return format;
    }
    
    @Override
    public RecordFormat createRecordFormat(RecordConfig config) {
        return null;
    }

    @Override
    public FieldFormat createFieldFormat(FieldConfig config, Class<?> type) {
        XmlFieldFormat format;
        if (XmlTypeConstants.XML_TYPE_ELEMENT.equals(config.getXmlType())) {
            XmlElementField element = new XmlElementField();
            element.setLocalName(config.getXmlName());
            element.setNillable(config.isNillable());
            element.setNamespace(config.getXmlNamespace());
            element.setNamespaceAware(config.isXmlNamespaceAware());
            element.setPrefix(config.getXmlPrefix());
            element.setRepeating(config.isRepeating());
            format = element;
        }
        else if (XmlTypeConstants.XML_TYPE_ATTRIBUTE.equals(config.getXmlType())) { 
            XmlAttributeField attribute = new XmlAttributeField();
            attribute.setLocalName(config.getXmlName());
            attribute.setNamespace(config.getXmlNamespace());
            attribute.setNamespaceAware(config.isXmlNamespaceAware());
            attribute.setPrefix(config.getXmlPrefix());
            format = attribute;
        }
        else if (XmlTypeConstants.XML_TYPE_TEXT.equals(config.getXmlType())) { 
            XmlTextField text = new XmlTextField();
            format = text;
        }
        else {
            throw new IllegalStateException("Invalid xml type: " + config.getXmlType());
        }
        format.setName(config.getName());
        format.setLazy(config.getMinOccurs().equals(0));
        
        if (config.getLength() != null) {
            FieldPadding padding = new FieldPadding();
            padding.setLength(config.getLength());
            padding.setFiller(config.getPadding());
            padding.setJustify(FieldConfig.RIGHT.equals(config.getJustify()) ? FieldPadding.RIGHT : FieldPadding.LEFT);
            padding.setOptional(!config.isRequired());
            padding.setPropertyType(type);
            padding.init();
            format.setPadding(padding);
        }
        
        return format;
    }

    @Override
    protected RecordParserFactory getDefaultRecordParserFactory() {
        return new XmlRecordParserFactory();
    }
}
