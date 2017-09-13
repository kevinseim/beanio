/*
 * Copyright 2010-2011 Kevin Seim
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
package org.beanio.stream.fixedlength;

import java.io.*;

import org.beanio.stream.*;
import org.beanio.stream.util.CommentReader;

/**
 * A <tt>FixedLengthReader</tt> is used to read records from a fixed length
 * file or input stream.  A fixed length record is represented using the
 * {@link String} class.  Records must be terminated by a single 
 * configurable character, or by default, any of the following: line feed (LF), 
 * carriage return (CR), or CRLF combination.
 * <p>
 * If a record may span multiple lines, a single line continuation
 * character may be configured.  The line continuation character must immediately 
 * precede the record termination character.  Note that line continuation characters 
 * are not included in the record text. 
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class FixedLengthReader implements RecordReader {

    private char lineContinuationChar = '\\';
    private boolean multilineEnabled = false;
    private char recordTerminator = 0;
    private CommentReader commentReader = null;
    
    private transient Reader in;
    private transient String recordText;
    private transient int recordLineNumber;
    private transient int lineNumber = 0;
    private transient boolean skipLF = false;
    private transient boolean eof = false;

    /**
     * Constructs a new <tt>FixedLengthReader</tt>.  By default, line
     * continuation is disabled. 
     * @param in the input stream to read from
     */
    public FixedLengthReader(Reader in) {
        this(in, (FixedLengthParserConfiguration) null);
    }
    
    /**
     * Constructs a new <tt>FixedLengthReader</tt>.
     * @param in the input stream to read from
     * @param config the reader configuration settings or <tt>null</tt> to accept defaults
     * @throws IllegalArgumentException if a configuration setting is invalid
     * @since 1.2
     */
    public FixedLengthReader(Reader in, FixedLengthParserConfiguration config) throws IllegalArgumentException {
        if (config == null) {
            config = new FixedLengthParserConfiguration();
        }
        
        this.in = in;
        
        if (config.getRecordTerminator() != null) {
            String s = config.getRecordTerminator();
            if ("\r\n".equals(s)) {
                // use default
            }
            else if (s.length() == 1) {
                this.recordTerminator = s.charAt(0);
            }
            else if (s.length() > 1) {
                throw new IllegalArgumentException("Record terminator must be a single character");
            }
        }
        
        if (config.getLineContinuationCharacter() == null) {
            this.multilineEnabled = false;
        }
        else {
            this.multilineEnabled = true;
            this.lineContinuationChar = config.getLineContinuationCharacter();
            
            if (recordTerminator != 0 && lineContinuationChar == recordTerminator) {
                throw new IllegalArgumentException("The line continuation character and record terminator cannot match.");
            }
        }
        
        if (config.isCommentEnabled()) {
            commentReader = new CommentReader(in, config.getComments(), this.recordTerminator);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.line.RecordReader#getRecordLineNumber()
     */
    @Override
    public int getRecordLineNumber() {
        if (recordLineNumber < 0) {
            return recordLineNumber;
        }
        return recordTerminator == 0 ? recordLineNumber : 0;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.line.RecordReader#getRecordText()
     */
    @Override
    public String getRecordText() {
        return recordText;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.line.RecordReader#read()
     */
    @Override
    public String read() throws IOException, RecordIOException {
        if (eof) {
            recordText = null;
            recordLineNumber = -1;
            return null;
        }

        ++lineNumber;
        
        // skip commented lines
        if (commentReader != null) {
            int lines = commentReader.skipComments(skipLF);
            if (lines > 0) {
                if (commentReader.isEOF()) {
                    eof = true;
                    recordText = null;
                    recordLineNumber = -1;
                    return null;
                }
                else {
                    lineNumber += lines;
                    skipLF = commentReader.isSkipLF();
                }
            }
        }
        
        int lineOffset = 0;

        boolean continued = false; // line continuation
        boolean eol = false; // end of record flag
        StringBuilder text = new StringBuilder();
        StringBuilder record = new StringBuilder();

        int n;
        while (!eol && (n = in.read()) != -1) {
            char c = (char) n;

            // skip '\n' after a '\r'
            if (skipLF) {
                skipLF = false;
                if (c == '\n') {
                    continue;
                }
            }

            // handle line continuation
            if (continued) {
                continued = false;

                text.append(c);

                if (endOfRecord(c)) {
                    ++lineNumber;
                    ++lineOffset;
                    continue;                    
                }
                else {
                    record.append(lineContinuationChar);
                }
            }

            if (multilineEnabled && c == lineContinuationChar) {
                continued = true;
            }
            else if (endOfRecord(c)) {
                eol = true;
            }
            else {
                text.append(c);
                record.append(c);
            }
        }

        // update the record line number
        recordLineNumber = lineNumber - lineOffset;
        recordText = text.toString();

        // if eol is true, we're done; if not, then the end of file was reached 
        // and further validation is needed
        if (eol) {
            return record.toString();
        }
        
        eof = true;

        if (continued) {
            recordText = null;
            recordLineNumber = -1;
            throw new RecordIOException("Unexpected end of stream after line continuation at line " + lineNumber);
        }

        if (recordText.length() == 0) {
            recordText = null;
            recordLineNumber = -1;
            return null;
        }
        else {
            return record.toString();
        }
    }

    /**
     * Returns <tt>true</tt> if the given character matches the record separator.  This
     * method also updates the internal <tt>skipLF</tt> flag.
     * @param c the character to test
     * @return <tt>true</tt> if the character signifies the end of the record
     */
    private boolean endOfRecord(char c) {
        if (recordTerminator == 0) {
            if (c == '\r') {
                skipLF = true;
                return true;
            }
            else if (c == '\n') {
                return true;
            }
            return false;
        }
        else {
            return c == recordTerminator;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.line.RecordReader#close()
     */
    @Override
    public void close() throws IOException {
        in.close();
    }
}
