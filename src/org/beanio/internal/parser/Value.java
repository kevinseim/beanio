/*
 * Copyright 2011-2012 Kevin Seim
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
package org.beanio.internal.parser;

/**
 * Stores special property values.
 * @author Kevin Seim
 * @since 2.0
 */
public interface Value {
    
    /** Constant indicating the field did not pass validation. */
    public static final String INVALID = new String("-invalid-");
    
    /** Constant indicating the field was not present in the stream */
    public static final String MISSING = new String("-missing-");
    
    /** Constant indicating the field was nil (XML only) */
    public static final String NIL = new String("-nil-");
    
}
