/*
 * Copyright 2011 Kevin Seim
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
package org.beanio.parser.writemode;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.text.*;
import java.util.Date;

import org.beanio.StreamFactory;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for parsing bean property configurations.
 * @author Kevin Seim
 * @since 1.2
 */
public class WriteModeParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("writemode_mapping.xml");
    }
    
    /**
     * Test basic write only operation.
     */
    @Test
    public void testBasic() {
        PersonInterface person = new PersonInterface() {
            @Override
            public String getFirstName() {
                return "John";
            }
            @Override
            public String getLastName() {
                return "Smith";
            }
            @Override
            public int getAge() {
                return 21;
            }
            @Override
            public Date getBirthDate() {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd").parse("2011-01-01");
                } 
                catch (ParseException ex) {
                    return null;
                }
            }
        };
        
        StringWriter text = new StringWriter();
        factory.createWriter("wm1", text).write(person);
        assertEquals("John,Smith,21,2011-01-01" + lineSeparator, text.toString());
    }
    
    /**
     * Test a BeanReader cannot be created when mode set to 'write'.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCreateReader() {
        factory.createReader("wm1", new StringReader("dummy"));
    }
}
