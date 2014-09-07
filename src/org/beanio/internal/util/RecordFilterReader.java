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
package org.beanio.internal.util;

import java.io.*;

/**
 * A {@link Reader} implementation for tracking the current line number, current 
 * position and record text.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class RecordFilterReader extends FilterReader {

    private int lineNumber = 1;
    private long position = 0;
    private StringBuilder record;
    private boolean skipLF = false;
    private int mark = -1;
    
    /**
     * Constructs a new <tt>RecordFilterReader</tt>.
     * @param in the {@link Reader} to read from
     */
    public RecordFilterReader(Reader in) {
        super(in);
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public long getPosition() {
        return position;
    }
    
    public void recordStarted() {
        record = new StringBuilder();
    }

    public void recordStarted(String text) {
        record = new StringBuilder(text);
    }
    
    public String recordCompleted() throws IllegalStateException {
        if (record == null) {
            throw new IllegalStateException("recordStarted() not called");
        }
        
        String text = record.toString();
        record = null;
        return text;
    }

    @Override
    public int read() throws IOException {
        int n = super.read();
        
        if (n != -1) {
            char c = (char) n;
            
            if (record != null) {
                record.append(c);
            }
            
            switch (c) {
            case '\n':
                if (skipLF) {
                    skipLF = false;
                }
                else {
                    ++lineNumber;
                    position = 0;
                }
                break;
                
            case '\r':
                skipLF = true;
                ++lineNumber;
                position = 0;
                break;
            
            default:
                position++;
                break;
                
            }
        }
        
        return n;
    }
    
    @Override
    public void mark(int readAheadLimit) throws IOException {
        super.mark(readAheadLimit);
        mark = record == null ? -1 : record.length();
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        
        if (mark < 0) {
            record = null;
        }
        else {
            record.setLength(mark);
        }
    }
}
