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
package org.beanio.parser.skip;

import static org.junit.Assert.assertEquals;

import java.io.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for testing BeanReader skipping logic.
 * @author Kevin Seim
 * @since 1.2
 */
public class SkippingParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("skip_mapping.xml");
    }
    
    @Test
    public void testSkip() {
        BeanReader in = factory.createReader("s1", new InputStreamReader(
            getClass().getResourceAsStream("s1.txt")));
        
        try {
            int count = in.skip(0);
            assertEquals(count, 0);
            
            count = in.skip(4);
            assertEquals(count, 4);
            
            in.read();
            assertEquals("Detail", in.getRecordName());
            in.read();
            assertEquals("Trailer", in.getRecordName());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testSkipPastEOF() {
        BeanReader in = factory.createReader("s1", new InputStreamReader(
            getClass().getResourceAsStream("s1.txt")));
        
        try {
            int count = in.skip(10);
            assertEquals(count, 6);
        }
        finally {
            in.close();
        }
    }
}
