package org.beanio.parser.indeterminates;

import java.util.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * Unit test cases for indetermine fields and segments in the middle
 * of a delimited or fixed length record.
 * 
 * @author Kevin Seim
 */
public class IndeterminateSegmentsTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("indeterminates_mapping.xml");
    }
    
    @Test
    public void testDelimitedIndeterminateFieldBeforeEOR() {
        testDelimitedIndeterminateFieldBeforeEOR("d1");
        testDelimitedIndeterminateFieldBeforeEOR("d3");
    }
    @SuppressWarnings("rawtypes")
    private void testDelimitedIndeterminateFieldBeforeEOR(String stream) {
        List list = null;
        String text = "v1,v2.1,v2.2,v3.1,v3.2,v4";
        
        Map map = null;
        Unmarshaller u = factory.createUnmarshaller(stream);
        map = (Map) u.unmarshal(text);
        
        Assert.assertNotNull(map);
        Assert.assertEquals(4, map.size());
        Assert.assertEquals("v1", map.get("f1"));
        list = (List) map.get("f2");
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("v2.1", list.get(0));
        Assert.assertEquals("v2.2", list.get(1));
        list = (List) map.get("f3");
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("v3.1", list.get(0));
        Assert.assertEquals("v3.2", list.get(1));        
        Assert.assertEquals("v4", map.get("f4"));
        
        Marshaller m = factory.createMarshaller(stream);
        Assert.assertEquals(text, m.marshal(map).toString());
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testDelimitedIndeterminateSegmentBeforeEOR() {
        List list, subList = null;
        Map map, subMap = null;
        String text = "v1,v2.1,v3.1,v2.2,v3.2,v4,v5.1,v6.1,v6.2,v5.2,v6.3,v6.4,v7";
        
        Unmarshaller u = factory.createUnmarshaller("d2");
        map = (Map) u.unmarshal(text);
        
        Assert.assertNotNull(map);
        Assert.assertEquals("v1", map.get("f1"));
        list = (List) map.get("rs1");
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        subMap = (Map) list.get(0);
        Assert.assertEquals("v2.1", subMap.get("f2"));
        Assert.assertEquals("v3.1", subMap.get("f3"));
        subMap = (Map) list.get(1);
        Assert.assertEquals("v2.2", subMap.get("f2"));
        Assert.assertEquals("v3.2", subMap.get("f3"));
        Assert.assertEquals("v4", map.get("f4"));
        
        list = (List) map.get("rs2");
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        subMap = (Map) list.get(0);
        Assert.assertEquals("v5.1", subMap.get("f5"));
        subList = (List) subMap.get("f6");
        Assert.assertNotNull(subList);
        Assert.assertEquals(2, subList.size());
        Assert.assertEquals("v6.1", subList.get(0));
        Assert.assertEquals("v6.2", subList.get(1));
        subMap = (Map) list.get(1);
        Assert.assertEquals("v5.2", subMap.get("f5"));
        subList = (List) subMap.get("f6");
        Assert.assertNotNull(subList);
        Assert.assertEquals(2, subList.size());
        Assert.assertEquals("v6.3", subList.get(0));
        Assert.assertEquals("v6.4", subList.get(1));
        
        Assert.assertEquals("v7", map.get("f7"));
        
        Marshaller m = factory.createMarshaller("d2");
        Assert.assertEquals(text, m.marshal(map).toString());
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testNestedSegment() {
        String text = "v1,v2.1,v3.1,v2.2,v3.2";
        
        Map map, submap = null;
        List list = null;
        
        Unmarshaller u = factory.createUnmarshaller("d4");
        map = (Map) u.unmarshal(text);
        Assert.assertEquals("v1", map.get("f1"));
        submap = (Map) map.get("rs1");
        Assert.assertNotNull(submap);
        list = (List) submap.get("rs2");
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        submap = (Map) list.get(0);
        Assert.assertEquals("v2.1", submap.get("f2"));
        Assert.assertEquals("v3.1", submap.get("f3"));
        submap = (Map) list.get(1);
        Assert.assertEquals("v2.2", submap.get("f2"));
        Assert.assertEquals("v3.2", submap.get("f3"));
        
        Marshaller m = factory.createMarshaller("d4");
        Assert.assertEquals(text, m.marshal(map).toString());
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testFixedLengthIndeterminateFieldBeforeEOR() {
        List list = null;
        String text = "v1v2.1v2.2v2.3v3.1v3.2v4";
        
        Map map = null;
        Unmarshaller u = factory.createUnmarshaller("fl1");
        map = (Map) u.unmarshal(text);
        
        Assert.assertNotNull(map);
        Assert.assertEquals(4, map.size());
        Assert.assertEquals("v1", map.get("f1"));
        list = (List) map.get("f2");
        Assert.assertNotNull(list);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals("v2.1", list.get(0));
        Assert.assertEquals("v2.2", list.get(1));
        Assert.assertEquals("v2.3", list.get(2));
        list = (List) map.get("f3");
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("v3.1", list.get(0));
        Assert.assertEquals("v3.2", list.get(1));        
        Assert.assertEquals("v4", map.get("f4"));
        
        Marshaller m = factory.createMarshaller("fl1");
        Assert.assertEquals(text, m.marshal(map).toString());
    }
}
