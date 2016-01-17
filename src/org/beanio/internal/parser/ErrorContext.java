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

import java.util.*;

import org.beanio.*;

/**
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class ErrorContext implements RecordContext, Cloneable {
    
    private int lineNumber;
    private String recordText;
    private String recordName;
    private ArrayList<String> recordErrors;
    private HashMap<String, String> fieldTextMap;
    private HashMap<String, Counter> fieldCountMap;
    private HashMap<String, Collection<String>> fieldErrorMap;
    
    /**
     * Constructs a new <tt>ErrorContext</tt>.
     */
    public ErrorContext() { }
    
    /**
     * Clears this context;
     */
    public void clear() {
        lineNumber = 0;
        recordName = null;
        recordText = null;
        
        if (fieldTextMap != null)
            fieldTextMap.clear();
        if (fieldCountMap != null)
            fieldCountMap.clear();
        if (fieldErrorMap != null)
            fieldErrorMap = null;
        if (recordErrors != null)
            recordErrors.clear();
    }
    
    /**
     * Returns a deep copy of this context.
     * @return the copy
     *
    @SuppressWarnings("unchecked")
    public ErrorContext copy() {
        try {
            ErrorContext ec = (ErrorContext) clone();
            if (fieldMap != null)
                ec.fieldMap = (HashMap<String, Object>) fieldMap.clone();
            //if (collectionFieldTextMap != null)
            //    ec.collectionFieldTextMap = (HashMap<String, List<String>>) collectionFieldTextMap.clone();
            if (fieldErrorMap != null)
                ec.fieldErrorMap = (HashMap<String, Collection<String>>) fieldErrorMap.clone();
            if (recordErrors != null)
                ec.recordErrors = (ArrayList<String>) recordErrors.clone();
            return ec;
        }
        catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
    }
    */
    
    /**
     * Returns the raw text of the last record read from the record reader.
     * @return the raw text of the last record read
     */
    @Override
    public String getRecordText() {
        return recordText;
    }

    /**
     * Sets the raw text of the last record read from the record reader.
     * @param text the raw text of the last record read
     */
    public void setRecordText(String text) {
        this.recordText = text;
    }
    
    /**
     * Returns the starting line number of the last record read from the record reader.
     * @return the line number of the last record
     */
    @Override
    public int getRecordLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the starting line number of the last record read from the record reader.
     * @param lineNumber the line number of the last record
     */
    public void setLineNumber(int lineNumber) {
        if (lineNumber > 0)
            this.lineNumber = lineNumber;
    }

    /**
     * Returns the name of the last record read from the record reader, 
     * or <tt>null</tt> if not known.
     * @return the name of the record
     */
    @Override
    public String getRecordName() {
        return recordName;
    }

    /**
     * Sets the name of the last record read from the record reader.
     * @param recordName the record name
     */
    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    /**
     * Adds a field error message.
     * @param fieldName the name of the field 
     * @param message the error message to add
     */
    public void addFieldError(String fieldName, String message) {
        if (fieldErrorMap == null) {
            fieldErrorMap = new HashMap<>();
        }
        Collection<String> errors = fieldErrorMap.get(fieldName);
        if (errors == null) {
            errors = new ArrayList<>();
            errors.add(message);
            fieldErrorMap.put(fieldName, errors);
        }
        else {
            errors.add(message);
        }
    }

    /**
     * Adds a record level error message.
     * @param message the error message to add
     */
    public void addRecordError(String message) {
        if (recordErrors == null) {
            recordErrors = new ArrayList<>(3);
        }
        recordErrors.add(message);
    }
    
    /**
     * Sets the raw field text for a named field.
     * @param fieldName the name of the field
     * @param text the raw field text
     * @param repeating whether the field repeats in the stream
     */
    public void setFieldText(String fieldName, String text, boolean repeating) {
        if (fieldTextMap == null) {
            fieldTextMap = new HashMap<>();
        }
        
        if (repeating) {
            // update the field count
            if (fieldCountMap == null) {
                fieldCountMap = new HashMap<>();
            }
            
            Counter counter = fieldCountMap.get(fieldName);
            if (counter == null) {
                counter = new Counter();
                fieldCountMap.put(fieldName, counter);
            }
            
            fieldTextMap.put(counter.getCount() + ":" + fieldName, text);
            
            counter.incrementCount();
        }
        else {
            fieldTextMap.put(fieldName, text);
        }
    }
        
    /*
     * (non-Javadoc)
     * @see org.beanio.RecordContext#hasErrors()
     */
    @Override
    public boolean hasErrors() {
        return hasRecordErrors() || hasFieldErrors();
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReaderContext#hasRecordErrors()
     */
    @Override
    public boolean hasRecordErrors() {
        return recordErrors != null && !recordErrors.isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReaderContext#getRecordErrors()
     */
    @Override
    public Collection<String> getRecordErrors() {
        if (recordErrors == null)
            return Collections.emptyList();
        return recordErrors;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.RecordContext#getFieldCount(java.lang.String)
     */
    @Override
    public int getFieldCount(String fieldName) {
        if (fieldTextMap == null) {
            return 0;
        }
        
        if (fieldCountMap != null) {
            Counter counter = fieldCountMap.get(fieldName);
            if (counter != null) {
                return counter.getCount();
            }
        }

        return fieldTextMap.containsKey(fieldName) ? 1 : 0;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReaderContext#getFieldText(java.lang.String)
     */
    @Override
    public String getFieldText(String fieldName) {
        return getFieldText(fieldName, 0);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReaderContext#getFieldText(java.lang.String, int)
     */
    @Override
    public String getFieldText(String fieldName, int index) {
        if (fieldTextMap == null) {
            return null;
        }
        else if (index == 0) {
            return fieldTextMap.get(fieldName);
        }
        else {
            return fieldTextMap.get(index + ":" + fieldName);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReaderContext#hasFieldErrors()
     */
    @Override
    public boolean hasFieldErrors() {
        return fieldErrorMap != null && !fieldErrorMap.isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReaderContext#getFieldErrors()
     */
    @Override
    public Map<String, Collection<String>> getFieldErrors() {
        if (fieldErrorMap == null) {
            return Collections.emptyMap();
        }
        return fieldErrorMap;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.BeanReaderContext#getFieldErrors(java.lang.String)
     */
    @Override
    public Collection<String> getFieldErrors(String fieldName) {
        if (fieldErrorMap == null)
            return null;
        else
            return fieldErrorMap.get(fieldName);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.RecordContext#getLineNumber()
     */
    @Override
    public int getLineNumber() {
        return getRecordLineNumber();
    }
    
    private static class Counter {
        private int count = 0;
        public Counter() { }
        public int getCount() { return count; }
        public void incrementCount() { ++count; }
    }
}
