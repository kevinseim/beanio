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
package org.beanio.parser.xml;

import org.beanio.stream.xml.*;

/**
 * Default writer factory for XML related test cases.
 * <p>
 * To simplify comparison of generated XML output, the line separator and
 * indentation are explicitly set.
 * 
 * @author Kevin Seim
 * @since 1.1
 */
public class TestXmlWriterFactory extends XmlRecordParserFactory {

    /**
     * Constructs a new <tt>TestXmlWriterFactory</tt>.
     */
    public TestXmlWriterFactory() {
        super();
        setSuppressHeader(true);
        setLineSeparator("\r\n");
        setIndentation(2);
    }
}
