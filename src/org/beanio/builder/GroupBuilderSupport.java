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

import org.beanio.BeanIOConfigurationException;
import org.beanio.internal.config.*;
import org.beanio.internal.config.annotation.AnnotationParser;

/**
 * Support for group configuration builders.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 * @param <T> the GroupBuilder subclass
 */
public abstract class GroupBuilderSupport<T extends GroupBuilderSupport<T>> extends PropertyBuilderSupport<T> {

    GroupBuilderSupport() { }
    
    @Override
    protected abstract GroupConfig getConfig();
    
    public T addGroup(GroupBuilder group) throws BeanIOConfigurationException {
        getConfig().add(group.build());
        return me();
    }
    
    public T addGroup(Class<?> group) throws BeanIOConfigurationException {
        GroupConfig gc = AnnotationParser.createGroupConfig(group);
        if (gc == null) {
            throw new BeanIOConfigurationException("Group annotation not detected on class '" + group.getName() + "'");
        }
        getConfig().add(gc);
        return me();
    }
    
    public T addRecord(RecordBuilder record) {
        getConfig().add(record.build());
        return me();
    }
    
    public T addRecord(Class<?> record) throws BeanIOConfigurationException {
        RecordConfig rc = AnnotationParser.createRecordConfig(record);
        if (rc == null) {
            throw new BeanIOConfigurationException("Record annotation not detected on class '" + record.getName() + "'");
        }
        getConfig().add(rc);
        return me();
    }
}
