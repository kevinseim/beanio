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
package org.beanio.internal.parser;

import java.io.*;
import java.util.*;

import org.beanio.*;
import org.beanio.stream.*;

/**
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class Stream {

    /** Stream definition supports reading and writing */
    public static final int READ_WRITE_MODE = 0;
    /** Stream definition supports reading only */
    public static final int READ_ONLY_MODE = 1;
    /** Stream definition supports writing only */
    public static final int WRITE_ONLY_MODE = 2;
    
    private int mode;
    private StreamFormat format;
    private Selector layout;
    private MessageFactory messageFactory;
    private boolean ignoreUnidentifiedRecords;
    
    private Set<ParserLocal<?>> locals;
    
    /**
     * Constructs a new <tt>Stream</tt>.
     * @param format the {@link StreamFormat}
     */
    public Stream(StreamFormat format) {
        if (format == null) {
            throw new NullPointerException("null format");
        }
        this.format = format;
    }
    
    @SuppressWarnings("unchecked")
    public void init() {
        locals = (Set<ParserLocal<?>>) (Set<?>) new HashSet<ParserLocal<Object>>();
        
        Component parser = ((Component)layout);
        parser.registerLocals(locals);
    }
    
    /**
     * Returns the name of this stream.
     * @return the stream name
     */
    public String getName() {
        return format.getName();
    }
    
    /**
     * Creates a new {@link BeanReader} for reading from the given input stream.
     * @param in the input stream to read from
     * @param locale the locale to use for rendering error messages
     * @return the new {@link BeanReader}
     */
    public BeanReader createBeanReader(Reader in, Locale locale) {
        if (in == null) {
            throw new NullPointerException("null reader");
        }
        
        UnmarshallingContext context = format.createUnmarshallingContext();
        initContext(context);
        context.setMessageFactory(messageFactory);
        context.setLocale(locale);
        context.setRecordReader(format.createRecordReader(in));
        
        BeanReaderImpl reader = new BeanReaderImpl(context, layout);
        reader.setIgnoreUnidentifiedRecords(ignoreUnidentifiedRecords);
        return reader;
    }
    
    /**
     * Creates a new {@link Unmarshaller}.
     * @param locale the {@link Locale} to use for rendering error messages
     * @return the new {@link Unmarshaller}
     */
    public Unmarshaller createUnmarshaller(Locale locale) {
        
        RecordUnmarshaller recordUnmarshaller = format.createRecordUnmarshaller(); 
        if (recordUnmarshaller == null) {
            throw new IllegalArgumentException("Unmarshaller not supported for stream format");
        }
        
        UnmarshallingContext context = format.createUnmarshallingContext();
        initContext(context);
        context.setMessageFactory(messageFactory);
        context.setLocale(locale);
        
        return new UnmarshallerImpl(context, layout, recordUnmarshaller);
    }

    /**
     * Creates a new {@link BeanWriter} for writing to the given output stream.
     * @param out the output stream to write to
     * @return the new {@link BeanWriter}
     */
    public BeanWriter createBeanWriter(Writer out) {
        if (out == null) {
            throw new NullPointerException("null writer");
        }
        
        MarshallingContext context = format.createMarshallingContext(true);
        initContext(context);
        context.setRecordWriter(format.createRecordWriter(out));

        BeanWriterImpl writer = new BeanWriterImpl(context, layout);
        return writer;
    }
    
    /**
     * Creates a new {@link Marshaller}.
     * @return the new {@link Marshaller}
     */
    public Marshaller createMarshaller() {
        RecordMarshaller recordMarshaller = format.createRecordMarshaller(); 
        if (recordMarshaller == null) {
            throw new IllegalArgumentException("Marshaller not supported for stream format");
        }
        
        MarshallingContext context = format.createMarshallingContext(false);
        initContext(context);
        
        return new MarshallerImpl(context, layout, recordMarshaller);
    }
    
    private void initContext(ParsingContext context) {
        context.createHeap(locals.size());
        int i=0;
        for (ParserLocal<?> local : locals) {
            local.init(i++, context);
        }
    }
    
    /**
     * Returns the allowed mode of operation for this stream configuration. 
     * @return {@link #READ_WRITE_MODE} if reading and writing from a stream is allowed,
     *   {@link #READ_ONLY_MODE} if only reading is allowed,
     *   {@link #WRITE_ONLY_MODE} if only writing is allowed
     */
    public int getMode() {
        return mode;
    }

    /**
     * Sets the allowed mode of operation for this stream configuration.
     * @param mode {@link #READ_WRITE_MODE} if reading and writing from a stream is allowed,
     *   {@link #READ_ONLY_MODE} if only reading is allowed,
     *   {@link #WRITE_ONLY_MODE} if only writing is allowed
     */
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public Selector getLayout() {
        return layout;
    }

    public void setLayout(Selector layout) {
        this.layout = layout;
    }

    public StreamFormat getFormat() {
        return format;
    }

    public void setFormat(StreamFormat format) {
        this.format = format;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    /**
     * Returns whether to ignore unidentified records.  Defaults to false.
     * @return true to ignore unidentified records, false otherwise
     * @since 2.0.4
     */
    public boolean isIgnoreUnidentifiedRecords() {
        return ignoreUnidentifiedRecords;
    }

    /**
     * Sets whether to ignore unidentified records.  Defaults to false.
     * @param ignoreUnidentifiedRecords true to ignore unidentified records, false otherwise
     * @since 2.0.4
     */
    public void setIgnoreUnidentifiedRecords(boolean ignoreUnidentifiedRecords) {
        this.ignoreUnidentifiedRecords = ignoreUnidentifiedRecords;
    }
}
