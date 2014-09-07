/*
 * Copyright 2010-2013 Kevin Seim
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
package org.beanio;

import java.io.IOException;

import org.beanio.internal.util.Debuggable;

/**
 * Interface for marshalling bean objects to an output stream.
 * 
 * <p>A <tt>BeanWriter</tt> is created using a {@link StreamFactory} and 
 * a mapping file.</p>
 * 
 * @author Kevin Seim
 * @since 1.0
 * @see StreamFactory
 */
public interface BeanWriter extends Debuggable {

    /**
     * Writes a bean object to this output stream.
     * @param bean the bean object to write
     * @throws BeanWriterException if a record could not be identified for marshalling, 
     *   or in a few other rare (but fatal) cases
     * @throws BeanWriterIOException if the underlying output stream throws an {@link IOException},
     *   or if this writer is closed
     */
    public void write(Object bean) throws BeanWriterException, BeanWriterIOException;

    /**
     * Writes a bean object to this output stream.
     * @param recordName the record or group name bound to the bean object from the mapping file 
     * @param bean the bean object to write
     * @throws BeanWriterException if a record could not be identified for marshalling, 
     *   or in a few other rare (but fatal) cases
     * @throws BeanWriterIOException if the underlying output stream throws an {@link IOException},
     *   or if this writer is closed
     */
    public void write(String recordName, Object bean) throws BeanWriterException;

    /**
     * Flushes this output stream.
     * @throws BeanWriterIOException if the underlying output stream throws an {@link IOException},
     *   or if this writer is closed
     */
    public void flush() throws BeanWriterIOException;

    /**
     * Closes this output stream.
     * @throws BeanWriterIOException if the underlying output stream throws an {@link IOException},
     *   or if this writer is already closed
     */
    public void close() throws BeanWriterIOException;

}
