/*
 * Copyright 2011-2012 Kevin Seim
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
package org.beanio.parser.misc;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for miscellaneous mapping related conditions.
 * 
 * @author Kevin Seim
 * @since 1.2.1
 */
public class MiscParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("misc_mapping.xml");
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testRecordWithoutFields_ClassNotSet() {
        BeanReader in = factory.createReader("stream1", new InputStreamReader(
            getClass().getResourceAsStream("m1.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals("Joe", map.get("field1"));
            
            StringWriter text = new StringWriter();
            factory.createWriter("stream1", text).write(map);
            assertEquals("Joe,Smith" + lineSeparator, text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testRecordWithoutFields_ClassSet() {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("m1.txt")));
        
        try {
            StringWriter text = new StringWriter();
            BeanWriter out = factory.createWriter("stream2", text);
            
            Map map = (Map) in.read();
            assertTrue(map.isEmpty());
            out.write(map);
            
            map = (Map) in.read();
            assertEquals("Joe", map.get("field1"));
            out.write(map);
            
            /* 
             * this is currently not supported for flat file
             * types which do not maintain state when writing
             * to an output stream
             *  
            assertEquals(lineSeparator + 
                "Joe,Smith" + lineSeparator, text.toString());
            */
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testRecordWithPropertyOnly_ClassSet() {
        BeanReader in = factory.createReader("stream3", new InputStreamReader(
            getClass().getResourceAsStream("m1.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals("value", map.get("key"));
            
            map = (Map) in.read();
            assertEquals("Joe", map.get("field1"));
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testTypeValidation_ClassNotSet() {
        BeanReader in = factory.createReader("stream4", new InputStreamReader(
            getClass().getResourceAsStream("m1.txt")));
        
        try {
            assertFieldError(in, 1, "header", "field1", "FirstName", "Type conversion error: Invalid date");
            
            Map map = (Map) in.read();
            assertEquals("Joe", map.get("field1"));
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testRecordWithPropertyOnly_ClassNotSet() {
        BeanReader in = factory.createReader("stream5", new InputStreamReader(
            getClass().getResourceAsStream("m1.txt")));
        
        try {
            Map map = (Map) in.read();
            assertNull(map.get("bean1"));
            
            map = (Map) map.get("bean");
            assertEquals("value", map.get("key"));
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testMarshalStaticRecord() {
        StringWriter text = new StringWriter();
        
        BeanWriter out = factory.createWriter("stream6", text);
        out.write("header", null);
        
        Map map = new HashMap();
        map.put("d1", "value1");
        
        out.write(map);
        out.flush();
        
        assertEquals(
            "Header1,Header2,Header3" + lineSeparator +
            "value1,," + lineSeparator, text.toString());
        
        Marshaller m = factory.createMarshaller("stream6");
        assertEquals("Header1,Header2,Header3", m.marshal("header", null).toString());
    }
}
