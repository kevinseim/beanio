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
package org.beanio.internal.compiler.flat;

import java.util.*;

import org.beanio.internal.compiler.*;
import org.beanio.internal.config.*;
import org.beanio.internal.parser.*;
import org.beanio.internal.parser.format.flat.FlatFieldFormat;

/**
 * Base class for flat file format parser factories including CSV, delimited and fixed
 * length formats.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class FlatParserFactory extends ParserFactorySupport {

    @Override
    protected Preprocessor createPreprocessor(StreamConfig config) {
        return new FlatPreprocessor(config);
    }
    
    @Override
    protected void finalizeRecord(RecordConfig config, Record record) {
        super.finalizeRecord(config, record);
        
        // sort nodes according to their position in the record
        //record.sort(new NodeComparator());
    }
    
    @SuppressWarnings("unused")
    private static class NodeComparator implements Comparator<Component> {

        private IdentityHashMap<Component, Integer> cache = new IdentityHashMap<>();
        
        /*
         * (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Component o1, Component o2) {
            return getPosition(o1).compareTo(getPosition(o2));
        }
        
        private Integer getPosition(Component component) {
            Integer p = cache.get(component);
            if (p != null) {
                return p;
            }
            
            if (component instanceof Field) {
                p = ((FlatFieldFormat)((Field)component).getFormat()).getPosition();
            }
            else if (component instanceof Aggregation) {
                p = getPosition(((Aggregation)component).getFirst());
            }
            else if (component instanceof Segment) {
                int n = Integer.MAX_VALUE;
                for (Component child : component.getChildren()) {
                    n = Math.min(getPosition(child), n);
                }
                p = n;
            }
            
            if (p < 0) {
                p = Integer.MAX_VALUE + p;
            }
            
            cache.put(component, p);
            return p;
        }
    }
}
