package org.example.juegofinalsupremo.model;

import org.example.juegofinalsupremo.data.Lista;
import org.example.juegofinalsupremo.data.RoomGraph;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void testConstructors() {
        Room r1 = new Room("r1", "R1", 5, 5);
        Player p = new Player("P", CharacterClass.GUERRERO);
        GameLog log = new GameLog();

        GameState state1 = new GameState(r1, p, log);
        assertEquals("r1", state1.getCurrentRoomId());
        assertEquals("r1", state1.getFinalRoomId());

        RoomGraph graph = new RoomGraph();
        graph.addRoom(r1);
        Room r2 = new Room("r2", "R2", 5, 5);
        graph.addRoom(r2);

        GameState state2 = new GameState(graph, "r1", "r2", p, log);
        assertEquals("r1", state2.getCurrentRoomId());
        assertEquals("r2", state2.getFinalRoomId());

        assertThrows(IllegalArgumentException.class, () -> new GameState(null, "r1", "r2", p, log));
        assertThrows(IllegalArgumentException.class, () -> new GameState(graph, "nonexistent", "r2", p, log));
        assertThrows(IllegalArgumentException.class, () -> new GameState(graph, "r1", "nonexistent", p, log));
    }

    @Test
    void testInitLog() {
        Room r1 = new Room("r1", "R1", 5, 5);
        Player p = new Player("P", CharacterClass.GUERRERO);
        GameState state = new GameState(r1, p, null);
        assertNull(state.getLog());

        state.initLog();
        assertNotNull(state.getLog());

        state.initLog();
        assertNotNull(state.getLog());
    }

    @Test
    void testChangeRoom() {
        RoomGraph graph = new RoomGraph();
        Room r1 = new Room("r1", "R1", 5, 5);
        Room r2 = new Room("r2", "R2", 5, 5);
        graph.addRoom(r1);
        graph.addRoom(r2);
        Player p = new Player("P", CharacterClass.GUERRERO);
        GameState state = new GameState(graph, "r1", "r2", p, new GameLog());

        assertThrows(IllegalArgumentException.class, () -> state.changeRoom("r3", new Position(0, 0)));

        state.changeRoom("r2", new Position(1, 1));
        assertEquals("r2", state.getCurrentRoomId());
        assertEquals(new Position(1, 1), p.getPosition());
        assertEquals(r2, state.getRoom());

        Map<String, Room> rooms = state.getRooms();
        assertTrue(rooms.containsKey("r1"));
        assertTrue(rooms.containsKey("r2"));
        assertEquals(graph, state.getRoomGraph());
        assertEquals(p, state.getPlayer());
    }

    @Test
    void testCoinsAndDifficulty() {
        Room r1 = new Room("r1", "R1", 5, 5);
        Player p = new Player("P", CharacterClass.GUERRERO);
        GameState state = new GameState(r1, p, new GameLog());

        assertEquals(0, state.getCoins());
        state.setCoins(10);
        assertEquals(10, state.getCoins());
        state.addCoins(5);
        assertEquals(15, state.getCoins());
        state.addCoins(-20);
        assertEquals(0, state.getCoins());

        state.setCoins(20);
        assertFalse(state.spendCoins(-5));
        assertFalse(state.spendCoins(25));
        assertTrue(state.spendCoins(10));
        assertEquals(10, state.getCoins());

        state.setDifficultyIndex(-1);
        assertEquals(0, state.getDifficultyIndex());
        state.setDifficultyIndex(5);
        assertEquals(3, state.getDifficultyIndex());
        state.setDifficultyIndex(2);
        assertEquals(2, state.getDifficultyIndex());
    }

    @Test
    void testRuntimeStateAndFlags() {
        Room r1 = new Room("r1", "R1", 5, 5);
        Player p = new Player("P", CharacterClass.GUERRERO);
        GameState state = new GameState(r1, p, new GameLog());

        state.setFinalRouteHintBought(true);
        assertTrue(state.isFinalRouteHintBought());

        state.restoreRuntimeState(10, 5, false, true, true, -5, 5, -2);
        assertEquals(10, state.getRemainingTurns());
        assertEquals(5, state.getMovementRemaining());
        assertFalse(state.isActionAvailable());
        assertTrue(state.isFinished());
        assertTrue(state.isWon());
        assertEquals(0, state.getCoins());
        assertEquals(3, state.getDifficultyIndex());
        assertEquals(0, state.getAviolentadoTurns());
    }

    @Test
    void testMovementAndAction() {
        Room r1 = new Room("r1", "R1", 5, 5);
        Player p = new Player("P", CharacterClass.GUERRERO);
        GameState state = new GameState(r1, p, new GameLog());

        int initialMovement = state.getMovementRemaining();
        state.consumeMovement();
        assertTrue(state.getMovementRemaining() < initialMovement || initialMovement == 0);

        while (state.getMovementRemaining() > 0) {
            state.consumeMovement();
        }
        state.consumeMovement();
        assertEquals(0, state.getMovementRemaining());

        assertTrue(state.isActionAvailable());
        state.consumeAction();
        assertFalse(state.isActionAvailable());
    }

    @Test
    void testNextPlayerTurn() {
        Room r1 = new Room("r1", "R1", 2, 2);
        r1.getCell(new Position(0,0)).setWall(true);
        Enemy enemy = new Enemy("e1", "E1", 1, 10, 2);
        enemy.setImmunePlayerTurns(2);
        r1.getCell(new Position(1,1)).setEnemy(enemy);

        Player p = new Player("P", CharacterClass.GUERRERO);
        GameState state = new GameState(r1, p, new GameLog());
        state.initLog();

        state.setAviolentadoTurns(2);
        assertEquals(2, state.getAviolentadoTurns());
        state.setAviolentadoTurns(1);
        assertEquals(2, state.getAviolentadoTurns());
        state.setAviolentadoTurns(-1);
        assertEquals(2, state.getAviolentadoTurns());

        assertEquals(3, state.getEffectiveDefensePenalty());

        int initialTurns = state.getRemainingTurns();
        state.startNextPlayerTurn();
        assertEquals(initialTurns - 1, state.getRemainingTurns());
        assertEquals(1, state.getAviolentadoTurns());
        assertEquals(1, enemy.getImmunePlayerTurns());

        state.startNextPlayerTurn();
        assertEquals(0, state.getAviolentadoTurns());
        assertEquals(0, state.getEffectiveDefensePenalty());
        assertEquals(0, enemy.getImmunePlayerTurns());

        state.restoreRuntimeState(0, 0, false, false, false, 0, 0, 0);
        state.startNextPlayerTurn();
        assertTrue(state.isFinished());
        assertFalse(state.isWon());

        state.restoreRuntimeState(0, 0, false, true, false, 0, 0, 0);
        state.startNextPlayerTurn();
        assertTrue(state.isFinished());
    }

    @Test
    void testAreaEffects() {
        Room r1 = new Room("r1", "R1", 5, 5);
        Player p = new Player("P", CharacterClass.GUERRERO);
        GameState state = new GameState(r1, p, new GameLog());

        Lista<Position> cells = new Lista<>();
        cells.add(new Position(1, 1));
        GameState.AreaEffect effect = new GameState.AreaEffect(cells, GameState.AreaEffect.RED, 10, "Msg", 2);

        assertEquals(cells, effect.getCells());
        assertEquals(GameState.AreaEffect.RED, effect.getColor());
        assertEquals(10, effect.getDamage());
        assertEquals("Msg", effect.getMessage());
        assertEquals(2, effect.getPlayerTurnsRemaining());
        assertFalse(effect.isTriggered());
        assertTrue(effect.contains(new Position(1, 1)));
        assertFalse(effect.contains(new Position(0, 0)));

        state.addAreaEffect(effect);
        assertEquals(1, state.getAreaEffects().size());

        effect.tick();
        assertEquals(1, effect.getPlayerTurnsRemaining());
        assertFalse(effect.isExpired());

        effect.tick();
        assertEquals(0, effect.getPlayerTurnsRemaining());
        assertTrue(effect.isExpired());

        effect.tick();
        assertEquals(0, effect.getPlayerTurnsRemaining());

        state.clearExpiredAreaEffects();
        assertEquals(0, state.getAreaEffects().size());

        GameState.AreaEffect effect2 = new GameState.AreaEffect(cells, GameState.AreaEffect.YELLOW, 5, "Msg2", 2);
        state.addAreaEffect(effect2);
        effect2.trigger();
        assertTrue(effect2.isTriggered());
        assertTrue(effect2.isExpired());

        state.addAreaEffect(new GameState.AreaEffect(cells, GameState.AreaEffect.WHITE, 0, "Wait", 5));

        state.clearExpiredAreaEffects();
        assertEquals(1, state.getAreaEffects().size());
    }

    @Test
    void testFinish() {
        Room r1 = new Room("r1", "R1", 5, 5);
        Player p = new Player("P", CharacterClass.GUERRERO);
        GameState state = new GameState(r1, p, new GameLog());

        state.finish(true);
        assertTrue(state.isFinished());
        assertTrue(state.isWon());
    }
}