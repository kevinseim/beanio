package org.beanio.parser.annotation;

import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;

@Fields({
    @Field(at=0, name="floor", literal="hardwood")
})
public class AnnotatedFloor {

    @Field(at=1)
    public int width;
    
    @Field(at=2)
    public int height;
    
}
