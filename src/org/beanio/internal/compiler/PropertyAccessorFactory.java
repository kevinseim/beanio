package org.beanio.internal.compiler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.beanio.internal.parser.PropertyAccessor;

/**
 * Factory interface for creating {@link PropertyAccessor}
 * implementations.
 * 
 * @author Kevin Seim
 * @since 2.0.1
 */
public interface PropertyAccessorFactory {

    /**
     * Creates a new {@link PropertyAccessor}.
     * @param parent the parent bean object type
     * @param descriptor the {@link PropertyDescriptor} to access
     * @param carg the constructor argument index
     * @return the new {@link PropertyAccessor}
     */
    public PropertyAccessor getPropertyAccessor(
        Class<?> parent, PropertyDescriptor descriptor, int carg);
    
    /**
     * Creates a new {@link PropertyAccessor}.
     * @param parent the parent bean object type
     * @param field the {@link Field} to access
     * @param carg the constructor argument index
     * @return the new {@link PropertyAccessor}
     */
    public PropertyAccessor getPropertyAccessor(
        Class<?> parent, Field field, int carg);
}
