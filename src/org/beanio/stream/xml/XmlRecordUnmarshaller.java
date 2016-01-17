/*
 * Copyright 2012 Kevin Seim
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

import java.io.*;

import javax.xml.parsers.*;

import org.beanio.BeanIOException;
import org.beanio.stream.*;
import org.xml.sax.*;

/**
 * A {@link RecordUnmarshaller} implementation for XML formatted records.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlRecordUnmarshaller implements RecordUnmarshaller {

    private static final DocumentBuilder documentBuilder;
    static {
        try {
            DocumentBuilderFactory domBuilderFactory = DocumentBuilderFactory.newInstance();
            domBuilderFactory.setIgnoringComments(true);
            domBuilderFactory.setCoalescing(true);
            domBuilderFactory.setNamespaceAware(true);
            domBuilderFactory.setValidating(false);
            documentBuilder = domBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex) {
            throw new BeanIOException(ex);
        }
    }
    
    /**
     * Constructs a new <tt>XmlRecordUnmarshaller</tt>.
     */
    public XmlRecordUnmarshaller() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordUnmarshaller#unmarshal(java.lang.String)
     */
    @Override
    public Object unmarshal(String text) throws RecordIOException {
        try {
            return documentBuilder.parse(new InputSource(new StringReader(text)));
        }
        catch (IOException ex) {
            throw new RecordIOException(ex);
        }
        catch (SAXException e) {
            throw new RecordIOException(e.getMessage(), e);
        }
    }
}
