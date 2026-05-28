package org.example.juegofinalsupremo.model;

public enum CharacterClass {
    LADRON("Asesino", 15, 4, 0, 2, "Daga", 1, 0, Enemy.MELEE),
    VIKINGO("Vikingo", 15, 7, 1, 1, "Tomahawk", 1, 0, Enemy.MELEE),
    GUERRERO("Guerrero", 20, 5, 0, 1, "Espada larga", 1, 0, Enemy.MELEE),
    ACORAZADO("Acorazado", 25, 3, 2, 1, "Escudo", 0, 1, Enemy.MELEE),
    OBISPO("Obispo", 12, 9, 0, 1, "Cetro", -2, 0, Enemy.RANGED),
    NAPOLEON("Napoleon", 1000, 20, 4, 3, "Sable imperial", 0, 0, Enemy.MELEE);

    private final String displayName;
    private final int health;
    private final int attack;
    private final int defense;
    private final int speed;
    private final String weaponName;
    private final int weaponDamageBonus;
    private final int weaponDefenseBonus;
    private final String weaponCombatType;

    CharacterClass(String displayName, int health, int attack, int defense, int speed,
                   String weaponName, int weaponDamageBonus, int weaponDefenseBonus, String weaponCombatType) {
        this.displayName = displayName;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.weaponName = weaponName;
        this.weaponDamageBonus = weaponDamageBonus;
        this.weaponDefenseBonus = weaponDefenseBonus;
        this.weaponCombatType = weaponCombatType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public String getWeaponName() {
        return weaponName;
    }

    public int getWeaponDamageBonus() {
        return weaponDamageBonus;
    }

    public int getWeaponDefenseBonus() {
        return weaponDefenseBonus;
    }

    public String getWeaponCombatType() {
        return weaponCombatType;
    }

    public GameObject createStarterItem() {
        return new GameObject("starter-" + name().toLowerCase(), weaponName, 0,
                weaponDamageBonus, weaponDefenseBonus, 0, weaponCombatType);
    }
}
