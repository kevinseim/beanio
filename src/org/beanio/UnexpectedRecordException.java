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
 * Exception thrown when the record type of last record read by a {@link BeanReader} 
 * is out of order based on the expected order defined by the stream's mapping file.
 * After this exception is thrown, further reads from the stream will likely result in
 * further exceptions.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class UnexpectedRecordException extends BeanReaderException {

	private static final long serialVersionUID = 2L;

	/**
	 * Constructs a new <tt>UnexpectedRecordException</tt>.
	 * @param context the current context of the bean reader
	 * @param message the error message
	 */
	public UnexpectedRecordException(RecordContext context, String message) {
		super(message);
		setRecordContext(context);
	}
}
