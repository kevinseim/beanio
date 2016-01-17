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

import java.util.Map;

import org.beanio.internal.parser.PropertyAccessor;

/**
 * A {@link PropertyAccessor} for getting and setting {@link Map} values.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class MapAccessor implements PropertyAccessor {

    private String key;
    
    /**
     * Constructs a new <tt>MapAccessor</tt>.
     * @param key the key used to get and set a value from a Map bean
     */
    public MapAccessor(String key) {
        this.key = key;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.PropertyAccessor#getValue(java.lang.Object)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getValue(Object bean) {
        return ((Map)bean).get(key);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.PropertyAccessor#setValue(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setValue(Object bean, Object value) {
        ((Map)bean).put(key, value);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.PropertyAccessor#isConstructorArgument()
     */
    @Override
    public boolean isConstructorArgument() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.PropertyAccessor#getConstructorArgumentIndex()
     */
    @Override
    public int getConstructorArgumentIndex() {
        return -1;
    }
}
