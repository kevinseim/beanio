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
package org.beanio.internal.compiler.fixedlength;

import org.beanio.internal.compiler.*;
import org.beanio.internal.compiler.flat.FlatParserFactory;
import org.beanio.internal.config.*;
import org.beanio.internal.parser.*;
import org.beanio.internal.parser.format.FieldPadding;
import org.beanio.internal.parser.format.fixedlength.*;
import org.beanio.stream.*;
import org.beanio.stream.fixedlength.*;

/**
 * A {@link ParserFactory} for the fixed length stream format.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FixedLengthParserFactory extends FlatParserFactory {

    /**
     * Constructs a new <tt>FixedLengthParserFactory</tt>.
     */
    public FixedLengthParserFactory() { }
    
    @Override
    protected Preprocessor createPreprocessor(StreamConfig config) {
        return new FixedLengthPreprocessor(config);
    }

    @Override
    public StreamFormat createStreamFormat(StreamConfig config) {
        FixedLengthStreamFormat format = new FixedLengthStreamFormat();
        format.setName(config.getName());
        format.setRecordParserFactory(createRecordParserFactory(config));
        return format;
    }

    @Override
    public RecordFormat createRecordFormat(RecordConfig config) {
        FixedLengthRecordFormat format = new FixedLengthRecordFormat();
        if (config.getMinLength() != null) {
            format.setMinLength(config.getMinLength());
        }
        if (config.getMaxLength() != null) {
            format.setMaxLength(config.getMaxLength());
        }
        if (config.getMinMatchLength() != null) {
            format.setMinMatchLength(config.getMinMatchLength());
        }
        if (config.getMaxMatchLength() != null) {
            format.setMaxMatchLength(config.getMaxMatchLength());
        }
        return format;
    }

    @Override
    public FieldFormat createFieldFormat(FieldConfig config, Class<?> type) {
        FixedLengthFieldFormat format = new FixedLengthFieldFormat();
        format.setName(config.getName());
        format.setPosition(config.getPosition());
        format.setUntil(config.getUntil() == null ? 0 : config.getUntil());
        format.setLazy(config.getMinOccurs().equals(0));
        format.setKeepPadding(config.isKeepPadding());
        format.setLenientPadding(config.isLenientPadding());
        
        FieldPadding padding = new FixedLengthFieldPadding();
        padding.setLength(config.getLength());
        padding.setFiller(config.getPadding());
        padding.setJustify(FieldConfig.RIGHT.equals(config.getJustify()) ? FieldPadding.RIGHT : FieldPadding.LEFT);
        padding.setOptional(!config.isRequired());
        padding.setPropertyType(type);
        padding.init();
        format.setPadding(padding);
        
        return format;
    }

    @Override
    protected RecordParserFactory getDefaultRecordParserFactory() {
        return new FixedLengthRecordParserFactory();
    }
}
