package org.beanio.parser.ordinal

import org.beanio.*

import org.beanio.annotation.*
import org.beanio.builder.StreamBuilder
import org.beanio.parser.AbstractParserTest
import org.junit.Test

/**
 * Test cases for the 'ordinal' annotation setting.
 * @author Kevin Seim
 */
class OrdinalTest extends AbstractParserTest {

    @Test
    void testOrdinal() {
        
        StreamFactory factory = StreamFactory.newInstance()
        factory.define(new StreamBuilder("c1")
            .format("csv")
            .addRecord(Man.class))
        
        Marshaller m = factory.createMarshaller("c1");
        
        Man man = new Man()
        man.with{
            age = 15
            lastName = "jones"
            firstName = "jason"
            company = "apple"
            ext = "1234"
        }

        assert m.marshal(man).toString() == "jason,jones,15,apple,1234"
    }

    @Record
    public static class Man {
        @Field
        String company;
        @Field(ordinal=3)
        int age;
        @Field
        String ext;
        @Field(ordinal=2)
        String lastName;
        @Field(ordinal=1)
        String firstName;
    }
}
