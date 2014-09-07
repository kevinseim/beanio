package org.beanio.stream;

import static org.junit.Assert.*;

import java.io.*;

import org.beanio.stream.csv.*;
import org.junit.*;

/**
 * JUnit test cases for the <tt>CsvReader</tt> and <tt>CsvReaderFactory</tt>.
 * 
 * @author Kevin Seim
 */
public class CsvReaderTest {

    private CsvRecordParserFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new CsvRecordParserFactory();
    }

    @After
    public void tearDown() throws Exception {
        factory = null;
    }

    @Test
    public void testEmptyFile() throws IOException {
        CsvReader in = createReader("");
        assertNull(in.read());
    }

    @Test
    public void testNewLine() throws IOException {
        String[] expected = new String[] { "" };
        CsvReader in = createReader("\n");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testDelimiter() throws IOException {
        String[] expected = new String[] { "   1", "2", "3" };
        CsvReader in = createReader("   1,2,3");
        assertArrayEquals(expected, in.read());
        assertNull(in.read());
    }

    @Test
    public void testDelimiterOnly() throws IOException {
        String[] expected = new String[] { "", "", "" };
        CsvReader in = createReader(",,\n");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());

        expected = new String[] { "", "" };
        in = createReader(",");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testEscapedDelimiter() throws IOException {
        String[] expected = new String[] { "1,", "2" };
        CsvReader in = createReader("\"1,\",2");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testEscapedQuote() throws IOException {
        String[] expected = new String[] { "1,\"", "2" };
        CsvReader in = createReader("\"1,\"\"\",2");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testQuotedFields() throws IOException {
        String[] expected = new String[] { "1", "", "3" };
        CsvReader in = createReader("\"1\",\"\",\"3\"");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test(expected = RecordIOException.class)
    public void testCharacaterOutofQuotedField() throws IOException {
        createReader("\"1\",\"\",\"3\"2\n\r1,2").read();
    }

    @Test(expected = RecordIOException.class)
    public void testSpaceOutofQuotedField() throws IOException {
        createReader("\"1\",\"\",\"3\" \n\r1,2").read();
    }

    @Test(expected = RecordIOException.class)
    public void testUnquotedQuote() throws IOException {
        createReader("1\"1,2,3").read();
    }

    @Test
    public void testRecover() throws IOException {
        String[] expected = new String[] { "", "2" };
        CsvReader in = createReader("\"1\",\"\",\"3\" 2\n,2");
        try {
            in.read();
        } catch (RecordIOException ex) {
        }
        assertArrayEquals(in.read(), expected);
    }

    @Test
    public void testCRLF() throws IOException {
        String[] expected = new String[] { "4", "5", "6" };
        CsvReader in = createReader("1,2,3\r\n4,5,6\r\n");
        in.read();
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testCR() throws IOException {
        String[] expected = new String[] { "4", "5", "6" };
        CsvReader in = createReader("1,2,3\r4,5,6\r");
        in.read();
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testLF() throws IOException {
        String[] expected = new String[] { "4", "5", "6" };
        CsvReader in = createReader("1,2,3\n4,5,6\n");
        in.read();
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testCustomDelimiter() throws IOException {
        factory.setDelimiter('|');
        String[] expected = new String[] { "1", "2", "3" };
        CsvReader in = createReader(factory, "\"1\"|2|3");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testCustomQuote() throws IOException {
        factory.setQuote('\'');
        String[] expected = new String[] { "1", " 234 ", "5" };
        CsvReader in = createReader(factory, "'1',' 234 ',5\n");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testCustomEscape() throws IOException {
        factory.setQuote('\'');
        factory.setEscape('\\');
        String[] expected = new String[] { "1", " '23\\4' ", "5\\\\" };
        CsvReader in = createReader(factory, "'1',' \\'23\\\\4\\' ',5\\\\\n");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testMultiline() throws IOException {
        factory.setQuote('\'');
        factory.setMultilineEnabled(true);
        String[] expected = new String[] { "12\n3", "4\r\n5" };
        CsvReader in = createReader(factory, "'12\n3','4\r\n5'\n'6',7");
        assertArrayEquals(in.read(), expected);
        assertEquals(1, in.getRecordLineNumber());
        assertArrayEquals(in.read(), new String[] { "6", "7" });
        assertEquals(4, in.getRecordLineNumber());
        assertNull(in.read());
    }

    @Test
    public void testWhitespaceeAllowed() throws IOException {
        factory.setQuote('\'');
        factory.setWhitespaceAllowed(true);
        String[] expected = new String[] { "1", "2" };
        CsvReader in = createReader(factory, " '1' , '2'  \n");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testEscapeDisabled() throws IOException {
        factory.setEscape(null);
        factory.setQuote('\'');
        String[] expected = new String[] { "1\"", "2" };
        CsvReader in = createReader(factory, "'1\"','2'\n");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test
    public void testUnquotedQuoteAllowed() throws IOException {
        factory.setQuote('\'');
        factory.setUnquotedQuotesAllowed(true);
        String[] expected = new String[] { "1\"1", "2" };
        CsvReader in = createReader(factory, "1\"1,2");
        assertArrayEquals(in.read(), expected);
        assertNull(in.read());
    }

    @Test(expected = RecordIOException.class)
    public void testMissingQuoteEOF() throws IOException {
        StringReader text = new StringReader("field1,\"field2");
        
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setDelimiter(',');
        config.setEscape(null);
        config.setMultilineEnabled(false);
        
        new CsvReader(text, config).read();
    }

    @Test
    public void testMissingQuoteEOL() throws IOException {
        StringReader text = new StringReader("field1,\"field2\nfield1");
        
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setDelimiter(',');
        config.setEscape(null);
        config.setMultilineEnabled(false);
        
        CsvReader in = new CsvReader(text, config);
        try {
            in.read();
            fail("Expected RecordIOException");
        } catch (RecordIOException ex) {
        }
        assertArrayEquals(new String[] { "field1" }, in.read());
        assertEquals(2, in.getRecordLineNumber());
    }

    @Test
    public void testRecoverSkipLF() throws IOException {
        StringReader text = new StringReader(
                "field1,\"field2\" ,field3\r\nfield1");
        
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setDelimiter(',');
        config.setEscape(null);
        config.setMultilineEnabled(false);
        CsvReader in = new CsvReader(text, config);
        try {
            in.read();
            fail("Expected RecordIOException");
        } catch (RecordIOException ex) {
        }
        assertArrayEquals(new String[] { "field1" }, in.read());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQuoteIsDelimiter() {
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setDelimiter(',');
        config.setQuote(',');
        
        new CsvReader(new StringReader(""), config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQuoteIsEscape() {
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setDelimiter(',');
        config.setEscape(',');
        
        new CsvReader(new StringReader(""), config);
    }

    @Test
    public void testCreateWhitespace() throws IOException {
        factory.setWhitespaceAllowed(true);
        factory.setQuote('\'');
        String[] expected = new String[] { "   1", "2", "  3  " };
        CsvReader in = createReader(factory, "   1,2,  3  ");
        assertArrayEquals(expected, in.read());
        assertEquals(1, in.getRecordLineNumber());
        assertNull(in.read());
    }
    
    @Test
    public void testComments() throws IOException {
        String[] comments = new String[] { "#", "$$", "--" };
        
        CsvParserConfiguration config = new CsvParserConfiguration();
        config.setComments(comments);
        
        StringReader input = new StringReader(
            "# Comment\n" +
            "1\r\n" +
            "-1\r\n" +
            "--Comment\r\n" +
            "2\n" +
            "#");
        
        CsvReader in = new CsvReader(input, config);
        assertArrayEquals(new String[] { "1" }, in.read());
        assertEquals(2, in.getRecordLineNumber());
        assertArrayEquals(new String[] { "-1" }, in.read());
        assertEquals(3, in.getRecordLineNumber());        
        assertArrayEquals(new String[] { "2" }, in.read());
        assertEquals(5, in.getRecordLineNumber());
        assertNull(in.read());
    }

    @Test
    public void testMalformedRecordAtEOF() throws IOException {
        
        StrictStringReader input = new StrictStringReader("\"hello,ma");
        
        RecordIOException error = null;
        
        CsvReader in = new CsvReader(input);
        try {
            in.read();
        }
        catch (RecordIOException ex) {
            error = ex;
        }
        
        Assert.assertNotNull(error);
        Assert.assertNull(in.read());
    }
    
    @SuppressWarnings("unused")
    private void print(String[] sa) {
        for (String s : sa) {
            System.out.println(s);
        }
    }

    private CsvReader createReader(CsvRecordParserFactory factory, String input) {
        return (CsvReader) factory.createReader(createInput(input));
    }

    private CsvReader createReader(String input) {
        return (CsvReader) factory.createReader(createInput(input));
    }

    private Reader createInput(String s) {
        return new StringReader(s);
    }

}
