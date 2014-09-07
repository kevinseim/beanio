package org.beanio.parser.annotation;

import org.beanio.annotation.Field;

public class AnnotatedBulb {
    
    @Field(at=0)
    public int watts;
    
    @Field(at=1)
    public String style;
    
}
