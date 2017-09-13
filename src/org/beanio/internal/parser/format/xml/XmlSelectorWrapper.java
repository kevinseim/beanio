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
package org.beanio.internal.parser.format.xml;

import java.io.IOException;
import java.util.*;

import org.beanio.internal.parser.*;
import org.beanio.internal.util.*;
import org.beanio.stream.xml.*;
import org.w3c.dom.*;

/**
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlSelectorWrapper extends ParserComponent implements Selector, XmlNode {

    /* map key used to store the state of the 'addToHierarchy' attribute */
    private static final String WRITTEN_KEY = "written";
    
    /* state attributes */
    private ParserLocal<Boolean> written = new ParserLocal<>(false);
    
    /* marshalling flags */
    private boolean group;
    private int depth;
    
    /* xml node attributes */
    private String localName;
    private String prefix;
    private String namespace;
    private boolean namespaceAware;
    
    /**
     * Constructs a new <tt>XmlSelectorWrapper</tt>.
     */
    public XmlSelectorWrapper() { 
        super(1);
    }
    
    /**
     * Creates a DOM made up of all <tt>XmlSelectorWrapper</tt> descendants that wrap
     * a group or record.
     * @return the created {@link Document}
     */
    public Document createBaseDocument() {
        Document document = DomUtil.newDocument();
        createBaseDocument(document, document, this);
        return document;
    }
    private void createBaseDocument(Document document, Node parent, Component node) {
        if (node instanceof XmlSelectorWrapper) {
            XmlSelectorWrapper wrapper = (XmlSelectorWrapper) node;
            if (!wrapper.isGroup()) {
                return;
            }
            Element element = document.createElementNS(wrapper.getNamespace(), wrapper.getLocalName());
            parent.appendChild(element);
            
            if (!wrapper.isNamespaceAware()) {
                element.setUserData(XmlReader.IS_NAMESPACE_IGNORED, Boolean.TRUE, null);
            }
            else {
                if ("".equals(getPrefix())) {
                    element.setUserData(XmlWriter.IS_DEFAULT_NAMESPACE, Boolean.TRUE, null);
                }
                else {
                    element.setPrefix(getPrefix());
                }
            }
            
            parent = element;
        }
    
        for (Component child : node.getChildren()) {
            createBaseDocument(document, parent, child);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Marshaller#marshal(org.beanio.parser2.MarshallingContext)
     */
    @Override
    public boolean marshal(MarshallingContext context) throws IOException {
        XmlMarshallingContext ctx = (XmlMarshallingContext) context;
        
        Node parent = ctx.getParent();
        Node node = parent.appendChild(
            ctx.getDocument().createElementNS(getNamespace(), getLocalName()));
        if (group && ctx.isStreaming()) {
            node.setUserData(XmlWriter.IS_GROUP_ELEMENT, Boolean.TRUE, null);
        }
        if (!isNamespaceAware()) {
            node.setUserData(XmlWriter.IS_NAMESPACE_IGNORED, Boolean.TRUE, null);
        }
        else {
            if ("".equals(getPrefix())) {
                node.setUserData(XmlWriter.IS_DEFAULT_NAMESPACE, Boolean.TRUE, null);
            }
            else {
                node.setPrefix(getPrefix());
            }
        }
        
        ctx.setParent(node);
        
        boolean b = getDelegate().marshal(context);
        
        if (group && ctx.isStreaming()) {
            ((XmlMarshallingContext)context).closeGroup(this);
        }
        ctx.setParent(null);
        
        return b;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#unmarshal(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public boolean unmarshal(UnmarshallingContext context) {
        return getDelegate().unmarshal(context);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#skip(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public void skip(UnmarshallingContext context) {
        getDelegate().skip(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#matchNextBean(org.beanio.internal.parser.MarshallingContext, java.lang.Object)
     */
    @Override
    public Selector matchNext(MarshallingContext context) {
        XmlMarshallingContext ctx = (XmlMarshallingContext) context;
        
        // stores the initial count before calling matchNext()...
        int initialCount = getCount(context);
        
        Selector match = getDelegate().matchNext(context);
        if (match == null) {
            if (written.get(context)) {
                written.set(context, false);
                ctx.closeGroup(this);
            }
            return null;
        }
        
        if (group) {

            // if not marshalling to a stream, a group is always appended to the document by calling openGroup()
            if (ctx.isStreaming()) {
                // if the group count increased, we need to close the current group
                // element (by calling remove) and adding a new one
                
                boolean w = written.get(context);
                if (w && getCount(context) > initialCount) {
                    ctx.closeGroup(this);
                    written.set(context, false);
                    w = false;
                }
                if (!w) {
                    ctx.openGroup(this);
                    written.set(context, true);
                }
            }
            else {
                ctx.openGroup(this);
            }
            return match;
        }
        else {
            return this;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#matchNext(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public Selector matchNext(UnmarshallingContext context) {
        return match(context, true);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#matchAny(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public Selector matchAny(UnmarshallingContext context) {
        return match(context, false);
    }
    
    /**
     * Matches a child {@link Selector}.
     * @param context the {@link UnmarshallingContext}
     * @param stateful whether to check the state of the matched child
     * @return the matched {@link Selector}, or null if no match was made
     */
    private Selector match(UnmarshallingContext context, boolean stateful) {
        // validate the next element in the document matches this record
        XmlUnmarshallingContext ctx = (XmlUnmarshallingContext) context;

        // update the position in the DOM tree (if null the node is matched)
        Element matchedDomNode = ctx.pushPosition(this, depth, group);
        if (matchedDomNode == null) {
            return null;
        }
        
        Selector match = null;
        try {
            if (stateful) {
                // get the number of times this node was read from the stream for comparing to our group count
                Integer n = (Integer) matchedDomNode.getUserData(XmlReader.GROUP_COUNT);
                /*
                    if the group count is null, it means we expected a group and got a record, therefore no match
                    if (n == null) {
                        return null;
                    }
                */
                if (n != null && n > getCount(context)) {
                    if (isMaxOccursReached(context)) {
                        return null;
                    }
                    setCount(context, n);
                    reset(context);
                }
            }
            
            // continue matching now that we've updated the DOM position...
            match = getDelegate().matchNext(context);
            
            return match;
        }
        finally {
            // if there was no match, reset the DOM position
            if (match == null) {
                ctx.popPosition();
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#matches(org.beanio.internal.parser.UnmarshallingContext)
     */
    @Override
    public boolean matches(UnmarshallingContext context) {
        // a group is never used to match a record
        return false;
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
        state.put(getKey(namespace, WRITTEN_KEY), written.get(context));
        
        // allow children to update their state
        for (Component node : getChildren()) {
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
        String key = getKey(namespace, WRITTEN_KEY); 
        Boolean written = (Boolean) state.get(key);
        if (written == null) {
            throw new IllegalStateException("Missing state information for key '" + key + "'");
        }
        this.written.set(context, written);
        
        // allow children to restore their state
        for (Component child : getChildren()) {
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
    
    /**
     * Returns the child selector of this component wraps.
     * @return the child {@link Selector}
     */
    private final Selector getDelegate() {
        return (Selector) getFirst();
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#getValue()
     */
    @Override
    public Object getValue(ParsingContext context) {
        return getDelegate().getValue(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#setValue(java.lang.Object)
     */
    @Override
    public void setValue(ParsingContext context, Object value) {
        getDelegate().setValue(context, value);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#getSize()
     */
    @Override
    public int getSize() {
        return getDelegate().getSize();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#close(org.beanio.internal.parser.ParsingContext)
     */
    @Override
    public Selector close(ParsingContext context) {
        return getDelegate().close(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#reset(org.beanio.internal.parser.ParsingContext)
     */
    @Override
    public void reset(ParsingContext context) {
        written.set(context, false);
        getDelegate().reset(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#getCount(org.beanio.internal.parser.ParsingContext)
     */
    @Override
    public int getCount(ParsingContext context) {
        return getDelegate().getCount(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#setCount(org.beanio.internal.parser.ParsingContext, int)
     */
    @Override
    public void setCount(ParsingContext context, int count) {
        getDelegate().setCount(context, count);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#getMinOccurs()
     */
    @Override
    public int getMinOccurs() {
        return getDelegate().getMinOccurs();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#getMaxOccurs()
     */
    @Override
    public int getMaxOccurs() {
        return getDelegate().getMaxOccurs();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#getOrder()
     */
    @Override
    public int getOrder() {
        return getDelegate().getOrder();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#isMaxOccursReached(org.beanio.internal.parser.ParsingContext)
     */
    @Override
    public boolean isMaxOccursReached(ParsingContext context) {
        return getDelegate().isMaxOccursReached(context);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#clearValue()
     */
    @Override
    public void clearValue(ParsingContext context) {
        getDelegate().clearValue(context);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#isIdentifier()
     */
    @Override
    public boolean isIdentifier() {
        return getDelegate().isIdentifier();
    }

    
    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#isLazy()
     */
    @Override
    public boolean isOptional() {
        return getDelegate().isOptional();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getLocalName()
     */
    @Override
    public String getLocalName() {
        return localName;
    }


    public void setLocalName(String localName) {
        this.localName = localName;
    }


    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getNamespace()
     */
    @Override
    public String getNamespace() {
        return namespace;
    }

    /**
     * 
     * @param namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getPrefix()
     */
    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * 
     * @param prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#isNamespaceAware()
     */
    @Override
    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    /**
     * 
     * @param namespaceAware
     */
    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#getType()
     */
    @Override
    public int getType() {
        return XmlNode.XML_TYPE_ELEMENT;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#isNillable()
     */
    @Override
    public boolean isNillable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.format.xml.XmlNode#isRepeating()
     */
    @Override
    public boolean isRepeating() {
        return false;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#getProperty()
     */
    @Override
    public Property getProperty() {
        return getDelegate().getProperty();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#hasContent()
     */
    @Override
    public boolean hasContent(ParsingContext context) {
        return getDelegate().hasContent(context);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Selector#isRecordGroup()
     */
    @Override
    public boolean isRecordGroup() {
        return false;
    }
    
    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    @Override
    public void registerLocals(Set<ParserLocal<?>> locals) {
        if (locals.add(written)) {
            super.registerLocals(locals);
        }
    }

    @Override
    protected void toParamString(StringBuilder s) {
        super.toParamString(s);
        s.append(", depth=").append(depth);
        s.append(", group=").append(group);
        s.append(", localName=").append(localName);
        if (prefix != null) {
            s.append(", prefix=").append(prefix);
        }
        if (namespace != null) {
            s.append(", xmlns=").append(isNamespaceAware() ? namespace : "*");
        }
    }
}