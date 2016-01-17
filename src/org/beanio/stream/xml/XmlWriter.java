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
package org.beanio.stream.xml;

import java.io.*;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.stream.*;

import org.beanio.internal.util.*;
import org.beanio.stream.RecordWriter;
import org.w3c.dom.*;

/**
 * A <tt>XmlWriter</tt> is used to write records to a XML output stream.  A document
 * object model (DOM) is used to represent a record.  Group elements, as indicated
 * by a user data key (see below), are not closed when a record is written.  When
 * <tt>write(null)</tt> is called, an open group element is closed.  Finally, calling
 * <tt>flush()</tt> will close all remaining group elements and complete the document.
 * <p>
 * A <tt>XmlWriter</tt> makes use of the DOM user data feature to pass additional
 * information to and from the parser.  The <tt>IS_GROUP_ELEMENT</tt> user data is 
 * a <tt>Boolean</tt> value added to an element to indicate the element is group.  
 * And the <tt>IS_NAMESPACE_IGNORED</tt> user data is a <tt>Boolean</tt> value set on 
 * elements where the XML namespace should be ignored when writing to the output stream.
 * 
 * @author Kevin Seim
 * @since 1.1
 */
public class XmlWriter implements RecordWriter, StatefulWriter {

    /**
     * The DOM user data key to indicate whether the namespace of a DOM element
     * should be ignored when writing to the output stream.  The value must be 
     * of type {@link Boolean}.
     */
    public static final String IS_NAMESPACE_IGNORED = "isNamespaceIgnored";
    
    /**
     * The DOM user data key to indicate whether the declared namespace should
     * override the default namespace.  The value must be of type {@link Boolean}.
     */
    public static final String IS_DEFAULT_NAMESPACE = "isDefaultNamespace";
    
    /**
     * The DOM user data key to indicate a DOM element is a group element and should
     * be left "open" when the record is written to the output stream.  The value must 
     * of type <tt>java.lang.Boolean</tt>. 
     */
    public static final String IS_GROUP_ELEMENT = "isGroup";

    private static final boolean DELTA_ENABLED = "true".equals(Settings.getInstance().getProperty(
        Settings.XML_WRITER_UPDATE_STATE_USING_DELTA));
    
    private static final String DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");
    private static final XMLOutputFactory xmlOutputFactory;
    static {
        xmlOutputFactory = XMLOutputFactory.newInstance();
    }
    
    /* map keys for storing state information for implementing StatefulWriter */
    private static final String OUTPUT_HEADER_KEY = "header";
    private static final String NAMESPACE_MAP_KEY = "nsMap";
    private static final String LEVEL_KEY = "level";
    private static final String STACK_ELEMENT_KEY = "xml";
    private static final String STACK_NS_MAP_KEY = "nsMap";
    
    /* The underlying writer */
    private Writer writer;
    /* The XML stream writer to write to */
    private XMLStreamWriter out;
    /* XML parser configuration */
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
    
    /* the minimum level last stored when the state was updated */
    private int dirtyLevel = 0;
    /* flag used to suppress output during state restoration */
    private boolean suppressOutput = false;
    
    /**
     * Constructs a new <tt>XmlWriter</tt>.
     * @param writer the output stream to write to
     */
    public XmlWriter(Writer writer) {
        this(writer, null);
    }
    
    /**
     * Constructs a new <tt>XmlWriter</tt>.
     * @param writer the output stream to write to
     * @param config the XML writer configuration
     */
    public XmlWriter(Writer writer, XmlParserConfiguration config) {
        if (writer == null) {
            throw new IllegalArgumentException("writer is null");
        }
        
        this.writer = new FilterWriter(writer) {
            @Override
            public void write(int c) throws IOException {
                if (!suppressOutput) {
                    super.write(c);
                }
            }
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                if (!suppressOutput) {
                    super.write(cbuf, off, len);
                }
            }
            @Override
            public void write(String str, int off, int len) throws IOException {
                if (!suppressOutput) {
                    super.write(str, off, len);
                }
            }
        };
        
        if (config == null) {
            // create a default configuration
            this.config = new XmlParserConfiguration();
        }
        else {
            // the configuration is cloned to prevent changes during execution
            this.config = config.clone();
        }
        init();

        try {
            out = xmlOutputFactory.createXMLStreamWriter(this.writer);
        }
        catch (XMLStreamException e) {
            throw new IllegalArgumentException("Failed to create XMLStreamWriter: " + e.getMessage(), e);
        }
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
     * @see org.beanio.stream.RecordWriter#write(java.lang.Object)
     */
    @Override
    public void write(Object record) throws IOException {
        try {
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
                outputHeader = false;
            }
            
            // a null record indicates we need to close an element
            if (record == null) {
                if (elementStack != null) {
                    endElement();
                }
            }
            // otherwise we write the record (i.e. DOM tree) to the stream
            else {
                write(((Document) record).getDocumentElement(), config.isIndentationEnabled());
            }
        }
        catch (XMLStreamException e) {
            throw (IOException) new IOException(e.getMessage()).initCause(e);
        }
    }
    
    /**
     * Recursively writes an element to the XML stream writer.
     * @param element the DOM element to write
     * @param indentationEnabled set to <tt>true</tt> if indentation is enabled
     * @throws XMLStreamException
     */
    private void write(Element element, boolean indentationEnabled) throws XMLStreamException {
        
        String name = element.getLocalName();
        String prefix = element.getPrefix();
        String namespace = element.getNamespaceURI();
        
        boolean ignoreNamespace = false;
        if (namespace == null) {
            if (Boolean.TRUE.equals(element.getUserData(IS_NAMESPACE_IGNORED))) {
                prefix = null;
                ignoreNamespace = true;
            }
            namespace = "";
        }
        
        boolean setDefaultNamespace = false;
        if (prefix == null && !ignoreNamespace) {
            if (Boolean.TRUE.equals(element.getUserData(IS_DEFAULT_NAMESPACE))) {
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
                newLine();
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
                    
                    write((Element) child, indentationEnabled);
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
        
        // end the element if it is not a group
        if (!Boolean.TRUE.equals(element.getUserData(IS_GROUP_ELEMENT))) {
            if (!pendingStackUpdate) {
                pop();
            }
            if (!empty) {
                if (isParent && indentationEnabled) {
                    newLine();
                }
                out.writeEndElement();
            }            
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriter#flush()
     */
    @Override
    public void flush() throws IOException {
        try {
            out.flush();
        }
        catch (XMLStreamException e) {
            throw (IOException) new IOException(e.getMessage()).initCause(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriter#close()
     */
    @Override
    public void close() throws IOException {
        try {
            while (elementStack != null) {
                endElement();
            }
            out.writeEndDocument();
            out.flush();
            out.close();
            
            // closing the XMLStreamWriter does not automatically
            // close the underlying writer
            writer.flush();
            writer.close();
        }
        catch (XMLStreamException e) {
            throw (IOException) new IOException(e.getMessage()).initCause(e);
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
    private void newLine() throws XMLStreamException {
        if (config.isIndentationEnabled()) {
            out.writeCharacters(config.getLineSeparator());
            for (int i=0,j=level; i<j; i++) {
                out.writeCharacters(indentation);
            }
        }
    }
    
    private void endElement() throws XMLStreamException {
        pop();
        newLine();
        out.writeEndElement();
    }
    
    private void push(String namespace, String prefix, String name) {
        push(new ElementStack(elementStack, namespace, prefix, name));
    }
    
    private void push(ElementStack e) {
        elementStack = e;
        ++level;
    }
    
    private ElementStack pop() {
        ElementStack e = elementStack;
        elementStack = elementStack.getParent();
        --level;
        dirtyLevel = Math.min(dirtyLevel, level);
        return e;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.util.StatefulWriter#updateState(java.lang.String, java.util.Map)
     */
    @Override
    public void updateState(String namespace, Map<String, Object> state) {
        state.put(getKey(namespace, OUTPUT_HEADER_KEY), outputHeader);
        state.put(getKey(namespace, NAMESPACE_MAP_KEY), toToken(namespaceMap));
        
        Integer n = (Integer) state.get(getKey(namespace, LEVEL_KEY));
        
        int lastLevel = (n == null) ? 0 : n;

        // remove previous stack items beyond the current level
        for (int i=lastLevel; i>level; i--) {
            String stackPrefix = namespace + ".s" + i;
            state.remove(getKey(stackPrefix, STACK_ELEMENT_KEY));
            state.remove(getKey(stackPrefix, STACK_NS_MAP_KEY));
        }
        
        int to = DELTA_ENABLED ? dirtyLevel : 0;
        
        // update dirtied stack items up to the current level
        ElementStack e = elementStack;
        for (int i=level; i>to; i--) {
            String stackPrefix = namespace + ".s" + i;
            state.put(getKey(stackPrefix, STACK_ELEMENT_KEY), e.toToken());
            
            String nsMapKey = getKey(stackPrefix, STACK_NS_MAP_KEY);
            String token = toToken(e.getNamespaces());
            if (token == null) {
                state.remove(nsMapKey);
            }
            else {
                state.put(nsMapKey, token);
            }
            
            e = elementStack.getParent();
        }
        dirtyLevel = level;
        
        state.put(getKey(namespace, LEVEL_KEY), level);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.util.StatefulWriter#restoreState(java.lang.String, java.util.Map)
     */
    @Override
    public void restoreState(String namespace, Map<String, Object> state) throws IllegalStateException {
        this.outputHeader = (Boolean) getRequired(namespace, OUTPUT_HEADER_KEY, state);
        
        String key = getKey(namespace, NAMESPACE_MAP_KEY);
        String token = (String) state.get(key);
        if (token != null) {
            this.namespaceMap = toMap(token, key);
            this.namespaceCount = this.namespaceMap.size();
        }
        else {
            this.namespaceCount = 0;
        }
        
        this.level = 0;
        this.elementStack = null;
        
        try {
            out.flush();
            this.suppressOutput = true;
            
            int level = (Integer) getRequired(namespace, LEVEL_KEY, state);
            for (int i=0; i<level; i++) {
                String stackPrefix = namespace + ".s" + (i+1);
                
                ElementStack e = ElementStack.fromToken(elementStack, (String)getRequired(stackPrefix, STACK_ELEMENT_KEY, state));
                
                if (e.isDefaultNamespace()) {
                    out.writeStartElement(e.getName());
                }
                else if (e.getPrefix() == null) {
                    out.writeStartElement(e.getName());
                    out.writeDefaultNamespace(e.getNamespace());
                }
                else {
                    out.writeStartElement(e.getPrefix(), e.getName(), e.getNamespace());
                }
                
                // create a stack item
                push(e);
                
                // add namespaces
                String nsMap = (String) state.get(getKey(stackPrefix, STACK_NS_MAP_KEY));
                if (nsMap != null) {
                    String[] s = nsMap.trim().split(" ");
                    if (s.length % 2 != 0) {
                        throw new IllegalStateException("Invalid state information for key '" + 
                            getKey(stackPrefix, STACK_NS_MAP_KEY) + "'");
                    }
                    for (int n=0; n<s.length; n+=2) {
                        this.elementStack.addNamespace(s[n+1], s[n]);
                        out.writeNamespace(s[n+1], s[n]);
                    }
                }
            }
            
            // by writing a single character here, we force the last started element to close
            // so that it can be flushed and ignored
            out.writeCharacters(" ");
            out.flush();
        }
        catch (XMLStreamException ex) {
            throw new IllegalStateException(ex);
        }
        finally {
            this.suppressOutput = false;
        }
        
        this.dirtyLevel = level;
    }

    /*
     * Retrieves a value from a Map for a given key prepended with the namespace.
     */
    private Object getRequired(String namespace, String key, Map<String, Object> state) {
        key = getKey(namespace, key);
        Object value = state.get(key);
        if (value == null) {
            throw new IllegalStateException("Missing state information for key '" + key + "'");
        }
        return value;
    }
    
    private String getKey(String namespace, String name) {
        return namespace + "." + name;
    }
    
    /*
     * Constructs a Map from a String of space delimited key-values pairings.
     */
    private Map<String,String> toMap(String token, String key) {
        if (token == null) {
            return null;
        }
        String[] s = token.trim().split(" ");
        if (s.length % 2 != 0) {
            throw new IllegalStateException("Invalid state information for key '" + key + "'");
        }
        Map<String,String> map = new HashMap<>();
        for (int n=0; n<s.length; n+=2) {
            map.put(s[n], s[n+1]);
        }
        return map;
    }
    
    /*
     * Converts a Map to a String of space delimited key-value pairings.
     */
    private String toToken(Map<String,String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        
        boolean first = true;
        StringBuilder token = new StringBuilder();
        for (Map.Entry<String,String> entry : map.entrySet()) {
            if (first) {
                first = false;
            }
            else {
                token.append(" ");
            }
            token.append(entry.getKey());
            token.append(" ");
            token.append(entry.getValue());
        }
        return token.toString();
    }
}
