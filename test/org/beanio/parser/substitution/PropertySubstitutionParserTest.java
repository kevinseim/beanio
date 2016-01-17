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
package org.beanio.parser.substitution;

import static org.junit.Assert.*;

import java.text.*;
import java.util.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for property substitution in a mapping file.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class PropertySubstitutionParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = StreamFactory.newInstance();
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testRuntimeSubstitution() throws ParseException {
        Properties properties = new Properties();
        properties.setProperty("dateFormat", "yyyy-MM-dd");
        properties.setProperty("type", "string");
        
        factory.loadResource("org/beanio/parser/substitution/substitution_mapping.xml", properties);
        
        Unmarshaller unmarshaller = factory.createUnmarshaller("stream");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        Map map = (Map) unmarshaller.unmarshal("2012-04-01,23,George");
        assertEquals(sdf.parse("2012-04-01"), map.get("date"));
        assertEquals("23", map.get("age"));
        assertEquals("George", map.get("name"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testDefaultSubstitution() {
        Properties properties = new Properties();
        properties.setProperty("dateFormat", "yyyy-MM-dd");
        
        factory.loadResource("org/beanio/parser/substitution/substitution_mapping.xml", properties);
        
        Unmarshaller unmarshaller = factory.createUnmarshaller("stream");
        
        Map map = (Map) unmarshaller.unmarshal("2012-04-01,23,George");
        assertEquals(23, map.get("age"));
    }

    @Test(expected=BeanIOConfigurationException.class)
    public void testMissingProperty() {
        factory.loadResource("org/beanio/parser/substitution/substitution_mapping.xml");
    }    
}
