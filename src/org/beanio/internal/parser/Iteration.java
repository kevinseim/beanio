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

/**
 * Repeating components must implement <tt>Iteration</tt> to offset record positions 
 * during marshalling and unmarshalling.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public interface Iteration {

    /**
     * Returns the index of the current iteration relative to its parent.
     * @return the index of the current iteration
     */
    public int getIterationIndex(ParsingContext context);
    
    /**
     * Returns the size of the components that make up a single iteration.
     * @return the iteration size
     */
    public int getIterationSize();
    
    /**
     * Returns whether the iteration size is variable based on another field
     * in the record.
     * @return true if variable, false otherwise
     */
    public boolean isDynamicIteration();
}
