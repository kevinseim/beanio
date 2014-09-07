package org.beanio.types;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.beanio.types.xml.XmlCalendarTimeTypeHandler;
import org.junit.Test;

/**
 * JUnit test cases for the XmlCalendarTimeTypeHandler.
 */
public class XmlCalendarTimeTypeHandlerTest {
    
    @Test
    public void testTime() throws TypeConversionException {
        XmlCalendarTimeTypeHandler handler = new XmlCalendarTimeTypeHandler();
        
        Calendar cal = handler.parse("15:14:13");
        assertEquals(15, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(14, cal.get(Calendar.MINUTE));
        assertEquals(13, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
        
        assertEquals("15:14:13", handler.format(cal));
    }
    
    @Test
    public void testTimeWithMilliseconds() throws TypeConversionException {
        XmlCalendarTimeTypeHandler handler = new XmlCalendarTimeTypeHandler();
        handler.setOutputMilliseconds(true);
        
        Calendar cal = handler.parse("08:04:03.1236");
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(4, cal.get(Calendar.MINUTE));
        assertEquals(3, cal.get(Calendar.SECOND));
        assertEquals(123, cal.get(Calendar.MILLISECOND));
        
        assertEquals("08:04:03.123", handler.format(cal));
    }
    
    @Test
    public void testTimeWithTimezone() throws TypeConversionException {
        XmlCalendarTimeTypeHandler handler = new XmlCalendarTimeTypeHandler();
        handler.setTimeZoneId("GMT+1:00");
        
        Calendar cal = handler.parse("23:04:03+01:00");
        assertEquals(23, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(4, cal.get(Calendar.MINUTE));
        assertEquals(3, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
        
        assertEquals("23:04:03+01:00", handler.format(cal));
    }

    @Test(expected=TypeConversionException.class)
    public void testInvalidTime() throws TypeConversionException {
        XmlCalendarTimeTypeHandler handler = new XmlCalendarTimeTypeHandler();
        handler.parse("23:62:03+01:00");
    }
    
    @Test(expected=TypeConversionException.class)
    public void testInvalidTimeWithTimezone() throws TypeConversionException {
        XmlCalendarTimeTypeHandler handler = new XmlCalendarTimeTypeHandler();
        handler.setTimeZoneAllowed(false);
        handler.parse("23:04:03+01:00");
    }
}
