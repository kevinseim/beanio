package org.beanio.beans

/**
 * A common bean object used by Groovy test cases.
 * @author Kevin Seim
 */
class Bean {

    // simple properties
	String type;
	String text;
    String field1;
    private String field2;
    String field3;
    
    // collection properties
	Map map;
	List list;
	
    // bean properties
	Bean group;
	Bean record;
	Bean segment;
    
}
