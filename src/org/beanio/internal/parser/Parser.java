/*
 * Copyright 2011-2013 Kevin Seim
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

import java.io.IOException;

/**
 * A <tt>Parser</tt> is used marshal and unmarshal record components.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface Parser {

    /**
     * Returns the name of this parser component.
     * @return the parser component name
     */
    public String getName();
    
    /**
     * Returns whether this parser and its children match a record
     * being unmarshalled.
     * @param context the {@link UnmarshallingContext}
     * @return true if matched, false otherwise
     */
    public boolean matches(UnmarshallingContext context);
    
    /**
     * Unmarshals a record.
     * @param context the {@link UnmarshallingContext}
     * @return <tt>true</tt> if this component was present in the unmarshalled record, 
     *   or <tt>false</tt> otherwise
     */
    public boolean unmarshal(UnmarshallingContext context) throws AbortRecordUnmarshalligException;
    
    /**
     * Marshals a record.
     * @param context the {@link MarshallingContext}
     * @return whether a value was marshalled
     * @throws IOException if an I/O error occurs
     */
    public boolean marshal(MarshallingContext context) throws IOException;
    
    /**
     * Returns whether this parser or any of its descendant have content for marshalling.
     * @param context the {@link ParsingContext}
     * @return true if there is content for marshalling, false otherwise
     */
    public boolean hasContent(ParsingContext context);
    
    /**
     * Clears the current property value.
     * @param context the {@link ParsingContext}
     */
    public void clearValue(ParsingContext context);
    
    /**
     * Sets the property value for marshaling.
     * @param context the {@link ParsingContext}
     * @param value the property value
     */
    public void setValue(ParsingContext context, Object value);

    /**
     * Returns the unmarshalled property value.
     * @param context the {@link ParsingContext}
     * @return the property value
     */
    public Object getValue(ParsingContext context);
    
    /**
     * Returns the size of a single occurrence of this element, which is used to offset
     * field positions for repeating segments and fields.
     * 
     * <p>The concept of size is dependent on the stream format.  The size of an element in a fixed 
     * length stream format is determined by the length of the element in characters, while other 
     * stream formats calculate size based on the number of fields.  Some stream formats, 
     * such as XML, may ignore size settings.
     * 
     * @return the size of this parser element
     */
    public int getSize();
    
    /**
     * Returns whether this parser or any descendant of this parser is used to identify 
     * a record during unmarshalling.
     * @return true if this parser or any descendant is used to identify a record
     */
    public boolean isIdentifier();
    
    /**
     * Returns whether this node must exist during unmarshalling.
     * @return true if this node is optional during unmarshalling, false otherwise
     */
    public boolean isOptional();
}
