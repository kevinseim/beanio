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
package org.beanio.parser.xml.field;

import static org.junit.Assert.*;

import java.io.*;

import org.beanio.*;
import org.beanio.parser.xml.*;
import org.junit.*;

/**
 * JUnit test cases related to reading and writing fields in an XML formatted stream. 
 * @author Kevin Seim
 * @since 1.1.1
 */
public class XmlFieldTest extends XmlParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("field_mapping.xml");
    }
    
    /**
     * Test an optional padded field.
     */
    @Test
    public void testPaddingForOptionalField() throws Exception {
        BeanReader in = factory.createReader("stream", new InputStreamReader(
            getClass().getResourceAsStream("f1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream", s);
        try {
            Person person = (Person) in.read();
            assertEquals(Integer.valueOf(25), person.getAge());
            out.write(person);
            
            person = (Person) in.read();
            assertNull(person.getAge());
            out.write(person);
            
            out.close();
            assertEquals(load("f1_in.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
   
    /**
     * Test a required padded field.
     */
    @Test
    public void testPaddingForRequiredField() throws Exception {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("f2_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream2", s);
        try {
            Person person = (Person) in.read();
            assertEquals(Integer.valueOf(25), person.getAge());
            out.write(person);
            
            assertFieldError(in, 5, "record", "age", "", "Required field not set");
            person.setAge(null);
            out.write(person);
            
            assertFieldError(in, 8, "record", "age", "025", "Invalid padded field length, expected 5 characters");
            
            out.close();
            assertEquals(load("f2_out.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test a setter is not called if the element is missing.
     */
    @Test
    public void testSetterNotCalledForMissingField() throws Exception {
        BeanReader in = factory.createReader("stream3", new InputStreamReader(
            getClass().getResourceAsStream("f3_in.xml")));
        
        try {
            Person person = (Person) in.read();
            assertEquals("Joe", person.getFirstName());
            assertNull(person.getLastName());
            assertEquals(Integer.valueOf(10), person.getAge());
            
            person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            assertSame(Person.DEFAULT_NAME, person.getLastName());
            assertSame(Person.DEFAULT_AGE, person.getAge());
        }
        finally {
            in.close();
        }
    }
}
