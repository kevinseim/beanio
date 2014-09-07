/*
 * Copyright 2010-2013 Kevin Seim
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
package org.beanio.internal.util;

import java.io.PrintStream;

/**
 * Interface implemented by marshallers and unmarshallers for debugging
 * BeanIO's compiled configuration.  The information displayed by these
 * methods may be changed without notice.
 * @author Kevin Seim
 * @since 2.1.0
 */
public interface Debuggable {

    /**
     * Prints the internal view of the stream configuration
     * to {@link System#out}
     */
    public void debug();
    
    /**
     * Prints the internal view of the stream configuration.
     * @param out the {@link PrintStream} to write to
     */
    public void debug(PrintStream out);
    
}
