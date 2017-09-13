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
package org.beanio.internal.parser.format.fixedlength;

import java.util.*;

import org.beanio.internal.parser.MarshallingContext;

/**
 * A {@link MarshallingContext} for a fixed length formatted stream.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class FixedLengthMarshallingContext extends MarshallingContext {

    // the filler character for missing fields
    private char filler = ' ';
    // the committed length of the record, aka the size of the record after
    // appending the last required field
    private int committed = 0;
    // the list of entries for creating the record (may be unordered)
    private ArrayList<Entry> entries = new ArrayList<>();
    
    /**
     * Constructs a new <tt>FixedLengthMarshallingContext</tt>.
     */
    public FixedLengthMarshallingContext() { }
    
    @Override
    public void clear() {
        super.clear();
        
        committed = 0;
        entries.clear();
    }
    
    /**
     * Inserts field text into the record being marshalled.
     * @param position the position of the field in the record
     * @param text the field text to insert
     * @param commit true to commit the current field length, or false
     *   if the field is optional and should not extend the record length
     *   unless a subsequent field is appended to the record 
     */
    public void setFieldText(int position, String text, boolean commit) {
        
        int index = getAdjustedFieldPosition(position);
        
        Entry entry = new Entry(index, text);
        entries.add(entry);
        
        if (commit) {
            committed = entries.size();
        }
    }
    
    @Override
    public Object getRecordObject() {
        
        StringBuilder record = new StringBuilder();
        
        List<Entry> committedEntries;
        if (committed < entries.size()) {
            committedEntries = entries.subList(0, committed);
        }
        else {
            committedEntries = entries;
        }
        
        Collections.sort(committedEntries);
        
        // the current index to write out
        int size = 0;
        // the offset for positions relative to the end of the record
        int offset = -1;
        
        for (Entry entry : committedEntries) {
            
            int index = entry.position;
            if (index < 0) {
                // the offset is calculated the first time we encounter
                // a position relative to the end of the record
                if (offset == -1) {
                    offset = size + Math.abs(index);
                    index = size;
                }
                else {
                    index += offset;
                }
            }
            
            if (index < size) {
                record.replace(index, index + entry.text.length(), entry.text);
                size = record.length();
            }
            else {
                while (index > size) {
                    record.append(filler);
                    ++size;
                }
                
                record.append(entry.text);
                size += entry.text.length();
            }
        }
        
        return record.toString();
    }
    
    private static class Entry implements Comparable<Entry> {
        int position;
        int order;
        String text;
        
        public Entry(int position, String text) {
            this.position = position;
            this.order = position < 0 ? position + Integer.MAX_VALUE : position;
            this.text = text;
        }
        
        @Override
        public int compareTo(Entry o) {
            return Integer.valueOf(this.order).compareTo(o.order);
        }
        
        @Override
        public String toString() {
            return order + ":" + text;
        }
    }
}
