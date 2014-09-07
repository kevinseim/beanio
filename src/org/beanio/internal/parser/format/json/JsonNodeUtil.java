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
package org.beanio.internal.parser.format.json;

/**
 * Utility class for working with a {@link JsonNode}.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonNodeUtil {

    private JsonNodeUtil() { }
    
    /**
     * Returns a description for given JSON node type.
     * @param type the JSON node type
     * @param array whether the node is an array
     * @return the description
     */
    public static String getTypeDescription(char type, boolean array) {
        String s = "unknown";
        
        switch (type) {
        case JsonNode.STRING:
            s = "string";
            break;
        case JsonNode.NUMBER:
            s = "number";
            break;
        case JsonNode.BOOLEAN:
            s = "boolean";
            break;
        case JsonNode.OBJECT:
            s = "object";
            break;
        case JsonNode.ARRAY:
            return "array";
        }
        
        if (array) {
            s += "[]";
        }
        
        return s;
    }
}
