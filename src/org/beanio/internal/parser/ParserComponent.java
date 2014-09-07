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
 * Base class for all parser components in that implement {@link Parser}.
 * 
 * <p>The method {@link #isSupportedChild(Component)} is overridden to restrict
 * children to components that also implement {@link Parser}.
 * 
 * @author Kevin Seim
 * @since 2.0
 * @see Parser
 */
public abstract class ParserComponent extends Component implements Parser {

    /**
     * Constructs a new <tt>ParserComponent</tt>.
     */
    public ParserComponent() {
        super();
    }

    /**
     * Constructs a new <tt>ParserComponent</tt>.
     * @param size the initial child capacity
     */
    public ParserComponent(int size) {
        super(size);
    }

    @Override
    protected boolean isSupportedChild(Component child) {
        return child instanceof Parser;
    }
}
