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

/**
 * Interface for marshalling a single record object.
 * 
 * <p>The class used to represent a <i>record</i> is specific to the
 * format of a record.  For example, a delimited record marshaller may use 
 * <tt>Stringp[]</tt>.</p>
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface RecordMarshaller {

    /**
     * Marshals a single record object to a <tt>String</tt>.
     * @param record the record object to marshal
     * @return the marshalled record text
     */
    public String marshal(Object record);
    
}
