/*
 * Copyright 2011-2012 Kevin Seim
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

import javax.xml.XMLConstants;

import org.beanio.internal.parser.*;
import org.beanio.internal.util.DebugUtil;
import org.beanio.stream.xml.XmlWriter;
import org.w3c.dom.*;

/**
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class XmlWrapper extends DelegatingParser implements XmlNode {

    /* xml attributes */
    private String localName;
    private String prefix;
    private String namespace;
    private boolean namespaceAware;
    private boolean nillable;
    private boolean lazy;
    private boolean repeating;
    
    /**
     * Constructs a new <tt>XmlWrapper</tt>.
     */
    public XmlWrapper() { }
    
    @Override
    public boolean matches(UnmarshallingContext context) {
        if (!isIdentifier()) {
            return true;
        }
        
        XmlUnmarshallingContext ctx = (XmlUnmarshallingContext) context;
        if (ctx.pushPosition(this) == null) {
            return false;
        }
        
        try {
            return super.matches(context);
        }
        finally {
            ctx.popPosition();
        }
    }

    @Override
    public boolean unmarshal(UnmarshallingContext context) {
        XmlUnmarshallingContext ctx = (XmlUnmarshallingContext) context;
        if (ctx.pushPosition(this) == null) {
            return false;
        }
        
        try {
            // check for nil
            if (XmlNodeUtil.isNil(ctx.getPosition())) {
                if (!isNillable()) {
                    context.addFieldError(getName(), null, "nillable");
                }
            }
            else {
                super.unmarshal(context);
            }
            
            return true;
        }
        finally {
            ctx.popPosition();
        }
    }

    @Override
    public boolean marshal(MarshallingContext context) throws IOException {
        boolean contentChecked = false;
        
        if (lazy && !repeating) {
            if (!hasContent(context)) {
                return false;
            }
            contentChecked = true;
        }
        
        XmlMarshallingContext ctx = (XmlMarshallingContext) context;
        
        // create an element for this node
        Element element = ctx.getDocument().createElementNS(getNamespace(), getLocalName());
        if (!isNamespaceAware()) {
            element.setUserData(XmlWriter.IS_NAMESPACE_IGNORED, Boolean.TRUE, null);
        }
        else {
            if ("".equals(getPrefix())) {
                element.setUserData(XmlWriter.IS_DEFAULT_NAMESPACE, Boolean.TRUE, null);
            }
            else {
                element.setPrefix(getPrefix());
            }
        }
        
        // append the new element to its parent
        Node parent = ctx.getParent();
        parent.appendChild(element);
        
        // if nillable and there is no descendant with content, mark the element nil
        if (isNillable() && !contentChecked && !hasContent(context)) {
            element.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil", "true");
        }
        // otherwise marshal our descendants
        else {
            ctx.setParent(element);
            super.marshal(context);
            ctx.setParent(parent);
        }
        
        return true;
    }

    @Override
    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    @Override
    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    @Override
    public int getType() {
        return XmlNode.XML_TYPE_ELEMENT;
    }
    
    @Override
    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }
    
    @Override
    public boolean isOptional() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    @Override
    protected void toParamString(StringBuilder s) {
        super.toParamString(s);
        
        s.append(", element=").append(localName);
        if (prefix != null) {
            s.append(", prefix=").append(prefix);
        }
        if (namespace != null) {
            s.append(", xmlns=").append(isNamespaceAware() ? namespace : "*");
        }
        s.append(", ").append(DebugUtil.formatOption("lazy", lazy));
        s.append(", ").append(DebugUtil.formatOption("nillable", nillable));
        s.append(", ").append(DebugUtil.formatOption("repeating", repeating));
    }
}
