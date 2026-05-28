package org.example.juegofinalsupremo.model;

public class Position {
    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Position translate(Direction direction) {
        return new Position(row + direction.getDeltaRow(), column + direction.getDeltaColumn());
    }

    public boolean equals(Object other) {
        if (!(other instanceof Position)) {
            return false;
        }
        Position position = (Position) other;
        return row == position.row && column == position.column;
    }

    public int hashCode() {
        return row * 31 + column;
    }

    public String toString() {
        return "(" + row + "," + column + ")";
    }
}
