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
package org.beanio.parser.xml.typehandler;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.beanio.*;
import org.beanio.parser.xml.XmlParserTest;
import org.junit.*;

/**
 * JUnit test cases related to reading and writing XML using XML specific type handlers. 
 * @author Kevin Seim
 * @since 1.1
 */
public class XmlTypeHandlerTest extends XmlParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("typehandler_mapping.xml");
    }
    
    /**
     * Test XML specific type handlers.
     */
    @Test
    @SuppressWarnings("rawtypes")
    public void testFieldTypesAndNillable() throws Exception {
        BeanReader in = factory.createReader("stream", new InputStreamReader(
            getClass().getResourceAsStream("th1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream", s);
        try {
            Map map = (Map) in.read();
            assertEquals("2011-01-01", new SimpleDateFormat("yyyy-MM-dd").format(map.get("date")));
            assertEquals("2011-01-01T13:45:00", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(map.get("datetime")));
            assertEquals("11:12:13", new SimpleDateFormat("hh:mm:ss").format(map.get("time")));
            assertEquals("2011-01-01", new SimpleDateFormat("yyyy-MM-dd").format(map.get("customdate")));
            assertEquals(Boolean.TRUE, map.get("boolean"));
            
            out.write(map);
            out.close();
            assertEquals(load("th1_in.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
}
