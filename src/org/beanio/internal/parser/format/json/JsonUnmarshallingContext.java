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

import org.beanio.internal.config.PropertyConfig;
import org.beanio.internal.parser.*;

/**
 * An {@link UnmarshallingContext} for JSON formatted streams.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonUnmarshallingContext extends UnmarshallingContext {

    /* the current position in the record */
    private Object[] valueStack;
    private char[] typeStack;
    private int depth = 0;
    
    /**
     * Constructs a new <tt>JsonUnmarshallingContext</tt>.
     * @param maxDepth the maximum depth of the all {@link JsonWrapper} components in the parser tree layout.
     */
    public JsonUnmarshallingContext(int maxDepth) {
        valueStack = new Object[maxDepth];
        typeStack = new char[maxDepth];
    }
    
    @Override
    public void setRecordValue(Object value) {
        this.depth = 0;
        valueStack[0] = value;
        typeStack[0] = JsonNode.OBJECT;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> map() {
        return (Map<String,Object>) valueStack[depth]; 
    }
    
    @SuppressWarnings("unchecked")
    private List<Object> list() {
        return (List<Object>) valueStack[depth];
    }
    
    /**
     * 
     * @param node
     * @return
     */
    public Object getValue(JsonNode node) {
        String fieldName = node.getJsonName();

        if (typeStack[depth] == JsonNode.OBJECT) {
             Object value = map().get(fieldName);
             if (value == null) {
                 return map().containsKey(fieldName) ? Value.NIL : null;
             }
             else {
                 return value;
             }
        }
        else {
            int index = node.getJsonArrayIndex();
            if (index < 0) {
                index = getRelativeFieldIndex();
            }
            
            List<Object> parent = list();
            if (index < parent.size()) {
                Object value = parent.get(index);
                if (value == null) {
                    return Value.NIL;
                }
                return value;
            }
            else {
                return null;
            }
        }
    }
    
    /**
     * 
     * @param node
     * @param validate
     * @return
     */
    public Object push(JsonNode node, boolean validate) {
        
        Object value = getValue(node);
        
        if (value == Value.NIL) {
            if (validate && !node.isNillable()) {
                addFieldError(node.getName(), null, "nillable");
                return Value.INVALID;
            }
            return Value.NIL;
        }
        if (value == null) {
            return null;
        }
        
        // validate type...
        if (node.getJsonType() == JsonNode.ARRAY) {
            if (!(value instanceof List)) {
                if (validate) {
                    addFieldError(node.getName(), null, "jsontype", PropertyConfig.JSON_TYPE_ARRAY);
                    return Value.INVALID;
                }
                return null;
            }
        }
        else if (node.getJsonType() == JsonNode.OBJECT) {
            if (!(value instanceof Map)) {
                if (validate) {
                    System.out.println("HERE");
                    addFieldError(node.getName(), null, "jsontype", PropertyConfig.JSON_TYPE_OBJECT);
                    return Value.INVALID;
                }
                return null;
            }
        }
        else {
            throw new IllegalStateException("Invalid json type: " + node.getJsonType());
        }
        
        depth++;
        valueStack[depth] = value;
        typeStack[depth] = node.getJsonType();
        
        return value;
    }
    
    /**
     * 
     * @return
     */
    public Object pop() {
        return valueStack[depth--];
    }
}
