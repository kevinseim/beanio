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

/**
 * Base class for writing a JSON formatted stream.  
 * 
 * The {@link #init(Writer, JsonParserConfiguration)} method must be invoked before
 * any writeXXX method.
 * 
 * @author Kevin Seim
 * @since 2.0
 * @see JsonRecordParserFactory
 */
public abstract class JsonWriterSupport {

    private static final String DEFAULT_LINE_SEP = System.getProperty("line.separator");
    
    private Writer out;
    
    private boolean pretty = true;
    private int indent = 2;
    private String lineSeparator = DEFAULT_LINE_SEP;
    
    private transient boolean indentEnabled = true;
    private transient int level = 0;
    
    /**
     * Constructs a new <tt>JsonWriterSupport</tt>.
     */
    JsonWriterSupport() { }
    
    /**
     * Initializes this object.
     * @param out the {@link Writer} to write to
     * @param config the {@link JsonParserConfiguration}
     */
    protected void init(Writer out, JsonParserConfiguration config) {
        this.out = out;
        this.pretty = config.isPretty();
        this.indent = config.getIndentation();
        this.indentEnabled = config.isPretty();
        if (config.getLineSeparator() != null) {
            this.lineSeparator = config.getLineSeparator();
        }
    }
    
    /**
     * Writes a {@link Map} in JSON object format.
     * @param map the Map to write
     * @throws IOException
     */
    protected void write(Map<String,Object> map) throws IOException {
        ++level;
        out.write('{');

        boolean comma = false;
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            if (comma) {
                out.write(',');
                if (indentEnabled)
                    newLine();
                else if (pretty)
                    out.write(' ');
            }
            else {   
                checkLine();
            }
            
            writeString(entry.getKey());
            if (pretty) {
                out.write(": ");
            }
            else {
                out.write(':');
            }
            writeValue(entry.getValue());
            
            comma = true;
        }
        
        --level;
        checkLine();
        out.write('}');
    }
    
    /**
     * Writes a value in JSON format.  The value must be a <tt>String</tt>, <tt>Number</tt>,
     * <tt>Boolean</tt>, <tt>Map</tt> or <tt>Iterable</tt> (array), or else {@link Object#toString()}
     * is called and the value is formatted as a JSON string.
     * @param value the <tt>Object</tt> to write
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    protected void writeValue(Object value) throws IOException {
        if (value == null) {    
            out.write("null");
        }
        else if (value instanceof String) {
            writeString((String)value);
        }
        else if (value instanceof Boolean || value instanceof Number) {
            out.write(value.toString());
        }
        else if (value instanceof Map) {
            write((Map<String,Object>)value);
        }
        else if (value instanceof Iterable) {
            writeArray((Iterable<Object>)value);
        }
        else {
            writeString(value.toString());
        }
    }
    
    /**
     * Writes a {@link String} in JSON string format.
     * @param text the {@link String} to write
     * @throws IOException
     */
    protected void writeString(String text) throws IOException {
        out.write('"');
        for (char c : text.toCharArray()) {
            switch (c) {
            case '"':
            case '\\':
            case '/':
                out.write('\\');
                out.write(c);
                break;
            case '\t':
                out.write("\\t");
                break;
            case '\n':
                out.write("\\n");
                break;
            case '\r':
                out.write("\\r");
                break;
            case '\f':
                out.write("\\f");
                break;
            case '\b':
                out.write("\\b");
                break;
            default:
                out.write(c);
                break;
            }
        }
        out.write('"');
    }
    
    /**
     * 
     * @param list
     * @throws IOException
     *
    protected void writeArray(List<Object> list) throws IOException {
        if (list.isEmpty()) {
            out.write("[]");
            return;
        }
        
        out.write('[');
        
        if (pretty) {
            boolean updated = indentEnabled;
            indentEnabled = false;
            
            // write array of objects:
            if ((list.get(0) instanceof Map)) {
                ++level;
                newLine();
                boolean comma = false;
                for (Object value : list) {
                    if (comma) {
                        out.write(',');
                        newLine();
                    }
                    writeValue(value);
                    comma = true;
                }
                --level;
                newLine();
            }
            // write array of other
            else {
                boolean comma = false;
                for (Object value : list) {
                    if (comma) {
                        out.write(", ");
                    }
                    writeValue(value);
                    comma = true;
                }
            }
            
            if (updated) {
                indentEnabled = true;
            }
        }
        else {
            boolean comma = false;
            for (Object value : list) {
                if (comma) {
                    out.write(',');
                }
                writeValue(value);
                comma = true;
            }
        }
        
        out.write(']');
    }
    */

    /**
     * Writes an {@link Iterable} in JSON array format.
     * @param iterable the {@link Iterable} to write
     * @throws IOException
     */
    protected void writeArray(Iterable<Object> iterable) throws IOException {
        Iterator<Object> iter = iterable.iterator();
        if (!iter.hasNext()) {
            out.write("[]");
            return;
        }
        
        out.write('[');
        
        Object value = iter.next();
        
        if (pretty) {
            boolean updated = indentEnabled;
            indentEnabled = false;
            
            // write array of objects:
            if (value instanceof Map) {
                ++level;
                newLine();
                writeValue(value);
                while (iter.hasNext()) {
                    out.write(',');
                    newLine();
                    writeValue(iter.next());
                }
                --level;
                newLine();
            }
            // write array of other
            else {
                writeValue(value);
                while (iter.hasNext()) {
                    out.write(", ");
                    writeValue(iter.next());
                }
            }
            
            if (updated) {
                indentEnabled = true;
            }
        }
        else {
            writeValue(value);
            while (iter.hasNext()) {
                out.write(',');
                writeValue(iter.next());
            }
        }
        
        out.write(']');
    }
    
    private void checkLine() throws IOException {
        if (indentEnabled) {
            newLine();
        }
    }
    
    private void newLine() throws IOException {
        out.write(lineSeparator);
        for (int i=0, j=level * indent; i<j; i++) {
            out.write(' ');
        }
    }

    protected boolean isPretty() {
        return pretty;
    }

    protected int getIndent() {
        return indent;
    }

    protected String getLineSeparator() {
        return lineSeparator;
    }
}
