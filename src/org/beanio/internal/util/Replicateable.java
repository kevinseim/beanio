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
package org.beanio.internal.util;

import java.util.Map;

/**
 * Graph nodes that use a {@link Replicator} for cloning itself must implement
 * this interface.
 * 
 * @author Kevin Seim
 * @since 2.0
 * @see Replicator
 */
public interface Replicateable extends Cloneable {

    /**
     * Updates a node's references to other nodes.
     * @param map the (identity) map of clones by prior object reference
     */
    public void updateReferences(Map<Object,Object> map);
    
    /**
     * Clones this node.
     * @return the clone
     */
    public Object clone();
    
}
