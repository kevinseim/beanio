package org.beanio.parser.ignoreUnidentifiedRecords;

import java.io.InputStreamReader;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * Unit test cases for ignoring unidentified records.
 * 
 * @author Kevin Seim
 */
public class IgnoreUnidentifiedRecordsParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("ignore_mapping.xml");
    }
    
    @Test
    public void testIgnoreUnidentfiedRecords() {
        BeanReader in = factory.createReader("stream1", new InputStreamReader(
            getClass().getResourceAsStream("ignoreUnidentifiedRecords1.txt")));
        
        try {
            
            in.read();
            Assert.assertEquals("header", in.getRecordName());
            in.read();
            Assert.assertEquals("group_header", in.getRecordName());
            in.read();
            Assert.assertEquals("group_trailer", in.getRecordName());

            in.read();
            Assert.assertEquals("header", in.getRecordName());
            in.read();
            Assert.assertEquals("group_header", in.getRecordName());
            in.read();
            Assert.assertEquals("group_trailer", in.getRecordName());

            Assert.assertNull(in.read());
        }
        finally {
            in.close();
        }
    }
}
