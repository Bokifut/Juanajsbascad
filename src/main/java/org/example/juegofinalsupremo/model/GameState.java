package org.example.juegofinalsupremo.model;

import org.example.juegofinalsupremo.data.Lista;
import org.example.juegofinalsupremo.data.RoomGraph;

import java.util.Map;

public class GameState {
    private final RoomGraph roomGraph;
    private String currentRoomId;
    private final String finalRoomId;
    private final Player player;
    private transient GameLog log;
    private int remainingTurns = 500;
    private int movementRemaining;
    private boolean actionAvailable = true;
    private boolean finished;
    private boolean won;
    private int coins;
    private int difficultyIndex = 1;
    private int aviolentadoTurns;
    private boolean finalRouteHintBought;
    private final Lista<AreaEffect> areaEffects = new Lista<AreaEffect>();

    public GameState(Room room, Player player, GameLog log) {
        this(singleRoomGraph(room), room.getId(), room.getId(), player, log);
    }

    public GameState(RoomGraph roomGraph, String currentRoomId, String finalRoomId, Player player, GameLog log) {
        if (roomGraph == null || !roomGraph.contains(currentRoomId) || !roomGraph.contains(finalRoomId)) {
            throw new IllegalArgumentException("Grafo de habitaciones invalido");
        }
        this.roomGraph = roomGraph;
        this.currentRoomId = currentRoomId;
        this.finalRoomId = finalRoomId;
        this.player = player;
        this.log = log;
        this.movementRemaining = Math.max(0, player.getMovementPower());
    }

    public void initLog() {
        if (log == null) {
            log = new GameLog();
        }
    }

    public Room getRoom() {
        return roomGraph.getRoom(currentRoomId);
    }

    public Map<String, Room> getRooms() {
        return roomGraph.getRooms();
    }

    public RoomGraph getRoomGraph() {
        return roomGraph;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public String getFinalRoomId() {
        return finalRoomId;
    }

    public void changeRoom(String roomId, Position playerPosition) {
        if (!roomGraph.contains(roomId)) {
            throw new IllegalArgumentException("No existe la habitacion: " + roomId);
        }
        currentRoomId = roomId;
        player.setPosition(playerPosition);
    }

    public Player getPlayer() {
        return player;
    }

    public GameLog getLog() {
        return log;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWon() {
        return won;
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }

    public int getMovementRemaining() {
        return movementRemaining;
    }

    public boolean isActionAvailable() {
        return actionAvailable;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getDifficultyIndex() {
        return difficultyIndex;
    }

    public void setDifficultyIndex(int difficultyIndex) {
        this.difficultyIndex = Math.max(0, Math.min(3, difficultyIndex));
    }

    public void addCoins(int amount) {
        coins = Math.max(0, coins + amount);
    }

    public boolean spendCoins(int amount) {
        if (amount < 0 || coins < amount) {
            return false;
        }
        coins -= amount;
        return true;
    }

    public void restoreRuntimeState(int remainingTurns, int movementRemaining, boolean actionAvailable,
                                    boolean finished, boolean won, int coins, int difficultyIndex,
                                    int aviolentadoTurns) {
        this.remainingTurns = Math.max(0, remainingTurns);
        this.movementRemaining = Math.max(0, movementRemaining);
        this.actionAvailable = actionAvailable;
        this.finished = finished;
        this.won = won;
        this.coins = Math.max(0, coins);
        setDifficultyIndex(difficultyIndex);
        this.aviolentadoTurns = Math.max(0, aviolentadoTurns);
    }

    public void consumeMovement() {
        if (movementRemaining > 0) {
            movementRemaining--;
        }
    }

    public void consumeAction() {
        actionAvailable = false;
    }

    public void startNextPlayerTurn() {
        tickPlayerDebuffs();
        tickEnemyImmunity();
        if (remainingTurns > 0) {
            remainingTurns--;
        }
        movementRemaining = Math.max(0, player.getMovementPower());
        actionAvailable = true;
        if (remainingTurns <= 0 && !finished) {
            finish(false);
            log.add("Fin de partida: se han agotado los turnos");
        }
    }

    public int getAviolentadoTurns() {
        return aviolentadoTurns;
    }

    public void setAviolentadoTurns(int turns) {
        aviolentadoTurns = Math.max(aviolentadoTurns, Math.max(0, turns));
    }

    public int getEffectiveDefensePenalty() {
        return aviolentadoTurns > 0 ? 3 : 0;
    }

    public boolean isFinalRouteHintBought() {
        return finalRouteHintBought;
    }

    public void setFinalRouteHintBought(boolean finalRouteHintBought) {
        this.finalRouteHintBought = finalRouteHintBought;
    }

    public Lista<AreaEffect> getAreaEffects() {
        return areaEffects;
    }

    public void addAreaEffect(AreaEffect effect) {
        areaEffects.add(effect);
    }

    public void clearExpiredAreaEffects() {
        Lista<AreaEffect> activeEffects = new Lista<AreaEffect>();
        for (AreaEffect effect : areaEffects) {
            if (!effect.isExpired()) {
                activeEffects.add(effect);
            }
        }
        areaEffects.clear();
        for (AreaEffect effect : activeEffects) {
            areaEffects.add(effect);
        }
    }

    private void tickPlayerDebuffs() {
        if (aviolentadoTurns > 0) {
            aviolentadoTurns--;
        }
    }

    private void tickEnemyImmunity() {
        Room room = getRoom();
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Enemy enemy = room.getCell(new Position(row, column)).getEnemy();
                if (enemy != null) {
                    enemy.consumeImmunePlayerTurn();
                }
            }
        }
    }

    public void finish(boolean won) {
        this.finished = true;
        this.won = won;
    }

    private static RoomGraph singleRoomGraph(Room room) {
        RoomGraph graph = new RoomGraph();
        graph.addRoom(room);
        return graph;
    }

    public static class AreaEffect {
        public static final String RED = "red";
        public static final String YELLOW = "yellow";
        public static final String GREEN = "green";
        public static final String WHITE = "white";

        private final Lista<Position> cells;
        private final String color;
        private final int damage;
        private final String message;
        private int playerTurnsRemaining;
        private boolean triggered;

        public AreaEffect(Lista<Position> cells, String color, int damage, String message, int playerTurnsRemaining) {
            this.cells = cells;
            this.color = color;
            this.damage = damage;
            this.message = message;
            this.playerTurnsRemaining = playerTurnsRemaining;
        }

        public Lista<Position> getCells() {
            return cells;
        }

        public String getColor() {
            return color;
        }

        public int getDamage() {
            return damage;
        }

        public String getMessage() {
            return message;
        }

        public int getPlayerTurnsRemaining() {
            return playerTurnsRemaining;
        }

        public boolean isTriggered() {
            return triggered;
        }

        public boolean contains(Position position) {
            for (Position cell : cells) {
                if (cell.equals(position)) {
                    return true;
                }
            }
            return false;
        }

        public void tick() {
            if (playerTurnsRemaining > 0) {
                playerTurnsRemaining--;
            }
        }

        public void trigger() {
            triggered = true;
        }

        public boolean isExpired() {
            return triggered || playerTurnsRemaining <= 0;
        }
    }
}
