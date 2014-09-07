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
package org.beanio.parser.xml;

import static org.junit.Assert.assertEquals;

import java.io.*;

import javax.xml.parsers.*;

import org.beanio.parser.ParserTest;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Base class for XML parser JUnit test cases.
 * 
 * @author Kevin Seim
 * @since 1.1
 */
public class XmlParserTest extends ParserTest {
    
    /**
     * Compares expected and actual XML documents using the documnet object model's
     * <tt>node.isEqualNode()</tt> method.
     * @param expected the expected XML document
     * @param actual the actual XML document
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @see {@link Node#isEqualNode(Node)}
     */
    public void assertXmlEquals(String expected, String actual) throws IOException, 
        SAXException, ParserConfigurationException {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document d1 = builder.parse(new InputSource(new StringReader(expected)));

        Document d2 = builder.parse(new InputSource(new StringReader(actual)));
        
        if (!d1.isEqualNode(d2)) {
            assertEquals(expected, actual);
        }
    }
}
