package org.beanio.parser.annotation;

import org.beanio.annotation.Field;

public interface AnnotatedUserInterface {

    @Field(at=3, minOccurs=2, regex="(left|right)")
    public String[] getHands();
    
}
