package org.example.juegofinalsupremo.model;

import org.example.juegofinalsupremo.data.RoomGraph;

import java.util.Random;

public class RoomNetworkFactory {
    private static final int ROOM_COUNT = 25;
    private static final int FINAL_ROOM_INDEX = 9;
    private static final int SEMIFINAL_ROOM_INDEX = 8;
    private static final int BOSS_ROOM_INDEX = 6;
    private static final int BLACKSMITH_NEAR_FINAL_ROOM_INDEX = 10;
    private static final int BLACKSMITH_FAR_FINAL_ROOM_INDEX = 11;
    private static final int LOCKED_TREASURE_ROOM_INDEX = 24;
    private static final int MAX_GRAPH_DOORS = 4;
    private static final int[] MAIN_PATH = {0, 1, 2, 3, 4, 5, 6, 7, SEMIFINAL_ROOM_INDEX, FINAL_ROOM_INDEX};
    private int currentDifficultyIndex = 1;

    public GameState createRandomState(String playerName) {
        return createRandomState(playerName, CharacterClass.GUERRERO);
    }

    public GameState createRandomState(String playerName, CharacterClass characterClass) {
        return createRandomState(playerName, characterClass, 1);
    }

    public GameState createRandomState(String playerName, CharacterClass characterClass, int difficultyIndex) {
        currentDifficultyIndex = Math.max(0, Math.min(3, difficultyIndex));
        Random random = new Random();
        Room[] rooms = createRooms(random);
        RoomGraph graph = new RoomGraph();
        for (int i = 0; i < rooms.length; i++) {
            graph.addRoom(rooms[i]);
        }

        int[] doorCounts = new int[ROOM_COUNT];
        boolean[][] connected = new boolean[ROOM_COUNT][ROOM_COUNT];
        buildMainPath(graph, rooms, doorCounts, connected);
        buildSecondaryPaths(random, graph, rooms, doorCounts, connected);
        addFinalExit(rooms[FINAL_ROOM_INDEX], doorCounts[FINAL_ROOM_INDEX]);
        addRoomContent(random, rooms);

        Position start = centerOf(rooms[0]);
        Player player = new Player(playerName, characterClass.getHealth(), characterClass.getAttack(),
                characterClass.getDefense(), characterClass.getSpeed(), start);
        GameObject starterItem = characterClass.createStarterItem();
        player.getInventory().add(starterItem);
        player.equip(starterItem);
        GameLog log = new GameLog();
        log.add("Red de " + ROOM_COUNT + " habitaciones generada");
        log.add("Clase seleccionada: " + characterClass.getDisplayName());
        GameState state = new GameState(graph, rooms[0].getId(), rooms[FINAL_ROOM_INDEX].getId(), player, log);
        state.setDifficultyIndex(currentDifficultyIndex);
        if (characterClass == CharacterClass.NAPOLEON) {
            state.addCoins(2000);
        }
        return state;
    }

    private Room[] createRooms(Random random) {
        Room[] rooms = new Room[ROOM_COUNT];
        for (int i = 0; i < rooms.length; i++) {
            int size;
            if (i == FINAL_ROOM_INDEX) {
                rooms[i] = new Room(roomId(i), roomName(i), 12, 12);
                continue;
            } else if (i == BOSS_ROOM_INDEX) {
                size = 9;
            } else if (i == LOCKED_TREASURE_ROOM_INDEX) {
                size = 5;
            } else if (isSecondaryRoom(i)) {
                size = 3 + random.nextInt(5);
            } else {
                size = 5 + random.nextInt(5);
            }
            rooms[i] = new Room(roomId(i), roomName(i), size, size);
        }
        return rooms;
    }

    private void buildMainPath(RoomGraph graph, Room[] rooms, int[] doorCounts, boolean[][] connected) {
        for (int i = 0; i < MAIN_PATH.length - 1; i++) {
            connectRooms(graph, rooms, doorCounts, connected, MAIN_PATH[i], MAIN_PATH[i + 1]);
        }
    }

    private void buildSecondaryPaths(Random random, RoomGraph graph, Room[] rooms, int[] doorCounts, boolean[][] connected) {
        connectRooms(graph, rooms, doorCounts, connected, 6, BLACKSMITH_NEAR_FINAL_ROOM_INDEX);
        connectRooms(graph, rooms, doorCounts, connected, 3, BLACKSMITH_FAR_FINAL_ROOM_INDEX);
        connectRooms(graph, rooms, doorCounts, connected, BOSS_ROOM_INDEX, LOCKED_TREASURE_ROOM_INDEX);
        lockDoorBetween(rooms, BOSS_ROOM_INDEX, LOCKED_TREASURE_ROOM_INDEX);

        int[] anchors = {0, 0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 7};
        for (int secondary = 12; secondary < LOCKED_TREASURE_ROOM_INDEX; secondary++) {
            int anchor = anchors[(secondary - 12) % anchors.length];
            connectRooms(graph, rooms, doorCounts, connected, anchor, secondary);
        }
    }

    private void connectRooms(RoomGraph graph, Room[] rooms, int[] doorCounts, boolean[][] connected, int from, int to) {
        graph.connectBoth(rooms[from].getId(), rooms[to].getId());
        connected[from][to] = true;
        connected[to][from] = true;

        Position fromDoor = doorPosition(rooms[from], doorCounts[from] + from + to);
        Position toDoor = doorPosition(rooms[to], doorCounts[to] + to + from + 2);
        rooms[from].getCell(fromDoor).setDoor(true, false, rooms[to].getId(), toDoor);
        rooms[to].getCell(toDoor).setDoor(true, false, rooms[from].getId(), fromDoor);
        doorCounts[from]++;
        doorCounts[to]++;
    }

    private boolean canConnect(int from, int to, int[] doorCounts, boolean[][] connected) {
        return from != FINAL_ROOM_INDEX
                && to != FINAL_ROOM_INDEX
                && !isSecondaryRoom(from)
                && !isSecondaryRoom(to)
                && from != to
                && !connected[from][to]
                && doorCounts[from] < MAX_GRAPH_DOORS
                && doorCounts[to] < MAX_GRAPH_DOORS;
    }

    private void addFinalExit(Room finalRoom, int usedDoors) {
        Position exit = new Position(0, finalRoom.getColumns() - 1);
        if (!finalRoom.getCell(exit).isEmpty()) {
            exit = doorPosition(finalRoom, usedDoors);
        }
        finalRoom.getCell(exit).setDoor(true, false);
        finalRoom.getCell(exit).setRequiredKeyId("llave-celestial");
    }

    private void addRoomContent(Random random, Room[] rooms) {
        for (int i = 0; i < rooms.length; i++) {
            if (i == LOCKED_TREASURE_ROOM_INDEX) {
                placeTreasureRoomReward(random, rooms[i]);
                continue;
            }
            Room room = rooms[i];
            if (i == BOSS_ROOM_INDEX) {
                placeEnemy(random, room, bossEnemy(4));
            } else if (i == FINAL_ROOM_INDEX) {
                placeFinalBoss(room, bossEnemy(5));
            } else {
                placeSpawns(random, room, i);
                if (i != 0) {
                    placeEnemies(random, room, i);
                }
            }
            if (i > 0 && i % 5 == 0) {
                placeTrap(random, room, 5);
            }
        }
        placeBlacksmith(random, rooms[BLACKSMITH_NEAR_FINAL_ROOM_INDEX]);
        placeBlacksmith(random, rooms[BLACKSMITH_FAR_FINAL_ROOM_INDEX]);
    }

    private void placeSpawns(Random random, Room room, int roomIndex) {
        int spawns;
        if (isSecondaryRoom(roomIndex)) {
            spawns = random.nextInt(100) < 75 ? 1 : 2;
        } else {
            int roll = random.nextInt(100);
            spawns = roll < 25 ? 1 : roll < 75 ? 2 : 3;
        }
        for (int i = 0; i < spawns; i++) {
            placeObject(random, room, randomSpawnObject(random, roomIndex, i));
        }
    }

    private GameObject randomSpawnObject(Random random, int roomIndex, int spawnIndex) {
        int roll = random.nextInt(100);
        boolean late = tierLate(roomIndex);
        if (roll < 50) {
            int value = late ? 1 : (random.nextBoolean() ? 1 : 2);
            return new GameObject("coin-spawn-" + roomIndex + "-" + spawnIndex, "Moneda", value, 0,
                    0, 0, 0, null, GameObject.TYPE_COIN);
        }
        if (roll < 80) {
            return randomPotionByTier(random, roomIndex, late);
        }
        if (roll < 90) {
            return randomCommonArmorByTier(random, roomIndex, late);
        }
        return randomCommonWeaponByTier(random, roomIndex, late);
    }

    private void placeEnemies(Random random, Room room, int roomIndex) {
        int count;
        if (isSecondaryRoom(roomIndex)) {
            count = random.nextInt(100) < 65 ? 0 : 1;
        } else {
            int roll = random.nextInt(100);
            count = roll < 40 ? 0 : roll < 80 ? 1 : 2;
        }
        for (int i = 0; i < count; i++) {
            Enemy enemy = randomEnemy(random, roomIndex);
            placeEnemy(random, room, enemy);
            if (enemy.getId().contains("enemy-1")) {
                placeEnemy(random, room, randomEnemyOfType(random, roomIndex, 1));
            }
        }
    }

    private void lockDoorBetween(Room[] rooms, int first, int second) {
        markLockedDoor(rooms[first], rooms[second].getId());
        markLockedDoor(rooms[second], rooms[first].getId());
    }

    private void markLockedDoor(Room room, String targetRoomId) {
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Cell cell = room.getCell(new Position(row, column));
                if (cell.isDoor() && targetRoomId.equals(cell.getTargetRoomId())) {
                    cell.setRequiredKeyId("llave-demoniaca");
                }
            }
        }
    }

    private Enemy randomEnemy(Random random, int roomIndex) {
        boolean late = tierLate(roomIndex);
        int roll = random.nextInt(100);
        int type = late ? (roll < 60 ? 1 : roll < 90 ? 2 : 3) : (roll < 40 ? 1 : roll < 70 ? 2 : 3);
        return randomEnemyOfType(random, roomIndex, type);
    }

    private Enemy randomEnemyOfType(Random random, int roomIndex, int type) {
        int level = randomLevel(random, tierLate(roomIndex));
        int[] stats = enemyStats(type, level);
        String combatType = type == 2 ? Enemy.RANGED : Enemy.MELEE;
        int speed = type == 3 ? 2 : 1;
        if (type == 1 && difficultyIndex() == 3 && level >= 2) {
            speed = level;
        }
        return new Enemy("enemy-" + type + "-" + roomIndex + "-" + System.nanoTime(), enemyName(type),
                level, combatType, stats[0], stats[1], speed, difficultyName(), false);
    }

    private int randomLevel(Random random, boolean late) {
        int roll = random.nextInt(100);
        if (late) {
            return roll < 65 ? 1 : roll < 90 ? 2 : 3;
        }
        return roll < 45 ? 1 : roll < 80 ? 2 : 3;
    }

    private int[] enemyStats(int type, int level) {
        int d = difficultyIndex();
        int[][][] rat = {
                {{4, 3}, {5, 3}, {6, 4}},
                {{5, 3}, {6, 3}, {7, 4}},
                {{6, 3}, {6, 4}, {8, 5}},
                {{7, 4}, {8, 4}, {8, 5}}
        };
        int[][][] goblin = {
                {{8, 4}, {9, 4}, {10, 5}},
                {{9, 4}, {10, 4}, {11, 5}},
                {{9, 5}, {10, 5}, {11, 6}},
                {{10, 5}, {11, 6}, {12, 6}}
        };
        int[][][] dragon = {
                {{8, 5}, {10, 5}, {12, 7}},
                {{10, 5}, {12, 5}, {14, 7}},
                {{12, 5}, {12, 7}, {16, 9}},
                {{14, 7}, {16, 7}, {16, 9}}
        };
        if (type == 1) {
            return rat[d][level - 1];
        }
        if (type == 2) {
            return goblin[d][level - 1];
        }
        return dragon[d][level - 1];
    }

    private Enemy bossEnemy(int type) {
        int d = difficultyIndex();
        if (type == 4) {
            int[][] stats = {{16, 8}, {19, 9}, {22, 10}, {25, 12}};
            return new Enemy("enemy-4-boss", "Ametrallador (BOSS)", 1, Enemy.RANGED,
                    stats[d][0], stats[d][1], 1, difficultyName(), false);
        }
        int[][] stats = {{40, 12}, {46, 14}, {52, 16}, {60, 20}};
        return new Enemy("enemy-5-final-boss", "Dios Demoniaco Marquette (BOSS FINAL)", 1, Enemy.MELEE,
                stats[d][0], stats[d][1], 1, difficultyName(), false);
    }

    private String enemyName(int type) {
        if (type == 1) {
            return "Rata rabiosa";
        }
        if (type == 2) {
            return "Goblin arquero";
        }
        return "Dragon";
    }

    private GameObject randomCommonWeapon(Random random, int roomIndex) {
        int type = 1 + random.nextInt(3);
        if (type == 1) {
            return weapon("weapon-1-" + roomIndex, "espada", 2, Enemy.MELEE);
        }
        if (type == 2) {
            return weapon("weapon-2-" + roomIndex, "arco de madera", -1, Enemy.RANGED);
        }
        return weapon("weapon-3-" + roomIndex, "hacha de guerra", 3, Enemy.MELEE);
    }

    private GameObject randomCommonWeaponByTier(Random random, int roomIndex, boolean late) {
        int roll = random.nextInt(100);
        if (late) {
            if (roll < 60) {
                return weapon("weapon-1-" + roomIndex, "Espada", 2, Enemy.MELEE);
            }
            if (roll < 80) {
                return weapon("weapon-2-" + roomIndex, "Arco De Madera", -1, Enemy.RANGED);
            }
            return weapon("weapon-3-" + roomIndex, "Hacha De Guerra", 3, Enemy.MELEE);
        }
        if (roll < 30) {
            return weapon("weapon-1-" + roomIndex, "Espada", 2, Enemy.MELEE);
        }
        if (roll < 60) {
            return weapon("weapon-2-" + roomIndex, "Arco De Madera", -1, Enemy.RANGED);
        }
        if (roll < 96) {
            return weapon("weapon-3-" + roomIndex, "Hacha De Guerra", 3, Enemy.MELEE);
        }
        if (roll < 98) {
            return weapon("weapon-4-" + roomIndex, "Arco Celestial", 1, Enemy.RANGED);
        }
        return weapon("weapon-5-" + roomIndex, "Espada Celestial", 5, Enemy.MELEE);
    }

    private GameObject randomCommonArmor(Random random, int roomIndex) {
        int type = 1 + random.nextInt(4);
        if (type == 1) {
            return armor("armor-1-" + roomIndex, "armadura roja", 5, 1, 0);
        }
        if (type == 2) {
            return armor("armor-2-" + roomIndex, "armadura de tela", 0, 2, 0);
        }
        if (type == 3) {
            return armor("armor-3-" + roomIndex, "armadura de hermes", 0, 1, 1);
        }
        return armor("armor-4-" + roomIndex, "armadura de hierro", 0, 3, 0);
    }

    private GameObject randomCommonArmorByTier(Random random, int roomIndex, boolean late) {
        int roll = random.nextInt(100);
        if (late) {
            if (roll < 40) {
                return armor("armor-1-" + roomIndex, "Armadura Roja", 5, 1, 0);
            }
            if (roll < 80) {
                return armor("armor-2-" + roomIndex, "Armadura De Tela", 0, 2, 0);
            }
            if (roll < 90) {
                return armor("armor-4-" + roomIndex, "Armadura De Hierro", 0, 3, 0);
            }
            return armor("armor-3-" + roomIndex, "Armadura De Hermes", 0, 1, 1);
        }
        if (roll < 25) {
            return armor("armor-1-" + roomIndex, "Armadura Roja", 5, 1, 0);
        }
        if (roll < 50) {
            return armor("armor-2-" + roomIndex, "Armadura De Tela", 0, 2, 0);
        }
        if (roll < 73) {
            return armor("armor-4-" + roomIndex, "Armadura De Hierro", 0, 3, 0);
        }
        if (roll < 96) {
            return armor("armor-3-" + roomIndex, "Armadura De Hermes", 0, 1, 1);
        }
        return armor("armor-5-" + roomIndex, "Armadura Celestial", 10, 4, 1);
    }

    private GameObject randomPotion(Random random, int roomIndex) {
        int type = 1 + random.nextInt(4);
        if (type == 1) {
            return new GameObject("potion-max-" + roomIndex, "pocion de vida Maxima", 5, 5,
                    0, 0, 0, null, GameObject.TYPE_POTION);
        }
        if (type == 2) {
            return new GameObject("potion-health-" + roomIndex, "pocion de vida normal", 10, 0,
                    0, 0, 0, null, GameObject.TYPE_POTION);
        }
        if (type == 3) {
            return new GameObject("potion-speed-" + roomIndex, "pocion de velocidad", 0, 0,
                    0, 0, 1, null, GameObject.TYPE_POTION);
        }
        return new GameObject("potion-defense-" + roomIndex, "pocion de defensa", 0, 0,
                0, 1, 0, null, GameObject.TYPE_POTION);
    }

    private GameObject randomPotionByTier(Random random, int roomIndex, boolean late) {
        int roll = random.nextInt(100);
        if (late) {
            if (roll < 65) {
                return new GameObject("potion-health-" + roomIndex, "Pocion De Vida", 10, 0,
                        0, 0, 0, null, GameObject.TYPE_POTION);
            }
            if (roll < 87) {
                return new GameObject("potion-max-" + roomIndex, "Pocion De Vida Maxima", 5, 5,
                        0, 0, 0, null, GameObject.TYPE_POTION);
            }
            if (roll < 97) {
                return new GameObject("potion-defense-" + roomIndex, "Pocion De Defensa", 0, 0,
                        0, 1, 0, null, GameObject.TYPE_POTION);
            }
            return new GameObject("potion-speed-" + roomIndex, "Pocion De Velocidad", 0, 0,
                    0, 0, 1, null, GameObject.TYPE_POTION);
        }
        if (roll < 50) {
            return new GameObject("potion-health-" + roomIndex, "Pocion De Vida", 10, 0,
                    0, 0, 0, null, GameObject.TYPE_POTION);
        }
        if (roll < 75) {
            return new GameObject("potion-max-" + roomIndex, "Pocion De Vida Maxima", 5, 5,
                    0, 0, 0, null, GameObject.TYPE_POTION);
        }
        if (roll < 90) {
            return new GameObject("potion-defense-" + roomIndex, "Pocion De Defensa", 0, 0,
                    0, 1, 0, null, GameObject.TYPE_POTION);
        }
        return new GameObject("potion-speed-" + roomIndex, "Pocion De Velocidad", 0, 0,
                0, 0, 1, null, GameObject.TYPE_POTION);
    }

    private void placeTreasureRoomReward(Random random, Room room) {
        GameObject reward;
        int roll = random.nextInt(3);
        if (roll == 0) {
            reward = armor("armor-5-demonic", "armadura celestial", 10, 4, 1);
        } else if (roll == 1) {
            reward = weapon("weapon-4-demonic", "arco celestial", 1, Enemy.RANGED);
        } else {
            reward = weapon("weapon-5-demonic", "espada celestial", 5, Enemy.MELEE);
        }
        Position chest = new Position(room.getRows() / 2, room.getColumns() / 2);
        room.getCell(chest).setObject(reward);
    }

    private GameObject weapon(String id, String name, int damageBonus, String combatType) {
        return new GameObject(id, name, 0, 0, damageBonus, 0, 0, combatType, GameObject.TYPE_WEAPON);
    }

    private GameObject armor(String id, String name, int maxHealthBonus, int defenseBonus, int movementBonus) {
        return new GameObject(id, name, 0, maxHealthBonus, 0, defenseBonus, movementBonus, null, GameObject.TYPE_ARMOR);
    }

    private void placeBlacksmith(Random random, Room room) {
        Position position = randomEmptyInteriorPosition(random, room);
        if (position != null) {
            room.getCell(position).setBlacksmith(true);
        }
    }

    private void placeObject(Random random, Room room, GameObject object) {
        Position position = randomEmptyInteriorPosition(random, room);
        if (position != null) {
            room.getCell(position).setObject(object);
        }
    }

    private void placeEnemy(Random random, Room room, Enemy enemy) {
        Position position = enemyPosition(random, room);
        if (position != null) {
            room.getCell(position).setEnemy(enemy);
        }
    }

    private void placeFinalBoss(Room room, Enemy enemy) {
        Position anchor = new Position(room.getRows() / 2 - 1, room.getColumns() / 2 - 1);
        for (int row = anchor.getRow(); row < anchor.getRow() + 3; row++) {
            for (int column = anchor.getColumn(); column < anchor.getColumn() + 3; column++) {
                room.getCell(new Position(row, column)).setEnemy(enemy);
            }
        }
    }

    private Position enemyPosition(Random random, Room room) {
        if (isSmallRoom(room)) {
            return cornerEnemyPosition(room);
        }
        Position position = randomEnemyPositionAtDistance(random, room);
        if (position != null) {
            return position;
        }
        return farthestEmptyPosition(room);
    }

    private Position cornerEnemyPosition(Room room) {
        Position[] corners = {
                new Position(0, 0),
                new Position(0, room.getColumns() - 1),
                new Position(room.getRows() - 1, 0),
                new Position(room.getRows() - 1, room.getColumns() - 1)
        };
        for (int i = 0; i < corners.length; i++) {
            if (room.getCell(corners[i]).isEmpty()) {
                return corners[i];
            }
        }
        return randomEmptyInteriorPosition(new Random(), room);
    }

    private Position randomEnemyPositionAtDistance(Random random, Room room) {
        for (int attempt = 0; attempt < 60; attempt++) {
            Position position = randomEmptyInteriorPosition(random, room);
            if (position != null && manhattan(position, centerOf(room)) >= 5) {
                return position;
            }
        }
        return null;
    }

    private Position farthestEmptyPosition(Room room) {
        Position best = null;
        int bestDistance = -1;
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Position position = new Position(row, column);
                if (room.getCell(position).isEmpty()) {
                    int distance = manhattan(position, centerOf(room));
                    if (distance > bestDistance) {
                        bestDistance = distance;
                        best = position;
                    }
                }
            }
        }
        return best;
    }

    private int manhattan(Position first, Position second) {
        return Math.abs(first.getRow() - second.getRow()) + Math.abs(first.getColumn() - second.getColumn());
    }

    private boolean tierLate(int roomIndex) {
        int mainIndex = mainAnchorIndex(roomIndex);
        return mainIndex >= 6;
    }

    private int mainAnchorIndex(int roomIndex) {
        if (!isSecondaryRoom(roomIndex)) {
            return roomIndex;
        }
        if (roomIndex == BLACKSMITH_NEAR_FINAL_ROOM_INDEX || roomIndex == LOCKED_TREASURE_ROOM_INDEX) {
            return BOSS_ROOM_INDEX;
        }
        if (roomIndex == BLACKSMITH_FAR_FINAL_ROOM_INDEX) {
            return 3;
        }
        int[] anchors = {0, 0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 7};
        return anchors[Math.max(0, Math.min(anchors.length - 1, roomIndex - 12))];
    }

    private int difficultyIndex() {
        return currentDifficultyIndex;
    }

    private String difficultyName() {
        String[] names = {"Facil", "Medio", "Dificil", "Marquette"};
        return names[currentDifficultyIndex];
    }

    private boolean isSmallRoom(Room room) {
        return room.getRows() <= 5 && room.getColumns() <= 5;
    }

    private void placeTrap(Random random, Room room, int damage) {
        Position position = randomEmptyInteriorPosition(random, room);
        if (position != null) {
            room.getCell(position).setTrapDamage(damage);
        }
    }

    private Position randomEmptyInteriorPosition(Random random, Room room) {
        for (int attempt = 0; attempt < 30; attempt++) {
            int row = 1 + random.nextInt(Math.max(1, room.getRows() - 2));
            int column = 1 + random.nextInt(Math.max(1, room.getColumns() - 2));
            Position position = new Position(row, column);
            if (room.getCell(position).isEmpty() && !position.equals(centerOf(room))) {
                return position;
            }
        }
        return null;
    }

    private Position doorPosition(Room room, int index) {
        int rowMiddle = room.getRows() / 2;
        int columnMiddle = room.getColumns() / 2;
        Position[] positions = {
                new Position(0, columnMiddle),
                new Position(room.getRows() - 1, columnMiddle),
                new Position(rowMiddle, 0),
                new Position(rowMiddle, room.getColumns() - 1)
        };
        for (int i = 0; i < positions.length; i++) {
            Position position = positions[(index + i) % positions.length];
            if (!room.getCell(position).isDoor()) {
                return position;
            }
        }
        throw new IllegalStateException("La habitacion " + room.getName() + " no tiene lados libres para otra puerta");
    }

    private Position centerOf(Room room) {
        return new Position(room.getRows() / 2, room.getColumns() / 2);
    }

    private boolean isSecondaryRoom(int index) {
        return index >= 10;
    }

    private String roomId(int index) {
        return "habitacion-" + index;
    }

    private String roomName(int index) {
        if (index == FINAL_ROOM_INDEX) {
            return "Habitacion final";
        }
        if (index == SEMIFINAL_ROOM_INDEX) {
            return "Antesala final";
        }
        if (index == LOCKED_TREASURE_ROOM_INDEX) {
            return "Sala secundaria cerrada";
        }
        if (isSecondaryRoom(index)) {
            return "Sala secundaria " + (index - 9);
        }
        return "Habitacion " + index;
    }
}
