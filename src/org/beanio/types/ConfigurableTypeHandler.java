/*
 * Copyright 2010-2011 Kevin Seim
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
package org.beanio.types;

import java.util.Properties;

/**
 * Interface for type handlers that support field specific customization.  When a type
 * handler is registered that implements this interface, the <tt>TypeHandlerFactory</tt> 
 * invokes <tt>newInstance(Properties)</tt> if any type handler field properties were
 * set.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public interface ConfigurableTypeHandler extends TypeHandler {

    /** The field format pattern */
    public static final String FORMAT_SETTING = "format";
    
    /**
     * Creates a customized instance of this type handler.
     * @param properties the properties for customizing the instance
     * @return the new <tt>TypeHandler</tt>
     * @throws IllegalArgumentException if a property value is invalid
     */
    public TypeHandler newInstance(Properties properties) throws IllegalArgumentException;
    
}
