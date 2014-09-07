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
package org.beanio;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Locale;

import org.beanio.internal.DefaultStreamFactory;
import org.junit.Test;

/**
 * JUnit test cases for testing the <tt>StreamFactory</tt> and its default
 * implementation.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class StreamFactoryTest {
    
    @Test
    public void testLoadMappingFile() throws Exception {
        StreamFactory factory = StreamFactory.newInstance();
        
        String filename = "test/org/beanio/mapping.xml";
        File file = new File(filename);
        factory.load(file);
        factory.load(filename);
        factory.loadResource("org/beanio/mapping.xml");
    }
    
    @Test(expected=BeanIOException.class)
    public void testMappingFileNotFound() {
        StreamFactory factory = StreamFactory.newInstance();
        factory.load("/org/beanio/mapping-notfound.xml");
    }
    
    @Test
    public void testInvalidStreamName() throws IOException {
        StreamFactory factory = StreamFactory.newInstance();
        
        // assert we get the default instance
        assertEquals(factory.getClass(), org.beanio.internal.DefaultStreamFactory.class);
        
        // load the mapping file
        factory.load(getClass().getResourceAsStream("mapping.xml"));
        
        // remove 'stream2'
        assertNotNull(((DefaultStreamFactory)factory).removeStream("stream2"));
        
        try {
            factory.createReader("stream2", new StringReader(""));
            fail("expected IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) { }
        try {
            factory.createReader("stream2", new StringReader(""), new Locale("en"));
            fail("expected IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) { }
        try {
            factory.createWriter("stream2", new StringWriter());
            fail("expected IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) { }
    }
    
    @Test
    public void testCreateReaderForFile() {
        StreamFactory factory = StreamFactory.newInstance();
        factory.loadResource("org/beanio/mapping.xml");
        
        File file = new File("test/org/beanio/file.txt");
        BeanReader in = factory.createReader("stream1", file);
        try {
            while (in.read() != null);
        }
        finally {
            in.close();
        }
    }
    
    @Test(expected=BeanIOException.class)
    public void testInputFileNotFound() {
        StreamFactory factory = StreamFactory.newInstance();
        factory.load("/org/beanio/mapping.xml");
        factory.createReader("stream1", new File("test/org/beanio/filenotfound.txt"));
    }
    
    @Test
    public void testCreateWriterForFile() throws IOException {
        StreamFactory factory = StreamFactory.newInstance();
        factory.loadResource("org/beanio/mapping.xml");
        
        File file = File.createTempFile("temp", "txt");
        file.deleteOnExit();
        
        BeanWriter out = factory.createWriter("stream1", file);
        out.flush();
        out.close();
    }
}
