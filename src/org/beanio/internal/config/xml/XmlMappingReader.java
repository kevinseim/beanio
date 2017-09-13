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
package org.beanio.internal.config.xml;

import java.io.*;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;

import org.beanio.*;
import org.w3c.dom.Document;
import org.xml.sax.*;

/**
 * Reads a BeanIO XML mapping file into an XML document object model (DOM)
 * and validates the XML using the BeanIO mapping schema.
 * 
 * <p>This class can safely parse multiple input streams concurrently.
 * 
 * @author Kevin Seim
 * @since 1.2.1
 */
public class XmlMappingReader {

    private static final String BEANIO_XMLNS = "http://www.beanio.org/2012/03";
    private static final String BEANIO_XSD = "/org/beanio/xsd/2012/03/mapping.xsd";

    private static final EntityResolver defaultEntityResolver = new DefaultEntityResolver();
    
    private DocumentBuilderFactory factory;
    
    /**
     * Constructs a new <tt>XmlMappingReader</tt>.
     */
    public XmlMappingReader() { 
        factory = createDocumentBuilderFactory();
    }
    
    /**
     * Parses an XML BeanIO mapping file into a document object model (DOM).
     * @param in the input stream to read
     * @return the resulting DOM
     * @throws IOException if an I/O error occurs
     * @throws BeanIOConfigurationException if the XML mapping file is
     *   malformed or invalid
     */
    public Document loadDocument(InputStream in) throws IOException,
        BeanIOConfigurationException {
        
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(createEntityResolver());

            final List<String> errorMessages = new ArrayList<>();

            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    errorMessages.add("Error at line " + exception.getLineNumber() +
                        ": " + exception.getMessage());
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    errorMessages.add("Error at line " + exception.getLineNumber() +
                        ": " + exception.getMessage());
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }
            });

            Document document = builder.parse(in);
            if (!errorMessages.isEmpty()) {
                StringBuilder message = new StringBuilder();
                message.append("Invalid mapping file");
                for (String s : errorMessages) {
                    message.append("\n  ==> ");
                    message.append(s);
                }
                throw new BeanIOConfigurationException(message.toString());
            }

            return document;
        }
        catch (SAXException ex) {
            throw new BeanIOConfigurationException("Malformed mapping file", ex);
        }
        catch (ParserConfigurationException ex) {
            throw new BeanIOConfigurationException("Failed to load suitable DOM implementation", ex);
        }
    }
    
    /**
     * Creates an XML document builder factory.
     * @return the new <tt>DocumentBuilderFactory</tt>
     */
    protected DocumentBuilderFactory createDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setCoalescing(true);
        factory.setNamespaceAware(true);
        factory.setValidating(true);

        try {
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", 
                XmlMappingReader.class.getResource(BEANIO_XSD).toExternalForm());
        }
        catch (IllegalArgumentException ex) {
            throw new BeanIOException("Unable to validate using XSD: JAXP provider [" +
                factory + "] does not support XML Schema.", ex);
        }
        return factory;
    }

    /**
     * Returns the XML entity resolver for loading the BeanIO schema definition or 
     * other reference entities.
     * @return XML entity resolver
     */
    protected EntityResolver createEntityResolver() {
        return defaultEntityResolver;
    }

    private static class DefaultEntityResolver implements EntityResolver {
        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
            IOException {
            if (publicId == null && (BEANIO_XMLNS.equals(systemId) ||
                (BEANIO_XMLNS + "/mapping.xsd").equals(systemId))) {
                return new InputSource(XmlConfigurationLoader.class.getResourceAsStream(BEANIO_XSD));
            }
            else {
                return null;
            }
        }
    }
}
