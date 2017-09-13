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
package org.beanio.internal.util;

/**
 * Utility class for working with JSON formatted values.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonUtil {

    private static final int INT_SIZE = Integer.toString(Integer.MAX_VALUE).length() - 1;
    
    private JsonUtil() { }
    
    /**
     * Parses a JSON formatted boolean from text.
     * @param text the text to parse
     * @return the parsed {@link Boolean}
     * @throws IllegalArgumentException if the text is not a valid boolean
     */
    public static Boolean toBoolean(String text) throws IllegalArgumentException {
        if (text == null) {
            return null;
        }
        else if ("true".equals(text)) {
            return Boolean.TRUE;
        }
        else if ("false".equals(text)) {
            return Boolean.FALSE;
        }
        else {
            throw new IllegalArgumentException("Cannot parse '" + text + "' into JSON boolean");
        }
    }
    
    /**
     * Parses a JSON formatted number from text.
     * @param text the text to parse
     * @return the parsed {@link Number}
     * @throws NumberFormatException if the text is not a valid number
     */
    public static Number toNumber(String text) throws NumberFormatException {
        if (text == null) {
            return null;
        }
        
        boolean fp = false;
        for (char c : text.toCharArray()) {
            if (c == '.' || c == 'e' || c == 'E') {
                fp = true;
                break;
            }
        }
        
        if (fp) {
            Double d = new Double(text);
            if (d.isNaN() || d.isInfinite()) {
                throw new NumberFormatException("Invalid number");
            }
            return d;
        } 
        else if (text.length() < INT_SIZE) {
            return Integer.valueOf(text);
        }
        else {
            Long n = new Long(text);
            if (n.intValue() == n.longValue()) {
                return Integer.valueOf(n.intValue());
            }
            return n;
        }
    }
}
