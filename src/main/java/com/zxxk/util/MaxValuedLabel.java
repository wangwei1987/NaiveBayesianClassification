package com.zxxk.util;

/**
 * Created by shiti on 17-4-21.
 */
public class MaxValuedLabel {

    private int index;

    private boolean multi;

    public MaxValuedLabel(int index, boolean multi) {
        this.index = index;
        this.multi = multi;
    }

    public MaxValuedLabel() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }
}
