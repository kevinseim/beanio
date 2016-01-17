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
package org.beanio.internal.parser.accessor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.beanio.*;
import org.beanio.internal.parser.PropertyAccessor;

/**
 * A {@link PropertyAccessor} that uses reflection to get and set a bean value.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class MethodReflectionAccessor extends PropertyAccessorSupport implements PropertyAccessor {

    private PropertyDescriptor descriptor;
    private Method getter;
    private Method setter;
    
    /**
     * Constructs a new <tt>ReflectionAccessor</tt>.
     * @param descriptor the PropertyDescriptor describing the bean property
     * @param constructorArgumentIndex the constructor argument index, or -1 if not a constructor argument
     */
    public MethodReflectionAccessor(PropertyDescriptor descriptor, int constructorArgumentIndex) {
        this.descriptor = descriptor;
        setConstructorArgumentIndex(constructorArgumentIndex);
        
        if (descriptor != null) {
            getter = descriptor.getReadMethod();
            setter = descriptor.getWriteMethod();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.PropertyAccessor#getValue(java.lang.Object)
     */
    @Override
    public Object getValue(Object bean) {
        if (getter == null) {
            throw new BeanIOException("There is no readable property named '" + 
                descriptor.getName() + "' on bean class '" + bean.getClass().getName() + "'");
        }

        // user the getter method to extract the field value from the bean class
        try {
            return getter.invoke(bean);
        }
        catch (Exception ex) {
            throw new BeanIOException("Failed to invoke method '" + getter.getName() + 
                "' on bean class '" + bean.getClass().getName() + "'", ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.PropertyAccessor#setValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setValue(Object bean, Object value) {
        if (setter == null) {
            throw new BeanIOException(
                "There is no writeable property named '" + descriptor.getName() + 
                "' on class '" + bean.getClass().getName() + "'");
        }
        
        try {
            setter.invoke(bean, new Object[] { value });
        }
        catch (Exception ex) {
            throw new BeanIOException("Failed to invoke method '" + setter.getName() + 
                "' on bean class '" + bean.getClass().getName() + "'", ex);
        }
    }
}
