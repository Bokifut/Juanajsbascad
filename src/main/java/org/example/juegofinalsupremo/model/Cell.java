package org.example.juegofinalsupremo.model;

public class Cell {
    private final Position position;
    private boolean wall;
    private boolean door;
    private boolean open;
    private String targetRoomId;
    private Position targetPosition;
    private int trapDamage;
    private GameObject object;
    private Enemy enemy;
    private boolean blacksmith;
    private String requiredKeyId;

    public Cell(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isWall() {
        return wall;
    }

    public void setWall(boolean wall) {
        this.wall = wall;
    }

    public boolean isDoor() {
        return door;
    }

    public boolean isOpen() {
        return open;
    }

    public void setDoor(boolean door, boolean open) {
        setDoor(door, open, null, null);
    }

    public void setDoor(boolean door, boolean open, String targetRoomId, Position targetPosition) {
        ensureNoContent();
        this.door = door;
        this.open = open;
        this.targetRoomId = targetRoomId;
        this.targetPosition = targetPosition;
    }

    public void openDoor() {
        if (door) {
            open = true;
            requiredKeyId = null;
        }
    }

    public void clearDoor() {
        door = false;
        open = false;
        targetRoomId = null;
        targetPosition = null;
        requiredKeyId = null;
    }

    public String getTargetRoomId() {
        return targetRoomId;
    }

    public Position getTargetPosition() {
        return targetPosition;
    }

    public String getRequiredKeyId() {
        return requiredKeyId;
    }

    public void setRequiredKeyId(String requiredKeyId) {
        this.requiredKeyId = requiredKeyId;
    }

    public boolean hasRoomTransition() {
        return door && open && targetRoomId != null && targetPosition != null;
    }

    public boolean hasTrap() {
        return trapDamage > 0;
    }

    public int getTrapDamage() {
        return trapDamage;
    }

    public int consumeTrap() {
        int damage = trapDamage;
        trapDamage = 0;
        return damage;
    }

    public void setTrapDamage(int trapDamage) {
        ensureNoContent();
        this.trapDamage = Math.max(0, trapDamage);
    }

    public GameObject getObject() {
        return object;
    }

    public void setObject(GameObject object) {
        if (object != null && (wall || door || trapDamage > 0 || this.object != null || blacksmith)) {
            throw new IllegalStateException("La celda " + position + " ya contiene otra entidad; no se puede poner objeto");
        }
        this.object = object;
    }

    public GameObject takeObject() {
        GameObject taken = object;
        object = null;
        return taken;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void setEnemy(Enemy enemy) {
        if (enemy != null && (wall || door || this.enemy != null || blacksmith)) {
            throw new IllegalStateException("La celda " + position + " ya contiene otra entidad; no se puede poner enemigo");
        }
        this.enemy = enemy;
    }

    public void clearEnemy() {
        enemy = null;
    }

    public boolean hasBlacksmith() {
        return blacksmith;
    }

    public void setBlacksmith(boolean blacksmith) {
        ensureAssignable(blacksmith, "herrero");
        this.blacksmith = blacksmith;
    }

    public boolean isWalkable() {
        return !wall && (!door || open) && enemy == null && !blacksmith;
    }

    public boolean isEmpty() {
        return !wall && !door && trapDamage == 0 && object == null && enemy == null && !blacksmith;
    }

    private void ensureAssignable(boolean assigning, String type) {
        if (assigning && !isEmpty()) {
            throw new IllegalStateException("La celda " + position + " ya contiene otra entidad; no se puede poner " + type);
        }
    }

    private void ensureNoContent() {
        if (object != null || enemy != null || blacksmith) {
            throw new IllegalStateException("La celda " + position + " ya contiene entidad");
        }
    }
}
