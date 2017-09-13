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
package org.beanio.stream.csv;

import java.io.*;

import org.beanio.stream.*;

/**
 * Default {@link RecordParserFactory} for the CSV stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class CsvRecordParserFactory extends CsvParserConfiguration implements RecordParserFactory {

    /**
     * Constructs a new <tt>CsvRecordParserFactory</tt>.
     */
    public CsvRecordParserFactory() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#init()
     */
    @Override
    public void init() throws IllegalArgumentException {
        
        if (getQuote() == getDelimiter()) {
            throw new IllegalArgumentException("The CSV field delimiter cannot " +
                "match the character used for the quotation mark.");
        }
        
        if (getEscape() != null && getEscape() == getDelimiter()) {
            throw new IllegalArgumentException(
                "The CSV field delimiter cannot match the escape character.");
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordReaderFactory#createReader(java.io.Reader)
     */
    @Override
    public RecordReader createReader(Reader in) throws IllegalArgumentException {
        return new CsvReader(in, this);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriterFactory#createWriter(java.io.Writer)
     */
    @Override
    public RecordWriter createWriter(Writer out) throws IllegalArgumentException {
        return new CsvWriter(out, this);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createMarshaller()
     */
    @Override
    public RecordMarshaller createMarshaller() throws IllegalArgumentException {
        return new CsvRecordParser(this);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createUnmarshaller()
     */
    @Override
    public RecordUnmarshaller createUnmarshaller() throws IllegalArgumentException {
        return new CsvRecordParser(this);
    }
}
