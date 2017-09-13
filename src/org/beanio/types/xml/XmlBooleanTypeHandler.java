/*
 * Copyright 2011 Kevin Seim
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
package org.beanio.types.xml;

import org.beanio.types.*;

/**
 * A type handler implementation for the <tt>Boolean</tt> class based on the
 * W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#boolean">boolean</a> datatype
 * specification.
 * 
 * @author Kevin Seim
 * @since 1.1
 */
public class XmlBooleanTypeHandler implements TypeHandler {

    private boolean numericFormatEnabled = false;
    
    /**
     * Constructs a new <tt>XmlBooleanTypeHandler</tt>.
     */
    public XmlBooleanTypeHandler() { }
    
    /**
     * Parses a Boolean object from the given text.
     * @param text the text to parse
     * @return new Boolean
     */
    @Override
    public Boolean parse(String text) throws TypeConversionException {
        if (text == null || "".equals(text))
            return null;

        if ("true".equals(text) || "1".equals(text)) {
            return Boolean.TRUE;
        }
        else if ("false".equals(text) || "0".equals(text)) {
            return Boolean.FALSE;
        }
        else {
            throw new TypeConversionException("Invalid XML boolean");   
        }
    }

    /**
     * Returns {@link Boolean#toString()}, or <tt>null</tt> if <tt>value</tt>
     * is <tt>null</tt>.
     */
    @Override
    public String format(Object value) {
        if (value == null)
            return null;
        
        boolean b = ((Boolean)value).booleanValue();
        if (isNumericFormatEnabled()) {
            return b ? "1" : "0";
        }
        else {
            return b ? "true" : "false";
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#getType()
     */
    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    /**
     * Returns whether the numeric format of a XML boolean is used to format
     * Java <tt>Boolean</tt> types.
     * @return <tt>true</tt> if a Boolean is formatted using 0 and 1 instead
     *   of 'true' and 'false'
     */
    public boolean isNumericFormatEnabled() {
        return numericFormatEnabled;
    }

    /**
     * Sets whether the numeric format of a XML boolean is used to format Java
     * <tt>Boolean</tt> types.
     * @param numericFormatEnabled <tt>true</tt> if a Boolean is formatted using 0 
     *   and 1 instead of 'true' and 'false'
     */
    public void setNumericFormatEnabled(boolean numericFormatEnabled) {
        this.numericFormatEnabled = numericFormatEnabled;
    }
}
