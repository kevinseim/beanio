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

import org.beanio.BeanIOConfigurationException;
import org.beanio.internal.config.BeanConfig;
import org.beanio.stream.RecordParserFactory;
import org.beanio.stream.csv.CsvRecordParserFactory;

/**
 * Builder for CSV parsers.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public class CsvParserBuilder extends ParserBuilder {

    private CsvRecordParserFactory parser = new CsvRecordParserFactory();
    
    public CsvParserBuilder() { }
    
    public CsvParserBuilder delimiter(char delimiter) {
        parser.setDelimiter(delimiter);
        return this;
    }

    public CsvParserBuilder quote(char quote) {
        parser.setQuote(quote);
        return this;
    } 
    
    public CsvParserBuilder escape(char escape) {
        parser.setEscape(escape);
        return this;
    }    

    public CsvParserBuilder recordTerminator(String terminator) {
        parser.setRecordTerminator(terminator);
        return this;
    }

    public CsvParserBuilder enableComments(String... comments) {
        parser.setComments(comments);
        return this;
    }
    
    public CsvParserBuilder enableMultiline() {
        parser.setMultilineEnabled(true);
        return this;
    }
    
    public CsvParserBuilder allowUnquotedWhitespace() {
        parser.setWhitespaceAllowed(true);
        return this;
    }
    
    public CsvParserBuilder allowUnquotedQuotes() throws BeanIOConfigurationException {
        parser.setUnquotedQuotesAllowed(true);
        return this;
    }
    
    public CsvParserBuilder alwaysQuote() throws BeanIOConfigurationException {
        parser.setAlwaysQuote(true);
        return this;
    }
    
    @Override
    public BeanConfig<RecordParserFactory> build() {
        BeanConfig<RecordParserFactory> config = new BeanConfig<>();
        config.setInstance(parser);
        return config;
    }
}
