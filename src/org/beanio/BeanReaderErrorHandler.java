/*
 * Copyright 2010-2012 Kevin Seim
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

/**
 * A callback interface for handling exceptions thrown by a {@link BeanReader}.  When set on a <tt>BeanReader</tt>,
 * the <tt>BeanReader</tt> will delegate all exception handling to this class.  When an error handler
 * is not set on a reader, the <tt>BeanReader</tt>'s <tt>read()</tt> will simply throw the exception.
 *  
 * @author Kevin Seim
 * @since 1.0
 * @see BeanReader
 */
public interface BeanReaderErrorHandler {

    /**
     * Callback method for handling a {@link BeanReaderException} when using
     * a {@link BeanReader}.
     * @param ex the {@link BeanReaderException} to handle
     * @throws Exception if the <tt>BeanReaderException</tt> is rethrown or the error
     *   handler throws a new Exception
     */
    public void handleError(BeanReaderException ex) throws Exception;
    
}
