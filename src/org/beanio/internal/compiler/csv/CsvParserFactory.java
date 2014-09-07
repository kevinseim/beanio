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
package org.beanio.internal.compiler.csv;

import org.beanio.internal.compiler.ParserFactory;
import org.beanio.internal.compiler.delimited.DelimitedParserFactory;
import org.beanio.internal.config.StreamConfig;
import org.beanio.internal.parser.StreamFormat;
import org.beanio.internal.parser.format.csv.CsvStreamFormat;
import org.beanio.stream.*;
import org.beanio.stream.csv.*;

/**
 * A {@link ParserFactory} for the CSV stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class CsvParserFactory extends DelimitedParserFactory {

    /**
     * Constructs a new <tt>CsvParserFactory</tt>.
     */
    public CsvParserFactory() { }
    
    @Override
    public StreamFormat createStreamFormat(StreamConfig config) {
        CsvStreamFormat format = new CsvStreamFormat();
        format.setName(config.getName());
        format.setRecordParserFactory(createRecordParserFactory(config));
        return format;
    }

    @Override
    protected RecordParserFactory getDefaultRecordParserFactory() {
        return new CsvRecordParserFactory();
    }
}
