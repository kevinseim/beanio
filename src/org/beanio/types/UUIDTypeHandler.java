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
package org.beanio.types;

import java.util.UUID;

/**
 * A type handler for {@link UUID} values.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class UUIDTypeHandler implements TypeHandler {

    /**
     * Parses a UUID using {@link UUID#fromString(String)}.
     * @param text the text to parse
     * @return the parsed {@link UUID} or null if <tt>text</tt>
     *   is null or an empty string
     */
    @Override
    public Object parse(String text) throws TypeConversionException {
        if (text == null || "".equals(text)) {
            return null;
        }
        
        try {
            return UUID.fromString(text);
        }
        catch (IllegalArgumentException ex) {
            throw new TypeConversionException("Invalid UUID " +
                "value '" + text + "'", ex);
        }
    }

    /**
     * Formats a {@link UUID} by calling <tt>toString()</tt>.  If <tt>value</tt> is
     * null, <tt>null</tt> is returned.
     * @param value the {@link UUID} to format
     * @return the formatted text
     */
    @Override
    public String format(Object value) {
        if (value == null)
            return null;
        else
            return value.toString();
    }

    /**
     * Returns {@link UUID}.
     */
    @Override
    public Class<?> getType() {
        return UUID.class;
    }
}
