/*
 * Copyright 2013 Kevin Seim
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

import java.text.*;
import java.util.*;

/**
 * This type handler uses a {@link SimpleDateFormat} to parse and format 
 * <tt>java.util.Calendar</tt> objects.  If no pattern is set, <tt>DateFormat.getInstance()</tt> 
 * is used to create a default date format.  By default, <tt>lenient</tt> is false.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 * @see Date
 * @see DateFormat
 * @see SimpleDateFormat
 */
public class CalendarTypeHandler extends DateTypeHandlerSupport {

    /**
     * Constructs a new CalendarTypeHandler.
     */
    public CalendarTypeHandler() {
        super();
    }

    /**
     * Constructs a new CalendarTypeHandler.
     * @param pattern the {@link SimpleDateFormat} pattern 
     */
    public CalendarTypeHandler(String pattern) {
        super(pattern);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#parse(java.lang.String)
     */
    @Override
    public Calendar parse(String text) throws TypeConversionException {
        Date date = parseDate(text);
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.types.AbstractDateTypeHandler#format(java.lang.Object)
     */
    @Override
    public String format(Object value) {
        return formatCalendar((Calendar)value);
    }
    
    protected String formatCalendar(Calendar calendar) {
        return formatDate(calendar != null ? calendar.getTime() : null);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#getType()
     */
    @Override
    public Class<?> getType() {
        return Calendar.class;
    }
}
