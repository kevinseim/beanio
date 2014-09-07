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
import org.beanio.internal.compiler.Preprocessor;
import org.beanio.internal.config.*;

/**
 * Configuration {@link Preprocessor} for the JSON stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonPreprocessor extends Preprocessor {

    /**
     * Constructs a new <tt>JsonPreprocessor</tt>.
     * @param stream the {@link StreamConfig} to preprocess
     */
    public JsonPreprocessor(StreamConfig stream) {
        super(stream);
    }

    @Override
    protected void initializeRecord(RecordConfig record) throws BeanIOConfigurationException {
        // default 'jsonName' to the record name
        if (record.getJsonName() == null) {
            record.setJsonName(record.getName());
        }
        
        // default the JSON type to 'none'
        if (record.getJsonType() == null) {
            record.setJsonType(PropertyConfig.JSON_TYPE_NONE);
        }
        else {
            if (record.getJsonType().endsWith("[]")) {
                throw new BeanIOConfigurationException("Invalid jsonType '" + record.getJsonType() + "', [] not supported");
            }
        }
        
        super.initializeRecord(record);
    }
    
    @Override
    protected void initializeSegment(SegmentConfig segment) throws BeanIOConfigurationException {
        super.initializeSegment(segment);
        
        // default the JSON name to the segment name
        if (segment.getJsonName() == null) {
            segment.setJsonName(segment.getName());
        }
        
        // default the JSON type to 'object' if the segment is bound to bean object, or 'none' otherwise
        if (segment.getJsonType() == null) {
            if (segment.getType() != null) {
                segment.setJsonType(PropertyConfig.JSON_TYPE_OBJECT);
                segment.setJsonArray(segment.isRepeating());
            }
            else {
                segment.setJsonType(PropertyConfig.JSON_TYPE_NONE);
            }
        }
        // otherwise validate the type
        else {
            String type = segment.getJsonType();
            if (type.endsWith("[]")) {
                type = type.substring(0, type.length() - 2);
                segment.setJsonArray(true);
            }
            else if (segment.isRepeating() && segment.getComponentType() != ComponentConfig.RECORD) {
                throw new BeanIOConfigurationException("Invalid jsonType '" + segment.getJsonType() + "', expected 'object[]'");
            }
            
            if (!PropertyConfig.JSON_TYPE_OBJECT.equals(type) &&
                !PropertyConfig.JSON_TYPE_NONE.equals(type) &&
                !PropertyConfig.JSON_TYPE_ARRAY.equals(type)) {
                throw new BeanIOConfigurationException("Invalid jsonType '" + segment.getJsonType() + "'");
            }
            
            segment.setJsonType(type);
        }
    }

    @Override
    protected void finalizeSegment(SegmentConfig segment) throws BeanIOConfigurationException {
        super.finalizeSegment(segment);
        
        if (PropertyConfig.JSON_TYPE_ARRAY.equals(segment.getJsonType())) {
            int n = 0;
            for (PropertyConfig property : segment.getPropertyList()) {
                property.setJsonArrayIndex(n++);
            }
        }
    }

    @Override
    protected void handleField(FieldConfig field) throws BeanIOConfigurationException {
        super.handleField(field);
        
        // default the JSON name to the field name
        if (field.getJsonName() == null) {
            field.setJsonName(field.getName());
        }
        
        // validate the JSON type if set
        if (field.getJsonType() != null) {
            String type = field.getJsonType();
            if (type.endsWith("[]")) {
                type = type.substring(0, type.length() - 2);
                field.setJsonArray(true);
            }
            else if (field.isRepeating()) {
                throw new BeanIOConfigurationException("Invalid jsonType '" + field.getJsonType() + "', expected array");
            }
            if (!PropertyConfig.JSON_TYPE_STRING.equals(type) &&
                !PropertyConfig.JSON_TYPE_NUMBER.equals(type) &&
                !PropertyConfig.JSON_TYPE_BOOLEAN.equals(type)) {
                throw new BeanIOConfigurationException("Invalid jsonType '" + field.getJsonType() + "'");
            }
            
            field.setJsonType(type);
        }
        else if (field.isRepeating()) {
            field.setJsonArray(true);
        }
    }
}
