package org.beanio.internal.util;

import org.beanio.types.*;

/**
 * Default {@link Enum} type handler that uses {@link Enum#valueOf(Class, String)}
 * to parse a value and {@link Enum#name()} to format a value.
 * 
 * @author Kevin Seim
 * @since 2.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class EnumTypeHandler implements TypeHandler {

    private Class<Enum> type;
    
    /**
     * Constructs a new <tt>EnumTypeHandler</tt>.
     * @param type the Enum class
     */
    public EnumTypeHandler(Class<Enum> type) {
        this.type = type;
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
        try {
            return Enum.valueOf(type, text);
        }
        catch (IllegalArgumentException ex) {
            throw new TypeConversionException("Invalid " + getType().getSimpleName() + 
                " enum value '" + text + "'", ex);
        }
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
        return ((Enum)value).name();
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
