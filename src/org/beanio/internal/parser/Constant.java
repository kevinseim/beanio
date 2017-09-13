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

/**
 * A simple property implementation that stores a constant value.
 * @author Kevin Seim
 * @since 2.0
 */
public class Constant extends Component implements Property {

    private Object value;
    private Class<?> type;
    private boolean identifier;
    private PropertyAccessor accessor;
    
    /**
     * Constructs a new <tt>Constant</tt>.
     */
    public Constant() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#type()
     */
    @Override
    public int type() {
        return Property.SIMPLE;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#defines(java.lang.Object)
     */
    @Override
    public boolean defines(Object value) {
        return this.value.equals(value);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#clearValue()
     */
    @Override
    public void clearValue(ParsingContext context) { }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#createValue()
     */
    @Override
    public Object createValue(ParsingContext context) {
        return getValue(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#getValue()
     */
    @Override
    public Object getValue(ParsingContext context) {
        return value;
    }

    /**
     * Sets the constant value.
     * @param value the value
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#setValue(java.lang.Object)
     */
    @Override
    public void setValue(ParsingContext context, Object value) {
        
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#getType()
     */
    @Override
    public Class<?> getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#setType(java.lang.Class)
     */
    @Override
    public void setType(Class<?> type) {
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#isIdentifier()
     */
    @Override
    public boolean isIdentifier() {
        return identifier;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#setIdentifier(boolean)
     */
    @Override
    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#getAccessor()
     */
    @Override
    public PropertyAccessor getAccessor() {
        return accessor;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#setAccessor(org.beanio.parser.PropertyAccessor)
     */
    @Override
    public void setAccessor(PropertyAccessor accessor) {
        this.accessor = accessor;
    }
}
