/*
 * Copyright 2012 Kevin Seim
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
package org.beanio.internal.compiler.json;

import org.beanio.BeanIOConfigurationException;
import org.beanio.internal.compiler.*;
import org.beanio.internal.config.*;
import org.beanio.internal.parser.*;
import org.beanio.internal.parser.format.json.*;
import org.beanio.stream.RecordParserFactory;
import org.beanio.stream.json.JsonRecordParserFactory;

/**
 * A {@link ParserFactory} for the JSON stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonParserFactory extends ParserFactorySupport {

    // depth starts at one to accomodate the root JSON object
    private int maxDepth = 1;
    
    /**
     * Constructs a new <tt>JsonParserFactory</tt>.
     */
    public JsonParserFactory() { }
    
    @Override
    protected Preprocessor createPreprocessor(StreamConfig config) {
        return new JsonPreprocessor(config);
    }
    
    @Override
    public Stream createStream(StreamConfig config) throws BeanIOConfigurationException {
        Stream stream =  super.createStream(config);
        ((JsonStreamFormat)stream.getFormat()).setMaxDepth(maxDepth);
        return stream;
    }

    @Override
    protected void initializeSegmentIteration(SegmentConfig config, Property property) {
        JsonWrapper wrapper = new JsonWrapper();
        wrapper.setName(config.getName());
        wrapper.setJsonName(config.getJsonName());
        wrapper.setJsonType(JsonNode.ARRAY);
        wrapper.setJsonArrayIndex(config.getJsonArrayIndex());
        wrapper.setNillable(true);
        wrapper.setOptional(config.getMinOccurs().equals(0));
        pushParser(wrapper);
        maxDepth++;
        
        super.initializeSegmentIteration(config, property);
    }

    @Override
    protected void finalizeSegmentIteration(SegmentConfig config, Property property) { 
        super.finalizeSegmentIteration(config, property);
        popParser(); // pop the wrapper
    }

    @Override
    protected void initializeSegmentMain(SegmentConfig config, Property property) {
        if (isWrappingRequired(config)) {
            JsonWrapper wrapper = new JsonWrapper();
            wrapper.setName(config.getName());
            wrapper.setJsonName(config.getJsonName());
            wrapper.setJsonType(convertJsonType(config.getJsonType()));
            wrapper.setJsonArrayIndex(config.getJsonArrayIndex());
            wrapper.setNillable(true);
            wrapper.setOptional(config.getMinOccurs().equals(0));
            pushParser(wrapper);
            maxDepth++;
        }
        super.initializeSegmentMain(config, property);
    }
    
    @Override
    protected Property finalizeSegmentMain(SegmentConfig config) throws BeanIOConfigurationException {
        Property property = super.finalizeSegmentMain(config);
        if (isWrappingRequired(config)) {
            popParser(); // pop the wrapper
        }
        return property;
    }
    private boolean isWrappingRequired(SegmentConfig config) {
        return !PropertyConfig.JSON_TYPE_NONE.equals(config.getJsonType());
    }
    
    @Override
    protected boolean isSegmentRequired(SegmentConfig config) {
        if (config.isConstant()) {
            return false;
        }
        else if (config.getType() != null) {
            return true;
        }
        else if (config.getChildren().size() > 1) {
            return true;
        }
        return false;
    }
    
    private char convertJsonType(String type) {
        if (PropertyConfig.JSON_TYPE_ARRAY.equals(type)) {
            return JsonNode.ARRAY;
        }
        else if (PropertyConfig.JSON_TYPE_OBJECT.equals(type)) {
            return JsonNode.OBJECT;
        }
        else {
            throw new BeanIOConfigurationException("Invalid jsonType '" + type + "'");
        }
    }
    
    @Override
    protected StreamFormat createStreamFormat(StreamConfig config) {
        JsonStreamFormat format = new JsonStreamFormat();
        format.setName(config.getName());
        format.setRecordParserFactory(createRecordParserFactory(config));
        return format;
    }

    @Override
    protected RecordFormat createRecordFormat(RecordConfig config) {
        return null;
    }

    @Override
    protected FieldFormat createFieldFormat(FieldConfig config, Class<?> type) {
        
        JsonFieldFormat format = new JsonFieldFormat();
        format.setName(config.getName());
        format.setJsonName(config.getJsonName());
        format.setJsonArray(config.isJsonArray());
        format.setJsonArrayIndex(config.getJsonArrayIndex());
        format.setLazy(config.getMinOccurs() != null && Integer.valueOf(0).equals(config.getMinOccurs()));
        format.setNillable(true); // for now, allow any JSON field to be nullable
        
        // default the JSON type based on the property type
        if (config.getJsonType() == null) {
            if (Number.class.isAssignableFrom(type)) {
                format.setBypassTypeHandler(true);
                format.setJsonType(JsonNode.NUMBER);
            }
            else if (Boolean.class.isAssignableFrom(type)) {
                format.setBypassTypeHandler(true);
                format.setJsonType(JsonNode.BOOLEAN);
            }
            else {
                format.setBypassTypeHandler(false);
                format.setJsonType(JsonNode.STRING);
            }
        }
        // or set if explicitly configured
        else {
            String jsonType = config.getJsonType();
            if (PropertyConfig.JSON_TYPE_BOOLEAN.equals(jsonType)) {
                format.setJsonType(JsonNode.BOOLEAN);
                format.setBypassTypeHandler(Boolean.class.isAssignableFrom(type));
            }
            else if (PropertyConfig.JSON_TYPE_NUMBER.equals(jsonType)) {
                format.setJsonType(JsonNode.NUMBER);
                format.setBypassTypeHandler(Number.class.isAssignableFrom(type));
            }
            else {
                format.setJsonType(JsonNode.STRING);
                format.setBypassTypeHandler(false);
            }
        }
        
        return format;
    }

    @Override
    protected RecordParserFactory getDefaultRecordParserFactory() {
        return new JsonRecordParserFactory();
    }
}
