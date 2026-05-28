package org.example.juegofinalsupremo.data;

import org.example.juegofinalsupremo.model.Direction;
import org.example.juegofinalsupremo.model.Position;
import org.example.juegofinalsupremo.model.Room;

public class GridGraph {
    private final Room room;

    public GridGraph(Room room) {
        this.room = room;
    }

    public Lista<Position> reachable(Position start, int maxSteps) {
        Lista<Position> reached = new Lista<Position>();
        Cola<NodeDistance> queue = new Cola<NodeDistance>();
        boolean[][] visited = new boolean[room.getRows()][room.getColumns()];

        if (!room.isValid(start) || maxSteps < 0) {
            return reached;
        }

        queue.enqueue(new NodeDistance(start, 0));
        visited[start.getRow()][start.getColumn()] = true;

        while (!queue.isEmpty()) {
            NodeDistance current = queue.dequeue();
            reached.add(current.position);
            if (current.distance == maxSteps) {
                continue;
            }
            Direction[] directions = Direction.values();
            for (int i = 0; i < directions.length; i++) {
                Position next = current.position.translate(directions[i]);
                if (room.isValid(next)
                        && !visited[next.getRow()][next.getColumn()]
                        && room.getCell(next).isWalkable()) {
                    visited[next.getRow()][next.getColumn()] = true;
                    queue.enqueue(new NodeDistance(next, current.distance + 1));
                }
            }
        }
        return reached;
    }

    private static class NodeDistance {
        private final Position position;
        private final int distance;

        private NodeDistance(Position position, int distance) {
            this.position = position;
            this.distance = distance;
        }
    }
}
