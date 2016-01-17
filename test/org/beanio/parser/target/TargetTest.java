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
package org.beanio.parser.target;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.List;

import org.beanio.*;
import org.beanio.beans.Person;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for the record 'target' attribute.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class TargetTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("target_mapping.xml");
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testTarget() {
        Unmarshaller unmarshaller = factory.createUnmarshaller("stream");
        Marshaller marshaller = factory.createMarshaller("stream");
        
        List<String> list = (List<String>) unmarshaller.unmarshal("N,kevin,kev,kevo");
        assertEquals(2, list.size());
        assertEquals("kev", list.get(0));
        assertEquals("kevo", list.get(1));
        assertEquals("N,,kev,kevo", marshaller.marshal(list).toString());
        
        Integer age = (Integer) unmarshaller.unmarshal("A,jen,28");
        assertEquals(Integer.valueOf(28), age);
        assertEquals("A,unknown,28", marshaller.marshal(age).toString());
    }
    
    @Test
    public void testSegmentTarget() {
    	Unmarshaller u = factory.createUnmarshaller("t2");
    	Marshaller m = factory.createMarshaller("t2");
    	
    	Person person = (Person) u.unmarshal("john,smith");
    	assertEquals("smith", person.getLastName());
    	
    	assertEquals(",smith", m.marshal(person).toString());
    }
    
    @Test
    public void testRecordTarget() {
    	BeanReader in = factory.createReader("t3", new StringReader("john,smith"));
    	Person person = (Person) in.read();
    	assertEquals("smith", person.getLastName());
    }
    
    @Test(expected=BeanIOConfigurationException.class)
    public void testTarget_TargetNotFound() throws Exception {
        newStreamFactory("targetNotFound.xml");
    }
    
    @Test(expected=BeanIOConfigurationException.class)
    public void testTarget_RepeatingTarget() throws Exception {
        newStreamFactory("targetRepeating.xml");
    }
    
    @Test(expected=BeanIOConfigurationException.class)
    public void testTarget_IsNotProperty() throws Exception {
        newStreamFactory("targetIsNotProperty.xml");
    }

    @Test(expected=BeanIOConfigurationException.class)
    public void testTarget_TargetAndClassSet() throws Exception {
        newStreamFactory("targetAndClass.xml");
    }
}
