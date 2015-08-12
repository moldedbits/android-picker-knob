package com.moldedbits.horizontaldialer;

/**
 * Created by shubham on 12/08/15.
 *
 */
public class Line {

    public static final String LARGE_LINE = "large_line";

    public static final String SMALL_LINE = "small_line";

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private int value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public Line(int value) {
        this.value = value;
        type = SMALL_LINE;
    }
}
