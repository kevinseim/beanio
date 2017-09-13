package org.beanio.internal.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

import org.beanio.internal.parser.format.FieldPadding;

/**
 * Utility methods for formatting debug output.
 * @author Kevin Seim
 * @since 2.1.0
 */
public class DebugUtil {

    private DebugUtil() { }
    
    public static String formatRange(int min, int max) {
        if (max == Integer.MAX_VALUE) {
            return min + "+";
        }
        else {
            return min + "-" + max;
        }
    }
    
    public static String formatOption(String option, boolean value) {
        if (value) {
            return option;
        }
        else {
            return "!" + option;
        }
    }
    
    public static String formatPadding(FieldPadding padding) {
        if (padding == null) {
            return "";
        }
        else {
            return ", padded[" +
                "length=" + padding.getLength() +
                ", filler=" + padding.getFiller() + 
                ", align=" + padding.getJustify() +
                "]";
        }
    }
    
    public static String toString(Debuggable c) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        c.debug(new PrintStream(out));
        return new String(out.toByteArray(), StandardCharsets.US_ASCII);
    }
}
