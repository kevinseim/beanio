/*
 * Copyright 2010-2013 Kevin Seim
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

import java.io.*;
import java.util.*;

import org.beanio.builder.StreamBuilder;
import org.beanio.internal.util.*;

/**
 * A <tt>StreamFactory</tt> is used to load BeanIO mapping files and create 
 * {@link BeanReader}, {@link BeanWriter}, {@link Unmarshaller} and {@link Marshaller} instances.
 * <p>
 * The default <tt>StreamFactory</tt> implementation can be safely shared 
 * across multiple threads.
 * 
 * @author Kevin Seim
 * @since 1.0
 * @see BeanReader
 * @see BeanWriter
 * @see Unmarshaller
 * @see Marshaller
 */
public abstract class StreamFactory {

    private ClassLoader classLoader;
    
    /**
     * Constructs a new <tt>StreamFactory</tt>.
     */
    public StreamFactory() { }

    /**
     * Creates a new <tt>BeanReader</tt> for reading from a file.
     * @param name the name of the stream in the mapping file
     * @param filename the name of the file to read
     * @return the created {@link BeanReader}
     * @throws IllegalArgumentException if there is no stream configured for the given name, or
     *   if the stream mapping mode does not support reading an input stream
     * @throws BeanReaderIOException if the file could not be opened for reading
     */
    public BeanReader createReader(String name, String filename) throws IllegalArgumentException, BeanReaderIOException {
        return createReader(name, new File(filename));
    }
    
    /**
     * Creates a new <tt>BeanReader</tt> for reading from a file.
     * @param name the name of the stream in the mapping file
     * @param file the {@link File} to read
     * @return the created {@link BeanReader}
     * @throws IllegalArgumentException if there is no stream configured for the given name, or
     *   if the stream mapping mode does not support reading an input stream
     * @throws BeanReaderIOException if the file could not be opened for reading
     */
    public BeanReader createReader(String name, File file) throws IllegalArgumentException, BeanReaderIOException {
        if (!isMapped(name)) {
            throw new IllegalArgumentException("No stream mapping configured for name '" + name + "'");
        }
        
        Reader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            return createReader(name, in);
        }
        catch (IOException ex) {
            IOUtil.closeQuietly(in);
            throw new BeanReaderException("Failed to open file '" + file + "' for reading", ex);
        }
        catch (RuntimeException ex) {
            IOUtil.closeQuietly(in);
            throw ex;            
        }
    }

    /**
     * Creates a new <tt>BeanReader</tt> for reading from the given input stream.
     * @param name the name of the stream in the mapping file
     * @param in the input stream to read from
     * @return the created {@link BeanReader}
     * @throws IllegalArgumentException if there is no stream configured for the given name, or
     *   if the stream mapping mode does not support reading an input stream
     */
    public BeanReader createReader(String name, Reader in) throws IllegalArgumentException {
        return createReader(name, in, Locale.getDefault());
    }

    /**
     * Creates a new <tt>BeanReader</tt> for reading from a stream.
     * @param name the name of the stream in the mapping file
     * @param in the input stream to read from
     * @param locale the {@link Locale} used to format error messages, or null to use {@link Locale#getDefault()}
     * @return the created {@link BeanReader}
     * @throws IllegalArgumentException if there is no stream configured for the given name, or
     *   if the stream mapping mode does not support reading an input stream
     */
    public abstract BeanReader createReader(String name, Reader in, Locale locale)
        throws IllegalArgumentException;

    /**
     * Creates a new {@link Unmarshaller} for unmarshalling records.
     * @param name the name of the stream in the mapping file
     * @return the created {@link Unmarshaller}
     * @throws IllegalArgumentException if there is no stream configured for the given name, or
     *   if the stream mapping mode does not support unmarshalling
     */    
    public Unmarshaller createUnmarshaller(String name) throws IllegalArgumentException {
        return createUnmarshaller(name, null);
    }
    
    /**
     * Creates a new {@link Unmarshaller} for unmarshalling records.
     * @param name the name of the stream in the mapping file
     * @param locale the {@link Locale} used to format error messages, or null to use {@link Locale#getDefault()}
     * @return the created {@link Unmarshaller}
     * @throws IllegalArgumentException if there is no stream configured for the given name, or
     *   if the stream mapping mode does not support unmarshalling
     */
    public abstract Unmarshaller createUnmarshaller(String name, Locale locale);
    
    /**
     * Creates a new <tt>BeanWriter</tt> for writing to the given file.
     * @param name the name of the stream in the mapping file
     * @param file the file to write to
     * @return the created {@link BeanWriter}
     * @throws IllegalArgumentException if there is no stream configured for the given name, or
     *   if the stream mapping mode does not support writing to an output stream
     * @throws BeanWriterIOException if the file could not be opened for writing
     */
    public BeanWriter createWriter(String name, File file) throws IllegalArgumentException, BeanWriterIOException {
        if (!isMapped(name)) {
            throw new IllegalArgumentException("No stream mapping configured for name '" + name + "'");
        }
        
        Writer out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));
            return createWriter(name, out);
        }
        catch (IOException ex) {
            IOUtil.closeQuietly(out);
            throw new BeanWriterIOException("Failed to open file '" + file + "' for writing", ex);
        }
        catch (RuntimeException ex) {
            IOUtil.closeQuietly(out);
            throw ex;
        }
    }

    /**
     * Creates a new <tt>BeanWriter</tt> for writing to a stream.
     * @param name the name of the stream in the mapping file
     * @param out the output stream to write to
     * @return the created {@link BeanWriter}
     * @throws IllegalArgumentException if there is no stream configured for the given name, or
     *   if the stream mapping mode does not support writing to an output stream
     */
    public abstract BeanWriter createWriter(String name, Writer out)
        throws IllegalArgumentException;

    /**
     * Creates a new {@link Marshaller} for marshalling bean objects.
     * @param name the name of the stream in the mapping file
     * @return the created {@link Marshaller}
     * @throws IllegalArgumentException if there is no stream configured for the given name, or
     *   if the stream mapping mode does not support marshalling
     */
    public abstract Marshaller createMarshaller(String name) throws IllegalArgumentException;
    
    /**
     * Defines a new stream mapping.
     * @param builder the {@link StreamBuilder}
     * @throws BeanIOConfigurationException if the stream builder is not valid
     * @since 2.1.0
     */
    public abstract void define(StreamBuilder builder) throws BeanIOConfigurationException;
    
    /**
     * Loads a BeanIO mapping file from the application's classpath.
     * @param resource the configuration resource name
     * @throws BeanIOException if an {@link IOException} or other fatal error is caught while loading the file
     * @throws BeanIOConfigurationException if the mapping file is not found or invalid
     */
    public void loadResource(String resource) throws BeanIOException, BeanIOConfigurationException {
        loadResource(resource, null);
    }
    
    /**
     * Loads a BeanIO mapping file from the application's classpath.
     * @param resource the configuration resource name
     * @param properties user {@link Properties} for property substitution
     * @throws BeanIOException if an {@link IOException} or other fatal error is caught while loading the file
     * @throws BeanIOConfigurationException if the mapping file is not found or invalid
     */
    public void loadResource(String resource, Properties properties) throws BeanIOException, BeanIOConfigurationException {
        InputStream in = null;
        try {
            in = getClassLoader().getResourceAsStream(resource);
            if (in == null) {
                throw new BeanIOConfigurationException("BeanIO mapping file '" + resource + "' not found on classpath");
            }
            load(in, properties);
        }
        catch (IOException ex) {
            throw new BeanIOException("Failed to load resource '" + resource + "' from classpath", ex);
        }
        finally {
            IOUtil.closeQuietly(in);
        }
    }
    
    /**
     * Loads a BeanIO mapping file from the file system, and adds the configured streams to this factory.
     * @param filename the name of the BeanIO configuration file to load
     * @throws BeanIOException if an {@link IOException} or other fatal error is caught while loading the file
     * @throws BeanIOConfigurationException if the mapping file is invalid
     */
    public void load(String filename) throws BeanIOException, BeanIOConfigurationException {
        load(filename, null);
    }

    /**
     * Loads a BeanIO mapping file from the file system, and adds the configured streams to this factory.
     * @param filename the name of the BeanIO configuration file to load
     * @param properties user {@link Properties} for property substitution
     * @throws BeanIOException if an {@link IOException} or other fatal error is caught while loading the file
     * @throws BeanIOConfigurationException if the mapping file is invalid
     */
    public void load(String filename, Properties properties) throws BeanIOException, BeanIOConfigurationException {
        load(new File(filename), properties);
    }
    
    /**
     * Loads a BeanIO mapping file from the file system, and adds the configured streams to this factory.
     * @param file the BeanIO configuration file to load
     * @throws BeanIOException if an {@link IOException} or other fatal error is caught while loading the file
     * @throws BeanIOConfigurationException if the mapping file is invalid
     */
    public void load(File file) throws BeanIOException, BeanIOConfigurationException {
        load(file, null);
    }
    
    /**
     * Loads a BeanIO mapping file from the file system, and adds the configured streams to this factory.
     * @param file the BeanIO configuration file to load
     * @param properties user {@link Properties} for property substitution
     * @throws BeanIOException if an {@link IOException} or other fatal error is caught while loading the file
     * @throws BeanIOConfigurationException if the mapping file is invalid
     */
    public void load(File file, Properties properties) throws BeanIOException, BeanIOConfigurationException {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            load(in, properties);
        }
        catch (IOException ex) {
            throw new BeanIOException("Failed to load '" + file + "' from the file system", ex);
        }
        finally {
            IOUtil.closeQuietly(in);
        }
    }

    /**
     * Loads a BeanIO mapping file, and adds the configured streams to this factory.
     * @param in the input stream to read the mapping file from
     * @throws BeanIOException if an {@link IOException} or other fatal error is caught while reading the input stream
     * @throws BeanIOConfigurationException if the mapping file is invalid
     */
    public void load(InputStream in) throws IOException, BeanIOConfigurationException {
        load(in, null);
    }

    /**
     * Loads a BeanIO mapping file, and adds the configured streams to this factory.
     * @param in the input stream to read the mapping file from
     * @param properties user {@link Properties} for property substitution
     * @throws BeanIOException if an {@link IOException} or other fatal error is caught while reading the input stream
     * @throws BeanIOConfigurationException if the mapping file is invalid
     */
    public abstract void load(InputStream in, Properties properties) throws IOException, BeanIOConfigurationException;

    /**
     * Returns a new <tt>StreamFactory</tt> instance.  The implementation class is resolved
     * using the the BeanIO configuration setting <tt>org.beanio.streamFactory</tt>.
     * @return a new <tt>StreamFactory</tt>
     * @throws BeanIOException if a <tt>StreamFactory</tt> could not be created
     * @see Settings
     */
    public static StreamFactory newInstance() throws BeanIOException {
        return newInstance(null);
    }
    
    /**
     * Returns a new <tt>StreamFactory</tt> instance.  An implementation class is loaded
     * using the the BeanIO configuration setting <tt>org.beanio.streamFactory</tt>.
     * @param classLoader the {@link ClassLoader} to use to load the stream factory and
     *   all subcomponents.  If null, the current thread's context class loader is used.
     *   If there is no context class loader for the thread, the class loader that loaded 
     *   this class is used.
     * @return a new <tt>StreamFactory</tt>
     * @throws BeanIOException if a <tt>StreamFactory</tt> could not be created
     * @see Settings
     * @since 2.0
     */
    public static StreamFactory newInstance(ClassLoader classLoader) throws BeanIOException {
        // find a default class loader
        if (classLoader == null) {
            try {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
            catch (Throwable t) { }
            
            if (classLoader == null) {
                classLoader = StreamFactory.class.getClassLoader();
            }
        }
        
        String className = Settings.getInstance(classLoader).getProperty(Settings.STREAM_FACTORY_CLASS);
        if (className == null) {
            throw new BeanIOException("Property '" + Settings.STREAM_FACTORY_CLASS + "' not set");
        }

        try {
            // use our own class loader for BeanIO classes
            StreamFactory factory;
            if (className.startsWith("org.beanio.")) {
                factory = (StreamFactory) StreamFactory.class.getClassLoader().
                    loadClass(className).newInstance();
            }
            else {
                factory = (StreamFactory) classLoader.loadClass(className).newInstance();
            }
            
            factory.setClassLoader(classLoader);
            factory.init();
            return factory;
        }
        catch (Exception ex) {
            throw new BeanIOException("Failed to load stream factory implementation class '" +
                className + "'", ex);
        }
    }
    
    /**
     * Test whether a mapping configuration exists for a named stream.
     * @param streamName the stream name to test for existence
     * @return <tt>true</tt> if a mapping configuration is found for the named stream
     * @since 1.2
     */
    public abstract boolean isMapped(String streamName);
    
    /**
     * This method is invoked after a StreamFactory is loaded and all attributes
     * have been set.
     * @since 2.0
     */
    protected void init() { }
    
    /**
     * Returns the class loader to use for resolving classpath resources and bean
     * objects declared in a mapping file.
     * @return the {@link ClassLoader} to use
     * @since 2.0
     */
    protected ClassLoader getClassLoader() {
        return classLoader;
    }
    
    /**
     * Sets the class loader to use for resolving classpath resources and bean objects
     * declared in a mapping files.
     * @param cl the {@link ClassLoader} to use
     * @since 2.0
     */
    protected void setClassLoader(ClassLoader cl) {
        this.classLoader = cl;
    }
}
