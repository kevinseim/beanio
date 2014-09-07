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
package org.beanio.parser.imports;

import static org.junit.Assert.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for imported mapping files.
 * 
 * @author Kevin Seim
 * @since 1.2.1
 */
public class ImportParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("import_mapping1.xml");
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void testImportHierarchy() throws Exception {
        BeanReader in;
        Map map;
        
        in = factory.createReader("stream1.1", new StringReader("Joe ,1970-01-01"));
        map = (Map) in.read();
        assertEquals("Joe", map.get("name"));
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("1970-01-01"),
            map.get("date"));
        
        in = factory.createReader("stream2.1", new StringReader("Joe ,01021970"));
        map = (Map) in.read();
        assertEquals("Joe ", map.get("name"));
        assertEquals(new SimpleDateFormat("MMddyyyy").parse("01021970"),
            map.get("date"));

        in = factory.createReader("stream3.1", new StringReader("Joe,01021970"));
        map = (Map) in.read();
        assertEquals(new SimpleDateFormat("MMddyyyy").parse("01021970"),
            map.get("date"));
    }
    
    @Test
    public void testCircularReference() throws Exception {
        try {
            newStreamFactory("circular_mapping1.xml");
            fail("BeanIOConfigurationException expected");
        }
        catch (BeanIOConfigurationException ex) {
            assertEquals(
        		"Invalid mapping file 'classpath:org/beanio/parser/imports/circular_mapping1.xml': " +
        		"Failed to import resource 'classpath:org/beanio/parser/imports/circular_mapping2.xml': " +
        		"Circular reference(s) detected", ex.getMessage());
        }
    }
}
