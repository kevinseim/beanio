/*
 * Copyright 2011 Kevin Seim
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
package org.beanio.parser.bean;

import java.util.*;

public class Widget {

    private int id;
    private String name;
    private String model;
    
    private Widget top;
    private Widget bottom;
    private List<Widget> partsList;
    private Map<String,Widget> partsMap;
    
    @Override
    public String toString() {
        return 
            "[id=" + id +
            ", name=" + name +
            ", model=" + model +
            ", partsList=" + partsList +
            "]";
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public Widget getTop() {
        return top;
    }
    public void setTop(Widget top) {
        this.top = top;
    }
    public Widget getBottom() {
        return bottom;
    }
    public void setBottom(Widget bottom) {
        this.bottom = bottom;
    }
    public List<Widget> getPartsList() {
        return partsList;
    }
    public Widget getPart(int index) {
        return partsList.get(index);
    }
    public void addPart(Widget w) {
        if (partsList == null) {
            partsList = new ArrayList<>();
        }
        partsList.add(w);
    }
    public void setPartsList(List<Widget> partsList) {
        this.partsList = partsList;
    }
    public Map<String, Widget> getPartsMap() {
        return partsMap;
    }
    public void setPartsMap(Map<String, Widget> partsMap) {
        this.partsMap = partsMap;
    }
    public Widget getPart(String key) {
        return partsMap.get(key);
    }
}
