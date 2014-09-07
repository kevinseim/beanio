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
package org.beanio.builder;

import org.beanio.internal.config.GroupConfig;

/**
 * Builds a new group configuration.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public class GroupBuilder extends GroupBuilderSupport<GroupBuilder> {
    
    protected GroupConfig config;
    
    public GroupBuilder(String name) {
        config = new GroupConfig();
        config.setName(name);
    }

    @Override
    protected GroupBuilder me() {
        return this;
    }
    
    @Override
    protected GroupConfig getConfig() {
        return config;
    }

    /**
     * Sets the order of this group relative to other children
     * of the same parent.
     * @param order the order
     * @return this
     */
    public GroupBuilder order(int order) {
        config.setOrder(order);
        return this;
    }
    
    /**
     * Builds the group configuration.
     * @return the group configuration
     */
    public GroupConfig build() {
        return config;
    }
}
