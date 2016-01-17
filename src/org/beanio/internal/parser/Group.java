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

import org.beanio.*;
import org.beanio.internal.util.DebugUtil;

/**
 * A Group holds child nodes including records and other groups.
 * This class is the dynamic counterpart to the <tt>GroupDefinition</tt> and
 * holds the current state of a group node during stream processing. 
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class Group extends ParserComponent implements Selector {

    /* map key used to store the state of the 'lastMatchedChild' attribute */
    private static final String LAST_MATCHED_KEY = "lastMatched";
    
    private int minOccurs = 0;
    private int maxOccurs = Integer.MAX_VALUE;
    private int order = 1;
    private Property property = null;
    // the current group count
    private ParserLocal<Integer> count = new ParserLocal<>(0);
    // the last matched child
    private ParserLocal<Selector> lastMatched = new ParserLocal<>();
    
    /**
     * Constructs a new <tt>Group</tt>.
     */
    public Group() { 
        super(5);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Marshaller#marshal(org.beanio.parser2.MarshallingContext)
     */
    @Override
    public boolean marshal(MarshallingContext context) throws IOException {
        // this method is only invoked when this group is configured to
        // marshal a bean object that spans multiple records
        
        boolean marshalled = false;
        for (Component node : getChildren()) {
            marshalled = ((Parser)node).marshal(context) || marshalled;
        }
        
        return marshalled;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#skip(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public void skip(UnmarshallingContext context) {
        // this method is only invoked when this group is configured to
        // unmarshal a bean object that spans multiple records
        
        try {
            Selector child = (Selector) lastMatched.get(context);
            child.skip(context);
            
            // read the next record
            while (true) {
                context.nextRecord();
                
                if (context.isEOF()) {
                    Selector unsatisfied = close(context);
                    if (unsatisfied != null) {
                        throw context.newUnsatisfiedRecordException(unsatisfied.getName());
                    }
                    break;
                }
                
                // find the child unmarshaller for the record...
                child = (Selector) matchCurrent(context);
                if (child == null) {
                    reset(context);
                    break;
                }
                
                child.skip(context);
            }
        }
        catch (UnsatisfiedNodeException ex) {
            throw context.newUnsatisfiedRecordException(ex.getNode().getName());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Unmarshaller#unmarshal(org.beanio.parser2.UnmarshallingContext)
     */
    @Override
    public boolean unmarshal(UnmarshallingContext context) {
        // this method is only invoked when this group is configured to
        // unmarshal a bean object that spans multiple records
        
        try {
            Selector child = (Selector) lastMatched.get(context);
            child.unmarshal(context);
            
            // read the next record
            while (true) {
                context.nextRecord();
                
                if (context.isEOF()) {
                    Selector unsatisfied = close(context);
                    if (unsatisfied != null) {
                        throw context.newUnsatisfiedRecordException(unsatisfied.getName());
                    }
                    break;
                }
                
                // find the child unmarshaller for the record...
                child = (Selector) matchCurrent(context);
                if (child == null) {
                    reset(context);
                    break;
                }
                
                try {
                    child.unmarshal(context);
                }
                catch (AbortRecordUnmarshalligException ex) { }
            }
            
            if (property != null) {
                property.createValue(context);
            }
            
            return true;
        }
        catch (UnsatisfiedNodeException ex) {
            throw context.newUnsatisfiedRecordException(ex.getNode().getName());
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.RecordMatcher#matchAny(org.beanio.parser2.UnmarshallingContext)
     */
    @Override
    public Selector matchAny(UnmarshallingContext context) {
        for (Component n : getChildren()) {
            Selector node = (Selector) n;
            
            Selector match = node.matchAny(context);
            if (match != null) {
                return match;
            }
        }
        return null;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.RecordMatcher#matchNext(org.beanio.parser2.UnmarshallingContext)
     */
    @Override
    public Selector matchNext(UnmarshallingContext context) {
        try {
            return internalMatchNext(context);
        }
        catch (UnsatisfiedNodeException ex) {
            throw context.newUnsatisfiedRecordException(ex.getNode().getName());
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.RecordMatcher#matchNextBean(java.lang.Object)
     */
    @Override
    public Selector matchNext(MarshallingContext context) {
        try {
            if (property == null) {
                return internalMatchNext(context);
            }
            else {
                String componentName = context.getComponentName();
                if (componentName != null && !getName().equals(componentName)) {
                    return null;
                }
                
                Object value = context.getBean();
                if (property.defines(value)) {
                    property.setValue(context, value);
                    return this;
                }
                
                return null;
            }
        }
        catch (UnsatisfiedNodeException ex) {
            throw new BeanWriterException("Bean identification failed: expected record type '" + 
                ex.getNode().getName() + "'", ex);
        }
    }
    
    /**
     * 
     * @return
     * @throws UnsatisfiedNodeException
     */
    private Selector internalMatchNext(ParsingContext context) throws UnsatisfiedNodeException {
        /*
         * A matching record is searched for in 3 stages:
         * 1.  First, we give the last matching node an opportunity to match the next 
         *     record if it hasn't reached it's max occurs.
         * 2.  Second, we search for another matching node at the same position/order
         *     or increment the position until we find a matching node or a min occurs
         *     is not met.
         * 3.  Finally, if all nodes in this group have been satisfied and this group
         *     hasn't reached its max occurs, we search nodes from the beginning again
         *     and increment the group count if a node matches.
         *     
         * If no match is found, there SHOULD be no changes to the state of this node.
         */
        
        //Selector last = this.lastMatched.get(context);
        //System.out.println("Group '" + getName() + "', lastMatched=" +
        //    (last == null ? "null" : last.getName()) + ", count=" + getCount(context));
        
        Selector match = matchCurrent(context);
        if (match == null && maxOccurs > 1) {
            match = matchAgain(context);
        }
        if (match != null) {
            return property != null ? this : match;
        }
        return null;
    }
    
    /**
     * 
     * @return
     * @throws UnsatisfiedNodeException
     */
    private Selector matchCurrent(ParsingContext context) throws UnsatisfiedNodeException {
        Selector match = null;
        Selector lastMatch = this.lastMatched.get(context);
        Selector unsatisfied = null;
        
        // check the last matching node - do not check records where the max occurs
        // has already been reached
        if (lastMatch != null && !(lastMatch.isMaxOccursReached(context))) {
            match = matchNext(context, lastMatch);
            if (match != null) {
                return match;
            }
        }
        
        // set the current position to the order of the last matched node (or default to 1)
        int position = (lastMatch == null) ? 1 : lastMatch.getOrder();
        
        // iterate over each child
        for (Component child : getChildren()) {
            Selector node = (Selector) child;
            
            // skip the last node which was already checked
            if (node == lastMatch) {
                continue;
            }
            // skip nodes where their order is less than the current position
            if (node.getOrder() < position) {
                continue;
            }
            // skip nodes where max occurs has already been met
            if (node.isMaxOccursReached(context)) {
                continue;
            }
            // if no node matched at the current position, increment the position and test the next node
            if (node.getOrder() > position) {
                // before increasing the position, we must validate that all
                // min occurs have been met at the previous position
                if (unsatisfied != null) {
                    if (lastMatch != null) {
                        throw new UnsatisfiedNodeException(unsatisfied);
                    }
                    return null;
                }

                position = node.getOrder();
            }

            // if the min occurs has not been met for the next node, set the unsatisfied flag so we
            // can throw an exception before incrementing the position again
            if (node.getCount(context) < node.getMinOccurs()) {
                // when marshalling, allow records to be skipped that aren't bound to a property
                if (context.getMode() != ParsingContext.MARSHALLING || node.getProperty() != null) {
                    unsatisfied = node;    
                }
            }
            
            // search the child node for a match
            match = matchNext(context, node);
            if (match != null) {
                // the group count is incremented only when first invoked
                if (lastMatch == null) {
                	count.set(context, count.get(context) + 1);
                }
                // reset the last group when a new record or group is found
                // at the same level (this has no effect for a record)
                else {
                    lastMatch.reset(context);
                }
                lastMatched.set(context, node);
                return match;
            }
        }
        
        // if last was not null, we continued checking for matches at the current position, now
        // we'll check for matches at the beginning (assuming there is no unsatisfied node)
        if (lastMatch != null) {
            if (unsatisfied != null) {
                throw new UnsatisfiedNodeException(unsatisfied);
            }
        }
        
        return null;
    }
    
    /**
     * 
     * @return
     */
    private Selector matchAgain(ParsingContext context) {

        Selector match = null;
        Selector unsatisfied = null;
        int position = 1;
        
        if (lastMatched.get(context) != null) {
            
            // no need to check if the max occurs was already reached
            if (getCount(context) >= getMaxOccurs()) {
                return null;
            }
            
            // if there was no unsatisfied node and we haven't reached the max occurs, 
            // try to find a match from the beginning again so that the parent can 
            // skip this node
            position = 1;
            for (Component child : getChildren()) {
                Selector node = (Selector) child;
                
                if (node.getOrder() > position) {
                    if (unsatisfied != null) {
                        return null;
                    }
                    position = node.getOrder();
                }

                if (node.getMinOccurs() > 0) {
                    // when marshalling, allow records to be skipped that aren't bound to a property
                    if (context.getMode() != ParsingContext.MARSHALLING || node.getProperty() != null) {
                        unsatisfied = node;    
                    }
                }

                match = matchNext(context, node);
                if (match != null) {
                    // this is different than reset() because we reset every node
                    // except the one that matched...
                    for (Component c : getChildren()) {
                        if (c == node) {
                            continue;
                        }
                        Selector sel = (Selector) c;
                        sel.setCount(context, 0);
                        sel.reset(context);
                    }
                    
                    count.set(context, count.get(context) + 1);
                    node.setCount(context, 1);
                    lastMatched.set(context, node);
                    
                    return match;
                }
            }
        }

        return null;
    }
    
    /**
     * Matches the next record or bean depending on the type of parsing context.
     * @param context the parsing context
     * @param child the child Selector to invoke
     * @return the matched Selector
     */
    private Selector matchNext(ParsingContext context, Selector child) {
        switch (context.getMode()) {
            case ParsingContext.MARSHALLING:
                return child.matchNext((MarshallingContext) context);
            case ParsingContext.UNMARSHALLING:
                return child.matchNext((UnmarshallingContext) context);
            default:
                throw new IllegalStateException("Invalid mode: " + context.getMode());
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#reset()
     */
    @Override
    public void reset(ParsingContext context) {
        lastMatched.set(context, null);
        for (Component c : getChildren()) {
            Selector node = (Selector) c;
            node.setCount(context, 0);
            node.reset(context);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.RecordMatcher#close()
     */
    @Override
    public Selector close(ParsingContext context) {
        Selector lastMatch = lastMatched.get(context);
        
        if (lastMatch == null && getMinOccurs() == 0) {
            return null;
        }
        
        int pos = lastMatch == null ? 1 : lastMatch.getOrder();
        
        Selector unsatisfied = findUnsatisfiedChild(context, pos);
        if (unsatisfied != null) {
            return unsatisfied;
        }
        
        if (getCount(context) < getMinOccurs()) {
            // try to find a specific record before reporting any record from this group
            if (pos > 1) {
                reset(context);
                unsatisfied = findUnsatisfiedChild(context, 1);
                if (unsatisfied != null) {
                    return unsatisfied;
                }
            }
            
            return this;
        }
        
        return null;
    }
    
    private Selector findUnsatisfiedChild(ParsingContext context, int from) {
        // find any unsatisfied child
        for (Component c : getChildren()) {
            Selector node = (Selector) c;
            if (node.getOrder() < from) {
                continue;
            }

            Selector unsatisfied = node.close(context);
            if (unsatisfied != null) {
                return unsatisfied;
            }
        }
        
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Unmarshaller#matches(org.beanio.parser2.UnmarshallingContext)
     */
    @Override
    public boolean matches(UnmarshallingContext context) {
        return false;
    }
    
    /**
     * Tests if the max occurs has been reached for this node.
     * @return true if max occurs has been reached
     */
    @Override
    public boolean isMaxOccursReached(ParsingContext context) {
        return lastMatched.get(context) == null && getCount(context) >= getMaxOccurs();
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Unmarshaller#getSize()
     */
    @Override
    public int getSize() {
        return -1;
    }
    
    /**
     * Updates a Map with the current state of the Marshaller.  Used for
     * creating restartable Writers for Spring Batch.
     * @param namespace a String to prefix all state keys with
     * @param state the Map to update with the latest state
     * @since 1.2
     */
    @Override
    public void updateState(ParsingContext context, String namespace, Map<String, Object> state) {
        state.put(getKey(namespace, COUNT_KEY), count.get(context));
        
        String lastMatchedChildName = "";
        Selector lastMatch = lastMatched.get(context);
        if (lastMatch != null) {
            lastMatchedChildName = lastMatch.getName();
        }
        state.put(getKey(namespace, LAST_MATCHED_KEY), lastMatchedChildName);

        // allow children to update their state
        for (Component node : this) {
            ((Selector)node).updateState(context, namespace, state);
        }
    }

    /**
     * Restores a Map of previously stored state information.  Used for
     * restarting XML writers from Spring Batch.
     * @param namespace a String to prefix all state keys with
     * @param state the Map containing the state to restore
     * @since 1.2
     */
    @Override
    public void restoreState(ParsingContext context, String namespace, Map<String, Object> state) {
        String key = getKey(namespace, COUNT_KEY);
        Integer n = (Integer) state.get(key);
        if (n == null) {
            throw new IllegalStateException("Missing state information for key '" + key + "'");
        }
        this.count.set(context, n);
        
        // determine the last matched child
        key = getKey(namespace, LAST_MATCHED_KEY);
        String lastMatchedChildName = (String) state.get(key);
        if (lastMatchedChildName == null) {
            throw new IllegalStateException("Missing state information for key '" + key + "'");
        }
        
        if (lastMatchedChildName.length() == 0) {
            lastMatched.set(context, null);
            lastMatchedChildName = null;
        }
        
        // allow children to restore their state
        for (Component child : getChildren()) {
            if (lastMatchedChildName != null && 
                lastMatchedChildName.equals(child.getName())) {
                lastMatched.set(context, (Selector)child);
            }
            ((Selector)child).restoreState(context, namespace, state);
        }
    }
    
    /**
     * Returns a Map key for accessing state information for this Node.
     * @param namespace the assigned namespace for the key
     * @param name the state information to access
     * @return the fully qualified key
     */
    protected String getKey(String namespace, String name) {
        return namespace + "." + getName() + "." + name;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#isRecordGroup()
     */
    @Override
    public boolean isRecordGroup() {
        return true;
    }

    @Override
    public int getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    @Override
    public int getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#getCount()
     */
    @Override
    public int getCount(ParsingContext context) {
        return count.get(context);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#setCount(int)
     */
    @Override
    public void setCount(ParsingContext context, int count) {
        this.count.set(context, count);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Parser#clearValue()
     */
    @Override
    public void clearValue(ParsingContext context) {
        if (property != null) {
            property.clearValue(context);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Parser#setValue(java.lang.Object)
     */
    @Override
    public void setValue(ParsingContext context, Object value) {
        property.setValue(context, value);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Parser#getValue()
     */
    @Override
    public Object getValue(ParsingContext context) {
        return property.getValue(context);
    }
    
    
    @Override
    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#isLazy()
     */
    @Override
    public boolean isOptional() {
        return minOccurs == 0;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#isIdentifier()
     */
    @Override
    public boolean isIdentifier() {
        return false;
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
    
    @Override
    public void registerLocals(Set<ParserLocal<?>> locals) {
        if (property != null) {
            ((Component)property).registerLocals(locals);
        }
        
        if (locals.add(lastMatched)) {
            locals.add(count);
            super.registerLocals(locals);
        }
    }
    
    
    @Override
    protected boolean isSupportedChild(Component child) {
        return child instanceof Selector;
    }
    
    @Override
    protected void toParamString(StringBuilder s) {
        super.toParamString(s);
        s.append(", order=").append(order);
        s.append(", occurs=").append(DebugUtil.formatRange(minOccurs, maxOccurs));
        if (property != null) {
            s.append(", property=").append(property);
        }
    }

    @SuppressWarnings("serial")
    private static class UnsatisfiedNodeException extends Exception {
        private Selector node;
        public UnsatisfiedNodeException(Selector node) {
            this.node = node;
        }
        
        public Selector getNode() {
            return node;
        }
    }
}
