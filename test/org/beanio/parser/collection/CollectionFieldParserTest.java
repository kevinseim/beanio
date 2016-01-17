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
package org.beanio.parser.collection;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for collection type fields.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class CollectionFieldParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("collection.xml");
    }
    
    @Test
    public void testCollectionDelimited() {
        BeanReader in = factory.createReader("dc1", new InputStreamReader(
            getClass().getResourceAsStream("dc1_valid.txt")));
        
        try {
            CollectionBean bean = (CollectionBean) in.read();
            assertEquals(Arrays.asList("George", "Gary", "Jon"), bean.getList());
            assertArrayEquals(new int[] { 1, 2, 3, 4 }, bean.getArray());
            
            StringWriter text = new StringWriter();
            factory.createWriter("dc1", text).write(bean);
            assertEquals("George,Gary,Jon,1,2,3,4" + lineSeparator, text.toString());
            
            bean = (CollectionBean) in.read();
            assertEquals(Arrays.asList("George", "Gary", "Jon"), bean.getList());
            assertArrayEquals(new int[0], bean.getArray());
            
            text = new StringWriter();
            factory.createWriter("dc1", text).write(bean);
            assertEquals("George,Gary,Jon" + lineSeparator, text.toString());
            
            assertFieldError(in, 3, "record1", "list", 2, null, "Expected minimum 3 occurrences");
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testNullPrimiitve() {
        BeanReader in = factory.createReader("dc2", new InputStreamReader(
            getClass().getResourceAsStream("dc2_nullPrimitive.txt")));
        
        try {
            CollectionBean bean = (CollectionBean) in.read();
            assertArrayEquals(new int[] { 1, 0, 3 }, bean.getArray());
            
            StringWriter text = new StringWriter();
            factory.createWriter("dc2", text).write(new CollectionBean());
            assertEquals(",," + lineSeparator, text.toString());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testCollectionFixedLength() {
        BeanReader in = factory.createReader("fc1", new InputStreamReader(
            getClass().getResourceAsStream("fc1_valid.txt")));
        
        try {
            CollectionBean bean = (CollectionBean) in.read();
            assertArrayEquals(new int[] { 1, 100, 24 }, bean.getArray());
            assertEquals(new HashSet<>(Arrays.asList('A', 'B', 'C', ' ')), bean.getSet());

            StringWriter text = new StringWriter();
            factory.createWriter("fc1", text).write(bean);
            assertEquals("001100024ABC " + lineSeparator, text.toString());
            
            bean = (CollectionBean) in.read();
            assertArrayEquals(new int[] { 0, 400, 500 }, bean.getArray());
            assertEquals(new HashSet<Character>(), bean.getSet());
            
            text = new StringWriter();
            factory.createWriter("fc1", text).write(bean);
            assertEquals("000400500" + lineSeparator, text.toString());
            
            text = new StringWriter();
            factory.createWriter("fc1", text).write(new CollectionBean());
            assertEquals("000000000" + lineSeparator, text.toString());
        }
        finally {
            in.close();
        }
    }
}
