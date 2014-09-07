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
package org.beanio.parser.direct;

import static org.junit.Assert.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for direct access to public class attributes.
 * 
 * @author Kevin Seim
 * @since 2.0.2
 */
public class DirectParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("direct_mapping.xml");
    }
    
    @Test
    public void testDirectAccess() {
        Unmarshaller u = factory.createUnmarshaller("d1");
        DirectUser p = (DirectUser) u.unmarshal("george,true");
        
        assertEquals("george", p.firstName);
        assertTrue(p.enabled);
    }
    
}
