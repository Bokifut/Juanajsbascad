package org.example.juegofinalsupremo.io;

import org.example.juegofinalsupremo.data.Lista;
import org.example.juegofinalsupremo.data.RoomGraph;
import org.example.juegofinalsupremo.model.Cell;
import org.example.juegofinalsupremo.model.Enemy;
import org.example.juegofinalsupremo.model.GameObject;
import org.example.juegofinalsupremo.model.GameState;
import org.example.juegofinalsupremo.model.Player;
import org.example.juegofinalsupremo.model.Position;
import org.example.juegofinalsupremo.model.Room;
import org.example.juegofinalsupremo.model.GameLog;
import org.example.juegofinalsupremo.exceptions.GameStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GameJsonRepositoryTest {

    private GameJsonRepository repository;
    private GameState gameState;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        repository = new GameJsonRepository();

        // 1. Crear Habitaciones y Grafo
        Room room0 = new Room("habitacion-0", "Inicio", 5, 5);
        Room room1 = new Room("habitacion-1", "Final", 5, 5);

        // --- POBLAR CELDAS PARA COBERTURA AL 100% ---
        room0.getCell(new Position(0, 1)).setWall(true); // Pared
        room0.getCell(new Position(0, 2)).setDoor(true, true, "habitacion-1", new Position(0,0)); // Puerta abierta
        Cell puertaCerrada = room0.getCell(new Position(0, 3));
        puertaCerrada.setDoor(true, false, null, null); // Puerta cerrada
        puertaCerrada.setRequiredKeyId("llave-1");
        room0.getCell(new Position(1, 1)).setTrapDamage(5); // Trampa
        room0.getCell(new Position(1, 2)).setBlacksmith(true); // Herrero

        Enemy orco = new Enemy("e1", "Orco", 1, Enemy.MELEE, 10, 2, 1, "Medio", false);
        orco.setPhaseTwo(true);
        room0.getCell(new Position(2, 2)).setEnemy(orco); // Enemigo

        GameObject pocion = new GameObject("p1", "Pocion", 10, 0, 0, 0, 0, null, GameObject.TYPE_POTION);
        room0.getCell(new Position(3, 3)).setObject(pocion); // Objeto en el suelo
        // --------------------------------------------

        RoomGraph graph = new RoomGraph();
        graph.addRoom(room0);
        graph.addRoom(room1);
        graph.connect("habitacion-0", "habitacion-1"); // Conexión

        // 2. Jugador con inventario para cubrir equipamiento y daño
        Player player = new Player("Heroe", 20, 5, 0, 1, new Position(0, 0));
        GameObject espada = new GameObject("w1", "Espada", 0, 0, 5, 0, 0, Enemy.MELEE, GameObject.TYPE_WEAPON);
        player.getInventory().add(espada);
        player.equip(espada);
        player.receiveDamage(5); // Forzamos salud < salud máxima

        GameLog log = new GameLog();
        log.add("Comienza la aventura");

        gameState = new GameState(graph, "habitacion-0", "habitacion-1", player, log);

        // 3. Efectos de área
        Lista<Position> area = new Lista<>();
        area.add(new Position(4, 4));
        GameState.AreaEffect effect = new GameState.AreaEffect(area, GameState.AreaEffect.RED, 5, "Fuego", 2);
        effect.trigger();
        gameState.addAreaEffect(effect);
    }

    @Test
    void testSaveAndLoad() throws GameStorageException {
        Path filePath = tempDir.resolve("savegame.json");
        String path = filePath.toString();

        repository.save(gameState, path);
        GameState loadedState = repository.load(path);

        assertNotNull(loadedState);
        assertEquals(gameState.getCurrentRoomId(), loadedState.getCurrentRoomId());
        assertEquals(gameState.getPlayer().getName(), loadedState.getPlayer().getName());
        assertEquals(gameState.getRemainingTurns(), loadedState.getRemainingTurns());
        // Comprobar que cargó las habitaciones y el daño del jugador
        assertTrue(loadedState.getRoomGraph().contains("habitacion-1"));
        assertEquals(15, loadedState.getPlayer().getHealth());
    }

    @Test
    void testToJsonContainsExpectedData() {
        String json = repository.toJson(gameState);

        assertTrue(json.contains("\"version\": 2"));
        assertTrue(json.contains("\"currentRoomId\": \"habitacion-0\""));
        assertTrue(json.contains("\"player\""));
        assertTrue(json.contains("\"rooms\""));
        assertTrue(json.contains("\"connections\""));
        assertTrue(json.contains("\"areaEffects\""));
        assertTrue(json.contains("Orco")); // Enemigo serializado
        assertTrue(json.contains("Fuego")); // Área serializada
    }

    @Test
    void testLoadInvalidFileThrowsException() {
        Path filePath = tempDir.resolve("nonexistent.json");
        assertThrows(GameStorageException.class, () -> repository.load(filePath.toString()));
    }

    @Test
    void testLoadMalformedJsonThrowsException() throws Exception {
        Path filePath = tempDir.resolve("malformed.json");
        Files.write(filePath, "{ \"invalid\": ".getBytes());

        assertThrows(GameStorageException.class, () -> repository.load(filePath.toString()));
    }

    @Test
    void testSaveThrowsExceptionOnInvalidPath() {
        // Pasar un directorio en lugar de un archivo fuerza el fallo de guardado
        assertThrows(GameStorageException.class, () -> repository.save(gameState, tempDir.toString()));
    }

    @Test
    void testLoadLegacyJson() throws Exception {
        // Simulamos un JSON de la versión antigua (sin la clave "rooms")
        String legacyJson = "{ \"version\": 1, \"rows\": 5, \"columns\": 5, " +
                "\"player\": { \"name\": \"Clasico\", \"health\": 20, \"row\": 0, \"column\": 0 }, " +
                "\"cells\": [], \"log\": [] }";
        Path filePath = tempDir.resolve("legacy.json");
        Files.writeString(filePath, legacyJson);

        GameState loadedState = repository.load(filePath.toString());
        assertNotNull(loadedState);
        assertEquals("Clasico", loadedState.getPlayer().getName());
    }

    @Test
    void testLoadWithMissingFieldsForFallbacks() throws Exception {
        // JSON válido pero vacío para forzar que los métodos lean los valores por defecto (fallbacks)
        String emptyJson = "{ \"rooms\": [ { \"id\": \"habitacion-0\" } ] }";
        Path filePath = tempDir.resolve("empty.json");
        Files.writeString(filePath, emptyJson);

        GameState loadedState = repository.load(filePath.toString());

        assertEquals(1, loadedState.getDifficultyIndex()); // Default 1
        assertEquals("Heroe", loadedState.getPlayer().getName()); // Default "Heroe"
    }
}