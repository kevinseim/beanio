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
package org.beanio.internal.parser.format.delimited;

import java.util.*;

import org.beanio.internal.parser.MarshallingContext;

/**
 * A {@link MarshallingContext} for delimited records.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class DelimitedMarshallingContext extends MarshallingContext {

    // the index of the last committed field in the record
    private int committed = 0;
    // the list used to build the final record
    private ArrayList<String> record = new ArrayList<>();
    // the list of entries for creating the record (may be unordered)
    private ArrayList<Entry> entries = new ArrayList<>();
    
    /**
     * Constructs a new <tt>DelimitedMarshallingContext</tt>.
     */
    public DelimitedMarshallingContext() { }
    
    @Override
    public void clear() {
        super.clear();
        
        entries.clear();
        committed = 0;
    }
    
    /**
     * Puts the field text in the record.
     * @param position the position of the field in the record.
     * @param fieldText the field text
     * @param commit true to commit the current record, or false
     *   if the field is optional and should not extend the record
     *   unless a subsequent field is later appended to the record 
     */
    public void setField(int position, String fieldText, boolean commit) {
        
        int index = getAdjustedFieldPosition(position);
        
        Entry entry = new Entry(index, fieldText);
        entries.add(entry);
        
        if (commit) {
            committed = entries.size();
        }
    }
    
    @Override
    public Object getRecordObject() {
        
        record.clear();
        
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
                record.set(index, entry.text);
            }
            else {
                while (index > size) {
                    record.add("");
                    ++size;
                }
                
                record.add(entry.text);
                ++size;
            }
        }
        
        return record.toArray(new String[0]);
    }
    
    @Override
    public String[] toArray(Object record) {
        return (String[])record;
    }
    
    @Override
    public List<String> toList(Object record) {
        return Arrays.asList((String[])record);
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
