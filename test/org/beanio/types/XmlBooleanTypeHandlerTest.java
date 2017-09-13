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
package org.beanio.types;

import static org.junit.Assert.*;

import org.beanio.types.xml.XmlBooleanTypeHandler;
import org.junit.Test;

/**
 * JUnit test cases for the <tt>XmlBooleanTypeHandler</tt>.
 * 
 * @author Kevin Seim
 * @since 1.1
 * @see XmlBooleanTypeHandler
 */
public class XmlBooleanTypeHandlerTest {
    
    @Test
    public void testParse() throws TypeConversionException {
        XmlBooleanTypeHandler handler = new XmlBooleanTypeHandler();
        assertTrue(handler.parse("true"));
        assertTrue(handler.parse("1"));
        assertFalse(handler.parse("false"));
        assertFalse(handler.parse("0"));
        assertNull(handler.parse(""));
        assertNull(handler.parse(null));
    }
    
    @Test
    public void testTextualFormat() {
        XmlBooleanTypeHandler handler = new XmlBooleanTypeHandler();
        assertFalse(handler.isNumericFormatEnabled());
        assertNull(handler.format(null));
        assertEquals("false", handler.format(Boolean.FALSE));
        assertEquals("true", handler.format(Boolean.TRUE));
    }
    
    @Test
    public void testNumericFormat() {
        XmlBooleanTypeHandler handler = new XmlBooleanTypeHandler();
        handler.setNumericFormatEnabled(true);
        assertTrue(handler.isNumericFormatEnabled());
        assertEquals("0", handler.format(Boolean.FALSE));
        assertEquals("1", handler.format(Boolean.TRUE));
    }
}
