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

import org.beanio.stream.csv.*;
import org.junit.*;

/**
 * JUnit test cases for the {@link CsvRecordParser}.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class CsvRecordParserTest {

    private CsvRecordParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new CsvRecordParser();
    }

    @Test
    public void testEmptyString() {
        String[] expected = { "" };
        assertArrayEquals(expected, (String[])parser.unmarshal(""));
    }

    @Test
    public void testNewLine() {
        String record = "\n";
        String[] expected = new String[] { "\n" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }

    @Test
    public void testDelimiter() {
        String record = "   1,2,3";
        String[] expected = new String[] { "   1", "2", "3" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }

    @Test
    public void testDelimiterOnly() {
        String record = ",";
        String[] expected = { "", "" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }
    
    @Test
    public void testEscapedDelimiter() {
        String record = "\"1,\",2";
        String[] expected = { "1,", "2" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }    

    @Test
    public void testEscapedQuote() {
        String record = "\"1,\"\"\",2";
        String[] expected = { "1,\"", "2" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }
    
    @Test
    public void testQuotedFields() {
        String record = "\"1\",\"\",\"3\"";
        String[] expected = new String[] { "1", "", "3" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }
    
    @Test(expected = RecordIOException.class)
    public void testCharacaterOutofQuotedField() {
        parser.unmarshal("\"1\",\"\",\"3\"2\n\r1,2");
    }
    
    @Test(expected = RecordIOException.class)
    public void testSpaceOutofQuotedField() {
        parser.unmarshal("\"1\",\"\",\"3\" \n\r1,2");
    }

    @Test(expected = RecordIOException.class)
    public void testUnquotedQuote() {
        parser.unmarshal("1\"1,2,3");
    }
    
    @Test
    public void testCustomDelimiter() {
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setDelimiter('|');
        
        CsvRecordParser parser = new CsvRecordParser(config);
        String record = "\"1\"|2|3";
        String[] expected = new String[] { "1", "2", "3" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }

    @Test
    public void testCustomQuote() {
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setQuote('\'');
        
        CsvRecordParser parser = new CsvRecordParser(config);
        
        String record =  "'1',' 234 ',5\n";
        String[] expected = new String[] { "1", " 234 ", "5\n" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }

    @Test
    public void testCustomEscape() {
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setQuote('\'');
        config.setEscape('\\');
        
        CsvRecordParser parser = new CsvRecordParser(config);
        
        String record = "'1',' \\'23\\\\4\\' ',5\\\\";
        String[] expected = { "1", " '23\\4' ", "5\\\\" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }

    @Test
    public void testWhitespaceeAllowed() {
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setQuote('\'');
        config.setWhitespaceAllowed(true);
        
        CsvRecordParser parser = new CsvRecordParser(config);
        
        String record = " '1' , '2'  ";
        String[] expected = { "1", "2" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }

    @Test
    public void testEscapeDisabled() {
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setEscape(null);
        config.setQuote('\'');
        
        CsvRecordParser parser = new CsvRecordParser(config);
        
        String record = "'1\"','2'";
        String[] expected = new String[] { "1\"", "2" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }

    @Test
    public void testUnquotedQuoteAllowed() {
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setQuote('\'');
        config.setUnquotedQuotesAllowed(true);
        
        CsvRecordParser parser = new CsvRecordParser(config);
        
        String record = "1\"1,2";
        String[] expected = new String[] { "1\"1", "2" };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }

    @Test(expected = RecordIOException.class)
    public void testMissingQuoteEOF() {
        parser.unmarshal("field1,\"field2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQuoteIsDelimiter() {
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setDelimiter(',');
        config.setQuote(',');
        
        new CsvRecordParser(config);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testQuoteIsEscape() {
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setDelimiter(',');
        config.setEscape(',');
        
        new CsvRecordParser(config);
    }
    
    @Test
    public void testCreateWhitespace() {
        String record = "   1,2,  3  ";
        String[] expected = new String[] { "   1", "2", "  3  " };
        assertArrayEquals(expected, (String[])parser.unmarshal(record));
    }
    
    @Test
    public void testMarshal_DefaultConfiguration() {
        CsvRecordParserFactory factory = new CsvRecordParserFactory();
        CsvRecordParser parser = (CsvRecordParser) factory.createMarshaller();
        assertEquals("value1,\"\"\"value2\"\"\",\"value,3\"",
            parser.marshal(new String[] { "value1", "\"value2\"", "value,3" }));
    }
    
    @Test
    public void testMarshal_CustomConfiguration() {
        CsvRecordParserFactory factory = new CsvRecordParserFactory();
        factory.setDelimiter(':');
        factory.setQuote('\'');
        factory.setEscape('\\');
        factory.setRecordTerminator("");
        
        CsvRecordParser parser = (CsvRecordParser) factory.createMarshaller();
        assertEquals("value1:'\\'value2\\'':'value:3'", 
            parser.marshal(new String[] { "value1", "'value2'", "value:3" }));
    }

    @Test
    public void testMarshal_AlwaysQuote() {
        CsvRecordParserFactory factory = new CsvRecordParserFactory();
        factory.setQuote('\'');
        factory.setEscape('\\');
        factory.setRecordTerminator("");
        factory.setAlwaysQuote(true);
        
        CsvRecordParser parser = (CsvRecordParser) factory.createMarshaller();
        assertEquals("'value1','\\'value2\\'','value,3'", 
            parser.marshal(new String[] { "value1", "'value2'", "value,3" }));
    }
}
