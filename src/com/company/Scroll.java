package com.company;

import javax.swing.*;

class Scroll extends JScrollBar {
    public Scroll(int minimum, int maximum, int orientation) {
        super(orientation);
        this.setMaximum(maximum);
        this.setMinimum(minimum);
    }

    public void setMinMax(int minimum, int maximum) {
        this.setMinimum(minimum);
        this.setMaximum(maximum);
    }

    public int getMValue() {
        return -this.getValue() + this.getMaximum();
    }
}
