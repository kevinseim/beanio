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
 * An interface implemented by any JSON segment or field.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface JsonNode {

    /** JSON object type */
    public static char OBJECT = 'O';
    /** JSON array type */
    public static char ARRAY = 'A';
    /** JSON string type */
    public static char STRING = 'S';
    /** JSON number type */
    public static char NUMBER = 'N';
    /** JSON boolean type */
    public static char BOOLEAN = 'B';
    
    /**
     * Returns the field name.
     * @return the field name
     */
    public String getName();
    
    /**
     * Returns the JSON field name.
     * @return the JSON field name
     */
    public String getJsonName();

    /**
     * Returns the type of node.  If {@link #isJsonArray()} is true, this method
     * returns the component type of the array.
     * @return the node type
     */
    public char getJsonType();
    
    /**
     * Returns whether this node is a JSON array.
     * @return true if this node a JSON array, false otherwise
     */
    public boolean isJsonArray();

    /**
     * Returns the index of this node in its parent array, or -1 if not applicable
     * (i.e. its parent is an object).
     * @return the index of this node in its parent array
     */
    public int getJsonArrayIndex();
    
    /**
     * Returns whether this node may be explicitly set to <tt>null</tt>.
     * @return true if this node value may be set to <tt>null</tt>, false otherwise
     */
    public boolean isNillable();
    
}
