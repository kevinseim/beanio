/*
 * Copyright 2010-2012 Kevin Seim
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
package org.beanio.stream.delimited;

import java.io.*;

import org.beanio.stream.*;

/**
 * A <tt>DelimitedWriter</tt> is used to write records to delimited flat files.
 * Each record must be a String array of fields.  By default, fields are 
 * delimited by the tab character, but any other single character may be configured
 * instead.
 * <p>
 * If an escape character is configured, any field containing the delimiter
 * will be escaped by placing the escape character immediately before the
 * delimiter.  For example, if the record "Field1,2", "Field3" is written
 * using a comma delimiter and backslash escape character, the following text
 * will be written to the output stream:
 * <pre>
 * Field1\,2,Field3
 * </pre>
 * Note that no validation is performed when a record is written, so if an escape character
 * is not configured and a field contains a delimiting character, the generated
 * output may be invalid.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class DelimitedWriter implements RecordWriter {

    private char delim = '\t';
    private char escapeChar = '\\';
    private boolean escapeEnabled = true;
    private String recordTerminator;

    private Writer out;

    /**
     * Constructs a new <tt>DelimitedWriter</tt>.
     * @param out the output stream to write to
     */
    public DelimitedWriter(Writer out) {
        this(out, '\t');
    }

    /**
     * Constructs a new <tt>DelimitedWriter</tt>.  By default, the 
     * escape character is disabled.
     * @param out the output stream to write to
     * @param delimiter the field delimiting character
     */
    public DelimitedWriter(Writer out, char delimiter) {
        this(out, new DelimitedParserConfiguration(delimiter));
    }

    /**
     * Constructs a new <tt>DelimitedWriter</tt>.
     * @param out the output stream to write to
     * @param config the delimited parser configuration
     */
    public DelimitedWriter(Writer out, DelimitedParserConfiguration config) {
        
        this.out = out;
        delim = config.getDelimiter();
        
        if (config.getEscape() == null) {
            escapeEnabled = false;
        }
        else {
            escapeEnabled = true;
            escapeChar = config.getEscape();

            if (delim == escapeChar) {
                throw new IllegalArgumentException("Delimiter cannot match the escape character");
            }
        }
        
        recordTerminator = config.getRecordTerminator();
        if (recordTerminator == null) {
            recordTerminator = System.getProperty("line.separator");
        }
    }

    /* 
     * (non-Javadoc)
     * @see org.beanio.line.RecordWriter#write(java.lang.Object)
     */
    @Override
    public void write(Object value) throws IOException, RecordIOException {
        write((String[]) value);
    }

    /**
     * Writes a record to the output stream.
     * @param record the record to write
     * @throws IOException if an I/O error occurs
     */
    public void write(String[] record) throws IOException {
        if (escapeEnabled) {
            int pos = 0;
            for (String field : record) {
                if (pos++ > 0)
                    out.write(delim);

                char[] cs = field.toCharArray();
                for (int i = 0, j = cs.length; i < j; i++) {
                    if (cs[i] == delim || cs[i] == escapeChar) {
                        out.write(escapeChar);
                    }
                    out.write(cs[i]);
                }
            }
        }
        else {
            int pos = 0;
            for (String field : record) {
                if (pos++ > 0) {
                    out.write(delim);
                }
                out.write(field);
            }
        }

        out.write(recordTerminator);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.line.RecordWriter#flush()
     */
    @Override
    public void flush() throws IOException {
        out.flush();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.line.RecordWriter#close()
     */
    @Override
    public void close() throws IOException {
        out.close();
    }
}
