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
package org.beanio.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.*;
import java.util.*;

import org.beanio.internal.util.TypeUtil;
import org.junit.Test;

/**
 * JUnit test cases for the <tt>TypeUtil</tt> class.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class TypeUtilTest {

    private ClassLoader cl = TypeUtilTest.class.getClassLoader();
    
    @Test
    public void testToType() {
        assertEquals(String.class, TypeUtil.toType(cl, "string"));
        assertEquals(Boolean.class, TypeUtil.toType(cl, "boolean"));
        assertEquals(Byte.class, TypeUtil.toType(cl, "byte"));
        assertEquals(Character.class, TypeUtil.toType(cl, "char"));
        assertEquals(Character.class, TypeUtil.toType(cl, "character"));
        assertEquals(Short.class, TypeUtil.toType(cl, "short"));
        assertEquals(Integer.class, TypeUtil.toType(cl, "int"));
        assertEquals(Integer.class, TypeUtil.toType(cl, "integer"));
        assertEquals(Long.class, TypeUtil.toType(cl, "long"));
        assertEquals(Float.class, TypeUtil.toType(cl, "float"));
        assertEquals(Double.class, TypeUtil.toType(cl, "double"));
        assertEquals(BigDecimal.class, TypeUtil.toType(cl, "BigDecimal"));
        assertEquals(BigDecimal.class, TypeUtil.toType(cl, "decimal"));
        assertEquals(BigInteger.class, TypeUtil.toType(cl, "BigInteger"));
        assertEquals(Date.class, TypeUtil.toType(cl, "date"));
        assertEquals(Date.class, TypeUtil.toType(cl, "time"));
        assertEquals(Date.class, TypeUtil.toType(cl, "datetime"));
        assertEquals(getClass(), TypeUtil.toType(cl, "org.beanio.util.TypeUtilTest"));
        assertEquals(List.class, TypeUtil.toType(cl, "java.util.List"));
        assertEquals(AbstractList.class, TypeUtil.toType(cl, "java.util.AbstractList"));
    }
    
    @Test
    public void testToTypeClassNotFound() {
        assertNull(TypeUtil.toType(cl, "org.beanio.types.NoClass"));
    }
    
    @Test
    public void testToAggregation() {
        assertEquals(List.class, TypeUtil.toAggregationType("list"));
        assertEquals(Collection.class, TypeUtil.toAggregationType("collection"));
        assertEquals(Set.class, TypeUtil.toAggregationType("set"));
        assertEquals(TypeUtil.ARRAY_TYPE, TypeUtil.toAggregationType("array"));
        assertEquals(ArrayList.class, TypeUtil.toAggregationType("java.util.ArrayList"));
        assertEquals(AbstractList.class, TypeUtil.toAggregationType("java.util.AbstractList"));
        assertEquals(Map.class, TypeUtil.toAggregationType("map"));
        assertEquals(HashMap.class, TypeUtil.toAggregationType("java.util.HashMap"));
        assertNull(TypeUtil.toAggregationType("org.beanio.types.NoClass"));
    }
}
