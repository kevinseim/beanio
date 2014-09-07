package org.beanio.parser.annotation;

import org.beanio.annotation.*;

@Record
public class AnnotatedRoom {

    @Segment(at=0, type=AnnotatedLight.class, getter="getLightFixture", setter="setLightFixture")
    private Object light;
    
    @Field(at=5)
    public String name;
    
    private AnnotatedFloor floor;
    
    public AnnotatedLight getLightFixture() {
        return (AnnotatedLight) light;
    }

    public void setLightFixture(AnnotatedLight light) {
        this.light = light;
    }

    @Segment(at=6)
    public AnnotatedFloor getFlooring() {
        return floor;
    }

    public void setFlooring(AnnotatedFloor floor) {
        this.floor = floor;
    }
}
