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
 * A {@link Parser} component for aggregating inline {@link Map} objects.
 * For example: <tt>key1,field1,key2,field2</tt>.
 * 
 * @author Kevin Seim
 * @since 2.0.1
 */
public class MapParser extends Aggregation {

    // the map type
    private Class<? extends Map<Object,Object>> type;
    // the child property used for the key
    private Property key;
    // the property value
    private ParserLocal<Object> value = new ParserLocal<>();    
    
    /**
     * Constructs a new <tt>MapParser</tt>.
     */
    public MapParser() { }
    
    @Override
    public void clearValue(ParsingContext context) {
        this.value.set(context, null);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Property#defines(java.lang.Object)
     */
    @Override
    public boolean defines(Object value) {
        if (value == null || type == null) {
            return false;
        }
        
        if (Map.class.isAssignableFrom(value.getClass())) {
            // children of collections cannot be used to identify bean objects
            // so we can immediately return true here
            return true;
        }
        
        return false;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.DelegatingParser#matches(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public boolean matches(UnmarshallingContext context) {
        // matching repeating fields is not supported
        return true;
    }
    
    @Override
    protected boolean marshal(MarshallingContext context, Parser delegate, int minOccurs, int maxOccurs) throws IOException {
        context.pushIteration(this);
        try {
            Map<Object,Object> map = getMap(context);
            if (map == null && minOccurs == 0) {
                return false;
            }
            
            int i = 0;
            
            if (map != null) {
                for (Map.Entry<Object,Object> entry : map.entrySet()) {
                    if (i < maxOccurs) {
                        setIterationIndex(context, i);
                        key.setValue(context, entry.getKey());
                        delegate.setValue(context, entry.getValue());
                        delegate.marshal(context);
                        ++i;
                    }
                    else {
                        return true;
                    }
                }
            }
            
            if (i < minOccurs) {
                key.setValue(context, null);
                delegate.setValue(context, null);
                while (i < minOccurs) {
                    setIterationIndex(context, i);
                    delegate.marshal(context);
                    ++i;
                }
            }
            
            return true;
        }
        finally {
            context.popIteration();
        }
    }
    
    @Override
    protected boolean unmarshal(UnmarshallingContext context, Parser delegate, int minOccurs, int maxOccurs) {
        Map<Object,Object> map = lazy ? null : createMap();
        
        boolean invalid = false;
        int count = 0;
        try {
            context.pushIteration(this);
            
            Object fieldValue = null;
            for (int i=0; i < maxOccurs; i++) {
                setIterationIndex(context, i);
                
                // unmarshal the field
                boolean found = delegate.unmarshal(context);
                if (!found) {
                    delegate.clearValue(context);
                    break;
                }
                
                // collect the field value and add it to our buffered list
                fieldValue = delegate.getValue(context);
                if (fieldValue == Value.INVALID) {
                    invalid = true;
                }
                else if (fieldValue != Value.MISSING) {
                	Object mapKey = key.getValue(context);
                	if (!lazy || StringUtil.hasValue(mapKey) || StringUtil.hasValue(fieldValue)) {
                		if (map == null) {
                			map = createMap();
                		}
                		map.put(mapKey, fieldValue);
                	}
                }
                
                delegate.clearValue(context);
                ++count;
            }
        }
        finally {
            context.popIteration();
        }
        
        Object value;
        
        // validate minimum occurrences have been met
        if (count < minOccurs) {
            context.addFieldError(getName(), null, "minOccurs", minOccurs, maxOccurs);
            value = Value.INVALID;
        }
        else if (invalid) {
            value = Value.INVALID;
        }
        else {
            value = map;
        }
        
        this.value.set(context, value);
        
        return count > 0;
    }

    /**
     * Returns whether this iteration is a property of a bean object.
     * @return true if this iteration is a property, false otherwise
     */
    @Override
    public boolean isProperty() {
        return type != null;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#getType()
     */
    @Override
    public Class<? extends Map<Object,Object>> getType() {
        return type;
    }
    
    /**
     * Sets the concrete {@link Map} type.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setType(Class<?> mapType) {
        this.type = (Class<? extends Map<Object,Object>>) mapType;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Property#create()
     */
    @Override
    public Object createValue(ParsingContext context) {
        Object value = this.value.get(context);
        if (value == null) {
            value = createMap();
            this.value.set(context, value);
        }
        return getValue(context);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.DelegatingParser#getValue()
     */
    @Override
    public Object getValue(ParsingContext context) {
        Object value = this.value.get(context);
        return value == null ? Value.MISSING : value;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.DelegatingParser#setValue(java.lang.Object)
     */
    @Override
    public void setValue(ParsingContext context, Object value) {
        // convert empty collections to null so that parent parsers
        // will consider this property missing during marshalling
        if (value != null && ((Map<?,?>)value).isEmpty()) {
            value = null;
        }
        
        this.value.set(context, value);
        
        super.setValue(context, value);
    }
    
    protected Map<Object,Object> createMap() {
        return ObjectUtils.newInstance(type);
    }
    
    /**
     * Returns the map value being parsed.
     * @return the {@link Map}
     */
    @SuppressWarnings("unchecked")
    protected Map<Object,Object> getMap(ParsingContext context) {
        Object value = this.value.get(context);
        if (value == Value.INVALID) {
            return null;
        }
        else {
            return (Map<Object,Object>) value;
        }
    }
    
    @Override
    protected int length(Object value) {
        Map<?,?> map = (Map<?,?>) value;
        return map != null ? map.size() : 0;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Iteration#getIterationSize()
     */
    @Override
    public int getIterationSize() {
        return getSize();
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
    public void registerLocals(Set<ParserLocal<? extends Object>> locals) {
        if (key != null) {
            ((Component)key).registerLocals(locals);
        }
        
        if (locals.add(value)) {
            super.registerLocals(locals);
        }
    }
    
    @Override
    public boolean hasContent(ParsingContext context) {
        Map<Object,Object> map = getMap(context);
        return map != null && map.size() > 0; 
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
        if (type != null) {
            s.append(", type=").append(type.getSimpleName());
        }
    }
}
