package org.example.juegofinalsupremo.model;

import org.example.juegofinalsupremo.contracts.GameActions;
import org.example.juegofinalsupremo.data.GridGraph;
import org.example.juegofinalsupremo.data.Lista;
import org.example.juegofinalsupremo.exceptions.GameException;
import org.example.juegofinalsupremo.exceptions.InvalidActionException;
import org.example.juegofinalsupremo.exceptions.InvalidMoveException;

import java.util.Random;

public class GameEngine implements GameActions {
    private final GameState state;
    private final Random random = new Random();
    private final Lista<MovementEvent> movementEvents = new Lista<MovementEvent>();
    private final Lista<PickupEvent> pickupEvents = new Lista<PickupEvent>();

    public GameEngine(GameState state) {
        this.state = state;
        state.initLog();
    }

    public static GameEngine sampleGame() {
        return sampleGame("Heroe");
    }

    public static GameEngine sampleGame(String playerName) {
        return sampleGame(playerName, CharacterClass.GUERRERO);
    }

    public static GameEngine sampleGame(String playerName, CharacterClass characterClass) {
        return sampleGame(playerName, characterClass, 1);
    }

    public static GameEngine sampleGame(String playerName, CharacterClass characterClass, int difficultyIndex) {
        GameState state = new RoomNetworkFactory().createRandomState(playerName, characterClass, difficultyIndex);
        state.getLog().add("Partida iniciada");
        return new GameEngine(state);
    }

    public GameState getState() {
        return state;
    }

    public Lista<Position> reachableCells() {
        return new GridGraph(state.getRoom()).reachable(state.getPlayer().getPosition(), state.getPlayer().getMovementPower());
    }

    public void move(Direction direction) throws GameException {
        ensurePlayable();
        movementEvents.clear();
        if (state.getMovementRemaining() <= 0) {
            state.getLog().add("No quedan movimientos: se pasa turno automaticamente");
            passTurn();
            if (state.isFinished()) {
                return;
            }
        }
        Position previous = state.getPlayer().getPosition();
        Position target = state.getPlayer().getPosition().translate(direction);
        if (!state.getRoom().isValid(target)) {
            throw new InvalidMoveException("Movimiento fuera del tablero");
        }
        Cell cell = state.getRoom().getCell(target);
        if (!cell.isWalkable()) {
            throw new InvalidMoveException("No se puede entrar en " + target);
        }
        state.getPlayer().setPosition(target);
        movementEvents.add(new MovementEvent(previous, target, true));
        state.consumeMovement();
        state.getLog().add("El jugador se mueve a " + target);
        if (cell.hasTrap()) {
            int damage = cell.consumeTrap();
            state.getPlayer().receiveDamage(damage);
            state.getLog().add("Has pisado una trampa, -" + damage + " de vida");
        }
        if (cell.hasRoomTransition()) {
            String previousRoomId = state.getCurrentRoomId();
            String nextRoomId = cell.getTargetRoomId();
            Position nextPosition = cell.getTargetPosition();
            state.changeRoom(nextRoomId, nextPosition);
            openDoorBackTo(previousRoomId);
            state.getLog().add("El jugador entra en " + state.getRoom().getName());
        }
        checkEnd();
        if (!state.isFinished() && state.getMovementRemaining() <= 0 && !state.isActionAvailable()) {
            state.getLog().add("El turno del jugador se agota y pasa al enemigo");
            passTurn();
        }
    }

    public void attack(Direction direction) throws GameException {
        ensurePlayable();
        if (!state.isActionAvailable()) {
            state.getLog().add("No quedan acciones: se pasa turno automaticamente");
            passTurn();
            if (state.isFinished()) {
                return;
            }
        }
        ensureActionAvailable();
        Cell target = adjacentCell(direction);
        Enemy enemy = target.getEnemy();
        if (enemy == null && isPlayerRanged()) {
            target = rangedCell(direction);
            enemy = target == null ? null : target.getEnemy();
        }
        if (enemy == null) {
            throw new InvalidActionException("No hay enemigo en esa celda");
        }
        int damage = state.getPlayer().getAttackPower();
        enemy.receiveDamage(damage);
        state.getLog().add("El jugador ataca a " + enemy.getName() + " por " + damage);
        afterPlayerHitsEnemy(enemy);
        if (!enemy.isAlive()) {
            Position defeatedPosition = target.getPosition();
            clearEnemy(enemy);
            state.getLog().add(enemy.getName() + " ha sido derrotado");
            dropEnemyLoot(enemy, defeatedPosition);
        }
        checkEnd();
        state.consumeAction();
        if (!state.isFinished() && state.getMovementRemaining() <= 0 && !state.isActionAvailable()) {
            state.getLog().add("El turno del jugador se agota y pasa al enemigo");
            passTurn();
        }
    }

    public void pickUp(Direction direction) throws GameException {
        ensurePlayable();
        if (!state.isActionAvailable()) {
            state.getLog().add("No quedan acciones: se pasa turno automaticamente");
            passTurn();
            if (state.isFinished()) {
                return;
            }
        }
        ensureActionAvailable();
        Cell target = adjacentCell(direction);
        if (target.hasBlacksmith()) {
            state.getLog().add("Hablas con el herrero");
            state.consumeAction();
            if (!state.isFinished() && state.getMovementRemaining() <= 0 && !state.isActionAvailable()) {
                state.getLog().add("El turno del jugador se agota y pasa al enemigo");
                passTurn();
            }
            return;
        }
        GameObject object = target.takeObject();
        if (object == null) {
            throw new InvalidActionException("No hay objeto en esa celda");
        }
        pickupEvents.add(new PickupEvent(target.getPosition(), state.getPlayer().getPosition(), object));
        if (object.isCoin()) {
            state.addCoins(object.getCoinValue());
            state.getLog().add("El jugador recoge " + object.getCoinValue() + " moneda" + (object.getCoinValue() == 1 ? "" : "s"));
            state.consumeAction();
            return;
        }
        if (object.isKey() && isObjectIdInInventory(object.getId())) {
            target.setObject(object);
            throw new InvalidActionException("El objeto ya esta en el inventario");
        }
        state.getPlayer().getInventory().add(object);
        state.getLog().add("El jugador recoge " + object.getName());
        state.consumeAction();
        if (!state.isFinished() && state.getMovementRemaining() <= 0 && !state.isActionAvailable()) {
            state.getLog().add("El turno del jugador se agota y pasa al enemigo");
            passTurn();
        }
    }

    public void openDoor(Direction direction) throws GameException {
        ensurePlayable();
        if (!state.isActionAvailable()) {
            state.getLog().add("No quedan acciones: se pasa turno automaticamente");
            passTurn();
            if (state.isFinished()) {
                return;
            }
        }
        ensureActionAvailable();
        Cell target = adjacentCell(direction);
        if (!target.isDoor()) {
            throw new InvalidActionException("No hay puerta en esa celda");
        }
        if (target.getRequiredKeyId() != null && !state.getPlayer().consumeItem(target.getRequiredKeyId())) {
            throw new InvalidActionException("Necesitas " + target.getRequiredKeyId().replace("-", " "));
        }
        target.openDoor();
        state.getLog().add("El jugador abre una puerta");
        state.consumeAction();
        if (!state.isFinished() && state.getMovementRemaining() <= 0 && !state.isActionAvailable()) {
            state.getLog().add("El turno del jugador se agota y pasa al enemigo");
            passTurn();
        }
    }

    public void useInventoryItem(int index) throws GameException {
        ensurePlayable();
        if (index < 0 || index >= state.getPlayer().getInventory().size()) {
            throw new InvalidActionException("Objeto de inventario invalido");
        }
        GameObject object = state.getPlayer().getInventory().get(index);
        if (object.getHealing() <= 0 && object.getMaxHealthBonus() <= 0 && object.getMovementBonus() <= 0 && object.getDefenseBonus() <= 0) {
            throw new InvalidActionException("Ese objeto no es consumible ahora");
        }
        if (!object.isPotion()) {
            throw new InvalidActionException("Ese objeto no es una pocion");
        }
        if (object.getMaxHealthBonus() > 0) {
            state.getPlayer().increaseMaxHealth(object.getMaxHealthBonus());
        }
        state.getPlayer().heal(object.getHealing());
        if (object.getMovementBonus() > 0) {
            state.getPlayer().increaseMovement(object.getMovementBonus());
        }
        if (object.getDefenseBonus() > 0) {
            state.getPlayer().increaseBaseDefense(object.getDefenseBonus());
        }
        state.getPlayer().getInventory().removeAt(index);
        state.getLog().add("El jugador usa " + object.getName());
    }

    public void equipInventoryItem(int index) throws GameException {
        ensurePlayable();
        if (index < 0 || index >= state.getPlayer().getInventory().size()) {
            throw new InvalidActionException("Objeto de inventario invalido");
        }
        GameObject object = state.getPlayer().getInventory().get(index);
        if (!object.isWeapon() && !object.isArmor()) {
            throw new InvalidActionException("Ese objeto no se puede equipar");
        }
        state.getPlayer().equip(object);
        state.getLog().add("Objeto equipado: " + object.getName());
    }

    public void passTurn() throws GameException {
        ensurePlayable();
        movementEvents.clear();
        state.getLog().add("El jugador pasa turno");
        triggerAreaEffectsAtEndOfPlayerTurn();
        runEnemiesTurn();
        if (!state.isFinished()) {
            state.startNextPlayerTurn();
            state.getLog().add("Turno del jugador. Turnos restantes: " + state.getRemainingTurns());
        }
    }

    public void interact(Direction direction) throws GameException {
        ensurePlayable();
        if (!state.isActionAvailable()) {
            state.getLog().add("No quedan acciones: se pasa turno automaticamente");
            passTurn();
            if (state.isFinished()) {
                return;
            }
        }
        ensureActionAvailable();
        Cell target = adjacentCell(direction);
        if (!target.hasBlacksmith()) {
            throw new InvalidActionException("No hay herrero en esa celda");
        }
        state.getLog().add("Hablas con el herrero");
        state.consumeAction();
        if (!state.isFinished() && state.getMovementRemaining() <= 0 && !state.isActionAvailable()) {
            state.getLog().add("El turno del jugador se agota y pasa al enemigo");
            passTurn();
        }
    }

    public void forgeInventoryItem(int index, int forgeLevel, int cost) throws GameException {
        ensurePlayable();
        if (index < 0 || index >= state.getPlayer().getInventory().size()) {
            throw new InvalidActionException("Objeto de inventario invalido");
        }
        GameObject object = state.getPlayer().getInventory().get(index);
        if (!object.isForgeable()) {
            throw new InvalidActionException("Ese objeto no se puede forjar");
        }
        if (!state.spendCoins(cost)) {
            throw new InvalidActionException("No tienes monedas suficientes");
        }
        GameObject forged = object.forged(forgeLevel);
        state.getPlayer().getInventory().set(index, forged);
        if (object == state.getPlayer().getEquippedWeapon() || object == state.getPlayer().getEquippedArmor()) {
            state.getPlayer().equip(forged);
        }
        state.getLog().add("Herrero: " + object.getName() + " pasa a " + forged.getName());
    }

    public void sellInventoryItem(int index) throws GameException {
        ensurePlayable();
        if (index < 0 || index >= state.getPlayer().getInventory().size()) {
            throw new InvalidActionException("Objeto de inventario invalido");
        }
        GameObject object = state.getPlayer().getInventory().get(index);
        if (object.getId().startsWith("starter-")) {
            throw new InvalidActionException("No puedes vender el arma inicial del personaje");
        }
        int price = sellPrice(object);
        if (price <= 0) {
            throw new InvalidActionException("El herrero no compra ese objeto");
        }
        state.getPlayer().unequip(object);
        state.getPlayer().getInventory().removeAt(index);
        state.addCoins(price);
        state.getLog().add("Herrero: vendes " + object.getName() + " por " + price + " moneda" + (price == 1 ? "" : "s"));
    }

    public void buyShopItem(String itemId) throws GameException {
        ensurePlayable();
        GameObject object = shopObject(itemId);
        int price = buyPrice(itemId);
        if (object == null || price <= 0) {
            throw new InvalidActionException("Objeto de tienda invalido");
        }
        if (!state.spendCoins(price)) {
            throw new InvalidActionException("No tienes monedas suficientes");
        }
        state.getPlayer().getInventory().add(object);
        state.getLog().add("Herrero: compras " + object.getName() + " por " + price + " monedas");
    }

    public Lista<MovementEvent> consumeMovementEvents() {
        Lista<MovementEvent> copy = new Lista<MovementEvent>();
        for (MovementEvent event : movementEvents) {
            copy.add(event);
        }
        movementEvents.clear();
        return copy;
    }

    public Lista<PickupEvent> consumePickupEvents() {
        Lista<PickupEvent> copy = new Lista<PickupEvent>();
        for (PickupEvent event : pickupEvents) {
            copy.add(event);
        }
        pickupEvents.clear();
        return copy;
    }

    public int sellPrice(GameObject object) {
        if (object == null || object.getId().startsWith("starter-")) {
            return 0;
        }
        String name = object.getName().toLowerCase();
        if (object.isWeapon()) {
            if (name.contains("celestial")) {
                return 10;
            }
            if (name.contains("hacha")) {
                return 4;
            }
            if (name.contains("arco")) {
                return 3;
            }
            if (name.contains("espada")) {
                return 2;
            }
        }
        if (object.isArmor()) {
            if (name.contains("celestial")) {
                return 10;
            }
            if (name.contains("hierro") || name.contains("hermes")) {
                return 4;
            }
            if (name.contains("tela") || name.contains("roja")) {
                return 2;
            }
        }
        if (object.isPotion()) {
            if (name.contains("velocidad")) {
                return 5;
            }
            if (name.contains("defensa")) {
                return 3;
            }
            if (name.contains("maxima") || name.contains("max")) {
                return 2;
            }
            if (name.contains("vida")) {
                return 1;
            }
        }
        return 0;
    }

    public int buyPrice(String itemId) {
        if ("potion-health".equals(itemId)) {
            return 7;
        }
        if ("potion-max".equals(itemId)) {
            return 15;
        }
        if ("potion-defense".equals(itemId)) {
            return 25;
        }
        if ("potion-speed".equals(itemId)) {
            return 35;
        }
        if ("weapon-arco-celestial".equals(itemId)
                || "weapon-espada-celestial".equals(itemId)
                || "armor-celestial".equals(itemId)) {
            return 45;
        }
        return 0;
    }

    public GameObject shopObject(String itemId) {
        if ("potion-health".equals(itemId)) {
            return potionHealth("shop-potion-health-" + System.nanoTime());
        }
        if ("potion-max".equals(itemId)) {
            return potionMax("shop-potion-max-" + System.nanoTime());
        }
        if ("potion-defense".equals(itemId)) {
            return potionDefense("shop-potion-defense-" + System.nanoTime());
        }
        if ("potion-speed".equals(itemId)) {
            return potionSpeed("shop-potion-speed-" + System.nanoTime());
        }
        if ("weapon-arco-celestial".equals(itemId)) {
            return weapon("shop-arco-celestial-" + System.nanoTime(), "Arco Celestial", 1, Enemy.RANGED);
        }
        if ("weapon-espada-celestial".equals(itemId)) {
            return weapon("shop-espada-celestial-" + System.nanoTime(), "Espada Celestial", 5, Enemy.MELEE);
        }
        if ("armor-celestial".equals(itemId)) {
            return armor("shop-armadura-celestial-" + System.nanoTime(), "Armadura Celestial", 10, 4, 1);
        }
        return null;
    }

    private void dropEnemyLoot(Enemy enemy, Position defeatedPosition) {
        int coinValue = coinDropFor(enemy);
        if (coinValue > 0) {
            state.addCoins(coinValue);
            state.getLog().add(enemy.getName() + " suelta " + coinValue + " moneda" + (coinValue == 1 ? "" : "s"));
        }
        Lista<GameObject> extras = enemyExtraDrops(enemy);
        for (int i = 0; i < extras.size(); i++) {
            placeNear(defeatedPosition, extras.get(i));
        }
    }

    private int coinDropFor(Enemy enemy) {
        if (enemy.getId().contains("enemy-1")) {
            return 1;
        }
        if (enemy.getId().contains("enemy-2")) {
            return 2;
        }
        if (enemy.getId().contains("enemy-3")) {
            return 3;
        }
        if (enemy.getId().equals("enemy-4-boss")) {
            return 5;
        }
        return 0;
    }

    private Lista<GameObject> enemyExtraDrops(Enemy enemy) {
        Lista<GameObject> drops = new Lista<GameObject>();
        int roll = random.nextInt(100);
        if (enemy.getId().equals("enemy-4-boss")) {
            drops.add(key("llave-demoniaca", "Llave Demoniaca"));
            if (roll < 42) {
                drops.add(potionMax("drop-boss-max"));
            } else if (roll < 72) {
                drops.add(potionDefense("drop-boss-defense"));
            } else if (roll < 92) {
                drops.add(weapon("drop-boss-hacha", "Hacha De Guerra", 3, Enemy.MELEE));
            } else if (roll < 97) {
                drops.add(potionSpeed("drop-boss-speed"));
            } else if (roll < 98) {
                drops.add(weapon("drop-boss-arco-celestial", "Arco Celestial", 1, Enemy.RANGED));
            } else if (roll < 99) {
                drops.add(armor("drop-boss-armadura-celestial", "Armadura Celestial", 10, 4, 1));
            } else {
                drops.add(weapon("drop-boss-espada-celestial", "Espada Celestial", 5, Enemy.MELEE));
            }
            return drops;
        }
        if (enemy.getId().equals("enemy-5-final-boss")) {
            drops.add(key("llave-celestial", "Llave Celestial"));
            return drops;
        }
        if (enemy.getId().contains("enemy-1")) {
            if (roll < 10) {
                drops.add(potionHealth("drop-rata-vida"));
            } else if (roll < 15) {
                drops.add(potionMax("drop-rata-max"));
            }
        } else if (enemy.getId().contains("enemy-2")) {
            if (roll < 20) {
                drops.add(weapon("drop-goblin-arco", "Arco De Madera", -1, Enemy.RANGED));
            } else if (roll < 33) {
                drops.add(potionHealth("drop-goblin-vida"));
            } else if (roll < 40) {
                drops.add(potionMax("drop-goblin-max"));
            }
        } else if (enemy.getId().contains("enemy-3")) {
            if (roll < 20) {
                drops.add(potionMax("drop-dragon-max"));
            } else if (roll < 30) {
                drops.add(potionDefense("drop-dragon-defense"));
            } else if (roll < 35) {
                drops.add(potionSpeed("drop-dragon-speed"));
            }
        }
        return drops;
    }

    private void placeNear(Position origin, GameObject object) {
        Direction[] directions = Direction.values();
        for (int i = 0; i < directions.length; i++) {
            Position position = origin.translate(directions[i]);
            if (state.getRoom().isValid(position) && state.getRoom().getCell(position).isEmpty()) {
                state.getRoom().getCell(position).setObject(object);
                return;
            }
        }
    }

    private GameObject key(String id, String name) {
        return new GameObject(id, name, 0, 0, 0, 0, 0, null, GameObject.TYPE_KEY);
    }

    private GameObject weapon(String id, String name, int attackBonus, String combatType) {
        return new GameObject(id, name, 0, 0, attackBonus, 0, 0, combatType, GameObject.TYPE_WEAPON);
    }

    private GameObject armor(String id, String name, int maxHealthBonus, int defenseBonus, int movementBonus) {
        return new GameObject(id, name, 0, maxHealthBonus, 0, defenseBonus, movementBonus, null, GameObject.TYPE_ARMOR);
    }

    private GameObject potionHealth(String id) {
        return new GameObject(id, "Pocion De Vida", 10, 0, 0, 0, 0, null, GameObject.TYPE_POTION);
    }

    private GameObject potionMax(String id) {
        return new GameObject(id, "Pocion De Vida Maxima", 5, 5, 0, 0, 0, null, GameObject.TYPE_POTION);
    }

    private GameObject potionDefense(String id) {
        return new GameObject(id, "Pocion De Defensa", 0, 0, 0, 1, 0, null, GameObject.TYPE_POTION);
    }

    private GameObject potionSpeed(String id) {
        return new GameObject(id, "Pocion De Velocidad", 0, 0, 0, 0, 1, null, GameObject.TYPE_POTION);
    }

    private Cell adjacentCell(Direction direction) throws InvalidActionException {
        Position target = state.getPlayer().getPosition().translate(direction);
        if (!state.getRoom().isValid(target)) {
            throw new InvalidActionException("La celda contigua no existe");
        }
        return state.getRoom().getCell(target);
    }

    private Cell rangedCell(Direction direction) {
        Position target = state.getPlayer().getPosition();
        for (int distance = 0; distance < 3; distance++) {
            target = target.translate(direction);
            if (!state.getRoom().isValid(target)) {
                return null;
            }
            Cell cell = state.getRoom().getCell(target);
            if (cell.getEnemy() != null) {
                return cell;
            }
            if (cell.isWall() || cell.isDoor()) {
                return null;
            }
        }
        return null;
    }

    private boolean isPlayerRanged() {
        GameObject weapon = state.getPlayer().getEquippedWeapon();
        return weapon != null && Enemy.RANGED.equals(weapon.getWeaponCombatType());
    }

    private boolean isObjectIdInInventory(String id) {
        for (int i = 0; i < state.getPlayer().getInventory().size(); i++) {
            if (state.getPlayer().getInventory().get(i).getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private void openDoorBackTo(String previousRoomId) {
        Room room = state.getRoom();
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Cell door = room.getCell(new Position(row, column));
                if (door.isDoor() && previousRoomId.equals(door.getTargetRoomId())) {
                    if (state.getCurrentRoomId().equals(state.getFinalRoomId())) {
                        door.clearDoor();
                        return;
                    }
                    door.openDoor();
                    return;
                }
            }
        }
    }

    private void ensurePlayable() throws InvalidActionException {
        if (state.isFinished()) {
            throw new InvalidActionException("La partida ya ha terminado");
        }
    }

    private void ensureActionAvailable() throws InvalidActionException {
        if (!state.isActionAvailable()) {
            throw new InvalidActionException("Ya has usado la accion extra este turno");
        }
    }

    private void checkEnd() {
        if (state.getPlayer().getHealth() <= 0) {
            state.finish(false);
            state.getLog().add("Fin de partida: derrota");
            return;
        }
        if (state.getCurrentRoomId().equals(state.getFinalRoomId())
                && state.getPlayer().getPosition().equals(new Position(0, state.getRoom().getColumns() - 1))) {
            state.finish(true);
            state.getLog().add("Fin de partida: victoria");
        }
    }

    private void runEnemiesTurn() {
        state.getLog().add("Turno de enemigos");
        Lista<Position> enemies = enemyPositions();
        for (int i = 0; i < enemies.size(); i++) {
            Position enemyPosition = enemies.get(i);
            if (!state.getRoom().isValid(enemyPosition)) {
                continue;
            }
            Cell enemyCell = state.getRoom().getCell(enemyPosition);
            Enemy enemy = enemyCell.getEnemy();
            if (enemy == null) {
                continue;
            }
            updateFinalBossPhase(enemy);
            if (enemy.getSkipTurns() > 0) {
                enemy.consumeSkipTurn();
                state.getLog().add(enemy.getName() + " permanece inmovil");
                continue;
            }
            if (executeFinalJudgmentIfReady(enemy, enemyPosition)) {
                checkEnd();
                if (state.isFinished()) {
                    return;
                }
                continue;
            }
            if (tryBossSpecialInsteadOfAttack(enemy, enemyPosition)) {
                continue;
            }
            tryEnemyHeal(enemy);
            boolean rangedThisTurn = shouldFinalBossUseRay(enemy);
            if (isInEnemyAttackRange(enemyPosition, enemy)) {
                enemyAttack(enemy, rangedThisTurn);
                checkEnd();
                if (state.isFinished()) {
                    return;
                }
            } else {
                Position current = enemyPosition;
                for (int step = 0; step < effectiveEnemySpeed(enemy); step++) {
                    Position next = isFinalBoss(enemy) ? bestLargeBossStep(current, enemy) : bestEnemyStepByPath(current);
                    if (next == null) {
                        break;
                    }
                    moveEnemyTo(current, next, enemy);
                    movementEvents.add(new MovementEvent(current, next, false));
                    current = next;
                    state.getLog().add(enemy.getName() + " se mueve a " + next);
                    if (isInEnemyAttackRange(current, enemy) || (rangedThisTurn && distanceToPlayer(current, enemy) <= 2)) {
                        break;
                    }
                }
                if (state.getRoom().isValid(current) && (isInEnemyAttackRange(current, enemy) || (rangedThisTurn && distanceToPlayer(current, enemy) <= 2))) {
                    enemyAttack(enemy, rangedThisTurn);
                    checkEnd();
                    if (state.isFinished()) {
                        return;
                    }
                }
            }
        }
    }

    private Lista<Position> enemyPositions() {
        Lista<Position> positions = new Lista<Position>();
        Lista<Enemy> seen = new Lista<Enemy>();
        Room room = state.getRoom();
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Position position = new Position(row, column);
                Enemy enemy = room.getCell(position).getEnemy();
                if (enemy != null && !containsEnemyReference(seen, enemy)) {
                    seen.add(enemy);
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    private void afterPlayerHitsEnemy(Enemy enemy) {
        if (enemy.getId().contains("enemy-1") && difficulty() >= 1) {
            int chance = difficulty() == 1 ? 10 : difficulty() == 2 ? 15 : 20;
            if (random.nextInt(100) < chance) {
                summonRatNear(findEnemy(enemy));
                state.getLog().add("Habilidad llamada: aparece otra rata");
            }
        }
        updateFinalBossPhase(enemy);
    }

    private void triggerAreaEffectsAtEndOfPlayerTurn() {
        for (GameState.AreaEffect effect : state.getAreaEffects()) {
            if (GameState.AreaEffect.WHITE.equals(effect.getColor())) {
                continue;
            }
            if (!effect.isExpired() && effect.contains(state.getPlayer().getPosition())) {
                state.getPlayer().receiveDamage(effect.getDamage());
                state.getLog().add(effect.getMessage());
                effect.trigger();
            } else {
                effect.tick();
            }
        }
        state.clearExpiredAreaEffects();
        checkEnd();
    }

    private int effectiveEnemySpeed(Enemy enemy) {
        if (enemy.getId().contains("enemy-1") && difficulty() >= 2 && enemy.getHealth() <= 3) {
            return enemy.getSpeed() + 1;
        }
        if (enemy.getId().equals("enemy-5-final-boss") && enemy.isPhaseTwo()) {
            return 2;
        }
        return enemy.getSpeed();
    }

    private void enemyAttack(Enemy enemy, boolean rangedRay) {
        int rawAttack = rangedRay ? finalBossRayDamage(enemy) : enemy.getAttack();
        boolean ignoreDefense = enemy.getId().contains("enemy-2") && difficulty() >= 1
                && random.nextInt(100) < (difficulty() == 1 ? 15 : difficulty() == 2 ? 25 : 35);
        int defense = ignoreDefense ? 0 : Math.max(0, state.getPlayer().getDefensePower() - state.getEffectiveDefensePenalty());
        int damage = Math.max(0, rawAttack - defense);
        state.getPlayer().receiveDamage(damage);
        state.getLog().add(enemy.getName() + (rangedRay ? " usa rayo infernal por " : " ataca por ") + damage);
        if (ignoreDefense) {
            state.getLog().add("Flechazo perforante ignora tu defensa");
        }
        if (enemy.getId().equals("enemy-4-boss") && difficulty() >= 2 && random.nextInt(100) < 25) {
            int extra = Math.max(0, 5 - defense);
            state.getPlayer().receiveDamage(extra);
            state.getLog().add("Disparo doble: -" + extra + " de vida");
        }
        if (enemy.getId().equals("enemy-5-final-boss") && difficulty() >= 1) {
            int chance = finalBossAviolentacionChance(enemy);
            if (chance > 0 && random.nextInt(100) < chance) {
                state.setAviolentadoTurns(2);
                state.getLog().add("Aviolentacion: pierdes 3 de armadura durante 2 turnos");
            }
        }
        afterEnemyAttack(enemy);
        Position enemyPosition = findEnemy(enemy);
        if (enemyPosition != null && enemy.getId().contains("enemy-2") && difficulty() >= 2
                && random.nextInt(100) < (difficulty() == 2 ? 30 : 40)) {
            moveAway(enemyPosition, enemy);
            state.getLog().add("Huida: el goblin se aleja");
        }
    }

    private void afterEnemyAttack(Enemy enemy) {
        Position enemyPosition = findEnemy(enemy);
        if (enemyPosition == null) {
            return;
        }
        if (enemy.getId().contains("enemy-3") && difficulty() >= 1
                && random.nextInt(100) < (difficulty() == 1 ? 25 : difficulty() == 2 ? 30 : 40)) {
            addConeEffect(enemyPosition, GameState.AreaEffect.RED, 5, "Cuidado, te has quemado, -5 de vida");
        }
        if (enemy.getId().equals("enemy-4-boss")) {
            int grenade = difficulty() == 0 ? 20 : difficulty() == 1 ? 25 : difficulty() == 2 ? 30 : 35;
            int machine = difficulty() == 0 ? 20 : difficulty() == 1 ? 25 : difficulty() == 2 ? 30 : 35;
            if (random.nextInt(100) < grenade) {
                addSquareEffect(state.getPlayer().getPosition(), 1, GameState.AreaEffect.YELLOW, 3,
                        "Cuidado, lanzan granadas, -3 de vida");
            }
            if (random.nextInt(100) < machine) {
                addLineEffect(enemyPosition, GameState.AreaEffect.GREEN, 6,
                        "Cuidado con la ametralladora,-2 -2 -2 de vida");
            }
            int lateral = difficulty() == 0 ? 30 : difficulty() == 1 ? 35 : difficulty() == 2 ? 40 : 50;
            if (random.nextInt(100) < lateral) {
                moveAway(enemyPosition, enemy);
            }
        }
        if (enemy.getId().equals("enemy-5-final-boss")) {
            int earthquake = difficulty() == 0 ? 25 : difficulty() == 1 ? 30 : difficulty() == 2 ? 35 : 40;
            if (random.nextInt(100) < earthquake) {
                addSquareEffect(state.getPlayer().getPosition(), 1, GameState.AreaEffect.YELLOW, 7,
                        "Has sufrido un terremoto, -7 de vida");
            }
        }
    }

    private boolean tryBossSpecialInsteadOfAttack(Enemy enemy, Position position) {
        if (enemy.getId().equals("enemy-4-boss") && difficulty() >= 1) {
            int chance = difficulty() == 1 ? 15 : difficulty() == 2 ? 20 : 25;
            if (random.nextInt(100) < chance) {
                summonNear(position, 2, "Goblin invocado", 8 + Math.max(0, difficulty() - 1), 4, Enemy.RANGED, true);
                state.getLog().add("Invocacion goblin: aparece un arquero invocado");
                return true;
            }
        }
        if (enemy.getId().equals("enemy-5-final-boss")) {
            int help = difficulty() == 0 ? 10 : difficulty() == 1 ? 15 : 25;
            if (random.nextInt(100) < help) {
                int hp = enemy.isPhaseTwo() ? 10 + difficulty() * 2 : 8 + difficulty() * 2;
                int atk = enemy.isPhaseTwo() ? 5 + Math.max(0, difficulty() - 1) : 5 + Math.max(0, difficulty() - 1);
                summonNear(state.getPlayer().getPosition(), 3, "Dragon invocado", hp, atk, Enemy.MELEE, true);
                summonNear(state.getPlayer().getPosition(), -3, "Dragon invocado", hp, atk, Enemy.MELEE, true);
                moveAway(position, enemy);
                moveAway(findEnemy(enemy), enemy);
                enemy.setSkipTurns(1);
                state.getLog().add("Solicitar ayuda: Marquette invoca dragones y queda AFK");
                return true;
            }
            if (difficulty() >= 2 && random.nextInt(100) < (enemy.isPhaseTwo() ? (difficulty() == 2 ? 20 : 25) : (difficulty() == 2 ? 15 : 20))) {
                enemy.setImmunePlayerTurns(2);
                enemy.setFinalJudgmentCountdown(3);
                addSquareEffect(position, 4, GameState.AreaEffect.WHITE, 500,
                        state.getPlayer().getName() + " ha sido ejecutado por el Rey Demonio Marquette");
                state.getLog().add("Juicio Final: Marquette se vuelve inmune y marca la zona blanca");
                return true;
            }
            if (enemy.isPhaseTwo()) {
                int teleport = difficulty() == 0 ? 20 : difficulty() == 1 ? 25 : difficulty() == 2 ? 30 : 35;
                if (random.nextInt(100) < teleport) {
                    teleportNearPlayer(position, enemy);
                }
                int heal = difficulty() == 0 ? 30 : difficulty() == 1 ? 35 : difficulty() == 2 ? 40 : 45;
                if (random.nextInt(100) < heal) {
                    enemy.heal(4);
                    state.getLog().add("Curar: Marquette recupera 4 de vida");
                }
            }
        }
        return false;
    }

    private boolean executeFinalJudgmentIfReady(Enemy enemy, Position position) {
        if (!enemy.getId().equals("enemy-5-final-boss") || enemy.getFinalJudgmentCountdown() <= 0) {
            return false;
        }
        enemy.consumeFinalJudgmentCountdown();
        if (enemy.getFinalJudgmentCountdown() == 0) {
            for (GameState.AreaEffect effect : state.getAreaEffects()) {
                if (GameState.AreaEffect.WHITE.equals(effect.getColor()) && effect.contains(state.getPlayer().getPosition())) {
                    state.getPlayer().receiveDamage(500);
                    state.getLog().add(effect.getMessage());
                    effect.trigger();
                    state.clearExpiredAreaEffects();
                    break;
                }
            }
        }
        return true;
    }

    private void tryEnemyHeal(Enemy enemy) {
        if (enemy.getId().contains("enemy-3") && difficulty() >= 2 && enemy.getHealth() <= 7
                && random.nextInt(100) < (difficulty() == 2 ? 40 : 50)) {
            enemy.heal(3);
            state.getLog().add("Curar: el dragon recupera 3 de vida");
        }
    }

    private boolean shouldFinalBossUseRay(Enemy enemy) {
        return enemy.getId().equals("enemy-5-final-boss") && random.nextInt(100) < 25;
    }

    private int finalBossRayDamage(Enemy enemy) {
        if (!enemy.getId().equals("enemy-5-final-boss")) {
            return enemy.getAttack();
        }
        if (enemy.isPhaseTwo()) {
            int[] phase = {12, 14, 16, 20};
            return phase[difficulty()];
        }
        int[] base = {8, 10, 16, 16};
        return base[difficulty()];
    }

    private int finalBossAviolentacionChance(Enemy enemy) {
        if (!enemy.getId().equals("enemy-5-final-boss") || difficulty() == 0) {
            return 0;
        }
        if (!enemy.isPhaseTwo()) {
            return difficulty() == 1 ? 25 : difficulty() == 2 ? 30 : 35;
        }
        return difficulty() == 1 ? 30 : difficulty() == 2 ? 35 : 40;
    }

    private void updateFinalBossPhase(Enemy enemy) {
        if (!enemy.getId().equals("enemy-5-final-boss") || enemy.isPhaseTwo()) {
            return;
        }
        int[] half = {20, 23, 26, 30};
        if (enemy.getHealth() <= half[difficulty()]) {
            enemy.setPhaseTwo(true);
            int[] damage = {15, 18, 20, 25};
            enemy.setAttack(damage[difficulty()]);
            state.getLog().add("Marquette esta enfadado");
        }
    }

    private int difficulty() {
        return state.getDifficultyIndex();
    }

    private Position findEnemy(Enemy target) {
        Room room = state.getRoom();
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Position position = new Position(row, column);
                if (room.getCell(position).getEnemy() == target) {
                    return position;
                }
            }
        }
        return null;
    }

    private int distanceToPlayer(Position position) {
        return distanceToPlayer(position, state.getRoom().getCell(position).getEnemy());
    }

    private int distanceToPlayer(Position position, Enemy enemy) {
        if (isFinalBoss(enemy)) {
            return distanceFromLargeBossToPlayer(position);
        }
        return Math.abs(position.getRow() - state.getPlayer().getPosition().getRow())
                + Math.abs(position.getColumn() - state.getPlayer().getPosition().getColumn());
    }

    private void addSquareEffect(Position center, int radius, String color, int damage, String message) {
        Lista<Position> cells = new Lista<Position>();
        Room room = state.getRoom();
        for (int row = center.getRow() - radius; row <= center.getRow() + radius; row++) {
            for (int column = center.getColumn() - radius; column <= center.getColumn() + radius; column++) {
                Position position = new Position(row, column);
                if (room.isValid(position)) {
                    cells.add(position);
                }
            }
        }
        state.addAreaEffect(new GameState.AreaEffect(cells, color, damage, message, 2));
        state.getLog().add(message);
    }

    private void addLineEffect(Position enemyPosition, String color, int damage, String message) {
        Lista<Position> cells = new Lista<Position>();
        Direction direction = directionToward(enemyPosition, state.getPlayer().getPosition());
        Position current = enemyPosition;
        for (int i = 0; i < 3; i++) {
            current = current.translate(direction);
            if (state.getRoom().isValid(current)) {
                cells.add(current);
            }
        }
        state.addAreaEffect(new GameState.AreaEffect(cells, color, damage, message, 2));
        state.getLog().add(message);
    }

    private void addConeEffect(Position enemyPosition, String color, int damage, String message) {
        Lista<Position> cells = new Lista<Position>();
        Direction direction = directionToward(enemyPosition, state.getPlayer().getPosition());
        Direction sideA = direction == Direction.UP || direction == Direction.DOWN ? Direction.LEFT : Direction.UP;
        Direction sideB = direction == Direction.UP || direction == Direction.DOWN ? Direction.RIGHT : Direction.DOWN;
        Position front = enemyPosition.translate(direction);
        Position front2 = front.translate(direction);
        Position[] positions = {front, front.translate(sideA), front.translate(sideB),
                front2, front2.translate(sideA), front2.translate(sideB)};
        for (Position position : positions) {
            if (state.getRoom().isValid(position)) {
                cells.add(position);
            }
        }
        state.addAreaEffect(new GameState.AreaEffect(cells, color, damage, message, 2));
        state.getLog().add(message);
    }

    private Direction directionToward(Position from, Position to) {
        int rowDelta = to.getRow() - from.getRow();
        int columnDelta = to.getColumn() - from.getColumn();
        if (Math.abs(rowDelta) >= Math.abs(columnDelta)) {
            return rowDelta >= 0 ? Direction.DOWN : Direction.UP;
        }
        return columnDelta >= 0 ? Direction.RIGHT : Direction.LEFT;
    }

    private void moveAway(Position enemyPosition, Enemy enemy) {
        if (enemyPosition == null) {
            return;
        }
        Position player = state.getPlayer().getPosition();
        Direction best = null;
        int bestDistance = distanceToPlayer(enemyPosition, enemy);
        for (Direction direction : Direction.values()) {
            Position next = enemyPosition.translate(direction);
            if (canMoveEnemyTo(next, enemy)) {
                int distance = isFinalBoss(enemy)
                        ? distanceFromLargeBossToPlayer(next)
                        : Math.abs(next.getRow() - player.getRow()) + Math.abs(next.getColumn() - player.getColumn());
                if (distance > bestDistance) {
                    bestDistance = distance;
                    best = direction;
                }
            }
        }
        if (best != null) {
            Position next = enemyPosition.translate(best);
            moveEnemyTo(enemyPosition, next, enemy);
            movementEvents.add(new MovementEvent(enemyPosition, next, false));
        }
    }

    private void teleportNearPlayer(Position enemyPosition, Enemy enemy) {
        if (enemyPosition == null) {
            return;
        }
        Position player = state.getPlayer().getPosition();
        for (int attempt = 0; attempt < 40; attempt++) {
            int row = player.getRow() - 3 + random.nextInt(7);
            int column = player.getColumn() - 3 + random.nextInt(7);
            Position target = new Position(row, column);
            if (canMoveEnemyTo(target, enemy) && distance(player, bossCenter(target, enemy)) == 3) {
                moveEnemyTo(enemyPosition, target, enemy);
                state.getLog().add("Teletransportacion: Marquette cambia de posicion");
                return;
            }
        }
    }

    private int distance(Position first, Position second) {
        return Math.abs(first.getRow() - second.getRow()) + Math.abs(first.getColumn() - second.getColumn());
    }

    private void summonEnemyAtBorder(int type, String name, boolean summoned) {
        Room room = state.getRoom();
        for (int attempt = 0; attempt < 60; attempt++) {
            int side = random.nextInt(4);
            int row = side == 0 ? 0 : side == 1 ? room.getRows() - 1 : random.nextInt(room.getRows());
            int column = side == 2 ? 0 : side == 3 ? room.getColumns() - 1 : random.nextInt(room.getColumns());
            Position position = new Position(row, column);
            if (room.getCell(position).isWalkable()) {
                room.getCell(position).setEnemy(new Enemy("enemy-" + type + "-called-" + System.nanoTime(),
                        name, 1, type == 2 ? Enemy.RANGED : Enemy.MELEE, 4, 3, 1, difficultyName(), summoned));
                return;
            }
        }
    }

    private void summonRatNear(Position origin) {
        if (origin == null) {
            return;
        }
        Position[] candidates = {
                new Position(origin.getRow() - 3, origin.getColumn()),
                new Position(origin.getRow() + 3, origin.getColumn()),
                new Position(origin.getRow(), origin.getColumn() - 3),
                new Position(origin.getRow(), origin.getColumn() + 3)
        };
        for (Position position : candidates) {
            if (state.getRoom().isValid(position) && state.getRoom().getCell(position).isWalkable()) {
                state.getRoom().getCell(position).setEnemy(new Enemy("enemy-1-called-" + System.nanoTime(),
                        "Rata llamada", 1, Enemy.MELEE, 4, 3, 1, difficultyName(), false));
                return;
            }
        }
    }

    private void summonNear(Position origin, int offset, String name, int health, int attack, String combatType, boolean summoned) {
        Position[] candidates = {
                new Position(origin.getRow(), origin.getColumn() + offset),
                new Position(origin.getRow() + offset, origin.getColumn()),
                new Position(origin.getRow(), origin.getColumn() - offset),
                new Position(origin.getRow() - offset, origin.getColumn())
        };
        for (Position position : candidates) {
            if (state.getRoom().isValid(position) && state.getRoom().getCell(position).isWalkable()) {
                String type = Enemy.RANGED.equals(combatType) ? "2" : "3";
                state.getRoom().getCell(position).setEnemy(new Enemy("enemy-" + type + "-summoned-" + System.nanoTime(),
                        name, 1, combatType, health, attack, Enemy.RANGED.equals(combatType) ? 1 : 2,
                        difficultyName(), summoned));
                return;
            }
        }
    }

    private String difficultyName() {
        String[] names = {"Facil", "Medio", "Dificil", "Marquette"};
        return names[difficulty()];
    }

    private boolean isAdjacent(Position first, Position second) {
        int distance = Math.abs(first.getRow() - second.getRow()) + Math.abs(first.getColumn() - second.getColumn());
        return distance == 1;
    }

    private boolean isInEnemyAttackRange(Position enemyPosition, Enemy enemy) {
        int distance = isFinalBoss(enemy)
                ? distanceFromLargeBossToPlayer(enemyPosition)
                : Math.abs(enemyPosition.getRow() - state.getPlayer().getPosition().getRow())
                + Math.abs(enemyPosition.getColumn() - state.getPlayer().getPosition().getColumn());
        int range = Enemy.RANGED.equals(enemy.getCombatType()) ? 2 : 1;
        return distance <= range;
    }

    private void moveEnemyTowardsPlayer(Position enemyPosition, Enemy enemy) {
        Position playerPosition = state.getPlayer().getPosition();
        Position next = bestEnemyStep(enemyPosition, playerPosition);
        if (next == null || !state.getRoom().isValid(next) || next.equals(playerPosition)) {
            return;
        }
        Cell nextCell = state.getRoom().getCell(next);
        if (!nextCell.isWalkable()) {
            return;
        }
        moveEnemyTo(enemyPosition, next, enemy);
        state.getLog().add(enemy.getName() + " se mueve a " + next);
    }

    private Position bestEnemyStepByPath(Position enemyPosition) {
        Room room = state.getRoom();
        Position playerPosition = state.getPlayer().getPosition();
        org.example.juegofinalsupremo.data.Cola<Position> queue = new org.example.juegofinalsupremo.data.Cola<Position>();
        Lista<Position> previousPosition = new Lista<Position>();
        Lista<Position> previousFrom = new Lista<Position>();
        boolean[][] visited = new boolean[room.getRows()][room.getColumns()];
        queue.enqueue(enemyPosition);
        visited[enemyPosition.getRow()][enemyPosition.getColumn()] = true;
        while (!queue.isEmpty()) {
            Position current = queue.dequeue();
            if (isAdjacent(current, playerPosition)) {
                return firstStep(enemyPosition, current, previousPosition, previousFrom);
            }
            Direction[] directions = Direction.values();
            for (int i = 0; i < directions.length; i++) {
                Position next = current.translate(directions[i]);
                if (room.isValid(next) && !visited[next.getRow()][next.getColumn()]
                        && !next.equals(playerPosition) && room.getCell(next).isWalkable()) {
                    visited[next.getRow()][next.getColumn()] = true;
                    queue.enqueue(next);
                    previousPosition.add(next);
                    previousFrom.add(current);
                }
            }
        }
        return bestEnemyStep(enemyPosition, playerPosition);
    }

    private Position firstStep(Position start, Position target, Lista<Position> previousPosition, Lista<Position> previousFrom) {
        Position current = target;
        Position previous = null;
        while (!current.equals(start)) {
            previous = current;
            int index = previousPosition.indexOf(current);
            if (index < 0) {
                return null;
            }
            current = previousFrom.get(index);
        }
        return previous;
    }

    private Position bestEnemyStep(Position enemyPosition, Position playerPosition) {
        int rowDelta = Integer.compare(playerPosition.getRow(), enemyPosition.getRow());
        int columnDelta = Integer.compare(playerPosition.getColumn(), enemyPosition.getColumn());
        Position rowStep = rowDelta == 0 ? null : new Position(enemyPosition.getRow() + rowDelta, enemyPosition.getColumn());
        Position columnStep = columnDelta == 0 ? null : new Position(enemyPosition.getRow(), enemyPosition.getColumn() + columnDelta);
        if (canEnemyMoveTo(rowStep, playerPosition)) {
            return rowStep;
        }
        if (canEnemyMoveTo(columnStep, playerPosition)) {
            return columnStep;
        }
        return null;
    }

    private boolean canEnemyMoveTo(Position position, Position playerPosition) {
        return position != null
                && !position.equals(playerPosition)
                && state.getRoom().isValid(position)
                && state.getRoom().getCell(position).isWalkable();
    }

    private boolean isFinalBoss(Enemy enemy) {
        return enemy != null && "enemy-5-final-boss".equals(enemy.getId());
    }

    private boolean containsEnemyReference(Lista<Enemy> seen, Enemy enemy) {
        for (int i = 0; i < seen.size(); i++) {
            if (seen.get(i) == enemy) {
                return true;
            }
        }
        return false;
    }

    private void clearEnemy(Enemy enemy) {
        Room room = state.getRoom();
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Cell cell = room.getCell(new Position(row, column));
                if (cell.getEnemy() == enemy) {
                    cell.clearEnemy();
                }
            }
        }
    }

    private Position bestLargeBossStep(Position enemyPosition, Enemy enemy) {
        Position playerPosition = state.getPlayer().getPosition();
        Direction best = null;
        int bestDistance = distanceFromLargeBossToPlayer(enemyPosition);
        for (Direction direction : Direction.values()) {
            Position next = enemyPosition.translate(direction);
            if (canMoveEnemyTo(next, enemy)) {
                int distance = distanceFromLargeBossToPlayer(next);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = direction;
                }
            }
        }
        return best == null || playerPosition == null ? null : enemyPosition.translate(best);
    }

    private boolean canMoveEnemyTo(Position anchor, Enemy enemy) {
        if (!isFinalBoss(enemy)) {
            return anchor != null
                    && state.getRoom().isValid(anchor)
                    && state.getRoom().getCell(anchor).isWalkable();
        }
        if (anchor == null) {
            return false;
        }
        Room room = state.getRoom();
        Position player = state.getPlayer().getPosition();
        for (int row = anchor.getRow(); row < anchor.getRow() + 3; row++) {
            for (int column = anchor.getColumn(); column < anchor.getColumn() + 3; column++) {
                Position position = new Position(row, column);
                if (!room.isValid(position) || position.equals(player)) {
                    return false;
                }
                Cell cell = room.getCell(position);
                if (cell.isWall() || cell.isDoor() || cell.hasBlacksmith() || cell.getObject() != null
                        || (cell.getEnemy() != null && cell.getEnemy() != enemy)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void moveEnemyTo(Position from, Position to, Enemy enemy) {
        if (!isFinalBoss(enemy)) {
            state.getRoom().getCell(from).clearEnemy();
            state.getRoom().getCell(to).setEnemy(enemy);
            return;
        }
        clearEnemy(enemy);
        for (int row = to.getRow(); row < to.getRow() + 3; row++) {
            for (int column = to.getColumn(); column < to.getColumn() + 3; column++) {
                state.getRoom().getCell(new Position(row, column)).setEnemy(enemy);
            }
        }
    }

    private Position bossCenter(Position anchor, Enemy enemy) {
        if (!isFinalBoss(enemy)) {
            return anchor;
        }
        return new Position(anchor.getRow() + 1, anchor.getColumn() + 1);
    }

    private int distanceFromLargeBossToPlayer(Position anchor) {
        Position player = state.getPlayer().getPosition();
        int best = Integer.MAX_VALUE;
        for (int row = anchor.getRow(); row < anchor.getRow() + 3; row++) {
            for (int column = anchor.getColumn(); column < anchor.getColumn() + 3; column++) {
                int distance = Math.abs(row - player.getRow()) + Math.abs(column - player.getColumn());
                if (distance < best) {
                    best = distance;
                }
            }
        }
        return best;
    }

    public static class MovementEvent {
        private final Position from;
        private final Position to;
        private final boolean player;

        public MovementEvent(Position from, Position to, boolean player) {
            this.from = from;
            this.to = to;
            this.player = player;
        }

        public Position getFrom() {
            return from;
        }

        public Position getTo() {
            return to;
        }

        public boolean isPlayer() {
            return player;
        }
    }

    public static class PickupEvent {
        private final Position from;
        private final Position to;
        private final GameObject object;

        public PickupEvent(Position from, Position to, GameObject object) {
            this.from = from;
            this.to = to;
            this.object = object;
        }

        public Position getFrom() {
            return from;
        }

        public Position getTo() {
            return to;
        }

        public GameObject getObject() {
            return object;
        }
    }
}
