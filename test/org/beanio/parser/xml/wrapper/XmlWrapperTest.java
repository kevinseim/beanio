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
package org.beanio.parser.xml.wrapper;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.beanio.*;
import org.beanio.parser.xml.*;
import org.junit.*;

/**
 * JUnit test cases for testing XML wrapper elements.
 * @author Kevin Seim
 * @since 1.1
 */
public class XmlWrapperTest extends XmlParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("wrapper_mapping.xml");
    }
    
    /**
     * Test a xmlWrapper configuration for various field types.
     */
    @Test
    public void testFieldCollection_NotNillable() throws Exception {
        BeanReader in = factory.createReader("stream", new InputStreamReader(
            getClass().getResourceAsStream("w1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            assertEquals("Smith", person.getLastName());
            List<String> list = person.getColor();
            assertEquals(3, list.size());
            assertEquals("Red", list.get(0));
            assertEquals("Blue", list.get(1));
            assertEquals("Green", list.get(2));
            List<Address> addressList = person.getAddressList();
            assertNull(addressList);
            //assertEquals(0, addressList.size());
            out.write(person);
            
            person = (Person) in.read();
            assertEquals("", person.getFirstName());
            assertSame(Person.DEFAULT_NAME, person.getLastName());
            assertEquals(0, person.getColor().size());
            addressList = person.getAddressList();
            assertEquals(2, addressList.size());
            assertEquals("CO", addressList.get(0).getState());
            assertEquals("IL", addressList.get(1).getState());
            person.setLastName(null);
            out.write(person);
            
            out.close();
            assertEquals(load("w1_in.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test a xmlWrapper configuration for a nillable field collection.
     */
    @Test
    public void testFieldCollection_Nillable() throws Exception {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("w2_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream2", s);
        try {
            Person person = (Person) in.read();
            List<String> list = person.getColor();
            assertEquals(2, list.size());
            assertEquals("Red", list.get(0));
            assertEquals("Blue", list.get(1));
            out.write(person);
            
            person = (Person) in.read();
            assertNull(person.getColor());
            //assertNotNull(person.getColor());
            //assertEquals(0, person.getColor().size());
            out.write(person);

            out.close();
            assertEquals(load("w2_in.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test a xmlWrapper configuration for a field collection where min occurs is one.
     */
    @Test
    public void testFieldCollection_MinOccursOne() throws Exception {
        BeanReader in = factory.createReader("stream3", new InputStreamReader(
            getClass().getResourceAsStream("w3_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream3", s);
        try {
            Person person = (Person) in.read();
            List<String> list = person.getColor();
            assertEquals(1, list.size());
            assertEquals("", list.get(0));
            out.write(person);
            
            person.setColor(new ArrayList<String>());
            out.write(person);

            out.close();
            assertEquals(load("w3_in.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
}
