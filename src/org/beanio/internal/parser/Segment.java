/*
 * Copyright 2011-2013 Kevin Seim
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
package org.beanio.internal.parser;

import java.io.IOException;
import java.util.*;

import org.beanio.internal.util.DebugUtil;

/**
 * A segment is used aggregate other {@link Parser} components, such as fields
 * and other segments.
 * 
 * <p>A segment may be bound to a {@link Property} by calling
 * {@link #setProperty(Property)}.
 * 
 * <p>Repeating segments will always marshal a value when {@link #marshal(MarshallingContext)} 
 * is called.  If not repeating, lazy segments are only marshalled if {@link #hasContent(ParsingContext)} 
 * returns true. 
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class Segment extends ParserComponent {

    // the size of one occurence of the segment
    private int size;
    // true if the segment is optional during unmarshalling
    private boolean optional;
    // true if existence is known to be true when unmarshall is called
    private boolean existencePredetermined;
    // true if any descendant is used to identify the record
    private boolean identifier;
    // true if the segment repeats
    private boolean repeating;
    // the property bound to this segment, may be null
    private Property property;
    // temporarily stores missing children during unmarshalling
    private ParserLocal<List<Parser>> missing = new ParserLocal<List<Parser>>() {
        @Override
        protected List<Parser> createDefaultValue() {
            return new ArrayList<>();
        }
    };
    
    /**
     * Constructs a new <tt>Segment</tt>.
     */
    public Segment() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#clearValue()
     */
    @Override
    public void clearValue(ParsingContext context) {
        if (property != null) {
            property.clearValue(context);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Parser#matches(org.beanio.parser2.UnmarshallingContext)
     */
    @Override
    public boolean matches(UnmarshallingContext context) {
        if (isIdentifier()) {
            for (Component node : getChildren()) {
                if (!((Parser)node).matches(context)) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Parser#unmarshal(org.beanio.parser2.UnmarshallingContext)
     */
    @Override
    public boolean unmarshal(UnmarshallingContext context) {
        List<Parser> missing = this.missing.get(context);
        
        // unmarshals all children and determine existence,
        // if a child exists, the segment must exist
        // existence may also be predetermined in any tag based format (such as XML)
        boolean exists = isExistencePredetermined();
        for (Component node : getChildren()) {
            Parser parser = (Parser)node;
            
            if (parser.unmarshal(context)) {
                exists = true;
            }
            else if (!parser.isOptional()) {
                missing.add(parser);
            }
        }
        
        // validate all required children are present if either the segment
        // exists or the segment itself is required
        if (exists || !optional) {
            // validate there are no missing children
            if (missing.isEmpty()) {
                // if the segment valid and bound to a property, create the property value
                if (property != null) {
                    property.createValue(context);
                }
            }
            // otherwise create appropriate field errors for missing children
            else {
                for (Parser parser : missing) {
                    context.addFieldError(parser.getName(), null, "minOccurs", 1);
                }
            }
        }

        missing.clear();
        
        return exists;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Parser#marshal(org.beanio.parser2.MarshallingContext)
     */
    @Override
    public boolean marshal(MarshallingContext context) throws IOException {
        // since we allow Collections containing a null reference to force
        // output of a bean, we also check that we are not repeating
        if (optional && !repeating) {
            if (!hasContent(context)) {
                return false;
            }
        }
        
        for (Component node : getChildren()) {
            ((Parser)node).marshal(context);
        }
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#hasContent()
     */
    @Override
    public boolean hasContent(ParsingContext context) {
        if (property != null) {
            return property.getValue(context) != Value.MISSING;
        }
        
        for (Component c : getChildren()) {
            if (((Parser)c).hasContent(context)) {
                return true;
            }
        }
        
        return false;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Parser#getValue()
     */
    @Override
    public Object getValue(ParsingContext context) {
        // getValue() may be called for a record where no property is set
        if (property == null) {
            return null;
        }
        else {
            return property.getValue(context);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Parser#setValue(java.lang.Object)
     */
    @Override
    public void setValue(ParsingContext context, Object value) {
        if (property != null) {
            property.setValue(context, value);
        }
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Parser#isLazy()
     */
    @Override
    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Parser#getSize()
     */
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public boolean isIdentifier() {
        return identifier;
    }

    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public void registerLocals(Set<ParserLocal<?>> locals) {
        if (property != null) {
            ((Component)property).registerLocals(locals);
        }
        
        if (locals.add(missing)) {
            super.registerLocals(locals);
        }
    }
    
    public boolean isExistencePredetermined() {
        return existencePredetermined;
    }

    public void setExistencePredetermined(boolean existencePredetermined) {
        this.existencePredetermined = existencePredetermined;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }
    
    @Override
    protected void toParamString(StringBuilder s) {
        super.toParamString(s);
        s.append(", size=").append(size == Integer.MAX_VALUE ? "unbounded" : Integer.toString(getSize()));
        s.append(", ").append(DebugUtil.formatOption("rid", identifier));
        s.append(", ").append(DebugUtil.formatOption("repeating", repeating));
        s.append(", ").append(DebugUtil.formatOption("optional", isOptional()));
        if (property != null) {
            if (property instanceof Field) {
                s.append(", property=$").append(property.getName());
            }
            else {
                s.append(", property=").append(property);
            }
        }
    }
}
