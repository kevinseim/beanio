package org.beanio.parser.lenientPadding

import org.beanio.*
import org.beanio.parser.AbstractParserTest
import org.junit.Test


/**
 * Test cases for the 'lenientPadding' field attribute.
 * @author Kevin Seim
 */
class LenientPaddingTest extends AbstractParserTest {

    @Test
    void testLazySegmentMap() {
        StreamFactory factory = createFactory("""
            <stream name="s" format="fixedlength" strict="true">
              <record name="record" class="org.beanio.beans.Bean">
                <field name="field1" length="3" />
                <field name="field2" length="3" minOccurs="0" lenientPadding="true" />
                <field name="field3" length="3" minOccurs="0" lenientPadding="true" />
              </record>
            </stream>""");
    
        Unmarshaller u = factory.createUnmarshaller("s")
        def obj = u.unmarshal("aaabb")
        assert obj?.field1 == "aaa"
        assert obj?.field2 == "bb"
        
        obj = u.unmarshal("aaabb c")
        assert obj?.field1 == "aaa"
        assert obj?.field2 == "bb"
        assert obj?.field3 == "c"
        
        obj = u.unmarshal("aaa")
        assert obj?.field1 == "aaa"
        assert obj.field2 == null
        assert obj.field3 == null
        
        try {
            u.unmarshal("aa")
            assert false, "InvalidRecordException expected"
        }
        catch (InvalidRecordException ex) { }
    }
    
}
