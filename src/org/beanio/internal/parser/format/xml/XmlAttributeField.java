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

import org.beanio.internal.parser.FieldFormat;
import org.w3c.dom.*;

/**
 * A {@link FieldFormat} for a field in an XML formatted stream parsed as
 * an attribute of its parent.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlAttributeField extends XmlFieldFormat {
    
    private String localName;
    private String prefix;
    private String namespace;
    private boolean namespaceAware;
    
    /**
     * Constructs a new <tt>XmlAttributeField</tt>.
     */
    public XmlAttributeField() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlFieldFormat#extractText(org.beanio.internal.parser.format.xml.XmlUnmarshallingContext)
     */
    @Override
    public String extractText(XmlUnmarshallingContext context) {
        Element parent = context.getPosition();
        if (parent == null) {
            return null;
        }
        else {
            return XmlNodeUtil.getAttribute((Element)parent, this);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlFieldFormat#insertText(org.beanio.internal.parser.format.xml.XmlMarshallingContext, java.lang.String)
     */
    @Override
    public void insertText(XmlMarshallingContext ctx, String fieldText) {
        Element parent = (Element) ctx.getParent();
        
        // format the field text (a null field value may not return null if a custom type handler was configured)
        String text = fieldText;
        
        // nothing to marshal if minOccurs is 0        
        if (text == null && isLazy()) {
            return;
        }
        
        if (parent.getNodeType() == Node.ELEMENT_NODE) {
            if (text == null) {
                text = "";
            }
            
            Attr att = parent.getOwnerDocument().createAttributeNS(getNamespace(), getLocalName());
            att.setValue(text);
            att.setPrefix(getPrefix());
            ((Element)parent).setAttributeNode(att);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getType()
     */
    @Override
    public int getType() {
        return XmlNode.XML_TYPE_ATTRIBUTE;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getLocalName()
     */
    @Override
    public String getLocalName() {
        return localName;
    }

    /**
     * Sets the attribute name.
     * @param localName the attribute name
     */
    public void setLocalName(String localName) {
        this.localName = localName;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getNamespace()
     */
    @Override
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the namespace of the attribute (typically null).
     * @param namespace the attribute namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getPrefix()
     */
    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix to use for this attribute's namespace.
     * @param prefix the namespace prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#isNamespaceAware()
     */
    @Override
    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    /**
     * Sets whether this attribute uses a namespace.
     * @param namespaceAware true if this attribute uses a namespace, false otherwise
     */
    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#isNillable()
     */
    @Override
    public boolean isNillable() {
        return false;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#isRepeating()
     */
    @Override
    public boolean isRepeating() {
        return false;
    }
    
    @Override
    public void toParamString(StringBuilder s) {
        super.toParamString(s);
        s.append(", localName=").append(localName);
        if (prefix != null) {
            s.append(", prefix=").append(prefix);    
        }
        if (namespace != null) {
            s.append(", xmlns=").append(isNamespaceAware() ? namespace : "*");
        }
    }
}
