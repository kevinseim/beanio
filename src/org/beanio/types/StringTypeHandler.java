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
 * A type handler implementation for the <tt>String</tt> class.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class StringTypeHandler implements TypeHandler {

    private boolean trim = false;
    private boolean nullIfEmpty = false;

    /**
     * Parses a <tt>String</tt> from the given text.
     * @param text the text to parse
     * @return the parsed <tt>String</tt>
     */
    @Override
    public String parse(String text) {
        if (text != null) {
            if (trim) {
                text = text.trim();
            }
            if (nullIfEmpty && text.length() == 0) {
                text = null;
            }
        }
        return text;
    }

    /**
     * Formats the value by calling {@link Object#toString()}.
     * @param value the value to format
     * @return the formatted value, or <tt>null</tt> if <tt>value</tt> is <tt>null</tt>
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
        return String.class;
    }

    /**
     * Returns <tt>true</tt> if <tt>parse(String)</tt> should trim the text.
     * By default, <tt>trim</tt> is <tt>false</tt> which allows trimming to
     * be controlled by the field definition.
     * @return <tt>true</tt> if parsed text is trimmed
     */
    public boolean isTrim() {
        return trim;
    }

    /**
     * Set to <tt>true</tt> to trim text when parsing.
     * @param trim <tt>true</tt> if text should be trimmed when parsed
     */
    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    /**
     * Returns <tt>true</tt> if empty string values are parsed as <tt>null</tt>.
     * Defaults to <tt>false</tt>.
     * @return <tt>true</tt> to convert the empty string to <tt>null</tt>
     */
    public boolean isNullIfEmpty() {
        return nullIfEmpty;
    }

    /**
     * Set to <tt>true</tt> if the parsed empty strings should be converted to <tt>null</tt>.
     * @param nullIfEmpty <tt>true</tt> to convert empty string to <tt>null</tt>
     */
    public void setNullIfEmpty(boolean nullIfEmpty) {
        this.nullIfEmpty = nullIfEmpty;
    }
}
