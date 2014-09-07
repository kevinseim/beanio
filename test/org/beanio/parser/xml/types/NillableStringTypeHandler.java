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
package org.beanio.parser.xml.types;

import org.beanio.types.*;

/**
 * Custom String type handler for testing NIL's.
 * @author Kevin Seim
 * @since 1.2
 */
public class NillableStringTypeHandler extends StringTypeHandler implements TypeHandler {

    @Override
    public String format(Object value) {
        if ("nil".equals(value)) {
            return TypeHandler.NIL;
        }
        return super.format(value);
    }
    
}
