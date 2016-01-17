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
package org.beanio.internal.parser;

import java.io.*;
import java.util.List;

import org.beanio.*;
import org.beanio.stream.*;
import org.w3c.dom.Node;

/**
 * Default {@link Unmarshaller} implementation.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class UnmarshallerImpl implements Unmarshaller {

    private Selector layout;
    private UnmarshallingContext context;
    
    private String recordName;
    private String recordText;
    private Object recordValue;
    
    /**
     * Constructs a new <tt>UnmarshallerImpl</tt>
     * @param context the {@link UnmarshallingContext}
     * @param layout the stream layout
     * @param recordUnmarshaller the {@link RecordUnmarshaller} for converting record text to record values
     */
    public UnmarshallerImpl(UnmarshallingContext context, Selector layout, final RecordUnmarshaller recordUnmarshaller) {
        this.context = context;
        this.layout = layout;
        
        this.context.setRecordReader(new RecordReader() {
            @Override
            public Object read() throws RecordIOException {
                try {
                    Object value = recordValue;
                    if (recordText != null) {
                        value = recordUnmarshaller.unmarshal(recordText);
                    }
                    return value;
                }
                finally {
                    recordText = null;
                    recordValue = null;
                }
            }
            @Override
            public int getRecordLineNumber() {
                return 0;
            }
            @Override
            public String getRecordText() {
                return recordText;
            }
            @Override
            public void close() throws IOException { }
        });
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.Unmarshaller#unmarshal(java.lang.String)
     */
    @Override
    public Object unmarshal(String text) throws MalformedRecordException, UnidentifiedRecordException,
        UnexpectedRecordException, InvalidRecordException {
        
        if (text == null) {
            throw new NullPointerException("null text");
        }
        
        this.recordName = null;
        this.recordText = text;
        
        return unmarshal();
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.Unmarshaller#unmarshal(java.util.List)
     */
    @Override
    public Object unmarshal(List<String> list) throws BeanReaderException, UnidentifiedRecordException,
        UnexpectedRecordException, InvalidRecordException {
        
        if (list == null) {
            throw new NullPointerException("null list");
        }
        
        this.recordName = null;
        this.recordValue = context.toRecordValue(list);
        
        if (recordValue == null) {
            throw new BeanReaderException("unmarshal(List) not supported by stream format");
        }
        
        return unmarshal();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.Unmarshaller#unmarshal(java.lang.String[])
     */
    @Override
    public Object unmarshal(String[] array) throws BeanReaderException, UnidentifiedRecordException,
        UnexpectedRecordException, InvalidRecordException {
        
        if (array == null) {
            throw new NullPointerException("null array");
        }
        
        this.recordName = null;
        this.recordValue = context.toRecordValue(array);
        
        if (recordValue == null) {
            throw new BeanReaderException("unmarshal(String[]) not supported by stream format");
        }
        
        return unmarshal();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.Unmarshaller#unmarshal(org.w3c.dom.Node)
     */
    @Override
    public Object unmarshal(Node node) throws BeanReaderException, UnidentifiedRecordException,
        UnexpectedRecordException, InvalidRecordException {
        
        if (node == null) {
            throw new NullPointerException("null node");
        }
        
        this.recordName = null;
        this.recordValue = context.toRecordValue(node);
        
        if (recordValue == null) {
            throw new BeanReaderException("unmarshal(Node) not supported by stream format");
        }
        
        return unmarshal();
    }
    
    /**
     * Internal unmarshal method.
     * @return the unmarshalled object
     */
    private Object unmarshal() {
        
        // allow the context to parse the next record value
        context.nextRecord();
        
        Selector parser = null;
        // match the next record
        try {
            parser = layout.matchNext(context);
        }
        catch (UnexpectedRecordException ex) {
            // when thrown, 'parser' is null and the error is handled below
        }

        // handle unmatched records
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
        
        try {
            // abort if we matched a record group
            if (parser.isRecordGroup()) {
                context.recordSkipped();
                throw new BeanReaderException("Record groups not supported by Unmarshallers");
            }
            
            // notify the unmarshalling context that we are about to unmarshal a new record
            context.prepare(parser.getName(), false);
            
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

    /*
     * (non-Javadoc)
     * @see org.beanio.Unmarshaller#getRecordName()
     */
    @Override
    public String getRecordName() {
        return recordName;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.Unmarshaller#getRecordContext()
     */
    @Override
    public RecordContext getRecordContext() {
        return context.getRecordContext(0);
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
