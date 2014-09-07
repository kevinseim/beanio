/*
 * Copyright 2010-2012 Kevin Seim
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
package org.beanio.parser.validation;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for testing field level validations.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class FieldValidationTest extends ParserTest {

	private StreamFactory factory;
	private int lineNumber;

	@Before
	public void setup() throws Exception {
		factory = newStreamFactory("validation.xml");
		lineNumber = 0;
	}

	@Test
	public void testFieldValidation() throws Exception {
		BeanReader in = factory.createReader("v1", new InputStreamReader(
				getClass().getResourceAsStream("v1.txt")));
		try {
			testValid(in, "regex", "12345");
			testInvalid(in, "regex", "abc", "regex('\\d+') at line 2");
			testValid(in, "minLength", "ab");
			testInvalid(in, "minLength", "a", "minLength(2) at line 4");
			testValid(in, "maxLength", "abcde");
			testInvalid(in, "maxLength", "abcdef", "maxLength(5) at line 6");
			testValid(in, "requiredWithTrim", "value");
			testInvalid(in, "requiredWithTrim", "     ", "required at line 8");
			testValid(in, "typeHandler", new SimpleDateFormat("MMddyy").parse("010170"));
			testInvalid(in, "typeHandler", "010170a", "type at line 10");
			testValid(in, "requiredWithoutTrim", " ");
			testInvalid(in, "requiredWithoutTrim", "", "required at line 12");
			testValid(in, "literal", "value");
			testInvalid(in, "literal", "other", 
				"Invalid Literal Field at line 14 on Literal Record, expected 'value'");
		} finally {
			in.close();
		}
	}

	@SuppressWarnings("rawtypes")
	private void testValid(BeanReader in, String recordName, Object expected) {
		++lineNumber;
		Map record = (Map) in.read();
		assertEquals(expected, record.get("field"));
		assertEquals(recordName, in.getRecordName());
		assertEquals(lineNumber, in.getLineNumber());
	}

	private void testInvalid(BeanReader in, String recordName,
		String fieldText, String message) {
		try {
			++lineNumber;
			in.read();
			fail("Record should not have passed validation");
		} catch (InvalidRecordException ex) {
			assertEquals(recordName, in.getRecordName());
			assertEquals(lineNumber, in.getLineNumber());

			RecordContext ctx = ex.getRecordContext();
			assertEquals(recordName, ctx.getRecordName());
			assertEquals(lineNumber, ctx.getLineNumber());
			assertEquals(fieldText, ctx.getFieldText("field"));
			for (String s : ctx.getFieldErrors("field")) {
				assertEquals(message, s);
			}
		}
	}
}
