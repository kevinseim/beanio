/*
 * Copyright 2012-2013 Kevin Seim
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

import java.util.Properties;

/**
 * Utility class for working with <tt>Strings</tt>.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public final class StringUtil {

	protected final static boolean LAZY_IF_EMPTY = Settings.getInstance().getBoolean(Settings.LAZY_IF_EMPTY);
	
    /**
     * Cannot instantiate.
     */
    private StringUtil() { }
    
    /**
     * Returns whether the given object has a value.
     * @param obj the object to test
     * @return true if the object is not null (and not the empty string based on configuration)
     */
    public static boolean hasValue(Object obj) {
    	if (obj == null) {
    		return false;
    	}
    	if (LAZY_IF_EMPTY && "".equals(obj)) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * Substitutes <tt>${key,default}</tt> place holders with their property values.
     * @param text the template text
     * @param properties the user provided property values
     * @return the text after property substitution
     * @throws IllegalArgumentException if a property value does not exist
     */
    public static String doPropertySubstitution(String text, final Properties properties) 
        throws IllegalArgumentException
    {
        return doPropertySubstitution(text, new PropertySource() {
            @Override
            public String getProperty(String key) {
                return properties != null ? properties.getProperty(key) : null;
            }
        });
    }
    
    /**
     * Substitutes <tt>${key,default}</tt> place holders with their property values.
     * @param text the template text
     * @param properties the user provided property values
     * @return the text after property substitution
     * @throws IllegalArgumentException if a property value does not exist
     */
    public static String doPropertySubstitution(String text, PropertySource properties) 
        throws IllegalArgumentException
    {
        if (text == null || text.length() < 3) {
            return text;
        }
        
        int i = text.indexOf('$');
        if (i < 0) {
            return text;
        }

        StringBuilder s = null;
        int state = 1;
        int keyBegin = i;
        int valueBegin = 0;
        
        ++i;
        
        char [] cs = text.toCharArray();
        for (int j=cs.length; i<j; i++) {
            char c = cs[i];
            
            switch (state) {
            
            // look for '$':
            case 0:
                if (c == '$') {
                    keyBegin = i;
                    valueBegin = 0;
                    state = 1;
                }
                else if (s != null) {
                    s.append(c);
                }
                break;
            
            // look for '{':
            case 1:
                if (c == '{') {
                    state = 2;
                }
                else {
                    if (s != null) {
                        s.append('$').append(c);
                    }
                    state = 0;
                }
                break;
                
            // look for '}'
            case 2:
                if (c == '}') {
                    int length = valueBegin > 0 ? valueBegin - keyBegin - 2 : i - keyBegin - 2;
                    
                    String key = new String(cs, keyBegin + 2, length);
                    String value = null;
                    if (properties != null) {
                        value = properties.getProperty(key);
                    }
                    if (value == null && valueBegin > 0) {
                        value = new String(cs, valueBegin + 1, i - valueBegin - 1);
                    }
                    if (value == null) {
                        throw new IllegalArgumentException("Unresovled property '" + key + "'");
                    }
                    
                    if (s == null) {
                        s = new StringBuilder(new String(cs, 0, keyBegin));
                    }
                    
                    s.append(value);
                    state = 0;
                }
                // collect the default value 
                else if (valueBegin == 0 && c == ',') {
                    valueBegin = i;
                }
                break;
            }
        }
        
        if (s != null) {
            if (state > 0) {
                s.append(new String(cs, keyBegin, cs.length - keyBegin));
            }
            return s.toString();
        }
        else {
            return text;
        }
    }
    
    /**
     * A source of property values.
     */
    public interface PropertySource {
        /**
         * Returns the property value for a given key.
         * @param key the property key
         * @return the property value
         */
        public String getProperty(String key);
    }
}
