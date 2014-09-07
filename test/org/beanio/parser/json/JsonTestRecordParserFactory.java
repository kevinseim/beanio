package org.beanio.parser.json;

import org.beanio.stream.json.JsonRecordParserFactory;

public class JsonTestRecordParserFactory extends JsonRecordParserFactory {
    public JsonTestRecordParserFactory() {
        setPretty(true);
        setIndentation(2);
        setLineSeparator("\r\n");
    }
}
