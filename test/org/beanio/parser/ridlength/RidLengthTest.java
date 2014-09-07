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
package org.beanio.parser.ridlength;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for identifying records based on length.
 * @author Kevin Seim
 * @since 2.0.3
 */
public class RidLengthTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("ridlength_mapping.xml");
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testRidLength() {
        BeanReader in = factory.createReader("r1", new InputStreamReader(
            getClass().getResourceAsStream("r1.txt")));
        
        try {
            Map map = (Map) in.read();
            assertEquals("acouple", in.getRecordName());
            assertEquals(Arrays.asList(1, 2), map.get("values"));
            
            map = (Map) in.read();
            assertEquals("afew", in.getRecordName());
            assertEquals(Arrays.asList(1, 2, 3), map.get("values"));
            
            map = (Map) in.read();
            assertEquals("acouple", in.getRecordName());
            
            map = (Map) in.read();
            assertEquals("afew", in.getRecordName());
        }
        finally {
            in.close();
        }
    }
}
