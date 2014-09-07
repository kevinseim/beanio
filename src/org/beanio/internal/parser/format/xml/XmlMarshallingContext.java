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
package org.beanio.internal.parser.format.xml;

import java.io.IOException;

import org.beanio.internal.parser.MarshallingContext;
import org.beanio.internal.util.DomUtil;
import org.beanio.stream.xml.XmlWriter;
import org.w3c.dom.*;

/**
 * A {@link MarshallingContext} for XML records.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlMarshallingContext extends MarshallingContext {

    private boolean streaming = false;
    
    protected Document document;
    protected Node parent;
    
    private XmlNode[] groupStack;
    private int groupStackCount = 0;
    
    private int ungroup = 0;
    
    /**
     * Constructs a new <tt>XmlMarshallingContext</tt>.
     * @param groupDepth the maximum depth of a group in the parser tree
     */
    public XmlMarshallingContext(int groupDepth) {
        groupStack = new XmlNode[groupDepth];
    }
    
    @Override
    public void clear() {
        setDocument(null);
    }
    
    @Override
    public Object getRecordObject() {
        return getDocument();
    }
    
    @Override
    public void writeRecord() throws IOException {
        super.clear();
        
        for (int i=0; i<ungroup; i++) {
            getRecordWriter().write(null);
        }
        ungroup = 0;
        
        super.writeRecord();
    }
    
    /**
     * Returns the document being marshalled.
     * @return the {@link Document} being marshalled
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Sets the document being marshalled.
     * @param document the {@link Document} being marshalled
     */
    private void setDocument(Document document) {
        this.document = document;
        this.parent = document;
    }

    /**
     * Adds a group to be marshalled when the next record is written to
     * the output stream.
     * @param node the group element to add
     */
    public void openGroup(XmlNode node) {
        groupStack[groupStackCount++] = node;
    }
    
    /**
     * Indicates a group element should be closed before marshalling the next record.
     * @param node the {@link XmlNode} to close
     */
    public void closeGroup(XmlNode node) {
        ++ungroup;
    }

    /**
     * Returns the parent node to append in the document being marshalled.
     * @return the parent {@link Node}
     */
    public Node getParent() {
        if (parent == null) {
            this.document = DomUtil.newDocument();
            this.parent = document;
            
            if (groupStackCount > 0) {
                for (int i=groupStackCount-1; i>=0; i--) {
                    XmlNode xml = groupStack[i];
                    
                    Node node = parent.appendChild(document.createElementNS(
                        xml.getNamespace(), xml.getLocalName()));
                    node.setPrefix(xml.getPrefix());
                    node.setUserData(XmlWriter.IS_GROUP_ELEMENT, Boolean.TRUE, null);
                    if (!xml.isNamespaceAware()) {
                        node.setUserData(XmlWriter.IS_NAMESPACE_IGNORED, Boolean.TRUE, null);
                    }
                    else {
                        if ("".equals(xml.getPrefix())) {
                            node.setUserData(XmlWriter.IS_DEFAULT_NAMESPACE, Boolean.TRUE, null);
                        }
                        else {
                            node.setPrefix(xml.getPrefix());
                        }
                    }
                    
                    this.parent = node;
                }
                groupStackCount = 0;
            }
        }
        return parent;
    }

    /**
     * Sets the parent node to append in the document being marshalled.
     * @param parent the parent {@link Node}
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    @Override
    public Document toDocument(Object record) {
        return (Document)record;
    }
    
    /**
     * Sets whether a stream is being marshalled, versus a single document.
     * @param streaming true if marshalling a stream, false if marshalling single documents
     */
    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }

    /**
     * Returns whether a stream is being marshalled, versus a single document.
     * @return true if marshalling a stream, false if marshalling single documents
     */
    public boolean isStreaming() {
        return streaming;
    }
}
