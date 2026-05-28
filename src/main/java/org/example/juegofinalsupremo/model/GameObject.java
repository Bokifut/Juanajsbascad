package org.example.juegofinalsupremo.model;

public class GameObject {
    public static final String TYPE_WEAPON = "weapon";
    public static final String TYPE_ARMOR = "armor";
    public static final String TYPE_POTION = "potion";
    public static final String TYPE_KEY = "key";
    public static final String TYPE_COIN = "coin";

    private final String id;
    private final String name;
    private final int healing;
    private final int maxHealthBonus;
    private final int damageBonus;
    private final int defenseBonus;
    private final int movementBonus;
    private final String weaponCombatType;
    private final String type;

    public GameObject() {
        this("default_id", "Objeto por defecto", 0, 0, 0, 0);
    }

    public GameObject(String id, String name, int value) {
        this(id, name, value, 0, 0, 0, 0, null, TYPE_COIN);
    }

    public GameObject(String id, String name, int healing, int damageBonus, int defenseBonus, int movementBonus) {
        this(id, name, healing, damageBonus, defenseBonus, movementBonus, null);
    }

    public GameObject(String id, String name, int healing, int damageBonus, int defenseBonus, int movementBonus, String weaponCombatType) {
        this(id, name, healing, 0, damageBonus, defenseBonus, movementBonus, weaponCombatType, inferType(damageBonus, defenseBonus, weaponCombatType));
    }

    public GameObject(String id, String name, int healing, int maxHealthBonus, int damageBonus,
                      int defenseBonus, int movementBonus, String weaponCombatType, String type) {
        this.id = id;
        this.name = name;
        this.healing = healing;
        this.maxHealthBonus = maxHealthBonus;
        this.damageBonus = damageBonus;
        this.defenseBonus = defenseBonus;
        this.movementBonus = movementBonus;
        this.weaponCombatType = weaponCombatType;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHealing() {
        return healing;
    }

    public int getMaxHealthBonus() {
        return maxHealthBonus;
    }

    public int getDamageBonus() {
        return damageBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public int getMovementBonus() {
        return movementBonus;
    }

    public String getWeaponCombatType() {
        return weaponCombatType;
    }

    public String getType() {
        return type;
    }

    public boolean isWeapon() {
        return TYPE_WEAPON.equals(type) || weaponCombatType != null || damageBonus != 0;
    }

    public boolean isArmor() {
        return TYPE_ARMOR.equals(type) || defenseBonus > 0;
    }

    public boolean isPotion() {
        return TYPE_POTION.equals(type);
    }

    public boolean isKey() {
        return TYPE_KEY.equals(type);
    }

    public boolean isCoin() {
        return TYPE_COIN.equals(type);
    }

    public int getCoinValue() {
        return isCoin() ? Math.max(0, healing) : 0;
    }

    public boolean isForgeable() {
        return (isWeapon() || isArmor()) && !id.contains("-forged-");
    }

    public GameObject forged(int level) {
        int safeLevel = Math.max(1, Math.min(3, level));
        int newDamage = damageBonus;
        int newDefense = defenseBonus;
        if (isArmor()) {
            newDefense += safeLevel == 1 ? 1 : safeLevel == 2 ? 2 : 4;
        } else if (Enemy.RANGED.equals(weaponCombatType)) {
            newDamage += safeLevel == 1 ? 1 : safeLevel == 2 ? 2 : 4;
        } else {
            newDamage += safeLevel == 1 ? 1 : safeLevel == 2 ? 3 : 6;
        }
        return new GameObject(id + "-forged-" + safeLevel, name + " (Forjado Nvl " + safeLevel + ")",
                healing, maxHealthBonus, newDamage, newDefense, movementBonus, weaponCombatType, type);
    }

    public String bonusDescription() {
        StringBuilder builder = new StringBuilder();
        if (isCoin()) {
            return getCoinValue() + " moneda" + (getCoinValue() == 1 ? "" : "s");
        }
        if (healing > 0) {
            builder.append("+").append(healing).append(" vida");
        }
        if (maxHealthBonus > 0) {
            appendSeparator(builder);
            builder.append("+").append(maxHealthBonus).append(" vida maxima");
        }
        if (damageBonus != 0) {
            appendSeparator(builder);
            appendSigned(builder, damageBonus, " ataque");
        }
        if (defenseBonus != 0) {
            appendSeparator(builder);
            appendSigned(builder, defenseBonus, " defensa");
        }
        if (movementBonus != 0) {
            appendSeparator(builder);
            appendSigned(builder, movementBonus, " velocidad");
        }
        if (weaponCombatType != null) {
            appendSeparator(builder);
            builder.append(weaponCombatType);
        }
        if (builder.length() == 0) {
            builder.append("Sin modificadores");
        }
        return builder.toString();
    }

    private void appendSeparator(StringBuilder builder) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
    }

    private void appendSigned(StringBuilder builder, int value, String text) {
        if (value > 0) {
            builder.append("+");
        }
        builder.append(value).append(text);
    }

    public String toString() {
        return name;
    }

    private static String inferType(int damageBonus, int defenseBonus, String weaponCombatType) {
        if (defenseBonus > 0 && weaponCombatType == null && damageBonus == 0) {
            return TYPE_ARMOR;
        }
        if (weaponCombatType != null || damageBonus != 0) {
            return TYPE_WEAPON;
        }
        return TYPE_POTION;
    }
}
