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
package org.beanio.util;

import java.io.*;

import org.beanio.internal.util.IOUtil;
import org.junit.Test;

/**
 * JUnit test cases for the <tt>IOUtil</tt> class.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class IOUtilTest {

    @Test
    public void testCloseReader() {
        Reader in = new FilterReader(new StringReader("value")) {
            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        };
        IOUtil.closeQuietly(in);
        IOUtil.closeQuietly(new StringReader("value"));
    }
    
    @Test
    public void testCloseWriter() {
        Writer out = new StringWriter() {
            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        };
        IOUtil.closeQuietly(out);
        IOUtil.closeQuietly(new StringWriter());
    }
    
    @Test
    public void testCloseInputStream() {
        byte [] b = new byte[] { 1, 2, 3, 4 };
        InputStream in = new ByteArrayInputStream(b) {
            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        };
        IOUtil.closeQuietly(in);
        IOUtil.closeQuietly(new ByteArrayInputStream(b));
    }
    
    @Test
    public void testCloseOutputStream() {
        OutputStream out = new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        };
        IOUtil.closeQuietly(out);
        IOUtil.closeQuietly(new ByteArrayOutputStream());
    }
}