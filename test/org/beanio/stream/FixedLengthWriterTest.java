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
package org.beanio.stream;

import static org.junit.Assert.assertEquals;

import java.io.*;

import org.beanio.stream.fixedlength.*;
import org.junit.Test;

/**
 * JUnit test cases for testing the <tt>FixedLengthWriter</tt> and <tt>FixedLengthRecordParserFactory</tt>.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class FixedLengthWriterTest {

    private static final String lineSep = System.getProperty("line.separator");

    @Test
    public void testDefaultConfiguration() throws IOException {
        StringWriter text = new StringWriter();
        FixedLengthWriter out = new FixedLengthWriter(text);
        out.write("value1  value2");
        assertEquals("value1  value2" + lineSep, text.toString());
    }
    
    @Test
    public void testDefaultFactoryConfiguration() throws IOException {
        FixedLengthRecordParserFactory factory = new FixedLengthRecordParserFactory();
        StringWriter text = new StringWriter();
        RecordWriter out = factory.createWriter(text);
        out.write("value1  value2");
        assertEquals("value1  value2" + lineSep, text.toString());
    }

    @Test
    public void testCustomFactoryConfiguration() throws IOException {
        FixedLengthRecordParserFactory factory = new FixedLengthRecordParserFactory();
        factory.setRecordTerminator("");
        StringWriter text = new StringWriter();
        RecordWriter out = factory.createWriter(text);
        out.write("value1  value2");
        assertEquals("value1  value2", text.toString());
    }

    @Test
    public void testFlushAndClose() throws IOException {
        FixedLengthRecordParserFactory factory = new FixedLengthRecordParserFactory();
        factory.setRecordTerminator("");
        StringWriter text = new StringWriter();
        RecordWriter out = factory.createWriter(new BufferedWriter(text));
        out.write("v");
        out.flush();
        assertEquals("v", text.toString());
        out.close();
    }
}
