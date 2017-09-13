/*
 * Copyright 2011-2012 Kevin Seim
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
package org.beanio.internal.parser.message;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.beanio.internal.parser.MessageFactory;

/**
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class ResourceBundleMessageFactory implements MessageFactory {
    
    /* resource bundle key prefixes */
    private static final String LABEL_MESSAGE_PREFIX = "label";
    private static final String FIELD_ERROR_MESSAGE_PREFIX = "fielderror";
    private static final String RECORD_ERROR_MESSAGE_PREFIX = "recorderror";
    
    /* configured resource bundle for messages */
    private ResourceBundle resourceBundle;
    /* default resource bundle for messages based on the stream format */
    private ResourceBundle defaultResourceBundle;
    /* cache messages from resource bundles */
    private ConcurrentHashMap<String, String> messageCache = new ConcurrentHashMap<>();
    /* used to flag cache misses */
    private static final String NOT_FOUND = new String();
    
    public ResourceBundleMessageFactory() { }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.MessageContext#getRecordLabel(java.lang.String)
     */
    @Override
    public String getRecordLabel(String recordName) {
        return getLabel(LABEL_MESSAGE_PREFIX + "." + recordName);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.parser.MessageContext#getFieldLabel(java.lang.String, java.lang.String)
     */
    @Override
    public String getFieldLabel(String recordName, String fieldName) {
        return getLabel(LABEL_MESSAGE_PREFIX + "." + recordName + "." + fieldName);
    }

    /**
     * Returns a label from the configured resource bundle.
     * @param key the resource bundle key
     * @return the label, or null if not found
     */
    protected String getLabel(String key) {
        String label = messageCache.get(key);
        if (label != null) {
            return label == NOT_FOUND ? null : label;
        }

        if (resourceBundle != null) {
            label = getMessage(resourceBundle, key);
        }

        if (label == null) {
            messageCache.putIfAbsent(key, NOT_FOUND);
            return null;
        }

        messageCache.putIfAbsent(key, label);
        return label;
    }

    /*
     * (non-Javadoc)
     * @see org.bio.context.MessageContext#getFieldErrorMessage(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getFieldErrorMessage(String recordName, String fieldName, String rule) {
        String key = FIELD_ERROR_MESSAGE_PREFIX + "." + recordName + "." + fieldName + "." + rule;

        String message = messageCache.get(key);
        if (message != null) {
            return message == NOT_FOUND ? key : message;
        }

        String k = key;
        if (resourceBundle != null) {
            message = getMessage(resourceBundle, k);
            if (message == null) {
                k = FIELD_ERROR_MESSAGE_PREFIX + "." + recordName + "." + rule;
                message = getMessage(resourceBundle, k);
                if (message == null) {
                    k = FIELD_ERROR_MESSAGE_PREFIX + "." + rule;
                    message = getMessage(resourceBundle, k);
                }
            }
        }

        if (message == null && defaultResourceBundle != null) {
            message = getMessage(defaultResourceBundle, FIELD_ERROR_MESSAGE_PREFIX + "." + rule);
        }

        if (message == null) {
            messageCache.putIfAbsent(key, NOT_FOUND);
            return key;
        }
        else {
            messageCache.putIfAbsent(key, message);
            return message;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.bio.context.MessageContext#getRecordErrorMessage(java.lang.String, java.lang.String)
     */
    @Override
    public String getRecordErrorMessage(String recordName, String rule) {
        String key = RECORD_ERROR_MESSAGE_PREFIX + "." + recordName + "." + rule;

        String message = messageCache.get(key);
        if (message != null) {
            return message == NOT_FOUND ? key : message;
        }

        if (resourceBundle != null) {
            message = getMessage(resourceBundle, key);
            if (message == null) {
                message = getMessage(resourceBundle, RECORD_ERROR_MESSAGE_PREFIX + "." + rule);
            }
        }

        if (message == null && defaultResourceBundle != null) {
            message = getMessage(defaultResourceBundle, RECORD_ERROR_MESSAGE_PREFIX + "." + rule);
        }

        if (message == null) {
            messageCache.putIfAbsent(key, NOT_FOUND);
            return key;
        }
        else {
            messageCache.putIfAbsent(key, message);
            return message;
        }
    }

    /**
     * Returns a message from a resource bundle.
     * @param bundle the resource bundle to check
     * @param key the resource bundle key for the message
     * @return the message or <tt>null</tt> if not found
     */
    private String getMessage(ResourceBundle bundle, String key) {
        try {
            return bundle.getString(key);
        }
        catch (MissingResourceException ex) {
            return null;
        }
    }

    /**
     * Sets the primary resource bundle to check for messages.
     * @param resourceBundle the resource bundle
     */
    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    /**
     * Sets the default resource bundle to check of messages not found in the
     * primary resource bundle.
     * @param defaultResourceBundle the default resource bundle
     */
    public void setDefaultResourceBundle(ResourceBundle defaultResourceBundle) {
        this.defaultResourceBundle = defaultResourceBundle;
    }
}
