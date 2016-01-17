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

import java.util.*;

import org.beanio.stream.*;

/**'
 * A combined {@link RecordMarshaller} and {@link RecordUnmarshaller} implementation 
 * for CSV formatted records.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class CsvRecordParser implements RecordMarshaller, RecordUnmarshaller {

    private char delim = ',';
    private char quote = '"';
    private char endQuote = '"';
    private char escape = '"';
    private boolean escapeEnabled = true;
    private boolean whitespaceAllowed = false;
    private boolean unquotedQuotesAllowed = false;
    private boolean alwaysQuote = false;
    
    private List<String> fieldList = new ArrayList<>();
    
    /**
     * Constructs a new <tt>CsvRecordParser</tt>.
     */
    public CsvRecordParser() { 
        this(null);
    }
    
    /**
     * Constructs a new <tt>CsvRecordParser</tt>.
     * @param config the {@link CsvParserConfiguration}
     */
    public CsvRecordParser(CsvParserConfiguration config) {
        if (config == null) {
            config = new CsvParserConfiguration();
        }
        
        delim = config.getDelimiter();
        if (this.delim == ' ') {
            throw new IllegalArgumentException("The CSV field delimiter '" + this.delim + 
                "' is not supported");
        }
        quote = config.getQuote();
        endQuote = quote;
        if (quote == delim) {
            throw new IllegalArgumentException("The CSV field delimiter cannot " +
                "match the character used for the quotation mark.");
        }
        whitespaceAllowed = config.isWhitespaceAllowed();
        unquotedQuotesAllowed = config.isUnquotedQuotesAllowed();
        if (config.getEscape() != null) {
            escapeEnabled = true;
            escape = config.getEscape();
            if (escape == delim) {
                throw new IllegalArgumentException(
                    "The CSV field delimiter cannot match the escape character.");
            }
        }
        else {
            escapeEnabled = false;
        }
        
        alwaysQuote = config.isAlwaysQuote();
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParser#parse(java.lang.String)
     */
    @Override
    public Object unmarshal(String text) throws RecordIOException {
        fieldList.clear();
        
        StringBuilder field = new StringBuilder();
        
        int state = 0; // current state
        int whitespace = 0;
        boolean escaped = false; // last character read matched the escape char
        
        for (char c : text.toCharArray()) {
            // handle escaped characters
            if (escaped) {
                escaped = false;

                // an escape character can be used to escape itself or an end quote
                if (c == endQuote || c == escape) {
                    field.append(c);
                    continue;
                }

                // the field was ended if escape and endQuote are the same character such as "
                if (escape == endQuote) {
                    fieldList.add(field.toString());
                    field = new StringBuilder();
                    state = 10;
                }
            }
            
            switch (state) {
            
            // initial state (beginning of line, or next value)
            case 0: 
                if (c == delim) {
                    fieldList.add(createWhitespace(whitespace));
                    whitespace = 0;
                }
                else if (c == quote) {
                    whitespace = 0;
                    state = 1; // look for trailing quote
                }
                else if (c == ' ') {
                    if (!whitespaceAllowed) {
                        field.append(c);
                        state = 2; // look for next delimiter
                    }
                    else {
                        ++whitespace;
                    }
                }
                else {
                    field.append(createWhitespace(whitespace));
                    whitespace = 0;
                    field.append(c);
                    state = 2; // look for next delimiter
                }
                break;
            
            // quoted field, look for trailing quote at end of field
            case 1: 
                if (c == escape && escapeEnabled) {
                    escaped = true;
                }
                else if (c == endQuote) {
                    fieldList.add(field.toString());
                    field = new StringBuilder();
                    state = 10; // look for next delimiter
                }
                else {
                    field.append(c);
                }
                break;
            
            // unquoted field, look for next delimiter
            case 2: 
                if (c == delim) {
                    fieldList.add(field.toString());
                    field = new StringBuilder();
                    state = 0;
                }
                else if (c == quote && !unquotedQuotesAllowed) {
                    throw new RecordIOException("Quotation character '" + quote + "' must be quoted");
                }
                else {
                    field.append(c);
                }
                break;            

            // quoted field, after final quote read
            case 10: 
                if (c == ' ') {
                    if (!whitespaceAllowed) {
                        throw new RecordIOException("Invalid whitespace found outside of a quoted field");
                    }
                }
                else if (c == delim) {
                    state = 0;
                }
                else {
                    throw new RecordIOException("Invalid character found outside of quoted field");
                }
                break;
            }
        }
        
        // handle escaped mode
        if (escaped) {
            if (escape == endQuote) {
                fieldList.add(field.toString());
                state = 10;
            }
        }
        
        // validate current state...
        switch (state) {
        case 0:
            fieldList.add(createWhitespace(whitespace));
            break;
        case 1:
            throw new RecordIOException("Expected end quote before end of record");
        case 2:
            fieldList.add(field.toString());
            break;
        case 10:
            break;
        }

        String[] record = new String[fieldList.size()];
        record = fieldList.toArray(record);
        return record;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordMarshaller#marshal(java.lang.Object)
     */
    @Override
    public String marshal(Object record) {
        return marshal((String[])record);
    }
    
    /**
     * Marshals a <tt>String</tt> array into a CSV formatted record.
     * @param record the <tt>String[]</tt> to marshal
     * @return the CSV formatted record
     */
    public String marshal(String[] record) {
        StringBuilder text = new StringBuilder();
        
        int pos = 0;
        for (String field : record) {
            if (pos++ > 0) {
                text.append(delim);
            }
            
            char [] cs = field.toCharArray();
            
            boolean quoted = alwaysQuote || mustQuote(cs);
            if (quoted) {
                text.append(quote);
            }
            
            for (char c : cs) {
                if (c == endQuote || c == escape) {
                    text.append(escape);
                }
                
                text.append(c);
            }
            
            if (quoted) {
                text.append(endQuote);
            }
        }
        
        return text.toString();
    }

    /**
     * 
     * @param size
     * @return
     */
    private String createWhitespace(int size) {
        if (size == 0)
            return "";

        StringBuilder b = new StringBuilder(size);
        for (int i = 0; i < size; i++)
            b.append(' ');
        return b.toString();
    }
    
    /**
     * Returns true if the given field must be quoted.
     * @param cs the field test
     * @return <tt>true</tt> if the given field must be quoted
     */
    private boolean mustQuote(char [] cs) {
        for (char c : cs) {
            if (c == delim)
                return true;
            if (c == quote)
                return true;
            if (c == '\n')
                return true;
            if (c == '\r')
                return true;
        }
        return false;
    }
}
