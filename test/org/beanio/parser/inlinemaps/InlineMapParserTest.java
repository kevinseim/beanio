package org.beanio.parser.inlinemaps;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.Map;

import org.beanio.*;
import org.beanio.beans.Person;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * Unit test cases for inline maps (e.g. <tt>key1,value1,key2,value2</tt>).
 * 
 * @author Kevin Seim
 */
@SuppressWarnings("rawtypes")
public class InlineMapParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("map_mapping.xml");
    }
    
    @Test
    public void testMap_WithClass() {
        Unmarshaller u = factory.createUnmarshaller("stream1");
        Marshaller m = factory.createMarshaller("stream1");
        
        String text = "js,Joe,Smith,bm,Bob,Marshall";
        
        Person person;
        Map map = (Map) u.unmarshal(text);
        
        Assert.assertEquals(map.size(), 2);
        Assert.assertTrue(map.containsKey("js"));
        
        person = (Person) map.get("js");
        Assert.assertEquals("js", person.getId());
        Assert.assertEquals("Joe", person.getFirstName());
        Assert.assertEquals("Smith", person.getLastName());
        
        Assert.assertTrue(map.containsKey("bm"));
        person = (Person) map.get("bm");
        Assert.assertEquals("bm", person.getId());
        Assert.assertEquals("Bob", person.getFirstName());
        Assert.assertEquals("Marshall", person.getLastName());    
        
        Assert.assertEquals(text, m.marshal(map).toString());
    }
    
    @Test
    public void testMap_WithTarget() {
        Unmarshaller u = factory.createUnmarshaller("stream2");
        Marshaller m = factory.createMarshaller("stream2");
        
        String text = "js,Joe,Smith,bm,Bob,Marshall";
        
        Map map = (Map) u.unmarshal(text);
        
        Assert.assertEquals(map.size(), 2);
        Assert.assertTrue(map.containsKey("js"));
        Assert.assertEquals("Joe", map.get("js"));
        Assert.assertTrue(map.containsKey("bm"));
        Assert.assertEquals("Bob", map.get("bm"));   
        
        Assert.assertEquals("js,Joe,,bm,Bob,", m.marshal(map).toString());
    }
    
    @Test
    public void testRecordBoundMap() {
    	Unmarshaller u = factory.createUnmarshaller("stream3");
    	Marshaller m = factory.createMarshaller("stream3");
    	
    	String text = "J,1,key1,value1,key2,value2";
    	Job job = (Job) u.unmarshal(text);
    	Assert.assertEquals("1", job.getId());
    	Map map = job.getCodes();
    	Assert.assertEquals("value1", map.get("key1"));
    	Assert.assertEquals("value2", map.get("key2"));
    	
    	Assert.assertEquals(text, m.marshal(job).toString());
    }
    
    @Test
    public void testGroupBoundMap() {
    	String text = "key1,value1\nkey2,value2";
    	BeanReader in = factory.createReader("stream4", new StringReader(text));
    	
    	Job job = (Job) in.read();
    	Map map = job.getCodes();
    	Assert.assertEquals("value1", map.get("key1"));
    	Assert.assertEquals("value2", map.get("key2"));
    }
    
    @Test
    public void testMapRecordGroup() {
        String text = 
            "entity,PERSON,8.400000,-77.200000,TEST_ENTITY_1\n" +
            "detail,foo,bar\n" +
            "detail,foo2,bar\n" +
            "entity,PERSON,-33.993670,25.676320,TEST_ENTITY_2\n" +
            "entity,PERSON,-22.282174,166.441458,TEST_ENTITY_3\n";
        
        BeanReader in = factory.createReader("stream5", new StringReader(text));
        
        Map map = (Map) in.read();
        assertEquals("ACTIVE", map.get("status"));
        assertEquals("PERSON", map.get("subtype"));
        assertEquals (Double.valueOf(8.4), map.get("lat"));
        assertEquals(Double.valueOf(-77.2), map.get("lon"));
        
        map = (Map) in.read();
        assertEquals ("ACTIVE", map.get("status"));
        assertEquals("PERSON", map.get("subtype"));
        assertEquals (Double.valueOf(-33.99367), map.get("lat"));
        assertEquals(Double.valueOf(25.67632), map.get("lon"));
        
        map = (Map) in.read();
        assertEquals ("ACTIVE", map.get("status"));
        assertEquals("PERSON", map.get("subtype"));
        assertEquals (Double.valueOf(-22.282174), map.get("lat"));
        assertEquals(Double.valueOf(166.441458), map.get("lon"));
    }
}
