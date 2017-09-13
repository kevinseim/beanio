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
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class DelegatingParser extends ParserComponent {

    /**
     * Constructs a new <tt>DelegatingParser</tt>.
     */
    public DelegatingParser() {
        super(1);
    }
    
    @Override
    public boolean matches(UnmarshallingContext context) {
        return getParser().matches(context);
    }

    @Override
    public boolean unmarshal(UnmarshallingContext context) {
        return getParser().unmarshal(context);
    }
    
    @Override
    public boolean marshal(MarshallingContext context) throws IOException {
        return getParser().marshal(context);
    }

    @Override
    public void clearValue(ParsingContext context) {
        getParser().clearValue(context);
    }

    @Override
    public void setValue(ParsingContext context, Object value) {
        getParser().setValue(context, value);
    }

    @Override
    public Object getValue(ParsingContext context) {
        return getParser().getValue(context);
    }

    @Override
    public int getSize() {
        return getParser().getSize();
    }

    @Override
    public boolean isOptional() {
        return getParser().isOptional();
    }
    
    @Override
    public boolean isIdentifier() {
        return getParser().isIdentifier();
    }

    @Override
    public boolean hasContent(ParsingContext context) {
        return getParser().hasContent(context);
    }
    
    protected Parser getParser() {
        return (Parser) getFirst();
    }
    
}
