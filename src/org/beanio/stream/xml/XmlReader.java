/*
 * Copyright 2011 Kevin Seim
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

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.*;

import javax.xml.stream.*;

import org.beanio.internal.util.DomUtil;
import org.beanio.stream.*;
import org.w3c.dom.*;

/**
 * A <tt>XmlReader</tt> is used to read records from a XML input stream.  Each XML
 * record read from the input stream is parsed into a Document Object Model (DOM).  
 * A <tt>XmlReader</tt> is configured using a base DOM object to define the group
 * structure of the XML.  When a XML element is read from the input stream that
 * is not found in the base document, the element and its children are appended
 * to the base document to form the <i>record</i>.  The base document object model
 * will be modified as the input stream is read and should therefore not be
 * shared across multiple streams.
 * <p>
 * A <tt>XmlReader</tt> makes use of the DOM user data feature to pass additional
 * information to and from the parser.  The <tt>GROUP_COUNT</tt> is an <tt>Integer</tt> 
 * value added to elements in the base document to indicate the number of times an
 * element was read from the input stream.  And the <tt>IS_NAMESPACE_IGNORED</tt> is a
 * <tt>Boolean</tt> value set on elements in the base document where the XML namespace
 * should not be used to match nodes read from the input stream.
 * <p>
 * The method <tt>getRecordText()</tt> is not currently supported.
 * 
 * @author Kevin Seim
 * @since 1.1
 */
public class XmlReader implements RecordReader {

    /**
     * The DOM user data key to obtain the number of times a group element was
     * read in the base document as a <tt>java.lang.Integer</tt>.
     */
    public static final String GROUP_COUNT = "count";
    
    /**
     * The DOM user data key to indicate whether the namespace of an element in 
     * the base document is ignored when matching nodes read from an input stream.
     * The value must be a <tt>java.lang.Boolean</tt>. 
     */
    public static final String IS_NAMESPACE_IGNORED = "namespaceIgnored";
    
    private static final XMLInputFactory xmlInputFactory;
    static {
        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
    }
    
    /* the input stream to read from */
    private XMLStreamReader in;
    /* the base document used to define the group structure of the XML read from the input stream */
    private Document document;
    /* the parent node is the record node's parent in the base document */
    private Node parentNode;
    /* the "root" element of the last record read */
    private Node recordNode;
    /* set to true if the base document was null during construction and the XML input stream 
     * will be fully read */
    private boolean readFully = false;
    
    private transient int recordLineNumber = -1;
    private transient boolean eof = false;

    /**
     * Constructs a new <tt>XmlReader</tt>.
     * @param reader the input stream to read from
     */
    public XmlReader(Reader reader) {
        this(reader, null);
    }
    
    /**
     * Constructs a new <tt>XmlReader</tt>.
     * @param reader the input stream to read from
     * @param base the base document object model (DOM) that defines the
     *   group structure of the XML.  May be <tt>null</tt> if fully reading 
     *   the XML document.
     */
    public XmlReader(Reader reader, Document base) {
        if (reader == null) {
            throw new IllegalArgumentException("reader is null");
        }
        
        try {
            this.in = xmlInputFactory.createXMLStreamReader(reader);
        }
        catch (XMLStreamException ex) {
            throw new IllegalArgumentException("Failed to create XMLStreamReader: " + ex.getMessage(), ex);
        }
        
        if (base == null) {
            base = DomUtil.newDocument();
        }
        this.document = base;
        
        if (base.getDocumentElement() == null) {
            this.readFully = true;
            this.parentNode = base;
        }
        else {
            this.readFully = false;
            this.parentNode = null;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordReader#read()
     */
    @Override
    public Document read() throws IOException, RecordIOException {
        if (eof) {
            return null;
        }
        
        try {
            if (parentNode != null) {
                if (recordNode != null) {
                    parentNode.removeChild(recordNode);
                }
                recordNode = null;
            }

            return readRecord() ? document : null;
        }
        catch (XMLStreamException ex) {
            throw new RecordIOException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Appends the next record read from the XML stream reader to the base document object model.
     * @return <tt>true</tt> if a record was found, or <tt>false</tt> if the end of the
     *   stream was reached
     * @throws XMLStreamException
     */
    private boolean readRecord() throws XMLStreamException {
        
        // the record position stores the number of elements deep in the record, or -1 if a
        // record has not been found yet
        int recordPosition = readFully ? 0 : -1;
        
        // the parent element to the node we are reading
        Node node = parentNode;
        
        while (in.hasNext()) {
            int event = in.next();
            
            switch (event) {
            case START_ELEMENT:
                if (recordPosition < 0) {
                    // handle the root element of the document
                    if (node == null) {
                        node = document.getDocumentElement();
                        if (isNode(node, in.getNamespaceURI(), in.getLocalName())) {
                            node.setUserData(GROUP_COUNT, 1, null);
                            continue;
                        }
                    }
                    else {
                        // try to find a child in the base document that matches the element we just read
                        Element baseElement = findChild((Element)node, in.getNamespaceURI(), in.getLocalName());
                        if (baseElement != null) {
                            // if found, increment its counter and continue
                            Integer count = (Integer) baseElement.getUserData(GROUP_COUNT);
                            baseElement.setUserData(GROUP_COUNT, count == null ? 1 : 1 + count, null);
                            node = baseElement;
                            continue;
                        }
                    }
                
                    // if we find an element not included in the base document, this is the beginning of our record
                    recordLineNumber = in.getLocation().getLineNumber();
                    parentNode = node;
                }
                
                // create and append the new element to our Document
                Element e = document.createElementNS(in.getNamespaceURI(), in.getLocalName());
                for (int i=0,j=in.getAttributeCount(); i<j; i++) {
                    e.setAttributeNS(
                        in.getAttributeNamespace(i), 
                        in.getAttributeLocalName(i), 
                        in.getAttributeValue(i));
                }
                node.appendChild(e);
                node = e;
                
                // set the record node if this is the "root" element of the record
                if (recordNode == null) {
                    recordNode = node;
                }
                
                ++recordPosition;
                break;
            
            case CHARACTERS:
                if (recordPosition >= 0) {
                    node.appendChild(document.createTextNode(in.getText()));
                }
                break;
                
            case END_ELEMENT:
                Node parent = node.getParentNode();
                if (parent.getNodeType() == Node.ELEMENT_NODE) {
                    node = (Element) parent;
                }
                else {
                    node = null;
                }
                
                if (recordPosition < 0) {
                    continue;
                }
                
                // if the record position reaches 0, the record is complete
                if (recordPosition-- == 0) {
                    return true;
                }
                break;
                
            case END_DOCUMENT:
                break;
            }
        }
        
        eof = true;
        return readFully;
    }
    
    /**
     * Searches a DOM element for a child element matching the given XML namespace
     * and local name. 
     * @param parent the parent DOM element
     * @param namespace the XML namesapce to match
     * @param name the XML local name to match
     * @return the matched child element, or <tt>null</tt> if not found
     */
    private Element findChild(Element parent, String namespace, String name) {
        Node node = parent.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (isNode(element, namespace, name)) {
                    return element;
                }
            }
            node = node.getNextSibling();
        }
        return null;
    }
    
    /**
     * Returns whether a XML node matches a given namespace and local name.
     * @param node the Node to test
     * @param namespace the namespace to match
     * @param name the local name to match
     * @return <tt>true</tt> if the Node matches the given XML namespace and
     *   local name
     */
    private boolean isNode(Node node, String namespace, String name) {
        if (node.getLocalName().equals(name)) {
            if (Boolean.TRUE.equals(node.getUserData(IS_NAMESPACE_IGNORED))) {
                return true;
            }
            
            String uri = node.getNamespaceURI();
            if (namespace == null && uri == null) {
                return true;
            }
            else {
                return uri != null && uri.equals(namespace);
            }
        }
        return false;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordReader#close()
     */
    @Override
    public void close() throws IOException {
        try {
            in.close();
        }
        catch (XMLStreamException e) {
            IOException ex = new IOException("XMLStreamException caught closing input stream");
            ex.initCause(e);
            throw ex;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordReader#getRecordLineNumber()
     */
    @Override
    public int getRecordLineNumber() {
        return recordLineNumber;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordReader#getRecordText()
     */
    @Override
    public String getRecordText() {
        return null;
    }
}
