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

/**
 * Stores configuration settings for parsing delimited formatted streams.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class DelimitedParserConfiguration {

    private char delimiter = '\t';
    private Character escape = null;
    private Character lineContinuationCharacter = null;
    private String recordTerminator = null;
    private String[] comments;
    
    /**
     * Constructs a new <tt>DelimitedParserConfiguration</tt>.
     */
    public DelimitedParserConfiguration() { }

    /**
     * Constructs a new <tt>DelimitedReaderConfiguration</tt>.
     * @param delimiter the field delimiting character
     */
    public DelimitedParserConfiguration(char delimiter) { 
        this.delimiter = delimiter;
    }
    
    /**
     * Returns the field delimiting character.  Defaults to tab.
     * @return the record delimiter
     */
    public char getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the field delimiting character to use.
     * @param delimiter the record delimiting character
     */
    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Returns the character used by the input stream to escape delimiters and itself.
     * By default, escaping is disabled.
     * @return the escape character or <tt>null</tt> if escaping is disabled
     */
    public Character getEscape() {
        return escape;
    }

    /**
     * Sets the character used by the input stream to escape delimiters and itself.
     * If set to null, escaping is disabled.
     * @param escapeCharacter the new escape character
     */
    public void setEscape(Character escapeCharacter) {
        this.escape = escapeCharacter;
    }

    /**
     * Returns the line continuation character or <tt>null</tt> if line
     * continuation is disabled.  By default, line continuation is disabled.
     * @return the line continuation character or <tt>null</tt> if disabled
     */
    public Character getLineContinuationCharacter() {
        return lineContinuationCharacter;
    }

    /**
     * Sets the line continuation character.  May be set to <tt>null</tt> to
     * disable line continuation.
     * @param lineContinuationCharacter the line continuation character
     */
    public void setLineContinuationCharacter(Character lineContinuationCharacter) {
        this.lineContinuationCharacter = lineContinuationCharacter;
    }

    /**
     * Returns whether an escape character is enabled.
     * @return <tt>true</tt> if enabled, false otherwise
     */
    public boolean isEscapeEnabled() {
        return escape != null;
    }

    /**
     * Returns whether the line continuation character is enabled.
     * @return <tt>true</tt> if the line continuation character is enabled
     */
    public boolean isLineContinationEnabled() {
        return lineContinuationCharacter  != null;
    }
    
    /**
     * Returns the character used to mark the end of a record.  By default,
     * a carriage return (CR), line feed (LF), or CRLF sequence is used to
     * signify the end of the record.
     * @return the record termination character
     */
    public String getRecordTerminator() {
        return recordTerminator;
    }

    /**
     * Sets the character used to mark the end of a record.  If set to <tt>null</tt>,
     * a carriage return (CR), line feed (LF), or CRLF sequence is used.
     * @param recordTerminator the record termination character
     */
    public void setRecordTerminator(String recordTerminator) {
        this.recordTerminator = recordTerminator;
    }

    /**
     * Returns the array of comment prefixes.  If a line read from a stream begins
     * with a configured comment prefix, the line is ignored.  By default, no lines
     * are considered commented.
     * @return the array of comment prefixes
     */
    public String[] getComments() {
        return comments;
    }

    /**
     * Sets the array of comment prefixes.  If a line read from a stream begins
     * with a configured comment prefix, the line is ignored. 
     * @param comments the array of comment prefixes
     */
    public void setComments(String[] comments) {
        this.comments = comments;
    }
    
    /**
     * Returns whether one or more comment prefixes have been configured.
     * @return <tt>true</tt> if one or more comment prefixes have been configured
     */
    public boolean isCommentEnabled() {
        return comments != null && comments.length > 0;
    }
}
