/*
 * Copyright 2012 Kevin Seim
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
package org.beanio.stream.json;

/**
 * Stores configuration settings for parsing JSON formatted streams.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class JsonParserConfiguration {

    private boolean pretty = false;
    private int indentation = 2;
    private String lineSeparator;

    /**
     * Constructs a new <tt>JsonParserConfiguration</tt>
     */
    public JsonParserConfiguration() { }
    
    /**
     * Returns whether JSON output should be formatted prettily.
     * @return true to format JSON output, false otherwise
     */
    public boolean isPretty() {
        return pretty;
    }
    
    /**
     * Sets whether to prettily format JSON output.
     * @param pretty true to format JSON output, false otherwise
     */
    public void setPretty(boolean pretty) {
        this.pretty = pretty;
    }
    
    /**
     * Returns the number of spaces to indent when <tt>pretty</tt> is enabled.
     * Defaults to 2.
     * @return the number of spaces
     */
    public int getIndentation() {
        return indentation;
    }
    
    /**
     * Sets the number of spaces to indent when <tt>pretty</tt> is enabled.
     * @param indentation the number of spaces
     */
    public void setIndentation(int indentation) {
        this.indentation = indentation;
    }
    
    /**
     * Returns the line separator to use when <tt>pretty</tt> is enabled.
     * Defaults to the <tt>line.separator</tt> system property.
     * @return the line separator
     */
    public String getLineSeparator() {
        return lineSeparator;
    }
    
    /**
     * Sets the line separator to use when <tt>pretty</tt> is enabled.
     * @param lineSeparator the line separator
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }
}
