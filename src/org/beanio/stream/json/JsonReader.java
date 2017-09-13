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

import org.beanio.internal.util.RecordFilterReader;
import org.beanio.stream.*;

/**
 * A {@link RecordReader} implementation for JSON formatted records.
 * 
 * @author Kevin Seim
 * @since 2.0
 * @see JsonRecordParserFactory
 */
public class JsonReader extends JsonReaderSupport implements RecordReader {

    // TODO support BigDecimal/BigInteger numbers...
    
    private RecordFilterReader filter;
    private String recordText;
    private int recordLineNumber;
    
    /**
     * Constructs a new <tt>JsonReader</tt>.
     * @param in the {@link Reader} to read from
     */
    public JsonReader(Reader in) {
        filter = new RecordFilterReader(in);
        setReader(filter);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordReader#read()
     */
    @Override
    public Map<String,Object> read() throws IOException, RecordIOException {
        if (eof) {
            return null;
        }
        
        try {
            int n;
            while ((n = in.read()) != -1) {
                char c = (char)n;
                if (c == '{') {
                    recordLineNumber = filter.getLineNumber();
                    filter.recordStarted("{");
                    Map<String,Object> value = readObject();
                    recordText = filter.recordCompleted();
                    return value;
                }
                else if (!isWhitespace(c)) {
                    throw new RecordIOException("Unexpected character");
                }
            }
        }
        catch (RecordIOException ex) {
            throw new RecordIOException(ex.getMessage() + " at line " + filter.getLineNumber() +
                ", near position " + filter.getPosition(), ex);
        }
        
        eof = true;
        return null;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordReader#close()
     */
    @Override
    public void close() throws IOException {
        if (in != null) {
            try {
                in.close();
            }
            finally {
                in = null;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordReader#getRecordLineNumber()
     */
    @Override
    public int getRecordLineNumber() {
        return recordLineNumber;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordReader#getRecordText()
     */
    @Override
    public String getRecordText() {
        return recordText;
    }
    
    public static void main(String [] args) throws Exception {
        String text = "{ \"firstName\\u004B\" : \"kevin\", \"lastName\" : \"seim\", \"value\":10,\"age\":true" +
            ", \"object\" : { \"field\" : [ 20, 10, 30 ] } }\r\n\r { \"firstName\": 10 }";
        
        JsonReader in = new JsonReader(new StringReader(text));
        Object value = null;
        while ((value = in.read()) != null) {
            System.out.println(value);
            System.out.println(in.getRecordLineNumber());
            System.out.println(in.getRecordText());
        }
    }

}
