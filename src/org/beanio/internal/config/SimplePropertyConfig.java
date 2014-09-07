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
package org.beanio.internal.config;

import java.text.*;

import org.beanio.types.TypeHandler;

/**
 * A base class for configuration components that can be bound to "simple
 * attributes" of a bean object.  A simple attribute is one that does not have
 * any child properties itself and can be derived using a {@link TypeHandler}.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class SimplePropertyConfig extends PropertyConfig {

    private String handler;
    private TypeHandler typeHandlerInstance;
    private String format;
    
    /**
     * Constructs a new <tt>SimplePropertyConfig</tt>.
     */
    public SimplePropertyConfig() { }
    
    /**
     * Returns the name of the custom type handler used for type 
     * conversion by this component, or <tt>null</tt> if the default
     * type handler is sufficient.
     * @return the name of a custom type handler
     */
    public String getTypeHandler() {
        return handler;
    }

    /**
     * Sets the name of the custom type handler to use for type 
     * conversion by this component.  Set to <tt>null</tt> if the default
     * type handler is sufficient.
     * @param handler the name of a custom type handler
     */
    public void setTypeHandler(String handler) {
        this.handler = handler;
    }

    /**
     * Returns the type handler.
     * @return the {@link TypeHandler}
     */
    public TypeHandler getTypeHandlerInstance() {
        return typeHandlerInstance;
    }

    /**
     * Sets the type handler.
     * @param typeHandlerInstance the {@link TypeHandler}
     */
    public void setTypeHandlerInstance(TypeHandler typeHandlerInstance) {
        this.typeHandlerInstance = typeHandlerInstance;
    }

    /**
     * Returns the pattern used by date and number type handlers to parse
     * and format the property value.
     * @return the date or number format pattern
     * @see SimpleDateFormat
     * @see DecimalFormat
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the pattern used by date and number type handlers to parse
     * and format the property value.
     * @param format the date or number format pattern
     * @see SimpleDateFormat
     * @see DecimalFormat
     */
    public void setFormat(String format) {
        this.format = format;
    }
}
