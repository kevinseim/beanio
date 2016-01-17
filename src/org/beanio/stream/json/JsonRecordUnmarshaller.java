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

import org.beanio.stream.*;

/**
 * A {@link RecordUnmarshaller} implementation for JSON formatted records.
 * 
 * @author Kevin Seim
 * @since 2.0
 * @see JsonRecordParserFactory
 */
public class JsonRecordUnmarshaller extends JsonReaderSupport implements RecordUnmarshaller {

    private StringReader reader = new StringReader();
    
    /**
     * Constructs a new <tt>JsonRecordUnmarshaller</tt>.
     */
    public JsonRecordUnmarshaller() {
        setReader(reader);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordUnmarshaller#unmarshal(java.lang.String)
     */
    @Override
    public Map<String,Object> unmarshal(String text) throws RecordIOException {
        if (text == null) {
            return null;
        }
        
        Map<String,Object> map = null;
        
        reader.setText(text);
        
        try {    
            int state = 0;
            
            int n;
            while ((n = in.read()) != -1) {
                char c = (char)n;
                
                switch (state) {
                case 0: // find object
                    if (c == '{') {
                        map = super.readObject();
                        state = 1;
                    }
                    else if (!isWhitespace(c)) {
                        throw new RecordIOException("Unexpected character");   
                    }
                    break;
                    
                case 1: // ensure no more characters
                    if (!isWhitespace((char)n)) {
                        throw new RecordIOException("Unexpected character");
                    }
                    break;
                }
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException("Unexpected IOException caught", ex);
        }
        catch (RecordIOException ex) {
            throw new RecordIOException(ex.getMessage() + " near position " + reader.getPosition(), ex);
        }
        
        if (map == null) {
            throw new RecordIOException("Expected '{' near position 1");
        }
        
        return map;
    }
    
    /*
     * Reader implementation for reading from a String.
     */
    private static class StringReader extends Reader {

        private char[] buffer;
        private int position;
        
        /**
         * Sets the String to read from.
         * @param text the {@link String}
         */
        public void setText(String text) {
            this.buffer = text.toCharArray();
            this.position = 0;
        }
        
        public int getPosition() {
            return position;
        }
        
        @Override
        public int read() {
            if (position < buffer.length) {
                return buffer[position++];
            }
            else {
                return -1;
            }
        }

        @Override
        public int read(char[] cbuf, int off, int len) {
            if (position < buffer.length) {
                len = Math.min(len, buffer.length - position);
                System.arraycopy(buffer, position, cbuf, off, len);
                position += len;
                return len;
            }
            else {
                return -1;
            }
        }

        @Override
        public void close() { }
    }

}
