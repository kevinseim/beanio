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
import org.beanio.stream.xml.XmlRecordParserFactory;

/**
 * Builder for XML parsers.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public class XmlParserBuilder extends ParserBuilder {

    private XmlRecordParserFactory parser = new XmlRecordParserFactory();
    
    /**
     * Constructs a new <tt>XmlParserBuilder</tt>.
     */
    public XmlParserBuilder() { }
    
    public XmlParserBuilder suppressHeader() {
        parser.setSuppressHeader(true);
        return this;
    }
    
    public XmlParserBuilder headerVersion(String version) {
        parser.setVersion(version);
        return this;
    }
    
    public XmlParserBuilder headerEncoding(String encoding) {
        parser.setEncoding(encoding);
        return this;
    }
    
    public XmlParserBuilder addNamespace(String prefix, String uri) {
        parser.addNamespace(prefix, uri);
        return this;
    }
    
    public XmlParserBuilder indent() {
        return indent(2);
    }
    
    public XmlParserBuilder indent(int amount) {
        parser.setIndentation(amount);
        return this;
    }
    
    public XmlParserBuilder lineSeparator(String sep) {
        parser.setLineSeparator(sep);
        return this;
    }
    
    @Override
    public BeanConfig<RecordParserFactory> build() {
        BeanConfig<RecordParserFactory> config = new BeanConfig<>();
        config.setInstance(parser);
        return config;
    }
}
