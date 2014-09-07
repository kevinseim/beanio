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

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

/**
 * JUnit test cases for the <tt>NumberTypeHandler</tt> class.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class NumberTypeHandlerTest {

    @Test(expected=TypeConversionException.class)
    public void testParseInvalid() throws TypeConversionException {
        IntegerTypeHandler handler = new IntegerTypeHandler();
        handler.parse("abc");
    }
    
    @Test(expected=TypeConversionException.class)
    public void testParseInvalidWithPattern() throws TypeConversionException {
        IntegerTypeHandler handler = new IntegerTypeHandler();
        handler.setPattern("0x");
        handler.parse("10");
    }
    
    @Test(expected=TypeConversionException.class)
    public void testParseInvalidIncomplete() throws TypeConversionException {
        IntegerTypeHandler handler = new IntegerTypeHandler();
        handler.setPattern("0");
        handler.parse("10a");
    }
    
    @Test(expected=TypeConversionException.class)
    public void testParseInvalidSize() throws TypeConversionException {
        ByteTypeHandler handler = new ByteTypeHandler();
        handler.setPattern("0");
        handler.parse("1000");
    }

    @Test
    public void testNewInstance() {
        IntegerTypeHandler handler = new IntegerTypeHandler();
        
        Properties props = new Properties();
        assertEquals(handler, handler.newInstance(props));
        props.setProperty(ConfigurableTypeHandler.FORMAT_SETTING, "");
        assertEquals(handler, handler.newInstance(props));
        
        props.setProperty(ConfigurableTypeHandler.FORMAT_SETTING, "0.00");
        IntegerTypeHandler handler2 = (IntegerTypeHandler) handler.newInstance(props);
        assertEquals("0.00", handler2.getPattern());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testInvalidPattern() {
        IntegerTypeHandler handler = new IntegerTypeHandler();
        handler.setPattern("0.00.00");
    }
}
