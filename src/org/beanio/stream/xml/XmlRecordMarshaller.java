/*
 * Copyright 2012-2013 Kevin Seim
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
package org.beanio.stream.xml;

import java.io.StringWriter;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.stream.*;

import org.beanio.internal.util.Settings;
import org.beanio.stream.*;
import org.w3c.dom.*;

/**
 * A {@link RecordMarshaller} implementation for XML formatted records.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlRecordMarshaller implements RecordMarshaller {
    
    private static final String DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");
    private static final XMLOutputFactory xmlOutputFactory;
    static {
        xmlOutputFactory = XMLOutputFactory.newInstance();
    }
    
    /* XML parser settings */
    private XmlParserConfiguration config;
    /* String used to indent new lines of XML */
    private String indentation = "";
    
    private int level = 0;
    private ElementStack elementStack;
    /* whether a XML header needs to be output before writing a record */
    private boolean outputHeader = false;
    /* the next index to try when auto generating a namespace prefix */
    private int namespaceCount = 0;
    /* Map of auto-generated namespace prefixes to namespaces */
    private Map<String,String> namespaceMap = new HashMap<>();

    /**
     * Constructs a new <tt>XmlRecordMarshaller</tt>.
     */
    public XmlRecordMarshaller() { 
        this(null);
    }
    
    /**
     * Constructs a new <tt>XmlRecordMarshaller</tt>.
     * @param config the {@link XmlParserConfiguration}
     */
    public XmlRecordMarshaller(XmlParserConfiguration config) { 
        if (config == null) {
            // create a default configuration
            this.config = new XmlParserConfiguration();
        }
        else {
            // the configuration is cloned to prevent changes during execution
            this.config = config.clone();
        }
        init();
    }
    
    /**
     * Initializes this writer after the configuration has been set.
     */
    private void init() {
        if (config.getLineSeparator() == null) {
            config.setLineSeparator(DEFAULT_LINE_SEPARATOR);
        }
        
        if (config.isIndentationEnabled()) {
            StringBuilder b = new StringBuilder();
            for (int i=0; i<config.getIndentation(); i++) {
                b.append(' ');
            }
            this.indentation = b.toString();
        }
        
        this.outputHeader = !config.isSuppressHeader();
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordMarshaller#marshal(java.lang.Object)
     */
    @Override
    public String marshal(Object record) throws RecordIOException {
        try {
            return marshal((Document)record);
        }
        catch (XMLStreamException ex) {
            throw new RecordIOException("Failed to marshal XML record: " + ex.getMessage(), ex);
        }
    }   
    
    /**
     * Marshals a {@link Document}.
     * @param document the {@link Document} to marshal
     * @return the marshalled record text
     * @throws XMLStreamException
     */
    protected String marshal(Document document) throws XMLStreamException {
     
        StringWriter output = new StringWriter();
        XMLStreamWriter out;
        try {
            out = xmlOutputFactory.createXMLStreamWriter(output);
        }
        catch (XMLStreamException e) {
            throw new IllegalStateException("Failed to create XMLStreamWriter: " + e.getMessage(), e);
        }
        
        // write the XMl header if needed
        if (outputHeader) {
            String encoding = config.getEncoding();
            if (encoding != null && encoding.length() != 0) {
                out.writeStartDocument(encoding, config.getVersion());
            }
            else {
                out.writeStartDocument(config.getVersion());
            }
            if (config.isIndentationEnabled()) {
                out.writeCharacters(config.getLineSeparator());
            }
        }
        
        write(out, document.getDocumentElement(), config.isIndentationEnabled());
        
        return output.toString();
    }
    
    
    /**
     * Recursively writes an element to the XML stream writer.
     * @param element the DOM element to write
     * @param indentationEnabled set to <tt>true</tt> if indentation is enabled
     * @throws XMLStreamException
     */
    private void write(XMLStreamWriter out, Element element, boolean indentationEnabled) throws XMLStreamException {
        
        String name = element.getLocalName();
        String prefix = element.getPrefix();
        String namespace = element.getNamespaceURI();
        
        boolean ignoreNamespace = false;
        if (namespace == null) {
            if (Boolean.TRUE.equals(element.getUserData(XmlWriter.IS_NAMESPACE_IGNORED))) {
                prefix = null;
                ignoreNamespace = true;
            }
            namespace = "";
        }
        
        boolean setDefaultNamespace = false;
        if (prefix == null && !ignoreNamespace) {
            if (Boolean.TRUE.equals(element.getUserData(XmlWriter.IS_DEFAULT_NAMESPACE))) {
                setDefaultNamespace = true;
            }
        }
        
        // flag indicating if the element is empty or not
        boolean empty = false;
        // flag for lazily appending to stack
        boolean pendingStackUpdate = true;

        // start the element
        if (elementStack == null) {
            if (ignoreNamespace) {
                out.writeStartElement(name);
            }
            else if (prefix != null) {
                out.writeStartElement(prefix, name, namespace);
                out.writeNamespace(prefix, namespace);
            }
            else {
                out.writeStartElement(name);
                out.writeDefaultNamespace(namespace);
            }
            
            push(namespace, prefix, name);
            for (Map.Entry<String,String> ns : config.getNamespaceMap().entrySet()) {
                out.writeNamespace(ns.getKey(), ns.getValue());
                elementStack.addNamespace(ns.getKey(), ns.getValue());
            }
            
            pendingStackUpdate = false;
        }
        else {
            if (indentationEnabled) {
                newLine(out);
            }
            
            empty = !element.hasChildNodes();
            
            if (ignoreNamespace || (elementStack.isDefaultNamespace(namespace)) && prefix == null) {
                if (empty) {
                    out.writeEmptyElement(name);   
                }
                else {
                    out.writeStartElement(name);
                }
                namespace = elementStack.getDefaultNamespace();
                prefix = null;
            }
            else {
                String p = elementStack.findPrefix(namespace);
                
                boolean declareNamespace = false;
                if (p == null) {
                    declareNamespace = true;
                }
                else if (prefix == null && !setDefaultNamespace) {
                    prefix = p;
                }
                
                if (prefix == null) {
                    if (empty) {
                        out.writeEmptyElement(name);
                    }
                    else {
                        out.writeStartElement(name);
                    }
                }
                else {
                    if (empty) {
                        out.writeEmptyElement(prefix, name, namespace);
                    }
                    else {
                        out.writeStartElement(prefix, name, namespace);
                    }
                }
                
                if (setDefaultNamespace) {
                    out.writeDefaultNamespace(namespace);
                }
                else if (declareNamespace) {
                    out.writeNamespace(prefix, namespace);
                }
            }
        }
        
        // write attributes
        Set<String> attPrefixSet = null;
        NamedNodeMap map = element.getAttributes();
        if (map.getLength() > 0) {
            if (pendingStackUpdate) {
                push(namespace, prefix, name);
                pendingStackUpdate = false;
            }
        }
        for (int i=0,j=map.getLength(); i<j; i++) {
            Attr att = (Attr) map.item(i);
            String attName = att.getLocalName();
            String attNamespace = att.getNamespaceURI();
            String attPrefix = att.getPrefix();
            
            if (attNamespace == null) {
                out.writeAttribute(attName, att.getValue());
            }
            else {
                String p = elementStack.findPrefix(attNamespace);
                
                boolean declareNamespace = false;
                if (p == null) {
                    if (attPrefix == null) {
                        attPrefix = namespaceMap.get(attNamespace);
                        if (attPrefix == null) {
                            attPrefix = createNamespace(attNamespace);
                        }
                    }    
                    
                    if (attPrefixSet == null || !attPrefixSet.contains(attPrefix)) {
                        declareNamespace = true;
                    }
                }
                else if (attPrefix == null) {
                    attPrefix = p;
                }
                
                if (declareNamespace) {
                    out.writeNamespace(attPrefix, attNamespace);
                    if (attPrefixSet == null) {
                        attPrefixSet = new HashSet<>();
                    }
                    attPrefixSet.add(attPrefix);
                }
                
                out.writeAttribute(attPrefix, attNamespace, attName, att.getValue());
            }
        }
        
        // if the element contains text, we disable indentation 
        if (indentationEnabled) {
            Node child = element.getFirstChild();
            while (child != null) {
                if (child.getNodeType() == Node.TEXT_NODE) {
                    indentationEnabled = false;
                    break;
                }
                child = child.getNextSibling();
            }
        }
        
        boolean isParent = false;
        
        // write children
        Node child = element.getFirstChild();
        while (child != null) {
            switch (child.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if (pendingStackUpdate) {
                        push(namespace, prefix, name);
                        pendingStackUpdate = false;
                    }
                    
                    write(out, (Element) child, indentationEnabled);
                    isParent = true;
                    break;
                
                case Node.TEXT_NODE:
                    out.writeCharacters(((Text)child).getData());
                    break;
                    
                default:
                    break;
            }
            child = child.getNextSibling();
        }
        
        if (!pendingStackUpdate) {
            pop();
        }
        if (!empty) {
            if (isParent && indentationEnabled) {
                newLine(out);
            }
            out.writeEndElement();
        }
    }

    /**
     * Auto generates a prefix for a given namespace uri.
     * @param uri the namespace uri
     * @return the unique auto generated namespace prefix
     */
    private String createNamespace(String uri) {
        String prefix;
        if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(uri)) {
            prefix = Settings.getInstance().getProperty(Settings.DEFAULT_XSI_NAMESPACE_PREFIX);
        }
        else {
            prefix = "ns" + (++namespaceCount);
        }
        
        while (namespaceMap.containsValue(prefix)) {
            prefix = "ns" + (++namespaceCount);
        } 
        
        namespaceMap.put(uri, prefix);
        return prefix;
    }
    
    /**
     * Terminates the current line and indents the start of the next line.
     * @throws XMLStreamException
     */
    private void newLine(XMLStreamWriter out) throws XMLStreamException {
        if (config.isIndentationEnabled()) {
            out.writeCharacters(config.getLineSeparator());
            for (int i=0,j=level; i<j; i++) {
                out.writeCharacters(indentation);
            }
        }
    }
    
    private void push(String namespace, String prefix, String name) {
        push(new ElementStack(elementStack, namespace, prefix, name));
    }
    
    private void push(ElementStack e) {
        elementStack = e;
        ++level;
    }
    
    private ElementStack pop() {
        --level;
        
        ElementStack e = elementStack;
        elementStack = elementStack.getParent();
        return e;
    }
}
