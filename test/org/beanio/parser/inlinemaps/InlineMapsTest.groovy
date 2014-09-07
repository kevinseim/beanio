package org.beanio.parser.inlinemaps

import org.beanio.Marshaller
import org.beanio.StreamFactory
import org.beanio.Unmarshaller
import org.beanio.annotation.*
import org.beanio.builder.FieldBuilder
import org.beanio.builder.RecordBuilder
import org.beanio.builder.SegmentBuilder
import org.beanio.builder.StreamBuilder
import org.beanio.parser.AbstractParserTest
import org.junit.Test

/**
 * JUnit test cases for inline maps.
 */
class InlineMapsTest extends AbstractParserTest {

    @Record
    static class AnnotatedRecord {
        @Segment(at=0, key="key", minOccurs=0, maxOccurs=-1)
        Map<String,AnnotatedSegment> map;
    }
    static class AnnotatedSegment {
        @Field(at=0)
        String key;
        @Field(at=1)
        String value;   
    }
    
    @Test
    void testAnnotatedSegmentMap() {
        StreamFactory factory = createFactory("""\
            <stream name="s" format="csv" strict="true">
              <record name="record" class="org.beanio.parser.inlinemaps.InlineMapsTest\$AnnotatedRecord" />
            </stream>""");
        validateSegmentMap(factory)
    }
    
    @Test
    void testBuilderSegmentMap() {
        StreamFactory factory = createFactory(new StreamBuilder("s")
            .format("csv")
            .addRecord(new RecordBuilder("record")
                .type(AnnotatedRecord.class)
                .addSegment(new SegmentBuilder("map")
                    .type(AnnotatedSegment.class)
                    .collection(Map.class)
                    .occurs(0, -1)
                    .key("key")
                    .addField(new FieldBuilder("key"))
                    .addField(new FieldBuilder("value"))
                )
            ));
        
        validateSegmentMap(factory)
    }
    
    private void validateSegmentMap(StreamFactory factory) {
        Unmarshaller u = factory.createUnmarshaller("s")
        Marshaller m = factory.createMarshaller("s")
        
        String text = "key1,value1,key2,value2";
        AnnotatedRecord record = u.unmarshal(text)
        assert record.map
        assert record.map.key1 instanceof AnnotatedSegment
        assert record.map.key1.key == "key1"
        assert record.map.key1.value == "value1"
        assert record.map.key2.key == "key2"
        assert record.map.key2.value == "value2"
        assert m.marshal(record).toString() == text
    }
    
    @Record
    static class AnnotatedRecord2 {
        @Segment(at=0, key="key", value="value", minOccurs=0, maxOccurs=5)
        Map<String,AnnotatedSegment> map;
    }
    
    @Test
    void testAnnotatedSegmentMapWithValue() {
        StreamFactory factory = createFactory("""\
            <stream name="s" format="csv" strict="true">
              <record name="record" class="org.beanio.parser.inlinemaps.InlineMapsTest\$AnnotatedRecord2" />
            </stream>""");
        validateSegmentMapWithValue(factory);
    }
    
    @Test
    void testBuilderSegmentMapWithValue() {
        StreamFactory factory = createFactory(new StreamBuilder("s")
            .format("csv")
            .addRecord(new RecordBuilder("record")
                .type(AnnotatedRecord2.class)
                .addSegment(new SegmentBuilder("map")
                    .collection(Map.class)
                    .occurs(0, -1)
                    .key("key")
                    .value("value")
                    .addField(new FieldBuilder("key"))
                    .addField(new FieldBuilder("value"))
                )
            ));
        validateSegmentMapWithValue(factory);
    }
    
    private void validateSegmentMapWithValue(StreamFactory factory) {
        Unmarshaller u = factory.createUnmarshaller("s")
        Marshaller m = factory.createMarshaller("s")
        
        String text = "key1,value1,key2,value2";
        AnnotatedRecord2 record = u.unmarshal(text)
        assert record?.map == ["key1":"value1", "key2":"value2"]
        assert m.marshal(record).toString() == text
    }
}
