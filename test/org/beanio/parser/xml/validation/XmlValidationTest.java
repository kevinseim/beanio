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
package org.beanio.parser.xml.validation;

import static org.junit.Assert.*;

import java.io.*;

import org.beanio.*;
import org.beanio.parser.xml.*;
import org.junit.*;

/**
 * JUnit test cases for testing XML specific field validation.
 * @author Kevin Seim
 * @since 1.1
 */
public class XmlValidationTest extends XmlParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("validation_mapping.xml");
    }
    
    /**
     * Test nillable field errors.
     */
    @Test
    public void testFieldErrors_NillableAndMinOccurs() throws Exception {
        BeanReader in = factory.createReader("stream", new InputStreamReader(
            getClass().getResourceAsStream("v1_in.xml")));
        
        try {
            Person person = (Person) in.read();
            assertEquals("", person.getFirstName());
            assertEquals("", person.getLastName());
            
            assertFieldError(in, 6, "person", "firstName", 0, null, "Field is not nillable");
            assertFieldError(in, 10, "person", "lastName", 0, null, "Field is not nillable");
            assertFieldError(in, 14, "person", "firstName", 0, null, "Expected minimum 1 occurrences");
            assertFieldError(in, 17, "person", "lastName", 0, null, "Expected minimum 1 occurrences");
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test nillable field error for a bean.
     */
    @Test
    public void testFieldErrors_NillableBean() throws Exception {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("v2_in.xml")));
        
        try {
            Person person = (Person) in.read();
            assertNotNull(person.getAddress());
            assertEquals("IL", person.getAddress().getState());
            
            assertFieldError(in, 7, "person", "address", 0, null, "Field is not nillable");
            assertFieldError(in, 10, "person", "address", 0, null, "Expected minimum 1 occurrences");
        }
        finally {
            in.close();
        }
    }
}
