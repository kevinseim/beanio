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
package org.beanio;

import java.util.List;

import org.beanio.internal.util.Debuggable;
import org.w3c.dom.Node;

/**
 * Interface for unmarshalling single records.
 * 
 * <p>An <tt>Unmarshaller</tt> can be used to unmarshal a bean object bound to
 * a <tt>record</tt> in a mapping file.  Unmarshalling bean objects that span multiple
 * records is not supported and will cause a {@link BeanReaderException}.</p>
 * 
 * <p>An <tt>Unmarshaller</tt> instance is stateful.  If a BeanIO mapping file declares
 * record ordering and expected occurrences, a {@link BeanWriterException} may be thrown for
 * records read out of sequence or that have exceeded their maximum occurrences.</p>
 * 
 * <p>There is some performance benefit for reusing the same <tt>Unmarshaller</tt> instance,
 * but an <tt>Unmarshaller</tt> is not thread safe and should not be used to unmarshal multiple 
 * records concurrently.</p>
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface Unmarshaller extends Debuggable {

    /**
     * Unmarshals a bean object from the given record text.  This method is supported
     * by all stream formats.
     * @param record the record text to unmarhal
     * @return the unmarshalled bean object
     * @throws BeanReaderException if the bean is bound to a record group, or some other
     *   rare (but fatal) error occurs
     * @throws MalformedRecordException if the record text could not be parsed (due to the
     *   expected syntax of the stream format)
     * @throws UnidentifiedRecordException if the record type could not be identified
     * @throws UnexpectedRecordException if the record is out of sequence
     * @throws InvalidRecordException if the record fails validation
     */
    public Object unmarshal(String record) throws BeanReaderException, MalformedRecordException,
        UnidentifiedRecordException, UnexpectedRecordException, InvalidRecordException;
    
    /**
     * Unmarshals a bean object from the given {@link List} of fields.  This method is supported by
     * CSV and delimited formatted streams only.
     * @param fields the {@link List} of fields to unmarshal
     * @return the unmarshalled bean object
     * @throws BeanReaderException if a {@link List} is not supported by the stream format,
     *   or if the bean is bound to a record group, or if some other rare (but fatal) error occurs
     * @throws UnidentifiedRecordException if the record type could not be identified
     * @throws UnexpectedRecordException if the record is out of sequence
     * @throws InvalidRecordException if the record fails validation
     */
    public Object unmarshal(List<String> fields) throws BeanReaderException,
        UnidentifiedRecordException, UnexpectedRecordException, InvalidRecordException;

    /**
     * Unmarshals a bean object from the given <tt>String[]</tt> of fields.  This method is supported by
     * CSV and delimited formatted streams only.
     * @param fields the <tt>String[]</tt> of fields to unmarshal
     * @return the unmarshalled bean object
     * @throws BeanReaderException if a <tt>String[]</tt> is not supported by the stream format,
     *   or if the bean is bound to a record group, or if some other rare (but fatal) error occurs
     * @throws UnidentifiedRecordException if the record type could not be identified
     * @throws UnexpectedRecordException if the record is out of sequence
     * @throws InvalidRecordException if the record fails validation
     */
    public Object unmarshal(String[] fields) throws BeanReaderException,
        UnidentifiedRecordException, UnexpectedRecordException, InvalidRecordException;

    /**
     * Unmarshals a bean object from the given {@link Node}.  This method is supported by
     * XML formatted streams only.
     * @param node the {@link Node} to unmarshal
     * @return the unmarshalled bean object
     * @throws BeanReaderException if a {@link Node} is not supported by the stream format,
     *   or if the bean is bound to a record group, or if some other rare (but fatal) error occurs
     * @throws UnidentifiedRecordException if the record type could not be identified
     * @throws UnexpectedRecordException if the record is out of sequence
     * @throws InvalidRecordException if the record fails validation
     */
    public Object unmarshal(Node node) throws BeanReaderException,
        UnidentifiedRecordException, UnexpectedRecordException, InvalidRecordException;
    
    /**
     * Returns the record or group name of the most recent unmarshalled bean object.
     * @return the record or group name
     */
    public String getRecordName();
    
    /**
     * Returns record information for the most recent unmarshalled bean object.
     * @return the unmarshalled {@link RecordContext}
     */
    public RecordContext getRecordContext();
}
