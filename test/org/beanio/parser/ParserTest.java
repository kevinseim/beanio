/*
 * Copyright 2010-2011 Kevin Seim
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
package org.beanio.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.*;

import org.beanio.*;
import org.beanio.internal.util.IOUtil;

/**
 * Base class for JUnit test classes that test the parsing framework.
 * @author Kevin Seim
 * @since 1.0
 */
public class ParserTest {

    protected static String lineSeparator = System.getProperty("line.separator");
    
    /**
     * Loads the contents of a file into a String.
     * @param filename the name of the file to load
     * @return the file contents
     * @throws IOException if an I/O error occurs
     */
    public String load(String filename) {
        Reader in = new InputStreamReader(getClass().getResourceAsStream(filename));
        StringBuilder s = new StringBuilder();
        try {
            int n = -1;
            char [] c = new char[1024];
            while ((n = in.read(c)) != -1) {
                s.append(c, 0, n);
            }
            return s.toString();
        }
        catch (IOException ex) {
            throw new IllegalStateException("IOException caught", ex);
        }
        finally {
            IOUtil.closeQuietly(in);
        }
    }
    
    /**
     * 
     * @param config
     * @return
     * @throws IOException
     */
    protected StreamFactory newStreamFactory(String config) throws IOException {
        StreamFactory factory = StreamFactory.newInstance();
        loadMappingFile(factory, config);
        return factory;
    }
    
    protected void loadMappingFile(StreamFactory factory, String config) throws IOException {
        InputStream in = getClass().getResourceAsStream(config);
        try {
            factory.load(in);
        }
        finally {
            IOUtil.closeQuietly(in);
        }
    }

    protected void assertRecordError(BeanReader in, int lineNumber, String recordName, String message) {
        try {
            in.read();
            fail("Record expected to fail validation");
        }
        catch (InvalidRecordException ex) {
            assertEquals(recordName, in.getRecordName());
            assertEquals(lineNumber, in.getLineNumber());

            RecordContext ctx = ex.getRecordContext();
            assertEquals(recordName, ctx.getRecordName());
            assertEquals(lineNumber, ctx.getLineNumber());
            for (String s : ctx.getRecordErrors()) {
                assertEquals(message, s);
            }
        }
    }

    protected void assertFieldError(BeanReader in, int lineNumber, String recordName,
            String fieldName, String fieldText, String message) {
        assertFieldError(in, lineNumber, recordName, fieldName, 0, fieldText, message);
    }
    
    protected void assertFieldError(BeanReader in, int lineNumber, String recordName,
        String fieldName, int fieldIndex, String fieldText, String message) {
        try {
            in.read();
            fail("Record expected to fail validation");
        }
        catch (InvalidRecordException ex) {
            assertEquals(recordName, in.getRecordName());
            assertEquals(lineNumber, in.getLineNumber());

            RecordContext ctx = ex.getRecordContext();
            assertEquals(recordName, ctx.getRecordName());
            assertEquals(lineNumber, ctx.getLineNumber());
            assertEquals(fieldText, ctx.getFieldText(fieldName, fieldIndex));
            for (String s : ctx.getFieldErrors(fieldName)) {
                assertEquals(message, s);
            }
        }
    }
}
