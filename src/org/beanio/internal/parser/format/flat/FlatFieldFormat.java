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
package org.beanio.internal.parser.format.flat;

import org.beanio.internal.parser.*;

/**
 * A <tt>FlatFieldFormat</tt> is a {@link FieldFormat} for flat stream formats 
 * (i.e. CSV, delimited and fixed length).
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface FlatFieldFormat extends FieldFormat {
    
    /**
     * Returns the field position.  
     * 
     * <p>In a delimited/CSV stream format, the position is the index of the field in the 
     * record starting at 0.  For example, the position of field2 in the following
     * comma delimited record is 1:
     * 
     * <p><tt>field1,field2,field3</tt>
     * 
     * <p>In a fixed length stream format, the position is the index of the first character 
     * of the field in the record, also starting at 0.  For example, the position of field2
     * in the following record is 6: 
     * 
     * <p><tt>field1field2field3</tt>
     * 
     * @return the field position
     */
    public int getPosition();
    
}
