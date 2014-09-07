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
package org.beanio.internal.compiler.fixedlength;

import org.beanio.internal.compiler.Preprocessor;
import org.beanio.internal.compiler.flat.FlatPreprocessor;
import org.beanio.internal.config.*;

/**
 * Configuration {@link Preprocessor} for a fixed length stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FixedLengthPreprocessor extends FlatPreprocessor {

    /**
     * Constructs a new <tt>FixedLengthPreprocessor</tt>.
     * @param stream the stream configuration to pre-process
     */
    public FixedLengthPreprocessor(StreamConfig stream) {
        super(stream);
    }

    @Override
    protected int getSize(FieldConfig field) {
        return field.getLength();
    }
    
    @Override
    protected boolean isFixedLength() {
        return true;
    }
}
