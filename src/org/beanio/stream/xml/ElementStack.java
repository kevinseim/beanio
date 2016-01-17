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

import java.util.*;

/**
 * An <tt>ElementStack</tt> is used internally by {@link XmlWriter} for managing
 * namespace declarations as an XML document is written.
 * 
 * <p>When an element is created from the stack, its default namespace is derrived 
 * from the element and its parent using the following strategy:</p>
 * <ul>
 * <li>if no prefix is declared, the element's namespace is the default namespace
 * <li>else if the element has no parent, then <tt>null</tt> is used
 * <li>otherwise the parent's default namespace is used
 * </ul>
 * 
 * @author Kevin Seim
 * @since 1.2
 */
class ElementStack {
    
    // the parent element
    private ElementStack parent;
    // the default namespace: 
    //   may be null if its the root element and a namespace prefix was used, 
    //   or empty string if no namespace was defined
    private String dns;
    // the element namespace (may be null)
    private String ns;
    // the element prefix (may be null)
    private String prefix; 
    // the element name
    private String name; 
    // Map of namespaces to prefixes declared by this element
    private Map<String,String> nsMap;
    
    /**
     * Constructs a new <tt>ElementStack</tt>.
     * @param parent the parent element, or null if this is the root element
     * @param ns the element namespace
     * @param prefix the element prefix or null
     * @param name the element name
     */
    public ElementStack(ElementStack parent, String ns, String prefix, String name) {
        this.parent = parent;
        this.ns = ns;
        this.prefix = prefix;
        this.name = name;
        
        if (prefix == null) {
            dns = ns;
        }
        else {
            dns = (parent == null) ? null : parent.dns;
            addNamespace(prefix, ns);
        }
    }
    
    /**
     * Returns the parent element in this stack.
     * @return this elements parent element or null if this is the root
     *   element in the stack
     */
    public ElementStack getParent() {
        return parent;
    }

    /**
     * Returns the default XML namespace for a child element.
     * @return the default XML namespace
     */
    public String getDefaultNamespace() {
        return dns;
    }
    
    /**
     * Returns whether this element uses the default XML namespace.
     * @return <tt>true</tt> if this element uses the default XML namespace
     */
    public boolean isDefaultNamespace() {
        if (ns == null) {
            return true;
        }
        else if (parent == null) {
            return ns.length() == 0;
        }
        else {
            return ns.equals(dns);
        }
    }
    
    /**
     * Tests whether a given namespace matches the current default namespace.
     * @param namespace the XML namespace to test
     * @return <tt>true</tt> if a default namespace is assigned and the given
     *   namespace matches it
     */
    public boolean isDefaultNamespace(String namespace) {
        return dns != null && dns.equals(namespace);
    }
    
    /**
     * Returns the XML namespace of this element.
     * @return the XML namespace of this element
     */
    public String getNamespace() {
        return ns;
    }
    
    /**
     * Returns the XML namespace prefix of this element, or <tt>null</tt>
     * if no prefix was assigned.
     * @return the XML namespace prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the XML element name.
     * @return the XML element name
     */
    public String getName() {
        return name;
    }

    /**
     * Adds an XML namespace declaration for this element.
     * @param prefix the namespace prefix
     * @param namespace the namespace
     */
    public void addNamespace(String prefix, String namespace) {
        if (nsMap == null) {
            nsMap = new HashMap<>();
        }
        nsMap.put(namespace, prefix);
    }
    
    /**
     * Returns the XML namespaces declared by this element.  May be <tt>null</tt>.
     * @return the XML namespaces declared by this element
     */
    public Map<String,String> getNamespaces() {
        return nsMap;
    }
    
    /**
     * Sets the XML namespaces declared by this element.
     * @param namespaces the Map of namespaces to prefixes declared by this element
     */
    public void setNamespaces(Map<String,String> namespaces) {
        this.nsMap = namespaces;
    }
    
    /**
     * Searches this element and its ancestors for a prefix declared for a
     * given XML namespace.
     * @param namespace the namespace to search for
     * @return the previously declared prefix or <tt>null</tt> 
     *   if no prefix has been declared
     */
    public String findPrefix(String namespace) {
        String prefix = null;
        if (nsMap  != null) {
            prefix = nsMap.get(namespace);
            if (prefix != null) {
                return prefix;
            }
        }
        if (parent != null) {
            return parent.findPrefix(namespace);
        }
        return null;
    }
    
    /**
     * Creates a token for this stack element containing the namespace prefix
     * and element name.
     * @return a token representing this stack element
     */
    public String toToken() {
        StringBuilder s = new StringBuilder(); 
        if (ns != null) {
            s.append("{").append(ns).append("}");
        }
        if (prefix != null) {
            s.append(prefix).append(':');
        }
        s.append(name);
        return s.toString();
    }
    
    /**
     * Creates a new <tt>ElementStack</tt> from its token value.
     * @param parent the parent stack element
     * @param token the element token
     * @return the new <tt>ElementStack</tt>
     */
    public static ElementStack fromToken(ElementStack parent, String token) {
        if (token == null || "".equals(token.trim())) {
            throw new IllegalArgumentException("Missing ElementStack token");
        }
        
        String namespace = null;
        String prefix = null;
        String name = null;
        
        int start = 0;
        int pos;
        
        // parse out the namespace
        if (token.startsWith("{")) {
            pos = token.indexOf('}');
            namespace = token.substring(1, pos);
            start = pos + 1;
        }
        
        // check for a prefix
        pos = token.indexOf(':', start);
        if (pos > 0) {
            prefix = token.substring(start, pos);
            start = pos + 1;
        }
        
        name = token.substring(start);
        
        return new ElementStack(parent, namespace, prefix, name);
    }
    
    @Override
    public String toString() {
        return toToken();
    }
}