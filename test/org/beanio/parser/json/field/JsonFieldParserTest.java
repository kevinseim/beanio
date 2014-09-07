package org.beanio.parser.json.field;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for JSON fields.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonFieldParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("jsonField_mapping.xml");
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testField_Simple() {
        BeanReader in = factory.createReader("stream", new InputStreamReader(
            getClass().getResourceAsStream("jf1.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals("Joe", map.get("firstName"));
            assertEquals("Johnson", map.get("lastName"));
            assertEquals("20", map.get("age"));
            assertEquals(1, map.get("number"));
            assertEquals(Boolean.TRUE, map.get("healthy"));
            assertArrayEquals(new Integer[] { 10, 20 }, ((List)map.get("array")).toArray());
            
            StringWriter text = new StringWriter();
            factory.createWriter("stream", text).write(map);
            assertEquals(load("jf1.txt"), text.toString());
        }
        finally {
            in.close();
        }
    }
}
