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

import java.io.IOException;
import java.util.*;

import org.beanio.internal.util.StringUtil;

/**
 * 
 * @author Kevin Seim
 * @since 2.0.1.
 */
public class RecordMap extends RecordAggregation {

    // the child property used for the key
    private Property key;
    
    /**
     * Constructs a new <tt>RecordMap</tt>.
     */
    public RecordMap() { }
    
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean unmarshal(UnmarshallingContext context) {
        // allow the delegate to unmarshal itself
        boolean result = super.unmarshal(context);
        
        Object aggregatedValue = getSelector().getValue(context);
        if (aggregatedValue != Value.INVALID) {
            Object keyValue = key.getValue(context);
            
            if (!lazy || StringUtil.hasValue(keyValue) || StringUtil.hasValue(aggregatedValue)) {
                Object aggregation = value.get(context);
                if (aggregation == null || aggregation == Value.MISSING) {
                    aggregation = createAggregationType();
                    value.set(context, aggregation);
                }
                
                Map map = (Map) aggregation;
                map.put(keyValue, aggregatedValue);
            }
        }
        
        getParser().clearValue(context);
        return result;
    }
    
    @Override
    public boolean marshal(MarshallingContext context) throws IOException {
        int minOccurs = getMinOccurs();
        
        Map<Object,Object> map = getMap(context);
        if (map == null && minOccurs == 0) {
            return false;
        }
        
        Parser delegate = getParser();
        int maxOccurs = getMaxOccurs();
        int index = 0;
        
        if (map != null) {
            for (Map.Entry<Object,Object> entry : map.entrySet()) {
                
                if (index < maxOccurs) {
                    key.setValue(context, entry.getKey());
                    delegate.setValue(context, entry.getValue());
                    delegate.marshal(context);
                    ++index;
                }
                else {
                    return true;
                }
            }
        }
        
        if (index < minOccurs) {
            key.setValue(context, null);
            delegate.setValue(context, null);
            while (index < minOccurs) {
                delegate.marshal(context);
                ++index;
            }
        }
        
        return true;
    }
    
    @Override
    public void setValue(ParsingContext context, Object value) {
        // convert empty collections to null so that parent parsers
        // will consider this property missing during marshalling
        if (value != null && ((Map<?,?>)value).isEmpty()) {
            value = null;
        }
        super.setValue(context, value);
    }
    
    @Override
    public boolean hasContent(ParsingContext context) {
        Map<Object,Object> map = getMap(context);
        return map != null && map.size() > 0; 
    }
    
    /**
     * Returns the collection value being parsed.
     * @param context the {@link ParsingContext}
     * @return the {@link Collection}
     */
    @SuppressWarnings("unchecked")
    protected Map<Object,Object> getMap(ParsingContext context) {
        return (Map<Object,Object>) super.getValue(context);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#type()
     */
    @Override
    public int type() {
        return Property.AGGREGATION_MAP;
    }
    
    @Override
    public boolean defines(Object value) {
        return value != null && Map.class.isAssignableFrom(value.getClass());
    }

    public Property getKey() {
        return key;
    }

    public void setKey(Property key) {
        this.key = key;
    }
    
    @Override
    protected void toParamString(StringBuilder s) {
        super.toParamString(s);
        if (key != null) {
            s.append(", key=$").append(key.getName());
        }
    }
}
