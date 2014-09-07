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

/**
 * Used to create a reference to a variable held by a {@link ParsingContext}.
 * 
 * @author Kevin Seim
 * @param <T> the variable type
 */
public class ParserLocal<T> {

    private int index = -1;
    private T defaultValue = null;

    /**
     * Constructs a new <tt>ParserLocal</tt>.
     */
    public ParserLocal() {
        this(null);
    }
    
    /**
     * Constructs a new <tt>ParserLocal</tt>.
     * @param defaultValue the default value
     */
    public ParserLocal(T defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    /**
     * Called when initialized to return a default value.  If not overridden,
     * it returns the default value passed via the constructor.
     * @return the default value
     */
    protected T createDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Initializes the variable.
     * @param index the index of the variable in the heap
     * @param context the {@link ParsingContext} being initialized
     */
    public final void init(int index, ParsingContext context) {
        if (this.index < 0) {
            this.index = index;
        }
        this.set(context, createDefaultValue());
    }
    
    /**
     * Gets the value.
     * @param context the {@link ParsingContext} to get the value from
     * @return the value
     */
    @SuppressWarnings("unchecked")
    public final T get(ParsingContext context) {
        return (T) context.getLocal(index);
    }
    
    /**
     * Sets the value.
     * @param context the {@link ParsingContext} to set the value on
     * @param obj the value
     */
    public final void set(ParsingContext context, T obj) {
       context.setLocal(index, obj);
    }
}
