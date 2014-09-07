/*
 * Copyright 2012 Kevin Seim
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
package org.beanio.util;

import java.util.Properties;

import org.junit.Assert;

import org.beanio.internal.util.StringUtil;
import org.junit.Test;

/**
 * JUnit test cases for the {@link StringUtil} class.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class StringUtilTest {

    @Test
    public void testSuccessful() {
        
        Properties props = new Properties();
        props.setProperty("1", "1");
        props.setProperty("2", "2");
        props.setProperty("", "empty");
        props.setProperty(" space ", " ");
        
        String[] s = {
            null, null,
            "", "",
            " no prop ", " no prop ",
            "${1}", "1",
            " ${1} ${2} ", " 1 2 ",
            "${1} ${2", "1 ${2",
            "${}", "empty",
            "${ space }" , " ",
            "-$}", "-$}",
            "${missing,int}", "int",
            "${missing, }", " ",
            "${missing,1}", "1",
            "${missing,}", "",
            "$", "$"
        };
        
        for (int i=0; i<s.length; i+=2) {
            Assert.assertEquals(s[i+1], StringUtil.doPropertySubstitution(s[i], props));
        }
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testMissingProperty() {
        StringUtil.doPropertySubstitution("${missing}", (Properties) null);
    }
}
