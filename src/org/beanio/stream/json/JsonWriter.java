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
 * A {@link RecordWriter} implementation for writing JSON formatted record.
 * 
 * @author Kevin Seim
 * @since 2.0
 * @see JsonRecordParserFactory
 */
public class JsonWriter extends JsonWriterSupport implements RecordWriter {

    private Writer out;

    /**
     * Constructs a new <tt>JsonWriter</tt>.
     * @param out
     */
    public JsonWriter(Writer out) {
        this(out, new JsonParserConfiguration());
    }
    
    /**
     * Constructs a new <tt>JsonWriter</tt>.
     * @param out
     */
    public JsonWriter(Writer out, JsonParserConfiguration config) {
        this.out = out;
        
        init(out, config);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriter#write(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(Object record) throws IOException {
        write((Map<String,Object>)record);
        out.write(getLineSeparator());
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriter#flush()
     */
    @Override
    public void flush() throws IOException {
        out.flush();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordWriter#close()
     */
    @Override
    public void close() throws IOException {
        out.close();
    }
}
