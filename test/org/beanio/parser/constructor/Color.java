package org.beanio.parser.constructor;

public class Color {

    private String name;
    private int r;
    private int g;
    private int b;
    
    public Color() {
        this("black", 0, 0, 0);
    }

    public Color(String name, int r, int g, int b) {
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public String getName() {
        return this.name;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }
}
