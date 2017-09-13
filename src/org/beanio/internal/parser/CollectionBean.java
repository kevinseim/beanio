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
package org.beanio.internal.parser;

import java.util.*;

/**
 * A {@link Property} that stores children in a {@link Collection}.
 * 
 * <p>If a child property is missing or null, null is added to the collection.</p>
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class CollectionBean extends PropertyComponent implements Property {

    private ParserLocal<Object> bean = new ParserLocal<Object>() {
        @Override
        protected Object createDefaultValue() {
            return isRequired() ? null : Value.MISSING;
        }
    };
    
    /**
     * Constructs a new <tt>CollectionBean</tt>.
     */
    public CollectionBean() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#type()
     */
    @Override
    public int type() {
        return Property.COLLECTION;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.PropertyComponent#clearValue()
     */
    @Override
    public void clearValue(ParsingContext context) {
        for (Component child : getChildren()) {
            ((Property) child).clearValue(context);
        }
        bean.set(context, isRequired() ? null : Value.MISSING);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.PropertyComponent#createValue()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object createValue(ParsingContext context) {
        Object b = null;
        
        for (Component child : getChildren()) {
            Property property = (Property) child;
            
            Object value = property.getValue(context);
            if (createMissingBeans && value == Value.MISSING) {
                value = property.createValue(context);
            }
            
            if (value == Value.INVALID) {
                bean.set(context, b);
                return Value.INVALID;
            }

            if (value == Value.MISSING) {
                if (b == null) {
                    continue;
                }
                value = null;
            }
            
            if (b == null) {
                b = newInstance();
                backfill((Collection<Object>)b, child);
            }
            
            ((Collection<Object>)b).add(value);
        }

        if (b == null) {
            b = isRequired() || createMissingBeans ? newInstance() : Value.MISSING;
        }
        
        bean.set(context, b);
        return b;
    }
    
    private void backfill(Collection<Object> collection, Component to) {
        for (Component child : getChildren()) {
            if (child == to) {
                return;
            }
            collection.add(null);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.PropertyComponent#getValue()
     */
    @Override
    public Object getValue(ParsingContext context) {
        return bean.get(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.PropertyComponent#setValue(java.lang.Object)
     */
    @Override
    public void setValue(ParsingContext context, Object value) {
        if (value == null) {
            clearValue(null);
            return;
        }
        
        this.bean.set(context, value);
        
        Iterator<?> iter = ((Collection<?>)value).iterator();
        for (Component child : getChildren()) {
            Object childValue = null;
            if (iter.hasNext()) {
                childValue = iter.next();
            }

            ((Property) child).setValue(context, childValue);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.PropertyComponent#defines(java.lang.Object)
     */
    @Override
    public boolean defines(Object value) {
        if (getType() == null) {
            return false;
        }
        if (value == null) {
            return isMatchNull();
        }
        if (!getType().isAssignableFrom(value.getClass())) {
            return false;
        }
        if (!isIdentifier()) {
            return true;
        }
        
        Iterator<?> iter = ((Collection<?>)value).iterator();
        
        // check identifying properties
        for (Component child : getChildren()) {
            Object childValue = null;
            if (iter.hasNext()) {
                childValue = iter.next();    
            }
            
            // if the child property is not used to identify records, no need to go further
            Property property = (Property) child;
            if (!property.isIdentifier()) {
                continue;
            }
            if (!property.defines(childValue)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Creates a new instance of this bean object.
     * @return the new bean <tt>Object</tt>
     */
    protected Object newInstance() {
        return ObjectUtils.newInstance(getType());
    }

    @Override
    public void registerLocals(Set<ParserLocal<? extends Object>> locals) {
        if (locals.add(bean)) {
            super.registerLocals(locals);
        }
    }
    
    
}
