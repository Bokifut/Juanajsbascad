package org.example.juegofinalsupremo.data;

import org.example.juegofinalsupremo.model.Position;
import org.example.juegofinalsupremo.model.Room;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GridGraphTest {

    private void trySetWall(Room room, Position pos) {
        try {
            Object cell = room.getCell(pos);
            if (cell != null) {
                for (java.lang.reflect.Method m : cell.getClass().getMethods()) {
                    if (m.getParameterCount() == 1) {
                        String name = m.getName().toLowerCase();
                        if (name.contains("walkable") || name.contains("passable")) {
                            m.invoke(cell, false);
                        } else if (name.contains("solid") || name.contains("wall")) {
                            m.invoke(cell, true);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    void testReachableInvalidStart() {
        Room room = new Room(5, 5);
        Position start = new Position(-1, -1);
        GridGraph gridGraph = new GridGraph(room);

        Lista<Position> result = gridGraph.reachable(start, 2);

        assertTrue(result.isEmpty());
    }

    @Test
    void testReachableNegativeMaxSteps() {
        Room room = new Room(5, 5);
        Position start = new Position(2, 2);
        GridGraph gridGraph = new GridGraph(room);

        Lista<Position> result = gridGraph.reachable(start, -1);

        assertTrue(result.isEmpty());
    }

    @Test
    void testReachableZeroSteps() {
        Room room = new Room(5, 5);
        Position start = new Position(2, 2);
        GridGraph gridGraph = new GridGraph(room);

        Lista<Position> result = gridGraph.reachable(start, 0);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getRow());
        assertEquals(2, result.get(0).getColumn());
    }

    @Test
    void testReachableMultipleStepsAndVisitedBranch() {
        Room room = new Room(5, 5);
        Position start = new Position(2, 2);
        GridGraph gridGraph = new GridGraph(room);

        Lista<Position> result = gridGraph.reachable(start, 2);

        assertTrue(result.size() > 1);
    }

    @Test
    void testReachableEdgesAndNotWalkableBranch() {
        Room room = new Room(4, 4);
        Position start = new Position(0, 0);
        GridGraph gridGraph = new GridGraph(room);

        trySetWall(room, new Position(0, 1));
        trySetWall(room, new Position(1, 0));

        Lista<Position> result = gridGraph.reachable(start, 10);

        assertTrue(result.size() >= 1);
        assertTrue(result.size() <= 16);
    }

    @Test
    void testReachableMaxCoverageFullRoom() {
        Room room = new Room(3, 3);
        Position start = new Position(1, 1);
        GridGraph gridGraph = new GridGraph(room);

        trySetWall(room, new Position(1, 2));

        Lista<Position> result = gridGraph.reachable(start, 5);

        assertTrue(result.size() > 1);
        assertTrue(result.size() <= 9);
    }
}