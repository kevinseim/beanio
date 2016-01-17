/*
 * Copyright 2011-2012 Kevin Seim
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
package org.beanio.beans;

import java.util.*;

public class Order {

    private String id;
    private Date date;
    private String paymentMethod;
    private Person customer;
    private Person shipper;
    private List<OrderItem> items;
    private Map<String,OrderItem> itemMap;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public Person getCustomer() {
        return customer;
    }
    public void setCustomer(Person buyer) {
        this.customer = buyer;
    }
    public Person getShipper() {
        return shipper;
    }
    public void setShipper(Person shipper) {
        this.shipper = shipper;
    }
    public List<OrderItem> getItems() {
        return items;
    }
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    public Map<String, OrderItem> getItemMap() {
        return itemMap;
    }
    public void setItemMap(Map<String, OrderItem> itemMap) {
        this.itemMap = itemMap;
    }
    @Override
    public String toString() {
        return id;
    }
}
