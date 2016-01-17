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
package org.beanio.internal.parser;

import java.io.IOException;
import java.util.*;

import org.beanio.internal.util.DebugUtil;

/**
 * Base class for parser components capable of aggregating descendant properties.
 * 
 * @author Kevin Seim
 * @since 2.0.1
 */
public abstract class Aggregation extends DelegatingParser implements Property, Iteration {

    // minimum occurrences
    private int minOccurs = 0;
    // maximum occurrences
    private int maxOccurs = Integer.MAX_VALUE;
    // the property accessor, may be null if not bound
    private PropertyAccessor accessor;
    // true if null should be returned for an empty collection
    protected boolean lazy;
    // the property that dictates the number of occurrences or null if its not dynamic
    protected Field occurs;
    
    // the current iteration index
    private ParserLocal<Integer> index = new ParserLocal<>();
    
    /**
     * Constructs a new <tt>Aggregation</tt>.
     */
    public Aggregation() { }
    
    /**
     * Returns whether this aggregation is a property of
     * its parent bean object.
     * @return true if this a property, false otherwise
     */
    public abstract boolean isProperty();
    
    /**
     * Returns the length of aggregation.
     * @param value the aggregation value
     * @return the length
     */
    protected abstract int length(Object value);
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Marshaller#marshal(org.beanio.parser2.MarshallingContext)
     */
    @Override
    public final boolean marshal(MarshallingContext context) throws IOException {
        
        int min = minOccurs;
        int max = maxOccurs;
        
        // handle dynamic occurrences
        if (occurs != null) {
            min = max = ((Number)occurs.getValue(context)).intValue();
            setIterationIndex(context, -1);
        }
        
        return marshal(context, getParser(), min, max);
    }
    
    protected abstract boolean marshal(MarshallingContext context, Parser delegate, int minOccurs, int maxOccurs) throws IOException;
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Field#unmarshal(org.beanio.parser2.UnmarshallingContext)
     */
    @Override
    public final boolean unmarshal(UnmarshallingContext context) {
        
        int min = minOccurs;
        int max = maxOccurs;
        
        // handle dynamic occurrences
        if (occurs != null) {
            Object n = occurs.getValue(context);
            if (n == Value.INVALID) {
                throw new AbortRecordUnmarshalligException("Invalid occurences");
            }
            else if (n == Value.MISSING) {
                n = 0;
            }
            int occursVal = ((Number)n).intValue();
            if (occursVal < minOccurs) {
                context.addFieldError(getName(), null, "minOccurs", minOccurs, maxOccurs);
                // this prevents a duplicate exception being thrown by a parent segment:
                if (occursVal == 0) {
                    return true;
                }
            }
            else if (occursVal > maxOccurs) {
                context.addFieldError(getName(), null, "maxOccurs", minOccurs, maxOccurs);
            }
            min = max = occursVal;
            setIterationIndex(context, -1);
        }
        
        return unmarshal(context, getParser(), min, max);
    }
    
    protected abstract boolean unmarshal(UnmarshallingContext context, Parser delegate, int minOccurs, int maxOccurs);
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.DelegatingParser#setValue(java.lang.Object)
     */
    @Override
    public void setValue(ParsingContext context, Object value) {
        if (occurs != null && !occurs.isBound()) {
            occurs.setValue(context, length(value));
        }
    }
    
    @Override
    public boolean isOptional() {
        return minOccurs == 0;
    }
    
    /*
     * Returns false.  Iterations cannot be used to identify records.
     */
    @Override
    public boolean isIdentifier() {
        return false;
    }
    
    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public void setIdentifier(boolean identifier) { 
        if (identifier) {
            throw new UnsupportedOperationException();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#getAccessor()
     */
    @Override
    public PropertyAccessor getAccessor() {
        return accessor;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#setAccessor(org.beanio.internal.parser.PropertyAccessor)
     */
    @Override
    public void setAccessor(PropertyAccessor accessor) {
        this.accessor = accessor;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Iteration#getIterationIndex()
     */
    @Override
    public final int getIterationIndex(ParsingContext context) {
        return index.get(context);
    }
    
    protected final void setIterationIndex(ParsingContext context, int index) {
        this.index.set(context, index);
    }
    
    public int getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    public int getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }
    
    public Field getOccurs() {
        return occurs;
    }

    public void setOccurs(Field occurs) {
        this.occurs = occurs;
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }
    
    @Override
    public boolean isDynamicIteration() {
        return occurs != null;
    }
    
    @Override
    public void registerLocals(Set<ParserLocal<? extends Object>> locals) {
        if (locals.add(index)) {
            super.registerLocals(locals);
        }
    }

    @Override
    protected void toParamString(StringBuilder s) {
        super.toParamString(s);
        if (occurs != null) {
            s.append("occursRef=$").append(occurs.getName());
        }
        s.append(", occurs=").append(DebugUtil.formatRange(minOccurs, maxOccurs));
        s.append(", ").append(DebugUtil.formatOption("lazy", lazy));
    }
}
