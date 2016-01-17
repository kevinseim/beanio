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

import java.io.IOException;
import java.util.*;

import org.beanio.internal.util.StringUtil;

/**
 * A <tt>CollectionParser</tt> provides iteration support for a {@link Segment} or {@link Field},
 * and is optionally bound to a {@link Collection} type property value.
 * 
 * <p>A <tt>CollectionParser</tt> must contain exactly one child {@link ParserComponent}.
 *  
 * @author Kevin Seim
 * @since 2.0
 */
public class CollectionParser extends Aggregation {

    // the collection type
    private Class<? extends Collection<Object>> type;
    // the property value
    private ParserLocal<Object> value = new ParserLocal<>();  
    
    /**
     * Constructs a new <tt>CollectionParser</tt>.
     */
    public CollectionParser() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.DelegatingParser#clear()
     */
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
        // TODO implement for arrays....
        
        if (value == null || type == null) {
            return false;
        }
        
        if (Collection.class.isAssignableFrom(value.getClass())) {
            // children of collections cannot be used to identify bean objects
            // so we can immediately return true here
            return true;
        }
        
        return false;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.DelegatingParser#matches(org.beanio.parser.UnmarshallingContext)
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
            Collection<Object> collection = getCollection(context);
            if (collection == null && minOccurs == 0) {
                return false;
            }
            
            int i = 0;
            if (collection != null) {
                for (Object value : collection) {
                    if (i < maxOccurs) {
                        setIterationIndex(context, i);
                        delegate.setValue(context, value);
                        delegate.marshal(context);
                        ++i;
                    }
                    else {
                        return true;
                    }
                }
            }
            
            if (i < minOccurs) {
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

        Collection<Object> collection = lazy ? null : createCollection();
        
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
                // the field value may still be missing if 'optional' is true on a child segment
                else if (fieldValue != Value.MISSING) {
                    if (!lazy || StringUtil.hasValue(fieldValue)) {
                        if (collection == null) {
                            collection = createCollection();
                        }
                        collection.add(fieldValue);
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
            value = collection;
        }
        
        this.value.set(context, value);
        
        return value == Value.INVALID || count > 0;
    }
    
    /**
     * Returns whether this iteration is a property of a bean object.
     * @return true if this iteration is a property, false otherwise
     */
    @Override
    public boolean isProperty() {
        return type != null;
    }
    
    /**
     * Returns whether this iteration contained invalid values when last unmarshalled.
     * @return true if this iteration contained invalid values
     */
    protected boolean isInvalid(ParsingContext context) {
        return this.value.get(context) == Value.INVALID;
    }
    
    /**
     * Returns the collection value being parsed.
     * @return the {@link Collection}
     */
    @SuppressWarnings("unchecked")
    protected Collection<Object> getCollection(ParsingContext context) {
        Object value = this.value.get(context);
        if (value == Value.INVALID) {
            return null;
        }
        else {
            return (Collection<Object>) value;
        }
    }
    
    @Override
    protected int length(Object value) {
        Collection<?> collection = (Collection<?>) value;
        return collection != null ? collection.size() : 0;
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setType(Class<?> collectionType) {
        this.type = (Class<? extends Collection<Object>>) collectionType;
    }
    
    /*
     * Returns the property type.
     */
    @Override
    public Class<? extends Collection<Object>> getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Property#create()
     */
    @Override
    public Object createValue(ParsingContext context) {
        Object value = this.value.get(context);
        if (value == null) {
            value = createCollection();
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
        if (value != null && ((Collection<?>)value).isEmpty()) {
            value = null;
        }
        
        this.value.set(context, value);
        
        super.setValue(context, value);
    }
    
    protected Collection<Object> createCollection() {
        return ObjectUtils.newInstance(type);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Property#type()
     */
    @Override
    public int type() {
        return Property.AGGREGATION_COLLECTION;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Iteration#getIterationSize()
     */
    @Override
    public int getIterationSize() {
        return getSize();
    }

    @Override
    public void registerLocals(Set<ParserLocal<? extends Object>> locals) {
        if (locals.add(value)) {
            super.registerLocals(locals);
        }
    }
    
    @Override
    public boolean hasContent(ParsingContext context) {
        Collection<Object> collection = getCollection(context);
        return collection != null && collection.size() > 0; 
    }
    
    @Override
    protected void toParamString(StringBuilder s) {
        super.toParamString(s);
        if (type != null) {
            s.append(", type=").append(type.getSimpleName());
        }
    }
}
