/*
 * Copyright 2010-2012 Kevin Seim
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
package org.beanio.parser.fixedlength;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for fixed length streams.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class FixedLengthParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("fixedlength.xml");
    }

    @Test
    public void testParallelLongConversion() throws InterruptedException, ExecutionException, TimeoutException {

        Map<MyRec,String> dataMap = new HashMap<>();
        for(int i = 0; i<1000; i++) {
            String string = "T"+String.format("%010d", i);
            MyRec rec = (MyRec)factory.createUnmarshaller("f9").unmarshal(string);
            dataMap.put(rec,string);
        }

        ExecutorService service = Executors.newFixedThreadPool(10);
        List<FutureTask<MyRec>> tasks = new ArrayList<>();
        for(final String inp:dataMap.values()) {
            tasks.add(new FutureTask<>(new Callable<MyRec>() {
                @Override
                public MyRec call() throws Exception {
                    return (MyRec) factory.createUnmarshaller("f9").unmarshal(inp);
                }
            }));
        }

        for(FutureTask<MyRec> task:tasks) {
            service.submit(task);
        }

        Set<MyRec> resultSet = new HashSet<>();
        for(FutureTask<MyRec> task:tasks) {
            resultSet.add(task.get(5, TimeUnit.SECONDS));
        }

        service.shutdown();

        for(MyRec ok:dataMap.keySet()) {
            assertTrue(resultSet.contains(ok));
        }
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testFieldDefinitions() throws Exception {
        BeanReader in = factory.createReader("f1", new InputStreamReader(
            getClass().getResourceAsStream("f1_valid.txt")));
        try {
            Map map;
            map = (Map) in.read();
            assertEquals(" value", map.get("default"));
            assertEquals(12345, map.get("number"));
            assertEquals("value", map.get("padx"));
            assertEquals("value", map.get("pos40"));

            StringWriter text = new StringWriter();
            BeanWriter out = factory.createWriter("f1", text);
            out.write(map);
            out.flush();
            out.close();
            assertEquals(" value    0000012345valuexxxxx          value", text.toString());
        }
        finally {
            in.close();
        }
    }

    @Test(expected = InvalidRecordException.class)
    public void testDefaultMinLengthValidation() {
        BeanReader in = factory.createReader("f1", new InputStreamReader(
            getClass().getResourceAsStream("f1_minLength.txt")));
        try {
            in.read();
        }
        finally {
            in.close();
        }
    }

    @Test(expected = InvalidRecordException.class)
    public void testDefaultMaxLengthValidation() {
        BeanReader in = factory.createReader("f1", new InputStreamReader(
            getClass().getResourceAsStream("f1_maxLength.txt")));
        try {
            in.read();
        }
        finally {
            in.close();
        }
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testOptionalField() {
        BeanReader in = factory.createReader("f2", new InputStreamReader(
            getClass().getResourceAsStream("f2_valid.txt")));
        try {
            Map map = (Map) in.read();
            assertEquals("value", map.get("field3"));

            map = (Map) in.read();
            assertEquals("value", map.get("field3"));

            map = (Map) in.read();
            assertNull(map.get("field3"));

            StringWriter text = new StringWriter();
            BeanWriter out = factory.createWriter("f2", text);
            out.write(map);
            assertEquals("1234512345\r\n", text.toString());
        }
        finally {
            in.close();
        }
    }

    @Test
    public void testValidation() {
        BeanReader in = factory.createReader("f2", new InputStreamReader(
            getClass().getResourceAsStream("f2_invalid.txt")));
        try {
            assertRecordError(in, 1, "record", "minLength, 1, Record Label, 12345, 10, 20");
            assertRecordError(in, 2, "record",
                "maxLength, 2, Record Label, 123456789012345678901, 10, 20");
            assertFieldError(in, 3, "record", "field3", "val", "Invalid field length, expected 5 characters");
        }
        finally {
            in.close();
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testReader() {
        BeanReader in = factory.createReader("f3", new InputStreamReader(
            getClass().getResourceAsStream("f3_valid.txt")));
        try {
            Map map = (Map) in.read();
            assertEquals("00001", map.get("field1"));
            assertEquals("", map.get("field2"));
            assertEquals("XXXXX", map.get("field3"));
            
            StringWriter text = new StringWriter();
            factory.createWriter("f3", text).write(map);
            assertEquals("00001     XXXXX" + lineSeparator, text.toString());
            
            map = (Map) in.read();
            assertEquals("00002", map.get("field1"));
            assertEquals("Val2", map.get("field2"));
            
            map.put("field2", "Value2");
            
            text = new StringWriter();
            factory.createWriter("f3", text).write(map);
            assertEquals("00002Value" + lineSeparator, text.toString());
            
            in.read();
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testPadding() {
        BeanReader in = factory.createReader("f4", new InputStreamReader(
            getClass().getResourceAsStream("f4_padding.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals(Arrays.asList(0, 1, 10, 100, 1000, 10000, null), map.get("number"));
            
            StringWriter text = new StringWriter();
            factory.createWriter("f4", text).write(map);
            assertEquals("INT000000000100010001000100010000     ", text.toString());
            
            map = (Map) in.read();
            assertEquals(Arrays.asList('A', 'B', ' ', 'D'), map.get("character"));
            
            text = new StringWriter();
            factory.createWriter("f4", text).write(map);
            assertEquals("CHAAB D", text.toString());
            
            map = (Map) in.read();
            assertEquals(Arrays.asList("TXT", "TX" , "T", ""), map.get("stringLeft"));

            text = new StringWriter();
            factory.createWriter("f4", text).write(map);
            assertEquals("STLTXTTX T     ", text.toString());
            
            map = (Map) in.read();
            assertEquals(Arrays.asList("TXT", "TX" , "T", ""), map.get("stringRight"));
            
            text = new StringWriter();
            factory.createWriter("f4", text).write(map);
            assertEquals("STRTXT TX  T   ", text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testIgnoredField() {
        BeanReader in = factory.createReader("f5", new InputStreamReader(
            getClass().getResourceAsStream("f5_valid.txt")));
        
        StringWriter text;
        try {
            Map map = (Map) in.read();
            assertEquals("Smith", map.get("lastName"));
            
            text = new StringWriter();
            factory.createWriter("f5", text).write(map);
            assertEquals("AAAAAAAAAASmith     ", text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testVariableLengthField() {
        String record = "kevin     johnson";
        
        Unmarshaller u = factory.createUnmarshaller("f6");
        
        Map map = (Map) u.unmarshal(record);
        assertEquals("kevin", map.get("firstName"));
        assertEquals("johnson", map.get("lastName"));
        
        Marshaller m = factory.createMarshaller("f6");
        assertEquals(record, m.marshal(map).toString());
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testKeepPadding() {
        Marshaller m = factory.createMarshaller("f7");
        BeanReader in = factory.createReader("f7", new InputStreamReader(
            getClass().getResourceAsStream("f7.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals("kevin     ", map.get("firstName"));
            assertEquals("          ", map.get("lastName"));
            assertEquals("kevin               ", m.marshal(map).toString());
            
            assertFieldError(in, 2, "record", "firstName", "          ", "Required field not set");
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testOverlay() {
        StringWriter output = new StringWriter();
        BeanWriter out = factory.createWriter("f8", output);
        
        Map map = new HashMap();
        map.put("number", 3);
        map.put("name", "LAUREN1");
        out.write("record1", map);
        
        map.clear();
        map.put("number", 5);
        out.write("record2", map);
        
        out.flush();
        
        assertEquals(
            "003LAUREN1\n" +
            "0005\n", output.toString());
    }
}
