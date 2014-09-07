package org.beanio.parser.types

import org.beanio.*
import org.beanio.beans.Bean
import org.beanio.parser.AbstractParserTest
import org.junit.Test

/**
 * Unit test cases for type handling locale support
 * @author Kevin Seim
 */
class TypeHandlerLocaleTest extends AbstractParserTest {

    @Test
    void testFieldWithDefault() {
        Date date = new GregorianCalendar(2013, 1, 1).getTime();
        
        StreamFactory factory = createFactory("""
            <stream name="s" format="csv" strict="true">
              <typeHandler name="int_de" class="org.beanio.types.IntegerTypeHandler">
                <property name="locale" value="de" />
              </typeHandler>
              <typeHandler name="date_de" class="org.beanio.types.DateTypeHandler">
                <property name="locale" value="de" />
              </typeHandler>
              <record name="record" class="map">
                <field name="int1" typeHandler="int_de" format="#,##0" />
                <field name="int2" type="int" format="#,##0" />
                <field name="date" typeHandler="date_de" />
              </record>
            </stream>""");
        
        String text = '10.000,"10,000",01.02.2013 00:00:00'
        Map map = ['int1':10000, 'int2':10000, 'date':date]
        
        Marshaller m = factory.createMarshaller("s")
        assert m.marshal(map).toString() == text
        
        Unmarshaller u = factory.createUnmarshaller("s")
        assert u.unmarshal(text) == map
    }
}


