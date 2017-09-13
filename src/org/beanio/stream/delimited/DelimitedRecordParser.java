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

import java.util.*;

import org.beanio.stream.*;

/**
 * A combined {@link RecordMarshaller} and {@link RecordUnmarshaller} implementation 
 * for delimited formatted records.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class DelimitedRecordParser implements RecordUnmarshaller, RecordMarshaller {

    private char delim;
    private char escape;
    private boolean escapeEnabled;
    
    private List<String> fieldList = new ArrayList<>();
    
    /**
     * Constructs a new <tt>DelimitedRecordParser</tt>.
     */
    public DelimitedRecordParser() {  
        this(new DelimitedParserConfiguration());
    }
    
    /**
     * Constructs a new <tt>DelimitedRecordParser</tt>.
     * @param config the parser configuration settings
     */
    public DelimitedRecordParser(DelimitedParserConfiguration config) {
        delim = config.getDelimiter();
        
        if (config.getEscape() == null) {
            escapeEnabled = false;
        }
        else {
            escapeEnabled = true;
            escape = config.getEscape();

            if (delim == escape) {
                throw new IllegalArgumentException("The field delimiter canot match the escape character");
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordParser#parse(java.lang.String)
     */
    @Override
    public String[] unmarshal(String text) {
        fieldList.clear();
        
        boolean escaped = false;
        StringBuilder field = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (escaped) {
                escaped = false;
                
                if (c == escape || c == delim) {
                    field.append(c);
                    continue;
                }
                
                field.append(escape);
            }
            
            if (c == escape && escapeEnabled) {
                escaped = true;
            }
            else if (c == delim) {
                fieldList.add(field.toString());
                field = new StringBuilder();
            }
            else {
                field.append(c);
            }
        }
        
        if (escaped) {
            field.append(escape);
        }
        
        fieldList.add(field.toString());
        
        String [] record = new String[fieldList.size()];
        fieldList.toArray(record);
        return record;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordMarshaller#marshal(java.lang.Object)
     */
    @Override
    public String marshal(Object record) {
        return marshal((String[]) record);
    }

    /**
     * Marshals a <tt>String</tt> array into a delimited record.
     * @param record the <tt>String[]</tt> to marshal
     * @return the formatted record text
     */
    public String marshal(String[] record) {
        StringBuilder text = new StringBuilder();
        
        if (escapeEnabled) {
            int pos = 0;
            for (String field : record) {
                if (pos++ > 0)
                    text.append(delim);

                char[] cs = field.toCharArray();
                for (int i = 0, j = cs.length; i < j; i++) {
                    if (cs[i] == delim || cs[i] == escape) {
                        text.append(escape);
                    }
                    text.append(cs[i]);
                }
            }
        }
        else {
            int pos = 0;
            for (String field : record) {
                if (pos++ > 0) {
                    text.append(delim);
                }
                text.append(field);
            }
        }
        
        return text.toString();
    }
}
