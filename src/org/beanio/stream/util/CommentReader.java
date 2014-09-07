/*
 * Copyright 2011 Kevin Seim
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
package org.beanio.stream.util;

import java.io.*;

/**
 * Skips commented lines read from an input stream.  The input stream must support marking (i.e.
 * {@link Reader#markSupported()} must return <tt>true</tt>).  A line is considered commented if it starts
 * with one of the configured comment indicators.
 * 
 * @author Kevin Seim
 * @since 1.2
 */
public class CommentReader {

    private Reader in;
    private String[] comments;
    private char[] commentBuffer;
    private char recordTerminator = 0;
    private boolean skipLF;
    private boolean eof;

    /**
     * Constructs a new <tt>CommentReader</tt>.
     * @param in the input stream to read
     * @param comments an array of comment identifying strings
     * @throws IllegalArgumentException if the configured comments are invalid or the reader does
     *   not support marking
     */
    public CommentReader(Reader in, String [] comments) throws IllegalArgumentException {
        this(in, comments, null);
    }
    
    /**
     * Constructs a new <tt>CommentReader</tt>.
     * @param in the input stream to read from
     * @param comments an array of comment identifying strings
     * @param recordTerminator the record terminating character
     * @throws IllegalArgumentException if the configured comments are invalid or the reader does
     *   not support marking
     */
    public CommentReader(Reader in, String [] comments, Character recordTerminator) throws IllegalArgumentException {
        if (comments == null) {
            throw new IllegalArgumentException("Comments not set");
        }
        if (!in.markSupported()) {
            throw new IllegalArgumentException("Comments require reader.markSupported() to return true");
        }
        
        this.in = in;
        this.comments = comments;
        if (recordTerminator != null) {
            this.recordTerminator = recordTerminator;
        }
        
        int maximumCommentLength = 0;
        for (String s : comments) {
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Comment value cannot be null or empty string");
            }
            maximumCommentLength = Math.max(maximumCommentLength, s.length());
        }
        commentBuffer = new char[maximumCommentLength + 1];
    }
    
    /**
     * Returns whether the next character should be ignored if its a line feed.
     * @return <tt>true</tt> if the next line feed should be ignored
     */
    public boolean isSkipLF() {
        return skipLF;
    }
    
    /**
     * Returns whether the end of the stream was reached reading a commented line.
     * @return <tt>true</tt> if the end of the stream was reached reading a commented line.
     */
    public boolean isEOF() {
        return eof;
    }
    
    /**
     * Skips comments in the input stream and returns the number of commented lines read.
     * If no commented lines were read, the stream is positioned just as it had been before
     * this method is called.
     * @param initialSkipLF <tt>true</tt> if the first line feed character read should be ignored
     * @return the number of skipped comment lines
     * @throws IOException
     */
    public int skipComments(boolean initialSkipLF) throws IOException {
        skipLF = initialSkipLF;
        
        int lines = 0;
        while (true) {
            // mark our current position in the stream
            in.mark(commentBuffer.length);
            
            // read the start of the line
            int n = in.read(commentBuffer);
            if (n <= 0) {
                break;
            }
            
            String linePrefix;
            if (skipLF && commentBuffer[0] == '\n') {
                linePrefix = new String(commentBuffer, 1, n-1);
            }
            else {
                linePrefix = new String(commentBuffer, 0, n);
            }
            
            // determine if the line prefix matches a configured comment
            boolean commentFound = false;
            for (String s : comments) {
                if (linePrefix.startsWith(s)) {
                    commentFound = true;
                    ++lines;
                    break;
                }
            }

            // if no comment was found, break out
            if (!commentFound) {
                break;
            }

            // finish reading the entire line
            in.reset();                    
            while ((n = in.read()) != -1) {
                char c = (char) n;
                
                if (recordTerminator == 0) {
                    if (skipLF) {
                        skipLF = false;
                        if (c == '\n') {
                            continue;
                        }
                    }
                    
                    if (c == '\n') {
                        break;
                    }
                    else if (c == '\r') {
                        skipLF = true;
                        break;
                    }
                }
                else if (c == recordTerminator) {
                    break;
                }
            }
            if (n == -1) {
                eof = true;
                return lines;
            }
        }
        
        in.reset();
        return lines;
    }
}
