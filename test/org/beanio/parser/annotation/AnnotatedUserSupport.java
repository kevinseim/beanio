package org.beanio.parser.annotation;

import org.beanio.annotation.Field;

public abstract class AnnotatedUserSupport {

    @Field(at=1, required=true)
    private String firstName;
    
    @Field(at=2, getter="getSurname", setter="setSurname")
    private String lastName;
    
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getSurname() {
        return lastName;
    }
    public void setSurname(String lastName) {
        this.lastName = lastName;
    }
    
}
