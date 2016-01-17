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

import org.beanio.stream.*;
import org.w3c.dom.Document;

/**
 * Default {@link RecordParserFactory} for the XML stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlRecordParserFactory extends XmlParserConfiguration 
    implements RecordParserFactory, XmlStreamConfigurationAware {

    private XmlStreamConfiguration source;
    
    /**
     * Constructs a new <tt>XmlRecordParserFactory</tt>.
     */
    public XmlRecordParserFactory() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#init()
     */
    @Override
    public void init() throws IllegalArgumentException {
        
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordReaderFactory#createReader(java.io.Reader)
     */
    @Override
    public RecordReader createReader(Reader in) throws IllegalArgumentException {
        Document base = null;
        if (source != null) {
            base = source.getDocument();
        }
        
        return new XmlReader(in, base);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriterFactory#createWriter(java.io.Writer)
     */
    @Override
    public RecordWriter createWriter(Writer out) throws IllegalArgumentException {
        return new XmlWriter(out, this);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createMarshaller()
     */
    @Override
    public RecordMarshaller createMarshaller() throws IllegalArgumentException {
        return new XmlRecordMarshaller(this);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createUnmarshaller()
     */
    @Override
    public RecordUnmarshaller createUnmarshaller() throws IllegalArgumentException {
        return new XmlRecordUnmarshaller();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.xml.XmlDocumentAware#setSource(org.beanio.stream.xml.XmlDocumentSource)
     */
    @Override
    public void setConfiguration(XmlStreamConfiguration source) {
        this.source = source;
    }
}
