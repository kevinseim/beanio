package org.beanio.parser.lazy;

import static org.junit.Assert.*;

import java.util.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for testing the <tt>lazy</tt> attribute of the <tt>segment</tt> element.
 * 
 * @author Kevin Seim
 * @since 2.0.2
 */
public class LazyTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("lazy_mapping.xml");
    }

    @Test
    public void testLazySegment() {
        Unmarshaller u = factory.createUnmarshaller("s1");
        
        LazyUser user = (LazyUser) u.unmarshal("kevin          ");
        assertEquals("kevin", user.name);
        assertNull(user.account);
        
        user = (LazyUser) u.unmarshal("kevin1         ");
        assertEquals("kevin", user.name);
        assertNotNull(user.account);
        assertEquals(Integer.valueOf(1), user.account.getNumber());
        assertEquals("", user.account.getText());
    }
    
    @Test
    public void testRepeatingLazySegments() {
        Unmarshaller u = factory.createUnmarshaller("s2");
        
        LazyUser user = (LazyUser) u.unmarshal("kevin      ");
        assertEquals("kevin", user.name);
        assertNull(user.accounts);
        
        user = (LazyUser) u.unmarshal("kevin   001");
        assertEquals("kevin", user.name);
        assertNotNull(user.accounts);
        assertEquals(1, user.accounts.size());        
    }
    
    @Test
    public void testNestedLazySegments() {
        Unmarshaller u = factory.createUnmarshaller("s3");
        
        LazyUser user = (LazyUser) u.unmarshal("kevin,7,checking,DR,CR");
        assertEquals("kevin", user.name);
        assertNotNull(user.account);
        assertEquals((Integer) 7, user.account.getNumber());
        assertEquals("checking", user.account.getText());
        assertNotNull(user.account.getTransactions());
        assertEquals(2, user.account.getTransactions().size());
        assertEquals("DR", user.account.getTransactions().get(0).getType());
        assertEquals("CR", user.account.getTransactions().get(1).getType());
        
        user = (LazyUser) u.unmarshal("kevin,,,,");
        assertEquals("kevin", user.name);
        assertNull(user.account);
    }
    
    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testRepeatingLazyField() {
        Unmarshaller u = factory.createUnmarshaller("s4");
        
        Map record = (Map) u.unmarshal("kevin,trevor");
        List<String> names = (List<String>) record.get("names");
        assertEquals(2, names.size());      
        assertEquals("kevin", names.get(0));
        assertEquals("trevor", names.get(1));
        
        record = (Map) u.unmarshal(",");
        assertNull(record.get("names"));
    }
}
