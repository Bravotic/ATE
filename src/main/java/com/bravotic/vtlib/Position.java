package com.bravotic.vtlib;

public class Position {
    public int row;
    public int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public String toString() {
        return "[Row: " + row + "; Column: " + column + "]";
    }
}