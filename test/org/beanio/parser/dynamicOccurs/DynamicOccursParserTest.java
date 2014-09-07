package org.beanio.parser.dynamicOccurs;

import static org.junit.Assert.*;

import java.util.*;

import org.beanio.*;
import org.beanio.beans.Person;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for fields and segments that use dynamic occurrences.
 * @author Kevin Seim
 */
public class DynamicOccursParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("dynamicOccurs_mapping.xml");
    }
    
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testDynamicFieldOccurrences() {
        Unmarshaller u = factory.createUnmarshaller("o1");
        Marshaller m = factory.createMarshaller("o1");
        
        String text = "2,one,two,0,done";
        Map map = (Map) u.unmarshal(text);
        Assert.assertNull(map.get("occurs"));
        List list = (List) map.get("values");
        Assert.assertNotNull(list);
        Assert.assertEquals(Arrays.asList("one", "two"), list);
        Assert.assertEquals(0, map.get("occurs2"));
        list = (List) map.get("values2");
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
        Assert.assertEquals("done", map.get("after"));
        Assert.assertEquals(text, m.marshal(map).toString());
        
        text = "0,1,one,done";
        map = (Map) u.unmarshal(text);
        list = (List) map.get("values");
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(1, map.get("occurs2"));
        list = (List) map.get("values2");
        Assert.assertNotNull(list);
        Assert.assertEquals(Arrays.asList("one"), list);
        Assert.assertEquals("done", map.get("after"));
        Assert.assertEquals(text, m.marshal(map).toString());
        
        map.put("occurs2", 0);
        Assert.assertEquals("0,0,done", m.marshal(map).toString());
    }
    
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testDynamicSegmentOccurrences() {
        Unmarshaller u = factory.createUnmarshaller("o2");
        Marshaller m = factory.createMarshaller("o2");
        
        Map<?,?> map = null;
        List<Person> people = null;
        Person person = null;
        
        String text = "02Rob 00Mike020102end";
        map = (Map) u.unmarshal(text);
        people = (List<Person>) map.get("people");
        Assert.assertNotNull(people);
        Assert.assertEquals(2, people.size());
        person = people.get(0);
        Assert.assertEquals("Rob", person.getFirstName());
        Assert.assertEquals(0, person.getNumbers().size());
        person = people.get(1);
        Assert.assertEquals("Mike", person.getFirstName());
        Assert.assertEquals(Arrays.asList(1, 2), person.getNumbers());      
        Assert.assertEquals(text, m.marshal(map).toString());
        
        text = "00end";
        map = (Map) u.unmarshal(text);
        Assert.assertEquals(Collections.emptyList(), map.get("people"));
        Assert.assertEquals(text, m.marshal(map).toString());
    }
    
    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testInlineMapDynamicOccurrences() {
        Unmarshaller u = factory.createUnmarshaller("o3");
        Marshaller m = factory.createMarshaller("o3");
        
        Map<?,?> map = null;
        Map<?,?> inline = null;
        
        String text = "0201Rob 02Mikeend";
        map = (Map) u.unmarshal(text);
        inline = (Map) map.get("names");
        Assert.assertNotNull(inline);
        Assert.assertEquals("Rob", inline.get(1));
        Assert.assertEquals("Mike", inline.get(2));     
        Assert.assertEquals(text, m.marshal(map).toString());
        
        text = "00end";
        map = (Map) u.unmarshal(text);
        inline = (Map) map.get("names");
        Assert.assertNotNull(inline);
        Assert.assertEquals(0, inline.size());
        Assert.assertEquals(text, m.marshal(map).toString());
    }
    
    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testArrayDynamicOccurrences() {
        Unmarshaller u = factory.createUnmarshaller("o4");
        Marshaller m = factory.createMarshaller("o4");
        
        Map<?,?> map = null;
        
        String text = "3,3,2,1,end";
        map = (Map) u.unmarshal(text);
        Assert.assertArrayEquals(new Integer[] { 3, 2, 1 }, (Integer[]) map.get("numbers"));
        Assert.assertEquals(text, m.marshal(map).toString());

        text = "0,end";
        map = (Map) u.unmarshal(text);
        Assert.assertArrayEquals(new Integer[0], (Integer[]) map.get("numbers"));
        Assert.assertEquals(text, m.marshal(map).toString());
    }
    
    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testDynamicOccurrencesValidation() {
        Unmarshaller u = factory.createUnmarshaller("o5");
        Marshaller m = factory.createMarshaller("o5");
        
        String text = "2,one,two";
        List list = (List) u.unmarshal(text);
        Assert.assertEquals(Arrays.asList("one", "two"), list);
        Assert.assertEquals(text, m.marshal(list).toString());
        
        try {
            u.unmarshal("0");
            fail("Record expected to fail validation");
        }
        catch (InvalidRecordException ex) {
            for (String s : ex.getRecordContext().getFieldErrors("values")) {
                assertEquals("Expected minimum 1 occurrences", s);
            }
        }
        
        try {
            u.unmarshal("3,one,two,three");
            fail("Record expected to fail validation");
        }
        catch (InvalidRecordException ex) {
            for (String s : ex.getRecordContext().getFieldErrors("values")) {
                assertEquals("Expected maximum 2 occurrences", s);
            }
        }
    }
}
