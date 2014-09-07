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
package org.beanio.parser.xml.namespace;

import static org.junit.Assert.*;

import java.io.*;

import org.beanio.*;
import org.beanio.parser.xml.*;
import org.junit.*;

/**
 * JUnit test cases related to reading and writing XML namespaces. 
 * @author Kevin Seim
 * @since 1.1
 */
public class XmlNamespaceTest extends XmlParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("namespace_mapping.xml");
    }
    
    /**
     * Test no root namespace declaration.
     */
    @Test
    public void testNoRootNamespace() throws Exception {
        BeanReader in = factory.createReader("stream", new InputStreamReader(
            getClass().getResourceAsStream("ns1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            
            out.write(person);
            out.close();
            
            assertEquals(load("ns1_out.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test root namespace declaration of '*'. 
     */
    @Test
    public void testAnyRootNamespace() throws Exception {
        BeanReader in = factory.createReader("stream1", new InputStreamReader(
            getClass().getResourceAsStream("ns1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream1", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            
            out.write(person);
            out.close();
            
            assertEquals(load("ns1_out.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test namespace declarations at all levels.
     */
    @Test
    public void testExactNamespace() throws Exception {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("ns1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream2", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            
            out.write(person);
            out.close();
            
            assertEquals(load("ns2_out.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test any namespace.
     */
    @Test
    public void testRecordNamespaceAny() throws Exception {
        BeanReader in = factory.createReader("stream3", new InputStreamReader(
            getClass().getResourceAsStream("ns1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream3", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            
            out.write(person);
            out.close();
            
            assertEquals(load("ns3_out.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test a field namespace of '*'.
     */
    @Test
    public void testFieldNamespaceAny() throws Exception {
        BeanReader in = factory.createReader("stream4", new InputStreamReader(
            getClass().getResourceAsStream("ns1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream4", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            
            out.write(person);
            out.close();
            
            assertEquals(load("ns4_out.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test a group namespace of '*'.
     */
    @Test
    public void testGroupNamespaceAny() throws Exception {
        BeanReader in = factory.createReader("stream5", new InputStreamReader(
            getClass().getResourceAsStream("ns1_in.xml")));
        
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream5", s);
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
            
            out.write(person);
            out.close();
            
            assertEquals(load("ns5_out.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test(expected = UnidentifiedRecordException.class)
    public void testStreamNamespaceDoesNotMatch() throws Exception {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("ns_noMatchingStream.xml")));
        
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
        }
        finally {
            in.close();
        }
    }
    
    @Test(expected = UnidentifiedRecordException.class)
    public void testGroupNamespaceDoesNotMatch() throws Exception {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("ns_noMatchingGroup.xml")));
        
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
        }
        finally {
            in.close();
        }
    }

    @Test(expected = UnidentifiedRecordException.class)
    public void testRecordNamespaceDoesNotMatch() throws Exception {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("ns_noMatchingRecord.xml")));
        
        try {
            Person person = (Person) in.read();
            assertEquals("John", person.getFirstName());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testFieldNamespaceDoesNotMatch() throws Exception {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("ns_noMatchingField.xml")));
        
        try {
            // since the field is not used to identify the record, a bean is
            // still created and the field (with the non-matching namespace)
            // is never populated on the bean
            Person person = (Person) in.read();
            assertNull(person.getFirstName());
        }
        finally {
            in.close();
        }
    }
    
    /**
     * Test a namespace prefix set at the record level.
     */
    @Test
    public void testNamespacePrefixRecord() throws Exception {
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream6", s);
        
        Person person = new Person();
        person.setFirstName("John");
        out.write(person);
        
        person.setFirstName("David");
        out.write(person);
        out.close();
        
        assertEquals(load("ns6_out.xml"), s.toString());
    }
    
    /**
     * Test a namespace prefix set at the field level.
     */
    @Test
    public void testNamespacePrefixField() throws Exception {
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream7", s);
        
        Person person = new Person();
        person.setFirstName("John");
        out.write(person);
        out.close();
        
        assertEquals(load("ns7_out.xml"), s.toString());
    }
    
    /**
     * Test a namespace prefix set at the stream level.  Also test XML header
     * with overriden values.
     */
    @Test
    public void testNamespacePrefixStream() throws Exception {
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream8", s);
        
        Person person = new Person();
        person.setFirstName("John");
        out.write(person);
        out.close();
        
        assertEquals(load("ns8_out.xml"), s.toString());
    }
    
    /**
     * Test a namespace prefix set at the group level.  Also test XML header without
     * encoding.
     */
    @Test
    public void testNamespacePrefixGroup() throws Exception {
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream9", s);
        
        Person person = new Person();
        person.setFirstName("John");
        out.write(person);
        out.close();
        
        assertEquals(load("ns9_out.xml"), s.toString());
    }
    
    /**
     * Test namespace declarations on the root element.  Also test default XML
     * header values.
     */
    @Test
    public void testEagerNamespaceDeclaration() throws Exception {
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream10", s);
        
        Person person = new Person();
        person.setLastName("Smith");
        out.write(person);
        out.close();
        
        assertXmlEquals(load("ns10_out.xml"), s.toString());
    }
    
    /**
     * Test xmlPrefix=""
     */
    @Test
    public void testEmptyPrefix() throws Exception {
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream11", s);
        
        Person person = new Person();
        person.setFirstName("Joe");
        person.setLastName("Smith");
        out.write(person);
        out.close();
        
        assertXmlEquals(load("ns11_out.xml"), s.toString());
    }
    
    /**
     * Test xmlPrefix=""
     */
    @Test
    public void testEmptyPrefix2() throws Exception {
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream12", s);

        Person person = new Person();
        Address address = new Address();
        address.setCity("San Francisco");
        person.setAddress(address);
        out.write(person);
        out.close();

        assertXmlEquals(load("ns12_out.xml"), s.toString());
    }

    /**
     * Test xmlPrefix=""
     */
    @Test
    public void testEmptyPrefix3() throws Exception {
        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream13", s);

        Person person = new Person();
        Address address = new Address();
        address.setCity("San Francisco");
        person.setAddress(address);
        out.write(person);
        out.close();

        assertXmlEquals(load("ns13_out.xml"), s.toString());
    }
}
