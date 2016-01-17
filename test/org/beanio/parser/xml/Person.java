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
package org.beanio.parser.xml;

import java.util.List;

/**
 * Test bean object for XML parser related test cases.
 * @author Kevin Seim
 * @since 1.1
 */
public class Person {
    /* used to test that the setter is not called for missing elements */
    public static final String DEFAULT_NAME = new String();
    public static final Integer DEFAULT_AGE = Integer.valueOf(-1);
    
    private String type;
    private String gender;
    private String firstName;
    private String lastName = DEFAULT_NAME;
    private List<String> color;
    private Address address;
    private List<Address> addressList;
    private Integer age = DEFAULT_AGE;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public List<String> getColor() {
        return color;
    }
    public void setColor(List<String> color) {
        this.color = color;
    }
    
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    
    public List<Address> getAddressList() {
        return addressList;
    }
    public void setAddressList(List<Address> address) {
        this.addressList = address;
    }
    
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    
    @Override
    public String toString() {
        return gender + ": " + firstName + " " + lastName + ":" + color + " " + addressList;
    }
}
