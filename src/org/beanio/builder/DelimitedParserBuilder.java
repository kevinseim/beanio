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
import org.beanio.stream.delimited.DelimitedRecordParserFactory;

/**
 * Builder for delimited parsers.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public class DelimitedParserBuilder extends ParserBuilder {

    private DelimitedRecordParserFactory parser = new DelimitedRecordParserFactory();
    
    public DelimitedParserBuilder() { }
    
    public DelimitedParserBuilder(char delimiter) {
        parser.setDelimiter(delimiter);
    }
    
    public DelimitedParserBuilder delimiter(char delimiter) {
        parser.setDelimiter(delimiter);
        return this;
    }
    
    public DelimitedParserBuilder recordTerminator(String terminator) {
        parser.setRecordTerminator(terminator);
        return this;
    }
    
    public DelimitedParserBuilder enableEscape(char escape) {
        parser.setEscape(escape);
        return this;
    }    

    public DelimitedParserBuilder enableLineContinuation(char c) {
        parser.setLineContinuationCharacter(c);
        return this;
    }    
    
    public DelimitedParserBuilder enableComments(String... comments) {
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
