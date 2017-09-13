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

import java.util.List;

import org.beanio.BeanWriterException;
import org.beanio.internal.parser.*;
import org.beanio.internal.parser.format.FieldPadding;
import org.beanio.internal.util.JsonUtil;

/**
 * A {@link FieldFormat} implementation for a field in a JSON formatted record.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonFieldFormat implements FieldFormat, JsonNode {

    /* the field name from the mapping file */
    private String name;
    /* the jsonName from the mapping file */
    private String jsonName;
    /* the JSON type: boolean, number or string */
    private char jsonType;
    /* whether the field is mapped to an array */
    private boolean jsonArray;
    /* set to the index of this field in a JSON array, or -1 if the field itself repeats */
    private int jsonArrayIndex = -1;
    /* whether the field must be present in the stream (i.e minOccurs > 0) */
    private boolean lazy;
    /* whether the field may be set to 'null' */
    private boolean nillable;
    /* optional field padding */
    private FieldPadding padding;
    /* whether type conversion can be bypassed and the value directly set into the map */
    private boolean bypassTypeHandler;
    
    /**
     * Constructs a new <tt>JsonFieldFormat</tt>.
     */
    public JsonFieldFormat() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#extract(org.beanio.internal.parser.UnmarshallingContext, boolean)
     */
    @SuppressWarnings("unchecked")
    @Override
    public String extract(UnmarshallingContext context, boolean reportErrors) {
        JsonUnmarshallingContext ctx = (JsonUnmarshallingContext) context;
    
        Object value = ctx.getValue(this);
        
        // nothing more to do with null or missing values
        if (value == null || value == Value.NIL) {
            ctx.setFieldText(getName(), null);
            return (String) value;
        }
        
        // extract the field from a list if repeating
        if (isJsonArray()) {
            int index = -1;// jsonArrayIndex; // TODO is this needed?
            if (index < 0) {
                index = ctx.getRelativeFieldIndex();
            }
            
            try {
                List<Object> list = (List<Object>)value;
                if (index < list.size()) {
                    value = list.get(index);
                }
                else {
                    return null;
                }
            }
            catch (ClassCastException ex) {
                // if index is greater than zero, we're trying to get next value
                // which doesn't exist if the value isn't a list so return null
                // instead of repetitively reporting the same field error
                if (index > 0 && jsonArrayIndex < 0) {
                    return null;
                }
                
                String fieldText = value.toString();
                ctx.setFieldText(getName(), fieldText);
                
                if (reportErrors) {
                    context.addFieldError(getName(), fieldText, "jsontype", 
                        JsonNodeUtil.getTypeDescription(jsonType, jsonArray));
                }
                return Value.INVALID;
            }
        }

        // TODO validate JSON type (how should this be configured...?)
        
        // convert to field text
        String fieldText = value.toString();
        ctx.setFieldText(getName(), fieldText);

        // handle padded fields
        if (padding != null) {
            int length = fieldText.length();
            if (length == 0) {
                // this will either cause a required validation error or map
                // to a null value depending on the value of 'required'
                return "";
            }
            else if (length != padding.getLength()) {
                if (reportErrors) {
                    context.addFieldError(getName(), fieldText, "length", padding.getLength());
                }
                fieldText = Value.INVALID;
            }
            else {
                fieldText = padding.unpad(fieldText);
            }
        }
        
        return fieldText;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#insertValue(org.beanio.internal.parser.MarshallingContext, java.lang.Object)
     */
    @Override
    public boolean insertValue(MarshallingContext context, Object value) {
        if (!bypassTypeHandler) {
            return false;
        }
        
        JsonMarshallingContext ctx = (JsonMarshallingContext) context;

        if (value == Value.NIL) {
            ctx.put(this, null);
        }
        else if (value == null && isLazy()) {
            // do nothing
        }
        else {
            ctx.put(this, value);
        }
        
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#insertField(org.beanio.internal.parser.MarshallingContext, java.lang.String)
     */
    @Override
    public void insertField(MarshallingContext context, String text) {
        
        JsonMarshallingContext ctx = (JsonMarshallingContext) context;

        if (text == Value.NIL) {
            ctx.put(this, null);
            return;
        }
        
        if (text == null && isLazy()) {
            return;
        }
        
        Object value = null;
        
        // convert text to JSON type
        switch (jsonType) {
        case JsonNode.BOOLEAN:
            try {
                value = JsonUtil.toBoolean(text);
            }
            catch (IllegalArgumentException ex) {
                throw new BeanWriterException("Cannot parse '" + text + "' into a JSON number", ex);
            }
            break;
            
        case JsonNode.NUMBER:
            try {
                value = JsonUtil.toNumber(text);
            }
            catch (NumberFormatException ex) {
                throw new BeanWriterException("Cannot parse '" + text + "' into a JSON number", ex);
            }
            break;
            
        case JsonNode.STRING:
            value = text;
            break;
                
        default: 
            throw new BeanWriterException("Invalid jsonType");
        }

        ctx.put(this, value);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.json.JsonType#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 
     * @param lazy
     */
    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#getSize()
     */
    @Override
    public int getSize() {
        return 1;
    }

    /**
     * 
     * @param nillable
     */
    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#isNillable()
     */
    @Override
    public boolean isNillable() {
        return nillable;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.FieldFormat#isLazy()
     */
    @Override
    public boolean isLazy() {
        return lazy;
    }
    
    /**
     * 
     * @param jsonName
     */
    public void setJsonName(String jsonName) {
        this.jsonName = jsonName;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.json.JsonType#getJsonName()
     */
    @Override
    public String getJsonName() {
        return jsonName;
    }

    /**
     * 
     * @param jsonType
     */
    public void setJsonType(char jsonType) {
        this.jsonType = jsonType;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.json.JsonType#getJsonType()
     */
    @Override
    public char getJsonType() {
        return jsonType;
    }

    public void setJsonArray(boolean repeating) {
        this.jsonArray = repeating;
    }
    
    @Override
    public boolean isJsonArray() {
        return jsonArray;
    }
    
    @Override
    public int getJsonArrayIndex() {
        return jsonArrayIndex;
    }

    public void setJsonArrayIndex(int jsonArrayIndex) {
        this.jsonArrayIndex = jsonArrayIndex;
    }

    public FieldPadding getPadding() {
        return padding;
    }

    public void setPadding(FieldPadding padding) {
        this.padding = padding;
    }
    
    public boolean isBypassTypeHandler() {
        return bypassTypeHandler;
    }

    public void setBypassTypeHandler(boolean bypassTypeHandler) {
        this.bypassTypeHandler = bypassTypeHandler;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + 
            "[name=" + getName() +
            ", jsonName=" + jsonName +
            ", jsonType=" + jsonType + (isJsonArray() ? "[]" : "") +
            ", jsonArrayIndex=" + jsonArrayIndex +
            ", bypass=" + bypassTypeHandler +
            "]";
    }
}
