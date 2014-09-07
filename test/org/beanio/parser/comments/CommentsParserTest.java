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
package org.beanio.parser.comments;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.util.Map;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for testing commented lines.
 * @author Kevin Seim
 * @since 1.2
 */
public class CommentsParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("comments_mapping.xml");
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testCsvComments() {
        BeanReader in = factory.createReader("c1", new BufferedReader(new InputStreamReader(
            getClass().getResourceAsStream("c1.txt"))));
        
        try {
            Map map = (Map) in.read();
            assertEquals("joe", map.get("name"));
            assertEquals("25", map.get("age"));
            
            map = (Map) in.read();
            assertEquals("john", map.get("name"));
            assertEquals("42", map.get("age"));

            map = (Map) in.read();
            assertEquals("mary", map.get("name"));
            assertEquals("33", map.get("age"));
        }
        finally {
            in.close();
        }
    }
    
}
