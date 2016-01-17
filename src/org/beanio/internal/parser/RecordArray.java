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

import java.lang.reflect.Array;
import java.util.*;

import org.beanio.BeanReaderException;

/**
 * A {@link Parser} tree component for parsing an array of bean objects, where
 * a bean object is mapped to a {@link Record}.
 * 
 * <p>A <tt>RecordArray</tt> supports a single {@link Record} child.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class RecordArray extends RecordCollection {

    private Class<?> arrayType;
    
    /**
     * Constructs a new <tt>RecordArray</tt>.
     */
    public RecordArray() { }
    
    @Override
    public int type() {
        return Property.AGGREGATION_ARRAY;
    }

    @Override
    public Object getValue(ParsingContext context) {
        Collection<Object> collection = super.getCollection(context);
        if (collection == null) {
            return null;
        }
        
        try {
            int index = 0;
            Object array = Array.newInstance(arrayType, collection.size());
            for (Object obj : collection) {
                Array.set(array, index++, obj);
            }
            return array;
        }
        catch (IllegalArgumentException ex) {
            throw new BeanReaderException("Failed to set array value.", ex);
        }
    }

    @Override
    public void setValue(ParsingContext context, Object value) {
        Collection<Object> collection = null;
        if (value != null) {
            int length = Array.getLength(value);
            if (length > 0) {
                collection = new ArrayList<>(length);
                for (int i=0; i<length; i++) {
                    collection.add(Array.get(value, i));
                }
            }
        }
        super.setValue(context, collection);
    }
    
    @Override
    protected Collection<Object> createAggregationType() {
        return new ArrayList<>();
    }

    /**
     * Returns the class type of the array.
     * @return the array class type
     */
    public Class<?> getArrayType() {
        return arrayType;
    }

    /**
     * Sets the class type of the array (e.g. <tt>int.class</tt> for <tt>int[]</tt>)
     * @param arrayType the array class type
     */
    public void setArrayType(Class<?> arrayType) {
        this.arrayType = arrayType;
    }
}
