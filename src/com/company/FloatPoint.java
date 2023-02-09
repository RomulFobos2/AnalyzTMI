package com.company;

public class FloatPoint {
    public float x, y;

    public FloatPoint(Float x, Float y) {
        this.x = x;
        this.y = y;
    }

    public String toString(Float x, Float y) {
        return "Время: " + String.valueOf(this.x - x) + "; Значение: " + String.valueOf(this.y - y);
    }
}


