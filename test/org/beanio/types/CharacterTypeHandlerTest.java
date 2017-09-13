/*
 * Copyright 2010-2011 Kevin Seim
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
package org.beanio.types;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit test cases for the <tt>CharacterTypeHandler</tt> class.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class CharacterTypeHandlerTest {

    @Test
    public void testParse() throws TypeConversionException {
        CharacterTypeHandler handler = new CharacterTypeHandler();
        assertEquals(Character.valueOf('V'), handler.parse("V"));
        assertNull(handler.parse(null));
        assertNull(handler.parse(""));
    }
    
    @Test(expected=TypeConversionException.class)
    public void testParseInvalid() throws TypeConversionException {
        CharacterTypeHandler handler = new CharacterTypeHandler();
        handler.parse("value");
    }
    
    @Test
    public void testFormat() {
        CharacterTypeHandler handler = new CharacterTypeHandler();
        assertEquals("V", handler.format(Character.valueOf('V')));
        assertEquals("", handler.format(""));
        assertNull(handler.format(null));
    }
}
