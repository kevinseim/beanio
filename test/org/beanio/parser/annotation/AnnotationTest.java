package org.beanio.parser.annotation;

import java.util.*;

import org.beanio.*;
import org.beanio.builder.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test scenarios for annotated classes.
 * @author Kevin Seim
 */
public class AnnotationTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = StreamFactory.newInstance();
    }

    @Test
    public void testAnnotatedSegments() {
        factory.define(new StreamBuilder("s1")
            .format("csv")
            .addRecord(AnnotatedRoom.class));

        factory.define(new StreamBuilder("s1-xml")
            .format("xml")
            .addRecord(AnnotatedRoom.class));
        
        Unmarshaller[] u = new Unmarshaller[] {
            factory.createUnmarshaller("s1"),
            factory.createUnmarshaller("s1-xml")};
        
        Marshaller[] m = new Marshaller[] {
            factory.createMarshaller("s1"),
            factory.createMarshaller("s1-xml")};
        
        String [] input = new String[] {
            // CSV input:
            "2,60,CFL,40,IC,Bath,hardwood,10,20",
            // XML input:
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<s1-xml>" +
            "<annotatedRoom>" +
            "<light quantity=\"2\"><bulbs><watts>60</watts><style>CFL</style></bulbs><bulbs><watts>40</watts><style>IC</style></bulbs></light>" +
            "<name>Bath</name>" +
            "<flooring><floor>hardwood</floor><width>10</width><height>20</height></flooring>" +
            "</annotatedRoom></s1-xml>"
        };
        
        for (int i=0; i<input.length; i++) {
            AnnotatedRoom room = (AnnotatedRoom) u[i].unmarshal(input[i]);
            Assert.assertEquals(2, room.getLightFixture().quantity);
            Assert.assertEquals(2, room.getLightFixture().bulbs.size());
            Assert.assertEquals(LinkedList.class, room.getLightFixture().bulbs.getClass());
            Assert.assertEquals(60, room.getLightFixture().bulbs.get(0).watts);
            Assert.assertEquals("CFL", room.getLightFixture().bulbs.get(0).style);
            Assert.assertEquals(40, room.getLightFixture().bulbs.get(1).watts);
            Assert.assertEquals("IC", room.getLightFixture().bulbs.get(1).style);          
            Assert.assertEquals("Bath", room.name);
            Assert.assertEquals(10, room.getFlooring().width);
            Assert.assertEquals(20, room.getFlooring().height);
            
            Assert.assertEquals(input[i], m[i].marshal(room).toString());
        }
    }
    
    @Test
    public void testAnnotatedFields() {
        factory.define(new StreamBuilder("s1")
            .format("csv")
            .addRecord(AnnotatedUser.class));
        
        factory.define(new StreamBuilder("s1-xml")
            .format("xml")
            .xmlType(XmlType.NONE)
            .addRecord(AnnotatedUser.class));
    
        Unmarshaller[] u = new Unmarshaller[] {
            factory.createUnmarshaller("s1"),
            factory.createUnmarshaller("s1-xml")};
        
        Marshaller[] m = new Marshaller[] {
            factory.createMarshaller("s1"),
            factory.createMarshaller("s1-xml")};
        
        String [] input = new String[] {
            // CSV input:
            "USER,joe,smith,left,right,1970-01-01,0028,A,B,1,END",
            
            // XML input:
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<user xmlns=\"http://org.beanio.test\" type=\"USER\">" +
    		"<firstName>joe</firstName>" +
    		"<lastName>smith</lastName>" +
    		"<hands>left</hands>" +
    		"<hands>right</hands>" +
    		"<birthDate>1970-01-01</birthDate>" +
    		"<age>0028</age>" +
    		"<letters>A</letters>" +
    		"<letters>B</letters>" +
    		"<numbers>1</numbers>" +
    		"<end>END</end>" +
    		"</user>"};
        
        for (int i=0; i<input.length; i++) {
            AnnotatedUser user = (AnnotatedUser) u[i].unmarshal(input[i]);
            Assert.assertEquals("joe", user.getFirstName());
            Assert.assertEquals("smith", user.getSurname());
            Assert.assertEquals("left", user.getHands()[0]);
            Assert.assertEquals("right", user.getHands()[1]);
            Assert.assertEquals(new GregorianCalendar(1970, 0, 1).getTime(), user.birthDate);
            Assert.assertEquals(Integer.valueOf(28), user.getAge());
            Assert.assertEquals(2, user.letters.size());
            Assert.assertEquals(Character.valueOf('A'), user.letters.get(0));
            Assert.assertEquals(Character.valueOf('B'), user.letters.get(1));
            Assert.assertEquals(1, user.numbers.size());
            Assert.assertEquals(LinkedList.class, user.numbers.getClass());
            Assert.assertEquals(Integer.valueOf(1), user.numbers.get(0));
            Assert.assertEquals("END", user.end);
            
            Assert.assertEquals(input[i], m[i].marshal(user).toString());
        }
    }
}
