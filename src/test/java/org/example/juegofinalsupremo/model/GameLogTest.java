package org.example.juegofinalsupremo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameLogTest {

    @Test
    void add() {
        GameLog log = new GameLog();
        log.add("Player 1 moved to (1,1)");
        assertEquals(1, log.getEntries().size());
        assertEquals("Player 1 moved to (1,1)", log.getEntries().get(0));
    }

    @Test
    void getEntries() {
        GameLog log = new GameLog();
        log.add("Entry 1");
        log.add("Entry 2");
        assertNotNull(log.getEntries());
        assertEquals(2, log.getEntries().size());
    }

    @Test
    void asText() {
        GameLog log = new GameLog();
        log.add("Start");
        log.add("End");
        String expected = "1. Start\n2. End";
        assertEquals(expected, log.asText());
    }
}
