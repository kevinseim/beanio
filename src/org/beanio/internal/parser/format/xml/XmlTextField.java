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

import org.w3c.dom.*;

/**
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlTextField extends XmlFieldFormat {

    /**
     * Constructs a new <tt>XmlTextField</tt>.
     */
    public XmlTextField() { }
    
    @Override
    public String extractText(XmlUnmarshallingContext context) {
        Element parent = context.getPosition();
        if (parent == null) {
            return null;
        }
        else {
            String fieldText = XmlNodeUtil.getText(parent);
            if (fieldText == null) {
                fieldText = "";
            }
            return fieldText;
        }
    }

    @Override
    public void insertText(XmlMarshallingContext ctx, String text) {
        if (text == null) {
            return;
        }
        
        Node parent = ctx.getParent();
        if (parent != null) {
            parent.setTextContent(text);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getType()
     */
    @Override
    public int getType() {
        return XmlNode.XML_TYPE_TEXT;
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
     * @see org.beanio.internal.parser.format.xml.XmlNode#getLocalName()
     */
    @Override
    public String getLocalName() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getNamespace()
     */
    @Override
    public String getNamespace() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#isNamespaceAware()
     */
    @Override
    public boolean isNamespaceAware() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getPrefix()
     */
    @Override
    public String getPrefix() {
        return null;
    }
    
    @Override
    public boolean isRepeating() {
        return false;
    }
}
