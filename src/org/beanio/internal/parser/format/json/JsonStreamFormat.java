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
package org.beanio.internal.parser.format.json;

import org.beanio.internal.parser.*;

/**
 * A {@link StreamFormatSupport} implementation for the JSON stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonStreamFormat extends StreamFormatSupport implements StreamFormat {

    private int maxDepth;
    
    /**
     * Constructs a new <tt>JsonStreamFormat</tt>.
     */
    public JsonStreamFormat() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.StreamFormat#createUnmarshallingContext()
     */
    @Override
    public UnmarshallingContext createUnmarshallingContext() {
        return new JsonUnmarshallingContext(maxDepth);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.StreamFormat#createMarshallingContext(boolean)
     */
    @Override
    public MarshallingContext createMarshallingContext(boolean streaming) {
        return new JsonMarshallingContext(maxDepth);
    }

    /**
     * Returns the maximum depth of the all {@link JsonWrapper} components in the parser tree layout. 
     * @return the maximum depth
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Sets the maximum depth of the all {@link JsonWrapper} components in the parser tree layout.
     * @param maxDepth the maximum depth
     */
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
