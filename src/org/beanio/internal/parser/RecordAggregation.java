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

import java.util.*;

import org.beanio.internal.util.DebugUtil;

/**
 * 
 * @author Kevin Seim
 * @since 2.0.1
 */
public abstract class RecordAggregation extends DelegatingParser implements Selector, Property {

    // the property accessor, may be null if not bound
    private PropertyAccessor accessor;
    // the property value
    protected ParserLocal<Object> value = new ParserLocal<Object>(Value.MISSING);
    // the collection type
    private Class<?> type;
    // true if null should be returned for an empty collection
    protected boolean lazy;
    
    /**
     * Constructs a new <tt>RecordAggregation</tt>.
     */
    public RecordAggregation() { }
    
    /**
     * Sets the collection type.
     * @param type {@link Collection} class type
     */
    @Override
    public void setType(Class<?> type) {
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#getType()
     */
    @Override
    public Class<?> getType() {
        return type;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#createValue()
     */
    @Override
    public Object createValue(ParsingContext context) {
        if (value.get(context) == Value.MISSING) {
            value.set(context, createAggregationType());
        }
        return getValue(context);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#getNullValue()
     */
    public Object getNullValue() {
        return createAggregationType();
    }
    
    protected Object createAggregationType() {
        return ObjectUtils.newInstance(type);
    }
    
    @Override
    public Object getValue(ParsingContext context) {
        return value.get(context);
    }
    
    @Override
    public void clearValue(ParsingContext context) {
        value.set(context, Value.MISSING);
    }

    @Override
    public void setValue(ParsingContext context, Object value) {
        this.value.set(context, value);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#getProperty()
     */
    @Override
    public Property getProperty() {
        // for now, a collection cannot be a property root so its safe to return null here
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#matchNextRecord(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public Selector matchNext(UnmarshallingContext context) {
        if (getSelector().matchNext(context) != null) {
            return this;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#matchAny(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public Selector matchAny(UnmarshallingContext context) {
        return getSelector().matchAny(context);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#matchNextBean(org.beanio.internal.parser.MarshallingContext, java.lang.Object)
     */
    @Override
    public Selector matchNext(MarshallingContext context) {
        return getSelector().matchNext(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#close(org.beanio.internal.parser.ParsingContext)
     */
    @Override
    public Selector close(ParsingContext context) {
        return getSelector().close(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#reset(org.beanio.internal.parser.ParsingContext)
     */
    @Override
    public void reset(ParsingContext context) {
        getSelector().reset(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#getCount(org.beanio.internal.parser.ParsingContext)
     */
    @Override
    public int getCount(ParsingContext context) {
        return getSelector().getCount(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#setCount(org.beanio.internal.parser.ParsingContext, int)
     */
    @Override
    public void setCount(ParsingContext context, int count) {
        getSelector().setCount(context, count);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#getOrder()
     */
    @Override
    public int getOrder() {
        return getSelector().getOrder();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#isMaxOccursReached(org.beanio.internal.parser.ParsingContext)
     */
    @Override
    public boolean isMaxOccursReached(ParsingContext context) {
        return getSelector().isMaxOccursReached(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.util.StatefulWriter#updateState(java.lang.String, java.util.Map)
     */
    @Override
    public void updateState(ParsingContext context, String namespace, Map<String, Object> state) {
        getSelector().updateState(context, namespace, state);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.util.StatefulWriter#restoreState(java.lang.String, java.util.Map)
     */
    @Override
    public void restoreState(ParsingContext context, String namespace, Map<String, Object> state) throws IllegalStateException {
        getSelector().restoreState(context, namespace, state);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#getMinOccurs()
     */
    @Override
    public int getMinOccurs() {
        return getSelector().getMinOccurs();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#getMaxOccurs()
     */
    @Override
    public int getMaxOccurs() {
        return getSelector().getMaxOccurs();
    }
    
    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }
    
    @Override
    protected boolean isSupportedChild(Component child) {
        return child instanceof Selector;
    }
    
    /**
     * Returns the child selector.
     * @return the child {@link Selector}
     */
    public Selector getSelector() {
        return (Selector) getFirst();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#getAccessor()
     */
    @Override
    public PropertyAccessor getAccessor() {
        return accessor;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#setAccessor(org.beanio.internal.parser.PropertyAccessor)
     */
    @Override
    public void setAccessor(PropertyAccessor accessor) {
        this.accessor = accessor;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#defines(java.lang.Object)
     */
    @Override
    public boolean defines(Object value) {
        throw new IllegalStateException("RecordAggregation cannot identify a bean");
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#setIdentifier(boolean)
     */
    @Override
    public void setIdentifier(boolean identifier) {
        // a collection cannot be used to identify a bean
    }

    @Override
    public boolean isIdentifier() {
        // a collection cannot be used to identify a bean
        return false;
    }

    @Override
    public void registerLocals(Set<ParserLocal<? extends Object>> locals) {
        if (locals.add(value)) {
            super.registerLocals(locals);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#skip(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public void skip(UnmarshallingContext context) {
        getSelector().skip(context);
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
    protected void toParamString(StringBuilder s) {
        super.toParamString(s);
        if (type != null) {
            s.append(", type=").append(type.getSimpleName());
        }
        s.append(", ").append(DebugUtil.formatOption("lazy", lazy));
    }
}
