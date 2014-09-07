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
package org.beanio.internal.util;

import java.io.*;
import java.net.URL;

/**
 * Utility class for manipulating streams.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class IOUtil {

    private IOUtil() { }
   
    /**
     * Closes an input stream and quietly ignores any exception.
     * @param in the stream to close
     */
    public static void closeQuietly(Reader in) {
        try {
            if (in != null)
                in.close();
        }
        catch (IOException ex) { }
    }

    /**
     * Closes an output stream and quietly ignores any exception.
     * @param out the stream to close
     */
    public static void closeQuietly(Writer out) {
        try {
            if (out != null)
                out.close();
        }
        catch (IOException ex) { }
    }

    /**
     * Closes an input stream and quietly ignores any exception.
     * @param in the stream to close
     */
    public static void closeQuietly(InputStream in) {
        try {
            if (in != null)
                in.close();
        }
        catch (IOException ex) { }
    }

    /**
     * Closes an output stream and quietly ignores any exception.
     * @param out the stream to close
     */
    public static void closeQuietly(OutputStream out) {
        try {
            if (out != null)
                out.close();
        }
        catch (IOException ex) { }
    }
    
    /**
     * Finds a resource on the classpath.  The resource is always loaded from
     * the root of the classpath, whether the resource name includes a
     * leading slash or not.
     * @param resource the name of the resource to load
     * @return the resource URL, or <tt>null</tt> if the resource was not found
     * @since 1.2.1
     */
    public static URL getResource(ClassLoader classLoader, String resource) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) { }
        
        if (cl == null) {
            cl = IOUtil.class.getClassLoader();
        }
        
        if (resource.startsWith("/")) {
            resource = resource.substring(1);
        }
        
        return cl.getResource(resource);
    }
    
    /**
     * Loads a resource from the classpath.
     * @param resource the name of the resource to load
     * @return a new {@link InputStream} to read the resource or <tt>null</tt> if the
     *   resource was not found
     * @throws IOException if an I/O error occurs
     * @since 1.2
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) { }
        
        if (cl == null) {
            cl = IOUtil.class.getClassLoader();
        }
        
        return cl.getResourceAsStream(resource);
    }
}
