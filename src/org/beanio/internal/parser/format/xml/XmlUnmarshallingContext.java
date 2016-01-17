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

import java.util.LinkedList;

import org.beanio.internal.parser.*;
import org.w3c.dom.*;

/**
 * An {@link UnmarshallingContext} for an XML formatted record.
 * 
 * <p>The record value type is a {@link Document}.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlUnmarshallingContext extends UnmarshallingContext {

    /* The DOM to parse */
    private Document document;
    /* The last parsed node in the document, which is the parent node of the next field/bean to parse */
    private Element position;
    /* This stack of elements is used to store the last XML node parsed for a field or bean collection. */
    private LinkedList<Element> elementStack = new LinkedList<>();
    /* Store previously matched groups for parsing subsequent records in a record group */
    private XmlNode[] groupStack;
    
    /**
     * Constructs a new <tt>XmlUnmarshallingContext</tt>
     * @param groupDepth the maximum depth of an element mapped to a {@link Group} in the DOM
     */
    public XmlUnmarshallingContext(int groupDepth) {
        groupStack = new XmlNode[groupDepth];
    }
    
    @Override
    public void setRecordValue(Object value) {
        Node node = (Node) value;
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            this.document = (Document) value;
            this.position = null;
        }
        else if (node.getNodeType() == Node.ELEMENT_NODE) {
            this.document = node.getOwnerDocument();
            this.position = (Element)node;
        }
        else {
            this.document = node.getOwnerDocument();
            this.position = null;          
        }
    }
    
    @Override
    public void pushIteration(Iteration b) {
        super.pushIteration(b);
        elementStack.addFirst(null);
    }
    
    @Override
    public Iteration popIteration() {
        elementStack.removeFirst();
        return super.popIteration();
    }

    /**
     * Returns the last parsed DOM element for a field or bean collection.
     * @return the last parsed element
     */
    public Element getPreviousElement() {
        return elementStack.getFirst();
    }
    
    /**
     * Sets the last parsed DOM element for a field or bean collection.
     * @param e the last parsed element
     */
    public void setPreviousElement(Element e) {
        elementStack.set(0, e);
    }
    
    /**
     * Returns the XML document object model (DOM) for the current record.
     * @return the XML document object model
     */
    public Document getDocument() {
        return document;
    }
    
    /**
     * Returns the current unmarshalled position in the DOM tree, or null
     * if a node has not been matched yet.
     * @return the current parent DOM node
     * @see #pushPosition(XmlNode, int, boolean)
     * @see #pushPosition(XmlNode)
     */
    public Element getPosition() {
        return position;
    }
    
    /**
     * Updates <tt>position</tt> by finding a child of the current position
     * that matches a given node.  If <tt>isGroup</tt> is true, the node is
     * indexed by its depth so that calls to this method for subsequent records
     * in the same group can update <tt>position</tt> according to the depth
     * of the record.
     * @param node the {@link XmlNode} to match
     * @param depth the depth of the node in the DOM tree 
     * @param isGroup whether the node is mapped to a {@link Group}
     * @return the matched node or null if not matched
     */
    public Element pushPosition(XmlNode node, int depth, boolean isGroup) {
        // if the pushed node is a group node, add it to the group stack
        // for the workaround below
        if (isGroup) {
            groupStack[depth] = node;
        }
        
        // this is a workaround for handling bean objects that span multiple records
        // once the first record is identified, parent groups are not called for
        // subsequent records so the current position will be null even though we
        // already deeper in the parser tree
        if (position == null && depth > 0) {
            for (int i=0; i<depth; i++) {   
                position = findElement(groupStack[i]);
                if (position == null) {
                    return null;
                }
            }
            
            // if we still don't match, update the position back to null
            Element element = pushPosition(node);
            if (element == null) {
                position = null;
            }
            return element;
        }
        else {
            return pushPosition(node);
        }
    }
    
    /**
     * Updates <tt>position</tt> by finding a child of the current position
     * that matches a given node.
     * @param node the {@link XmlNode} to match
     * @return the matching element, or null if not found
     * @see #getPosition()
     */
    public Element pushPosition(XmlNode node) {
        Element element = findElement(node);
        if (element == null) {
            return null;
        }
        else {
            position = element;
            return position;
        }
    }
    
    /**
     * Updates <tt>position</tt> to its parent (element), 
     * or null if the parent element is the document itself.
     * @see #getPosition()
     */
    public void popPosition() {
        if (position != null) {
            Node n = position.getParentNode();
            if (n == null || n.getNodeType() == Node.DOCUMENT_NODE){
                position = null;
            }
            else {
                position = (Element) n;
            }
        }
    }
    
    /**
     * Finds a child element of the current <tt>position</tt>.
     * @param node the {@link XmlNodeUtil}
     * @return the matched element or null if not found
     */
    public Element findElement(XmlNode node) {
        Element parent = position;
        
        Element element;
        if (node.isRepeating()) {
            int index = getRelativeFieldIndex();
            
            if (index > 0) {
                element = XmlNodeUtil.findSibling(getPreviousElement(), node);
            }
            else {
                element = XmlNodeUtil.findChild(parent, node, index);
            }
            if (element != null) {
                setPreviousElement(element);
            }
        }
        else {
            if (parent == null) {
                element = XmlNodeUtil.findChild(document, node, 0);
            }
            else {
                element = XmlNodeUtil.findChild(parent, node, 0);
            }
        }
        return element;
    }
    
    @Override
    public Object toRecordValue(Node node) {
        return node;
    }
}
