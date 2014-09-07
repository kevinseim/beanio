/*
 * Copyright 2010 Kevin Seim
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
package org.beanio.parser.types;

import java.math.*;
import java.net.URL;
import java.util.*;

/**
 * <tt>ObjectRecord</tt> is used to test type handlers for Objects.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class ObjectRecord {

	private Byte byteValue;
	private Short shortValue;
	private Integer integerValue;
	private Long longValue;
	private Float floatValue;
	private Double doubleValue;
	private Character characterValue;
	private String stringValue;
	private Date dateValue;
	private Boolean booleanValue;
	private BigInteger bigIntegerValue;
	private BigDecimal bigDecimalValue;
	private UUID id;
	private URL url;
	private Calendar calendar;
	private TypeEnum enum1;
	private TypeEnum enum2;
	
	public Byte getByteValue() {
		return byteValue;
	}
	public void setByteValue(Byte byteValue) {
		this.byteValue = byteValue;
	}
	public Short getShortValue() {
		return shortValue;
	}
	public void setShortValue(Short shortValue) {
		this.shortValue = shortValue;
	}
	public Integer getIntegerValue() {
		return integerValue;
	}
	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}
	public Long getLongValue() {
		return longValue;
	}
	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}
	public Float getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(Float floatValue) {
		this.floatValue = floatValue;
	}
	public Double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}
	public Character getCharacterValue() {
		return characterValue;
	}
	public void setCharacterValue(Character characterValue) {
		this.characterValue = characterValue;
	}
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public Date getDateValue() {
		return dateValue;
	}
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}
	public Boolean getBooleanValue() {
		return booleanValue;
	}
	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	public BigInteger getBigIntegerValue() {
		return bigIntegerValue;
	}
	public void setBigIntegerValue(BigInteger bigIntegerValue) {
		this.bigIntegerValue = bigIntegerValue;
	}
	public BigDecimal getBigDecimalValue() {
		return bigDecimalValue;
	}
	public void setBigDecimalValue(BigDecimal bigDecimalValue) {
		this.bigDecimalValue = bigDecimalValue;
	}
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public URL getUrl() {
        return url;
    }
    public void setUrl(URL url) {
        this.url = url;
    }
    public Calendar getCalendar() {
        return calendar;
    }
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
    public TypeEnum getEnum1() {
        return enum1;
    }
    public void setEnum1(TypeEnum enum1) {
        this.enum1 = enum1;
    }
    public TypeEnum getEnum2() {
        return enum2;
    }
    public void setEnum2(TypeEnum enum2) {
        this.enum2 = enum2;
    }
}
