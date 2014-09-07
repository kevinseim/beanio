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
package org.beanio.parser.xml.groups;

import static org.junit.Assert.*;

import java.io.*;

import org.beanio.*;
import org.beanio.parser.xml.*;
import org.junit.*;

/**
 * JUnit test cases related to reading and writing beans in an XML formatted stream. 
 * @author Kevin Seim
 * @since 1.1
 */
public class XmlGroupsTest extends XmlParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("groups_mapping.xml");
    }
    
    /**
     * Test XML groups.
     */
    @Test
    public void testNestedGroups() throws Exception {
        BeanReader in = factory.createReader("stream", new InputStreamReader(
            getClass().getResourceAsStream("g1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            assertEquals("C", person.getType());
            out.write(person);
            
            Address address = (Address) in.read();
            assertEquals("IL", address.getState());
            out.write(address);
            
            person = (Person) in.read();
            assertEquals("David", person.getFirstName());
            assertEquals("P", person.getType());
            out.write(person);
            
            person = (Person) in.read();
            assertEquals("Mary", person.getFirstName());
            assertEquals("P", person.getType());
            out.write(person);
            
            person = (Person) in.read();
            assertEquals("George1", person.getFirstName());
            out.write(person);
            
            person = (Person) in.read();
            assertEquals("George2", person.getFirstName());
            out.write(person);
            
            address = (Address) in.read();
            assertEquals("IL", address.getState());
            out.write(address);
            
            person = (Person) in.read();
            assertEquals("Kevin", person.getFirstName());
            assertEquals("F", person.getType());
            out.write(person);
            
            person = (Person) in.read();
            assertEquals("Jen", person.getFirstName());
            assertEquals("F", person.getType());
            out.write(person);
            
            out.close();
            assertEquals(load("g1_in.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test XML groups where <tt>xmlType="none"</tt>.
     */
    @Test
    public void testGroupXmlTypeNone() throws Exception {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("g2_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream2", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            assertEquals("C", person.getType());
            out.write(person);
            
            Address address = (Address) in.read();
            assertEquals("IL", address.getState());
            out.write(address);
            
            person = (Person) in.read();
            assertEquals("George", person.getFirstName());
            assertEquals("F", person.getType());
            out.write(person);

            person = (Person) in.read();
            assertEquals("Jane", person.getFirstName());
            assertEquals("F", person.getType());
            out.write(person);
            
            out.close();
            assertEquals(load("g2_in.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test an XML stream where <tt>xmlType="none"</tt>.
     */
    @Test
    public void testStreamXmlTypeNone() throws Exception {
        BeanReader in = factory.createReader("stream3", new InputStreamReader(
            getClass().getResourceAsStream("g3_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream3", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            Address address = person.getAddress();
            assertNotNull(address);
            assertEquals("IL", address.getState());
            out.write(person);
            
            out.close();
            assertEquals(load("g3_in.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
}
