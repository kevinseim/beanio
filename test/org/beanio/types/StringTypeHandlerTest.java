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
 * JUnit test cases for the <tt>StringTypeHandler</tt> class.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class StringTypeHandlerTest {

    @Test
    public void testTrim() {
        StringTypeHandler handler = new StringTypeHandler();
        handler.setTrim(true);
        assertTrue(handler.isTrim());
        assertEquals("value", handler.parse("  value  "));
    }
    
    @Test
    public void testNullIfEmpty() {
        StringTypeHandler handler = new StringTypeHandler();
        handler.setNullIfEmpty(true);
        assertTrue(handler.isNullIfEmpty());
        assertNull(handler.parse(""));
    }
    
    @Test
    public void testFormatNull() {
        StringTypeHandler handler = new StringTypeHandler();
        assertNull(handler.format(null));
    }
}
