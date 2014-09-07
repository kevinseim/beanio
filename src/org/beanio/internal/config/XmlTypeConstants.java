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
package org.beanio.internal.config;

/**
 * XML node type constants.
 * 
 * @author Kevin Seim
 * @since 1.1
 */
public interface XmlTypeConstants {

    /** The XML node type to indicate the node is not a structural part of the document */
    public static final String XML_TYPE_NONE = "none";
    /** The XML node type for an element */
    public static final String XML_TYPE_ELEMENT = "element";
    /** The XML node type for an attribute */
    public static final String XML_TYPE_ATTRIBUTE = "attribute";
    /** The XML node type for elemental text */
    public static final String XML_TYPE_TEXT = "text";
    
}
