package org.example.juegofinalsupremo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnemyTest {

    @Test
    void testConstructorsAndGetters() {
        Enemy emptyEnemy = new Enemy();
        assertEquals("enemigo_base", emptyEnemy.getId());
        assertEquals("Enemigo", emptyEnemy.getName());
        assertEquals(1, emptyEnemy.getLevel());
        assertEquals(Enemy.MELEE, emptyEnemy.getCombatType());
        assertEquals(10, emptyEnemy.getHealth());
        assertEquals(1, emptyEnemy.getAttack());
        assertEquals(1, emptyEnemy.getSpeed());
        assertEquals("Bajo", emptyEnemy.getDifficultyLabel());
        assertFalse(emptyEnemy.isSummoned());

        Enemy fiveParamEnemy = new Enemy("orc_1", "Orco", 2, 50, 15);
        assertEquals("orc_1", fiveParamEnemy.getId());
        assertEquals("Orco", fiveParamEnemy.getName());
        assertEquals(2, fiveParamEnemy.getLevel());
        assertEquals(Enemy.MELEE, fiveParamEnemy.getCombatType());
        assertEquals(50, fiveParamEnemy.getHealth());
        assertEquals(15, fiveParamEnemy.getAttack());

        Enemy sixParamEnemy = new Enemy("goblin_1", "Goblin", 1, Enemy.RANGED, 30, 5);
        assertEquals("goblin_1", sixParamEnemy.getId());
        assertEquals(Enemy.RANGED, sixParamEnemy.getCombatType());
        assertEquals(1, sixParamEnemy.getSpeed());
        assertEquals("Medio", sixParamEnemy.getDifficultyLabel());
        assertFalse(sixParamEnemy.isSummoned());
    }

    @Test
    void testConstructorBounds() {
        Enemy lowBoundsEnemy = new Enemy("low", "Low", 0, Enemy.MELEE, -10, 5, -5, "Bajo", false);
        assertEquals(1, lowBoundsEnemy.getLevel());
        assertEquals(0, lowBoundsEnemy.getHealth());
        assertEquals(0, lowBoundsEnemy.getSpeed());

        Enemy highBoundsEnemy = new Enemy("high", "High", 5, Enemy.MELEE, 100, 5, 10, "Alto", true);
        assertEquals(3, highBoundsEnemy.getLevel());
        assertTrue(highBoundsEnemy.isSummoned());
    }

    @Test
    void testSetAttackAndPhaseTwo() {
        Enemy enemy = new Enemy();

        enemy.setAttack(25);
        assertEquals(25, enemy.getAttack());

        assertFalse(enemy.isPhaseTwo());
        enemy.setPhaseTwo(true);
        assertTrue(enemy.isPhaseTwo());
    }

    @Test
    void testHealthAndDamageMechanics() {
        Enemy enemy = new Enemy("test", "Test", 1, Enemy.MELEE, 20, 5);

        assertTrue(enemy.isAlive());

        enemy.receiveDamage(5);
        assertEquals(15, enemy.getHealth());
        assertTrue(enemy.isAlive());

        enemy.receiveDamage(-10);
        assertEquals(15, enemy.getHealth());

        enemy.receiveDamage(20);
        assertEquals(0, enemy.getHealth());
        assertFalse(enemy.isAlive());

        enemy.setImmunePlayerTurns(1);
        enemy.receiveDamage(50);
        assertEquals(0, enemy.getHealth());

        enemy.heal(10);
        assertEquals(10, enemy.getHealth());
        assertTrue(enemy.isAlive());

        enemy.heal(-5);
        assertEquals(10, enemy.getHealth());

        enemy.receiveDamage(5);
        assertEquals(10, enemy.getHealth());
    }

    @Test
    void testSkipTurns() {
        Enemy enemy = new Enemy();

        assertEquals(0, enemy.getSkipTurns());

        enemy.consumeSkipTurn();
        assertEquals(0, enemy.getSkipTurns());

        enemy.setSkipTurns(-5);
        assertEquals(0, enemy.getSkipTurns());

        enemy.setSkipTurns(2);
        assertEquals(2, enemy.getSkipTurns());

        enemy.consumeSkipTurn();
        assertEquals(1, enemy.getSkipTurns());

        enemy.consumeSkipTurn();
        assertEquals(0, enemy.getSkipTurns());

        enemy.consumeSkipTurn();
        assertEquals(0, enemy.getSkipTurns());
    }

    @Test
    void testImmunePlayerTurns() {
        Enemy enemy = new Enemy();

        assertEquals(0, enemy.getImmunePlayerTurns());

        enemy.consumeImmunePlayerTurn();
        assertEquals(0, enemy.getImmunePlayerTurns());

        enemy.setImmunePlayerTurns(-3);
        assertEquals(0, enemy.getImmunePlayerTurns());

        enemy.setImmunePlayerTurns(2);
        assertEquals(2, enemy.getImmunePlayerTurns());

        enemy.consumeImmunePlayerTurn();
        assertEquals(1, enemy.getImmunePlayerTurns());

        enemy.consumeImmunePlayerTurn();
        assertEquals(0, enemy.getImmunePlayerTurns());

        enemy.consumeImmunePlayerTurn();
        assertEquals(0, enemy.getImmunePlayerTurns());
    }

    @Test
    void testFinalJudgmentCountdown() {
        Enemy enemy = new Enemy();

        assertEquals(0, enemy.getFinalJudgmentCountdown());

        enemy.consumeFinalJudgmentCountdown();
        assertEquals(0, enemy.getFinalJudgmentCountdown());

        enemy.setFinalJudgmentCountdown(-1);
        assertEquals(0, enemy.getFinalJudgmentCountdown());

        enemy.setFinalJudgmentCountdown(2);
        assertEquals(2, enemy.getFinalJudgmentCountdown());

        enemy.consumeFinalJudgmentCountdown();
        assertEquals(1, enemy.getFinalJudgmentCountdown());

        enemy.consumeFinalJudgmentCountdown();
        assertEquals(0, enemy.getFinalJudgmentCountdown());

        enemy.consumeFinalJudgmentCountdown();
        assertEquals(0, enemy.getFinalJudgmentCountdown());
    }
}