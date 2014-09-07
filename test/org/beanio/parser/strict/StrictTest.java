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
package org.beanio.parser.strict;

import static org.junit.Assert.*;

import java.io.InputStreamReader;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for testing the <tt>strict</tt> attribute of the <tt>stream</tt> element.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class StrictTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("strict_mapping.xml");
    }

    @Test
    public void testRecordLength_Strict() {
        BeanReader in = factory.createReader("s1_strict", new InputStreamReader(
            getClass().getResourceAsStream("s1_invalidRecordLength.txt")));
        
        try {
            assertRecordError(in, 3, "detail", "Too many fields, expected 3 maximum");
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testRecordLength_NotStrict() {
        BeanReader in = factory.createReader("s1_not_strict", new InputStreamReader(
            getClass().getResourceAsStream("s1_invalidRecordLength.txt")));
        
        try {
            in.read();
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testRecordSequence_Strict() {
        BeanReader in = factory.createReader("s1_strict", new InputStreamReader(
            getClass().getResourceAsStream("s1_invalidSequence.txt")));
        
        try {
            in.read();
            assertRecordError(in, 1, "detail", "Too many fields, expected 3 maximum");
            fail("UnexpectedRecordException expected");
        }
        catch (UnexpectedRecordException ex) {
            RecordContext ctx = ex.getRecordContext();
            assertNotNull(ctx);
            assertEquals(1, ctx.getLineNumber());
            assertEquals("detail", ctx.getRecordName());
            assertEquals("Unexpected 'detail' record at line 1", ctx.getRecordErrors().iterator().next());
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void testRecordSequence_NotStrict() {
        BeanReader in = factory.createReader("s1_not_strict", new InputStreamReader(
            getClass().getResourceAsStream("s1_invalidSequence.txt")));
        
        try {
            in.read();
        }
        finally {
            in.close();
        }
    }
}
