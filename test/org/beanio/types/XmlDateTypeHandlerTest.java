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

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.*;

import org.beanio.types.xml.XmlDateTypeHandler;
import org.junit.Test;

/**
 * JUnit test cases for the <tt>XmlDateTypeHandler</tt>.
 * 
 * @author Kevin Seim
 * @since 1.1
 * @see XmlDateTypeHandler
 */
public class XmlDateTypeHandlerTest {
    
    @Test
    public void testDate() throws TypeConversionException {
        XmlDateTypeHandler handler = new XmlDateTypeHandler();
        
        Date date = handler.parse("2000-01-01");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(0, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DATE));
        assertEquals(2000, cal.get(Calendar.YEAR));
        
        assertEquals("2000-01-01", handler.format(date));
    }
    
    @Test
    public void testDateWithTimezone() throws TypeConversionException {
        TimeZone tz = TimeZone.getTimeZone("GMT-1:00");
        
        XmlDateTypeHandler handler = new XmlDateTypeHandler();
        handler.setTimeZoneId("GMT-1:00");
        
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(tz);
        
        Date date = handler.parse("2000-01-01-01:00");
        assertEquals("2000-01-01 00:00", sdf.format(date));
        
        assertEquals("2000-01-01-01:00", handler.format(date));
    }
    
    @Test(expected=TypeConversionException.class)
    public void testInvalidDate1() throws TypeConversionException {
        XmlDateTypeHandler handler = new XmlDateTypeHandler();
        handler.parse("2000-01-01T10:00:00");
    }
    
    @Test(expected=TypeConversionException.class)
    public void testInvalidDate2() throws TypeConversionException {
        XmlDateTypeHandler handler = new XmlDateTypeHandler();
        handler.parse("2000-02-30");
    }
}
