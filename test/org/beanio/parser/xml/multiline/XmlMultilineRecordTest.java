/*
 * Copyright 2012 Kevin Seim
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
package org.beanio.parser.xml.multiline;

import static org.junit.Assert.*;

import java.io.*;
import java.text.SimpleDateFormat;

import org.beanio.*;
import org.beanio.beans.*;
import org.beanio.parser.xml.XmlParserTest;
import org.junit.*;

/**
 * JUnit test cases for testing bean objects that span multiple XML records (i.e. record groups).
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlMultilineRecordTest extends XmlParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("xml_multiline_mapping.xml");
    }

    @Test
    public void testRecordGroup() throws Exception {
        BeanReader in = factory.createReader("ml1", new InputStreamReader(
            getClass().getResourceAsStream("ml1_in.xml")));
        
        BeanWriter out = null;
        try {
            // read a valid multi-line record
            Order order = (Order) in.read();

            assertEquals(3, in.getLineNumber());
            assertEquals(4, in.getRecordCount());
            assertEquals("orderGroup", in.getRecordName());
            
            RecordContext ctx = in.getRecordContext(1);
            assertEquals(7, ctx.getLineNumber());
            assertEquals("customer", ctx.getRecordName());
            
            assertEquals("100", order.getId());
            assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2012-01-01"), order.getDate());
            
            Person buyer = order.getCustomer();
            assertEquals("George", buyer.getFirstName());
            assertEquals("Smith", buyer.getLastName());
            
            OrderItem item = order.getItems().get(0);
            assertEquals("soda", item.getName());
            assertEquals(2, item.getQuantity());
            
            item = order.getItems().get(1);
            assertEquals("carrots", item.getName());
            assertEquals(5, item.getQuantity());            
            
            StringWriter text = new StringWriter();
            out = factory.createWriter("ml1", text);
            out.write(order);
            
            // write bean object with missing data
            order.setCustomer(null);
            order.setItems(null);
            out.write(order);
            
            // read an invalid multi-line record
            try {
                in.read();
                fail("Record expected to fail validation");
            }
            catch (InvalidRecordException ex) {
                assertEquals(20, in.getLineNumber());
                assertEquals(2, in.getRecordCount());
                assertEquals("orderGroup", in.getRecordName());

                ctx = ex.getRecordContext(1);
                assertTrue(ctx.hasFieldErrors());
                assertEquals(24, ctx.getLineNumber());
                assertEquals("item", ctx.getRecordName());
                assertEquals("a", ctx.getFieldText("quantity", 0));
            }
            
            // skip 2 invalid records
            assertEquals(2, in.skip(2));
            
            // read another valid record
            order = (Order) in.read();
            assertEquals(55, in.getLineNumber());
            assertEquals(3, in.getRecordCount());
            assertEquals("orderGroup", in.getRecordName());
            assertEquals("104", order.getId());
            assertNull(order.getCustomer());
            
            out.write(order);
            out.flush();
            out.close();
            out = null;
            
            assertEquals(load("ml1_out.xml"), text.toString());
        }
        finally {
            in.close();
        }
    }
}
