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
package org.beanio.internal.parser.format.json;

import java.util.*;

import org.beanio.internal.parser.*;

/**
 * A {@link MarshallingContext} for JSON formatted streams.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonMarshallingContext extends MarshallingContext {

    private Object[] valueStack;
    private char[] typeStack;
    private int depth = -1;
    
    /**
     * Constructs a new <tt>JsonMarshallingContext</tt>.
     * @param maxDepth the maximum depth of the all {@link JsonWrapper} components in the parser tree layout.
     */
    public JsonMarshallingContext(int maxDepth) {
        valueStack = new Object[maxDepth];
        typeStack = new char[maxDepth];
    }
    
    @Override
    protected Object getRecordObject() {
        return depth < 0 ? null : valueStack[0];
    }

    @Override
    public void clear() {
        super.clear();
        depth = -1;
    }

    /**
     * 
     * @param type
     */
    public void push(JsonNode type) {
        Object value;
        if (type.getJsonType() == JsonNode.OBJECT) {
            value = new LinkedHashMap<String,String>();
        }
        else { // array
            value = new ArrayList<>();
        }
        
        put(type, value);
        
        depth++;
        valueStack[depth] = value;
        typeStack[depth] = type.getJsonType();
    }
    
    /**
     * 
     */
    public void pop() {
        --depth;
    }
    
    /**
     * 
     * @param type
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void put(JsonNode type, Object value) {
        if (depth < 0) {
            depth = 0;
            valueStack[depth] = new LinkedHashMap<String,Object>();
            typeStack[depth] = JsonNode.OBJECT;
        }

        if (type.isJsonArray()) {
            List<Object> list;
            if (type() == JsonNode.ARRAY) {
                int index = type.getJsonArrayIndex();
                if (index < list().size()) {
                    list = (List<Object>) list().get(index);
                }
                else {
                    list = new ArrayList<>();
                    list().add(list);                    
                }
            }
            else { // object
                list = (List<Object>) map().get(type.getJsonName());
                if (list == null) {
                    list = new ArrayList<>();
                    map().put(type.getJsonName(), list);
                }
            }
            list.add(value);
        }
        else {
            switch (type()) {
            case JsonNode.ARRAY:
                list().add(value);
                break;
                
            case JsonNode.OBJECT:
                map().put(type.getJsonName(), value);
                break;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> map() {
        return (Map<String,Object>) valueStack[depth]; 
    }
    
    @SuppressWarnings("unchecked")
    private List<Object> list() {
        return (List<Object>) valueStack[depth];
    }
    
    private char type() {
        return typeStack[depth];
    }
}
