/*
 * Copyright 2011-2012 Kevin Seim
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
import java.util.List;

import org.beanio.stream.RecordWriter;
import org.w3c.dom.Document;

/**
 * Stores context information needed to marshal a bean object.
 * 
 * <p>Subclasses must implement {@link #getRecordObject()} which is invoked
 * when {@link #writeRecord()} is called to write a record object to the
 * configured {@link RecordWriter}.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class MarshallingContext extends ParsingContext {

    private Object bean;
    private String componentName;
    private RecordWriter recordWriter;
    
    /**
     * Constructs a new <tt>MarshallingContext</tt>.
     */
    public MarshallingContext() { }

    @Override
    public final char getMode() {
        return MARSHALLING;
    }
    
    /**
     * Clear is invoked after each bean object (record or group) is marshalled.
     */
    @Override
    public void clear() { 
        this.bean = null;
        this.componentName = null;
    }
    
    /**
     * Writes the current record object to the record writer.  This method will
     * invoke the {@link #getRecordObject()} method.
     * @throws IOException if an I/O error occurs
     */
    public void writeRecord() throws IOException {
        recordWriter.write(getRecordObject());
        super.clear();
    }
    
    /**
     * Returns the record object to pass to the {@link RecordWriter} when
     * {@link #writeRecord()} is called.
     * @return the record object
     */
    protected abstract Object getRecordObject();
    
    /**
     * Converts a record object to a <tt>String[]</tt>.
     * @param record the record object to convert
     * @return the <tt>String</tt> array result, or null if not supported
     */
    public String[] toArray(Object record) {
        return null;
    }

    /**
     * Converts a record object to a {@link List}.
     * @param record the record object to convert
     * @return the {@link List} result, or null if not supported
     */
    public List<String> toList(Object record) {
        return null;
    }
    
    /**
     * Converts a record object to a {@link Document}.
     * @param record the record object to convert
     * @return the {@link Document} result, or null if not supported
     */
    public Document toDocument(Object record) {
        return null;
    }
    
    /**
     * Returns the component name of the record or group to marshal.  May
     * be null if not specified.
     * @return the component name to marshal
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * Sets the component name of the record or group to marshal. 
     * @param componentName the component name to marshal
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
     * Returns the bean object to marshal.
     * @return the bean object to marshal
     */
    public Object getBean() {
        return bean;
    }
    
    /**
     * Sets the bean object to marshal.
     * @param bean the bean object
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }
    
    /**
     * Returns the {@link RecordWriter} to write to.
     * @return the {@link RecordWriter}
     */
    public RecordWriter getRecordWriter() {
        return recordWriter;
    }

    /**
     * Set the {@link RecordWriter} to write to.
     * @param recordWriter the {@link RecordWriter}
     */
    public void setRecordWriter(RecordWriter recordWriter) {
        this.recordWriter = recordWriter;
    }
}
