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
package org.beanio.stream.delimited;

import java.io.*;

import org.beanio.stream.*;

/**
 * Default {@link RecordParserFactory} for the delimited stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class DelimitedRecordParserFactory  extends DelimitedParserConfiguration implements RecordParserFactory {

    /**
     * Constructs a new <tt>DelimitedRecordParserFactory</tt>.
     */
    public DelimitedRecordParserFactory() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#init()
     */
    @Override
    public void init() throws IllegalArgumentException {
        
        if (getEscape() != null && getEscape() == getDelimiter()) {
            throw new IllegalArgumentException("The field delimiter canot match the escape character");
        }
        
        if (getLineContinuationCharacter() != null && getLineContinuationCharacter() == getDelimiter()) {
            throw new IllegalArgumentException("The field delimiter cannot match the line continuation character");
        }
        
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordReaderFactory#createReader(java.io.Reader)
     */
    @Override
    public RecordReader createReader(Reader in) throws IllegalArgumentException {
        return new DelimitedReader(in, this);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriterFactory#createWriter(java.io.Writer)
     */
    @Override
    public RecordWriter createWriter(Writer out) throws IllegalArgumentException {
        return new DelimitedWriter(out, this);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createMarshaller()
     */
    @Override
    public RecordMarshaller createMarshaller() throws IllegalArgumentException {
        return new DelimitedRecordParser(this);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createUnmarshaller()
     */
    @Override
    public RecordUnmarshaller createUnmarshaller() throws IllegalArgumentException {
        return new DelimitedRecordParser(this);
    }
}
