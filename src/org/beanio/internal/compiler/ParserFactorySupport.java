/*
 * Copyright 2010-2013 Kevin Seim
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
package org.beanio.internal.compiler;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.PatternSyntaxException;

import org.beanio.BeanIOConfigurationException;
import org.beanio.internal.compiler.accessor.*;
import org.beanio.internal.config.*;
import org.beanio.internal.parser.*;
import org.beanio.internal.parser.Field;
import org.beanio.internal.parser.accessor.*;
import org.beanio.internal.parser.message.ResourceBundleMessageFactory;
import org.beanio.internal.util.*;
import org.beanio.stream.RecordParserFactory;
import org.beanio.types.*;

/**
 * Base {@link ParserFactory} implementation.
 * 
 * <p>A {@link StreamConfig} is "compiled" into a {@link Stream} in two passes.  First, a
 * {@link Preprocessor} is used to validate and set default configuration settings. And
 * secondly, the finalized configuration is walked again (using a {@link ProcessorSupport},
 * to create the parser and property tree structure.  As components are initialized they can
 * be added to the tree structure using stacks with the {@link #pushParser(Component)} and 
 * {@link #pushProperty(Component)} methods.  After a component is finalized, it should be      
 * removed from the stack using the {@link #popParser()} or {@link #popProperty()} method.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public abstract class ParserFactorySupport extends ProcessorSupport implements ParserFactory {

    private static final String CONSTRUCTOR_PREFIX = "#";
    
    private static final boolean allowProtectedPropertyAccess = "true".equalsIgnoreCase(
        Settings.getInstance().getProperty(Settings.ALLOW_PROTECTED_PROPERTY_ACCESS));
    
    private static final Component unbound = new Component() {{ setName("unbound"); }};
    
    private Stream stream;
    private String streamFormat;
    private boolean readEnabled = true;
    private boolean writeEnabled = true;
    private TypeHandlerFactory typeHandlerFactory;
    private PropertyAccessorFactory accessorFactory;
    private ClassLoader classLoader;
    
    private LinkedList<Component> parserStack = new LinkedList<>();
    private LinkedList<Component> propertyStack = new LinkedList<>();
    
    /**
     * Constructs a new <tt>ParserFactory</tt>.
     */
    public ParserFactorySupport() { }
    
    /**
     * Creates a new stream definition based on a stream configuration.
     * @param config the stream configuration
     * @return the new {@link Stream}
     * @throws BeanIOConfigurationException if the configuration is invalid
     */
    @Override
    public Stream createStream(StreamConfig config) throws BeanIOConfigurationException {
        if (config.getName() == null) {
            throw new BeanIOConfigurationException("stream name not configured");
        }
        
        // pre-process configuration settings to set defaults and validate as much as possible 
        createPreprocessor(config).process(config);
        
        accessorFactory = new ReflectionAccessorFactory();
        
        try {
        	process(config);
        }
        catch (BeanIOConfigurationException ex) {
        	throw ex;
        }
        catch (RuntimeException ex) {
        	throw new BeanIOConfigurationException("Failed to compile stream '" + config.getName() + "'", ex);
        }
        
        // calculate the heap size
        stream.init();
        
        //((TreeNode)stream.getLayout()).print();
        
        return stream;
    }
    
    /**
     * Creates a stream configuration pre-processor.  May be overridden to return a
     * format specific version.
     * @param config the stream configuration to pre-process
     * @return the new {@link Preprocessor}
     */
    protected Preprocessor createPreprocessor(StreamConfig config) {
        return new Preprocessor(config);
    }
    
    /**
     * Creates a format specific {@link StreamFormat}. 
     * @param config the stream configuration
     * @return the new {@link StreamFormat}
     */
    protected abstract StreamFormat createStreamFormat(StreamConfig config);
    
    /**
     * Creates a format specific {@link RecordFormat}.
     * @param config the record configuration
     * @return the new {@link RecordFormat}
     */
    protected abstract RecordFormat createRecordFormat(RecordConfig config);
    
    /**
     * Creates a format specific {@link FieldFormat}.
     * @param config the field configuration
     * @param type the property type, or null if not bound to a property
     * @return the new {@link FieldFormat}
     */
    protected abstract FieldFormat createFieldFormat(FieldConfig config, Class<?> type);
    
    /**
     * Adds a component to the parser stack.
     * @param component the component to add
     */
    protected void pushParser(Component component) {
        if (!parserStack.isEmpty()) {
            parserStack.getLast().add(component);
        }
        parserStack.addLast(component);
        //System.out.println("Layout: " + parserStack);
    }
    
    /**
     * Removes the last component added to the parser stack.
     * @return the removed component
     */
    protected Component popParser() {
        return parserStack.removeLast();
    }
    
    /**
     * Adds a component to the property stack.
     * @param component the component to add
     */
    protected void pushProperty(Component component) {
        if (isBound() && component != unbound) {            
            // add properties to their parent bean or Map
            Component parent = propertyStack.getLast();
            switch (((Property)parent).type()) {
                case Property.SIMPLE:
                    throw new IllegalStateException();
                case Property.COLLECTION:
                case Property.COMPLEX:
                case Property.MAP:
                    parent.add(component);
            }

            // if the parent property is an array or collection, the parser already holds
            // a reference to the child component when pushParser was called
        }
        
        propertyStack.addLast(component);
        //System.out.println("Property: " + propertyStack);
    }

    /**
     * Removes the last component added to the property stack.
     * @return the removed component
     */
    protected Property popProperty() {
    	Component c = propertyStack.removeLast();
    	if (c == unbound) {
    		return null;
    	}
        Property last = (Property) c;
        
        if (!propertyStack.isEmpty()) {
            if (last.isIdentifier()) {
            	for (int i=propertyStack.size() - 1; i>=0; i--) {
            		if (propertyStack.get(i) == unbound) {
            			continue;
            		}
            		else {
            			((Property)propertyStack.getLast()).setIdentifier(true);
            			break;
            		}
            	}
            }
        }
        
        return last;
    }
    
    /**
     * Returns true if a property has been pushed onto the property stack, indicating
     * that further properties will be bound to a parent property.
     * @return true if a property is currently bound to a parent bean object
     */
    protected boolean isBound() {
        return !propertyStack.isEmpty() && propertyStack.getLast() != unbound;
    }
    
    
    /**
     * Updates a {@link Bean}'s constructor if one or more of its properties are 
     * constructor arguments.
     * @param bean the {@link Bean} to check
     */
    protected void updateConstructor(Bean bean) {
        ArrayList<Property> args = null;
        
        for (Component child : bean.getChildren()) {
            Property property = (Property) child;
            
            if (property.getAccessor().isConstructorArgument()) {
                if (args == null) {
                    args = new ArrayList<>();
                }
                args.add(property);
            }
        }
        
        // return if no constructor arguments were found
        if (args == null) {
            return;
        }
        
        // sort arguments by constructor index
        Collections.sort(args, new Comparator<Property>() {
            @Override
            public int compare(Property o1, Property o2) {
                return o1.getAccessor().getConstructorArgumentIndex() -
                    o2.getAccessor().getConstructorArgumentIndex();
            }
        });
        
        int count = args.size();
        
        // verify the number of constructor arguments matches the provided constructor index
        if (count != (args.get(count - 1).getAccessor().getConstructorArgumentIndex() + 1)) {
            throw new BeanIOConfigurationException("Missing constructor argument for bean class '" + 
                bean.getType().getName() + "'");
        }
        
        // find a suitable constructor
        Constructor<?> constructor = null;
        CONSTRUCTOR_LOOP: for (Constructor<?> c : bean.getType().getDeclaredConstructors()) {
            if (c.getParameterTypes().length != count) {
                continue;
            }
            
            int i = 0;
            for (Class<?> type : c.getParameterTypes()) {
                Property arg = args.get(i);

                Class<?> argType = arg.getType();
                if (argType.isPrimitive()) {
                    argType = TypeUtil.toWrapperClass(argType);
                }
                
                if (!TypeUtil.isAssignable(type, argType)) {
                    continue CONSTRUCTOR_LOOP;
                }
                ++i;
            }
            
            constructor = c;
            break CONSTRUCTOR_LOOP; 
        }
        
        if (constructor != null) {
            int mods = constructor.getModifiers();
            if (!Modifier.isPublic(mods)) {
                if (allowProtectedPropertyAccess) {
                    constructor.setAccessible(true);
                }
                else {
                    constructor = null;
                }
            }
        }
        
        // verify a constructor was found
        if (constructor == null) {
            throw new BeanIOConfigurationException("No suitable constructor found for bean class '" + 
                bean.getType().getName() + "'");
        }
        
        bean.setConstructor(constructor);
    }
    
    @Override
    protected void initializeStream(StreamConfig config) throws BeanIOConfigurationException {
        streamFormat = config.getFormat();
        
        StreamFormat format = createStreamFormat(config);
        
        stream = new Stream(format);
        
        // set the stream mode, defaults to read/write, the stream mode may be used
        // to enforce or relax validation rules specific to marshalling or unmarshalling
        String mode = config.getMode();
        if (mode == null || StreamConfig.READ_WRITE_MODE.equals(mode)) {
            stream.setMode(Stream.READ_WRITE_MODE);
        }
        else if (StreamConfig.READ_ONLY_MODE.equals(mode)) {
            stream.setMode(Stream.READ_ONLY_MODE);
            writeEnabled = false;
        }
        else if (StreamConfig.WRITE_ONLY_MODE.equals(mode)) {
            stream.setMode(Stream.WRITE_ONLY_MODE);
            readEnabled = false;
        }
        else {
            throw new BeanIOConfigurationException("Invalid mode '" + mode + "'");
        }
                
        ResourceBundleMessageFactory messageFactory = new ResourceBundleMessageFactory();
        
        // load the stream's default resource bundle
        String bundleName = Settings.getInstance().getProperty(
            "org.beanio." + config.getFormat() + ".messages");
        if (bundleName != null) {
            try {
                messageFactory.setDefaultResourceBundle(ResourceBundle.getBundle(bundleName));
            }
            catch (MissingResourceException ex) {
                throw new BeanIOConfigurationException("Missing default resource bundle '" +
                    bundleName + "' for stream format '" + config.getFormat() + "'", ex);
            }
        }

        // load the stream resource bundle
        bundleName = config.getResourceBundle();
        if (bundleName != null) {
            try {
                messageFactory.setResourceBundle(ResourceBundle.getBundle(bundleName));
            }
            catch (MissingResourceException ex) {
                throw new BeanIOConfigurationException("Missing resource bundle '" +
                    bundleName + "'", ex);
            }
        }

        stream.setMessageFactory(messageFactory);
        stream.setIgnoreUnidentifiedRecords(config.isIgnoreUnidentifiedRecords());
        initializeGroup(config);
    }
    
    @Override
    protected void finalizeStream(StreamConfig config) throws BeanIOConfigurationException {
        stream.setLayout((Selector)parserStack.getFirst());
        finalizeGroup(config);
    }
    
    @Override
    protected void initializeGroup(GroupConfig config) throws BeanIOConfigurationException {
        if (config.getChildren().isEmpty()) {
            throw new BeanIOConfigurationException("At least one record or group is required.");
        }
        
        // determine and validate the bean class
        Property bean = createProperty(config);
        
        // handle bound repeating groups
        if (config.isBound() && config.isRepeating()) {
        	initializeGroupIteration(config, bean);
        }
        
        initializeGroupMain(config, bean);
    }
    
    protected void initializeGroupIteration(GroupConfig config, Property property) {
        // wrap the segment in an iteration
        Component aggregation = createRecordAggregation(config, property);

        pushParser(aggregation);
        if (property != null || config.getTarget() != null) {
            pushProperty(aggregation);
        }
    }
    
    protected void initializeGroupMain(GroupConfig config, Property property) {
        Group group = new Group();
        group.setName(config.getName());
        group.setMinOccurs(config.getMinOccurs());
        group.setMaxOccurs(config.getMaxOccurs());
        group.setOrder(config.getOrder());
        group.setProperty(property);
        
        pushParser(group);
        if (property != null) {
            pushProperty((Component)property);
        }
    }

    @Override
    protected void finalizeGroup(GroupConfig config) throws BeanIOConfigurationException {
        Property property = finalizeGroupMain(config);
        
        if (config.isBound() && config.isRepeating()) {
            finalizeGroupIteration(config, property);
        }
    }
    
    protected Property finalizeGroupMain(GroupConfig config) {
    	Property property = null;
    	
        // pop the group bean from the property stack
        if (config.getType() != null) {
        	property = popProperty();
            reflectPropertyType(config, property);
        }
        // pop the record from the parser stack
        finalizeGroup(config, (Group)popParser());
        
        return property;
    }
    
    protected void finalizeGroupIteration(GroupConfig config, Property property) {
        // pop the iteration from the parser stack
    	RecordAggregation aggregation = (RecordAggregation) popParser();
    	
        // pop the collection from the property stack
        if (config.getType() != null) {
        	popProperty();
        	reflectRecordAggregationType(config, aggregation, property);
        }
    }
    

    /**
     * Invoked by {@link #finalizeGroupMain(GroupConfig)} to allow subclasses to perform
     * further finalization of the created {@link Group}.
     * @param config the group configuration
     * @param group the {@link Group} being finalized
     */
    protected void finalizeGroup(GroupConfig config, Group group) { 
        String target = config.getTarget();
        if (target != null) {
            group.setProperty(findTarget(group, target));
        }
    }
    
    @Override
    protected void initializeRecord(RecordConfig config) throws BeanIOConfigurationException {        
        // determine and validate the bean class
        Property bean = createProperty(config);

        // handle bound repeating records
        if (config.isBound() && config.isRepeating()) {
        	initializeRecordIteration(config, bean);
        }
        
        initializeRecordMain(config, bean);
    }
    
    protected void initializeRecordIteration(RecordConfig config, Property property) {
        // wrap the segment in an iteration
        Component collection = createRecordAggregation(config, property);

        pushParser(collection);
        if (property != null || config.getTarget() != null) {
            pushProperty(collection);
        }
    }
    
    protected void initializeRecordMain(RecordConfig config, Property property) {
        Record record = new Record();
        record.setName(config.getName());
        record.setMinOccurs(config.getMinOccurs());
        record.setMaxOccurs(config.getMaxOccurs());    
        record.setOptional(config.getMinOccurs() < config.getMaxOccurs());
        record.setRepeating(config.isRepeating());
        record.setSize(config.getMaxSize());
        record.setFormat(createRecordFormat(config));
        record.setOrder(config.getOrder());
        record.setIdentifier(config.isIdentifier());
        record.setProperty(property);
        
        if (property != null) {
            pushProperty((Component)property);
        }
        else if (config.getTarget() != null) {
        	pushProperty(unbound);
        }
        
        pushParser(record);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.factory.ConfigurationProcessor#finalizeRecord(org.beanio.config.RecordConfig)
     */
    @Override
    protected void finalizeRecord(RecordConfig config) throws BeanIOConfigurationException {
        Property property = finalizeRecordMain(config);
        
        if (config.isBound() && config.isRepeating()) {
            finalizeRecordIteration(config, property);
        }
    }
    
    protected Property finalizeRecordMain(RecordConfig config) {
    	Property property = null;
    	Record record = (Record) popParser();
    	
        // pop the record bean from the property stack
        if (config.getType() != null || config.getTarget() != null) {
            property = popProperty(); // could be null if 'target' was set
            
            String target = config.getTarget();
            if (target != null) {
            	property = findTarget(record, target);
            	pushProperty((Component)property);
            	popProperty();
            	record.setProperty(property);
            }
            
            if (property != null) {
            	reflectPropertyType(config, property);
            }
        }
        
        // pop the record from the parser stack
        finalizeRecord(config, record);
        
        return property;
    }
    
    protected void finalizeRecordIteration(RecordConfig config, Property property) {
        // pop the iteration from the parser stack
        RecordAggregation aggregation = (RecordAggregation) popParser();
        
        // pop the collection from the property stack
        if (config.getType() != null || config.getTarget() != null) {
            popProperty();
            
            reflectRecordAggregationType(config, aggregation, property);
        }
        
        // assumes key is not null only for map aggregation
        String key = config.getKey();
        if (key != null) {
            // aggregations only have a single descendant so calling getFirst() is safe
            Component c = findDescendant("key", aggregation.getFirst(), key);
            if (c == null) {
                throw new BeanIOConfigurationException("Key '" + key + "' not found");
            }
            
            Property keyProperty = null;
            if (c instanceof Property) {
            	keyProperty = (Property) c;
            }
            if (keyProperty == null || keyProperty.getType() == null) {
                throw new BeanIOConfigurationException("Key '" + key + "' is not a property");
            }
            ((RecordMap)aggregation).setKey(keyProperty);
        }
    }
    
    /**
     * Invoked by {@link #finalizeRecord(RecordConfig)} to allow subclasses to perform
     * further finalization of the created {@link Record}.
     * @param config the record configuration
     * @param record the {@link Record} being finalized
     */
    protected void finalizeRecord(RecordConfig config, Record record) { 

    }
    
    private Property findTarget(Component segment, String name) {
        Component c = findDescendant("value", segment, name);
        if (c == null) {
            throw new BeanIOConfigurationException("Descendant value '" + name + "' not found");
        }
        
        Property property = null;
        if (c instanceof Property) {
            property = (Property) c;
        }
        if (property == null || property.getType() == null) {
            throw new BeanIOConfigurationException("No class defined for value '" + name + "'");
        }
        return property;
    }
    
    private Component findDescendant(String type, Component c, String name) {
        if (name.equals(c.getName())) {
            return c;
        }
        for (Component child : c.getChildren()) {
            
            Component match = findDescendant(type, child, name);
            if (match != null) {
                if (c instanceof Iteration) {
                    throw new BeanIOConfigurationException("Referenced component '" + name + 
                        "' may not repeat, or belong to a segment that repeats");
                }
                
                return match;
            }
        }
        return null;
    }
    

    /*
     * (non-Javadoc)
     * @see org.beanio.factory.ConfigurationProcessor#initializeSegment(org.beanio.config.SegmentConfig)
     */
    @Override
    protected final void initializeSegment(SegmentConfig config) throws BeanIOConfigurationException {
        Property bean = createProperty(config);
        
        if (config.isRepeating()) {
            initializeSegmentIteration(config, bean);
        }
        
        initializeSegmentMain(config, bean);
    }
    
    /**
     * Called by {@link #initializeSegment(SegmentConfig)} to initialize segment iteration.
     * @param config the segment configuration
     * @param property the {@link Property} bound to the segment, or null if no bean was bound
     */
    protected void initializeSegmentIteration(SegmentConfig config, Property property) {
        // wrap the segment in an aggregation component
        Aggregation aggregation = createAggregation(config, property);
        
        if (config.getOccursRef() != null) {
            Field occurs = findDynamicOccurs(parserStack.getLast(), config.getOccursRef());
            aggregation.setOccurs(occurs);
        }
        
        pushParser(aggregation);
        if (property != null || config.getTarget() != null) {
            pushProperty(aggregation);
        }
    }
    
    /**
     * Called by {@link #initializeSegment(SegmentConfig)} to initialize the segment.
     * @param config the segment configuration
     * @param property the property bound to the segment, or null if no property was bound
     */
    protected void initializeSegmentMain(SegmentConfig config, Property property) {
        String name = config.getName();
        if (name == null) {
            throw new BeanIOConfigurationException("Segment name not set");
        }
        
        Segment segment = new Segment();
        segment.setName(config.getName());
        segment.setSize(config.getMaxSize());
        segment.setIdentifier(config.isIdentifier());
        segment.setOptional(config.getMinOccurs() < config.getMaxOccurs());
        segment.setRepeating(config.isRepeating());
        segment.setProperty(property);
        segment.setExistencePredetermined(config.getDefaultExistence());
        
        if (isSegmentRequired(config)) {
            pushParser(segment);
        }
        
        if (property != null) {
            pushProperty((Component)property);
        }
        else if (config.getTarget() != null) {
        	pushProperty(unbound);
        }
    }

    protected boolean isSegmentRequired(SegmentConfig config) {
        return (config.getType() != null || config.getTarget() != null);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.factory.ConfigurationProcessor#finalizeSegment(org.beanio.config.SegmentConfig)
     */
    @Override
    protected final void finalizeSegment(SegmentConfig config) throws BeanIOConfigurationException {
        Property property = finalizeSegmentMain(config);
        if (config.isRepeating()) {
            finalizeSegmentIteration(config, property);
        }
    }
    
    /**
     * Called by {@link #finalizeSegment(SegmentConfig)} to finalize segment iteration.
     * @param config the segment configuration
     */
    protected void finalizeSegmentIteration(SegmentConfig config, Property property) {
        Aggregation aggregation = (Aggregation) popParser();
        if (config.getType() != null || config.getTarget() != null) {
        	popProperty(); // pop the iteration
            reflectAggregationType(config, aggregation, property);
        }
        
        // assumes key is not null only for map aggregation
        String key = config.getKey();
        if (key != null) {
            // aggregations only have a single descendant so calling getFirst() is safe
            Component c = findDescendant("key", aggregation.getFirst(), key);
            if (c == null) {
                throw new BeanIOConfigurationException("Key '" + key + "' not found");
            }
            
            Property keyProperty = null;
            if (c instanceof Property) {
                keyProperty = (Property) c;
            }
            if (keyProperty == null || keyProperty.getType() == null) {
                throw new BeanIOConfigurationException("Key '" + key + "' is not a property");
            }
            ((MapParser)aggregation).setKey(keyProperty);
        }
    }
    
    /**
     * Called by {@link #finalizeSegment(SegmentConfig)} to finalize the segment component.
     * @param config the segment configuration
     */
    protected Property finalizeSegmentMain(SegmentConfig config) {
    	Property property = null;
    	Segment segment = null;
    	
        if (isSegmentRequired(config)) {
        	segment = (Segment) popParser();
            finalizeSegment(config, segment);
        }
        
        if (config.getType() != null || config.getTarget() != null) {
            property = popProperty(); // could be null if 'target' was set
            
            String target = config.getTarget();
            if (target != null) {
            	property = findTarget(segment, target);
            	pushProperty((Component)property);
            	popProperty();
                segment.setProperty(property);
            }
            
            if (property != null) {
            	reflectPropertyType(config, property);
            }
        }
        
        return property;
    }
    
    /**
     * Invoked by {@link #finalizeSegmentMain(SegmentConfig)} to allow subclasses to perform
     * further finalization of the created {@link Segment}.
     * @param config the segment configuration
     * @param segment the new {@link Segment}
     */
    protected void finalizeSegment(SegmentConfig config, Segment segment) {

    }

    /*
     * (non-Javadoc)
     * @see org.beanio.factory.ConfigurationProcessor#handleField(org.beanio.config.FieldConfig)
     */
    @Override
    protected void handleField(FieldConfig config) throws BeanIOConfigurationException {
        if (config.getName() == null) {
            throw new BeanIOConfigurationException("Missing field name");
        }
        
        Field field = new Field();
        field.setName(config.getLabel());
        field.setIdentifier(config.isIdentifier());
        field.setRequired(config.isRequired());
        field.setTrim(config.isTrim());
        field.setLazy(config.isLazy());
        field.setLiteral(config.getLiteral());
        field.setMinLength(config.getMinLength());
        field.setMaxLength(config.getMaxLength());
        field.setBound(config.isBound());

        try {
            field.setRegex(config.getRegex());
        }
        catch (PatternSyntaxException ex) {
            throw new BeanIOConfigurationException("Invalid regex pattern", ex);
        }

        // set the property type if explicitly configured
        if (config.getType() != null) {
            Class<?> propertyType = TypeUtil.toType(classLoader, config.getType());
            if (propertyType == null) {
                throw new BeanIOConfigurationException("Invalid type or type alias '" + config.getType() + "'");
            }
            field.setPropertyType(propertyType);
        }
        
        // whether or not this property is bound to a bean property, Collections targets are not
        boolean bind = isBound() && (config.isBound() && !config.isRepeating());
        
        Aggregation aggregation = null;
        if (config.isRepeating()) {
            aggregation = createAggregation(config, field);
            
            if (config.getOccursRef() != null) {
                Field occurs = findDynamicOccurs(parserStack.getLast(), config.getOccursRef());
                aggregation.setOccurs(occurs);
            }
            
            pushParser(aggregation);
            if (aggregation.isProperty()) {
                pushProperty(aggregation);
            }
        }
        else {
            if (bind) {
                reflectPropertyType(config, field);
            }
        }

        // if not already determined, this will update the field type
        field.setHandler(findTypeHandler(config, field));           
        
        // set the default field value using the configured type handler
        field.setDefaultValue(parseDefaultValue(field, config.getDefault()));

        field.setFormat(createFieldFormat(config, field.getType()));
        
        pushParser(field);
        if (bind) {
            pushProperty(field);
            popProperty();
        }
        popParser();
        
        if (aggregation != null) {
            popParser();
            if (aggregation.isProperty()) {
                popProperty();
            }
            
            reflectAggregationType(config, aggregation, field);
        }
    }

    private Field findDynamicOccurs(Component segment, String name) {
        Component c = findDescendant("value", segment, name);
        if (c == null || !(c instanceof Field)) {
            throw new BeanIOConfigurationException("Referenced field '" + name + "' not found");
        }
        Field f = (Field) c;
        if (!Number.class.isAssignableFrom(f.getType())) {
            throw new BeanIOConfigurationException("Referenced field '" + name + "' must be assignable to java.lang.Number");
        }
        return f;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.factory.ConfigurationProcessor#handleConstant(org.beanio.config.ConstantConfig)
     */
    @Override
    protected void handleConstant(ConstantConfig config) throws BeanIOConfigurationException {
        Constant constant = new Constant();
        constant.setName(config.getName());
        constant.setIdentifier(config.isIdentifier());
        
        // determine the property type
        Class<?> propertyType = null;
        if (config.getType() != null) {
            propertyType = TypeUtil.toType(classLoader, config.getType());
            if (propertyType == null) {
                throw new BeanIOConfigurationException("Invalid type or type alias '" + config.getType() + "'");
            }
        }
        
        reflectPropertyType(config, constant);
        
        TypeHandler handler = findTypeHandler(config, constant);
        
        // set the property value using the configured type handler
        String text = config.getValue();
        if (text != null) {
            try {
                constant.setValue(handler.parse(text));
            }
            catch (TypeConversionException ex) {
                throw new BeanIOConfigurationException("Type conversion failed for " +
                    "configured value '" + text + "': " + ex.getMessage(), ex);
            }
        }
        
        pushProperty(constant);
        popProperty();
    }

    /**
     * Creates an iteration for a repeating segment or field.
     * @param config the property configuration
     * @param property the property component, may be null if the iteration is not
     *   a property of its parent bean
     * @return the iteration component
     * @throws BeanIOConfigurationException if the collection type is invalid
     */
    protected Aggregation createAggregation(PropertyConfig config, Property property) 
        throws BeanIOConfigurationException
    {
        boolean isMap = false;
        
        String collection = config.getCollection();
        
        // determine the collection type
        Class<?> collectionType = null;
        if (collection != null) {
            collectionType = TypeUtil.toAggregationType(collection);
            if (collectionType == null) {
                throw new BeanIOConfigurationException("Invalid collection type or " +
                    "type alias '" + collection + "'");
            }
            
            isMap = Map.class.isAssignableFrom(collectionType);
            if (isMap && config.getComponentType() == ComponentConfig.FIELD) {
                throw new BeanIOConfigurationException("Map type collections are " +
                    "not supported for fields");
            }
            if (isMap && ((SegmentConfig)config).getKey() == null) {
                throw new BeanIOConfigurationException("Key required for Map type " +
                    "collection");
            }
            
            collectionType = getConcreteAggregationType(collectionType);
        }
        
        // create the appropriate iteration type
        Aggregation aggregation = null;
        if (collectionType == TypeUtil.ARRAY_TYPE) {
            aggregation = new ArrayParser();
        }
        else if (isMap) {
            aggregation = new MapParser();
        }
        else {
            aggregation = new CollectionParser();
        }
        aggregation.setName(config.getLabel());
        if (config.getOccursRef() != null) {
            aggregation.setMinOccurs(config.getMinOccursRef());
            aggregation.setMaxOccurs(config.getMaxOccursRef());
        }
        else {
            aggregation.setMinOccurs(config.getMinOccurs());
            aggregation.setMaxOccurs(config.getMaxOccurs());
        }
        aggregation.setLazy(config.isLazy());
        aggregation.setType(collectionType);
        return aggregation;
    }
    
    /**
     * 
     * @param config
     * @param aggregation
     * @param property
     * @throws BeanIOConfigurationException
     */
    protected void reflectAggregationType(
		PropertyConfig config, 
		Aggregation aggregation,
		Property property) throws BeanIOConfigurationException {
    	
    	Class<?> collectionType = aggregation.getType();

        // if collection was set, then this is a property of its parent
        if (collectionType != null) {
            Class<?> reflectedType = reflectCollectionType(aggregation, property, 
                config.getGetter(), config.getSetter());
            
            // descriptor may be null if the parent was Map or Collection
            if (collectionType == TypeUtil.ARRAY_TYPE) {
                Class<? extends Object> arrayType = property.getType();
                
                // reflectedType may be null if our parent is a Map
                if (reflectedType != null) {
                    // use the reflected component type for an array
                    arrayType = reflectedType.getComponentType();
                    
                    // override target type if we were able to reflect its value
                    property.setType(arrayType);
                }
                else if (arrayType == null) {
                    // default to String
                    arrayType = String.class;
                }
                
                ((ArrayParser)aggregation).setArrayType(arrayType);
            }
        }
    }

    /**
     * Creates an aggregation for a repeating record or group.
     * @param config the record or group configuration
     * @param property the property component
     * @return the created {@link RecordAggregation}
     * @throws BeanIOConfigurationException
     */
    protected RecordAggregation createRecordAggregation(PropertyConfig config, Property property) 
        throws BeanIOConfigurationException
    {
        boolean isMap = false;
        String collection = config.getCollection();
        
        // determine the collection type
        Class<?> collectionType = null;
        if (collection != null) {
            collectionType = TypeUtil.toAggregationType(collection);
            if (collectionType == null) {
                throw new BeanIOConfigurationException("Invalid collection type or " +
                    "type alias '" + collection + "'");
            }
            
            isMap = Map.class.isAssignableFrom(collectionType);
            if (isMap && config.getKey() == null) {
                throw new BeanIOConfigurationException("Key required for Map type " +
                    "collection");
            }
            
            collectionType = getConcreteAggregationType(collectionType);
        }
        
        // create the appropriate iteration type
        RecordAggregation aggregation;;
        if (collectionType == TypeUtil.ARRAY_TYPE) {
            aggregation = new RecordArray();
        }
        else if (isMap) {
            aggregation = new RecordMap();
        }
        else {
            aggregation = new RecordCollection();
        }
        aggregation.setName(config.getName());
        aggregation.setType(collectionType);
        aggregation.setLazy(config.isLazy());
        return aggregation;
    }
   
    protected void reflectRecordAggregationType(
		PropertyConfig config, 
		RecordAggregation aggregation,
		Property property) throws BeanIOConfigurationException {
    	
    	Class<?> collectionType = aggregation.getType();
    	
        // if collection was set, then this is a property of its parent
        if (collectionType != null) {
            Class<?> reflectedType = reflectCollectionType(aggregation, property, 
                config.getGetter(), config.getSetter());
            
            // descriptor may be null if the parent was Map or Collection
            if (collectionType == TypeUtil.ARRAY_TYPE) {
                Class<? extends Object> arrayType = property.getType();
                
                // reflected type may be null if the parent bean is a Map
                if (reflectedType != null) {    
                    // use the reflected component type for an array
                    arrayType = reflectedType.getComponentType();   
                    
                    // override target type if we were able to reflect its value
                    property.setType(arrayType);
                }
                else if (arrayType == null) {
                    // default to String
                    arrayType = String.class;
                }
                
                ((RecordArray)aggregation).setArrayType(arrayType);
            }
        }
    }
    
    /**
     * 
     * @param iteration
     * @param property
     * @param getter
     * @param setter
     * @return the reflected property type
     * @throws BeanIOConfigurationException
     */
    protected Class<?> reflectCollectionType(Property iteration, Property property, 
        String getter, String setter) throws BeanIOConfigurationException {
        if (!isBound()) {
            return null;
        }
        
        Property parent = (Property) propertyStack.getLast();
        switch (parent.type()) {
            case Property.SIMPLE:
                throw new BeanIOConfigurationException("Cannot add property to attribute");
            case Property.COLLECTION:
            case Property.AGGREGATION_ARRAY:
            case Property.AGGREGATION_COLLECTION:
            case Property.AGGREGATION_MAP:
                return null;
            case Property.MAP:
                iteration.setAccessor(new MapAccessor(iteration.getName()));
                return null;
        }
        
        // parse the constructor argument index from the 'setter'
        int construtorArgumentIndex = -1;
        if (setter != null && setter.startsWith(CONSTRUCTOR_PREFIX)) {
            try {
                construtorArgumentIndex = Integer.parseInt(setter.substring(1));
                if (construtorArgumentIndex <= 0) {
                    throw new BeanIOConfigurationException("Invalid setter method");
                }
                construtorArgumentIndex--;
            }
            catch (NumberFormatException ex) { 
                throw new BeanIOConfigurationException("Invalid setter method");
            }
            setter = null;
        }
        
        Class<?> reflectedType;
        try {
            // set the property descriptor on the field
            PropertyDescriptor descriptor = getPropertyDescriptor(iteration.getName(),
                getter, setter, construtorArgumentIndex >= 0);
            reflectedType = descriptor.getPropertyType();
            
            iteration.setAccessor(accessorFactory.getPropertyAccessor( 
                parent.getType(), descriptor, construtorArgumentIndex));
        }
        catch (BeanIOConfigurationException ex) {
            // if a method accessor is not found, attempt to find a field declaration
            java.lang.reflect.Field field = getField(iteration.getName());
            if (field == null) {
                // give up and rethrow the exception
                throw ex;    
            }
            reflectedType = field.getType();
            
            iteration.setAccessor(accessorFactory.getPropertyAccessor( 
                parent.getType(), field, construtorArgumentIndex));
        }

        // reflectedType may be null for read-only streams using a constructor argument
        if (reflectedType == null) {
            return null;
        }
            
        Class<?> type = property.getType();
        if (iteration.getType() == TypeUtil.ARRAY_TYPE) {
            if (!reflectedType.isArray()) {
                throw new BeanIOConfigurationException("Collection type 'array' does not " +
                    "match bean property type '" + reflectedType.getName() + "'");
            }
            
            Class<?> arrayType = reflectedType.getComponentType();
            if (type == null) {
                property.setType(arrayType);
            }
            else if (!TypeUtil.isAssignable(arrayType, type)) {
                throw new BeanIOConfigurationException("Configured field array of type '" + 
                    type + "' is not assignable to bean property " +
                    "array of type '" + arrayType.getName() + "'");
            }
        }
        else {
            if (!reflectedType.isAssignableFrom(iteration.getType())) {
                String beanPropertyTypeName;
                if (reflectedType.isArray()) {
                    beanPropertyTypeName = reflectedType.getComponentType().getName() + "[]";
                }
                else {
                    beanPropertyTypeName = reflectedType.getName();                
                }
                
                throw new BeanIOConfigurationException("Configured collection type '" +
                    iteration.getType().getName() + "' is not assignable to bean property " +
                    "type '" + beanPropertyTypeName + "'");
            }
        }
            
        return reflectedType;
    }
    
    /**
     * Sets the property type and accessor using bean introspection.
     * @param config the property configuration
     * @param property the property
     * @throws BeanIOConfigurationException if the configured type is not assignable
     *   to the type determined through introspection
     */
    protected void reflectPropertyType(PropertyConfig config, Property property) throws BeanIOConfigurationException {

    	// check for constructor arguments
        if (property.type() == Property.COMPLEX) {
            updateConstructor((Bean)property);
        }
        
        if (!isBound()) {
            return;
        }
        
        Property parent = (Property) propertyStack.getLast();
        switch (parent.type()) {
            case Property.SIMPLE:
                throw new BeanIOConfigurationException("Cannot add a property to a simple property");
            case Property.COLLECTION:
            case Property.AGGREGATION_ARRAY:
            case Property.AGGREGATION_COLLECTION:
            case Property.AGGREGATION_MAP:
                return;
            case Property.MAP:
                property.setAccessor(new MapAccessor(config.getName()));
                return;
        }
        
        String setter = config.getSetter();
        String getter = config.getGetter();
        
        // parse the constructor argument index from the 'sett  er'
        
        int construtorArgumentIndex = -1;
        if (setter != null && setter.startsWith(CONSTRUCTOR_PREFIX)) {
            try {
                construtorArgumentIndex = Integer.parseInt(setter.substring(1));
                if (construtorArgumentIndex <= 0) {
                    throw new BeanIOConfigurationException("Invalid setter method");
                }
                construtorArgumentIndex--;
            }
            catch (NumberFormatException ex) { 
                throw new BeanIOConfigurationException("Invalid setter method");
            }
            setter = null;
        }
        
        Class<?> reflectedType;
        try {
            // set the property descriptor on the field
            PropertyDescriptor descriptor = getPropertyDescriptor(config.getName(), 
                getter, setter, construtorArgumentIndex >= 0);
            reflectedType = descriptor.getPropertyType();
            
            property.setAccessor(accessorFactory.getPropertyAccessor( 
                parent.getType(), descriptor, construtorArgumentIndex));
        }
        catch (BeanIOConfigurationException ex) {
            // if a method accessor is not found, attempt to find a field
            java.lang.reflect.Field field = getField(config.getName());
            if (field == null) {
                // give up and rethrow the exception
                throw ex;    
            }
            reflectedType = field.getType();
            
            property.setAccessor(accessorFactory.getPropertyAccessor( 
                parent.getType(), field, construtorArgumentIndex));
        }
        
        // validate the reflected type
        Class<?> type = property.getType();
        if (type == null) {
            property.setType(reflectedType);
        }
        // reflectedType may be null if for read-only streams using a constructor argument
        else if (reflectedType != null && !TypeUtil.isAssignable(reflectedType, type)) {
            throw new BeanIOConfigurationException("Property type '" + 
                config.getType() + "' is not assignable to bean property " +
                "type '" + reflectedType.getName() + "'");
        }
        else if (reflectedType.isPrimitive()) {
            property.setType(reflectedType);
        }
    }
        
    /**
     * Returns the {@link PropertyDescriptor} for getting and setting a property value from
     * current bean class on the property stack.
     * @param property the property name
     * @param getter the getter method name, or null to use the default
     * @param setter the setter method name, or null to use the default
     * @param isConstructorArgument
     * @return the <tt>PropertyDescriptor</tt>
     * @throws BeanIOConfigurationException if the property is not found on the bean class, or
     *   no read or write method is discovered
     */
    private PropertyDescriptor getPropertyDescriptor(String property, String getter, String setter, 
        boolean isConstructorArgument) throws BeanIOConfigurationException {
        
        Class<?> beanClass = ((Property)propertyStack.getLast()).getType();
        
        // calling new PropertyDescriptor(...) will throw an exception if either the getter
        // or setter method is not null and not found
        
        PropertyDescriptor descriptor = null;
        try {
            if (setter != null && getter != null) {
                descriptor = new PropertyDescriptor(property, beanClass, getter, setter);
            }
            else {
                // the Introspector class caches BeanInfo so subsequent calls shouldn't be a concern
                BeanInfo info = Introspector.getBeanInfo(beanClass);
                for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                    if (pd.getName().equals(property)) {
                        descriptor = pd;
                        break;
                    }
                }
                
                if (descriptor == null) {
                    if (setter == null && getter == null && !isConstructorArgument) {
                        throw new BeanIOConfigurationException("No such property '" + property +
                            "' in class '" + beanClass.getName() + "'");
                    }
                    else {
                        descriptor = new PropertyDescriptor(property, beanClass, getter, setter);
                    }
                }
                else if (setter != null) {
                    if (descriptor.getReadMethod() != null) {
                        getter = descriptor.getReadMethod().getName();
                    }
                    descriptor = new PropertyDescriptor(property, beanClass, getter, setter);
                }
                else if (getter != null) {
                    if (descriptor.getWriteMethod() != null) {
                        setter = descriptor.getWriteMethod().getName();
                    }
                    descriptor = new PropertyDescriptor(property, beanClass, getter, setter);
                }
            }
            
            // validate a read method is found for mapping configurations that write streams
            if (!isConstructorArgument && isReadEnabled() && descriptor.getWriteMethod() == null) {
                throw new BeanIOConfigurationException("No writeable method for property '" + property + 
                    "' in class '" + beanClass.getName() + "'");
            }
            // validate a write method is found for mapping configurations that read streams
            if (isWriteEnabled() && descriptor.getReadMethod() == null) {
                throw new BeanIOConfigurationException("No readable method for property '" + property + 
                    "' in class '" + beanClass.getName() + "'");
            }
            
            return descriptor;
        }
        catch (IntrospectionException e) {
            throw new BeanIOConfigurationException("Bean introspection failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns the non-final {@link java.lang.reflect.Field} for a given property
     * name from the current bean class on the property stack.
     * @param property the propety name
     * @return the property {@link java.lang.reflect.Field} or null if not found
     */
    protected java.lang.reflect.Field getField(String property) {
        Class<?> beanClass = ((Property)propertyStack.getLast()).getType();
        
        java.lang.reflect.Field field = null;
        
        Class<?> c = beanClass;
        while (field == null && c != null) {
            for (Class<?> i : c.getInterfaces()) {
                for (java.lang.reflect.Field f : i.getDeclaredFields()) {
                    if (f.getName().equals(property)) {
                        field = f;
                        break;
                    }
                }
                if (field != null) {
                    break;
                }
            }
            
            if (c.isInterface()) {
                break;
            }
        
            for (java.lang.reflect.Field f : c.getDeclaredFields()) {
                if (f.getName().equals(property)) {
                    field = f;
                    break;
                }
            }
            
            c = c.getSuperclass();
        }
        
        if (field == null) {
            return null;
        }
        
        // verify the field is public and not final
        int mod = field.getModifiers();
        if (Modifier.isFinal(mod)) {
            return null;
        }
        
        if (!Modifier.isPublic(mod)) {
            if (allowProtectedPropertyAccess) {
                field.setAccessible(true);
            }
            else {
                return null;
            }
        }
        
        return field;
    }
    
    
    
    /**
     * Updates a simple property with its type and accessor, and returns a type handler for it.
     * @param config the property configuration
     * @param field the property to update
     * @return a type handler for the property
     */
    private TypeHandler findTypeHandler(SimplePropertyConfig config, Property field) {
        
        Class<?> propertyType = field.getType();
        
        // configure type handler properties
        Properties typeHandlerProperties = null;
        if (config.getFormat() != null) {
            typeHandlerProperties = new Properties();
            typeHandlerProperties.put(ConfigurableTypeHandler.FORMAT_SETTING, config.getFormat());
        }

        // determine the type handler based on the named handler or the field class
        TypeHandler handler = null;
        if (config.getTypeHandlerInstance() != null) {
            handler = config.getTypeHandlerInstance();
        }
        else if (config.getTypeHandler() != null) {
            handler = typeHandlerFactory.getTypeHandler(config.getTypeHandler(), typeHandlerProperties);
            if (handler == null) {
                throw new BeanIOConfigurationException("No configured type handler named '" +
                    config.getTypeHandler() + "'");
            }
        }

        if (handler != null) {
            // if the property type was not already determine, use the type from the type handler
            if (propertyType == null) {
                propertyType = handler.getType();
                field.setType(propertyType);
            }
            // otherwise validate the property type is compatible with the type handler
            else if (!TypeUtil.isAssignable(propertyType, handler.getType())) {
                throw new BeanIOConfigurationException("Field property type '" +
                    propertyType.getName() + "' is not compatible " +
                    "with assigned type handler named '" + config.getTypeHandler() + "'");
            }
        }
        else {
            // assume String type if the property type was not determined any other way
            if (propertyType == null) {
                propertyType = String.class;
                field.setType(String.class);
            }

            // get a type handler for the the property type
            String typeName = config.getType();
            try {
                if (typeName == null) {
                    typeName = propertyType.getName();
                    handler = typeHandlerFactory.getTypeHandlerFor(propertyType, streamFormat, typeHandlerProperties);
                }
                else {
                    handler = typeHandlerFactory.getTypeHandlerFor(typeName, streamFormat, typeHandlerProperties);
                }
            }
            catch (IllegalArgumentException ex) {
                throw new BeanIOConfigurationException(ex.getMessage(), ex);
            }
            if (handler == null) {
                throw new BeanIOConfigurationException("Type handler not found for type '" + typeName + "'");
            }
        }
        
        return handler;
    }
    
    /**
     * Creates a property for holding other properties.
     * @param config the {@link PropertyConfig}
     * @return the created {@link Property} or null if the {@link PropertyConfig} was not 
     *   bound to a bean class 
     */
    protected Property createProperty(PropertyConfig config) {
        Class<?> beanClass = getBeanClass(config);
        if (beanClass == null) {
            return null;
        }
        
        Property property = null;
        if (Collection.class.isAssignableFrom(beanClass)) {
            boolean required = propertyStack.isEmpty();
            if (config.getComponentType() == ComponentConfig.SEGMENT) {
                required = config.getMinOccurs() > 0 && !config.isNillable();
            }
            boolean matchNull = !required && Integer.valueOf(0).equals(config.getMinOccurs());
            
            CollectionBean collection = new CollectionBean();
            collection.setName(config.getName());
            collection.setType(beanClass);
            collection.setRequired(required);
            collection.setMatchNull(matchNull);
            property = collection;
        }
        else {
            boolean required = propertyStack.isEmpty();
            boolean matchNull = !required && Integer.valueOf(0).equals(config.getMinOccurs());
            
            Bean bean = new Bean();
            bean.setName(config.getName());
            bean.setType(beanClass);
            bean.setLazy(config.isLazy());
            bean.setRequired(required);
            bean.setMatchNull(matchNull);
            property = bean;
        }
        
        return property;
    }
    
    /**
     * Returns the bean class for a segment configuration.
     * @param config the property configuration
     * @return the bean class
     * @throws BeanIOConfigurationException if the segment configuration is invalid
     */
    protected Class<?> getBeanClass(PropertyConfig config) {
        // determine the bean class associated with this record
        Class<?> beanClass = null;
        if (config.getType() != null) {
            if ("map".equals(config.getType())) {
                beanClass = HashMap.class;
            }
            else if ("list".equals(config.getType()) || "collection".equals(config.getType())) {
                beanClass = ArrayList.class;
            }
            else if ("set".equals(config.getType())) {
                beanClass = HashSet.class;
            }
            else {
                try {
                    beanClass = classLoader.loadClass(config.getType());
                    if (isReadEnabled() && (beanClass.isInterface() || Modifier.isAbstract(beanClass.getModifiers()))) {
                        throw new BeanIOConfigurationException("Class must be concrete unless " +
                            "stream mode is set to '" + StreamConfig.WRITE_ONLY_MODE + "'");
                    }
                }
                catch (ClassNotFoundException ex) {
                    throw new BeanIOConfigurationException(
                        "Invalid bean class '" + config.getType() + "'", ex);
                }
            }
        }
        return beanClass;
    }
    
    /**
     * Returns a concrete Class implementation for an aggregation type.
     * @param type the configured {@link Map} or {@link Collection} type
     * @return the concrete aggregation Class type
     */
    private Class<?> getConcreteAggregationType(Class<?> type) {
        if (type == null) {
            return null;
        }
        else if (type != TypeUtil.ARRAY_TYPE && 
            (type.isInterface() || Modifier.isAbstract(type.getModifiers()))) {
            
            if (Set.class.isAssignableFrom(type)) {
                return HashSet.class;
            }
            else if (Map.class.isAssignableFrom(type)) {
                return LinkedHashMap.class;
            }
            else {
                return ArrayList.class;
            }
        }
        else {
            return type;
        }
    }
    
    /**
     * Parses a default field value.
     * @param field the field
     * @param text the text to parse
     * @return the default value
     */
    protected Object parseDefaultValue(Field field, String text) {
        if (text == null) {
            return null;
        }
        
        TypeHandler handler = field.getHandler();
        if (handler != null) {
            try {
                return handler.parse(text);
            }
            catch (TypeConversionException ex) {
                throw new BeanIOConfigurationException("Type conversion failed for " +
                    "configured default '" + text + "': " + ex.getMessage(), ex);
            }
        }
        else {
            return text;
        }
    }
    
    /**
     * Returns the default {@link RecordParserFactory}.
     * @return the {@link RecordParserFactory}
     */
    protected abstract RecordParserFactory getDefaultRecordParserFactory();
    
    /**
     * Sets the type handler factory to use to create the stream definition.
     * @param typeHandlerFactory the <tt>TypeHandlerFactory</tt> to use to
     *   create the stream definition
     */
    @Override
    public void setTypeHandlerFactory(TypeHandlerFactory typeHandlerFactory) {
        this.typeHandlerFactory = typeHandlerFactory;
    }
    
    /**
     * Returns whether the stream definition must support reading
     * an input stream.
     * @return <tt>true</tt> if the stream definition must support reading
     *   an input stream
     */
    public boolean isReadEnabled() {
        return readEnabled;
    }

    /**
     * Returns whether the stream definition must support writing to an
     * output stream.
     * @return <tt>true</tt> if the stream definition must support writing
     *   to an output stream
     */
    public boolean isWriteEnabled() {
        return writeEnabled;
    }
    
    /**
     * Creates the {@link RecordParserFactory} for a stream configuration.
     * @param config the stream configuration
     * @return the created {@link RecordParserFactory}
     */
    protected RecordParserFactory createRecordParserFactory(StreamConfig config) {
        RecordParserFactory factory;
        
        // configure the record writer factory
        BeanConfig<RecordParserFactory> parserFactoryBean = config.getParserFactory();
        if (parserFactoryBean == null) {
            factory = getDefaultRecordParserFactory();
        }
        else if (parserFactoryBean.getInstance() != null) {
            factory = parserFactoryBean.getInstance();
        }
        else {
            if (parserFactoryBean.getClassName() == null) {
                factory = getDefaultRecordParserFactory();
            }
            else {
                Object object = BeanUtil.createBean(classLoader, parserFactoryBean.getClassName());
                if (!RecordParserFactory.class.isAssignableFrom(object.getClass())) {
                    throw new BeanIOConfigurationException("Configured writer factory class '" +
                        parserFactoryBean.getClassName() + "' does not implement RecordWriterFactory");
                }
                factory = (RecordParserFactory) object;
            }
            
            BeanUtil.configure(factory, parserFactoryBean.getProperties());
        }
        
        try {
            factory.init();
            return factory;
        }
        catch (IllegalArgumentException ex) {
            throw new BeanIOConfigurationException("Invalid parser setting(s): " + ex.getMessage(), ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.factory.ParserFactory#setClassLoader(java.lang.ClassLoader)
     */
    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    
}
