/*
 * Copyright 2011-2012 Kevin Seim
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beanio.parser.xml.marshaller;

import static org.junit.Assert.assertEquals;

import org.beanio.*;
import org.beanio.beans.*;
import org.beanio.parser.xml.XmlParserTest;
import org.junit.*;

/**
 * JUnit test cases for testing an XML {@link Marshaller} and {@link Unmarshaller}.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlMarshallerTest extends XmlParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("marshaller_mapping.xml");
    }
    
    @Test
    public void testMarshaller() throws Exception {
        String personRecord = 
            "<stream>\r\n" +
            "  <person>\r\n" +
            "    <firstName>Joe</firstName>\r\n" +
            "    <lastName>Smith</lastName>\r\n" +
            "  </person>\r\n" +
            "</stream>";
        
        String orderRecord =
            "<stream>\r\n" +
            "  <order>\r\n" +
            "    <id>100</id>\r\n" +
            "  </order>\r\n" +
            "</stream>";
        
        Marshaller m = factory.createMarshaller("stream");
        Unmarshaller u = factory.createUnmarshaller("stream");
        
        Person person = new Person();
        person.setFirstName("Joe");
        person.setLastName("Smith");
        
        String text = m.marshal(person).toString();
        assertEquals(personRecord, text);
        
        person = (Person) u.unmarshal(personRecord);
        assertEquals("Joe", person.getFirstName());
        assertEquals("Smith", person.getLastName());
        
        person = (Person) u.unmarshal(m.marshal(person).toDocument());
        assertEquals("Joe", person.getFirstName());
        assertEquals("Smith", person.getLastName());
        
        Order order = (Order) u.unmarshal(orderRecord);
        assertEquals("100", order.getId());
        
        order.setId("200");
        order = (Order) u.unmarshal(m.marshal(order).toDocument());
        assertEquals("200", order.getId());
    }
}
