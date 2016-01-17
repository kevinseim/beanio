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

import org.beanio.internal.util.JsonUtil;
import org.beanio.stream.RecordIOException;

/**
 * Base class for reading a JSON formatted stream.
 * 
 * @author Kevin Seim
 * @since 2.0
 * @see JsonRecordParserFactory
 */
public abstract class JsonReaderSupport {

    /** The {@link Reader} to read from */
    protected Reader in;
    /** Flag indicating the end of the stream was reached */
    protected boolean eof;
    
    /**
     * Constructs a new <tt>JsonReaderSupport</tt>.
     */
    JsonReaderSupport() { }
    
    /**
     * Set the {@link Reader} to read from.
     * @param in the {@link Reader}
     */
    protected void setReader(Reader in) {
        this.in = in;
    }
    
    /**
     * Reads the next JSON object from the stream.
     * @return the JSON object {@link Map}
     * @throws IOException
     */
    protected Map<String,Object> readObject() throws IOException {
        String fieldName = null;
        StringBuilder text = null;
        
        Map<String,Object> map = new HashMap<>();
        
        int state = 0;
        
        int n;
        LOOP: while ((n = in.read()) != -1) {
            char c = (char)n;
            
            switch (state) {
            
            // parsing object, looking for " to start field name or end of object
            case -1:
                if (c == '}') {
                    return map;
                }
                else if (c == ',') {
                    state = 1;
                }
                else if (!isWhitespace(c)) {
                    break LOOP;
                }
                break;
                // fall-through...
            
            case 0: // looking for first field
                if (c == '}') {
                    return map;
                }
                // fall-through...
                
            // looking for " to start field name
            case 1:
                if (c == '"') {
                    fieldName = readString();
                    state = 2;
                }
                else if (!isWhitespace(c)) {
                    break LOOP;
                }
                break;
            
            // got field name, looking for ':'
            case 2:
                if (c == ':') {
                    // now look for value
                    state = 3;
                }
                else if (!isWhitespace(c)) {
                    break LOOP;
                }
                break;
            
            // got ':' looking for field value
            case 3:
                if (c == '"') {
                    map.put(fieldName, readString());
                    state = -1;
                }
                else if (c == '{') {
                    map.put(fieldName, readObject());
                    state = -1;
                }
                else if (c == '[') {
                    map.put(fieldName, readArray());
                    state = -1;
                }
                else if (!isWhitespace(c)) {
                    text = new StringBuilder();
                    text.append(c);
                    state = 4;
                }
                break;

            // read field value (i.e. number, boolean or null)
            case 4:
                if (c == ',') {
                    map.put(fieldName, parseValue(text.toString()));
                    state = 1;
                }
                else if (c == '}') {
                    map.put(fieldName, parseValue(text.toString()));
                    return map;
                }
                else if (isWhitespace(c)) {
                    map.put(fieldName, parseValue(text.toString()));
                    state = -1;
                }
                else {
                    text.append(c);
                }
                break;
            }
        }
        
        if (n < 0) {
            eof = true;
        }
        
        switch (state) {
        case 0:
            throw new RecordIOException("Expected string or '}'");
        case -1:
        case 4:
            throw new RecordIOException("Expected ',' or '}'");
        case 1:
            throw new RecordIOException("Expected '\"'");
        case 2:
            throw new RecordIOException("Expected ':'");
        case 3:
            throw new RecordIOException("Expected value");
        default:
            throw new RecordIOException("Unexpected end of record");
        }
    }
    
    /**
     * Reads a JSON array from the input stream.
     * @return the parsed JSON array
     * @throws IOException
     */
    protected List<Object> readArray() throws IOException {
        List<Object> list = new ArrayList<>();
        StringBuilder text = null;
        int state = 0;
        
        int n;
        while ((n = in.read()) != -1) {
            char c = (char)n;
            
            switch (state) {
            
            // looking for comma or end of array
            case -1: 
                if (c == ']') {
                    return list;
                }
                else if (c == ',') {
                    state = 1;
                }
                else if (!isWhitespace(c)) {
                    throw new RecordIOException("Expected ','");
                }
                break;
            
            // looking for field name or end of array
            case 0:
                if (c == ']') {
                    return list;
                }
                // fall through...
                
            // looking for field name
            case 1:
                if (c == '"') {
                    list.add(readString());
                    state = -1;
                }
                else if (c == '{') {
                    list.add(readObject());
                    state = -1;
                }
                else if (c == '[') {
                    list.add(readArray());
                    state = -1;
                }
                else if (!isWhitespace(c)) {
                    text = new StringBuilder();
                    text.append(c);
                    state = 2;
                }
                break;
            
            // read value
            case 2:
                if (c == ',') {
                    list.add(parseValue(text.toString()));
                    state = 1;
                }
                else if (c == ']') {
                    list.add(parseValue(text.toString()));
                    return list;
                }
                else if (isWhitespace(c)) {
                    list.add(parseValue(text.toString()));
                    state = -1;
                }
                else {
                    text.append(c);
                }
                break;
            }
        }
        
        eof = true;
        
        switch (state) {
        case 0:
        case 2:
            throw new RecordIOException("Expected ',' or ']'");
        case 1:
            throw new RecordIOException("Expected value");
        default:
            throw new RecordIOException("Unexpected end of record");
        }
    }
    
    /**
     * Parses a null, boolean or numeric value from the given text.
     * @param text the text to parse
     * @return the parsed value: either null, Boolean, Double, Long or Integer
     * @throws IOException
     */
    protected Object parseValue(String text) throws IOException {
        if ("null".equals(text)) {
            return null;
        }
        else if ("true".equals(text)) {
            return Boolean.TRUE;
        }
        else if ("false".equals(text)) {
            return Boolean.FALSE;
        }
        
        try {
            return JsonUtil.toNumber(text);
        }
        catch (NumberFormatException ex) {
            throw new RecordIOException("Cannot parse '" + text + "' into a JSON string, number or boolean", ex);
        }
    }
    
    /**
     * Reads a JSON string value from the input stream.
     * @return the JSON string value
     * @throws IOException
     */
    protected String readString() throws IOException {
        StringBuilder text = new StringBuilder();
        int state = 0;
        
        int n;
        while ((n = in.read()) != -1) {
            char c = (char) n;
            
            switch (state) {
            
            // handle read value
            case 0:
                if (c == '"') {
                    return text.toString();
                }
                else if (c == '\\') {
                    state = 1;
                }
                else {
                    text.append(c);
                }             
                break;
                
            // handle escaped character
            case 1:
                if (c == '"')
                    text.append('"');
                else if (c == '\\')
                    text.append('\\');
                else if (c == '/')
                    text.append('/');
                else if (c == 't')
                    text.append('\t');
                else if (c == 'b')
                    text.append('\u0008');
                else if (c == 'f')
                    text.append('\f');
                else if (c == 'n') 
                    text.append('\n');
                else if (c == 'r')
                    text.append('\r');
                else if (c == 'u')
                    text.append(readUnicode());
                else
                    throw new RecordIOException("Invalid escaped character: '" + c + "'");
                
                state = 0;
                break;
            }
        }
        
        eof = true;
        
        throw new RecordIOException("Expected '\"'");
    }
    
    /**
     * Reads a Unicode character in JSON format.
     * @return the Unicode character
     * @throws IOException
     */
    protected char readUnicode() throws IOException {
        
        StringBuilder value = new StringBuilder();
        
        for (int i=0; i<4; i++) {
            int n = in.read();
            if (n == -1) {
                eof = true;
                throw new RecordIOException("Expected unicode value");
            }
            
            char c = (char) n;
            if (isWhitespace(c)) {
                throw new RecordIOException("Expected unicode value");
            }
            
            value.append(c);
        }
        
        try {
            return (char) Integer.valueOf(value.toString(), 16).intValue();
        }
        catch (NumberFormatException ex) {
            throw new RecordIOException("Invalid unicode character '\\u" + value.toString() + "'", ex);
        }
    }
    
    /**
     * Returns whether the given character is whitespace.
     * @param c the character to test
     * @return true if whitespace, false othereise
     */
    protected boolean isWhitespace(char c) {
        switch (c) {
        case ' ':
        case '\n':
        case '\r':
        case '\t':
            return true;
        default:
            return false;
        }
    }
}
