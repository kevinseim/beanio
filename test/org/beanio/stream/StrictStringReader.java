package org.beanio.stream;

import java.io.*;

/**
 * A StringReader that throw an IOException if another read is made
 * after the EOF is reported once.
 * @author Kevin Seim
 */
public class StrictStringReader extends Reader {
    private int pos = 0;
    private char[] c;
    
    /**
     * Constructs a new StrictStringReader.
     * @param input
     */
    public StrictStringReader(String input) {
        c = input.toCharArray();
    }
    
    @Override
    public int read() throws IOException {
        return internalRead();
    }
    
    private int internalRead() throws IOException {
        if (pos == -1) {
            throw new IOException("Stream is closed");
        }
        if (pos < c.length) {
            return c[pos++];
        }
        pos = -1;
        return pos;
    }

    @Override
    public void close() throws IOException {
        
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        throw new UnsupportedOperationException();
    }
}
