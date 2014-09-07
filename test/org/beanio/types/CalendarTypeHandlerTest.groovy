package org.beanio.types

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.junit.Test;

/**
 * JUnit test cases for the {@link CalendarTypeHandler}.
 * @author Kevin Seim
 */
class CalendarTypeHandlerTest {

    @Test
    void testLenient() throws TypeConversionException {
        CalendarTypeHandler handler = new CalendarTypeHandler();
        handler.setLenient(true);
        assert handler.isLenient()
        
        String pattern = "MM-dd-yyyy";
        handler.setPattern(pattern);
        assert handler.getPattern() == pattern
        
        Calendar cal = handler.parse("01-32-2000");
        assert cal.get(Calendar.MONTH) == 1
        assert cal.get(Calendar.DATE) == 1
        assert cal.get(Calendar.YEAR) == 2000
    }
    
    @Test(expected=TypeConversionException.class)
    void testParsePositionPastDate() throws TypeConversionException {
        CalendarTypeHandler handler = new CalendarTypeHandler();
        handler.setLenient(false);
        handler.setPattern("MM-dd-yyyy");
        handler.parse("01-01-2000abc");
    }
    
    @Test(expected=TypeConversionException.class)
    void testParsePosition() throws TypeConversionException {
        CalendarTypeHandler handler = new CalendarTypeHandler();
        handler.setLenient(false);
        handler.setPattern("MM-dd-yyyy");
        handler.parse("01-32-2000");
    }
    
    @Test
    void testNewInstance() {
        CalendarTypeHandler handler = new CalendarTypeHandler();
        handler.setLenient(true);
        
        Properties props = new Properties();
        assert handler.is(handler.newInstance(props))
        props.setProperty(ConfigurableTypeHandler.FORMAT_SETTING, "");
        assert handler.is(handler.newInstance(props))
        
        props.setProperty(ConfigurableTypeHandler.FORMAT_SETTING, "yyyy-MM-dd");
        CalendarTypeHandler handler2 = handler.newInstance(props);
        assert handler2.getPattern() == "yyyy-MM-dd"
        assert handler2.isLenient() == handler.isLenient()
        
        handler.setPattern("yyyy-MM-dd");
        assert handler.is(handler.newInstance(props))
    }
    
    @Test(expected=IllegalArgumentException.class)
    void testInvalidPattern() {
        CalendarTypeHandler handler = new CalendarTypeHandler();
        handler.setPattern("xxx");
    }
}
