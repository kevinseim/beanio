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
package org.beanio.parser.property;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for parsing bean property configurations.
 * @author Kevin Seim
 * @since 1.2
 */
public class PropertyParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("property_mapping.xml");
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testBasic() {
        BeanReader in = factory.createReader("p1", new InputStreamReader(
            getClass().getResourceAsStream("p1.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals(Integer.valueOf(1), map.get("id"));
            assertNull(map.get("recordType"));
            
            StringWriter text = new StringWriter();
            factory.createWriter("p1", text).write(map);
            assertEquals("Header,2011-07-04" + lineSeparator, text.toString());
            
            User user = (User) in.read();
            assertEquals(2, user.getType());
            
            text = new StringWriter();
            factory.createWriter("p1", text).write(user);
            assertEquals("Detail,John" + lineSeparator, text.toString());
            
            map = (Map) in.read();
            assertEquals(Integer.valueOf(3), map.get("id"));
            assertEquals(Integer.valueOf(1), map.get("recordCount"));
            assertNull(map.get("recordType"));
            
            text = new StringWriter();
            factory.createWriter("p1", text).write(map);
            assertEquals("Trailer,1" + lineSeparator, text.toString());
        }
        finally {
            in.close();
        }
    }
}
