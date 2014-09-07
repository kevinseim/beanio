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
package org.beanio.parser.xml.types;

import static org.junit.Assert.*;

import java.io.*;

import org.beanio.*;
import org.beanio.parser.xml.*;
import org.junit.*;

/**
 * JUnit test cases related to reading and writing the different XML types. 
 * @author Kevin Seim
 * @since 1.1
 */
public class XmlTypesTest extends XmlParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("types_mapping.xml");
    }
    
    /**
     * Test the various field XML types.
     */
    @Test
    public void testFieldTypesAndNillable() throws Exception {
        BeanReader in = factory.createReader("stream", new InputStreamReader(
            getClass().getResourceAsStream("t1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            assertEquals("Smith", person.getLastName());
            assertEquals("M", person.getGender());
            out.write(person);
            
            person = (Person) in.read();
            assertNull(person.getFirstName());
            assertEquals("Smith", person.getLastName());
            assertEquals("F", person.getGender());
            out.write(person);
            
            person = new Person();
            person.setFirstName(null);
            person.setLastName(null);
            person.setGender("M");
            out.write(person);
            
            out.close();
            assertEquals(load("t1_out.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test the attribute field types with namespaces.  Note that attribute order is not
     * guaranteed so the output comparison may fail... (need to improve). 
     */
    @Test
    public void testAttributeFieldTypes() throws Exception {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("t2_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream2", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            assertEquals("Smith", person.getLastName());
            assertEquals("M", person.getGender());
            out.write(person);
            
            person = (Person) in.read();
            assertEquals("Mary", person.getFirstName());
            assertEquals("Smith", person.getLastName());
            assertEquals("F", person.getGender());            
            out.write(person);
            
            person = (Person) in.read();
            assertNull(person.getFirstName());
            assertEquals("", person.getLastName());
            assertNull(person.getGender());          
            person.setLastName(null);
            out.write(person);
            
            out.close();
            
            assertEquals(load("t2_out.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test the text field types.
     */
    @Test
    public void testTextFieldTypes() throws Exception {
        BeanReader in = factory.createReader("stream3", new InputStreamReader(
            getClass().getResourceAsStream("t3_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream3", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            assertEquals("M", person.getGender());
            out.write(person);
            
            person = (Person) in.read();
            assertEquals("Mary", person.getFirstName());
            assertEquals("F", person.getGender());            
            out.write(person);
            
            out.close();
            
            assertEquals(load("t3_out.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test record identification by XML attribute.
     */
    @Test
    public void testRecordIdentificationByAttribute() throws Exception {
        BeanReader in = factory.createReader("stream4", new InputStreamReader(
            getClass().getResourceAsStream("t3_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream4", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            assertEquals("M", person.getGender());
            if (!(person instanceof Male)) {
                fail("Expected 'Male' type bean.");
            }
            out.write(person);
            
            person = (Person) in.read();
            assertEquals("Mary", person.getFirstName());
            assertEquals("F", person.getGender());     
            if (person instanceof Male) {
                fail("Expected 'Person' type bean.");
            }
            out.write(person);
            
            out.close();
            
            assertEquals(load("t3_out.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test record identification by XML text.
     */
    @Test
    public void testRecordIdentificationByText() throws Exception {
        BeanReader in = factory.createReader("stream5", new InputStreamReader(
            getClass().getResourceAsStream("t5_in.xml")));
        
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            assertEquals("M", person.getGender());
            if (!(person instanceof Male)) {
                fail("Expected 'Male' type bean.");
            }
            
            person = (Person) in.read();
            assertEquals("Mary", person.getFirstName());
            assertEquals("F", person.getGender());            
            if (person instanceof Male) {
                fail("Expected 'Person' type bean.");
            }
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test record identification by XML element.
     */
    @Test
    public void testRecordIdentificationByElement() throws Exception {
        BeanReader in = factory.createReader("stream6", new InputStreamReader(
            getClass().getResourceAsStream("t6_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream6", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            assertEquals("M", person.getGender());
            if (!(person instanceof Male)) {
                fail("Expected 'Male' type bean.");
            }
            out.write(person);
            
            person = (Person) in.read();
            assertEquals("Mary", person.getFirstName());
            assertEquals("F", person.getGender());     
            if (person instanceof Male) {
                fail("Expected 'Person' type bean.");
            }
            out.write(person);
            
            out.close();
            
            assertEquals(load("t6_in.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test behvavior of a custom type handler where the format method
     * may return null.
     */
    @Test
    public void testTypeHandlerNilSupport() throws Exception {
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream7", s);
        
        Person person = new Person();
        person.setFirstName("");
        person.setLastName(null);
        out.write(person);
        
        person.setFirstName("nil");
        person.setLastName("nil");
        out.write(person);
        out.close();
        
        assertEquals(load("t7_out.xml"), s.toString());
    }
}
