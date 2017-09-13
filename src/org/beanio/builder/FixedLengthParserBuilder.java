/*
 * Copyright 2013 Kevin Seim
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
package org.beanio.builder;

import org.beanio.internal.config.BeanConfig;
import org.beanio.stream.RecordParserFactory;
import org.beanio.stream.fixedlength.FixedLengthRecordParserFactory;

/**
 * Builder for fixed length stream parsers.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public class FixedLengthParserBuilder extends ParserBuilder {

    private FixedLengthRecordParserFactory parser = new FixedLengthRecordParserFactory();
    
    /**
     * Constructs a new FixedLengthParserBuilder.
     */
    public FixedLengthParserBuilder() { }
    
    /**
     * Sets the record terminator.
     * @param terminator the record termination character
     * @return this
     */
    public FixedLengthParserBuilder recordTerminator(String terminator) {
        parser.setRecordTerminator(terminator);
        return this;
    }
    
    /**
     * Enables the given line continuation character.
     * @param c the character
     * @return this
     */
    public FixedLengthParserBuilder enableLineContinuation(char c) {
        parser.setLineContinuationCharacter(c);
        return this;
    }    

    /**
     * Enables one or more line prefixes that indicate a commented line.
     * @param comments the list of prefixes
     * @return this
     */
    public FixedLengthParserBuilder enableComments(String... comments) {
        parser.setComments(comments);
        return this;
    }
    
    @Override
    public BeanConfig<RecordParserFactory> build() {
        BeanConfig<RecordParserFactory> config = new BeanConfig<>();
        config.setInstance(parser);
        return config;
    }
}
