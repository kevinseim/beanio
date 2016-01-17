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

import org.beanio.internal.parser.PropertyAccessor;

/**
 * Base class for {@link PropertyAccessor} implementations.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class PropertyAccessorSupport implements PropertyAccessor {

    private int constructorArgumentIndex = -1;
    
    /**
     * Constructs a new <tt>PropertyAccessorSupport</tt>.
     */
    public PropertyAccessorSupport() { }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.PropertyAccessor#isConstructorArgument()
     */
    @Override
    public boolean isConstructorArgument() {
        return getConstructorArgumentIndex() >= 0;
    }

    /**
     * Sets the constructor argument index (starting at 0).  Set to -1 to indicate
     * this property is not a constructor argument.
     * @param index the constructor argument index
     */
    public void setConstructorArgumentIndex(int index) {
        this.constructorArgumentIndex = index;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.PropertyAccessor#getConstructorArgumentIndex()
     */
    @Override
    public int getConstructorArgumentIndex() {
        return constructorArgumentIndex;
    }
}
