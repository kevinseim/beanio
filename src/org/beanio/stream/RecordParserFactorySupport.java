/*
 * Copyright 2013 Kevin Seim
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

import java.io.*;

/**
 * A base class for implementing a custom {@link RecordParserFactory}.  Unless
 * overridden, all createXXX() methods will throw {@link UnsupportedOperationException}.
 * 
 * @author Kevin Seim
 * @since 2.0.4
 */
public class RecordParserFactorySupport implements RecordParserFactory {

    @Override
    public void init() throws IllegalArgumentException { }

    @Override
    public RecordReader createReader(Reader in) throws IllegalArgumentException {
        throw new UnsupportedOperationException("BeanReader not supported");
    }

    @Override
    public RecordWriter createWriter(Writer out) throws IllegalArgumentException {
        throw new UnsupportedOperationException("BeanWriter not supported");
    }

    @Override
    public RecordMarshaller createMarshaller() throws IllegalArgumentException {
        throw new UnsupportedOperationException("Marshaller not supported");
    }

    @Override
    public RecordUnmarshaller createUnmarshaller() throws IllegalArgumentException {
        throw new UnsupportedOperationException("Unmarshaller not supported");
    }
}
