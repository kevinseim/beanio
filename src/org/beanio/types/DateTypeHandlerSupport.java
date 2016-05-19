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

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

/**
 * This abstract type handler uses a <tt>SimpleDateFormat</tt> class to parse and format 
 * <tt>java.util.Date</tt> objects.  If no pattern is set, <tt>DateFormat.getInstance()</tt> 
 * is used to create a default date format.  By default, <tt>lenient</tt> is false.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 * @see Date
 * @see DateFormat
 * @see SimpleDateFormat
 */
public abstract class DateTypeHandlerSupport extends LocaleSupport implements ConfigurableTypeHandler, Cloneable {

    protected String pattern = null;
    protected boolean lenient = false;
    protected TimeZone timeZone = null;
    
    // the same format instance can be reused if this type handler is not shared
    // by multiple unmarshallers/marshallers, this can lead to significant
    // performance improvements when parsing many records
    private transient ThreadLocal<DateFormat> format = new ThreadLocal<>();
    
    /**
     * Constructs a new AbstractDateTypeHandler.
     */
    public DateTypeHandlerSupport() { }

    /**
     * Constructs a new AbstractDateTypeHandler.
     * @param pattern the {@link SimpleDateFormat} pattern
     */
    public DateTypeHandlerSupport(String pattern) {
        this.pattern = pattern;
    }
    
    /**
     * Parses text into a {@link Date}.
     * @param text the text to parse
     * @return the parsed {@link Date}
     * @throws TypeConversionException
     */
    protected Date parseDate(String text) throws TypeConversionException {
        if ("".equals(text))
            return null;

        ParsePosition pp = new ParsePosition(0);
        Date date = getFormat().parse(text, pp);
        if (pp.getErrorIndex() >= 0 || pp.getIndex() != text.length()) {
            throw new TypeConversionException("Invalid date");
        }
        return date;
    }

    /**
     * Converts a {@link Date} to text.
     * @param date the {@link Date} to convert
     * @return the formatted text
     */
    protected String formatDate(Date date) {
        return date == null ? null : getFormat().format(date);
    }
    
    private DateFormat getFormat() {
        return this.format.get() != null ? this.format.get() : createDateFormat();
    }
    
    /**
     * Creates the <tt>DateFormat</tt> to use to parse and format the field value.
     * @return the <tt>DateFormat</tt> for type conversion
     */
    protected DateFormat createDateFormat() {
        if (pattern == null) {
            return createDefaultDateFormat();
        }
        else {
            DateFormat df = new SimpleDateFormat(pattern, locale);
            df.setLenient(lenient);
            if (timeZone != null) {
                df.setTimeZone(timeZone);
            }
            return df;
        }
    }
    
    /**
     * Creates a default date format when no pattern is set.
     * @return the default date format
     */
    protected DateFormat createDefaultDateFormat() {
        return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.types.AbstractDateTypeHandler#newInstance(java.util.Properties)
     */
    public DateTypeHandlerSupport newInstance(Properties properties) throws IllegalArgumentException {
        String pattern = properties.getProperty(FORMAT_SETTING);
        if (pattern == null || "".equals(pattern)) {
            return this;
        }
        if (pattern.equals(getPattern())) {
            return this;
        }

        try {
            DateTypeHandlerSupport handler = (DateTypeHandlerSupport) this.clone();
            handler.setPattern(pattern);
            handler.lenient = this.lenient;
            handler.timeZone = this.timeZone;
            handler.format = new ThreadLocal<>();
            handler.format.set(handler.createDateFormat());
            return handler;
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the date pattern used by the <tt>SimpleDateFormat</tt>.
     * @return the date pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets the date pattern used by the <tt>SimpleDateFormat</tt>.
     * @param pattern the date pattern
     * @throws IllegalArgumentException if the date pattern is invalid
     */
    public void setPattern(String pattern) throws IllegalArgumentException {
        // validate the pattern
        try {
            if (pattern != null) {
                new SimpleDateFormat(pattern);
            }
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid date format pattern '" + pattern + "': " + ex.getMessage());
        }
        
        this.pattern = pattern;
    }
    
    /**
     * Sets the time zone for interpreting dates.  If not set, the system default 
     * time zone is used.
     * @param name the time zone ID
     * @see TimeZone
     */
    public void setTimeZoneId(String name) {
        if (name == null || "".equals(name)) {
            timeZone = null;
        }
        else {
            timeZone = TimeZone.getTimeZone(name);
        }
    }
    
    /**
     * Returns the time zone used to interpret dates, or <tt>null</tt> if the default
     * time zone will be used.
     * @return the time zone ID
     * @see TimeZone
     */
    public String getTimeZoneId() {
        return timeZone == null ? null : timeZone.getID();
    }
    
    /**
     * Returns the configured {@link TimeZone} or null if not set.
     * @return the {@link TimeZone}
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Returns whether the <tt>SimpleDateFormat</tt> is lenient.
     * @return <tt>true</tt> if lenient, <tt>false</tt> otherwise
     */
    public boolean isLenient() {
        return lenient;
    }

    /**
     * Sets whether the <tt>SimpleDateFormat</tt> is lenient.
     * @param lenient <tt>true</tt> if lenient, <tt>false</tt> otherwise
     */
    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }
}
