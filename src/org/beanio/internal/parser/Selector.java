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

import java.util.Map;

/**
 * A <tt>Selector</tt> is used to match a {@link Group} or {@link Record} for
 * marshalling and unmarshalling.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface Selector extends Parser {
    
    /* map key used to store the state of the 'count' attribute */
    public static final String COUNT_KEY = "count";
    
    /**
     * Finds a parser for marshalling a bean object.  If matched by this Selector, the method
     * should set the bean object on the property tree and return itself.
     * @param context the {@link MarshallingContext}
     * @return the matched {@link Selector} for marshalling the bean object
     */
    public Selector matchNext(MarshallingContext context);
    
    /**
     * Finds a parser for unmarshalling a record based on the current state of the stream.
     * @param context the {@link UnmarshallingContext}
     * @return the matched {@link Selector} for unmarshalling the record
     */
    public Selector matchNext(UnmarshallingContext context);
    
    /**
     * Finds a parser that matches the input record.  This method is invoked when
     * {@link #matchNext(UnmarshallingContext)} returns null, in order to differentiate
     * between unexpected and unidentified record types.
     * @param context the {@link UnmarshallingContext}
     * @return the matched {@link Selector}
     */
    public Selector matchAny(UnmarshallingContext context);

    /**
     * Skips a record or group of records.
     * @param context the {@link UnmarshallingContext}
     */
    public void skip(UnmarshallingContext context);
    
    /**
     * Checks for any unsatisfied components before the stream is closed.
     * @param context the {@link ParsingContext}
     * @return the first unsatisfied node
     */
    public Selector close(ParsingContext context);

    /**
     * Resets the component count of this Selector's children.
     * @param context the {@link ParsingContext}
     */
    public void reset(ParsingContext context);
    
    /**
     * Returns the number of times this component was matched within the current
     * iteration of its parent component.
     * @param context the {@link ParsingContext}
     * @return the match count
     */
    public int getCount(ParsingContext context);
    
    /**
     * Sets the number of times this component was matched within the current 
     * iteration of its parent component.
     * @param context the {@link ParsingContext}
     * @param count the new match count
     */
    public void setCount(ParsingContext context, int count);
    
    /**
     * Returns whether this component has reached its maximum occurrences.
     * @param context the {@link ParsingContext}
     * @return true if maximum occurrences has been reached
     */
    public boolean isMaxOccursReached(ParsingContext context);
    
    /**
     * Returns the minimum number of occurrences of this component (within the context
     * of its parent).
     * @return the minimum occurrences
     */
    public int getMinOccurs();
    
    /**
     * Returns the maximum number of occurrences of this component (within the context
     * of its parent).
     * @return the maximum occurrences
     */
    public int getMaxOccurs();
    
    /**
     * Returns the order of this component (within the context of its parent).
     * @return the component order
     */
    public int getOrder();
    
    /**
     * Returns the {@link Property} mapped to this component, or null if there is
     * no property mapping.
     * @return the {@link Property} mapped to this component
     */
    public Property getProperty();
    
    /**
     * Returns whether this component is a record group.
     * @return true if this component is a record group, false otherwise
     */
    public boolean isRecordGroup();
    
    /**
     * Updates a Map with the current state of the Writer to allow for
     * restoration at a later time.
     * @param context the {@link ParsingContext}
     * @param namespace a String to prefix all state keys with
     * @param state the Map to update with the latest state
     */
    public void updateState(ParsingContext context, String namespace, Map<String,Object> state);
    
    /**
     * Restores a Map of previously stored state information.
     * @param context the {@link ParsingContext}
     * @param namespace a String to prefix all state keys with
     * @param state the Map containing the state to restore
     * @throws IllegalStateException if the Map is missing any state information 
     */
    public void restoreState(ParsingContext context, String namespace, Map<String,Object> state) throws IllegalStateException;
}
