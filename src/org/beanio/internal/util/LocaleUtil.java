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
package org.beanio.internal.util;

import java.util.Locale;

/**
 * Utility methods for handling Locale.
 * @author Kevin Seim
 * @since 2.1.0
 */
public class LocaleUtil {

    private static Locale defaultLocale;
    
    public static Locale parseLocale(String name) {
        if (name == null || "".equals(name)) {
            return null;
        }
        String[] args = name.split("_");
        if (args.length == 1) {
            return new Locale(args[0]);
        }
        else if (args.length == 2) {
            return new Locale(args[0], args[1]);
        }
        else {
            return new Locale(args[0], args[1], args[2]);
        }
    }
    
    public static Locale getDefaultLocale() {
        if (defaultLocale != null) {
            return defaultLocale;
        }
        
        synchronized (LocaleUtil.class) {
            if (defaultLocale == null) {
                String name = Settings.getInstance().getProperty(Settings.DEFAULT_LOCALE);
                try {
                    defaultLocale = parseLocale(name);
                }
                catch (IllegalArgumentException ex) { }
                
                if (defaultLocale == null) {
                    defaultLocale = Locale.getDefault();
                }
            }
            return defaultLocale;
        }
    }
}
