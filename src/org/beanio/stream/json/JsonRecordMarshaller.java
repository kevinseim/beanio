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
import java.util.Map;

import org.beanio.stream.RecordMarshaller;

/**
 * A {@link RecordMarshaller} implementation for JSON formatted records.
 * 
 * @author Kevin Seim
 * @since 2.0
 * @see JsonRecordParserFactory
 */
public class JsonRecordMarshaller extends JsonWriterSupport implements RecordMarshaller {

    private StringWriter out = new StringWriter();
    
    /**
     * Constructs a new <tt>JsonRecordMarshaller</tt>.
     */
    public JsonRecordMarshaller() {
        this(new JsonParserConfiguration());
    }
    
    /**
     * Constructs a new <tt>JsonRecordMarshaller</tt>.
     * @param config the {@link JsonParserConfiguration}
     */
    public JsonRecordMarshaller(JsonParserConfiguration config) {
        init(out, config);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordMarshaller#marshal(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public String marshal(Object record) {
        return marshal((Map<String,Object>) record);
    }
    
    /**
     * Marshals a single JSON object.
     * @param map the JSON object to marshal
     * @return the JSON formatted text
     */
    public String marshal(Map<String,Object> map) {
        try {
            super.write(map);
            return out.toString();
        }
        catch (IOException ex) {
            throw new IllegalStateException("Unexpected IOException caught", ex);
        }
        finally {
            out.clear();
        }
    }

    /*
     * Internal Writer implementation for capturing a String. 
     */
    private static class StringWriter extends Writer {
        private StringBuilder text = new StringBuilder();
        
        @Override
        public void write(char[] cbuf, int off, int len) {
            text.append(cbuf, off, len);
        }
        
        @Override
        public void write(int c) {
            text.append((char)c);
        }

        @Override
        public void write(char cbuf[]) {
            text.append(cbuf, 0, cbuf.length);
        }

        @Override
        public void flush() { }

        @Override
        public void close() { }
        
        public void clear() {
            text = new StringBuilder();
        }
        
        @Override
        public String toString() {
            return text.toString();
        }
    }
}
