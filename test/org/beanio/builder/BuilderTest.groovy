package org.beanio.builder

import org.beanio.internal.compiler.fixedlength.FixedLengthParserFactory
import org.beanio.internal.config.*
import org.beanio.stream.csv.CsvRecordParserFactory
import org.beanio.stream.delimited.DelimitedRecordParserFactory;
import org.beanio.stream.fixedlength.FixedLengthRecordParserFactory;
import org.beanio.stream.xml.XmlRecordParserFactory
import org.beanio.types.*
import org.junit.*

/**
 * JUnit test for the builder API.
 */
class BuilderTest {

    @Test
    void testXmlParserBuilder() {
        XmlParserBuilder b = new XmlParserBuilder()
        XmlRecordParserFactory p = b.build().instance
        assert p.indentation == -1
        assert !p.lineSeparator
        assert p.version == "1.0"
        assert p.encoding == "utf-8"
        assert !p.suppressHeader
        assert !p.getNamespaceMap()
        
        b = new XmlParserBuilder().with {
            indent()
            headerVersion "2.0"
            headerEncoding "ASCII"
            lineSeparator "\n"
            addNamespace("r", "rock")
            addNamespace("p", "paper")
        }
        p = b.build().instance
        assert p.indentation == 2
        assert p.version == "2.0"
        assert p.encoding == "ASCII"
        assert p.lineSeparator == "\n"
        assert p.getNamespaceMap() == ["rock":"r","paper":"p"]
    }
    
    @Test
    void testDelimitedParserBuilder() {
        DelimitedParserBuilder b = new DelimitedParserBuilder()
        DelimitedRecordParserFactory p = b.build().instance
        assert p.delimiter == '\t' as char
        assert !p.escape
        assert !p.lineContinuationCharacter
        assert !p.comments
        assert !p.recordTerminator
        
        b = new DelimitedParserBuilder().with {
            delimiter "," as char
            enableEscape "\\" as char
            enableLineContinuation "&" as char
            enableComments(["#","!"] as String[])
            recordTerminator("\n")
        }
        p = b.build().instance
        assert p.delimiter == "," as char
        assert p.escape == "\\" as char
        assert p.lineContinuationCharacter == "&" as char
        assert p.comments == ["#","!"]
        assert p.recordTerminator == "\n"
        
        b = new DelimitedParserBuilder("|" as char)
        p = b.build().instance
        assert p.delimiter == "|" as char
    }
    
    @Test
    void testFixedLengthParserBuilder() {
        FixedLengthParserBuilder b = new FixedLengthParserBuilder()
        FixedLengthRecordParserFactory p = b.build().instance
        assert !p.lineContinuationCharacter
        assert !p.comments
        assert !p.recordTerminator
        
        b = new FixedLengthParserBuilder().with {
            enableLineContinuation("\\" as char)
            enableComments(["#","!"] as String[])
            recordTerminator("\r\n")
        }
        p = b.build().instance
        assert p.lineContinuationCharacter == "\\" as char
        assert p.comments == ["#","!"]
        assert p.recordTerminator == "\r\n"
    }
    
    @Test
    void testCsvParserBuilder() {
        CsvParserBuilder b = new CsvParserBuilder()
        CsvRecordParserFactory p = b.build().instance
        assert p.delimiter == ',' as char
        assert p.quote == '"' as char
        assert p.escape == '"' as char
        assert !p.multilineEnabled
        assert !p.alwaysQuote
        assert !p.whitespaceAllowed
        assert !p.unquotedQuotesAllowed
        assert !p.comments
        assert !p.recordTerminator
        
        b = new CsvParserBuilder().with {
            delimiter '|' as char
            quote "'" as char
            escape "\\" as char
            enableMultiline()
            alwaysQuote()
            allowUnquotedQuotes()
            allowUnquotedWhitespace()
            enableComments(["#","!"] as String[])
            recordTerminator("\r")
        }
        p = b.build().instance
        assert p.delimiter == '|' as char
        assert p.quote == "'" as char
        assert p.escape == "\\" as char
        assert p.multilineEnabled
        assert p.alwaysQuote
        assert p.whitespaceAllowed
        assert p.unquotedQuotesAllowed
        assert p.comments == ["#","!"]
        assert p.recordTerminator == "\r"
    }
    
    @Test
    void testStreamBuilder() {
        StreamBuilder b = new StreamBuilder("stream")
        
        StreamConfig c = b.build()
        assert c.name == "stream"
        assert c.order == 1
        assert c.minOccurs == 0
        assert c.maxOccurs == 1
        assert !c.format
        assert !c.mode
        assert !c.strict
        assert !c.ignoreUnidentifiedRecords
        
        CsvRecordParserFactory csvParser = new CsvRecordParserFactory();
        DateTypeHandler birthDateHandler = new DateTypeHandler();
        StringTypeHandler stringTypeHandler = new StringTypeHandler();
        
        b.with {
            format "csv" 
            readOnly()
            resourceBundle "bundle"
            strict()
            ignoreUnidentifiedRecords()
            parser(csvParser)
            addRecord(new RecordBuilder("record"))
            addGroup(new GroupBuilder("subgroup"))
            addTypeHandler("birthDate", birthDateHandler)
            addTypeHandler(String.class, stringTypeHandler)
        }
        c = b.build()
        assert c.format == "csv"
        assert c.mode == StreamConfig.READ_ONLY_MODE
        assert c.resourceBundle == "bundle"
        assert c.strict
        assert c.ignoreUnidentifiedRecords
        assert c.parserFactory.instance.is(csvParser)
        assert c.getChildren().find { it.name == "record" && it instanceof RecordConfig }
        assert c.getChildren().find { it.name == "subgroup" && it instanceof GroupConfig }
        assert c.getHandlerList().find { it.name == "birthDate" && it.instance.is(birthDateHandler) }
        assert c.getHandlerList().find { it.type == "java.lang.String" && it.instance.is(stringTypeHandler) }
     
        XmlParserBuilder xmlParser = new XmlParserBuilder()
        b = new StreamBuilder("stream", "fixedlength").with {
            writeOnly()
            parser(xmlParser)
        }   
        c = b.build()
        assert c.format == "fixedlength"
        assert c.mode == StreamConfig.WRITE_ONLY_MODE
        assert c.parserFactory.instance instanceof XmlRecordParserFactory
    }
    
    @Test
    void testGroupBuilder() {
        GroupBuilder b = new GroupBuilder("group")
        
        GroupConfig c = b.build()
        assert c.name == "group"
        assert !c.order
        assert !c.minOccurs
        assert !c.maxOccurs
        
        b.with {
            order 1
            addRecord(new RecordBuilder("record"))
            addGroup(new GroupBuilder("subgroup"))
        }
        c = b.build()
        assert c.order == 1
        assert c.getChildren().find { it.name == "record" && it instanceof RecordConfig }
        assert c.getChildren().find { it.name == "subgroup" && it instanceof GroupConfig }
    }
    
    @Test
    void testRecordBuilder() {
        RecordBuilder b = new RecordBuilder("record")
        
        RecordConfig c = b.build()
        assert c.name == "record"
        assert !c.order
        assert !c.minLength
        assert !c.maxLength
        assert !c.minOccurs
        assert !c.maxOccurs
        assert !c.minMatchLength
        assert !c.maxMatchLength
        
        b.with {
            order 2
            minLength 1
            maxLength(-1)
            ridLength(5,10)
            addField(new FieldBuilder("field"))
            addSegment(new SegmentBuilder("segment"))
        }
        
        c = b.build()
        assert c.order == 2
        assert c.minLength == 1
        assert c.maxLength == Integer.MAX_VALUE
        assert c.minMatchLength == 5
        assert c.maxMatchLength == 10
        assert c.getChildren().find { it.name == "field" && it instanceof FieldConfig }
        assert c.getChildren().find { it.name == "segment" && it instanceof SegmentConfig }
        
        b = new RecordBuilder("record").with {
            length(3,6)
            minOccurs 5
            maxOccurs 10
        }
        c = b.build()
        assert c.minLength == 3
        assert c.maxLength == 6
        assert c.minOccurs == 5
        assert c.maxOccurs == 10
        
        b = new RecordBuilder("record").with {
            length 5
            ridLength 10
        }
        c = b.build()
        assert c.minLength == 5
        assert c.maxLength == 5
        assert c.minMatchLength == 10
        assert c.maxMatchLength == 10
    }
    
    @Test
    void testSegmentBuilder() {
        SegmentBuilder b = new SegmentBuilder("segment")
        
        SegmentConfig c = b.build()
        assert c.name == "segment"
        assert !c.key
        assert !c.target
        assert !c.occursRef
        assert !c.nillable
        assert !c.minOccurs
        assert !c.maxOccurs
        
        b.with {
            occursRef "other"
            key "key"
            value "value"
            nillable()
            occurs 5
            addField(new FieldBuilder("field"))
            addSegment(new SegmentBuilder("segment"))
        }
        
        c = b.build()
        assert c.occursRef == "other"
        assert c.key == "key"
        assert c.target == "value"
        assert c.nillable
        assert c.minOccurs == 5
        assert c.maxOccurs == 5
        assert c.getChildren().find { it.name == "field" && it instanceof FieldConfig }
        assert c.getChildren().find { it.name == "segment" && it instanceof SegmentConfig }
    }
    
    @Test
    void testFieldBuilder() {
        FieldBuilder b = new FieldBuilder("field")
        
        FieldConfig c = b.build()
        assert c.name == "field"
        assert !c.type
        assert !c.collection
        assert !c.getter
        assert !c.setter
        assert c.bound
        assert c.position == null
        assert c.until == null
        assert !c.identifier
        assert !c.trim
        assert !c.required
        assert !c.lazy
        assert !c.occursRef
        assert !c.minOccurs
        assert !c.maxOccurs
        assert !c.minLength
        assert !c.maxLength
        assert !c.length
        assert !c.literal
        assert !c.defaultValue
        assert !c.format
        assert c.justify == "left"
        assert !c.padding
        assert !c.keepPadding
        assert !c.lenientPadding
        assert !c.regex
        assert !c.nillable
        assert !c.xmlType
        assert !c.xmlName
        assert !c.xmlPrefix
        assert !c.xmlNamespace
        assert !c.typeHandler
        
        b.type(Integer.class)
        b.collection(ArrayList.class)
        b.getter("getField")
        b.setter("setField")
        b.ignore()
        b.at(3)
        b.until(-2)
        b.rid()
        b.trim()
        b.required()
        b.lazy()
        b.occursRef("other")
        b.occurs(0, -1)
        b.length(10)
        b.minLength(0)
        b.maxLength(-1)
        b.literal("literal")
        b.defaultValue("default")
        b.format("format")
        b.align(Align.RIGHT)
        b.padding('X' as char)
        b.keepPadding()
        b.lenientPadding()
        b.regex(".*")
        b.nillable()
        b.xmlType(XmlType.ATTRIBUTE)
        b.xmlName("xmlName")
        b.xmlPrefix("prefix")
        b.xmlNamespace("namespace")
        b.typeHandler("typeHandler")
        
        c = b.build()
        assert c.type == "java.lang.Integer"
        assert c.getCollection() == "java.util.ArrayList"
        assert c.getter == "getField"
        assert c.setter == "setField"
        assert !c.bound
        assert c.position == 3
        assert c.until == -2
        assert c.identifier
        assert c.trim
        assert c.required
        assert c.lazy
        assert c.occursRef == "other"
        assert c.minOccurs == 0
        assert c.maxOccurs == Integer.MAX_VALUE
        assert c.minLength == 0
        assert c.length == 10
        // TODO ? assert c.maxLength == Integer.MAX_VALUE
        assert c.literal == "literal"
        assert c.defaultValue == "default"
        assert c.format == "format"
        assert c.justify == "right"
        assert c.padding == 'X'
        assert c.keepPadding
        assert c.lenientPadding
        assert c.regex == ".*"
        assert c.nillable
        assert c.xmlType == "attribute"
        assert c.xmlName == "xmlName"
        assert c.xmlPrefix == "prefix"
        assert c.xmlNamespace == "namespace"
        assert c.typeHandler == "typeHandler"
        
        b = new FieldBuilder("field")
        b.typeHandler(StringTypeHandler.class)
        c = b.build()
        assert c.typeHandler == "org.beanio.types.StringTypeHandler"
        
        StringTypeHandler th = new StringTypeHandler()
        b = new FieldBuilder("field")
        b.typeHandler(th)
        c = b.build()
        assert c.typeHandlerInstance.is(th)
    }
    
    @Test
    void testAlign() {
        assert Align.LEFT.toString() == FieldConfig.LEFT
        assert Align.RIGHT.toString() == FieldConfig.RIGHT
    }
}
