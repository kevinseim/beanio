package org.beanio.types;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.beanio.types.xml.XmlCalendarDateTypeHandler;
import org.junit.Test;

/**
 * JUnit test cases for the {@link XmlCalendarDateTypeHandler}
 */
public class XmlCalendarDateTypeHandlerTest {
    
    @Test
    public void testDate() throws TypeConversionException {
        XmlCalendarDateTypeHandler handler = new XmlCalendarDateTypeHandler();
        
        Calendar cal = handler.parse("2000-01-01");
        assertEquals(0, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DATE));
        assertEquals(2000, cal.get(Calendar.YEAR));
        
        assertEquals("2000-01-01", handler.format(cal));
    }
    
    @Test
    public void testDateWithTimezone() throws TypeConversionException {
        TimeZone tz = TimeZone.getTimeZone("GMT-1:00");
        
        XmlCalendarDateTypeHandler handler = new XmlCalendarDateTypeHandler();
        handler.setTimeZoneId("GMT-1:00");
        
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(tz);
        
        Calendar cal = handler.parse("2000-01-01-01:00");
        assertEquals("2000-01-01 00:00", sdf.format(cal.getTime()));
        assertEquals("2000-01-01-01:00", handler.format(cal));
    }
    
    @Test(expected=TypeConversionException.class)
    public void testInvalidDate1() throws TypeConversionException {
        XmlCalendarDateTypeHandler handler = new XmlCalendarDateTypeHandler();
        handler.parse("2000-01-01T10:00:00");
    }
    
    @Test(expected=TypeConversionException.class)
    public void testInvalidDate2() throws TypeConversionException {
        XmlCalendarDateTypeHandler handler = new XmlCalendarDateTypeHandler();
        handler.parse("2000-02-30");
    }
}
