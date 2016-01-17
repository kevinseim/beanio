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

/**
 * A type handler implementation for the <tt>Boolean</tt> class, that
 * simply delegate parsing to its constructor.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class BooleanTypeHandler implements TypeHandler {

    /**
     * Parses a Boolean object from the given text.
     * @param text the text to parse
     * @return new Boolean
     */
    @Override
    public Boolean parse(String text) throws TypeConversionException {
        if (text == null || "".equals(text))
            return null;

        return new Boolean(text);
    }

    /**
     * Returns {@link Boolean#toString()}, or <tt>null</tt> if <tt>value</tt>
     * is <tt>null</tt>.
     */
    @Override
    public String format(Object value) {
        if (value == null)
            return null;
        return value.toString();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#getType()
     */
    @Override
    public Class<?> getType() {
        return Boolean.class;
    }
}
