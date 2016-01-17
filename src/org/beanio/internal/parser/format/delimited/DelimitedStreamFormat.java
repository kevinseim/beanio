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
package org.beanio.internal.parser.format.delimited;

import org.beanio.internal.parser.*;

/**
 * A {@link StreamFormatSupport} implementation for the delimited stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class DelimitedStreamFormat extends StreamFormatSupport {

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.StreamFormat#createUnmarshallingContext()
     */
    @Override
    public UnmarshallingContext createUnmarshallingContext() {
        return new DelimitedUnmarshallingContext();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.StreamFormat#createMarshallingContext()
     */
    @Override
    public MarshallingContext createMarshallingContext(boolean streaming) {
        return new DelimitedMarshallingContext();
    }
}
