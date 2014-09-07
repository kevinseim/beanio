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

import java.io.*;

import org.beanio.stream.*;

/**
 * A <tt>StreamFormat</tt> provides format specific extensions for a {@link Stream} parser. 
 * 
 * <p>Implementations of this interface must be thread-safe.
 *  
 * @author Kevin Seim
 * @since 2.0
 */
public interface StreamFormat {

    /**
     * Returns the name of the stream.
     * @return the stream name
     */
    public String getName();
    
    /**
     * Creates a new ummarshalling context.
     * @return the new {@link UnmarshallingContext}
     */
    public UnmarshallingContext createUnmarshallingContext();
 
    /**
     * Creates a new marshalling context.
     * @param streaming true if marshalling to a stream
     * @return the new {@link MarshallingContext}
     */
    public MarshallingContext createMarshallingContext(boolean streaming);
    
    /**
     * Creates a new record marshaller.
     * @return the new {@link RecordMarshaller}
     */
    public RecordMarshaller createRecordMarshaller();
    
    /**
     * Creates a new record unmarshaller.
     * @return the new {@link RecordUnmarshaller}
     */
    public RecordUnmarshaller createRecordUnmarshaller();
    
    /**
     * Creates a new record reader.
     * @param in the {@link Reader} to read records from
     * @return the new {@link RecordReader}
     */
    public RecordReader createRecordReader(Reader in);
    
    /**
     * Creates a new record writer.
     * @param out the {@link Writer} to write records to
     * @return the new {@link RecordWriter}
     */
    public RecordWriter createRecordWriter(Writer out);
    
}
