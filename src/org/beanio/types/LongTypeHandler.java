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

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * A type handler implementation for the <tt>Long</tt> class.  If <tt>pattern</tt>
 * is set, a <tt>DecimalFormat</tt> is used to parse and format the value.  Otherwise,
 * the value is parsed and formatted using the <tt>Long</tt> class.
 * 
 * @author Kevin Seim
 * @since 1.0
 * @see DecimalFormat
 */
public class LongTypeHandler extends NumberTypeHandler {

    @Override
    protected Long createNumber(String text) throws NumberFormatException {
        return new Long(text);
    }

    @Override
    protected Long createNumber(BigDecimal bg) throws ArithmeticException {
        return bg.longValueExact();
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#getType()
     */
    @Override
    public Class<?> getType() {
        return Long.class;
    }
}
