package org.example.juegofinalsupremo.data;

import org.example.juegofinalsupremo.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomGraphTest {

    private RoomGraph graph;
    private Room roomA;
    private Room roomB;
    private Room roomC;
    private Room roomD;

    @BeforeEach
    void setUp() {
        graph = new RoomGraph();
        roomA = new Room("A", "Room A", 5, 5);
        roomB = new Room("B", "Room B", 5, 5);
        roomC = new Room("C", "Room C", 5, 5);
        roomD = new Room("D", "Room D", 5, 5);
    }

    @Test
    void addRoom() {
        graph.addRoom(roomA);
        assertEquals(1, graph.roomCount());
        assertTrue(graph.contains("A"));

        assertThrows(IllegalArgumentException.class, () -> graph.addRoom(null));
        assertThrows(IllegalArgumentException.class, () -> graph.addRoom(roomA));
    }

    @Test
    void connect() {
        graph.addRoom(roomA);
        graph.addRoom(roomB);

        graph.connect("A", "B");
        graph.connect("A", "B");
        assertTrue(graph.areConnected("A", "B"));
        assertFalse(graph.areConnected("B", "A"));
    }

    @Test
    void connectBoth() {
        graph.addRoom(roomA);
        graph.addRoom(roomB);

        graph.connectBoth("A", "B");
        graph.connectBoth("A", "B");
        assertTrue(graph.areConnected("A", "B"));
        assertTrue(graph.areConnected("B", "A"));
    }

    @Test
    void getRoom() {
        graph.addRoom(roomA);
        assertEquals(roomA, graph.getRoom("A"));

        assertThrows(IllegalArgumentException.class, () -> graph.getRoom("NonExistent"));
    }

    @Test
    void contains() {
        assertFalse(graph.contains("A"));
        graph.addRoom(roomA);
        assertTrue(graph.contains("A"));
        assertFalse(graph.contains(null));
    }

    @Test
    void roomCount() {
        assertEquals(0, graph.roomCount());
        graph.addRoom(roomA);
        assertEquals(1, graph.roomCount());
        graph.addRoom(roomB);
        assertEquals(2, graph.roomCount());
    }

    @Test
    void roomIds() {
        assertTrue(graph.roomIds().isEmpty());

        graph.addRoom(roomA);
        graph.addRoom(roomB);

        Lista<String> ids = graph.roomIds();
        assertEquals(2, ids.size());
        assertTrue(ids.contains("A"));
        assertTrue(ids.contains("B"));
    }

    @Test
    void neighborIds() {
        graph.addRoom(roomA);
        graph.addRoom(roomB);
        graph.addRoom(roomC);

        graph.connect("A", "B");
        graph.connect("A", "C");

        Lista<String> neighborsA = graph.neighborIds("A");
        assertEquals(2, neighborsA.size());
        assertTrue(neighborsA.contains("B"));
        assertTrue(neighborsA.contains("C"));

        Lista<String> neighborsB = graph.neighborIds("B");
        assertTrue(neighborsB.isEmpty());
    }

    @Test
    void degree() {
        graph.addRoom(roomA);
        graph.addRoom(roomB);

        assertEquals(0, graph.degree("A"));

        graph.connect("A", "B");
        assertEquals(1, graph.degree("A"));
        assertEquals(0, graph.degree("B"));
    }

    @Test
    void areConnected() {
        graph.addRoom(roomA);
        graph.addRoom(roomB);

        assertFalse(graph.areConnected("A", "B"));
        assertFalse(graph.areConnected("A", "NonExistent"));

        assertThrows(IllegalArgumentException.class, () -> graph.areConnected("NonExistent", "B"));

        graph.connect("A", "B");
        assertTrue(graph.areConnected("A", "B"));
        assertFalse(graph.areConnected("B", "A"));
    }

    @Test
    void minimumRooms() {
        graph.addRoom(roomA);
        graph.addRoom(roomB);
        graph.addRoom(roomC);
        graph.addRoom(roomD);

        graph.connect("A", "B");
        graph.connect("A", "C");
        graph.connect("B", "D");
        graph.connect("C", "D");

        assertEquals(2, graph.minimumRooms("A", "D"));
        assertEquals(1, graph.minimumRooms("A", "B"));
        assertEquals(0, graph.minimumRooms("A", "A"));
        assertEquals(-1, graph.minimumRooms("D", "A"));
        assertEquals(-1, graph.minimumRooms("NonExistent", "A"));
        assertEquals(-1, graph.minimumRooms("A", "NonExistent"));
    }

    @Test
    void shortestPath() {
        graph.addRoom(roomA);
        graph.addRoom(roomB);
        graph.addRoom(roomC);
        graph.addRoom(roomD);

        assertTrue(graph.shortestPath(null, "A").isEmpty());
        assertTrue(graph.shortestPath("A", null).isEmpty());
        assertTrue(graph.shortestPath("NonExistent", "A").isEmpty());
        assertTrue(graph.shortestPath("A", "NonExistent").isEmpty());
        assertTrue(graph.shortestPath(null, null).isEmpty());

        assertTrue(graph.shortestPath("A", "B").isEmpty());

        graph.connect("A", "B");
        graph.connect("A", "C");
        graph.connect("B", "D");
        graph.connect("C", "D");

        Lista<String> path = graph.shortestPath("A", "D");
        assertFalse(path.isEmpty());
        assertEquals("A", path.get(0));
        assertEquals("D", path.get(path.size() - 1));

        Lista<String> samePath = graph.shortestPath("A", "A");
        assertEquals(1, samePath.size());
        assertEquals("A", samePath.get(0));

        assertTrue(graph.shortestPath("D", "A").isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRebuildPathDefensiveBranch() throws Exception {
        graph.addRoom(roomA);
        graph.addRoom(roomB);

        java.lang.reflect.Method rebuildMethod = RoomGraph.class.getDeclaredMethod(
                "rebuildPath", String.class, String.class, Lista.class, Lista.class);
        rebuildMethod.setAccessible(true);

        Lista<String> prevRoom = new Lista<>();
        Lista<String> prevFrom = new Lista<>();

        Lista<String> result = (Lista<String>) rebuildMethod.invoke(graph, "A", "B", prevRoom, prevFrom);

        assertTrue(result.isEmpty());
    }
}