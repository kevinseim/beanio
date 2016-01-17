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
import java.util.Map;

import org.beanio.*;
import org.beanio.internal.util.*;
import org.beanio.stream.RecordWriter;

/**
 * A {@link BeanReader} implementation.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class BeanWriterImpl implements BeanWriter, StatefulWriter {

    private Selector layout;
    private MarshallingContext context;
    
    /**
     * Constructs a new <tt>BeanWriterImpl</tt>.
     * @param context the {@link MarshallingContext}
     * @param layout the root {@link Selector} node in the parsing tree
     */
    public BeanWriterImpl(MarshallingContext context, Selector layout) {
        this.context = context;
        this.layout = layout;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.BeanWriter#write(java.lang.Object)
     */
    @Override
    public void write(Object bean) throws BeanWriterException {
        write(null, bean);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.BeanWriter#write(java.lang.String, java.lang.Object)
     */
    @Override
    public void write(String recordName, Object bean) throws BeanWriterException {
        ensureOpen();
        
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
            
            // marshal the bean object
            matched.marshal(context);
        }
        catch (IOException e) {
            throw new BeanWriterIOException(e);
        }
        catch (BeanWriterException ex) {
            throw ex;
        }
        catch (BeanIOException ex) {
            // wrap the generic exception in a BeanReaderException
           throw new BeanWriterException("Fatal BeanIOException caught", ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.BeanWriter#flush()
     */
    @Override
    public void flush() throws BeanWriterIOException {
        ensureOpen();
        
        try {
            context.getRecordWriter().flush();
        }
        catch (IOException e) {
            throw new BeanWriterIOException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.BeanWriter#close()
     */
    @Override
    public void close() throws BeanWriterIOException {
        ensureOpen();
        
        try {
            context.getRecordWriter().close();
        }
        catch (IOException e) {
            throw new BeanWriterIOException(e);
        }
        finally {
            context = null;
            layout = null;
        }
    }
    
    /*
     * Throws an exception if the stream has already been closed.
     */
    private void ensureOpen() {
        if (context == null) {
            throw new BeanWriterIOException("Stream closed");
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.StatefulBeanWriter#updateState(java.lang.String, java.util.Map)
     */
    @Override
    public void updateState(String namespace, Map<String, Object> state) {
        layout.updateState(context, namespace + ".m", state);
        
        RecordWriter writer = context.getRecordWriter();
        if (writer instanceof StatefulWriter) {
            ((StatefulWriter)writer).updateState(namespace + ".w", state);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.StatefulBeanWriter#restoreState(java.lang.String, java.util.Map)
     */
    @Override
    public void restoreState(String namespace, Map<String, Object> state) throws IllegalStateException {
        layout.restoreState(context, namespace + ".m", state);
        
        RecordWriter writer = context.getRecordWriter();
        if (writer instanceof StatefulWriter) {
            ((StatefulWriter)writer).restoreState(namespace + ".w", state);
        }
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
