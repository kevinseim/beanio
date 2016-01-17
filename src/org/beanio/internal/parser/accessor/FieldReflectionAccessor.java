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
package org.beanio.internal.parser.accessor;

import java.lang.reflect.Field;

import org.beanio.BeanIOException;
import org.beanio.internal.parser.PropertyAccessor;

/**
 * A {@link PropertyAccessor} that uses reflection to access a public field.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FieldReflectionAccessor extends PropertyAccessorSupport implements PropertyAccessor {

    private Field field;
    
    /**
     * Constructs a new <tt>FieldReflectionAccessor</tt>.
     * @param field the reflected {@link Field}
     * @param constructorArgumentIndex the constructor argument index, or -1 if not a constructor argument
     */
    public FieldReflectionAccessor(Field field, int constructorArgumentIndex) {
        this.field = field;
        setConstructorArgumentIndex(constructorArgumentIndex);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.PropertyAccessor#getValue(java.lang.Object)
     */
    @Override
    public Object getValue(Object bean) {
        try {
            return field.get(bean);
        }
        catch (Exception ex) {
            throw new BeanIOException("Failed to get field '" + field.getName() + 
                "' from bean class '" + bean.getClass().getName() + "'", ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.PropertyAccessor#setValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setValue(Object bean, Object value) {
        try {
            field.set(bean, value);
        }
        catch (Exception ex) {
            throw new BeanIOException("Failed to set field '" + field.getName() + 
                "' on bean class '" + bean.getClass().getName() + "'", ex);
        }
    }
}
