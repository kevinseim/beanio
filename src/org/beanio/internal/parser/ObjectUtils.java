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
package org.beanio.internal.parser;

import org.beanio.BeanIOException;

/**
 * Utility class for instantiating objects. 
 * @author Kevin
 * @since 2.0.3
 */
class ObjectUtils {

    public static <T> T newInstance(Class<T> type) {
        if (type == null) {
            return null;
        }
        
        try {
            return type.newInstance();
        }
        catch (Exception ex) {
            throw new BeanIOException("Failed to instantiate class '" + type.getName() + "'");
        }
    }
    
}
