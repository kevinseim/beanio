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
package org.beanio.stream.fixedlength;

import java.io.*;

import org.beanio.BeanIOConfigurationException;
import org.beanio.stream.*;

/**
 * Default {@link RecordParserFactory} for the fixed length stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FixedLengthRecordParserFactory extends FixedLengthParserConfiguration implements RecordParserFactory {

    private static FixedLengthRecordParser parser = new FixedLengthRecordParser();
   
    /**
     * Constructs a new <tt>FixedLengthRecordParserFactory</tt>.
     */
    public FixedLengthRecordParserFactory() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#init()
     */
    @Override
    public void init() throws BeanIOConfigurationException {
        
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createReader(java.io.Reader)
     */
    @Override
    public RecordReader createReader(Reader in) throws IllegalArgumentException {
        return new FixedLengthReader(in, this);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createWriter(java.io.Writer)
     */
    @Override
    public RecordWriter createWriter(Writer out) throws IllegalArgumentException {
        return new FixedLengthWriter(out, getRecordTerminator());
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createMarshaller()
     */
    @Override
    public RecordMarshaller createMarshaller() throws IllegalArgumentException {
        return parser;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createUnmarshaller()
     */
    @Override
    public RecordUnmarshaller createUnmarshaller() throws IllegalArgumentException {
        return parser;
    }
}
