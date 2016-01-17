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

import java.lang.reflect.Constructor;
import java.util.*;

import org.beanio.*;
import org.beanio.internal.util.StringUtil;

/**
 * A component used to aggregate {@link Property}'s into a bean object, which
 * may also be a property of a parent bean object itself. 
 * 
 * <p>A bean may only have children that implement {@link Property}.</p>
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class Bean extends PropertyComponent implements Property {

    // the bean object
    private ParserLocal<Object> bean = new ParserLocal<Object>() {
        @Override
        public Object createDefaultValue() {
            return isRequired() ? null : Value.MISSING;
        }
    };
    // the constructor for creating this bean object (if null, the no-arg constructor is used)
    private Constructor<?> constructor;
    // used to temporarily hold constructor argument values when a constructor is specified
    private ParserLocal<Object[]> constructorArgs = new ParserLocal<Object[]>() {
        @Override
        public Object[] createDefaultValue() {
            return constructor != null ? new Object[constructor.getParameterTypes().length] : null;
        }
    };
    // whether to return null for objects with all nulls and/or empty strings
    private boolean lazy;
    
    /**
     * Constructs a new <tt>Bean</tt>.
     */
    public Bean() { }
       
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#clearValue()
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
     * @see org.beanio.parser2.Property#defines(java.lang.Object)
     */
    @Override
    public boolean defines(Object bean) {   
        if (getType() == null) {
            return false;
        }
        
        if (bean == null) {
            // allow beans that are not top level to still match if minOccurs=0
            return isMatchNull();
        }
        
        if (!getType().isAssignableFrom(bean.getClass())) {
            return false;
        }
        
        // 'identifier' indicates the value of a child component must match 
        if (!isIdentifier()) {
            return true;
        }
        
        // check identifying properties
        for (Component child : getChildren()) {
            Property property = (Property) child;
            
            // if the child property is not used to identify records, no need to go further
            if (!property.isIdentifier()) {
                continue;
            }
            
            Object value = property.getAccessor().getValue(bean);
            if (!property.defines(value)) {
                return false;
            }
        }
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#createValue()
     */
    @Override
    public Object createValue(ParsingContext context) {
        Object b = null;
        
        boolean hasProperties = false;
        
        // populate constructor arguments first
        if (constructor != null) {
            // lazily create...
            boolean create = false;
            
            Object[] cargs = constructorArgs.get(context);
            
            for (Component child : getChildren()) {
                Property property = (Property) child;
                
                PropertyAccessor accessor = property.getAccessor();
                if (accessor == null) {
                    throw new IllegalStateException("Accessor not set for property value '" + child.getName() + "'");
                }
                if (!accessor.isConstructorArgument()) {
                    continue;
                }
                
                Object value = property.getValue(context);
                if (value == Value.INVALID) {
                    return Value.INVALID;
                }
                else if (value == Value.MISSING) {
                    value = createMissingBeans ? property.createValue(context) : null;
                }
                else {
                    hasProperties = true;
                    create = create || !lazy || StringUtil.hasValue(value);
                }
                
                cargs[accessor.getConstructorArgumentIndex()] = value;
            }
            
            if (create) {
                b = newInstance(context);
            }
        }
        
        for (Component child : getChildren()) {
            Property property = (Property) child;
            if (property.getAccessor().isConstructorArgument()) {
                continue;
            }
            
            Object value = property.getValue(context);
            if (createMissingBeans && value == Value.MISSING) {
                value = property.createValue(context);
            }
            
            if (value == Value.INVALID) {
                bean.set(context, b);
                return Value.INVALID;
            }
            // explicitly null values must still be set on the bean...
            else if (value != Value.MISSING) {
                hasProperties = true;
                
                if (b == null) {
                    if (lazy) {
                        if (!StringUtil.hasValue(value)) {
                            continue;
                        }
                        
                        b = newInstance(context);
                        backfill(context, b, child);
                    }
                    else {
                        b = newInstance(context);
                    }
                }

                try {
                    if (value != null || !property.getType().isPrimitive()) {
                        property.getAccessor().setValue(b, value);
                    }
                }
                catch (Exception ex) {
                    throw new BeanIOException("Failed to set property '" + property.getName() + 
                        "' on bean '" + getName() + "'", ex);
                }
            }
        }

        
        if (b == null) {
            if (isRequired() || createMissingBeans) {
                b = newInstance(context);
            }
            else if (hasProperties) {
                b = null;
            }
            else {
                b = Value.MISSING;
            }
        }
        
        bean.set(context, b);
        return b;
    }
    
    /**
     * Backfill bean properties up to the component <code>stop</code>.
     * @param context the parsing context
     * @param bean the bean object
     * @param stop the component to stop at
     */
    private void backfill(ParsingContext context, Object bean, Component stop) {
        for (Component child : getChildren()) {
            if (stop == child) {
                return;
            }
            Property property = (Property) child;
            if (property.getAccessor().isConstructorArgument()) {
                continue;
            }
            
            Object value = property.getValue(context);
            if (value == Value.MISSING) {
                continue;
            }
            
            try {
                property.getAccessor().setValue(bean, value);
            }
            catch (Exception ex) {
                throw new BeanIOException("Failed to set property '" + property.getName() + 
                    "' on bean '" + getName() + "'", ex);
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Property#getValue()
     */
    @Override
    public Object getValue(ParsingContext context) {
        return bean.get(context);
    }

    /*
     * Sets the bean object and populates all of its child properties.
     * 
     */
    @Override
    public void setValue(ParsingContext context, Object value) {
        if (value == null) {
            clearValue(context);
            return;
        }
        
        Object b = value;
        Object defaultValue = null; //bean == null ? Value.MISSING : null;

        for (Component child : getChildren()) {
            Property property = (Property) child;
            
            Object propertyValue = defaultValue;
            if (b != null) {
                propertyValue = property.getAccessor().getValue(b);
            }

            property.setValue(context, propertyValue);
        }
        
        bean.set(context, b);
    }
    
    /**
     * Creates a new instance of this bean object.
     * @param context the {@link ParsingContext}
     * @return the new bean <tt>Object</tt>
     */
    protected Object newInstance(ParsingContext context) {
        // if the bean class is null, the record will be ignored and null is returned here
        Class<?> beanClass = getType();
        if (beanClass == null) {
            return null;
        }
        
        try {
            if (constructor == null) {
                return beanClass.newInstance();
            }
            else {
                return constructor.newInstance(constructorArgs.get(context));
            }
        }
        catch (Exception e) {
            throw new BeanReaderException("Failed to instantiate class '" + beanClass.getName() + "'", e);
        }
    }

    @Override
    protected boolean isSupportedChild(Component child) {
        return child instanceof Property;
    }

    @Override
    public void setRequired(boolean required) {
        super.setRequired(required);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#type()
     */
    @Override
    public int type() {
        return (isMap()) ? Property.MAP : Property.COMPLEX;
    }
    
    /**
     * Returns whether the bean object implements {@link Map}.
     * @return true if the bean object implements {@link Map}, false otherwise
     */
    protected boolean isMap() {
        return Map.class.isAssignableFrom(getType());
    }
    
    /**
     * Returns the {@link Constructor} used to instantiate this bean object, or null
     * if the default no-arg constructor is used.
     * @return the {@link Constructor}
     */
    public Constructor<?> getConstructor() {
        return constructor;
    }

    /**
     * Sets the {@link Constructor} used to instantiate this bean object.
     * @param constructor the {@link Constructor}
     */
    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }
    
    @Override
    public void registerLocals(Set<ParserLocal<?>> locals) {
        if (locals.add(bean)) {
            locals.add(constructorArgs);
            super.registerLocals(locals);
        }
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }
}
