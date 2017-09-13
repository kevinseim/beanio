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
 * A type handler for the <tt>Character</tt> class.  A <tt>TypeConversionException</tt>
 * is thrown if the input text length exceeds 1 character.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class CharacterTypeHandler implements TypeHandler {

    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#parse(java.lang.String)
     */
    @Override
    public Character parse(String text) throws TypeConversionException {
        if (text == null || "".equals(text))
            return null;

        if (text.length() > 1) {
            throw new TypeConversionException("Invalid character");
        }

        return text.charAt(0);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#format(java.lang.Object)
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
        return Character.class;
    }
}
