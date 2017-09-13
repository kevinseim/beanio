package org.beanio.internal.compiler.accessor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.beanio.internal.compiler.PropertyAccessorFactory;
import org.beanio.internal.parser.PropertyAccessor;
import org.beanio.internal.parser.accessor.*;

/**
 * {@link PropertyAccessorFactory} implementations based on Java reflection.
 * 
 * @author Kevin Seim
 * @since 2.0.1
 */
public class ReflectionAccessorFactory implements PropertyAccessorFactory {

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.compiler.PropertyAccessorFactory#getPropertyAccessor(java.lang.Class, java.beans.PropertyDescriptor, int)
     */
    @Override
    public PropertyAccessor getPropertyAccessor(
        Class<?> parent, PropertyDescriptor descriptor, int carg) {
    
        MethodReflectionAccessor a = new MethodReflectionAccessor(descriptor, carg);
        a.setConstructorArgumentIndex(carg);
        return a;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.compiler.PropertyAccessorFactory#getPropertyAccessor(java.lang.Class, java.lang.reflect.Field, int)
     */
    @Override
    public PropertyAccessor getPropertyAccessor(
        Class<?> parent, Field field, int carg) {
    
        FieldReflectionAccessor a = new FieldReflectionAccessor(field, carg);
        a.setConstructorArgumentIndex(carg);
        return a;
    }
}
