package org.beanio.parser

import org.beanio.BeanReader
import org.beanio.StreamFactory
import org.beanio.builder.StreamBuilder

abstract class AbstractParserTest {

	protected BeanReader createReader(StreamFactory factory, String input, String name="s") {
		return factory.createReader(name, new StringReader(input))
	}
	
	protected StreamFactory createFactory(String xml=null) {
		StreamFactory factory = StreamFactory.newInstance()
		if (xml) {
			xml = "<beanio xmlns=\"http://www.beanio.org/2012/03\">\n" + xml + "\n</beanio>"
			factory.load(new ByteArrayInputStream(xml.getBytes("UTF-8")))
		}
		return factory
	}
    
    protected StreamFactory createFactory(StreamBuilder builder) {
        StreamFactory factory = StreamFactory.newInstance();
        if (builder != null) {
            factory.define(builder)
        }
        return factory;
    }
	
}
