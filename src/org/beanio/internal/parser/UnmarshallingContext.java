/*
 * Copyright 2011-2013 Kevin Seim
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
package org.beanio.internal.parser;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import org.beanio.*;
import org.beanio.stream.*;
import org.w3c.dom.Node;

/**
 * Stores context information needed to unmarshal a bean object.
 * 
 * <p>Subclasses must implement {@link #setRecordValue(Object)} which is called
 * by {@link #nextRecord()} each time a new record is read from the {@link RecordReader}.
 * The Java object used to represent a record is dependent on the {@link RecordReader} 
 * implementation for the stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class UnmarshallingContext extends ParsingContext {

    private Locale locale;
    private MessageFactory messageFactory;
    private RecordReader recordReader;

    // set to true when end of the stream is reached
    private boolean eof;
    // indicates the last record read from the reader has been processed
    private boolean processed = true;
    // the top level component name being unmarshalled
    private String componentName;
    // indicates the component being unmarshalled is a record group (otherwise just a record)
    private boolean isRecordGroup;
    // the last line number read from the record reader
    private int lineNumber = 0;
    // the number of records unmarshalled for the last bean object
    private int recordCount = 0;
    // the current record context
    private ErrorContext recordContext = new ErrorContext();    
    // indicates the record context was queried and must therefore be recreated when the next record is read 
    private boolean dirty;
    // a list of record contexts (for parsing record groups)
    private List<ErrorContext> recordList = new ArrayList<>();
    
    /**
     * Constructs a new <tt>UnmarshallingContext</tt>.
     */
    public UnmarshallingContext() { }
    
    @Override
    public final char getMode() {
        return UNMARSHALLING;
    }
    
    /**
     * Sets the value of the record returned from the <tt>RecordReader</tt>
     * @param value the record value read by a record reader
     * @see RecordReader
     */
    public abstract void setRecordValue(Object value);
    
    /**
     * Converts a <tt>String[]</tt> to a record value.
     * @param array the <tt>String[]</tt> to convert
     * @return the record value, or null if not supported
     */
    public Object toRecordValue(String[] array) {
        return null;
    }
    
    /**
     * Converts a {@link List} to a record value.
     * @param list the <tt>List</tt> to convert
     * @return the record value, or null if not supported
     */
    public Object toRecordValue(List<String> list) {
        return null;
    }

    /**
     * Converts a {@link Node} to a record value.
     * @param node the <tt>Node</tt> to convert
     * @return the record value, or null if not supported
     */
    public Object toRecordValue(Node node) {
        return null;
    }
    
    /**
     * Prepares this context for unmarshalling a record (or group of records that
     * are combined to form a single bean object).
     * @param componentName the record or group name to be unmarshalled
     * @param isRecordGroup true if the component is a group, false if it is a record
     */
    public void prepare(String componentName, boolean isRecordGroup) {
        // clear any context state information before parsing the next record
        if (dirty) {
            // create a new context
            recordContext = new ErrorContext();
            dirty = false;
        }
        else {
            // clear the current context instead
            recordContext.clear();
        }
        
        if (recordCount > 0) {
            recordList.clear();
        }
        
        this.recordCount = 0;
        this.dirty = false;
        this.componentName = componentName;
        this.isRecordGroup = isRecordGroup;
    }
        
    /**
     * This method must be invoked before a record is unmarshalled.
     * @param recordName the name of the record
     */
    public final void recordStarted(String recordName) {
        ++recordCount;
        
        recordContext.setRecordName(recordName);
        recordContext.setLineNumber(getLineNumber());
        recordContext.setRecordText(getRecordReader().getRecordText());
    }
    
    /**
     * Either this method (or {@link #recordSkipped()}) must be invoked after a 
     * record is unmarshalled, even if an error has occurred.
     */
    public final void recordCompleted() {
        processed = true;
        
        // if unmarshalling a record group, add the last record context to the
        // record list and create a new one
        if (isRecordGroup) {
            recordList.add(recordContext);
            recordContext = new ErrorContext();
        }
    }
    
    /**
     * This method should be invoked when a record is skipped.
     */
    public final void recordSkipped() {
        processed = true;
    }
    
    /**
     * Validates all unmarshalled records and throws an exception if any record
     * is invalid.  This method must be invoked after unmarshalling is completed.
     * If unmarshalling fails due to some other fatal exception, there is no need 
     * to call this method.
     * @throws InvalidRecordException if one or more unmarshalled records were invalid
     */
    public final void validate() throws InvalidRecordException {
        // check for errors
        if (isRecordGroup) {
            boolean hasErrors = false;
            for (RecordContext rc : recordList) {
                if (rc.hasErrors()) {
                    hasErrors = true;
                    break;
                }
            }
            if (hasErrors) {
                RecordContext [] rca = new RecordContext[recordList.size()];
                recordList.toArray(rca);
                throw new InvalidRecordGroupException(rca, "Invalid '" + componentName + 
                        "' record group at line " + rca[0].getLineNumber(), componentName);                    
            }
        }
        else {
            if (recordContext.hasErrors()) {
                dirty = true;
                if (lineNumber > 0) {
                    throw new InvalidRecordException(recordContext, "Invalid '" + componentName + 
                         "' record at line " + lineNumber);
                }
                else {
                    throw new InvalidRecordException(recordContext, 
                        "Invalid '" + componentName + "' record");
                }
            }
        }
    }
    
    /**
     * Returns the number of record read for the last unmarshalled bean object.
     * @return the record count for the last unmarshalled bean object
     */
    public final int getRecordCount() {
        return recordCount;
    }
    
    /**
     * Returns the record context for a record read for the last unmarshalled bean object.
     * @param index the index of the record
     * @return the {@link RecordContext}
     * @throws IndexOutOfBoundsException if there is no record for the given index
     */
    public final RecordContext getRecordContext(int index) throws IndexOutOfBoundsException {
        if (isRecordGroup) {
            return recordList.get(index);
        }
        else if (recordCount > 0 && index == 0) {
            dirty = true;
            return recordContext;
        }
        else {
            throw new IndexOutOfBoundsException();
        }
    }
        
    /**
     * Sets the raw field text for a named field.
     * @param fieldName the name of the field
     * @param text the raw field text
     */
    public final void setFieldText(String fieldName, String text) {
        recordContext.setFieldText(fieldName, text, isRepeating());
    }
    
    /**
     * Returns <tt>true</tt> if a field error was reported while parsing
     * this record.
     * @return <tt>true</tt> if a field error was reported
     */
    public final boolean hasFieldErrors() {
        return recordContext.hasFieldErrors();
    }
    
    /**
     * Returns <tt>true</tt> if a record level error was reported while parsing
     * this record.
     * @return <tt>true</tt> if a record level error was reported
     */
    public final boolean hasRecordErrors() {
        return recordContext.hasRecordErrors();
    }

    /**
     * Adds a field error to this record.
     * @param fieldName the name of the field in error
     * @param fieldText the invalid field text
     * @param rule the name of the failed validation rule
     * @param params an optional list of parameters for formatting the error message 
     * @return the formatted field error message 
     */
    public String addFieldError(String fieldName, String fieldText, String rule, Object... params) {
        int lineNumber = recordContext.getLineNumber();
        String recordName = recordContext.getRecordName();
        String recordLabel = messageFactory.getRecordLabel(recordName);
        String fieldLabel = messageFactory.getFieldLabel(recordName, fieldName);

        if (recordLabel == null)
            recordLabel = "'" + recordName + "'";
        if (fieldLabel == null)
            fieldLabel = "'" + fieldName + "'";

        Object[] messageParams;
        if (params.length == 0) {
            messageParams = new Object[] { lineNumber, recordLabel, fieldLabel, fieldText };
        }
        else {
            messageParams = new Object[4 + params.length];
            messageParams[0] = lineNumber;
            messageParams[1] = recordLabel;
            messageParams[2] = fieldLabel;
            messageParams[3] = fieldText;
            System.arraycopy(params, 0, messageParams, 4, params.length);
        }

        String pattern = messageFactory.getFieldErrorMessage(recordName, fieldName, rule);
        MessageFormat mf = new MessageFormat(pattern, locale);
        String message = mf.format(messageParams);
        recordContext.addFieldError(fieldName, message);
        return message;
    }
    
    /**
     * Adds a record level error to this record.
     * @param rule the name of the failed validation rule
     * @param params an optional list of parameters for formatting the error message
     * @return the formatted record error message 
     */
    public final String addRecordError(String rule, Object... params) {
        return addRecordError(recordContext, rule, params);
    }
    
    /**
     * Adds a record level error to this record.
     * @param errorContext the error context to update
     * @param rule the name of the failed validation rule
     * @param params an optional list of parameters for formatting the error message
     * @return the formatted record error message 
     */
    protected String addRecordError(ErrorContext errorContext, String rule, Object... params) {
        int lineNumber = errorContext.getLineNumber();
        String recordName = errorContext.getRecordName();
        
        // find the record label
        String recordLabel = null;
        if (recordName != null) {
            recordLabel = messageFactory.getRecordLabel(recordName);
        }
        if (recordLabel == null) {
            recordLabel = "'" + recordName + "'";
        }

        Object[] messageParams;
        if (params.length == 0) {
            messageParams = new Object[] { lineNumber, recordLabel, errorContext.getRecordText() };
        }
        else {
            messageParams = new Object[3 + params.length];
            messageParams[0] = lineNumber;
            messageParams[1] = recordLabel;
            messageParams[2] = errorContext.getRecordText();
            System.arraycopy(params, 0, messageParams, 3, params.length);
        }

        String pattern = messageFactory.getRecordErrorMessage(recordName, rule);
        String message = new MessageFormat(pattern, locale).format(messageParams);
        
        errorContext.addRecordError(message);
        return message;
    }
    
    public BeanReaderException newMalformedRecordException(RecordIOException cause) {
        return new MalformedRecordException(recordException(null, "malformed", cause.getMessage()), 
            "Malformed record at line " + recordReader.getRecordLineNumber() + ": " + cause.getMessage());
    }

    public BeanReaderException newUnsatisfiedGroupException(String groupName) {
        if (isEOF()) {
            return new UnexpectedRecordException(recordException(groupName, "unsatisfied"), 
                "End of stream reached, expected record from group '" + groupName + "'");
        }
        else {
            return new UnexpectedRecordException(recordException(groupName, "unsatisfied"), 
                "Expected record from group '" + groupName + "' at line " + recordReader.getRecordLineNumber());
        }        
    }
    
    public BeanReaderException newUnsatisfiedRecordException(String recordName) {
        if (isEOF()) {
            return new UnexpectedRecordException(recordException(recordName, "unsatisfied"), 
                "End of stream reached, expected record '" + recordName + "'");
        }
        else {
            return new UnexpectedRecordException(recordException(recordName, "unsatisfied"), 
                "Expected record '" + recordName + "' at line " + recordReader.getRecordLineNumber());
        }
    }
    
    public BeanReaderException recordUnexpectedException(String recordName) {
        return new UnexpectedRecordException(recordException(recordName, "unexpected"), 
            "Unexpected record '" + recordName + "' at line " + recordReader.getRecordLineNumber());
    }
    
    public BeanReaderException recordUnidentifiedException() {
        return new UnidentifiedRecordException(recordException(null, "unidentified"), 
            "Unidentified record at line " + recordReader.getRecordLineNumber());        
    }
    
    /**
     * Handles a record level exception and returns a new {@link ErrorContext} for
     * the exception. 
     * @param recordName the name of the record that failed
     * @param rule the record level rule that failed validation
     * @param params message parameters for formatting the error message
     * @return the created {@link ErrorContext}
     */
    protected ErrorContext recordException(String recordName, String rule, Object... params) {
        processed = true;
        
        ErrorContext ec = new ErrorContext();
        ec.setRecordName(recordName);
        ec.setLineNumber(getLineNumber());
        ec.setRecordText(recordReader.getRecordText());
        addRecordError(ec, rule, params);
        return ec;
    }
    
    /**
     * Reads the next record from the input stream and calls {@link #setRecordValue(Object)}.
     * @throws BeanReaderException if the next node cannot be determined
     */
    public final void nextRecord() throws BeanReaderException {
        if (!processed || eof) {
            return;
        }
        
        // clear the field offset for the next record
        super.clear();
        
        // reset the processed flag
        processed = false;
        
        // read the next record
        Object recordValue;
        try {
            recordValue = recordReader.read();
            if (recordValue == null) {
                eof = true;
                lineNumber++;
            }
            else {
                // set the value of the record (which is implementation specific) on the record
                setRecordValue(recordValue);
                lineNumber = recordReader.getRecordLineNumber();
            }
        }
        catch (RecordIOException e) {
            lineNumber = recordReader.getRecordLineNumber();
            throw newMalformedRecordException(e);
        }
        catch (IOException e) {
            throw new BeanReaderIOException("IOException caught reading from input stream", e);
        }
    }    
    
    /**
     * Returns the last line number read from the input stream.  If the end of stream
     * was reached, the line number is still incremented so that this method returns
     * the expected line number if another record was read.  If newlines are not
     * used to terminate records, this method will always return zero.
     * @return the line number
     */
    public final int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * Returns whether the end of the stream was reached after 
     * {@link #nextRecord()} was called.
     * @return true if the end of the stream was reached
     */
    public final boolean isEOF() {
        return eof;
    }
    
    /**
     * Returns the {@link RecordReader} to read from.
     * @return the {@link RecordReader} to read from
     */
    public final RecordReader getRecordReader() {
        return recordReader;
    }

    /**
     * Sets the {@link RecordReader} to read from.
     * @param recordReader the {@link RecordReader} to read from
     */
    public final void setRecordReader(RecordReader recordReader) {
        this.recordReader = recordReader;
    }
    
    /**
     * Returns the {@link MessageFactory} for formatting error messages.
     * @return the {@link MessageFactory}
     */
    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    /**
     * Sets the {@link MessageFactory} for formatting error messages.
     * @param messageFactory the {@link MessageFactory}
     */
    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    /**
     * Returns the locale to format error messages in.
     * @return the {@link Locale}
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale to format error messages in.
     * @param locale the {@link Locale}
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
