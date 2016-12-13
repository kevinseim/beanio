/*
 * Copyright 2013 Kevin Seim
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
package org.beanio.internal.config.annotation;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import org.beanio.BeanIOConfigurationException;
import org.beanio.annotation.*;
import org.beanio.annotation.Field;
import org.beanio.internal.config.*;
import org.beanio.internal.util.TypeUtil;

/**
 * Factory class for building component configurations from annotated classes.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 */
public class AnnotationParser {

    private static final Comparator<ComponentConfig> ORDINAL_COMPARATOR = new Comparator<ComponentConfig>() {
        public int compare(ComponentConfig c1, ComponentConfig c2) {
            Integer o1 = c1.getOrdinal();
            Integer o2 = c2.getOrdinal();
            
            if (o1 == null) {
                return o2 == null ? 0 : 1;
            }
            else if (o2 == null) {
                return -1;
            }
            else {
                return o1.compareTo(o2);
            }
        }
    };
    
    /**
     * Creates a {@link GroupConfig} from the given type, if the type is annotated
     * using {@link Group}.
     * @param classLoader the classloader to load the type
     * @param type the type name
     * @return the {@link GroupConfig} or null if the type was not annotated
     */
    public static GroupConfig createGroupConfig(ClassLoader classLoader, String type) {
        Class<?> clazz = TypeUtil.toBeanType(classLoader, type);
        if (clazz == null) {
            return null;
        }
        return createGroupConfig(clazz);
    }
    
    /**
     * Creates a {@link GroupConfig} from the given type, if the type is annotated
     * using {@link Group}.
     * @param clazz the group type
     * @return the {@link GroupConfig} or null if the type was not annotated
     */
    public static GroupConfig createGroupConfig(Class<?> clazz) {
        Group group = clazz.getAnnotation(Group.class);
        if (group == null) {
            return null;
        }
        
        String name = toValue(group.name());
        if (name == null) {
            name = Introspector.decapitalize(clazz.getSimpleName());
        }
        
        TypeInfo info = new TypeInfo();
        info.name = name;
        info.type = clazz;
        return createGroup(info, group);
    }
    
    /**
     * Creates a {@link RecordConfig} from the given type, if the type is annotated
     * using {@link Record}.
     * @param classLoader the classloader to load the type
     * @param type the type name
     * @return the {@link RecordConfig} or null if the type was not annotated
     */
    public static RecordConfig createRecordConfig(ClassLoader classLoader, String type) {
        Class<?> clazz = TypeUtil.toBeanType(classLoader, type);
        if (clazz == null) {
            return null;
        }
        return createRecordConfig(clazz);
    }
    
    /**
     * Creates a {@link RecordConfig} from the given type, if the type is annotated
     * using {@link Record}.
     * @param clazz the record type
     * @return the {@link RecordConfig} or null if the class was not annotated
     */
    public static RecordConfig createRecordConfig(Class<?> clazz) {
        Record record = clazz.getAnnotation(Record.class);
        if (record == null) {
            return null;
        }
        
        String name = toValue(record.name());
        if (name == null) {
            name = Introspector.decapitalize(clazz.getSimpleName());
        }
        
        TypeInfo info = new TypeInfo();
        info.name = name;
        info.type = clazz;
        return createRecord(info, record);
    }
    
    /**
     * 
     * @param config
     * @param clazz
     */
    private static void addAllChildren(ComponentConfig config, Class<?> clazz) {
        Class<?> superclazz = clazz.getSuperclass();
        if (superclazz != null && superclazz != Object.class) {
            addAllChildren(config, superclazz);
        }
        for (Class<?> intf : clazz.getInterfaces()) {
            addAllChildren(config, intf);
        }
        addChildren(config, clazz);
    }
    
    private static void handleConstructor(ComponentConfig config, Class<?> clazz) {
        try {
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                
                Class<?>[] parameters = constructor.getParameterTypes();
                Type[] parameterTypes = constructor.getGenericParameterTypes();
                Annotation[][] annotations = constructor.getParameterAnnotations();
                
                for (int i=0; i<annotations.length; i++) {
                    Field fa = null; 
                    
                    for (int j=0; j<annotations[i].length; j++) {
                        if (annotations[i][j].annotationType() == Field.class) {
                            fa = (Field) annotations[i][j];
                            break;
                        }
                    }
                    
                    if (fa != null) {
                        TypeInfo info = new TypeInfo();
                        info.carg = i + 1;
                        info.name = toValue(fa.name());
                        info.type = parameters[i];
                        info.genericType = parameterTypes[i];
                        
                        config.add(createField(info, fa));
                    }
                }
            }
        }
        catch (IllegalArgumentException ex) {
            throw new BeanIOConfigurationException("Invalid @Field annotation on a " +
                "constructor parameter in class '" + clazz.getName() + "': " + ex.getMessage(), ex);
        }
    }
    
    /**
     * 
     * @param config
     * @param parent
     */
    private static void addChildren(ComponentConfig config, Class<?> parent) {
        if (config.getComponentType() == ComponentConfig.GROUP) {
            addGroupChildren(config, parent);
        }
        else {
            addRecordChildren(config, parent);
        }
    }
    
    private static void addGroupChildren(ComponentConfig config, Class<?> parent) {
        for (java.lang.reflect.Field field : parent.getDeclaredFields()) {
            Group ga = field.getAnnotation(Group.class);
            Record ra = field.getAnnotation(Record.class);
            if (ra == null && ga == null) {
                continue;
            }
            if (ra != null && ga != null) {
                throw new BeanIOConfigurationException("Field '" + field.getName() +
                    "' on class '" + parent.getName() + "' cannot be annotated with " +
                    "both @Record and @Group");
            }
            
            TypeInfo info = new TypeInfo();
            info.bound = true;
            info.name = field.getName();
            info.type = field.getType();
            info.genericType = field.getGenericType();
            
            PropertyConfig child;
            try {
                if (ra != null) { // record
                    child = createRecord(info, ra);
                }
                else { // group
                    child = createGroup(info, ga);
                }
            }
            catch (IllegalArgumentException ex) {
                throw new BeanIOConfigurationException("Invalid annotation for field '" +
                    field.getName() + "' on class '" + parent.getName() + "': " + ex.getMessage(), ex);
            }
            
            config.add(child);
        }
        
        for (Method method : parent.getDeclaredMethods()) {
            Group ga = method.getAnnotation(Group.class);
            Record ra = method.getAnnotation(Record.class);
            if (ra == null && ga == null) {
                continue;
            }
            if (ra != null && ga != null) {
                throw new BeanIOConfigurationException("Method '" + method.getName() +
                    "' on class '" + parent.getName() + "' cannot be annotated with " +
                    "both @Record and @Group");
            }
            
            Class<?> clazz;
            Type type;
            String name = method.getName();
            String getter = null;
            String setter = null;
            
            // is this a getter or setter?
            if (method.getReturnType() != void.class &&
                method.getParameterTypes().length == 0) {
                
                getter = name;
                clazz = method.getReturnType();
                type = method.getGenericReturnType();
                if (name.startsWith("get")) {
                    name = name.substring(3);
                }
                else if (name.startsWith("is")) {
                    name = name.substring(2);
                }
            }
            else if (method.getReturnType() == void.class &&
                method.getParameterTypes().length == 1) {
                
                setter = name;
                clazz = method.getParameterTypes()[0];
                type = method.getGenericParameterTypes()[0];
                
                if (name.startsWith("set")) {
                    name = name.substring(3);
                }
            }
            else {
                throw new BeanIOConfigurationException("Method '" + method.getName() + 
                    "' on class '" + parent.getName() + "' is not a valid getter or setter");
            }
            
            name = Introspector.decapitalize(name);
            
            TypeInfo info = new TypeInfo();
            info.bound = true;
            info.name = name;
            info.type = clazz;
            info.genericType = type;
            info.getter = getter;
            info.setter = setter;
            
            PropertyConfig child;
            try {
                if (ra != null) { // record
                    child = createRecord(info, ra);
                }
                else { // group
                    child = createGroup(info, ga);
                }
            }
            catch (IllegalArgumentException ex) {
                throw new BeanIOConfigurationException("Invalid annotation for method '" +
                    method.getName() + "' on class '" + parent.getName() + "': " + ex.getMessage(), ex);
            }
            
            config.add(child);
        }
    }
    
    private static void addRecordChildren(ComponentConfig config, Class<?> parent) {
        
        for (java.lang.reflect.Field field : parent.getDeclaredFields()) {
            Field fa = field.getAnnotation(Field.class);
            Segment sa = field.getAnnotation(Segment.class);
            if (fa == null && sa == null) {
                continue;
            }
            if (fa != null && sa != null) {
                throw new BeanIOConfigurationException("Field '" + field.getName() +
                    "' on class '" + parent.getName() + "' cannot be annotated with " +
                    "both @Field and @Segment");
            }
            
            TypeInfo info = new TypeInfo();
            info.name = field.getName();
            info.type = field.getType();
            info.genericType = field.getGenericType();
            
            PropertyConfig child;
            try {
                if (fa != null) { // field
                    child = createField(info, fa);
                }
                else { // segment
                    child = createSegment(info, sa, field.getAnnotation(Fields.class));
                }
            }
            catch (IllegalArgumentException ex) {
                throw new BeanIOConfigurationException("Invalid annotation for field '" +
                    field.getName() + "' on class '" + parent.getName() + "': " + ex.getMessage(), ex);
            }
            
            config.add(child);
        }

        for (Method method : parent.getDeclaredMethods()) {
            Field fa = method.getAnnotation(Field.class);
            Segment sa = method.getAnnotation(Segment.class);
            if (fa == null && sa == null) {
                continue;
            }
            if (fa != null && sa != null) {
                throw new BeanIOConfigurationException("Method '" + method.getName() + 
                    "' on class '" + parent.getName() + "' cannot be annotated with " +
                    "both @Field and @Segment");
            }
            
            Class<?> clazz;
            Type type;
            String name = method.getName();
            String getter = null;
            String setter = null;
            
            // is this a getter or setter?
            if (method.getReturnType() != void.class &&
                method.getParameterTypes().length == 0) {
                
                getter = name;
                clazz = method.getReturnType();
                type = method.getGenericReturnType();
                if (name.startsWith("get")) {
                    name = name.substring(3);
                }
                else if (name.startsWith("is")) {
                    name = name.substring(2);
                }
            }
            else if (method.getReturnType() == void.class &&
                method.getParameterTypes().length == 1) {
                
                setter = name;
                clazz = method.getParameterTypes()[0];
                type = method.getGenericParameterTypes()[0];
                
                if (name.startsWith("set")) {
                    name = name.substring(3);
                }
            }
            else {
                throw new BeanIOConfigurationException("Method '" + method.getName() + 
                    "' on class '" + parent.getName() + "' is not a valid getter or setter");
            }
            
            name = Introspector.decapitalize(name);
            
            TypeInfo info = new TypeInfo();
            info.name = name;
            info.type = clazz;
            info.genericType = type;
            info.getter = getter;
            info.setter = setter;
            
            PropertyConfig child;
            try {
                if (fa != null) { // field
                    child = createField(info, fa);
                }
                else { // segment
                    child = createSegment(info, sa, method.getAnnotation(Fields.class));
                }
            }
            catch (IllegalArgumentException ex) {
                throw new BeanIOConfigurationException("Invalid annotation for method '" +
                    method.getName() + "' on class '" + parent.getName() + "': " + ex.getMessage(), ex);
            }
            
            config.add(child);
        }
    }
    
    private static GroupConfig createGroup(TypeInfo info, Group group) {
        updateTypeInfo(info, group.type(), group.collection());
        
        GroupConfig gc = new GroupConfig();
        gc.setName(info.name);
        gc.setType(info.propertyName);
        gc.setOrder(toValue(group.order()));
        gc.setCollection(info.collectionName);
        
        Integer minOccurs = toValue(group.minOccurs());
        Integer maxOccurs = toUnboundedValue(group.maxOccurs());
        if (maxOccurs == null && info.collectionName == null && info.bound) {
            maxOccurs = 1;
        }
        gc.setMinOccurs(minOccurs);
        gc.setMaxOccurs(maxOccurs);
        gc.setXmlType(group.xmlType().toValue());
        gc.setXmlName(toXmlValue(group.xmlName()));
        gc.setXmlNamespace(toXmlValue(group.xmlNamespace()));
        gc.setXmlPrefix(toXmlValue(group.xmlPrefix()));
        
        addAllChildren(gc, info.propertyType);
        return gc;
    }
    
    private static RecordConfig createRecord(TypeInfo info, Record record) {
        
        updateTypeInfo(info, record.type(), record.collection());
        
        RecordConfig rc = new RecordConfig();
        rc.setName(info.name);
        
        String target = toValue(record.value());
        if (target == null) {
            rc.setType(info.propertyName);
        }
        else {
            rc.setTarget(target);
        }
        rc.setCollection(info.collectionName);
        rc.setOrder(toValue(record.order()));
        
        Integer minOccurs = toValue(record.minOccurs());
        Integer maxOccurs = toUnboundedValue(record.maxOccurs());
        if (info.bound && maxOccurs == null && info.collectionName == null) {
            maxOccurs = 1;
        }
        rc.setMinOccurs(minOccurs);
        rc.setMaxOccurs(maxOccurs);
        rc.setMinLength(toValue(record.minLength()));
        rc.setMaxLength(toUnboundedValue(record.maxLength()));
        rc.setMinMatchLength(toValue(record.minRidLength()));
        rc.setMaxMatchLength(toUnboundedValue(record.maxRidLength()));
        rc.setXmlType(record.xmlType().toValue());
        rc.setXmlName(toXmlValue(record.xmlName()));
        rc.setXmlNamespace(toXmlValue(record.xmlNamespace()));
        rc.setXmlPrefix(toXmlValue(record.xmlPrefix()));
        
        Fields fields = info.propertyType.getAnnotation(Fields.class);
        if (fields != null) {
            for (Field field : fields.value()) {
                rc.add(createField(null, field));
            }
        }
        
        handleConstructor(rc, info.propertyType);
        addAllChildren(rc, info.propertyType);
        rc.sort(ORDINAL_COMPARATOR);
        return rc;
    }
    
    private static SegmentConfig createSegment(TypeInfo info, Segment sa, Fields fields) {
        updateTypeInfo(info, sa.type(), sa.collection());
        
        if (info.propertyType == String.class) {
            throw new IllegalArgumentException("type is undefined");
        }
        
        String target = toValue(sa.value());
        
        SegmentConfig sc = new SegmentConfig();
        sc.setName(info.name);
        sc.setLabel(toValue(sa.name()));
        if (target == null) {
            sc.setType(info.propertyName);
        }
        else {
            sc.setTarget(target);
        }
        sc.setCollection(info.collectionName);
        sc.setGetter(toValue(sa.getter()));
        if (sc.getGetter() == null) {
            sc.setGetter(info.getter);
        }
        sc.setSetter(toValue(sa.setter()));
        if (sc.getSetter() == null) {
            sc.setSetter(info.setter);
        }
        sc.setPosition(toValue(sa.at()));
        sc.setUntil(toValue(sa.until()));
        sc.setOrdinal(toValue(sa.ordinal()));
        sc.setMinOccurs(toValue(sa.minOccurs()));
        sc.setMaxOccurs(toUnboundedValue(sa.maxOccurs()));
        sc.setOccursRef(toValue(sa.occursRef()));
        sc.setKey(toValue(sa.key()));
        sc.setLazy(sa.lazy());
        sc.setXmlType(sa.xmlType().toValue());
        sc.setXmlName(toXmlValue(sa.xmlName()));
        sc.setXmlNamespace(toXmlValue(sa.xmlNamespace()));
        sc.setXmlPrefix(toXmlValue(sa.xmlPrefix()));
        sc.setNillable(sa.nillable());
        
        if (sc.getName() == null) {
            throw new IllegalArgumentException("name is undefined");
        }
        
        if (fields != null) {
            for (Field field : fields.value()) {
                sc.add(createField(null, field));
            }
        }
        fields = info.propertyType.getAnnotation(Fields.class);
        if (fields != null) {
            for (Field field : fields.value()) {
                sc.add(createField(null, field));
            }
        }
        
        handleConstructor(sc, info.propertyType);
        addAllChildren(sc, info.propertyType);
        
        return sc;
    }
    
    /**
     * Creates a new field configuration.
     * @param info the reflected property information, or null if not bound to a field
     * @param fa the field annotation
     * @return the field configuration
     */
    private static FieldConfig createField(TypeInfo info, Field fa) {
        FieldConfig fc = new FieldConfig();
        
        if (info != null) {
            updateTypeInfo(info, fa.type(), fa.collection());
            
            fc.setName(info.name);
            fc.setLabel(toValue(fa.name()));
            fc.setType(info.propertyName);
            fc.setCollection(info.collectionName);
            fc.setBound(true);
            
            fc.setGetter(toValue(fa.getter()));
            if (fc.getGetter() == null) {
                fc.setGetter(info.getter);
            }
            
            String setter = toValue(fa.setter());
            if (info.carg != null) {
                fc.setSetter("#" + info.carg);
                if (setter != null) {
                    throw new BeanIOConfigurationException("setter not allowed");
                }
            }
            else {
                fc.setSetter(setter);
                if (fc.getSetter() == null) {
                    fc.setSetter(info.setter);
                }
            }
        }
        else {
            fc.setName(toValue(fa.name()));
            fc.setLabel(fc.getName());
            fc.setBound(false);
        }
        if (fc.getName() == null) {
            throw new IllegalArgumentException("name is required");
        }
        
        fc.setLiteral(toValue(fa.literal()));
        fc.setPosition(toValue(fa.at()));
        fc.setUntil(toValue(fa.until()));
        fc.setOrdinal(toValue(fa.ordinal()));
        fc.setRegex(toValue(fa.regex()));
        fc.setFormat(toValue(fa.format()));
        fc.setRequired(fa.required());
        fc.setDefault(toValue(fa.defaultValue()));
        fc.setIdentifier(fa.rid());
        fc.setTrim(fa.trim());
        fc.setLazy(fa.lazy());
        fc.setMinLength(toValue(fa.minLength()));
        fc.setMaxLength(toUnboundedValue(fa.maxLength()));
        fc.setMinOccurs(toValue(fa.minOccurs()));
        fc.setMaxOccurs(toUnboundedValue(fa.maxOccurs()));
        fc.setOccursRef(toValue(fa.occursRef()));
        
        fc.setLength(toValue(fa.length()));
        if (fa.padding() >= Character.MIN_VALUE && fa.padding() <= Character.MAX_VALUE) {
            fc.setPadding((char) fa.padding());
        }
        fc.setJustify(fa.align().toString().toLowerCase());
        fc.setKeepPadding(fa.keepPadding());
        fc.setLenientPadding(fa.lenientPadding());
        
        fc.setTypeHandler(toValue(fa.handlerName()));
        Class<?> handler = toValue(fa.handlerClass());
        if (handler != null && fc.getTypeHandler() == null) {
            fc.setTypeHandler(fa.handlerClass().getName());
        }
        
        fc.setXmlType(fa.xmlType().toValue());
        fc.setXmlName(toXmlValue(fa.xmlName()));
        fc.setXmlNamespace(toXmlValue(fa.xmlNamespace()));
        fc.setXmlPrefix(toXmlValue(fa.xmlPrefix()));
        fc.setNillable(fa.nillable());
        
        return fc;
    }
    
    private static class TypeInfo {
        boolean bound;
        Integer carg;
        String name;
        Class<?> type;
        Type genericType;
        
        String propertyName; // the class name of propertyType
        String collectionName;
        Class<?> propertyType;
        String getter;
        String setter;
    }
    
    /**
     * 
     * @param info the type information obtained via reflection
     * @param annotatedType the configured type from the annotation
     * @param annotatedCollection the configured collectionType from the annotation
     */
    private static void updateTypeInfo(TypeInfo info, Class<?> annotatedType, Class<?> annotatedCollection) {

        annotatedType = toValue(annotatedType);
        
        String propertyName = null;
        String collectionName = null;
        Class<?> propertyType = info.type;
        
        if (propertyType.isArray()) {
            if (annotatedType != null) {
                propertyType = annotatedType;
            }
            else {
                propertyType = propertyType.getComponentType();
                if (propertyType.isPrimitive()) {
                    propertyType = TypeUtil.toWrapperClass(propertyType);
                }
            }
            collectionName = "array";
        }
        else if (Map.class.isAssignableFrom(propertyType)) {
            Class<?> collectionType = toValue(annotatedCollection);
            if (collectionType == null) {
                collectionType = propertyType;
                propertyType = null;
            }
            
            if (annotatedType != null) {
                propertyType = annotatedType;
            }
            else {
                if (info.genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) info.genericType;
                    if (pt.getActualTypeArguments().length > 1) {
                        propertyType = (Class<?>) pt.getActualTypeArguments()[1];
                    }
                }
                if (propertyType == null) {
                    propertyType = String.class;
                }
            }
            collectionName = collectionType.getName();
        }
        else if (Collection.class.isAssignableFrom(propertyType)) {
            Class<?> collectionType = toValue(annotatedCollection);
            if (collectionType == null) {
                collectionType = propertyType;
                propertyType = null;
            }
            
            if (annotatedType != null) {
                propertyType = annotatedType;
            }
            else {
                if (info.genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) info.genericType;
                    if (pt.getActualTypeArguments().length > 0) {
                        propertyType = (Class<?>) pt.getActualTypeArguments()[0];
                    }
                }
                if (propertyType == null) {
                    propertyType = String.class;
                }
            }
            
            collectionName = collectionType.getName();
        }
        else {
            if (annotatedType != null) {
                propertyType = annotatedType;
            }
            else if (propertyType.isPrimitive()) {
                propertyType = TypeUtil.toWrapperClass(propertyType);
            }
        }
        
        if (propertyName == null) {
            propertyName = propertyType.getName();
        }
        
        info.propertyType = propertyType;
        info.propertyName = propertyName;
        info.collectionName = collectionName;
        
    }
    
    private static Class<?> toValue(Class<?> type) {
        return (Void.class == type) ? null : type;
    }
    
    private static Integer toValue(int n) {
        return n == Integer.MIN_VALUE ? null : n;
    }
    
    private static Integer toUnboundedValue(int n) {
        Integer val = toValue(n);
        if (val == null) {
            return null;
        }
        if (val.compareTo(new Integer(0)) < 0) {
            return Integer.MAX_VALUE;
        }
        return val;
    }
    
    private static String toValue(String s) {
        return "".equals(s) ? null : s;
    }
    
    private static String toXmlValue(String s) {
        return AnnotationConstants.UNDEFINED.equals(s) ? null : s;
    }
}
