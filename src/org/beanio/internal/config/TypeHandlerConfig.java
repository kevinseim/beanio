/*
 * Copyright 2010-2013 Kevin Seim
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

import org.beanio.types.TypeHandler;

/**
 * Stores configuration settings for a custom type handler.  Type handlers
 * are used to convert field text to values and back.
 * 
 * @author Kevin Seim
 * @since 1.0
 * @see TypeHandler
 */
public class TypeHandlerConfig extends BeanConfig<TypeHandler> {

    private String name;
    private String type;
    private String format;

    /**
     * Returns the name of the type handler.
     * @return the type handler name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the type handler.
     * @param name the type handler name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the class name to register this type handler under.
     * @return the class name to register this type handler under
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the class name to register this type handler under.
     * @param type the class name to register this type handler
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the stream format to register this type handler for, or <tt>null</tt>
     * if the type handler is used for all formats.
     * @return the stream format (xml, csv, delimited, or fixedlength) or <tt>null</tt>
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the stream format to register this type handler for.  By default,
     * the stream format is <tt>null</tt> and the type handler is used for
     * all formats.
     * @param format the stream format (xml, csv, delimited, or fixedlength)
     */
    public void setFormat(String format) {
        this.format = format;
    }
}
