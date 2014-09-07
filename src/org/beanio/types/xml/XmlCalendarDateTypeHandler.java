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

/**
 * A {@link Calendar} type handler implementation for parsing dates based on
 * the W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#date">date datatype</a>
 * specification.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public class XmlCalendarDateTypeHandler extends AbstractXmlCalendarTypeHandler {

    /**
     * Constructs a new <tt>XmlCalendarDateTypeHandler</tt>.
     */
    public XmlCalendarDateTypeHandler() { }
    
    @Override
    public String format(Object value) {
        if (value == null) {
            return null;
        }
        
        Calendar cal = (Calendar) value;
        if (pattern != null) {
            return super.formatCalendar(cal);
        }
        
        XMLGregorianCalendar xcal = dataTypeFactory.newXMLGregorianCalendarDate(
            cal.get(Calendar.YEAR), 
            cal.get(Calendar.MONTH) + 1, 
            cal.get(Calendar.DATE), 
            getTimeZoneOffset(cal.getTime()));
        
        return xcal.toXMLFormat();
    }
    
    @Override
    protected QName getDatatypeQName() {
        return DatatypeConstants.DATE;
    }
}
