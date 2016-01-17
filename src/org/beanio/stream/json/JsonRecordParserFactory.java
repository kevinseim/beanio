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
package org.beanio.stream.json;

import java.io.*;
import java.util.*;

import org.beanio.stream.*;

/**
 * A {@link RecordParserFactory} for JSON formatted streams.
 * 
 * <p>Record objects are stored using a {@link Map}.  A map may contain {@link List}
 * values for a JSON array, another {@link Map} for a JSON object, or the simple 
 * types {@link String}, {@link Number}, {@link Boolean} or <tt>null</tt>.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonRecordParserFactory extends JsonParserConfiguration implements RecordParserFactory {

    /**
     * Constructs a new <tt>JsonRecordParserFactory</tt>.
     */
    public JsonRecordParserFactory() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#init()
     */
    @Override
    public void init() throws IllegalArgumentException { }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createReader(java.io.Reader)
     */
    @Override
    public RecordReader createReader(Reader in) throws IllegalArgumentException {
        return new JsonReader(in);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createWriter(java.io.Writer)
     */
    @Override
    public RecordWriter createWriter(Writer out) throws IllegalArgumentException {
        return new JsonWriter(out, this);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createMarshaller()
     */
    @Override
    public RecordMarshaller createMarshaller() throws IllegalArgumentException {
        return new JsonRecordMarshaller(this);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParserFactory#createUnmarshaller()
     */
    @Override
    public RecordUnmarshaller createUnmarshaller() throws IllegalArgumentException {
        return new JsonRecordUnmarshaller();
    }
}
