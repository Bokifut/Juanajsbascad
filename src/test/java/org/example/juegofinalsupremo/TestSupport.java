package org.example.juegofinalsupremo;

import org.example.juegofinalsupremo.data.Lista;
import org.example.juegofinalsupremo.model.Enemy;
import org.example.juegofinalsupremo.model.GameEngine;
import org.example.juegofinalsupremo.model.GameLog;
import org.example.juegofinalsupremo.model.GameState;
import org.example.juegofinalsupremo.model.Player;
import org.example.juegofinalsupremo.model.Position;
import org.example.juegofinalsupremo.model.Room;

final class TestSupport {
    private TestSupport() {
    }

    static TestContext context(int rows, int columns, Position playerPosition) {
        return context(rows, columns, playerPosition, 20, 5, 1);
    }

    static TestContext context(int rows, int columns, Position playerPosition, int health, int attack, int movement) {
        Room room = new Room("test-room", "Test Room", rows, columns);
        Player player = new Player("Tester", health, attack, movement, playerPosition);
        GameState state = new GameState(room, player, new GameLog());
        return new TestContext(room, player, state, new GameEngine(state));
    }

    static Lista<Position> cells(Position... positions) {
        Lista<Position> cells = new Lista<Position>();
        for (int i = 0; i < positions.length; i++) {
            cells.add(positions[i]);
        }
        return cells;
    }

    static void placeEnemy(TestContext context, Position position, Enemy enemy) {
        context.room.getCell(position).setEnemy(enemy);
    }

    static void placeTrap(TestContext context, Position position, int damage) {
        context.room.getCell(position).setTrapDamage(damage);
    }

    static void placeWall(TestContext context, Position position) {
        context.room.getCell(position).setWall(true);
    }

    static GameState.AreaEffect whiteEffect(Position... positions) {
        return new GameState.AreaEffect(cells(positions), GameState.AreaEffect.WHITE, 500, "ejecutado", 3);
    }

    static final class TestContext {
        final Room room;
        final Player player;
        final GameState state;
        final GameEngine engine;

        TestContext(Room room, Player player, GameState state, GameEngine engine) {
            this.room = room;
            this.player = player;
            this.state = state;
            this.engine = engine;
        }
    }
}
