package org.beanio.stream;

import static org.junit.Assert.*;

import java.io.*;

import org.beanio.stream.delimited.*;
import org.junit.*;

/**
 * JUnit test cases for <tt>DelimitedReader</tt> and <tt>DelimitedRecordParserFactory</tt>.
 * 
 * @author Kevin Seim
 */
public class DelimitedReaderTest {

    @Test
    public void testBasic() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        String[] expected = new String[] { "1", "2", "33", "444", "" };
        DelimitedReader in = createReader(factory, "1\t2\t33\t444\t\n");
        assertArrayEquals(expected, in.read());
        assertNull(in.read());
    }

    @Test
    public void testEscapeDisabled() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setEscape(null);
        DelimitedReader in = createReader(factory, "1\\\\\t2");
        assertArrayEquals(new String[] { "1\\\\", "2" }, in.read());
        assertNull(in.read());
    }

    @Test
    public void testEscapeEscape() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setEscape('\\');
        DelimitedReader in = createReader(factory, "1\\\\\t2");
        assertArrayEquals(new String[] { "1\\", "2" }, in.read());
        assertNull(in.read());
    }

    @Test
    public void testEscapeDelimiter() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setEscape('\\');
        DelimitedReader in = createReader(factory, "1\\\t\t2\\");
        assertArrayEquals(new String[] { "1\t", "2\\" }, in.read());
        assertNull(in.read());
    }

    @Test
    public void testEscapeOther() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setEscape('\\');
        DelimitedReader in = createReader(factory, "1\t2\\2");
        assertArrayEquals(new String[] { "1", "2\\2" }, in.read());
        assertNull(in.read());
    }

    @Test
    public void testCustomDelimiter() throws IOException {
        DelimitedReader in = new DelimitedReader(new StringReader("1,2,\t3"), ',');
        assertArrayEquals(new String[] { "1", "2", "\t3" }, in.read());
        assertNull(in.read());
    }

    @Test
    public void testLineContinuation() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        DelimitedReader in = createReader(factory, "1,2,\\\n3,4");
        assertArrayEquals(new String[] { "1", "2", "3", "4" }, in.read());
        assertEquals(in.getRecordLineNumber(), 1);
        assertNull(in.read());
    }

    @Test
    public void testLineContinuationCRLF() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        DelimitedReader in = createReader(factory, "1,2,\\\r\n3,4");
        assertArrayEquals(new String[] { "1", "2", "3", "4" }, in.read());
        assertEquals(in.getRecordLineNumber(), 1);
        assertNull(in.read());
    }

    @Test
    public void testLineContinuationIgnored() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        DelimitedReader in = createReader(factory, "1,2,\\3,4");
        assertArrayEquals(new String[] { "1", "2", "\\3", "4" }, in.read());
        assertEquals(in.getRecordLineNumber(), 1);
        assertNull(in.read());
    }

    @Test
    public void testLineContinuationAndEscape() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        factory.setEscape('\\');
        DelimitedReader in = createReader(factory, "1,2,\\3,4");
        assertArrayEquals(new String[] { "1", "2", "\\3", "4" }, in.read());
        assertEquals(in.getRecordLineNumber(), 1);
        assertNull(in.read());
    }

    @Test
    public void testLineNumber() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        DelimitedReader in = createReader(factory, "1,2,\\\n3,4\n5,6");
        assertArrayEquals(new String[] { "1", "2", "3", "4" }, in.read());
        assertEquals("1,2,\\\n3,4", in.getRecordText());
        assertEquals(1, in.getRecordLineNumber());
        assertArrayEquals(new String[] { "5", "6" }, in.read());
        assertEquals(3, in.getRecordLineNumber());
        assertNull(in.read());
    }

    @Test(expected = RecordIOException.class)
    public void testLineContinuationError() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        DelimitedReader in = createReader(factory, "1,2,\\");
        in.read();
    }

    @Test
    public void testCustomLineContinuation() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('#');
        DelimitedReader in = createReader(factory, "1,2,#\n3,4");
        assertArrayEquals(in.read(), new String[] { "1", "2", "3", "4" });
        assertNull(in.read());
    }

    @Test
    public void testLF() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        DelimitedReader in = createReader(factory, "1\t2\n3\t4");
        assertArrayEquals(in.read(), new String[] { "1", "2" });
        assertArrayEquals(in.read(), new String[] { "3", "4" });
        assertNull(in.read());
    }

    @Test
    public void testCRLF() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        DelimitedReader in = createReader(factory, "1\t2\r\n3\t4");
        assertArrayEquals(in.read(), new String[] { "1", "2" });
        assertArrayEquals(in.read(), new String[] { "3", "4" });
        assertNull(in.read());
    }

    @Test
    public void testCR() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        DelimitedReader in = createReader(factory, "1\t2\r3\t4");
        assertArrayEquals(in.read(), new String[] { "1", "2" });
        assertArrayEquals(in.read(), new String[] { "3", "4" });
        assertNull(in.read());
    }

    @Test
    public void testRecordTerminator() throws IOException {
        DelimitedRecordParserFactory factory = new DelimitedRecordParserFactory();
        factory.setDelimiter(',');
        factory.setLineContinuationCharacter('\\');
        factory.setRecordTerminator("*");
        DelimitedReader in = createReader(factory, "1,2,*,4\n5,6,\\*7*");
        assertArrayEquals(new String[] { "1", "2", "", }, in.read());
        assertEquals("1,2,", in.getRecordText());
        assertEquals(0, in.getRecordLineNumber());
        assertArrayEquals(new String[] { "", "4\n5", "6", "7" }, in.read());
        assertEquals(0, in.getRecordLineNumber());
        assertNull(in.read());
        assertEquals(-1, in.getRecordLineNumber());
    }
    
    @Test
    public void testClose() throws IOException {
        DelimitedReader in = new DelimitedReader(new StringReader(""));
        in.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelimiterCannotMatchContinuation() {
        DelimitedParserConfiguration config = new DelimitedParserConfiguration(',');
        config.setLineContinuationCharacter(',');
        
        new DelimitedReader(new StringReader(""), config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelimiterCannotMatchEscape() {
        DelimitedParserConfiguration config = new DelimitedParserConfiguration(',');
        config.setEscape(',');
        
        new DelimitedReader(new StringReader(""), config);
    }
    
    @Test
    public void testMalformedRecordAtEOF() throws IOException {
        DelimitedParserConfiguration config = new DelimitedParserConfiguration(',');
        config.setDelimiter(',');
        config.setLineContinuationCharacter('\\');
        
        StrictStringReader input = new StrictStringReader("hi\\");
        
        RecordIOException error = null;
        
        DelimitedReader in = new DelimitedReader(input, config);
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

    private DelimitedReader createReader(DelimitedRecordParserFactory factory, String input) {
        return (DelimitedReader) factory.createReader(createInput(input));
    }

    private Reader createInput(String s) {
        return new StringReader(s);
    }
}
