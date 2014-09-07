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

/**
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface XmlNode {

    /** Text XML node type */
    public final static int XML_TYPE_TEXT = 3;
    /** Element XML node type */
    public final static int XML_TYPE_ELEMENT = 2;
    /** Attribute XML node type */
    public final static int XML_TYPE_ATTRIBUTE = 1;
    /** XML type indicating no node */
    public final static int XML_TYPE_NONE = 0;
    
    /**
     * Returns the XML node type.
     * @return one of 
     *   {@link #XML_TYPE_NONE},
     *   {@link #XML_TYPE_ELEMENT},
     *   {@link #XML_TYPE_ATTRIBUTE}, or
     *   {@link #XML_TYPE_TEXT}
     */
    public int getType();
    
    /**
     * Returns the XML local name for this node.
     * @return the XML local name
     */
    public String getLocalName();

    /**
     * Returns the namespace of this node.  If there is no namespace for this
     * node, or this node is not namespace aware, <tt>null</tt> is returned.
     * @return the XML namespace of this node
     */
    public String getNamespace();
    
    /**
     * Returns <tt>true</tt> if a namespace was configured for this node, and is
     * therefore used to unmarshal and marshal the node.
     * @return <tt>true</tt> if this node uses a namespace for matching and 
     *   formatting this node
     */
    public boolean isNamespaceAware();

    /**
     * Returns the namespace prefix for marshaling this node, or <tt>null</tt>
     * if the namespace should override the default namespace.
     * @return the namespace prefix
     */
    public String getPrefix();
    
    /**
     * Returns whether this node is nillable.
     * @return <tt>true</tt> if this node is nillable
     */
    public boolean isNillable();

    /**
     * Returns whether this node may repeat in the context of its immediate parent.
     * @return true if this node repeats, false otherwise
     */
    public boolean isRepeating();
    
}
