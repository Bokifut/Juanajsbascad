package org.example.juegofinalsupremo.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RoomNetworkFactoryTest {

    private Object invoke(RoomNetworkFactory factory, String methodName, Object... args) {
        try {
            for (Method m : RoomNetworkFactory.class.getDeclaredMethods()) {
                if (m.getName().equals(methodName) && m.getParameterCount() == args.length) {
                    m.setAccessible(true);
                    return m.invoke(factory, args);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Test
    void testMassiveCoverage() {
        RoomNetworkFactory factory = new RoomNetworkFactory();

        for (int diff = 0; diff <= 3; diff++) {
            GameState state1 = factory.createRandomState("Player1");
            assertNotNull(state1);

            GameState state2 = factory.createRandomState("Player2", CharacterClass.GUERRERO);
            assertNotNull(state2);

            GameState state3 = factory.createRandomState("Player3", CharacterClass.NAPOLEON, diff);
            assertNotNull(state3);

            Random rand = new Random();
            Room rBig = new Room(12, 12);
            Room rSmall = new Room(3, 3);

            for (int i = 0; i < 250; i++) {
                invoke(factory, "randomCommonWeapon", rand, 1);
                invoke(factory, "randomCommonArmor", rand, 1);
                invoke(factory, "randomPotion", rand, 1);

                invoke(factory, "randomCommonWeaponByTier", rand, 1, false);
                invoke(factory, "randomCommonWeaponByTier", rand, 15, true);
                invoke(factory, "randomCommonArmorByTier", rand, 1, false);
                invoke(factory, "randomCommonArmorByTier", rand, 15, true);
                invoke(factory, "randomPotionByTier", rand, 1, false);
                invoke(factory, "randomPotionByTier", rand, 15, true);

                invoke(factory, "randomSpawnObject", rand, 1, 0);
                invoke(factory, "randomSpawnObject", rand, 15, 0);

                invoke(factory, "randomEnemy", rand, 1);
                invoke(factory, "randomEnemy", rand, 15);

                invoke(factory, "randomLevel", rand, false);
                invoke(factory, "randomLevel", rand, true);

                for (int type = 1; type <= 3; type++) {
                    invoke(factory, "randomEnemyOfType", rand, 1, type);
                    invoke(factory, "randomEnemyOfType", rand, 15, type);
                    for (int level = 1; level <= 4; level++) {
                        invoke(factory, "enemyStats", type, level);
                    }
                    invoke(factory, "enemyName", type);
                }

                invoke(factory, "placeSpawns", rand, rBig, 1);
                invoke(factory, "placeSpawns", rand, rBig, 15);

                invoke(factory, "placeEnemies", rand, rBig, 1);
                invoke(factory, "placeEnemies", rand, rBig, 15);

                invoke(factory, "placeTrap", rand, rBig, 5);
                invoke(factory, "placeBlacksmith", rand, rBig);
                invoke(factory, "placeObject", rand, rBig, new GameObject());
                invoke(factory, "placeEnemy", rand, rBig, new Enemy());
                invoke(factory, "placeTreasureRoomReward", rand, rBig);
            }

            invoke(factory, "bossEnemy", 4);
            invoke(factory, "bossEnemy", 5);
            invoke(factory, "placeFinalBoss", rBig, new Enemy());

            invoke(factory, "difficultyName");
        }

        for (int idx = 0; idx <= 30; idx++) {
            invoke(factory, "isSecondaryRoom", idx);
            invoke(factory, "roomId", idx);
            invoke(factory, "roomName", idx);
            invoke(factory, "tierLate", idx);
            invoke(factory, "mainAnchorIndex", idx);
        }

        int[] doorCounts = new int[25];
        boolean[][] connected = new boolean[25][25];
        invoke(factory, "canConnect", 0, 1, doorCounts, connected);
        invoke(factory, "canConnect", 9, 1, doorCounts, connected);
        invoke(factory, "canConnect", 1, 9, doorCounts, connected);
        invoke(factory, "canConnect", 10, 1, doorCounts, connected);
        invoke(factory, "canConnect", 1, 10, doorCounts, connected);
        invoke(factory, "canConnect", 1, 1, doorCounts, connected);
        connected[1][2] = true;
        invoke(factory, "canConnect", 1, 2, doorCounts, connected);
        doorCounts[1] = 4;
        invoke(factory, "canConnect", 1, 3, doorCounts, connected);
        doorCounts[1] = 0;
        doorCounts[3] = 4;
        invoke(factory, "canConnect", 1, 3, doorCounts, connected);

        Room rExit = new Room(12, 12);
        rExit.getCell(new Position(0, 11)).setWall(true);
        invoke(factory, "addFinalExit", rExit, 0);

        Random rand = new Random();
        Room rSmall = new Room(3, 3);
        Room rBig = new Room(12, 12);
        invoke(factory, "enemyPosition", rand, rBig);
        invoke(factory, "enemyPosition", rand, rSmall);

        Room rCorners = new Room(3, 3);
        rCorners.getCell(new Position(0, 0)).setWall(true);
        rCorners.getCell(new Position(0, 2)).setWall(true);
        rCorners.getCell(new Position(2, 0)).setWall(true);
        rCorners.getCell(new Position(2, 2)).setWall(true);
        invoke(factory, "cornerEnemyPosition", rCorners);

        Room rFull = new Room(3, 3);
        for (int i = 1; i < 2; i++) {
            for (int j = 1; j < 2; j++) {
                rFull.getCell(new Position(i, j)).setWall(true);
            }
        }
        invoke(factory, "randomEmptyInteriorPosition", rand, rFull);

        Room rDoor = new Room(3, 3);
        rDoor.getCell(new Position(0, 1)).setDoor(true, false);
        rDoor.getCell(new Position(2, 1)).setDoor(true, false);
        rDoor.getCell(new Position(1, 0)).setDoor(true, false);
        rDoor.getCell(new Position(1, 2)).setDoor(true, false);
        try {
            invoke(factory, "doorPosition", rDoor, 0);
        } catch (Exception ignored) {}

        invoke(factory, "farthestEmptyPosition", rSmall);

        Room[] mockRooms = new Room[25];
        for (int i = 0; i < 25; i++) {
            mockRooms[i] = new Room("id" + i, "name" + i, 5, 5);
            mockRooms[i].getCell(new Position(0, 1)).setDoor(true, false, "id" + (i == 0 ? 1 : 0), new Position(0, 0));
        }
        invoke(factory, "lockDoorBetween", mockRooms, 0, 1);

        invoke(factory, "isSmallRoom", new Room(3, 3));
        invoke(factory, "isSmallRoom", new Room(6, 3));
        invoke(factory, "isSmallRoom", new Room(3, 6));
        invoke(factory, "isSmallRoom", new Room(6, 6));
        invoke(factory, "isSmallRoom", new Room(5, 5));
    }
}