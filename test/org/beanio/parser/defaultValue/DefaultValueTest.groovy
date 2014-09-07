package org.beanio.parser.defaultValue

import org.beanio.Marshaller
import org.beanio.StreamFactory
import org.beanio.Unmarshaller
import org.beanio.beans.Bean
import org.beanio.parser.AbstractParserTest
import org.junit.Test

/**
 * JUnit test cases for the default field values.
 * @author Kevin Seim
 */
class DefaultValueTest extends AbstractParserTest {

    @Test
    void testFieldWithDefault() {
        StreamFactory factory = createFactory("""
            <stream name="s" format="csv" strict="true">
              <record name="record" class="org.beanio.beans.Bean">
                <field name="field1" default="default1" minOccurs="1" />
                <field name="field2" default="default2" minOccurs="0" />
              </record>
            </stream>""");
        
        Unmarshaller u = factory.createUnmarshaller("s")
        Marshaller m = factory.createMarshaller("s")
        
        Bean bean = u.unmarshal("value1,value2")
        assert bean.field1 == "value1"
        assert bean.field2 == "value2"
        assert m.marshal(bean).toString() == "value1,value2" 
        
        bean = u.unmarshal("")
        assert bean.field1 == "default1"
        assert bean.field2 == "default2"
        assert m.marshal(new Bean()).toString() == "default1,default2"   
    }
    
    @Test
    void testRepeatingFieldWithDefault() {
        StreamFactory factory = createFactory("""
            <stream name="s" format="csv" strict="true">
              <record name="record" class="org.beanio.beans.Bean">
                <field name="list" collection="list" default="default" minOccurs="1" maxOccurs="5" />
              </record>
            </stream>""");
        
        Unmarshaller u = factory.createUnmarshaller("s")
        Marshaller m = factory.createMarshaller("s")
        
        Bean bean = u.unmarshal("value1,value2")
        assert bean.list == ["value1", "value2"]
        assert m.marshal(bean).toString() == "value1,value2"
        
        bean = u.unmarshal("")
        assert bean.list == ["default"]
        assert m.marshal(new Bean()).toString() == "default"
    }
}
