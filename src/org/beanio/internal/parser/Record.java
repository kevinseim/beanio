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

import org.beanio.internal.util.DebugUtil;

/**
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class Record extends Segment implements Selector {

    // minimum occurrences
    private int minOccurs = 0;
    // maximum occurrences  
    private int maxOccurs = Integer.MAX_VALUE;
    // record order value
    private int order = 1;
    // the record format
    private RecordFormat format;
    // current record count
    private ParserLocal<Integer> count = new ParserLocal<>(0);

    /**
     * Constructs a new <tt>Record</tt>.
     */
    public Record() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Marshaller#marshal(org.beanio.parser2.MarshallingContext)
     */
    @Override
    public boolean marshal(MarshallingContext context) throws IOException {
        try {
            boolean marshalled = super.marshal(context);
            if (marshalled) {
                context.writeRecord();
            }
            return marshalled;
        }
        finally {
            // clear the context for the next marshalled record...
            context.clear();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Unmarshaller#unmarshal(org.beanio.parser2.UnmarshallingContext)
     */
    @Override
    public boolean unmarshal(UnmarshallingContext context) {
        try {
            // update the record context before unmarshalling
            context.recordStarted(getName());
            
            if (format != null) {
                format.validate(context);
                
                if (context.hasRecordErrors()) {
                    return true;
                }
            }
            
            // invoke segment unmarshalling
            super.unmarshal(context);
            
            return true;
        }
        finally {
            context.recordCompleted();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#skip(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public void skip(UnmarshallingContext context) {
        context.recordSkipped();
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#matchNext(org.beanio.internal.parser.MarshallingContext)
     */
    @Override
    public Selector matchNext(MarshallingContext context) {
        Property property = getProperty();
        if (property != null) {
            String componentName = context.getComponentName();
            if (componentName != null && !getName().equals(componentName)) {
                return null;
            }
            
            Object value = context.getBean();
            if (property.defines(value)) {
                setCount(context, getCount(context) + 1);
                property.setValue(context, value);
                return this;
            }
        }
        else if (context.getBean() == null) {
            String componentName = context.getComponentName();
            if (componentName != null && getName().equals(componentName)) {
                setCount(context, getCount(context) + 1);
                return this;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#matchNext(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public Selector matchNext(UnmarshallingContext context) {
        if (matches(context)) {
            setCount(context, getCount(context) + 1);
            return this;
        }
        return null;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Unmarshaller#matches(org.beanio.parser2.UnmarshallingContext)
     */
    @Override
    public boolean matches(UnmarshallingContext context) {
        if (format != null) {
            if (!format.matches(context)) {
                return false;
            }
        }
        return super.matches(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#matchAny(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public Selector matchAny(UnmarshallingContext context) {
        return matches(context) ? this : null;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#close(org.beanio.internal.parser.ParsingContext)
     */
    @Override
    public Selector close(ParsingContext context) {
        return getCount(context) < getMinOccurs() ? this : null;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#reset(org.beanio.internal.parser.ParsingContext)
     */
    @Override
    public void reset(ParsingContext context) { }
    
    /**
     * Updates a Map with the current state of the Marshaller.  Used for
     * creating restartable Writers for Spring Batch.
     * @param namespace a String to prefix all state keys with
     * @param state the Map to update with the latest state
     * @since 1.2
     */
    @Override
    public void updateState(ParsingContext context, String namespace, Map<String, Object> state) {
        state.put(getKey(namespace, COUNT_KEY), count.get(context));
    }

    /**
     * Restores a Map of previously stored state information.  Used for
     * restarting XML writers from Spring Batch.
     * @param namespace a String to prefix all state keys with
     * @param state the Map containing the state to restore
     * @since 1.2
     */
    @Override
    public void restoreState(ParsingContext context, String namespace, Map<String, Object> state) {
        String key = getKey(namespace, COUNT_KEY);
        Integer n = (Integer) state.get(key);
        if (n == null) {
            throw new IllegalStateException("Missing state information for key '" + key + "'");
        }
        count.set(context, n);
    }
    
    /**
     * Returns a Map key for accessing state information for this Node.
     * @param namespace the assigned namespace for the key
     * @param name the state information to access
     * @return the fully qualified key
     */
    protected String getKey(String namespace, String name) {
        return namespace + "." + getName() + "." + name;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#isRecordGroup()
     */
    @Override
    public boolean isRecordGroup() {
        return false;
    }
    
    @Override
    public int getMinOccurs() {
        return minOccurs;
    }
    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }
    @Override
    public int getMaxOccurs() {
        return maxOccurs;
    }
    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }
    @Override
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    @Override
    public int getCount(ParsingContext context) {
        return count.get(context);
    }
    @Override
    public void setCount(ParsingContext context, int count) {
        this.count.set(context, count);
    }
    public RecordFormat getFormat() {
        return format;
    }
    public void setFormat(RecordFormat format) {
        this.format = format;
    }
    
    @Override
    public void registerLocals(Set<ParserLocal<?>> locals) {
        if (locals.add(count)) {
            super.registerLocals(locals);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#isMaxOccursReached()
     */
    @Override
    public boolean isMaxOccursReached(ParsingContext context) {
        return getCount(context) >= getMaxOccurs();
    }
    
    @Override
    protected void toParamString(StringBuilder s) {
        s.append(", order=").append(order);
        s.append(", occurs=").append(DebugUtil.formatRange(minOccurs, maxOccurs));
        super.toParamString(s);
        s.append(", format=").append(format);
    }
}
