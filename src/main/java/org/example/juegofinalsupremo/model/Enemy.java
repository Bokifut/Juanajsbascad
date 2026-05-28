package org.example.juegofinalsupremo.model;

public class Enemy {
    public static final String MELEE = "Cuerpo a cuerpo";
    public static final String RANGED = "A distancia";

    private final String id;
    private final String name;
    private final int level;
    private final String combatType;
    private int health;
    private int attack;
    private int speed;
    private final String difficultyLabel;
    private final boolean summoned;
    private boolean phaseTwo;
    private int skipTurns;
    private int immunePlayerTurns;
    private int finalJudgmentCountdown;

    public Enemy() {
        this("enemigo_base", "Enemigo", 1, MELEE, 10, 1, 1, "Bajo", false);
    }

    public Enemy(String id, String name, int level, int health, int attack) {
        this(id, name, level, MELEE, health, attack);
    }

    public Enemy(String id, String name, int level, String combatType, int health, int attack) {
        this(id, name, level, combatType, health, attack, 1, "Medio", false);
    }

    public Enemy(String id, String name, int level, String combatType, int health, int attack, int speed,
                 String difficultyLabel, boolean summoned) {
        this.id = id;
        this.name = name;
        this.level = Math.max(1, Math.min(3, level));
        this.combatType = combatType;
        this.health = Math.max(0, health);
        this.attack = attack;
        this.speed = Math.max(0, speed);
        this.difficultyLabel = difficultyLabel;
        this.summoned = summoned;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getCombatType() {
        return combatType;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getSpeed() {
        return speed;
    }

    public String getDifficultyLabel() {
        return difficultyLabel;
    }

    public boolean isSummoned() {
        return summoned;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void receiveDamage(int damage) {
        if (immunePlayerTurns > 0) {
            return;
        }
        health = Math.max(0, health - Math.max(0, damage));
    }

    public void heal(int amount) {
        health += Math.max(0, amount);
    }

    public boolean isPhaseTwo() {
        return phaseTwo;
    }

    public void setPhaseTwo(boolean phaseTwo) {
        this.phaseTwo = phaseTwo;
    }

    public int getSkipTurns() {
        return skipTurns;
    }

    public void setSkipTurns(int skipTurns) {
        this.skipTurns = Math.max(0, skipTurns);
    }

    public void consumeSkipTurn() {
        if (skipTurns > 0) {
            skipTurns--;
        }
    }

    public int getImmunePlayerTurns() {
        return immunePlayerTurns;
    }

    public void setImmunePlayerTurns(int immunePlayerTurns) {
        this.immunePlayerTurns = Math.max(0, immunePlayerTurns);
    }

    public void consumeImmunePlayerTurn() {
        if (immunePlayerTurns > 0) {
            immunePlayerTurns--;
        }
    }

    public int getFinalJudgmentCountdown() {
        return finalJudgmentCountdown;
    }

    public void setFinalJudgmentCountdown(int finalJudgmentCountdown) {
        this.finalJudgmentCountdown = Math.max(0, finalJudgmentCountdown);
    }

    public void consumeFinalJudgmentCountdown() {
        if (finalJudgmentCountdown > 0) {
            finalJudgmentCountdown--;
        }
    }
}