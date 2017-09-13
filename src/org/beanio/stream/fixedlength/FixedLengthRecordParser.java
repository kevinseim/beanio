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
package org.beanio.stream.fixedlength;

import org.beanio.stream.*;

/**
 * A combined {@link RecordMarshaller} and {@link RecordUnmarshaller} implementation 
 * for fixed length formatted records.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FixedLengthRecordParser implements RecordMarshaller, RecordUnmarshaller {

    /**
     * Constructs a new <tt>FixedLengthRecordParser</tt>.
     */
    public FixedLengthRecordParser() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordUnmarshaller#unmarshal(java.lang.String)
     */
    @Override
    public Object unmarshal(String text) {
        return text;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.stream.RecordMarshaller#marshal(java.lang.Object)
     */
    @Override
    public String marshal(Object record) {
        return marshal((String)record);
    }
    
    /**
     * Marshals a single fixed length record.
     * @param record the record to marshal
     * @return the marshalled record text
     */
    public String marshal(String record) {
        return record;
    }
}
