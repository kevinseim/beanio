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
package org.beanio.internal.config;

import org.beanio.internal.util.TreeNode;

/**
 * The base class for nodes that that make up a stream configuration-
 * groups, records, segments, fields, constants and wrappers.  Nodes are 
 * organized into a tree structure.
 * 
 * <p>The following attributes apply to XML formatted streams only:
 * <ul>
 * <li>xmlName</li>
 * <li>xmlNamespace</li>
 * <li>xmlPrefix</li>
 * </ul>
 * 
 * <p>The following attributes are set during compilation, and are meant for 
 * internal use only:
 * <ul>
 * <li>xmlNamespaceAware</li>
 * </ul>
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class ComponentConfig extends TreeNode<ComponentConfig> {

    /** Group component type */
    public static final char GROUP = 'G';
    /** Record component type */
    public static final char RECORD = 'R';
    /** Segment component type */
    public static final char SEGMENT = 'S';
    /** Field component type */
    public static final char FIELD = 'F';
    /** Constant component type */
    public static final char CONSTANT = 'C';
    /** Wrapper component type */
    public static final char WRAPPER = 'W';
    /** Stream component type */
    public static final char STREAM = 'M';
    
    private Integer ordinal;
    private String xmlName;
    private String xmlNamespace;
    private String xmlPrefix;
    private boolean xmlNamespaceAware;
    
    /**
     * Constucts a new <tt>ComponentConfig</tt>.
     */
    public ComponentConfig() { }
    
    /**
     * Returns the component type.
     * @return one of {@link #GROUP}, {@link #RECORD}, {@link #SEGMENT}, {@link #FIELD},
     *   {@link #CONSTANT} or {@link #WRAPPER}
     */
    public abstract char getComponentType();
    
    /**
     * Returns the relative position of this component within its parent components.
     * @return the relative position
     */
    public Integer getOrdinal() {
        return ordinal;
    }

    /**
     * Sets the relative position of this component within its parent components.
     * @param ordinal the relative position
     */
    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }
    
    /**
     * Returns XML element or attribute name of this component.
     * @return the XML element or attribute name
     */
    public String getXmlName() {
        return xmlName;
    }

    /**
     * Sets the XML element or attribute name of this component.  If set to <tt>null</tt> 
     * (default), the XML name defaults to the component name.
     * @param xmlName the XML element or attribute name
     */
    public void setXmlName(String xmlName) {
        this.xmlName = xmlName;
    }

    /**
     * Returns the XML namespace of this component.
     * @return the XML namespace
     */
    public String getXmlNamespace() {
        return xmlNamespace;
    }

    /**
     * Sets the XML namespace of this component.  If set to <tt>null</tt>
     * (default), the namespace is inherited from its parent.
     * @param xmlNamespace the XML namespace
     */
    public void setXmlNamespace(String xmlNamespace) {
        this.xmlNamespace = xmlNamespace;
    }

    /**
     * Returns the XML prefix for the namespace assigned to this component.
     * @return the XML namespace prefix
     */
    public String getXmlPrefix() {
        return xmlPrefix;
    }

    /**
     * Sets the XML prefix for the namespace assigned to this component.  If 
     * set to <tt>null</tt> and a namespace is set, the namespace will replace the 
     * default namespace during marshaling.  A prefix should not be set if a
     * namespace is not set.
     * @param xmlPrefix the XML namespace prefix
     */
    public void setXmlPrefix(String xmlPrefix) {
        this.xmlPrefix = xmlPrefix;
    }
    
    /**
     * Returns whetther this component is namespace aware.
     * @return true if this component is namespace aware, false otherwise
     */
    public boolean isXmlNamespaceAware() {
        return xmlNamespaceAware;
    }

    /**
     * Sets whether this component is namespace aware.
     * @param xmlNamespaceAware true if this component is namespace aware
     */
    public void setXmlNamespaceAware(boolean xmlNamespaceAware) {
        this.xmlNamespaceAware = xmlNamespaceAware;
    }
}
