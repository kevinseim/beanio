package org.beanio.parser.lazy

import java.util.Map;

import org.beanio.*;
import org.beanio.parser.AbstractParserTest;
import org.junit.Assert;
import org.junit.Test
import groovy.xml.MarkupBuilder

/**
 * Groovy test cases for the 'lazy' configuration setting.
 * @author Kevin Seim
 */
class LazyMapTest extends AbstractParserTest {

	@Test
	void testLazySegmentMap() {
		StreamFactory factory = createFactory("""
		    <stream name="s" format="fixedlength" strict="true">
		      <record name="record" class="org.beanio.beans.Bean">
		        <segment name="map" collection="map" key="id" value="text" lazy="true" occurs="2">
		          <field name="id" type="int" length="5" justify="right" padding="0" />
		          <field name="text" length="5" />
		        </segment>
		      </record>
		    </stream>""");
	
		Unmarshaller u = factory.createUnmarshaller("s")
		def obj = u.unmarshal("00001Val1 00002Val2 ")
		assert obj?.map == [1:"Val1", 2:"Val2"]
		
		obj = u.unmarshal("     Val1 00002     ")
		assert obj?.map[null] == "Val1"
		assert obj?.map[2] == ""
		
		obj = u.unmarshal("                    ")
		assert !obj.map
	}
	
    @Test
    void testLazyRecord() {
        StreamFactory factory = createFactory("""
            <stream name="s" format="csv" strict="true">
              <group name="lazy" class="org.beanio.beans.Bean">
                <record name="record" class="org.beanio.beans.Bean" occurs="1" lazy="true">
				  <field name="type" rid="true" literal="L" ignore="true" />
                  <field name="text" />
                </record>
              </group>
              <group name="not-lazy" class="org.beanio.beans.Bean">
                <record name="record" class="org.beanio.beans.Bean" occurs="1">
                  <field name="type" rid="true" literal="N" ignore="true" />
                  <field name="text" />
                </record>
              </group>
            </stream>""");

        String input = """\
			|L,One
			|L,
			|N,
            """.stripMargin()

        BeanReader r = createReader(factory, input)
        def obj = r.read()
        assert obj.record?.text == "One"
        
        obj = r.read()
        assert !obj.record
        
        obj = r.read()
    	assert obj.record?.text == ""
    }
    
	@Test
	void testLazyRecordCollection() {
		StreamFactory factory = createFactory("""
		    <stream name="s" format="csv" strict="true">
              <group name="group" class="org.beanio.beans.Bean">
		        <record name="record" order="1" occurs="1">
		          <field name="type" rid="true" literal="H" ignore="true" />
		        </record>
		        <record name="list" order="2" collection="list" class="org.beanio.beans.Bean" lazy="true" occurs="0+">
				  <field name="type" rid="true" literal="D" ignore="true" />
		          <field name="text" />
		        </record>
			  </group>
		    </stream>""");

        String input = """\
			|H
			|D,One
			|D,Two
			|H
			|D,
			""".stripMargin()
		
		BeanReader r = createReader(factory, input)
		def obj = r.read()
        assert !obj.record
        assert obj.list
        assert obj.list.size == 2
		assert obj.list[0].text == "One"
		assert obj.list[1].text == "Two"
		
		obj = r.read()
        assert obj
        assert !obj.list
	}
	
    @Test
    void testLazyRecordMap() {
        StreamFactory factory = createFactory("""
            <stream name="s" format="csv" strict="true">
              <group name="group" class="org.beanio.beans.Bean">
                <record name="record" order="1" occurs="1">
                  <field name="type" rid="true" literal="H" ignore="true" />
                </record>
                <record name="map" order="2" collection="map" key="id" value="text" lazy="true" occurs="0+">
                  <field name="type" rid="true" literal="D" ignore="true" />
				  <field name="id" type="int" />
                  <field name="text" />
                </record>
              </group>
            </stream>""");

        String input = """\
            |H
            |D,1,One
            |D,2,Two
            |H
            |D,,
            """.stripMargin()
        
        BeanReader r = createReader(factory, input)
        def obj = r.read()
        assert obj?.map == [1:"One", 2:"Two"]

        obj = r.read()
        assert obj
        assert !obj.map
    }
    
    @Test
    void testLazyStringField() {
        StreamFactory factory = createFactory("""
          <stream name="n1" format="csv">
            <record name="record" class="map">
              <field name="field1" lazy="true" />
              <field name="field2" />
              <field name="field3" lazy="true" trim="true" minOccurs="0" />
            </record>  
          </stream>""");
      
        Unmarshaller u = factory.createUnmarshaller("n1");

        Map map = u.unmarshal(",");
        assert map.containsKey("field1")
        assert !map.field1
        assert map.field2 == ""
        assert !map.containsKey("field3")
        
        map = u.unmarshal(" ,, ");
        assert map.field1 == " "
        assert map.field2 == ""
        assert map.containsKey("field3")
        assert !map.field3
    }
}
