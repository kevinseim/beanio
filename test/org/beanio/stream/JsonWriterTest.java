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
package org.beanio.stream;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.*;

import org.beanio.stream.json.*;
import org.junit.Test;

/**
 * JUnit test cases for {@link JsonWriter} and {@link JsonRecordMarshaller}.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonWriterTest {

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void test_writeObject() {
        
        List numberList = new ArrayList();
        numberList.add(new BigDecimal("20.45"));
        numberList.add(new Integer("20"));

        List list = new ArrayList();
        list.add(Boolean.TRUE);
        list.add(Boolean.FALSE);
        list.add(newObject());
        list.add(numberList);
        
        Map map = new LinkedHashMap();
        map.put("null", null);
        map.put("field", "value");
        map.put("list", list);
        map.put("object", newObject());
        
        JsonRecordMarshaller m = new JsonRecordMarshaller();
        assertEquals("{\"null\":null,\"field\":\"value\",\"list\":[true,false,{\"field\":\"value\"},[20.45,20]],\"object\":{\"field\":\"value\"}}", m.marshal(map));
        
        List objectList = new ArrayList();
        objectList.add(newObject());
        objectList.add(newObject());
        map.put("objectList", objectList);
        		
        JsonParserConfiguration config = new JsonParserConfiguration();
        config.setPretty(true);
        config.setLineSeparator("\n");
        m = new JsonRecordMarshaller(config);
        
        String expected =
            "{\n" +
            "  \"null\": null,\n" +
            "  \"field\": \"value\",\n" +
            "  \"list\": [true, false, {\"field\": \"value\"}, [20.45, 20]],\n" +
            "  \"object\": {\n" + 
            "    \"field\": \"value\"\n" +
            "  },\n" +
            "  \"objectList\": [\n" +
            "    {\"field\": \"value\"},\n" +
            "    {\"field\": \"value\"}\n" +
            "  ]\n" +
            "}";
            
        assertEquals(expected, m.marshal(map));
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map newObject() {
        Map map = new LinkedHashMap();
        map.put("field", "value");
        return map;
    }
}
