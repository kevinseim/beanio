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
package org.beanio.internal.util;

import java.io.StringWriter;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.beanio.BeanIOException;
import org.w3c.dom.*;

/**
 * Utility class for working with XML document object models.
 * 
 * @author Kevin Seim
 * @since 1.1
 */
public class DomUtil {
    
    private static final DocumentBuilder domFactory;

    static {
        try {
            DocumentBuilderFactory domBuilderFactory = DocumentBuilderFactory.newInstance();
            domBuilderFactory.setIgnoringComments(true);
            domBuilderFactory.setCoalescing(true);
            domBuilderFactory.setNamespaceAware(true);
            domBuilderFactory.setValidating(false);
            domFactory = domBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex) {
            throw new BeanIOException(ex);
        }
    }
    
    /** Prevent instantiation */
    private DomUtil() { }
    
    /**
     * Creates a new XML document object model.
     * @return the new DOM
     */
    public static Document newDocument() {
        return domFactory.newDocument();
    }
    
    /**
     * Prints a DOM to standard out (for testing only).
     * @param title the name of the DOM
     * @param document the DOM to print
     */
    public static void print(String title, Node document) throws BeanIOException{
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer trans = factory.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
    
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(document);
            trans.transform(source, result);
            
            System.out.println("--" + title + "--------------------------------");
            System.out.print(sw.toString());
            System.out.println("-------------------------------------------");
        }
        catch (Exception ex) {
        	throw new BeanIOException(ex);
        }
    }
}
