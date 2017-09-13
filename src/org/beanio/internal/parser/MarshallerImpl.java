/*
 * Copyright 2012-2013 Kevin Seim
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
import org.w3c.dom.Document;

/**
 * Default {@link Marshaller} implementation.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class MarshallerImpl implements Marshaller {

    private Selector layout;
    private MarshallingContext context;
    private RecordMarshaller recordMarshaller;
    
    private Object recordValue;
    
    /**
     * Constructs a new <tt>UnmarshallerImpl</tt>
     * @param context the {@link UnmarshallingContext}
     * @param layout the stream layout
     * @param recordMarshaller the {@link RecordMarshaller} for converting record text to record values
     */
    public MarshallerImpl(MarshallingContext context, Selector layout, final RecordMarshaller recordMarshaller) {
        this.context = context;
        this.layout = layout;
        this.recordMarshaller = recordMarshaller;
        
        this.context.setRecordWriter(new RecordWriter() {
            @Override
            public void write(Object record) {
                recordValue = record;
            }
            @Override
            public void flush() { }
            @Override
            public void close() { }
        });
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.Marshaller#marshal(java.lang.Object)
     */
    @Override
    public Marshaller marshal(Object bean) throws BeanWriterException {
        return marshal(null, bean);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.Marshaller#marshal(java.lang.String, java.lang.Object)
     */
    @Override
    public Marshaller marshal(String recordName, Object bean) throws BeanWriterException {
        recordValue = null;
        
        if (recordName == null && bean == null) {
            throw new BeanWriterException("Bean identification failed: a record " +
                "name or bean object must be provided");
        }
        
        try {
            // set the name of the component to be marshalled (may be null if we're just matching on bean)
            context.setComponentName(recordName);
            // set the bean to be marshalled on the context
            context.setBean(bean);
            
            // find the parser in the layout that defines the given bean
            Selector matched = layout.matchNext(context);
            if (matched == null) {
                if (recordName != null) {
                    throw new BeanWriterException("Bean identification failed: " +
                        "record name '" + recordName + "' not matched at the current position" +
                        (bean != null ? " for bean class '" + bean.getClass() + "'" : ""));
                }
                else {
                    throw new BeanWriterException("Bean identification failed: " +
                        "no record or group mapping for bean class '" + bean.getClass() + 
                        "' at the current position");                    
                }             
            }
            if (matched.isRecordGroup()) {
                throw new BeanWriterException("Record groups not supported by Marshaller");
            }
            
            // marshal the bean object
            matched.marshal(context);
            
            return this;
        }
        catch (IOException e) {
            // not actually possible since we've overridden the RecordWriter
            throw new BeanWriterException(e);
        }
        catch (BeanWriterException ex) {
            throw ex;
        }
        catch (BeanIOException ex) {
            // wrap generic exceptions in a BeanReaderException
           throw new BeanWriterException(ex.getMessage(), ex);
        }
        finally {
            // clear the marshaling context
            context.clear();
        }
    }
        
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (recordValue == null) ? null : recordMarshaller.marshal(recordValue);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.Marshaller#toArray()
     */
    @Override
    public String[] toArray() throws BeanWriterException {
        String[] array = context.toArray(recordValue);
        if (array == null) {
            throw new BeanWriterException("toArray() not supported by stream format");
        }
        return array;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.Marshaller#toList()
     */
    @Override
    public List<String> toList() throws BeanWriterException {
        List<String> list = context.toList(recordValue);
        if (list == null) {
            throw new BeanWriterException("toList() not supported by stream format");
        }
        return list;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.Marshaller#toDocument()
     */
    @Override
    public Document toDocument() throws BeanWriterException {
        Document document = context.toDocument(recordValue);
        if (document == null) {
            throw new BeanWriterException("toNode() not supported by stream format");
        }
        return document;
    }
    
    /**
     * Returns the record value for the most recent marshalled bean object.
     * @return the record value
     */
    protected Object getRecordValue() {
        return recordValue;
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
