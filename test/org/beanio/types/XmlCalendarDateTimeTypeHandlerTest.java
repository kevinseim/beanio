package org.beanio.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.beanio.types.xml.XmlCalendarDateTimeTypeHandler;
import org.junit.Test;

/**
 * JUnit test cases for the {@link XmlCalendarDateTimeTypeHandler}.
 */
public class XmlCalendarDateTimeTypeHandlerTest {
    
    @Test
    public void testTime() throws TypeConversionException {
        XmlCalendarDateTimeTypeHandler handler = new XmlCalendarDateTimeTypeHandler();
        
        Calendar cal = handler.parse("2000-12-02T15:14:13");
        assertEquals(2000, cal.get(Calendar.YEAR));
        assertEquals(11, cal.get(Calendar.MONTH));
        assertEquals(2, cal.get(Calendar.DATE));
        assertEquals(15, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(14, cal.get(Calendar.MINUTE));
        assertEquals(13, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
        
        assertEquals("2000-12-02T15:14:13", handler.format(cal));
    }
    
    @Test
    public void testTimeWithMillisecondsAndTimezone() throws TypeConversionException {
        XmlCalendarDateTimeTypeHandler handler = new XmlCalendarDateTimeTypeHandler();
        handler.setOutputMilliseconds(true);
        handler.setTimeZoneId("GMT+1:00");
        
        Calendar cal = handler.parse("2000-01-31T08:04:03.1234+01:00");
        assertEquals(2000, cal.get(Calendar.YEAR));
        assertEquals(0, cal.get(Calendar.MONTH));
        assertEquals(31, cal.get(Calendar.DATE));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(4, cal.get(Calendar.MINUTE));
        assertEquals(3, cal.get(Calendar.SECOND));
        assertEquals(123, cal.get(Calendar.MILLISECOND));
        
        assertEquals("2000-01-31T08:04:03.123+01:00", handler.format(cal));
    }
    
    @Test(expected=TypeConversionException.class)
    public void testInvalidTime() throws TypeConversionException {
        XmlCalendarDateTimeTypeHandler handler = new XmlCalendarDateTimeTypeHandler();
        handler.parse("01:02:03");
    }
    
    @Test(expected=TypeConversionException.class)
    public void testInvalidTimeWithTimezone() throws TypeConversionException {
        XmlCalendarDateTimeTypeHandler handler = new XmlCalendarDateTimeTypeHandler();
        handler.setTimeZoneAllowed(false);
        handler.parse("2000-01-31T08:04:03.1234+01:00");
    }
    
    @Test
    public void testDatatypeLenient() throws TypeConversionException {
        XmlCalendarDateTimeTypeHandler handler = new XmlCalendarDateTimeTypeHandler();
        handler.setLenientDatatype(true);
        assertTrue(handler.isLenientDatatype());
        
        Calendar cal = handler.parse("15:14:13");
        assertEquals(1970, cal.get(Calendar.YEAR));
        assertEquals(0, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DATE));
        assertEquals(15, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(14, cal.get(Calendar.MINUTE));
        assertEquals(13, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
        
        assertEquals("1970-01-01T15:14:13", handler.format(cal));
    }
}
