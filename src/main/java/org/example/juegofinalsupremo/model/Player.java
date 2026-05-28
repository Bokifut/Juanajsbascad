package org.example.juegofinalsupremo.model;

import org.example.juegofinalsupremo.data.Lista;

public class Player {
    private final String name;
    private int maxHealth;
    private int health;
    private int baseAttack;
    private int baseDefense;
    private int movement;
    private Position position;
    private GameObject equippedWeapon;
    private GameObject equippedArmor;
    private final Lista<GameObject> inventory;

    public Player(String name, CharacterClass characterClass) {
        this(name, characterClass.getHealth(), characterClass.getAttack(), characterClass.getDefense(), characterClass.getSpeed(), new Position(0, 0));
    }

    public Player(String name, int health, int baseAttack, int movement, Position position) {
        this(name, health, baseAttack, 0, movement, position);
    }

    public Player(String name, int health, int baseAttack, int baseDefense, int movement, Position position) {
        this.name = name;
        this.maxHealth = Math.max(0, health);
        this.health = this.maxHealth;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.movement = movement;
        this.position = position;
        this.inventory = new Lista<GameObject>();
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        int total = maxHealth;
        if (equippedArmor != null) {
            total += equippedArmor.getMaxHealthBonus();
        }
        return total;
    }

    public int getBaseMaxHealth() {
        return maxHealth;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getMovement() {
        return movement;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Lista<GameObject> getInventory() {
        return inventory;
    }

    public int getAttackPower() {
        int power = baseAttack;
        if (equippedWeapon != null) {
            power += equippedWeapon.getDamageBonus();
        }
        return power;
    }

    public int getDefensePower() {
        int power = baseDefense;
        if (equippedArmor != null) {
            power += equippedArmor.getDefenseBonus();
        }
        return power;
    }

    public int getMovementPower() {
        int power = movement;
        if (equippedWeapon != null) {
            power += equippedWeapon.getMovementBonus();
        }
        if (equippedArmor != null) {
            power += equippedArmor.getMovementBonus();
        }
        return power;
    }

    public GameObject getEquippedWeapon() {
        return equippedWeapon;
    }

    public GameObject getEquippedArmor() {
        return equippedArmor;
    }

    public void equip(GameObject object) {
        if (object == null) {
            return;
        }
        if (object.isArmor()) {
            int oldBonus = equippedArmor == null ? 0 : equippedArmor.getMaxHealthBonus();
            equippedArmor = object;
            int gained = Math.max(0, equippedArmor.getMaxHealthBonus() - oldBonus);
            health = Math.min(getMaxHealth(), health + gained);
        } else if (object.isWeapon()) {
            equippedWeapon = object;
        }
    }

    public void unequip(GameObject object) {
        if (object == null) {
            return;
        }
        if (object == equippedWeapon) {
            equippedWeapon = null;
        }
        if (object == equippedArmor) {
            equippedArmor = null;
        }
    }

    public void receiveDamage(int damage) {
        health = Math.max(0, health - Math.max(0, damage));
    }

    public void heal(int amount) {
        health = Math.min(getMaxHealth(), Math.max(0, health + Math.max(0, amount)));
    }

    public void increaseMaxHealth(int amount) {
        int bonus = Math.max(0, amount);
        maxHealth += bonus;
        health = Math.min(maxHealth, health + bonus);
    }

    public void increaseBaseDefense(int amount) {
        baseDefense += amount;
    }

    public void increaseMovement(int amount) {
        movement += amount;
    }

    public boolean hasItem(String itemId) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getId().equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    public boolean consumeItem(String itemId) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getId().equals(itemId)) {
                inventory.removeAt(i);
                return true;
            }
        }
        return false;
    }
}
