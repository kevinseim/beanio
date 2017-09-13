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
package org.beanio.stream;

import static org.junit.Assert.*;

import java.io.*;

import org.beanio.stream.delimited.*;
import org.junit.*;

/**
 * JUnit test cases for {@link DelimitedRecordParser}.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class DelimitedRecordParserTest {

    private DelimitedRecordParserFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new DelimitedRecordParserFactory();
    }
    
    @Test
    public void testUnmarshal_Basic() {
        RecordUnmarshaller unmarshaller = factory.createUnmarshaller();
        String[] actual = (String[]) unmarshaller.unmarshal("1\t2\t33\t444\t");
        
        String[] expected = new String[] { "1", "2", "33", "444", "" };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testUnmarshal_EscapeDisabled() {
        factory.setEscape(null);
        
        RecordUnmarshaller unmarshaller = factory.createUnmarshaller();
        String[] actual = (String[]) unmarshaller.unmarshal("1\\\\\t2");
        
        assertArrayEquals(new String [] { "1\\\\", "2" }, actual);
    }

    @Test
    public void testUnmarshal_EscapeEscape() {
        factory.setEscape('\\');
        
        RecordUnmarshaller unmarshaller = factory.createUnmarshaller();
        String[] actual = (String[]) unmarshaller.unmarshal("1\\\\\t2");
        
        assertArrayEquals(new String[] { "1\\", "2" }, actual);
    }

    @Test
    public void testUnmarshal_EscapeDelimiter() {
        factory.setEscape('\\');
        
        RecordUnmarshaller unmarshaller = factory.createUnmarshaller();
        String[] actual = (String[]) unmarshaller.unmarshal("1\\\t\t2\\");
        
        assertArrayEquals(new String[] { "1\t", "2\\" }, actual);
    }

    @Test
    public void testUnmarshal_EscapeOther() {
        factory.setEscape('\\');
        
        RecordUnmarshaller unmarshaller = factory.createUnmarshaller();
        String[] actual = (String[]) unmarshaller.unmarshal("1\t2\\2");
        
        assertArrayEquals(new String[] { "1", "2\\2" }, actual);
    }

    @Test
    public void testUnmarshal_CustomDelimiter() {
        factory.setDelimiter(',');
        
        RecordUnmarshaller unmarshaller = factory.createUnmarshaller();
        String[] actual = (String[]) unmarshaller.unmarshal("1,2,\t3");
        
        assertArrayEquals(new String[] { "1", "2", "\t3" }, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshal_DelimiterIsEscape() {
        factory.setDelimiter(',');
        factory.setEscape(',');
        factory.createUnmarshaller();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMarshal_DelimiterIsEscape() {
        factory.setDelimiter(',');
        factory.setEscape(',');
        factory.createMarshaller();
    }

    @Test
    public void testMarshal_DefaultConfiguration() {
        RecordMarshaller marshaller = factory.createMarshaller();
        String record = marshaller.marshal(new String[] { "value1", "value\t2" });
        assertEquals("value1\tvalue\t2", record);
    }

    @Test
    public void testMarshal_CustomDelimiter() {
        factory.setDelimiter(',');
        
        RecordMarshaller marshaller = factory.createMarshaller();
        String record = marshaller.marshal(new String[] { "value1", "value2\t", "" });
        assertEquals("value1,value2\t,", record);
    }

    @Test
    public void testMarshal_CustomDelimiterAndEscape() {
        factory.setDelimiter(',');
        factory.setEscape('\\');
        
        RecordMarshaller marshaller = factory.createMarshaller();
        String record = marshaller.marshal(new String[] { "value1", "value2," });
        assertEquals("value1,value2\\,", record);
    }

    @Test
    public void testMarshal_DefaultFactoryConfiguration() {
        RecordMarshaller marshaller = factory.createMarshaller();
        String record = marshaller.marshal(new String[] { "value1", "value\t2" });
        assertEquals("value1\tvalue\t2", record);
    }

    @Test
    public void testMarshal_CustomFactoryConfiguration() {
        factory.setDelimiter(',');
        factory.setEscape('\\');
        factory.setRecordTerminator("");
        
        RecordMarshaller marshaller = factory.createMarshaller();
        String record = marshaller.marshal(new String[] { "value1", "value,2" });
        assertEquals("value1,value\\,2", record);
    }
}
