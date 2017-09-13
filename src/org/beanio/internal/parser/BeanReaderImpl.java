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

import java.io.*;

import org.beanio.*;

/**
 * A {@link BeanReader} implementation.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class BeanReaderImpl implements BeanReader {
    
    // stream specific unmarshalling context
    private UnmarshallingContext context;
    // the root component of the parser tree
    private Selector layout;
    // the line number of for the first record of the last bean object read
    private int lineNumber;
    // the record or group name of the last bean object read
    private String recordName;
    // error handler
    private BeanReaderErrorHandler errorHandler;
    // whether to ignore unidentified records
    private boolean ignoreUnidentifiedRecords;
    
    /**
     * Constructs a new <tt>BeanReaderImpl</tt>.
     * @param context the {@link UnmarshallingContext}
     * @param layout the root component of the parser tree
     */
    public BeanReaderImpl(UnmarshallingContext context, Selector layout) {
        this.context = context;
        this.layout = layout;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReader#read()
     */
    @Override
    public Object read() {
        ensureOpen();
        
        while (true) {
            if (layout == null) {
                return null;
            }
            
            try {
                Object bean = internalRead();
                if (bean != null) {
                    return bean;
                }
                if (context.isEOF()) {
                    return null;
                }
            }
            catch (BeanReaderException ex) {
                // if an exception is thrown when parsing a dependent record,
                // there is little chance of recovery
                handleError(ex);
                continue;
            }
            catch (BeanIOException ex) {
                // wrap the generic exception in a BeanReaderException
                BeanReaderException e = (BeanReaderException) new BeanReaderException(
                    "Fatal BeanIOException caught", ex).fillInStackTrace();
                handleError(e);
                continue;
            }
        }
    }
    
    private Object internalRead() {
        Selector parser = null;
        
        try {
            // match the next record, parser may be null if EOF was reached
            parser = nextRecord();
            if (parser == null) {
                return null;
            }
            
            // notify the unmarshalling context that we are about to unmarshal a new record
            context.prepare(parser.getName(), parser.isRecordGroup());
            
            // unmarshal the record
            try {
                parser.unmarshal(context);
            }
            catch (AbortRecordUnmarshalligException ex) { }
            
            // this will throw an exception if an invalid record was unmarshalled
            context.validate();
            
            // return the unmarshalled bean object
            return parser.getValue(context);
        }
        finally {
            if (parser != null) {
                parser.clearValue(context);
            }
        }
    }
    
    /**
     * Reads the next record from the input stream and returns the matching record node.
     * @return the next matching record node, or <tt>null</tt> if the end of the stream
     *   was reached
     * @throws BeanReaderException if the next node cannot be determined
     */
    private Selector nextRecord() throws BeanReaderException {
        Selector parser = null;
        
        // clear the current record name
        recordName = null;
        
        do {
            // read the next record
            context.nextRecord();
    
            // validate all record nodes are satisfied when the end of the file is reached
            if (context.isEOF()) {
                try {
                    // calling close will determine if all min occurs have been met
                    Selector unsatisfied = layout.close(context);
                    if (unsatisfied != null) {
                        if (unsatisfied.isRecordGroup()) {
                            throw context.newUnsatisfiedGroupException(unsatisfied.getName());
                        }
                        else {
                            throw context.newUnsatisfiedRecordException(unsatisfied.getName());
                        }
                    }
                    return null;
                }
                finally {
                    layout = null;
                    lineNumber = -1;
                }
            }
            
            // update the last line number read
            lineNumber = context.getLineNumber();
            
            try {
                parser = layout.matchNext(context);
            }
            catch (UnexpectedRecordException ex) {
                // when thrown, 'parser' is null and the error is handled below
            }
            
            if (parser == null && ignoreUnidentifiedRecords) {
                context.recordSkipped();
            }
            else {
                break;
            }
        }
        while (true);

        if (parser == null) {
            parser = layout.matchAny(context);

            if (parser != null) {
                throw context.recordUnexpectedException(parser.getName());
            }
            else {
                throw context.recordUnidentifiedException();
            }
        }
        
        recordName = parser.getName();
        return parser;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReader#skip(int)
     */
    @Override
    public int skip(int count) throws BeanReaderIOException, MalformedRecordException,
        UnidentifiedRecordException, UnexpectedRecordException {
        
        ensureOpen();
        
        if (layout == null) {
            return 0;
        }
        
        int n = 0;
        while (n < count) {
            // find the next matching record node
            Selector node = nextRecord();

            // node is null when the end of the stream is reached
            if (node == null) {
                return n;
            }
            
            node.skip(context);
            
            // if the bean definition does not have a property type configured, it would not
            // have been mapped to a bean object
            if (node.getProperty() != null) {
                ++n;
            }
        }
     
        return n;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.impl.AbstractBeanReader#close()
     */
    @Override
    public void close() throws BeanReaderIOException {
        ensureOpen();
        
        try {
            context.getRecordReader().close();
        }
        catch (IOException ex) {
            throw new BeanReaderIOException("Failed to close record reader", ex);
        }
        finally {
            context = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReader#getName()
     */
    @Override
    public String getRecordName() {
        return recordName;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReader#getLineNumber()
     */
    @Override
    public int getLineNumber() {
        return lineNumber;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReader#getRecordCount()
     */
    @Override
    public int getRecordCount() {
        return context == null ? 0: context.getRecordCount();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReader#getRecordContext(int)
     */
    @Override
    public RecordContext getRecordContext(int index) {
        if (context == null) {
            throw new IndexOutOfBoundsException();
        }
        return context.getRecordContext(index);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReader#setErrorHandler(org.beanio.BeanReaderErrorHandler)
     */
    @Override
    public void setErrorHandler(BeanReaderErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    /*
     * Throws an exception if the stream has already been closed.
     */
    private void ensureOpen() {
        if (context == null) {
            throw new BeanReaderIOException("Stream closed");
        }
    }

    private void handleError(BeanReaderException ex) {
        if (errorHandler == null) {
            throw ex;
        }
        else {
            try {
                errorHandler.handleError(ex);
            }
            catch (BeanReaderException e) {
                throw e;
            }
            catch (Exception e) {
                throw new BeanReaderException("Exception thrown by error handler", e);
            }
        }
    }

    /**
     * Sets whether to ignore unidentified records.  Defaults to false.
     * @param ignoreUnidentifiedRecords true to ignore unidentified records, false otherwise
     */
    public void setIgnoreUnidentifiedRecords(boolean ignoreUnidentifiedRecords) {
        this.ignoreUnidentifiedRecords = ignoreUnidentifiedRecords;
    }
    
    @Override
    public void debug() {
        debug(System.out);
    }
    @Override
    public void debug(PrintStream out) {
        ((Component)layout).print(out);
    }
}
