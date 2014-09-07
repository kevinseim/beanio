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

/**
 * Stores configuration settings for parsing CSV formatted streams.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class CsvParserConfiguration {

    private char delimiter = ',';
    private char quote = '"';
    private Character escape = '"';
    private boolean multilineEnabled = false;
    private boolean whitespaceAllowed = false;
    private boolean unquotedQuotesAllowed = false;
    private String[] comments;
    
    private String recordTerminator = null;
    private boolean alwaysQuote = false;
    
    /**
     * Constructs a new <tt>CsvParserConfiguration</tt>.
     */
    public CsvParserConfiguration() { } 
    
    /**
     * Sets the field delimiter. By default, the delimiter is a comma.
     * @param c the character used to delimit fields
     */
    public void setDelimiter(char c) {
        this.delimiter = c;
    }

    /**
     * Returns the field delimiter. By default, the delimiter is a comma.
     * @return the character used to delimit fields
     */
    public char getDelimiter() {
        return delimiter;
    }

    /**
     * Returns the character to use for a quotation mark. Defaults to '"'.
     * @return the quotation mark character
     */
    public char getQuote() {
        return quote;
    }

    /**
     * Sets the character to use for a quotation mark.
     * @param quote the new quotation mark character
     */
    public void setQuote(char quote) {
        this.quote = quote;
    }

    /**
     * Sets the escape character. Quotation marks can be escaped within quoted
     * values using the escape character. For example, using the default escape
     * character, '"Hello ""friend"""' is parsed into 'Hello "friend"'.  Set
     * to <tt>null</tt> to disable escaping.
     * @param c new escape character
     */
    public void setEscape(Character c) {
        this.escape = c;
    }

    /**
     * Returns the escape character. Quotation marks can be escaped within
     * quoted values using the escape character. For example, using the default
     * escape character, '"Hello ""friend"""' is parsed into 'Hello "friend"'.
     * Defaults to the quotation mark, <tt>"</tt>.
     * @return the escape character or <tt>null</tt> if escaping is disabled
     */
    public Character getEscape() {
        return escape;
    }
    
    /**
     * Returns whether escaping is enabled. By default, escaping is enabled.
     * @return <tt>true</tt> if an escape character is enabled
     * @see #getEscape()
     */
    public boolean isEscapeEnabled() {
        return escape != null;
    }

    /**
     * Returns whether a record may span multiple lines (when quoted). Defaults
     * to <tt>false</tt>.
     * @return <tt>true</tt> if a record may span multiple lines
     */
    public boolean isMultilineEnabled() {
        return multilineEnabled;
    }

    /**
     * Sets whether a record may span multiple lines (when quoted).
     * @param multilineEnabled set to true <tt>true</tt> to allow records to
     *   span multiple lines
     */
    public void setMultilineEnabled(boolean multilineEnabled) {
        this.multilineEnabled = multilineEnabled;
    }

    /**
     * Returns whether to ignore unquoted whitespace. Returns <tt>false</tt> by
     * default which causes the following record to raise an exception:
     * 
     * <pre>
     * "Field1", "Field2"
     *          ^
     *        Unquoted whitespace here
     * </pre>
     * 
     * @return <tt>true</tt> if unquoted whitespace is allowed
     */
    public boolean isWhitespaceAllowed() {
        return whitespaceAllowed;
    }

    /**
     * Sets whether unquoted whitespace is ignored.
     * @param whitespaceAllowed set to <tt>true</tt> to ignore unquoted
     *   whitespace
     */
    public void setWhitespaceAllowed(boolean whitespaceAllowed) {
        this.whitespaceAllowed = whitespaceAllowed;
    }

    /**
     * Returns whether quotes are allowed to appear in an unquoted field. Set to
     * <tt>false</tt> by default which will cause the following record to throw
     * an exception:
     * <pre>
     * Field1,Field"2,Field3
     * </pre>
     * @return <tt>true</tt> if quotes may appear in an unquoted field
     */
    public boolean isUnquotedQuotesAllowed() {
        return unquotedQuotesAllowed;
    }

    /**
     * Sets whether quotes are allowed to appear in an unquoted field.
     * @param unquotedQuotesAllowed set to <tt>true</tt> if quotes may appear in
     *   an unquoted field
     */
    public void setUnquotedQuotesAllowed(boolean unquotedQuotesAllowed) {
        this.unquotedQuotesAllowed = unquotedQuotesAllowed;
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
    
    /**
     * Returns <tt>true</tt> if fields should always be quoted when marshalled.  
     * Defaults to <tt>false</tt> which will only quote fields containing a quotation mark,
     * delimiter, line feeds or carriage return.
     * @return <tt>true</tt> if all fields will be quoted
     */
    public boolean isAlwaysQuote() {
        return alwaysQuote;
    }

    /**
     * Set to <tt>true</tt> to quote every field when marshalled.  If <tt>false</tt>, a field
     * will only be quoted if it contains a quotation mark, delimiter, line feed
     * or carriage return.
     * @param alwaysQuote set to <tt>true</tt> to quote every field regardless
     *   of content
     */
    public void setAlwaysQuote(boolean alwaysQuote) {
        this.alwaysQuote = alwaysQuote;
    }

    /**
     * Returns the text used to terminate a record.  By default, the record
     * terminator is set to the value of the <tt>line.separator</tt> system property.
     * @return the record termination text
     */
    public String getRecordTerminator() {
        return recordTerminator;
    }

    /**
     * Sets the text used to terminate a record.  If set to <tt>null</tt>, the 
     * the value of the <tt>line.separator</tt> system property is used to terminate
     * records.
     * @param recordTerminator the record termination text
     */
    public void setRecordTerminator(String recordTerminator) {
        this.recordTerminator = recordTerminator;
    }
    
    /**
     * Returns the text used to terminate a record.  By default, the
     * line separator is set using the 'line.separator' system property.
     * @return the line separation text
     * @deprecated
     */
    public String getLineSeparator() {
        return recordTerminator;
    }

    /**
     * Sets the text used to terminate a record.  If set to <tt>null</tt>, the default
     * line separator is used based on the 'line.separator' system property.
     * @param lineSeparator the line separation text
     * @deprecated
     */
    public void setLineSeparator(String lineSeparator) {
        this.recordTerminator = lineSeparator;
    }
}
