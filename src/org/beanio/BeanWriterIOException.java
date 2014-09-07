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

import java.io.IOException;

/**
 * Exception thrown when a {@link BeanWriter}'s underlying
 * output stream throws an {@link IOException}.
 * 
 * @author Kevin Seim
 * @since 1.0
 * @see BeanWriter
 */
public class BeanWriterIOException extends BeanWriterException {

    private static final long serialVersionUID = 2L;

    /**
     * Constructs a new <tt>BeanWriterIOException</tt>.
     * @param message the error message
     * @param cause the root cause
     */
    public BeanWriterIOException(String message, IOException cause) {
        super(message, cause);
    }

    /**
     * Constructs a new <tt>BeanWriterIOException</tt>.
     * @param message the error message
     */
    public BeanWriterIOException(String message) {
        super(message);
    }

    /**
     * Constructs a new <tt>BeanWriterIOException</tt>.
     * @param cause the root cause
     */
    public BeanWriterIOException(IOException cause) {
        super(cause);
    }
    
    @Override
    public IOException getCause() {
        return (IOException) super.getCause();
    }
}
