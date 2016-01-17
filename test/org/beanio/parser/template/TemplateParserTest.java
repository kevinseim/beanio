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
package org.beanio.parser.template;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for testing mapping file templates.
 * 
 * @author Kevin Seim
 * @since 1.2.1
 */
public class TemplateParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("template_mapping.xml");
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testRecordTemplate() {
        BeanReader in = factory.createReader("stream1", new InputStreamReader(
            getClass().getResourceAsStream("t1.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals(Integer.valueOf(1), map.get("id"));
            assertEquals("joe", map.get("name"));
            assertEquals('M', map.get("gender"));
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testBeanTemplate() {
        BeanReader in = factory.createReader("stream2", new InputStreamReader(
            getClass().getResourceAsStream("t1.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals('M', map.get("gender"));
            assertNotNull(map.get("bean"));
            map = (Map) map.get("bean");
            assertEquals(Integer.valueOf(1), map.get("id"));
            assertEquals("joe", map.get("name"));
            
        }
        finally {
            in.close();
        }
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testIncludeTemplateFromRecord() {
        BeanReader in = factory.createReader("stream3", new InputStreamReader(
            getClass().getResourceAsStream("t3.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals(Integer.valueOf(1), map.get("id"));
            assertEquals("joe", map.get("firstName"));
            assertEquals("smith", map.get("lastName"));
            assertEquals('M', map.get("gender"));
        }
        finally {
            in.close();
        }
    }
}
