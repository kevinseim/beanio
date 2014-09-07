/*
 * Copyright 2011 Kevin Seim
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
package org.beanio.parser.xml.collection;

import static org.junit.Assert.*;

import java.io.*;
import java.util.List;

import org.beanio.*;
import org.beanio.parser.xml.*;
import org.junit.*;

/**
 * JUnit test cases related to reading and writing collections in an XML formatted stream. 
 * @author Kevin Seim
 * @since 1.1
 */
public class XmlCollectionTest extends XmlParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("collection_mapping.xml");
    }
    
    /**
     * Test an XML field collection.
     */
    @Test
    public void testFieldCollection() throws Exception {
        BeanReader in = factory.createReader("stream", new InputStreamReader(
            getClass().getResourceAsStream("c1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            List<String> list = person.getColor();
            assertEquals(3, list.size());
            assertEquals("Red", list.get(0));
            assertEquals("Blue", list.get(1));
            assertEquals("Green", list.get(2));
            out.write(person);
            
            person = (Person) in.read();
            assertEquals("George", person.getFirstName());
            assertEquals(0, person.getColor().size());
            out.write(person);
            
            out.close();
            assertEquals(load("c1_in.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
}
