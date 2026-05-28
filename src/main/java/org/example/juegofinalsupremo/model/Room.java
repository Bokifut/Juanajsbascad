package org.example.juegofinalsupremo.model;

public class Room {
    private final String id;
    private final String name;
    private final int rows;
    private final int columns;
    private final Cell[][] cells;

    public Room(int rows, int columns) {
        this("room", "Habitacion", rows, columns);
    }

    public Room(int rows, int columns, String name) {
        this("room", name, rows, columns);
    }

    public Room(String id, String name, int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("La habitacion debe tener dimensiones positivas");
        }
        this.id = id;
        this.name = name;
        this.rows = rows;
        this.columns = columns;
        this.cells = new Cell[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                cells[row][column] = new Cell(new Position(row, column));
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public boolean isValid(Position position) {
        return position != null
                && position.getRow() >= 0
                && position.getRow() < rows
                && position.getColumn() >= 0
                && position.getColumn() < columns;
    }

    public Cell getCell(Position position) {
        if (!isValid(position)) {
            throw new IllegalArgumentException("Celda invalida: " + position);
        }
        return cells[position.getRow()][position.getColumn()];
    }
}
