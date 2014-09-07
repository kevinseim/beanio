package org.beanio.parser.annotation;

import java.util.*;

import org.beanio.annotation.*;
import org.beanio.builder.XmlType;

public class AnnotatedLight {

    @Field(at=0, xmlType=XmlType.ATTRIBUTE)
    public int quantity;
    
    @Segment(at=1, collection=LinkedList.class, minOccurs=2, maxOccurs=2)
    public List<AnnotatedBulb> bulbs;
    
}
