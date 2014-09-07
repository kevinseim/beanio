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
package org.beanio.types;

import java.util.Locale;

import org.beanio.internal.util.LocaleUtil;

/**
 * Base class for {@link Locale} aware type handlers.
 * @author Kevin Seim
 * @since 2.1.0
 */
public abstract class LocaleSupport {

    protected Locale locale;
    
    /**
     * Constructs a new LocaleSupport.
     */
    public LocaleSupport() {
        locale = LocaleUtil.getDefaultLocale();
    }
    
    /**
     * Returns the configured locale, or the default if not 
     * explicitly configured.
     * @return the configured locale
     */
    public String getLocale() {
        return locale.toString();
    }
    
    /**
     * Sets the locale.
     * @param localeString the locale (e.g. en_US)
     */
    public void setLocale(String localeString) {
        locale = LocaleUtil.parseLocale(localeString);
        if (locale == null) {
            locale = LocaleUtil.getDefaultLocale();
        }
    }
}
