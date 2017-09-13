package org.beanio.parser.json.segment;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.beanio.*;
import org.beanio.beans.Person;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for JSON segments.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonSegmentParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("jsonSegment_mapping.xml");
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testSegment_JsonTypeObject() {
        BeanReader in = factory.createReader("stream1", new InputStreamReader(
            getClass().getResourceAsStream("js1.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals("1234", map.get("account"));
            
            Person person = (Person) map.get("customer");
            assertEquals("Jen", person.getFirstName());
            assertEquals("Jones", person.getLastName());
            
            StringWriter text = new StringWriter();
            factory.createWriter("stream1", text).write(map);
            assertEquals(load("js1.txt"), text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testSegment_JsonTypeObjectList() {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("js2.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals(Integer.valueOf(2), map.get("count"));
            
            List list = (List) map.get("friends");
            Person person = (Person) list.get(0);
            assertEquals("Jen", person.getFirstName());
            assertEquals("Jones", person.getLastName());
            person = (Person) list.get(1);
            assertEquals("Mary", person.getFirstName());
            assertEquals("Smith", person.getLastName());
            
            StringWriter text = new StringWriter();
            factory.createWriter("stream2", text).write(map);
            assertEquals(load("js2.txt"), text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testSegment_JsonTypeArray() {
        BeanReader in = factory.createReader("stream3", new InputStreamReader(
            getClass().getResourceAsStream("js3.txt")));
        
        try {
            Map map = (Map) in.read();
            assertArrayEquals(new Integer[] { 1, 2, 3 }, ((List)map.get("numbers")).toArray());
            
            Person person = (Person) map.get("person");
            assertEquals("Jen", person.getFirstName());
            assertEquals("Jones", person.getLastName());
            
            StringWriter text = new StringWriter();
            factory.createWriter("stream3", text).write(map);
            assertEquals(load("js3.txt"), text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testSegment_JsonTypeNone() {
        BeanReader in = factory.createReader("stream4", new InputStreamReader(
            getClass().getResourceAsStream("js4.txt")));
        
        try {
            Map map = (Map) in.read();
            
            Person person = (Person) map.get("person");
            assertEquals("Jen", person.getFirstName());
            assertEquals("Jones", person.getLastName());
            assertEquals("1234", map.get("account"));
            
            StringWriter text = new StringWriter();
            BeanWriter out = factory.createWriter("stream4", text);
            out.write(map);
            
            map = (Map) in.read();
            person = (Person) map.get("person");
            assertEquals("Jason", person.getFirstName());
            assertEquals("Jones", person.getLastName());
            assertEquals("5678", map.get("account"));
            out.write(map);
            
            assertEquals(load("js4.txt"), text.toString());
        }
        finally {
            in.close();
        }
    }
}
