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

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

/**
 * JUnit test cases for the <tt>DateTypeHandler</tt> class.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class DateTypeHandlerTest {

    @Test
    public void testLenient() throws TypeConversionException {
        DateTypeHandler handler = new DateTypeHandler();
        handler.setLenient(true);
        assertTrue(handler.isLenient());
        
        String pattern = "MM-dd-yyyy";
        handler.setPattern(pattern);
        assertEquals(pattern, handler.getPattern());
        
        Date date = handler.parse("01-32-2000");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(1, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DATE));
        assertEquals(2000, cal.get(Calendar.YEAR));
    }
    
    @Test(expected=TypeConversionException.class)
    public void testParsePositionPastDate() throws TypeConversionException {
        DateTypeHandler handler = new DateTypeHandler();
        handler.setLenient(false);
        handler.setPattern("MM-dd-yyyy");
        handler.parse("01-01-2000abc");
    }
    
    @Test(expected=TypeConversionException.class)
    public void testParsePosition() throws TypeConversionException {
        DateTypeHandler handler = new DateTypeHandler();
        handler.setLenient(false);
        handler.setPattern("MM-dd-yyyy");
        handler.parse("01-32-2000");
    }
    
    @Test
    public void testNewInstance() {
        DateTypeHandler handler = new DateTypeHandler();
        handler.setLenient(true);
        
        Properties props = new Properties();
        assertEquals(handler, handler.newInstance(props));
        props.setProperty(ConfigurableTypeHandler.FORMAT_SETTING, "");
        assertEquals(handler, handler.newInstance(props));
        
        props.setProperty(ConfigurableTypeHandler.FORMAT_SETTING, "yyyy-MM-dd");
        DateTypeHandler handler2 = (DateTypeHandler) handler.newInstance(props);
        assertEquals("yyyy-MM-dd", handler2.getPattern());
        assertEquals(handler.isLenient(), handler2.isLenient());
        
        handler.setPattern("yyyy-MM-dd");
        assertEquals(handler, handler.newInstance(props));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testInvalidPattern() {
        DateTypeHandler handler = new DateTypeHandler();
        handler.setPattern("xxx");
    }
}
