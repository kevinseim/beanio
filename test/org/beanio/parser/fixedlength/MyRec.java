package org.beanio.parser.fixedlength;

import java.io.Serializable;

public class MyRec implements Serializable {

    private static final long serialVersionUID = 1L;

    private String string;
    private long num;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyRec myRec = (MyRec) o;

        if (num != myRec.num) return false;
        return string.equals(myRec.string);
    }

    @Override
    public int hashCode() {
        int result = string.hashCode();
        result = 31 * result + (int) (num ^ (num >>> 32));
        return result;
    }
}
