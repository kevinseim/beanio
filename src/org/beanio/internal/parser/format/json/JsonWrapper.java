/*
 * Copyright 2012-2013 Kevin Seim
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
package org.beanio.internal.parser.format.json;

import java.io.IOException;

import org.beanio.internal.parser.*;

/**
 * A <tt>JsonWrapper</tt> is used to handle nested JSON objects.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonWrapper extends DelegatingParser implements JsonNode {

    private String jsonName;
    /* the JSON type: object or array */
    private char jsonType;
    /* whether the segment is mapped to a JSON array */
    private boolean jsonArray;
    /* set to the index of this segment in a JSON array, or -1 if the segment itself repeats */
    private int jsonArrayIndex = -1;
    /* whether the segment may be explicitly set to 'null' */
    private boolean nillable;
    private boolean optional;
    
    /**
     * Constructs a new <tt>JsonWrapper</tt>.
     */
    public JsonWrapper() { }
    
    @Override
    public boolean matches(UnmarshallingContext context) {
        if (!isIdentifier()) {
            return true;
        }
        
        JsonUnmarshallingContext ctx = (JsonUnmarshallingContext) context;
        if (ctx.push(this, false) == null) {
            return false;
        }
        
        try {
            return super.matches(context);
        }
        finally {
            ctx.pop();
        }
    }

    @Override
    public boolean unmarshal(UnmarshallingContext context) {
        JsonUnmarshallingContext ctx = (JsonUnmarshallingContext) context;
        if (ctx.push(this, true) == null) {
            return false;
        }
        
        try {
            super.unmarshal(context);
            return true;
        }
        finally {
            ctx.pop();
        }
    }

    @Override
    public boolean marshal(MarshallingContext context) throws IOException {
        boolean contentChecked = false;
        
        if (optional ) {
            if (!hasContent(context)) {
                return false;
            }
            contentChecked = true;
        }
        
        JsonMarshallingContext ctx = (JsonMarshallingContext) context;
        
        // if nillable and there is no descendant with content, mark the element nil
        if (isNillable() && !contentChecked && !hasContent(context)) {
            ctx.put(this, null);
        }
        else {
            ctx.push(this);
            try {
                super.marshal(context);
            }
            finally {
                ctx.pop();
            }
        }
        
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.json.JsonNode#getJsonName()
     */
    @Override
    public String getJsonName() {
        return jsonName;
    }

    public void setJsonName(String jsonName) {
        this.jsonName = jsonName;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.json.JsonNode#getJsonType()
     */
    @Override
    public char getJsonType() {
        return jsonType;
    }

    public void setJsonType(char jsonType) {
        this.jsonType = jsonType;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.json.JsonNode#isJsonArray()
     */
    @Override
    public boolean isJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(boolean jsonArray) {
        this.jsonArray = jsonArray;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.json.JsonNode#getJsonArrayIndex()
     */
    @Override
    public int getJsonArrayIndex() {
        return jsonArrayIndex;
    }

    public void setJsonArrayIndex(int jsonArrayIndex) {
        this.jsonArrayIndex = jsonArrayIndex;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.json.JsonNode#isNillable()
     */
    @Override
    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.DelegatingParser#isLazy()
     */
    @Override
    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    protected void toParamString(StringBuilder s) {
        super.toParamString(s);
        
        s.append(", jsonName=").append(jsonName);
        s.append(", jsonType=").append(jsonType);
        if (isJsonArray()) {
            s.append("[]");
        }
        if (jsonArrayIndex >= 0) {
            s.append(", jsonArrayIndex=").append(jsonArrayIndex);
        }
        s.append(", optional=").append(optional);
        s.append(", nillable=").append(nillable);
    }

    
}
