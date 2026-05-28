package org.example.juegofinalsupremo.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.juegofinalsupremo.contracts.Repository;
import org.example.juegofinalsupremo.data.Lista;
import org.example.juegofinalsupremo.data.RoomGraph;
import org.example.juegofinalsupremo.exceptions.GameStorageException;
import org.example.juegofinalsupremo.model.Cell;
import org.example.juegofinalsupremo.model.Enemy;
import org.example.juegofinalsupremo.model.GameLog;
import org.example.juegofinalsupremo.model.GameObject;
import org.example.juegofinalsupremo.model.GameState;
import org.example.juegofinalsupremo.model.Player;
import org.example.juegofinalsupremo.model.Position;
import org.example.juegofinalsupremo.model.Room;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameJsonRepository implements Repository {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void save(GameState state, String path) throws GameStorageException {
        try {
            Files.write(Paths.get(path), toJson(state).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new GameStorageException("No se pudo guardar la partida JSON", e);
        }
    }

    public GameState load(String path) throws GameStorageException {
        try {
            JsonObject root = JsonParser.parseString(new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8)).getAsJsonObject();
            if (!root.has("rooms")) {
                return loadLegacy(root);
            }
            return loadFull(root);
        } catch (RuntimeException e) {
            throw new GameStorageException("El JSON de partida no tiene el formato esperado", e);
        } catch (IOException e) {
            throw new GameStorageException("No se pudo leer la partida JSON", e);
        }
    }

    public String toJson(GameState state) {
        JsonObject root = new JsonObject();
        root.addProperty("version", 2);
        root.addProperty("currentRoomId", state.getCurrentRoomId());
        root.addProperty("finalRoomId", state.getFinalRoomId());
        root.addProperty("remainingTurns", state.getRemainingTurns());
        root.addProperty("movementRemaining", state.getMovementRemaining());
        root.addProperty("actionAvailable", state.isActionAvailable());
        root.addProperty("finished", state.isFinished());
        root.addProperty("won", state.isWon());
        root.addProperty("coins", state.getCoins());
        root.addProperty("difficultyIndex", state.getDifficultyIndex());
        root.addProperty("aviolentadoTurns", state.getAviolentadoTurns());
        root.addProperty("finalRouteHintBought", state.isFinalRouteHintBought());
        root.add("player", playerJson(state.getPlayer()));
        root.add("rooms", roomsJson(state));
        root.add("connections", connectionsJson(state.getRoomGraph()));
        root.add("areaEffects", areaEffectsJson(state));
        root.add("log", logJson(state.getLog()));
        return gson.toJson(root);
    }

    private GameState loadFull(JsonObject root) {
        RoomGraph graph = new RoomGraph();
        Lista<RoomEntry> rooms = new Lista<RoomEntry>();
        JsonArray roomArray = array(root, "rooms");
        for (JsonElement element : roomArray) {
            JsonObject roomJson = element.getAsJsonObject();
            Room room = new Room(text(roomJson, "id", "room"), text(roomJson, "name", "Habitacion"),
                    integer(roomJson, "rows", 1), integer(roomJson, "columns", 1));
            rooms.add(new RoomEntry(room.getId(), room));
            graph.addRoom(room);
        }
        applyConnections(graph, array(root, "connections"));
        Lista<EnemyEntry> enemiesById = new Lista<EnemyEntry>();
        for (JsonElement element : roomArray) {
            JsonObject roomJson = element.getAsJsonObject();
            applyCells(array(roomJson, "cells"), roomById(rooms, text(roomJson, "id", "room")), enemiesById);
        }

        Player player = player(root.getAsJsonObject("player"));
        GameState state = new GameState(graph, text(root, "currentRoomId", "habitacion-0"),
                text(root, "finalRoomId", text(root, "currentRoomId", "habitacion-0")), player, log(root));
        state.restoreRuntimeState(integer(root, "remainingTurns", 500),
                integer(root, "movementRemaining", player.getMovementPower()),
                bool(root, "actionAvailable", true),
                bool(root, "finished", false),
                bool(root, "won", false),
                integer(root, "coins", 0),
                integer(root, "difficultyIndex", 1),
                integer(root, "aviolentadoTurns", 0));
        state.setFinalRouteHintBought(bool(root, "finalRouteHintBought", false));
        restoreAreaEffects(state, array(root, "areaEffects"));
        return state;
    }

    private GameState loadLegacy(JsonObject root) {
        int rows = integer(root, "rows", 1);
        int columns = integer(root, "columns", 1);
        JsonObject playerJson = root.getAsJsonObject("player");
        Player player = new Player(text(playerJson, "name", "Heroe"), integer(playerJson, "health", 20),
                integer(playerJson, "attack", 5), integer(playerJson, "defense", 0),
                integer(playerJson, "movement", 1),
                new Position(integer(playerJson, "row", 0), integer(playerJson, "column", 0)));
        Room room = new Room(rows, columns);
        applyCells(array(root, "cells"), room, new Lista<EnemyEntry>());
        GameState state = new GameState(room, player, log(root));
        state.restoreRuntimeState(integer(root, "remainingTurns", 500), player.getMovementPower(),
                true, bool(root, "finished", false), bool(root, "won", false),
                integer(root, "coins", 0), integer(root, "difficultyIndex", 1), 0);
        state.setFinalRouteHintBought(bool(root, "finalRouteHintBought", false));
        return state;
    }

    private JsonObject playerJson(Player player) {
        JsonObject json = new JsonObject();
        json.addProperty("name", player.getName());
        json.addProperty("row", player.getPosition().getRow());
        json.addProperty("column", player.getPosition().getColumn());
        json.addProperty("health", player.getHealth());
        json.addProperty("maxHealth", player.getBaseMaxHealth());
        json.addProperty("attack", player.getBaseAttack());
        json.addProperty("defense", player.getBaseDefense());
        json.addProperty("movement", player.getMovement());
        json.addProperty("equippedWeaponIndex", inventoryIndex(player, player.getEquippedWeapon()));
        json.addProperty("equippedArmorIndex", inventoryIndex(player, player.getEquippedArmor()));
        JsonArray inventory = new JsonArray();
        for (int i = 0; i < player.getInventory().size(); i++) {
            inventory.add(objectJson(player.getInventory().get(i)));
        }
        json.add("inventory", inventory);
        return json;
    }

    public Player player(JsonObject json) {
        int maxHealth = integer(json, "maxHealth", integer(json, "health", 20));
        Player player = new Player(text(json, "name", "Heroe"), maxHealth,
                integer(json, "attack", 5), integer(json, "defense", 0),
                integer(json, "movement", 1),
                new Position(integer(json, "row", 0), integer(json, "column", 0)));
        int health = integer(json, "health", maxHealth);
        JsonArray inventory = array(json, "inventory");
        for (JsonElement element : inventory) {
            player.getInventory().add(object(element.getAsJsonObject()));
        }
        int weaponIndex = integer(json, "equippedWeaponIndex", -1);
        int armorIndex = integer(json, "equippedArmorIndex", -1);
        if (weaponIndex >= 0 && weaponIndex < player.getInventory().size()) {
            player.equip(player.getInventory().get(weaponIndex));
        }
        if (armorIndex >= 0 && armorIndex < player.getInventory().size()) {
            player.equip(player.getInventory().get(armorIndex));
        }
        if (health < player.getMaxHealth()) {
            player.receiveDamage(player.getMaxHealth() - health);
        }
        return player;
    }

    private JsonArray roomsJson(GameState state) {
        JsonArray rooms = new JsonArray();
        Lista<String> ids = state.getRoomGraph().roomIds();
        for (int i = 0; i < ids.size(); i++) {
            Room room = state.getRoomGraph().getRoom(ids.get(i));
            JsonObject json = new JsonObject();
            json.addProperty("id", room.getId());
            json.addProperty("name", room.getName());
            json.addProperty("rows", room.getRows());
            json.addProperty("columns", room.getColumns());
            JsonArray cells = new JsonArray();
            for (int row = 0; row < room.getRows(); row++) {
                for (int column = 0; column < room.getColumns(); column++) {
                    Cell cell = room.getCell(new Position(row, column));
                    if (hasSerializableContent(cell)) {
                        cells.add(cellJson(cell));
                    }
                }
            }
            json.add("cells", cells);
            rooms.add(json);
        }
        return rooms;
    }

    private JsonArray connectionsJson(RoomGraph graph) {
        JsonArray connections = new JsonArray();
        Lista<String> ids = graph.roomIds();
        for (int i = 0; i < ids.size(); i++) {
            Lista<String> neighbors = graph.neighborIds(ids.get(i));
            for (int j = 0; j < neighbors.size(); j++) {
                JsonObject connection = new JsonObject();
                connection.addProperty("from", ids.get(i));
                connection.addProperty("to", neighbors.get(j));
                connections.add(connection);
            }
        }
        return connections;
    }

    private JsonObject cellJson(Cell cell) {
        JsonObject json = new JsonObject();
        json.addProperty("row", cell.getPosition().getRow());
        json.addProperty("column", cell.getPosition().getColumn());
        if (cell.isWall()) {
            json.addProperty("type", "wall");
        } else if (cell.isDoor()) {
            json.addProperty("type", "door");
            json.addProperty("open", cell.isOpen());
            addNullable(json, "targetRoomId", cell.getTargetRoomId());
            if (cell.getTargetPosition() != null) {
                json.add("targetPosition", positionJson(cell.getTargetPosition()));
            }
            addNullable(json, "requiredKeyId", cell.getRequiredKeyId());
        } else if (cell.hasTrap()) {
            json.addProperty("type", "trap");
            json.addProperty("damage", cell.getTrapDamage());
        } else if (cell.getObject() != null) {
            json.addProperty("type", "object");
            json.add("object", objectJson(cell.getObject()));
        } else if (cell.getEnemy() != null) {
            json.addProperty("type", "enemy");
            json.add("enemy", enemyJson(cell.getEnemy()));
        } else if (cell.hasBlacksmith()) {
            json.addProperty("type", "blacksmith");
        }
        return json;
    }

    private void applyCells(JsonArray cells, Room room, Lista<EnemyEntry> enemiesById) {
        for (JsonElement element : cells) {
            JsonObject json = element.getAsJsonObject();
            Position position = new Position(integer(json, "row", 0), integer(json, "column", 0));
            Cell cell = room.getCell(position);
            String type = text(json, "type", "");
            if ("wall".equals(type)) {
                cell.setWall(true);
            } else if ("door".equals(type)) {
                Position target = json.has("targetPosition") ? position(json.getAsJsonObject("targetPosition")) : null;
                cell.setDoor(true, bool(json, "open", false), nullableText(json, "targetRoomId"), target);
                cell.setRequiredKeyId(nullableText(json, "requiredKeyId"));
            } else if ("trap".equals(type)) {
                cell.setTrapDamage(integer(json, "damage", 0));
            } else if ("object".equals(type)) {
                cell.setObject(object(json.getAsJsonObject("object")));
            } else if ("enemy".equals(type)) {
                Enemy enemy = enemy(json.getAsJsonObject("enemy"), enemiesById);
                cell.setEnemy(enemy);
            } else if ("blacksmith".equals(type)) {
                cell.setBlacksmith(true);
            }
        }
    }

    private JsonObject enemyJson(Enemy enemy) {
        JsonObject json = new JsonObject();
        json.addProperty("id", enemy.getId());
        json.addProperty("name", enemy.getName());
        json.addProperty("level", enemy.getLevel());
        json.addProperty("combatType", enemy.getCombatType());
        json.addProperty("health", enemy.getHealth());
        json.addProperty("attack", enemy.getAttack());
        json.addProperty("speed", enemy.getSpeed());
        json.addProperty("difficultyLabel", enemy.getDifficultyLabel());
        json.addProperty("summoned", enemy.isSummoned());
        json.addProperty("phaseTwo", enemy.isPhaseTwo());
        json.addProperty("skipTurns", enemy.getSkipTurns());
        json.addProperty("immunePlayerTurns", enemy.getImmunePlayerTurns());
        json.addProperty("finalJudgmentCountdown", enemy.getFinalJudgmentCountdown());
        return json;
    }

    private Enemy enemy(JsonObject json, Lista<EnemyEntry> enemiesById) {
        String id = text(json, "id", "enemy");
        Enemy existing = enemyById(enemiesById, id);
        if (existing != null) {
            return existing;
        }
        Enemy enemy = new Enemy(id, text(json, "name", "Enemigo"),
                integer(json, "level", 1), text(json, "combatType", Enemy.MELEE),
                integer(json, "health", 1), integer(json, "attack", 1),
                integer(json, "speed", 1), text(json, "difficultyLabel", "Medio"),
                bool(json, "summoned", false));
        enemy.setPhaseTwo(bool(json, "phaseTwo", false));
        enemy.setSkipTurns(integer(json, "skipTurns", 0));
        enemy.setImmunePlayerTurns(integer(json, "immunePlayerTurns", 0));
        enemy.setFinalJudgmentCountdown(integer(json, "finalJudgmentCountdown", 0));
        enemiesById.add(new EnemyEntry(id, enemy));
        return enemy;
    }

    private JsonObject objectJson(GameObject object) {
        JsonObject json = new JsonObject();
        json.addProperty("id", object.getId());
        json.addProperty("name", object.getName());
        json.addProperty("healing", object.getHealing());
        json.addProperty("maxHealthBonus", object.getMaxHealthBonus());
        json.addProperty("damageBonus", object.getDamageBonus());
        json.addProperty("defenseBonus", object.getDefenseBonus());
        json.addProperty("movementBonus", object.getMovementBonus());
        addNullable(json, "weaponCombatType", object.getWeaponCombatType());
        json.addProperty("type", object.getType());
        return json;
    }

    public GameObject object(JsonObject json) {
        String combatType = nullableText(json, "weaponCombatType");
        int damageBonus = integer(json, "damageBonus", 0);
        int defenseBonus = integer(json, "defenseBonus", 0);
        return new GameObject(text(json, "id", "object"), text(json, "name", "Objeto"),
                integer(json, "healing", 0), integer(json, "maxHealthBonus", 0),
                damageBonus, defenseBonus,
                integer(json, "movementBonus", 0), combatType,
                text(json, "type", inferredType(damageBonus, defenseBonus, combatType)));
    }

    private String inferredType(int damageBonus, int defenseBonus, String combatType) {
        if (combatType != null || damageBonus != 0) {
            return GameObject.TYPE_WEAPON;
        }
        if (defenseBonus > 0) {
            return GameObject.TYPE_ARMOR;
        }
        return GameObject.TYPE_POTION;
    }

    private JsonArray areaEffectsJson(GameState state) {
        JsonArray effects = new JsonArray();
        for (GameState.AreaEffect effect : state.getAreaEffects()) {
            JsonObject json = new JsonObject();
            json.addProperty("color", effect.getColor());
            json.addProperty("damage", effect.getDamage());
            json.addProperty("message", effect.getMessage());
            json.addProperty("playerTurnsRemaining", effect.getPlayerTurnsRemaining());
            json.addProperty("triggered", effect.isTriggered());
            JsonArray cells = new JsonArray();
            for (Position position : effect.getCells()) {
                cells.add(positionJson(position));
            }
            json.add("cells", cells);
            effects.add(json);
        }
        return effects;
    }

    private void restoreAreaEffects(GameState state, JsonArray effects) {
        for (JsonElement element : effects) {
            JsonObject json = element.getAsJsonObject();
        Lista<Position> cells = new Lista<Position>();
            for (JsonElement cell : array(json, "cells")) {
                cells.add(position(cell.getAsJsonObject()));
            }
            GameState.AreaEffect effect = new GameState.AreaEffect(cells, text(json, "color", GameState.AreaEffect.RED),
                    integer(json, "damage", 0), text(json, "message", ""), integer(json, "playerTurnsRemaining", 0));
            if (bool(json, "triggered", false)) {
                effect.trigger();
            }
            state.addAreaEffect(effect);
        }
    }

    private JsonArray logJson(GameLog log) {
        JsonArray entries = new JsonArray();
        for (int i = 0; i < log.getEntries().size(); i++) {
            entries.add(log.getEntries().get(i));
        }
        return entries;
    }

    private GameLog log(JsonObject root) {
        GameLog log = new GameLog();
        for (JsonElement element : array(root, "log")) {
            log.add(element.getAsString());
        }
        return log;
    }

    private void applyConnections(RoomGraph graph, JsonArray connections) {
        for (JsonElement element : connections) {
            JsonObject connection = element.getAsJsonObject();
            String from = text(connection, "from", null);
            String to = text(connection, "to", null);
            if (from != null && to != null && graph.contains(from) && graph.contains(to) && !graph.areConnected(from, to)) {
                graph.connect(from, to);
            }
        }
    }

    private boolean hasSerializableContent(Cell cell) {
        return cell.isWall() || cell.isDoor() || cell.hasTrap() || cell.getObject() != null
                || cell.getEnemy() != null || cell.hasBlacksmith();
    }

    private int inventoryIndex(Player player, GameObject object) {
        if (object == null) {
            return -1;
        }
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().get(i) == object) {
                return i;
            }
        }
        return -1;
    }

    private JsonObject positionJson(Position position) {
        JsonObject json = new JsonObject();
        json.addProperty("row", position.getRow());
        json.addProperty("column", position.getColumn());
        return json;
    }

    private Position position(JsonObject json) {
        return new Position(integer(json, "row", 0), integer(json, "column", 0));
    }

    private JsonArray array(JsonObject json, String key) {
        if (json == null || !json.has(key) || !json.get(key).isJsonArray()) {
            return new JsonArray();
        }
        return json.getAsJsonArray(key);
    }

    private int integer(JsonObject json, String key, int fallback) {
        return json != null && json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsInt() : fallback;
    }

    private boolean bool(JsonObject json, String key, boolean fallback) {
        return json != null && json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsBoolean() : fallback;
    }

    private String text(JsonObject json, String key, String fallback) {
        return json != null && json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsString() : fallback;
    }

    private String nullableText(JsonObject json, String key) {
        String value = text(json, key, null);
        return value == null || value.isEmpty() ? null : value;
    }

    private void addNullable(JsonObject json, String key, String value) {
        if (value != null) {
            json.addProperty(key, value);
        }
    }

    private Room roomById(Lista<RoomEntry> rooms, String id) {
        for (int i = 0; i < rooms.size(); i++) {
            RoomEntry entry = rooms.get(i);
            if (entry.id.equals(id)) {
                return entry.room;
            }
        }
        throw new IllegalArgumentException("No existe la habitacion: " + id);
    }

    private Enemy enemyById(Lista<EnemyEntry> enemiesById, String id) {
        for (int i = 0; i < enemiesById.size(); i++) {
            EnemyEntry entry = enemiesById.get(i);
            if (entry.id.equals(id)) {
                return entry.enemy;
            }
        }
        return null;
    }

    private static class RoomEntry {
        private final String id;
        private final Room room;

        private RoomEntry(String id, Room room) {
            this.id = id;
            this.room = room;
        }
    }

    private static class EnemyEntry {
        private final String id;
        private final Enemy enemy;

        private EnemyEntry(String id, Enemy enemy) {
            this.id = id;
            this.enemy = enemy;
        }
    }
}
