package org.beanio.parser.trim;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for testing the <tt>trim</tt> attribute of a <tt>field</tt> element.
 * 
 * @author Kevin Seim
 * @since 2.0.2
 */
public class TrimTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("trim_mapping.xml");
    }

    @Test
    public void testLazySegment() {
        Unmarshaller u = factory.createUnmarshaller("s1");
        
        @SuppressWarnings("rawtypes")
        Map map = (Map) u.unmarshal("\"jen  \",jen  ,1    ");
        assertEquals("jen  ", map.get("text"));
        assertEquals("jen", map.get("text_trim"));
        assertEquals(Integer.valueOf(1), map.get("number"));
    }
}
