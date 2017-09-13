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
import java.util.Collection;

import org.beanio.internal.util.StringUtil;

/**
 * A {@link Parser} tree component for parsing a collection of bean objects, where
 * a bean object is mapped to a {@link Record} or {@link Group}.
 * 
 * <p>A <tt>RecordCollection</tt> supports a single {@link Record} or {@link Group} child.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class RecordCollection extends RecordAggregation {

    /**
     * Constructs a new <tt>RecordCollection</tt>.
     */
    public RecordCollection() { }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean unmarshal(UnmarshallingContext context) {
        // allow the delegate to unmarshal itself
        boolean result = super.unmarshal(context);
        
        Object aggregatedValue = getSelector().getValue(context);
        if (aggregatedValue != Value.INVALID) {
        	if (!lazy || StringUtil.hasValue(aggregatedValue)) {
        		Object aggregation = value.get(context);
        		if (aggregation == null || aggregation == Value.MISSING) {
        			aggregation = createAggregationType();
        			value.set(context, aggregation);
        		}

        		Collection<Object> collection = (Collection<Object>) aggregation;
        		collection.add(aggregatedValue);
        	}
        }
        
        getParser().clearValue(context);
        
        return result;
    }

    @Override
    public boolean marshal(MarshallingContext context) throws IOException {
        int minOccurs = getMinOccurs();
        
        Collection<Object> collection = getCollection(context);
        if (collection == null && minOccurs == 0) {
            return false;
        }
        
        Parser delegate = getParser();
        int maxOccurs = getMaxOccurs();
        int index = 0;
        
        if (collection != null) {
            for (Object value : collection) {
                if (index < maxOccurs) {
                    delegate.setValue(context, value);
                    delegate.marshal(context);
                    ++index;
                }
                else {
                    return true;
                }
            }
        }
        
        if (index < minOccurs) {
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
        if (value != null && ((Collection<?>)value).isEmpty()) {
            value = null;
        }
        super.setValue(context, value);
    }
    
    /**
     * Returns the collection value being parsed.
     * @param context the {@link ParsingContext}
     * @return the {@link Collection}
     */
    @SuppressWarnings("unchecked")
    protected Collection<Object> getCollection(ParsingContext context) {
        return (Collection<Object>) super.getValue(context);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#type()
     */
    @Override
    public int type() {
        return Property.AGGREGATION_COLLECTION;
    }

    @Override
    public boolean hasContent(ParsingContext context) {
        Collection<Object> collection = getCollection(context);
        return collection != null && collection.size() > 0; 
    }
}
