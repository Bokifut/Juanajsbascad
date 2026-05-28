package org.example.juegofinalsupremo.data;

import org.example.juegofinalsupremo.model.Room;

import java.util.HashMap;
import java.util.Map;

public class RoomGraph {
    private final Lista<RoomNode> nodes = new Lista<RoomNode>();

    public void addRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("La habitacion no puede ser nula");
        }
        if (findNode(room.getId()) != null) {
            throw new IllegalArgumentException("Ya existe la habitacion: " + room.getId());
        }
        nodes.add(new RoomNode(room));
    }

    public void connect(String fromId, String toId) {
        RoomNode from = requireNode(fromId);
        requireNode(toId);
        if (!from.neighbors.contains(toId)) {
            from.neighbors.add(toId);
        }
    }

    public void connectBoth(String firstId, String secondId) {
        connect(firstId, secondId);
        connect(secondId, firstId);
    }

    public Room getRoom(String id) {
        return requireNode(id).room;
    }

    public Map<String, Room> getRooms() {
        Map<String, Room> rooms = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            Room room = nodes.get(i).room;
            rooms.put(room.getId(), room);
        }
        return rooms;
    }

    public boolean contains(String id) {
        return findNode(id) != null;
    }

    public int roomCount() {
        return nodes.size();
    }

    public Lista<String> roomIds() {
        Lista<String> ids = new Lista<String>();
        for (int i = 0; i < nodes.size(); i++) {
            ids.add(nodes.get(i).room.getId());
        }
        return ids;
    }

    public Lista<String> neighborIds(String id) {
        RoomNode node = requireNode(id);
        Lista<String> ids = new Lista<String>();
        for (int i = 0; i < node.neighbors.size(); i++) {
            ids.add(node.neighbors.get(i));
        }
        return ids;
    }

    public int degree(String id) {
        return requireNode(id).neighbors.size();
    }

    public boolean areConnected(String fromId, String toId) {
        return requireNode(fromId).neighbors.contains(toId);
    }

    public int minimumRooms(String startId, String targetId) {
        Lista<String> path = shortestPath(startId, targetId);
        if (path.isEmpty()) {
            return -1;
        }
        return path.size() - 1;
    }

    public Lista<String> shortestPath(String startId, String targetId) {
        Lista<String> path = new Lista<String>();
        if (startId == null || targetId == null || !contains(startId) || !contains(targetId)) {
            return path;
        }
        Cola<RoomDistance> queue = new Cola<RoomDistance>();
        Lista<String> visited = new Lista<String>();
        Lista<String> previousRoom = new Lista<String>();
        Lista<String> previousFrom = new Lista<String>();
        queue.enqueue(new RoomDistance(startId, 0));
        visited.add(startId);

        while (!queue.isEmpty()) {
            RoomDistance current = queue.dequeue();
            if (current.roomId.equals(targetId)) {
                return rebuildPath(startId, targetId, previousRoom, previousFrom);
            }
            RoomNode node = requireNode(current.roomId);
            for (int i = 0; i < node.neighbors.size(); i++) {
                String next = node.neighbors.get(i);
                if (!visited.contains(next)) {
                    visited.add(next);
                    previousRoom.add(next);
                    previousFrom.add(current.roomId);
                    queue.enqueue(new RoomDistance(next, current.distance + 1));
                }
            }
        }
        return path;
    }

    private Lista<String> rebuildPath(String startId, String targetId, Lista<String> previousRoom, Lista<String> previousFrom) {
        Lista<String> reversed = new Lista<String>();
        String current = targetId;
        reversed.add(current);
        while (!current.equals(startId)) {
            int index = previousRoom.indexOf(current);
            if (index < 0) {
                return new Lista<String>();
            }
            current = previousFrom.get(index);
            reversed.add(current);
        }
        Lista<String> path = new Lista<String>();
        for (int i = reversed.size() - 1; i >= 0; i--) {
            path.add(reversed.get(i));
        }
        return path;
    }

    private RoomNode requireNode(String id) {
        RoomNode node = findNode(id);
        if (node == null) {
            throw new IllegalArgumentException("No existe la habitacion: " + id);
        }
        return node;
    }

    private RoomNode findNode(String id) {
        for (int i = 0; i < nodes.size(); i++) {
            RoomNode node = nodes.get(i);
            if (node.room.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    private static class RoomNode {
        private final Room room;
        private final Lista<String> neighbors = new Lista<String>();

        private RoomNode(Room room) {
            this.room = room;
        }
    }

    private static class RoomDistance {
        private final String roomId;
        private final int distance;

        private RoomDistance(String roomId, int distance) {
            this.roomId = roomId;
            this.distance = distance;
        }
    }
}
