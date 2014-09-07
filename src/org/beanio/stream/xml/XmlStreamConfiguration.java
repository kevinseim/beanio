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

import org.w3c.dom.Document;

/**
 * This interface provides access to the XMl stream definition for XML 
 * <tt>RecordReaderFactory</tt> classes that implement <tt>XmlStreamConfigurationAware</tt>. 
 * 
 * @author Kevin Seim
 * @since 1.1
 * @see XmlStreamConfigurationAware
 */
public interface XmlStreamConfiguration {

    /**
     * Returns the base document object model that defines the group structure
     * of the XML read from an input stream.  The returned DOM object 
     * should only be used to parse a single stream.
     * @return the base document object model
     */
    public Document getDocument();
    
}
