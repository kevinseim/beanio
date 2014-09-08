/*
 * Copyright 2014 Kevin Seim
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
 * Exception thrown by a {@link BeanWriter} or {@link Marshaller}, when a bean
 * cannot be marshalled to meet the configured field validation rules.
 * @author Kevin Seim
 */
public class InvalidBeanException extends BeanWriterException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new InvalidBeanException.
     * @param message the error message
     */
    public InvalidBeanException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidBeanException.
     * @param message the error message
     * @param cause the root cause
     */
    public InvalidBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidBeanException.
     * @param cause the root cause
     */
    public InvalidBeanException(Throwable cause) {
        super(cause);
    }
}
