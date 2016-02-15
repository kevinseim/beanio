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
package org.beanio.types.xml;

import java.util.*;

import javax.xml.datatype.*;
import javax.xml.namespace.QName;

import org.beanio.types.*;

/**
 * Base class for {@link Calendar} type handlers based on the W3C XML Schema 
 * datatype specification.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public abstract class AbstractXmlCalendarTypeHandler extends CalendarTypeHandler {

    private static final String DATE_TIME = "dateTime";
	private static final String INVALID_XML = "Invalid XML ";
	protected static final DatatypeFactory dataTypeFactory;
    static {
        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("Failed to create DatatypeFactory instance", e);
        } 
    }
    
    private boolean timeZoneAllowed = true;
    private boolean lenientDatatype = false;
    
    /**
     * Constructs a new <tt>AbstractXmlDateTypeHandler</tt>.
     */
    public AbstractXmlCalendarTypeHandler() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#parse(java.lang.String)
     */
    @Override
    public Calendar parse(String text) throws TypeConversionException {
        if ("".equals(text)) {
            return null;
        }
        if (pattern != null) {
            return super.parse(text);
        }
        
        QName type = getDatatypeQName();
        
        try {
            XMLGregorianCalendar xcal = dataTypeFactory.newXMLGregorianCalendar(text);
            if (!lenientDatatype && type != null && !xcal.getXMLSchemaType().equals(type)) {
                throw new TypeConversionException(INVALID_XML + type.getLocalPart());
            }
            
            if (!isTimeZoneAllowed() && xcal.getTimezone() != DatatypeConstants.FIELD_UNDEFINED) {
                String typeName = type == null ? DATE_TIME : type.getLocalPart();
                throw new TypeConversionException(INVALID_XML + typeName + 
                    ", time zone not allowed");
            }
            
            return xcal.toGregorianCalendar();
        }
        catch (IllegalArgumentException ex) {
            String typeName = type == null ? DATE_TIME : type.getLocalPart();
            throw new TypeConversionException(INVALID_XML + typeName);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#format(java.lang.Object)
     */
    @Override
    public abstract String format(Object value);

    /**
     * Returns the expected XML Schema data type when <tt>parse</tt> is called.
     * @return the expected XML schema data type <tt>QName</tt>
     */
    protected abstract QName getDatatypeQName();
    
    /**
     * Creates a new calendar using the configured time zone (if set).
     * @return a new <tt>Calendar</tt> instance
     */
    protected Calendar newCalendar() {
        return timeZone == null ? Calendar.getInstance() : Calendar.getInstance(timeZone);
    }
    
    /**
     * Returns the time zone offset in minutes for the given date, 
     * or {@link DatatypeConstants#FIELD_UNDEFINED} if a time zone was not configured.
     * @param date the date on which to determine the time zone offset
     * @return the time zone offset in minutes, or {@link DatatypeConstants#FIELD_UNDEFINED}
     */
    protected int getTimeZoneOffset(Date date) {
        if (timeZone == null) {
            return DatatypeConstants.FIELD_UNDEFINED;
        }
        else {
            return timeZone.getOffset(date.getTime()) / 60000;
        }
    }

    /**
     * Returns whether time zone information is allowed when parsing field text.  Defaults
     * to <tt>true</tt>.
     * @return <tt>true</tt> if time zone information is allowed when parsing field text
     */
    public boolean isTimeZoneAllowed() {
        return timeZoneAllowed;
    }

    /**
     * Sets whether time zone information is allowed when parsing field text.  Defaults
     * to <tt>true</tt>.
     * @param timeZoneAllowed <tt>true</tt> if time zone information is allowed when
     *   parsing field text
     */
    public void setTimeZoneAllowed(boolean timeZoneAllowed) {
        this.timeZoneAllowed = timeZoneAllowed;
    }

    /**
     * Returns whether data type validation is skipped when parsing field text.  Set to
     * <tt>false</tt> by default, a <tt>TypeConversionException</tt> is thrown when a
     * XML dateTime type handler is used to parse a XML date or XML time, or a XML date
     * handler is used to parse a XML dateTime field, etc.
     * @return <tt>true</tt> if data type validation is skipped
     */
    public boolean isLenientDatatype() {
        return lenientDatatype;
    }

    /**
     * Sets whether data type validation is skipped when parsing field text.  Set to
     * <tt>false</tt> by default, a <tt>TypeConversionException</tt> is thrown when a
     * XML dateTime type handler is used to parse a XML date or XML time, or a XML date
     * handler is used to parse a XML dateTime field, etc.
     * @param lenientDatatype <tt>true</tt> if data type validation is skipped
     */
    public void setLenientDatatype(boolean lenientDatatype) {
        this.lenientDatatype = lenientDatatype;
    }
}
