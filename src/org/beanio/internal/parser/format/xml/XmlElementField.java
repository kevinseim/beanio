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

import javax.xml.XMLConstants;

import org.beanio.internal.parser.*;
import org.beanio.internal.util.DebugUtil;
import org.beanio.stream.xml.XmlWriter;
import org.w3c.dom.*;

/**
 * A {@link FieldFormat} for a field in an XML formatted stream parsed as
 * an element of its parent.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlElementField extends XmlFieldFormat {

    private boolean repeating = false;
    
    private String localName;
    private String prefix;
    private String namespace;
    private boolean namespaceAware;
    private boolean nillable;
    
    /**
     * Constructs a new <tt>XmlElementField</tt>.
     */
    public XmlElementField() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlFieldFormat#extractText(org.beanio.internal.parser.format.xml.XmlUnmarshallingContext)
     */
    @Override
    public String extractText(XmlUnmarshallingContext context) {
        Element node = context.findElement(this);
        if (node == null) {
            return null;
        }

        // check for nil elements
        if (XmlNodeUtil.isNil(node)) {
            return Value.NIL;
        }
        
        String fieldText = XmlNodeUtil.getText(node);
        if (fieldText == null) {
            fieldText = "";
        }
        return fieldText;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlFieldFormat#insertText(org.beanio.internal.parser.format.xml.XmlMarshallingContext, java.lang.String)
     */
    @Override
    public void insertText(XmlMarshallingContext ctx, String fieldText) {
        if (fieldText == null && isLazy()) {
            return;
        }
        if (fieldText == Value.NIL) {
            fieldText = null;
        }
        
        Document document = ctx.getDocument();
        
        Element element = document.createElementNS(getNamespace(), getLocalName());
        if (!isNamespaceAware()) {
            element.setUserData(XmlWriter.IS_NAMESPACE_IGNORED, Boolean.TRUE, null);
        }
        else {
            if ("".equals(getPrefix())) {
                element.setUserData(XmlWriter.IS_DEFAULT_NAMESPACE, Boolean.TRUE, null);
            }
            else {
                element.setPrefix(getPrefix());
            }
        }
        
        if (fieldText == null && isNillable()) {
            element.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil", "true");
        }
        else if (fieldText != null && fieldText.length() > 0) {
            element.appendChild(document.createTextNode(fieldText));
        }
        
        Element parent = (Element) ctx.getParent();
        parent.appendChild(element);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#isRepeating()
     */
    @Override
    public boolean isRepeating() {
        return repeating;
    }

    /**
     * Sets whether this element repeats within the context of its parent.
     * @param repeating true if repeating, false otherwise
     */
    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getType()
     */
    @Override
    public int getType() {
        return XmlNode.XML_TYPE_ELEMENT;
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
     * Sets the element name.
     * @param localName the element name
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
     * Sets the namespace of this element.
     * @param namespace the namespace
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
     * Sets a prefix to use for the namespace during marshalling.
     * @param prefix the namespace prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#isNillable()
     */
    @Override
    public boolean isNillable() {
        return nillable;
    }

    /**
     * Sets whether this element is nillable.
     * @param nillable true if nillable, false otherwise
     */
    public void setNillable(boolean nillable) {
        this.nillable = nillable;
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
     * Sets whether this element is namespace aware.
     * @param namespaceAware true if namespace aware, false otherwise
     */
    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
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
        s.append(", ").append(DebugUtil.formatOption("nillable", nillable));
    }
}
