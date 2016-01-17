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
package org.beanio.internal.parser;

import java.io.*;

import org.beanio.stream.*;

/**
 * Base class for {@link StreamFormat} implementations.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class StreamFormatSupport implements StreamFormat {

    private String name;
    private RecordParserFactory recordParserFactory;
    
    /**
     * Constructs a new <tt>StreamFormatSupport</tt>.
     */
    public StreamFormatSupport() { }
    
    /**
     * Sets the name of this stream.
     * @param name the stream name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.StreamFormat#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Creates a new <tt>RecordReader</tt> to read from the given input stream.
     * This method delegates to the configured record parser factory.
     * @param in the input stream to read from
     * @return a new <tt>RecordReader</tt>
     */
    @Override
    public RecordReader createRecordReader(Reader in) {
        return recordParserFactory.createReader(in);
    }

    /**
     * Creates a new <tt>RecordWriter</tt> for writing to the given output stream.
     * This method delegates to the configured record parser factory.
     * @param out the output stream to write to
     * @return a new <tt>RecordWriter</tt>
     */
    @Override
    public RecordWriter createRecordWriter(Writer out) {
        return recordParserFactory.createWriter(out);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.StreamFormat#createRecordMarshaller()
     */
    @Override
    public RecordMarshaller createRecordMarshaller() {
        return recordParserFactory.createMarshaller();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.StreamFormat#createRecordUnmarshaller()
     */
    @Override
    public RecordUnmarshaller createRecordUnmarshaller() {
        return recordParserFactory.createUnmarshaller();
    }

    /**
     * Sets the {@link RecordParserFactory} for creating record parsers.
     * @param recordParserFactory the {@link RecordParserFactory}
     */
    public void setRecordParserFactory(RecordParserFactory recordParserFactory) {
        this.recordParserFactory = recordParserFactory;
    }
    
    /**
     * Returns the {@link RecordParserFactory} used by this stream.
     * @return the {@link RecordParserFactory}
     */
    protected RecordParserFactory getRecordParserFactory() {
        return recordParserFactory;
    }
}
