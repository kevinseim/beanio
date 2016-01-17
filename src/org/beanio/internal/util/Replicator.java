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

import java.util.*;

/**
 * Utility class used to copy a hierarchical object graph.
 * 
 * <p>If all nodes in the graph implement {@link Replicateable} and are registered
 * using {@link #register(Replicateable)}, calling {@link #replicate(Object)}
 * and passing the root node in the graph will clone all registered nodes and
 * return a copy of the root node with references to copies of its descendents.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class Replicator {

    private Set<Replicateable> archetypes = new HashSet<>();
    
    /**
     * Constructs a new <tt>Replicator</tt>.
     */
    public Replicator() { }
    
    /**
     * Registers a node in a graph that can be copied.
     * @param node the graph node to register
     */
    public void register(Replicateable node) {
        archetypes.add(node);
    }
    
    /**
     * Returns a copy of the registered graph node and its descendants.
     * This method will clone all registered nodes allow each node to update
     * its references. 
     * @param <T> the node type
     * @param value the root graph node
     * @return a copy of the root graph node and its descendants
     * @throws IllegalStateException if <tt>value</tt> is not a registered node
     */
    @SuppressWarnings("unchecked")
    public <T> T replicate(T value) {
        if (!archetypes.contains(value)) {
            throw new IllegalStateException("value not found");
        }
        
        // create a map of archetypes --> clones, aka old reference --> new reference
        IdentityHashMap<Object, Object> clones = new IdentityHashMap<>();
        
        // clone all registered archetypes and add their reference to the map
        for (Replicateable archetype : archetypes) {
            clones.put(archetype, archetype.clone());
        }
        
        // maps old objects to new versions
        for (Object clone : clones.values()) {
            ((Replicateable)clone).updateReferences(clones);
        }
        
        return (T) clones.get(value);
    }
}
