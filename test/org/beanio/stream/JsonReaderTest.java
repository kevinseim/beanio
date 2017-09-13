/*
 * Copyright 2012 Kevin Seim
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
package org.beanio.stream;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.beanio.stream.json.*;
import org.junit.Test;

/**
 * JUnit test cases for {@link JsonReader} and {@link JsonRecordUnmarshaller}
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonReaderTest {

    private JsonReader newReader(String text) {
        return new JsonReader(new StringReader(text));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test_readString() throws IOException {

        JsonReader in = newReader(
            "{\"field1\":\"value1\"}\n" +
            "{ \"field2\" : \"value2\" }");

        Map map = in.read();
        assertEquals("value1", (String) map.get("field1"));
        map = in.read();
        assertEquals("value2", (String) map.get("field2"));
        assertNull(in.read());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test_readNumber() throws IOException {
        Long long1 = Long.MAX_VALUE;
        Integer int1 = Integer.MAX_VALUE;

        JsonReader in = newReader(
            "{\"double1\":5e10}\n" +
            "{\"double2\":5.1}\n" +
            "{ \"double3\" : 5E10 }\n" +
            "{\"int1\":" + int1 + "}\n" +
            "{\"int2\":10}\n" +
            "{ \"long1\" : " + long1 + " }\n"
        );

        Map map = in.read();
        assertEquals(new Double("5e10"), map.get("double1"));
        map = in.read();
        assertEquals(new Double("5.1"), map.get("double2"));
        map = in.read();
        assertEquals(new Double("5E10"), map.get("double3"));
        map = in.read();
        assertEquals(int1, map.get("int1"));
        map = in.read();
        assertEquals(Integer.valueOf(10), map.get("int2"));
        map = in.read();
        assertEquals(long1, map.get("long1"));

        assertNull(in.read());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test_readNull() throws IOException {
        JsonReader in = newReader(
            "{\"field1\":null}\n" +
                "{ \"field2\" : null }\n"
            );

        Map map = in.read();
        assertNull(map.get("field1"));
        assertTrue(map.containsKey("field1"));
        map = in.read();
        assertNull(map.get("field2"));
        assertTrue(map.containsKey("field2"));

        assertNull(in.read());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test_readBoolean() throws IOException {
        JsonReader in = newReader(
            "{\"field1\":true}\n" +
                "{ \"field2\" : false }\n"
            );

        Map map = in.read();
        assertEquals(Boolean.TRUE, map.get("field1"));
        map = in.read();
        assertEquals(Boolean.FALSE, map.get("field2"));

        assertNull(in.read());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test_readObject() throws IOException {
        JsonReader in = newReader(
            "{\"o1\":{\"field1\":\"value1\"}, \"field2\":20}" +
                "{ \"o1\" : { \"field1\" : \"value1\" } }" +
                "{ \"o1\" : { \"field1\" : \"value1\", \"field2\" : 10} }"
            );

        Map object;

        Map map = in.read();
        assertEquals(Integer.valueOf(20), map.get("field2"));
        object = (Map) map.get("o1");
        assertEquals("value1", object.get("field1"));

        map = in.read();
        object = (Map) map.get("o1");
        assertEquals("value1", object.get("field1"));

        map = in.read();
        object = (Map) map.get("o1");
        assertEquals("value1", object.get("field1"));
        assertEquals(Integer.valueOf(10), object.get("field2"));

        assertNull(in.read());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test_readArray() throws IOException {
        JsonReader in = newReader(
            "{\"array1\":[1,2,3]}" +
            "{ \"array2\" : [ \"10\" , null , true , { \"field1\" : \"value1\" } ] }"
        );

        Map object;
        List list;

        Map map = in.read();
        list = (List) map.get("array1");
        assertArrayEquals(new Integer[] { 1, 2, 3 }, list.toArray());

        map = in.read();
        list = (List) map.get("array2");
        assertEquals("10", list.get(0));
        assertNull(list.get(1));
        assertEquals(Boolean.TRUE, list.get(2));
        object = (Map) list.get(3);
        assertEquals("value1", object.get("field1"));

        assertNull(in.read());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test_escapedString() throws IOException {
        JsonReader in = newReader(
            "{ \"field1\" : \" \\\\ \\/ \\b \\f \\n \\r \\t \\\" \\u004B \" } "
        );

        Map map = in.read();
        assertEquals(" \\ / \b \f \n \r \t \" K ", map.get("field1"));

        assertNull(in.read());
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void test_mixedArray() {
        JsonRecordUnmarshaller u = new JsonRecordUnmarshaller();
        
        Map map = u.unmarshal(
            "{ \"array\" : [ [10], { \"field\":\"value\" } ]}");
        
        List list = (List) ((List)map.get("array")).get(0);
        assertEquals(Integer.valueOf(10), list.get(0));
        
        map = (Map) ((List)map.get("array")).get(1);
        assertEquals("value", map.get("field"));
    }
    
    @Test
    public void test_missingObject() {
        assertError(null, "Expected '{' near position 1");
        assertError("", "Expected '{' near position 1");
        assertError(" ", "Expected '{' near position 1");
    }
    
    @Test
    public void test_missingQuotes() {
        assertError("{ field : \"value\" }", "Expected string or '}' near position 3");
    }
    
    @Test
    public void test_missingCommaInObject() {
        assertError("{ \"f1\" : \"value\" \"f2\" : \"value2\" }", "Expected ',' or '}' near position 18");
    }

    @Test
    public void test_missingCommaInArray() {
        assertError("{ \"array\" : [ 10 20 ] }", "Expected ',' near position 18");
    }
    
    @Test
    public void test_invalidValue() {
        assertError("{ \"number\" : a }", "Cannot parse 'a' into a JSON string, number or boolean near position 15");
    }

    @Test
    public void test_missingCloseObject() {
        assertError("{ \"number\" : 10", "Expected ',' or '}' near position 15");
    }

    @Test
    public void test_missingCloseArray() {
        assertError("{ \"number\" : [ 10", "Expected ',' or ']' near position 17");
    }
    
    @Test
    public void test_missingCloseString() {
        assertError("{ \"number", "Expected '\"' near position 9");
    }
    
    @Test
    public void test_missingColon() {
        assertError("{ \"number\" 10 }", "Expected ':' near position 12");
    }    
    
    private void assertError(String record, String message) {
        try {
            new JsonRecordUnmarshaller().unmarshal(record);
        }
        catch (RecordIOException ex) {
            assertEquals(message, ex.getMessage());
        }
    }
}
