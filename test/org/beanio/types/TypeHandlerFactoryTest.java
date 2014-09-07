/*
 * Copyright 2010-2011 Kevin Seim
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
import static org.junit.Assert.assertNull;

import java.text.DateFormat;
import java.util.*;

import org.beanio.internal.util.TypeHandlerFactory;
import org.junit.Test;

/**
 * JUnit test cases for the <tt>TypeHandlerFactory</tt> class.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class TypeHandlerFactoryTest {

    @Test
    public void testGetHandler() {
        TypeHandlerFactory factory = new TypeHandlerFactory();
        assertNull(factory.getTypeHandlerFor(getClass()));
        assertNull(factory.getTypeHandlerFor("invalid_alias"));
        assertNull(factory.getTypeHandler("invalid_name"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testFormatNotSupported() {
        Properties props = new Properties();
        props.setProperty(ConfigurableTypeHandler.FORMAT_SETTING, "yyyy-MM-dd");
        
        TypeHandlerFactory factory = new TypeHandlerFactory();
        factory.getTypeHandlerFor(Character.class, null, props);
    }
    
    @Test(expected=NullPointerException.class)
    public void testRegisterWithNullName() {
        new TypeHandlerFactory().registerHandler(null, new IntegerTypeHandler());
    }

    @Test(expected=NullPointerException.class)
    public void testRegisterWithNullHandler() {
        new TypeHandlerFactory().registerHandler("name", null);
    }

    @Test(expected=NullPointerException.class)
    public void testGetHandlerWithNullName() {
        new TypeHandlerFactory().getTypeHandler((String) null);
    }
    
    @Test(expected=NullPointerException.class)
    public void testRegisterWithNullType() {
        new TypeHandlerFactory().registerHandlerFor((String)null, new IntegerTypeHandler());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRegisterWithInvalidType() {
        new TypeHandlerFactory().registerHandlerFor("invalid_type_alias", new IntegerTypeHandler());
    }
    
    @Test(expected=NullPointerException.class)
    public void testGetHandlerWithNullType() {
        new TypeHandlerFactory().getTypeHandlerFor((String) null);
    }
    
    @Test(expected=NullPointerException.class)
    public void testRegisterWithNullClass() {
        new TypeHandlerFactory().registerHandlerFor((Class<?>)null, new IntegerTypeHandler());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRegisterHandlerForWrongClass() {
        new TypeHandlerFactory().registerHandlerFor(Integer.class, new ByteTypeHandler());
    }
    
    @Test(expected=NullPointerException.class)
    public void testGetHandlerWithNullClass() {
        new TypeHandlerFactory().getTypeHandlerFor((Class<?>) null);
    }
    
    @Test
    public void testDateTypeHandlers() {
        TypeHandlerFactory factory = new TypeHandlerFactory();
        
        Date now = new Date();
        DateTypeHandler handler = (DateTypeHandler) factory.getTypeHandlerFor("date");
        assertEquals(DateFormat.getDateInstance().format(now), handler.format(now));
        handler = (DateTypeHandler) factory.getTypeHandlerFor("time");
        assertEquals(DateFormat.getTimeInstance().format(now), handler.format(now));
        handler = (DateTypeHandler) factory.getTypeHandlerFor("datetime");
        assertEquals(DateFormat.getDateTimeInstance().format(now), handler.format(now));
        
        DateTypeHandler dateHandler = new DateTypeHandler();
        dateHandler.setPattern("MMddyyyy");
        DateTypeHandler datetimeHandler = new DateTypeHandler();
        datetimeHandler.setPattern("MMddyyyy HH:mm");        
        DateTypeHandler timeHandler = new DateTypeHandler();
        datetimeHandler.setPattern("HH:mm");  
        
        factory.registerHandlerFor("datetime", datetimeHandler);
        factory.registerHandlerFor("date", dateHandler);
        factory.registerHandlerFor("time", timeHandler);
        
        assertEquals(datetimeHandler, factory.getTypeHandlerFor("java.util.Date"));
        assertEquals(datetimeHandler, factory.getTypeHandlerFor(Date.class));
        assertEquals(dateHandler, factory.getTypeHandlerFor("DATE"));
        assertEquals(timeHandler, factory.getTypeHandlerFor("TIME"));
        
        Properties props = new Properties();
        props.setProperty(ConfigurableTypeHandler.FORMAT_SETTING, "yyyy-MM-dd");
        DateTypeHandler th = (DateTypeHandler) factory.getTypeHandlerFor("date", null, props);
        assertEquals("yyyy-MM-dd", th.getPattern());
    }
}
