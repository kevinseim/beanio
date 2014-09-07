/*
 * Copyright 2013 Kevin Seim
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
package org.beanio.builder;

import org.beanio.internal.config.XmlTypeConstants;

/**
 * Enumeration of XML types.
 * @author Kevin Seim
 * @since 2.1.0
 */
public enum XmlType {

    /** An XML element */
    ELEMENT(XmlTypeConstants.XML_TYPE_ELEMENT),
    /** An XML attribute */
    ATTRIBUTE(XmlTypeConstants.XML_TYPE_ATTRIBUTE),
    /** Text contained within an XML element */
    TEXT(XmlTypeConstants.XML_TYPE_TEXT),
    /** The component is tied to an XML attribute or element */
    NONE(XmlTypeConstants.XML_TYPE_NONE),
    /** The default XML type defined by <code>beanio.properties</code>. */
    DEFAULT(null);
    
    private String value;
    
    private XmlType (String value) {
        this.value = value;
    }
    
    /**
     * Returns the BeanIO configuration name for this XML type.
     * @return the configuration name
     */
    public String toValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
