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
package org.beanio.parser.bean;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for parsing bean objects and collections of bean objects.
 * @author Kevin Seim
 * @since 1.0
 */
public class BeanParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("widget.xml");
    }
    
    @Test
    public void testDelimitedPositions() {
        BeanReader in = factory.createReader("w1", new InputStreamReader(
            getClass().getResourceAsStream("w1_position.txt")));
        
        try {
            Widget w = (Widget) in.read();
            assertEquals(3, w.getId());
            assertEquals("Widget3", w.getName());
            assertEquals(2, w.getTop().getId());
            assertEquals("Widget2", w.getTop().getName());
            assertEquals(1, w.getBottom().getId());
            assertEquals("Widget1", w.getBottom().getName());
            
            StringWriter text = new StringWriter();
            factory.createWriter("w1", text).write(w);
            assertEquals(",Widget1,1,2,Widget2,Widget3,3" + lineSeparator, text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testCollectionsAndDefaultDelmiitedPositions() {
        BeanReader in = factory.createReader("w2", new InputStreamReader(
            getClass().getResourceAsStream("w2_collections.txt")));
        
        try {
            Widget w = (Widget) in.read();
            assertEquals("3", w.getName());
            assertEquals("2B", w.getPart(1).getPart(1).getName());
            
            StringWriter text = new StringWriter();
            factory.createWriter("w2", text).write(w);
            assertEquals("1,1M,1A,1AM,1B,1BM,2,2M,2A,2AM,2B,2BM,3" + lineSeparator, text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testFixedLengthAndOptionalFields() {
        BeanReader in = factory.createReader("w3", new InputStreamReader(
            getClass().getResourceAsStream("w3_fixedLength.txt")));
        
        try {
            Widget w = (Widget) in.read();
            assertEquals(1, w.getId());
            assertEquals("name1", w.getName());
            assertEquals("mode1", w.getModel());
            
            StringWriter text = new StringWriter();
            factory.createWriter("w3", text).write(w);
            assertEquals(" 1name1mode1" + lineSeparator, text.toString());
            
            w = (Widget) in.read();
            assertEquals(1, w.getId());
            assertEquals("name1", w.getName());
            assertEquals("mode1", w.getModel());
            assertEquals(2, w.getPart(0).getId());
            assertEquals("name2", w.getPart(0).getName());
            assertEquals("mode2", w.getPart(0).getModel());
            assertEquals(3, w.getPart(1).getId());
            assertEquals(4, w.getPart(2).getId());
            assertEquals("name4", w.getPart(2).getName());
            assertEquals("", w.getPart(2).getModel());          
            
            text = new StringWriter();
            factory.createWriter("w3", text).write(w);
            assertEquals(" 1name1mode1 2name2mode2 3           4name4     " + lineSeparator, text.toString());
            
            w = (Widget) in.read();
            text = new StringWriter();
            factory.createWriter("w3", text).write(w);
            assertEquals(" 1name1mode1 2name2mode2 0           4name4mode4" + lineSeparator, text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testFixedLengthMap() {
        BeanReader in = factory.createReader("w4", new InputStreamReader(
            getClass().getResourceAsStream("w4_map.txt")));
        
        try {
            Widget w = (Widget) in.read();
            assertEquals(1, w.getId());
            assertEquals("name1", w.getName());
            assertEquals(2, w.getPart("part1").getId());
            assertEquals("name2", w.getPart("part1").getName());
            
            StringWriter text = new StringWriter();
            factory.createWriter("w4", text).write(w);
            assertEquals("1name12name2" + lineSeparator, text.toString());
            
            w = (Widget) in.read();
            assertEquals(3, w.getPart("part2").getId());
            assertEquals("name3", w.getPart("part2").getName());
            
            text = new StringWriter();
            factory.createWriter("w4", text).write(w);
            assertEquals("1name12name23name3" + lineSeparator, text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testFixedLengthOutOfOrder() {
        BeanReader in = factory.createReader("w5", new InputStreamReader(
            getClass().getResourceAsStream("w5_outOfOrder.txt")));
        
        try {
            Widget w;
            
            @SuppressWarnings("rawtypes")
            Map map = (Map) in.read();
            w = (Widget) map.get("part3");
            assertEquals(3, w.getId());
            assertEquals("name3", w.getName());
            
            StringWriter text = new StringWriter();
            factory.createWriter("w5", text).write(map);
            assertEquals("123name1name2name3" + lineSeparator, text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testFieldError() {
        BeanReader in = factory.createReader("w6", new InputStreamReader(
            getClass().getResourceAsStream("w6_fieldError.txt")));
        
        try {
            assertFieldError(in, 1, "record1", "id", 2, "A", "Type conversion error: Invalid Integer value 'A'");
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testWriteNull() {
        Widget w = new Widget();
        
        StringWriter text = new StringWriter();
        factory.createWriter("w6", text).write(w);
        assertEquals("" + lineSeparator, text.toString());
        
        Widget part1 = new Widget();
        part1.setId(1);
        part1.setName("part1");
        w.addPart(part1);
        
        text = new StringWriter();
        factory.createWriter("w6", text).write(w);
        assertEquals("1,part1" + lineSeparator, text.toString());
        
        w.addPart(null);
        
        text = new StringWriter();
        factory.createWriter("w6", text).write(w);
        assertEquals("1,part1,," + lineSeparator, text.toString());
        
        Widget part2 = new Widget();
        part2.setId(2);
        part2.setName("part2");
        w.addPart(part2);
        
        text = new StringWriter();
        factory.createWriter("w6", text).write(w);
        assertEquals("1,part1,,,2,part2" + lineSeparator, text.toString());
    }
    
    @Test
    public void testBackfill() {
        Widget w = new Widget();
        w.setId(1);
        w.setName(null);
        w.setModel(null);
        
        StringWriter text = new StringWriter();
        factory.createWriter("w7", text).write(w);
        assertEquals("1" + lineSeparator, text.toString());
        
        w.setModel("model");
        
        text = new StringWriter();
        factory.createWriter("w7", text).write(w);
        assertEquals("1,,model" + lineSeparator, text.toString());
    }
    
    @Test
    public void testRecordIdentifier() { 
        Widget w = new Widget();
        w.setId(1);
        w.setName("name1");
        
        Map<String,Widget> map = new HashMap<>();
        map.put("widget", w);
        
        StringWriter text = new StringWriter();
        factory.createWriter("w8", text).write(map);
        assertEquals("R1,1,name1" + lineSeparator, text.toString());
        
        w.setId(2);
        w.setName("name2");
        
        text = new StringWriter();
        factory.createWriter("w8", text).write(map);
        assertEquals("R2,2,name2" + lineSeparator, text.toString());
        
        w.setId(3);
        try {
            factory.createWriter("w8", text).write(map);
            fail("Expected no record mapping found");
        }
        catch (BeanWriterException ex) { }
    }
    
    @Test
    public void testFixedLengthCollection() {
        BeanReader in = factory.createReader("w9", new InputStreamReader(
            getClass().getResourceAsStream("w9_flcollections.txt")));
            
        try {
            Widget w = (Widget) in.read();
            assertEquals(1, w.getId());
            assertEquals("name", w.getName());
            
            StringWriter text = new StringWriter();
            factory.createWriter("w9", text).write(w);
            assertEquals(" 1 1part1 2part2name " + lineSeparator, text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testNestedBeans() {
        BeanReader in = factory.createReader("w10", new InputStreamReader(
            getClass().getResourceAsStream("w10_nestedBeans.txt")));
        
        try {
            Map map = (Map) in.read();
            map = (Map) in.read();
            assertEquals("eof", (String)map.get("eof"));
            
            List list = (List) map.get("b1");
            assertNotNull(list);
            assertEquals(2, list.size());
            
            Map b1 = (Map) list.get(0);
            assertEquals("a", b1.get("f0"));
            assertEquals("d", b1.get("f2"));
            
            List b2List = (List) b1.get("b2");
            assertEquals("b", ((Map)b2List.get(0)).get("f1"));
            assertEquals("c", ((Map)b2List.get(1)).get("f1"));
            
            b1 = (Map) list.get(1);
            assertEquals("e", b1.get("f0"));
            assertEquals("h", b1.get("f2"));
            
            b2List = (List) b1.get("b2");
            assertEquals("f", ((Map)b2List.get(0)).get("f1"));
            assertEquals("g", ((Map)b2List.get(1)).get("f1"));
            
            StringWriter text = new StringWriter();
            factory.createWriter("w10", text).write(map);
            assertEquals("a b c d e f g h eof" + lineSeparator, text.toString());
        }
        finally {
            in.close();
        }
    }
}
