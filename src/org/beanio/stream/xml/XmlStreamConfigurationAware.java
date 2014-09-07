/*
 * Copyright 2011 Kevin Seim
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
package org.beanio.stream.xml;

/**
 * This callback interface can be implemented by <tt>RecordReader</tt> implementations
 * for XML formatted streams that wish to obtain configuration information from the
 * XML stream definition.
 * 
 * @author Kevin Seim
 * @since 1.1
 */
public interface XmlStreamConfigurationAware {

    /**
     * This method is invoked by a XML stream definition when a <tt>RecordReader</tt>
     * implementation is registered.
     * @param configuration the XML stream configuration
     */
    public void setConfiguration(XmlStreamConfiguration configuration);
    
}
