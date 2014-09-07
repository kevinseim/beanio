package org.beanio.parser.annotation

import org.beanio.*
import org.beanio.annotation.*
import org.beanio.builder.StreamBuilder
import org.beanio.parser.AbstractParserTest
import org.junit.*;

/**
 * Unit test cases for annotated groups.
 */
class AnnotatedGroupTest extends AbstractParserTest {
    
    @Test
    void testNestedGroups() {
        StreamBuilder builder = new StreamBuilder("s1")
            .format("csv")
            .addGroup(Main.class)
            
        StreamFactory factory = StreamFactory.newInstance()
        factory.define(builder)
        
        String input = """\
            H,h
            BH,bh
            BD,bd1
            BD,bd2
            BF,bf
        """.stripIndent()
        
        BeanReader reader = factory.createReader("s1", new StringReader(input));
        
        Main m = reader.read()
        assert m.header?.name == "h"
        assert m.batch?.batchHeader?.name == "bh"
        assert m.batch?.details.size() == 2
        assert m.batch?.details[0]?.name == "bd1"
        assert m.batch?.details[1]?.name == "bd2"
        assert m.batch?.batchFooter?.name == "bf"
    }
    
    @Group
    static class Main {
        @Record
        public Header header;
        @Group
        public Batch batch;
    }
    
    static class Header {
        @Field(at=0, rid=true, literal="H")
        public String type;
        @Field(at=1)
        public String name;
    }
    
    static class Batch {
        @Record(order=1, maxOccurs=1)
        public BatchHeader batchHeader;
        @Record(order=2)
        public List<BatchDetail> details;
        @Record(order=3, maxOccurs=1)
        public BatchFooter batchFooter;
    }
    
    static class BatchHeader {
        @Field(at=0, rid=true, literal="BH")
        public String type;
        @Field(at=1)
        public String name;
    }
    
    static class BatchDetail {
        @Field(at=0, rid=true, literal="BD")
        public String type;
        @Field(at=1)
        public String name;
    }
    
    static class BatchFooter {
        @Field(at=0, rid=true, literal="BF")
        public String type;
        @Field(at=1)
        public String name;
    }
}
