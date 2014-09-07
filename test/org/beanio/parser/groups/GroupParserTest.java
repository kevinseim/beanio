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
package org.beanio.parser.groups;

import java.io.*;

import org.beanio.*;
import org.beanio.parser.ParserTest;
import org.junit.*;

/**
 * JUnit test cases for testing record sequencing and groups.
 * 
 * @author Kevin Seim
 * @since 1.0
 */
public class GroupParserTest extends ParserTest {

    private StreamFactory factory;

    @Before
    public void setup() throws Exception {
        factory = newStreamFactory("group.xml");
    }

    @Test
    public void testEmptyFile() {
        test("g1", "g1_empty.txt");
    }

    @Test
    public void testMissingOptionalGroup() {
        test("g1", "g1_nobatch.txt");
    }

    @Test(expected = UnexpectedRecordException.class)
    public void testMissingTrailer() {
        test("g1", "g1_noTrailer.txt", 2);
    }

    @Test(expected = UnexpectedRecordException.class)
    public void testMissingHeader() {
        test("g1", "g1_noHeader.txt", 1);
    }

    @Test(expected = UnexpectedRecordException.class)
    public void testTooManyRecords() {
        test("g1", "g1_tooManyHeaders.txt", 2);
    }

    @Test
    public void testOneBatch() {
        test("g1", "g1_oneBatch.txt");
    }

    @Test(expected = UnexpectedRecordException.class)
    public void testTooManyGroups() {
        test("g1", "g1_threeBatch.txt", 7);
    }

    @Test(expected = UnexpectedRecordException.class)
    public void testIncompleteGroups() {
        test("g1", "g1_missingBatchTrailer.txt", 12);
    }

    @Test(expected = UnidentifiedRecordException.class)
    public void testUnidentifiedRecord() {
        test("g1", "g1_unidentifiedRecord.txt", 8);
    }

    @Test
    public void testOrderChoice1() {
        test("g2", "g2_valid1.txt");
    }

    @Test(expected = UnexpectedRecordException.class)
    public void testTooManyGroups2() {
        test("g2", "g2_tooManyGroups.txt", 15);
    }

    @Test(expected = UnexpectedRecordException.class)
    public void testMissingGroup() {
        test("g2", "g2_missingGroup.txt", 11);
    }

    @Test(expected = UnexpectedRecordException.class)
    public void testStreamMinOccurs() {
        test("g2", "g2_empty.txt", -1);
    }

    @Test
    public void testStreamMaxOccurs() {
        test("g2", "g2_twoLayout.txt");
    }

    @Test
    public void testUnorderedMaxOccurs() {
        test("g3", "g3_valid.txt");
    }

    @Test
    public void testRepeatingGroupWithOptionalHeader() {
        test("g4", "g4_repeatingGroup.txt");
    }

    /**
     * Tests the final return null in GroupNode.matchNext.
     */
    @Test
    public void testEndOfGroup() {
        test("g4", "g4_endOfGroup.txt");
    }
    
    @Test(expected = UnexpectedRecordException.class)
    public void testTooManyGroupsWithOptionalRecords() {
        test("g4", "g4_tooManyGroups.txt", 8);
    }

    @Test
    public void testSubgroups() {
        test("g5", "g5_subgroups.txt");
    }
    
    @Test(expected = UnexpectedRecordException.class)
    public void testMissingRecordEOF() {
        test("g6", "g6_missingRecordEOF.txt");
    }

    @Test(expected = UnexpectedRecordException.class)
    public void testMissingGroupEOF() {
        test("g7", "g7_missingGroupEOF.txt");
    }

    @Test
    public void testRepeatingStream() {
        test("g8", "g8_repeatingStream.txt");
    }
    
    /**
     * Fully parses the given file.
     * @param name the name of the stream
     * @param filename the name of the file to test
     */
    protected void test(String name, String filename) {
        test(name, filename, -1);
    }

    /**
     * Fully parses the given file.
     * @param name the name of the stream
     * @param filename the name of the file to test
     * @param errorLineNumber
     */
    protected void test(String name, String filename, int errorLineNumber) {
        BeanReader in = factory.createReader(name, new InputStreamReader(
            getClass().getResourceAsStream(filename)));
        try {
            while (in.read() != null);
        }
        catch (BeanReaderException ex) {
            if (errorLineNumber > 0) {
                // assert the line number from the exception matches expected
                Assert.assertEquals(errorLineNumber, ex.getRecordContext().getLineNumber());
            }
            throw ex;
        }
        finally {
            in.close();
        }
    }
}
