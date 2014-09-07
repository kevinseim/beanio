package org.beanio.parser.constructor;

import org.beanio.*;
import org.beanio.builder.StreamBuilder;
import org.beanio.parser.AbstractParserTest;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for fields and segments that use dynamic occurrences.
 * @author Kevin Seim
 */
class ConstructorParserTest extends AbstractParserTest {

    private StreamFactory factory;

    @Test
    void testConstructor() {
        StreamFactory factory = createFactory("""
          <stream name="c1" format="csv">
            <record name="record" class="org.beanio.parser.constructor.Color">
              <field name="name" setter="#1" />
              <field name="r" type="int" setter="#2" />
              <field name="g" type="int" setter="#3" />
              <field name="b" type="int" setter="#4" />
            </record>
          </stream>""");
        
        Unmarshaller u = factory.createUnmarshaller("c1");
        def color = u.unmarshal("red,255,0,0");
        assert color.name == "red"
        assert color.r == 255
        assert color.g == 0
        assert color.b == 0
    }
    
    @Test
    void testAnnotatedConstructor() {
        StreamFactory factory = createFactory(new StreamBuilder("c1")
            .format("csv")
            .addRecord(AnnotatedColor.class));
        
        Unmarshaller u = factory.createUnmarshaller("c1");
        def color = u.unmarshal("red,255,0,0");
        assert color.name == "red"
        assert color.r == 255
        assert color.g == 0
        assert color.b == 0
    }
}

