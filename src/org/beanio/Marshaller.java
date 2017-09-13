/*
 * Copyright 2012-2014 Kevin Seim
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

import java.util.List;

import org.beanio.internal.util.Debuggable;
import org.w3c.dom.*;

/**
 * Interface for marshalling bean objects.
 * 
 * <p>A <tt>Marshaller</tt> can be used to marshal a bean object bound to
 * a <tt>record</tt> in a mapping file.  Marshalling bean objects that span multiple
 * records is not supported and will cause a {@link BeanWriterException}.</p>
 * 
 * <p>Depending on the stream format, a bean object can be marshalled to one or more
 * formats.  All stream formats support marshalling to a <tt>String</tt> value,
 * as shown in the following example:</p>
 * 
 * <pre>   marshaller.marshal(object).toString();</pre>
 * 
 * <p>A <tt>Marshaller</tt> instance is stateful.  If a BeanIO mapping file declares
 * record ordering and expected occurrences, a {@link BeanWriterException} may be thrown for
 * bean objects written out of sequence or that have exceeded a record's maximum occurrences.</p>
 * 
 * <p>There is some performance benefit for reusing the same <tt>Marshaller</tt> instance,
 * but a <tt>Marshaller</tt> is not thread safe and should not be used to concurrently
 * marshal multiple bean objects.</p>
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface Marshaller extends Debuggable {

    /**
     * Marshals a single bean object.
     * @param bean the bean object to marshal
     * @return this <tt>Marshaller</tt>
     * @throws BeanWriterException if a record is not matched for the given bean object,
     *   or in some other rare (but fatal) conditions
     * @throws InvalidBeanException if BeanIO is configured to validate fields during marshalling,
     *   and a field does not meet the configured validation rules
     */
    public Marshaller marshal(Object bean) throws BeanWriterException;
    
    /**
     * Marshals a single bean object.
     * @param recordName the name of the record to marshal
     * @param bean the bean object to marshal
     * @return this <tt>Marshaller</tt>
     * @throws BeanWriterException if a record is not matched for the given record name
     *   and bean object, or in some other rare (but fatal) conditions
     * @throws InvalidBeanException if BeanIO is configured to validate fields during marshalling,
     *   and a field does not meet the configured validation rules
     */
    public Marshaller marshal(String recordName, Object bean) throws BeanWriterException;

    /**
     * Returns the most recent marshalled bean object as a <tt>String</tt>.  This method
     * is supported by all stream formats.
     * @return the record text
     * @throws BeanWriterException if a fatal error occurs
     */
    @Override
    public String toString() throws BeanWriterException;
    
    /**
     * Returns the most recent marshalled bean object as a <tt>String[]</tt> for <tt>csv</tt>
     * and <tt>delimited</tt> formatted streams.
     * @return the <tt>String</tt> array of fields
     * @throws BeanWriterException if an array is not supported by the stream format
     */
    public String[] toArray() throws BeanWriterException;

    /**
     * Returns the most recent marshalled bean object as a {@link List} for <tt>csv</tt>
     * and <tt>delimited</tt> formatted streams.
     * @return the {@link List} of fields
     * @throws BeanWriterException if an array is not supported by the stream format
     */
    public List<String> toList() throws BeanWriterException;
    
    /**
     * Returns the most recent marshalled bean object as a {@link Document} for <tt>xml</tt>
     * formatted streams.
     * @return the {@link Document}
     * @throws BeanWriterException if {@link Document} is not supported by the stream format
     */
    public Document toDocument() throws BeanWriterException;

}
