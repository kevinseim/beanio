/*
 * Copyright 2011-2014 Kevin Seim
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
package org.beanio.internal.parser;

import java.util.Set;
import java.util.regex.*;

import org.beanio.*;
import org.beanio.internal.util.*;
import org.beanio.types.*;

/**
 * A parser for marshalling and unmarshalling a single field in a record.  A field is usually, but
 * optionally, bound to a simple property value.
 * 
 * <p>A field component does not have any children.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class Field extends ParserComponent implements Property {

    private static final boolean ERROR_IF_NULL_PRIMITIVE = 
        Settings.getInstance().getBoolean(Settings.ERROR_IF_NULL_PRIMITIVE);
    
    private static final boolean USE_DEFAULT_IF_MISSING = 
        Settings.getInstance().getBoolean(Settings.USE_DEFAULT_IF_MISSING);

    private static final boolean VALIDATE_ON_MARSHAL = 
            Settings.getInstance().getBoolean(Settings.VALIDATE_ON_MARSHAL);
    
    private static final boolean marshalDefault = 
        Settings.getInstance().getBoolean(Settings.DEFAULT_MARSHALLING_ENABLED);
    
    private ParserLocal<Object> value = new ParserLocal<Object>(Value.MISSING);
    
    private boolean bound;
    private boolean identifier;
    
    /* validation settings */
    private boolean trim;
    private boolean required;
    private boolean lazy;
    private int minLength = 0;
    private int maxLength = Integer.MAX_VALUE;
    private String literal = null;
    private Pattern regex = null;
    private Object defaultValue;
    
    /* 
     * the property type
     * if this field is not a property, then null
     * if this field is an array, then the array component type
     */
    private Class<?> propertyType;
    private TypeHandler handler;
    private PropertyAccessor accessor;
    private FieldFormat format;
    
    /**
     * Constructs a new <tt>Field</tt>.
     */
    public Field() {
        super(0);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#hasContent()
     */
    @Override
    public boolean hasContent(ParsingContext context) {
        if (isBound()) {
            return getValue(context) != Value.MISSING;
        }
        else {
            // fields that aren't bound to a property of a bean object are
            // always considered to have content during marshalling
            return true;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Property#type()
     */
    @Override
    public int type() {
        return Property.SIMPLE;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Parser#isOptional()
     */
    @Override
    public boolean isOptional() {
        return format.isLazy();
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Property#defines(java.lang.Object)
     */
    @Override
    public boolean defines(Object value) {
        if (value == null) {
            return false;
        }
        
        if (!isIdentifier()) {
            return true;
        }
        
        if (!TypeUtil.isAssignable(getPropertyType(), value.getClass())) {
            return false;
        }
        
        return isMatch(formatValue(value));
    }
        
    /**
     * Tests if the field text in the record matches this field.
     * @param context the {@link UnmarshallingContext} containing the record to match
     * @return <tt>true</tt> if the field text is a match or this field is not used
     *   to identify the record
     */
    @Override
    public boolean matches(UnmarshallingContext context) {
        if (isIdentifier()) {
            return isMatch(format.extract(context, false));
        }
        else {
            return true;
        }
    }
        
    /**
     * Returns <tt>true</tt> if the provided field text is a match for this field
     * definition based on the configured literal value or regular expression.
     * @param text the field text to test
     * @return <tt>true</tt> if the field text matches this field definitions constraints,
     *   or <tt>false</tt> if the field text is null or does not match
     */
    protected boolean isMatch(String text) {
        if (text == null)
            return false;
        if (text == Value.INVALID)
            return false;
        if (text == Value.NIL)
            return false;
        if (literal != null && !literal.equals(text))
            return false;
        if (regex != null && !regex.matcher(text).matches())
            return false;
        
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Marshaller#marshal(org.beanio.parser2.MarshallingContext)
     */
    @Override
    public boolean marshal(MarshallingContext context) {
        String text;
        if (literal != null) {
            text = literal;
        }
        else {
            Object value = getValue(context);
            
            // the default value may be used to override null property values
            // if enabled (since 1.2.2)
            if (marshalDefault && value == Value.MISSING) {
                value = defaultValue;
                setValue(context, defaultValue);
            }
            
            if (value == Value.MISSING) {
                value = null;
                setValue(context, null);
            }
            
            // allow the format to bypass type conversion
            if (format.insertValue(context, value)) {
                return true;
            }
            
            text = formatValue(value);
        }
        
        if (VALIDATE_ON_MARSHAL) {
            if (text == Value.NIL) {
                if (!format.isNillable()) {
                    throw new InvalidBeanException("Invalid field '" + getName() + "', the value is not nillable");    
                } else if (required) {
                    throw new InvalidBeanException("Invalid field '" + getName() + "', a value is required");    
                }
            }
            else if (text == null) {
                if (required) {
                    throw new InvalidBeanException("Invalid field '" + getName() + "', a value is required");
                }
            }
            else {
                // validate minimum length
                if (text.length() < minLength) {
                    throw new InvalidBeanException("Invalid field '" + getName() + "', '" + 
                        text + "' does not meet minimum length of " + minLength);
                }
                // validate maximum length
                if (text.length() > maxLength) {
                    throw new InvalidBeanException("Invalid field '" + getName() + "', '" + 
                        text + "' exceeds maximum length of " + maxLength);
                }
                // validate the regular expression
                if (regex != null && !regex.matcher(text).matches()) {
                    throw new InvalidBeanException("Invalid field '" + getName() + "', '" + 
                        text + "' does not match pattern '" + regex.pattern() + "'");
                }
            }
        }
        
        format.insertField(context, text);
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Parser#hasNext(org.beanio.parser2.UnmarshallingContext)
     */
    public boolean hasNext(UnmarshallingContext context) {
        return format.extract(context, false) != null;
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Unmarshaller#unmarshal(org.beanio.parser2.UnmarshallingContext)
     */
    @Override
    public boolean unmarshal(UnmarshallingContext context) {
        String text = format.extract(context, true);
        if (text == null) {
            // minOccurs is validated at the segment level
            Object value = Value.MISSING;
            if (USE_DEFAULT_IF_MISSING && defaultValue != null) {
                value = defaultValue;
            }
            setValue(context, value);
            return false;
        }
        
        if (text == Value.INVALID) {
            this.value.set(context, Value.INVALID);
        }
        else {
            this.value.set(context, parseValue(context, text));
        }
        return true;
    }
    
    /**
     * Parses and validates a field property value from the given field text.
     * @param context the {@link UnmarshallingContext} to report field errors to
     * @param fieldText the field text to parse
     * @return the parsed field value, or {@link Value#INVALID} if the field was invalid,
     *   or {@link Value#MISSING} if the field was not present in the record
     */
    protected Object parseValue(UnmarshallingContext context, String fieldText) {
        boolean valid = true;
        String text = fieldText;
        
        if (text == Value.NIL) {            
            // validate field is nillable
            if (!format.isNillable()) {
                context.addFieldError(getName(), null, "nillable");
                return Value.INVALID;
            }
            // collections are not further validated
            else if (required) {
                context.addFieldError(getName(), null, "required");
                return Value.INVALID;
            }
            // return the default value if set
            else if (defaultValue != null) {
                return defaultValue;
            }
            
            return null;
        }
        
        // repeating fields are always optional
        if (text == null) {
            if (!format.isLazy()) {
                context.addFieldError(getName(), null, "minOccurs", 1);
                return Value.INVALID;
            }
        }
        else {
            // trim before validation if configured
            if (trim) {
                text = text.trim();     
            }
            if (lazy && text.length() == 0) {
                text = null;
            }
        }
        
        // check if field exists
        if (text == null || text.length() == 0) {
            // validation for required fields
            if (required) {
                context.addFieldError(getName(), fieldText, "required");
                valid = false;
            }
            // return the default value if set
            else if (defaultValue != null) {
                return defaultValue;
            }
        }
        else {
            // validate constant fields
            if (literal != null && !literal.equals(text)) {
                context.addFieldError(getName(), fieldText, "literal", literal);
                valid = false;
            }
            // validate minimum length
            if (text.length() < minLength) {
                context.addFieldError(getName(), fieldText, "minLength", minLength, maxLength);
                valid = false;
            }
            // validate maximum length
            if (text.length() > maxLength) {
                context.addFieldError(getName(), fieldText, "maxLength", minLength, maxLength);
                valid = false;
            }
            // validate the regular expression
            if (regex != null && !regex.matcher(text).matches()) {
                context.addFieldError(getName(), fieldText, "regex", regex.pattern());
                valid = false;
            }
        }

        // type conversion is skipped if the text does not pass other validations
        if (!valid) {
            return Value.INVALID;
        }
        
        // perform type conversion and return the result
        try {
            // if there is no type handler, assume its a String
            Object value = (handler == null) ? text : handler.parse(text);
            
            // validate primitive values are not null
            if (value == null && ERROR_IF_NULL_PRIMITIVE && propertyType != null && propertyType.isPrimitive()) {
                context.addFieldError(getName(), fieldText, "type",
                    "Primitive property values cannot be null");
                return Value.INVALID;
            }
            
            return value;
        }
        catch (TypeConversionException ex) {
            context.addFieldError(getName(), fieldText, "type", ex.getMessage());
            return Value.INVALID;
        }
        catch (Exception ex) {
            throw new BeanReaderException("Type conversion failed for field '" + getName() + 
                "' while parsing text '" + fieldText + "'", ex);
        }
    }
    
    /**
     * Formats a field/property value.
     * @param value the property value to format
     * @return the formatted field text
     */
    protected String formatValue(Object value) {
        String text = null;
        if (handler != null) {
            try {
                text = handler.format(value);
                
                if (text == TypeHandler.NIL) {
                    if (format.isNillable()) {
                        return Value.NIL;
                    }
                    text = null;
                }
            }
            catch (Exception ex) {
                throw new BeanWriterException("Type conversion failed for field '" +
                    getName() + "' while formatting value '" + value + "'", ex);
            }
        }
        else if (value != null) {
            text = value.toString();
        }

        return text;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Parser#clearValue()
     */
    @Override
    public void clearValue(ParsingContext context) {
        this.value.set(context, Value.MISSING);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#createValue()
     */
    @Override
    public Object createValue(ParsingContext context) {
        return getValue(context);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Parser#getValue()
     */
    @Override
    public Object getValue(ParsingContext context) {
        return value.get(context);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Parser#setValue(java.lang.Object)
     */
    @Override
    public void setValue(ParsingContext context, Object value) {
        this.value.set(context, value == null ? Value.MISSING : value);
    }
    
    @Override
    protected boolean isSupportedChild(Component child) {
        return false;
    }
    
    /**
     * Returns the regular expression pattern the field text parsed by this field
     * definition must match.
     * @return the regular expression pattern
     */
    public String getRegex() {
        return regex == null ? null : regex.pattern();
    }

    /**
     * Sets the regular expression pattern the field text parsed by this field
     * definition must match.
     * @param pattern the regular expression pattern
     * @throws PatternSyntaxException if the pattern is invalid
     */
    public void setRegex(String pattern) throws PatternSyntaxException {
        if (pattern == null)
            this.regex = null;
        else
            this.regex = Pattern.compile(pattern);
    }

    /**
     * Returns the regular expression the field text parsed by this field
     * definition must match.
     * @return the regular expression
     */
    protected Pattern getRegexPattern() {
        return regex;
    }
    
    @Override
    public void registerLocals(Set<ParserLocal<? extends Object>> locals) {
        if (locals.add(value)) {
            super.registerLocals(locals);
        }
    }

    public void setPropertyType(Class<?> type) {
        this.propertyType = type;
    }
    
    public Class<?> getPropertyType() {
        return propertyType;
    }

    @Override
    public boolean isIdentifier() {
        return identifier;
    }
    
    @Override
    public void setIdentifier(boolean recordIdentifier) {
        this.identifier = recordIdentifier;
    }

    public FieldFormat getFormat() {
        return format;
    }

    public void setFormat(FieldFormat format) {
        this.format = format;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    @Override
    public Class<?> getType() {
        return propertyType;
    }
    
    public boolean isTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setRegex(Pattern regex) {
        this.regex = regex;
    }

    @Override
    public void setType(Class<?> type) {
        this.propertyType = type;
    }
    
    @Override
    public PropertyAccessor getAccessor() {
        return accessor;
    }

    @Override
    public void setAccessor(PropertyAccessor accessor) {
        this.accessor = accessor;
    }
    
    /**
     * Returns the default value for a field parsed by this field definition
     * when the field text is null or the empty string (after trimming).
     * @return default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value for a field parsed by this field definition
     * when the field text is null or the empty string (after trimming).
     * @param defaultValue the default value
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public TypeHandler getHandler() {
        return handler;
    }

    public void setHandler(TypeHandler handler) {
        this.handler = handler;
    }
    
    @Override
    protected void toParamString(StringBuilder s) {
        super.toParamString(s);
        s.append(", type=").append(propertyType != null ? propertyType.getSimpleName() : null);
        s.append(", size=").append(Integer.toString(getSize()));
        s.append(", length=").append(DebugUtil.formatRange(minLength, maxLength));
        s.append(", ").append(DebugUtil.formatOption("bound", bound));
        s.append(", ").append(DebugUtil.formatOption("rid", identifier));
        s.append(", ").append(DebugUtil.formatOption("required", required));
        s.append(", ").append(DebugUtil.formatOption("lazy", lazy));
        s.append(", ").append(DebugUtil.formatOption("trim", trim));
        if (literal != null) {
            s.append(", literal=").append(literal);
        }
        if (regex != null) {
            s.append(", regex=").append(regex.toString());
        }
        if (defaultValue != null) {
            s.append(", default=").append(defaultValue);
        }
        s.append(", format=").append(format);
    }

    @Override
    public int getSize() {
        return format.getSize();
    }
    
    public boolean isBound() {
        return bound;
    }

    public void setBound(boolean property) {
        this.bound = property;
    }
}
