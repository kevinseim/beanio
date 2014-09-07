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
package org.beanio.internal.parser.format.delimited;

import org.beanio.internal.parser.*;
import org.beanio.internal.parser.format.flat.FlatFieldFormatSupport;

/**
 * A {@link FieldFormat} implementation for a field in a delimited stream.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class DelimitedFieldFormat extends FlatFieldFormatSupport implements FieldFormat {

    /**
     * Constructs a new <tt>DelimitedFieldFormat</tt>.
     */
    public DelimitedFieldFormat() { }
    
    @Override
    public void insertFieldText(MarshallingContext context, String fieldText, boolean commit) {
        ((DelimitedMarshallingContext)context).setField(getPosition(), fieldText, commit);
    }
    
    @Override
    public String extractFieldText(UnmarshallingContext context, boolean reporting) {
        return ((DelimitedUnmarshallingContext)context).getFieldText(getName(), getPosition(), getUntil());
    }
}
