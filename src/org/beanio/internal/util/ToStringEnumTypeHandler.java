package org.beanio.internal.util;

import java.util.*;

import org.beanio.types.*;

/**
 * An {@link Enum} type handler that uses {@link Enum#toString()} 
 * to parse and format Enum values.
 * 
 * @author Kevin Seim
 * @since 2.0.1
 */
@SuppressWarnings({"rawtypes"})
public class ToStringEnumTypeHandler implements TypeHandler {

    private Class<Enum> type;
    private Map<String,Enum> map;
    
    /**
     * Constructs a new <tt>ToStringEnumTypeHandler</tt>.
     * @param type the Enum class
     */
    public ToStringEnumTypeHandler(Class<Enum> type) {
        this.type = type;
        
        map = new HashMap<>();
        
        Enum[] values = type.getEnumConstants();
        for (Enum value : values) {
            map.put(value.toString(), value);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#parse(java.lang.String)
     */
    @Override
    public Object parse(String text) throws TypeConversionException {
        if (text == null || "".equals(text)) {
            return null;
        }
        
        Enum value = map.get(text);
        if (value == null) {
            throw new TypeConversionException("Invalid " + getType().getSimpleName() + 
                " enum value '" + text + "'");                
        }
        return value;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#format(java.lang.Object)
     */
    @Override
    public String format(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#getType()
     */
    @Override
    public Class<?> getType() {
        return type;
    }
}
