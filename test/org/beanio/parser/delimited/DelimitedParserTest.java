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
package org.beanio.parser.delimited;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.*;
import java.util.Map;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for delimited streams.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class DelimitedParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("delimited.xml");
    }
    
    @Test
    public void testRequiredField() {
        BeanReader in = factory.createReader("d1", new InputStreamReader(
            getClass().getResourceAsStream("d1_recordErrors.txt")));
        
        try {
            assertRecordError(in, 1, "record1", "Too few fields 2");
            assertRecordError(in, 2, "record1", "Too many fields 4");
            assertFieldError(in, 3, "record1", "field4", null, "Expected minimum 1 occurrences");
            assertFieldError(in, 4, "record1", "field4", "", "Required field not set");
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testOptionalField() {
        BeanReader in = factory.createReader("d2", new InputStreamReader(
            getClass().getResourceAsStream("d2_optionalField.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals("value1", map.get("field1"));
            assertEquals("value2", map.get("field2"));
            assertNull(map.get("field3"));
            assertNull(map.get("field4"));
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testPadding() {
        BeanReader in = factory.createReader("d3", new InputStreamReader(
            getClass().getResourceAsStream("d3_padding.txt")));
        
        try {
            Map map = (Map) in.read();
            assertArrayEquals(new String[] { "1", "2", "3", "" }, (String[]) map.get("field1"));
            
            StringWriter out = new StringWriter();
            factory.createWriter("d3", out).write(map);
            assertEquals("xx1\txx2\txx3\txxx~", out.toString());
        }
        finally {
            in.close();
        }
    }
}
