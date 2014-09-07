package org.beanio.parser.constructor

import org.beanio.annotation.Field
import org.beanio.annotation.Record;

@Record
class AnnotatedColor {

    String name
    int r
    int g
    int b
    
    AnnotatedColor() {
        this("black", 0, 0, 0)
    }

    AnnotatedColor(String name, 
        @Field(at=1, name="r") int r, 
        @Field(at=2, name="g") int g, 
        @Field(at=3, name="b") int b) 
    {
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    @Field(at=0, setter="#1")
    String getName() {
        return this.name;
    }

    int getR() {
        return r;
    }

    int getG() {
        return g;
    }

    int getB() {
        return b;
    }
}
