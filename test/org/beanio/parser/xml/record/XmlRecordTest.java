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
package org.beanio.parser.xml.record;

import static org.junit.Assert.*;

import java.io.*;
import java.util.List;

import org.beanio.*;
import org.beanio.parser.xml.XmlParserTest;
import org.junit.*;

/**
 * JUnit test cases for testing XML record components.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlRecordTest extends XmlParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("record_mapping.xml");
    }

    /**
     * Test a nillable child bean.
     */
    @Test
    @SuppressWarnings("rawtypes")
    public void testRecordClassIsCollection() throws Exception {
        BeanReader in = factory.createReader("stream", new InputStreamReader(
            getClass().getResourceAsStream("r1_in.xml")));

        StringWriter s = new StringWriter();
        BeanWriter out = factory.createWriter("stream", s);

        try {
            List list = (List) in.read();
            assertEquals("John", list.get(0));
            assertNull(list.get(1));
            assertEquals(Integer.valueOf(22), list.get(2));

            out.write(list);
            out.close();
            assertEquals(load("r1_in.xml"), s.toString());
        }
        finally {
            in.close();
        }
    }

}
