package org.beanio.parser.validation

import org.beanio.*
import org.beanio.parser.AbstractParserTest
import org.junit.Ignore;
import org.junit.Test

/**
 * Unit tests for marshalled field validation (which is disabled by default,
 * hence the Ignore unless configured).
 * @author Kevin Seim
 */
@Ignore
class MarshalledFieldValidationTest extends AbstractParserTest {

    @Test(expected=InvalidBeanException.class)
    void testRequired() {
        StreamFactory factory = createFactory("""
            <stream name="s" format="csv" strict="true">
              <record name="record" class="map">
                <field name="field" type="String" required="true" />
              </record>
            </stream>""");
    
        Marshaller m = factory.createMarshaller("s")
        Map bean = ["field":null]
        m.marshal(bean)
    }
    
    @Test(expected=InvalidBeanException.class)
    void testMinLength() {
        StreamFactory factory = createFactory("""
            <stream name="s" format="csv" strict="true">
              <record name="record" class="map">
                <field name="field" minLength="3" />
              </record>
            </stream>""");
    
        Marshaller m = factory.createMarshaller("s")
        Map bean = ["field":"ab"]
        m.marshal(bean)
    }
    
    @Test(expected=InvalidBeanException.class)
    void testMaxLength() {
        StreamFactory factory = createFactory("""
            <stream name="s" format="csv" strict="true">
              <record name="record" class="map">
                <field name="field" maxLength="3" />
              </record>
            </stream>""");
    
        Marshaller m = factory.createMarshaller("s")
        Map bean = ["field":"abcd"]
        m.marshal(bean)
    }
    
    @Test(expected=InvalidBeanException.class)
    void testRegex() {
        StreamFactory factory = createFactory("""
            <stream name="s" format="csv" strict="true">
              <record name="record" class="map">
                <field name="field" regex="\\d+" />
              </record>
            </stream>""");
    
        Marshaller m = factory.createMarshaller("s")
        Map bean = ["field":"abc"]
        m.marshal(bean)
    }
}
