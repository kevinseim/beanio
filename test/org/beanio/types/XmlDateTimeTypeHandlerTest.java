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

import java.util.*;

import org.beanio.types.xml.XmlDateTimeTypeHandler;
import org.junit.Test;

/**
 * JUnit test cases for the <tt>XmlDateTimeTypeHandler</tt>.
 * 
 * @author Kevin Seim
 * @since 1.1
 * @see XmlDateTimeTypeHandler
 */
public class XmlDateTimeTypeHandlerTest {
    
    @Test
    public void testTime() throws TypeConversionException {
        XmlDateTimeTypeHandler handler = new XmlDateTimeTypeHandler();
        
        Date date = handler.parse("2000-12-02T15:14:13");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(2000, cal.get(Calendar.YEAR));
        assertEquals(11, cal.get(Calendar.MONTH));
        assertEquals(2, cal.get(Calendar.DATE));
        assertEquals(15, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(14, cal.get(Calendar.MINUTE));
        assertEquals(13, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
        
        assertEquals("2000-12-02T15:14:13", handler.format(date));
    }
    
    @Test
    public void testTimeWithMillisecondsAndTimezone() throws TypeConversionException {
        XmlDateTimeTypeHandler handler = new XmlDateTimeTypeHandler();
        handler.setOutputMilliseconds(true);
        handler.setTimeZoneId("GMT+1:00");
        
        Date date = handler.parse("2000-01-31T08:04:03.1234+01:00");
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        cal.setTime(date);
        assertEquals(2000, cal.get(Calendar.YEAR));
        assertEquals(0, cal.get(Calendar.MONTH));
        assertEquals(31, cal.get(Calendar.DATE));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(4, cal.get(Calendar.MINUTE));
        assertEquals(3, cal.get(Calendar.SECOND));
        assertEquals(123, cal.get(Calendar.MILLISECOND));
        
        assertEquals("2000-01-31T08:04:03.123+01:00", handler.format(date));
    }
    
    @Test(expected=TypeConversionException.class)
    public void testInvalidTime() throws TypeConversionException {
        XmlDateTimeTypeHandler handler = new XmlDateTimeTypeHandler();
        handler.parse("01:02:03");
    }
    
    @Test(expected=TypeConversionException.class)
    public void testInvalidTimeWithTimezone() throws TypeConversionException {
        XmlDateTimeTypeHandler handler = new XmlDateTimeTypeHandler();
        handler.setTimeZoneAllowed(false);
        handler.parse("2000-01-31T08:04:03.1234+01:00");
    }
    
    @Test
    public void testDatatypeLenient() throws TypeConversionException {
        XmlDateTimeTypeHandler handler = new XmlDateTimeTypeHandler();
        handler.setLenientDatatype(true);
        assertTrue(handler.isLenientDatatype());
        
        Date date = handler.parse("15:14:13");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(1970, cal.get(Calendar.YEAR));
        assertEquals(0, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DATE));
        assertEquals(15, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(14, cal.get(Calendar.MINUTE));
        assertEquals(13, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
        
        assertEquals("1970-01-01T15:14:13", handler.format(date));
    }
}
