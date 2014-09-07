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

import org.beanio.stream.csv.*;
import org.junit.Test;

/**
 * JUnit test cases for testing the <tt>CsvWriter</tt> and <tt>CsvRecordParserFactory</tt>.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class CsvWriterTest {

    private static final String lineSep = System.getProperty("line.separator");

    @Test
    public void testDefaultConfiguration() throws IOException {
        CsvRecordParserFactory factory = new CsvRecordParserFactory();
        StringWriter text = new StringWriter();
        RecordWriter out = factory.createWriter(text);
        out.write(new String[] { "value1", "\"value2\"", "value,3" });
        assertEquals("value1,\"\"\"value2\"\"\",\"value,3\"" + lineSep, text.toString());
    }

    @Test
    public void testCustomConfiguration() throws IOException {
        CsvRecordParserFactory factory = new CsvRecordParserFactory();
        factory.setDelimiter(':');
        factory.setQuote('\'');
        factory.setEscape('\\');
        factory.setRecordTerminator("");
        StringWriter text = new StringWriter();
        RecordWriter out = factory.createWriter(text);
        out.write(new String[] { "value1", "'value2'", "value:3" });
        assertEquals("value1:'\\'value2\\'':'value:3'", text.toString());
    }

    @Test
    public void testAlwaysQuote() throws IOException {
        CsvRecordParserFactory factory = new CsvRecordParserFactory();
        factory.setQuote('\'');
        factory.setEscape('\\');
        factory.setRecordTerminator("");
        factory.setAlwaysQuote(true);
        StringWriter text = new StringWriter();
        RecordWriter out = factory.createWriter(text);
        out.write(new String[] { "value1", "'value2'", "value,3" });
        assertEquals("'value1','\\'value2\\'','value,3'", text.toString());
    }

    @Test
    public void testMultiline() throws IOException {
        CsvRecordParserFactory factory = new CsvRecordParserFactory();
        factory.setQuote('\'');
        factory.setEscape('\\');
        factory.setRecordTerminator("");
        StringWriter text = new StringWriter();
        CsvWriter out = (CsvWriter) factory.createWriter(text);
        out.write(new String[] { "value1", "value\n2", "value\r3", "value\r\n4" });
        assertEquals("value1,'value\n2','value\r3','value\r\n4'", text.toString());
        out.write(new String[] { "value1", "value2" });
        assertEquals(5, out.getLineNumber());
    }

    @Test
    public void testFlushAndClose() throws IOException {
        CsvRecordParserFactory factory = new CsvRecordParserFactory();
        factory.setQuote('\'');
        factory.setEscape('\\');
        factory.setRecordTerminator("");
        StringWriter text = new StringWriter();
        RecordWriter out = factory.createWriter(new BufferedWriter(text));
        out.write(new String[] { "v" });
        out.flush();
        assertEquals("v", text.toString());
        out.close();
    }
}
