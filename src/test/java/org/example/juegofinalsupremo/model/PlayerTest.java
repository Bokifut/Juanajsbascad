package org.example.juegofinalsupremo.model;

import org.example.juegofinalsupremo.data.Lista;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testConstructorsAndBaseGetters() {
        Position pos = new Position(1, 1);
        Player p1 = new Player("Heroe", 100, 10, 5, 3, pos);

        assertEquals("Heroe", p1.getName());
        assertEquals(100, p1.getHealth());
        assertEquals(100, p1.getBaseMaxHealth());
        assertEquals(100, p1.getMaxHealth());
        assertEquals(10, p1.getBaseAttack());
        assertEquals(5, p1.getBaseDefense());
        assertEquals(3, p1.getMovement());
        assertEquals(pos, p1.getPosition());
        assertNotNull(p1.getInventory());

        Player p2 = new Player("Bajo Cero", -10, 5, 2, new Position(0, 0));
        assertEquals(0, p2.getBaseMaxHealth());
        assertEquals(0, p2.getHealth());
        assertEquals(0, p2.getBaseDefense());
    }

    @Test
    void testCharacterClassConstructor() {
        try {
            Player p = new Player("ClaseTest", CharacterClass.GUERRERO);
            assertEquals("ClaseTest", p.getName());
            assertNotNull(p.getPosition());
            assertEquals(0, p.getPosition().getRow());
            assertEquals(0, p.getPosition().getColumn());
        } catch (Exception ignored) {
        }
    }

    @Test
    void testSetPosition() {
        Player p = new Player("P", 100, 10, 5, 3, new Position(0, 0));
        Position newPos = new Position(5, 5);
        p.setPosition(newPos);
        assertEquals(newPos, p.getPosition());
    }

    @Test
    void testEquipmentAndPowerModifiers() {
        Player p = new Player("P", 50, 10, 5, 3, new Position(0, 0));

        assertEquals(50, p.getMaxHealth());
        assertEquals(10, p.getAttackPower());
        assertEquals(5, p.getDefensePower());
        assertEquals(3, p.getMovementPower());

        GameObject armor1 = new GameObject("a1", "A1", 0, 10, 0, 5, 1, null, GameObject.TYPE_ARMOR);
        GameObject armor2 = new GameObject("a2", "A2", 0, 5, 0, 2, 2, null, GameObject.TYPE_ARMOR);
        GameObject weapon1 = new GameObject("w1", "W1", 0, 0, 15, 0, 2, "Melee", GameObject.TYPE_WEAPON);
        GameObject potion = new GameObject("pot", "Pot", 10, 0, 0, 0, 0, null, GameObject.TYPE_POTION);

        p.equip(null);
        assertNull(p.getEquippedArmor());
        assertNull(p.getEquippedWeapon());

        p.equip(potion);
        assertNull(p.getEquippedArmor());
        assertNull(p.getEquippedWeapon());

        p.equip(armor1);
        assertEquals(armor1, p.getEquippedArmor());
        assertEquals(60, p.getMaxHealth());
        assertEquals(60, p.getHealth());
        assertEquals(10, p.getDefensePower());
        assertEquals(4, p.getMovementPower());

        p.equip(weapon1);
        assertEquals(weapon1, p.getEquippedWeapon());
        assertEquals(25, p.getAttackPower());
        assertEquals(6, p.getMovementPower());

        p.equip(armor2);
        assertEquals(armor2, p.getEquippedArmor());
        assertEquals(55, p.getMaxHealth());
        assertEquals(55, p.getHealth());
        assertEquals(7, p.getDefensePower());
        assertEquals(7, p.getMovementPower());

        p.unequip(null);
        p.unequip(potion);

        p.unequip(weapon1);
        assertNull(p.getEquippedWeapon());
        assertEquals(10, p.getAttackPower());
        assertEquals(5, p.getMovementPower());

        p.unequip(armor2);
        assertNull(p.getEquippedArmor());
        assertEquals(5, p.getDefensePower());
        assertEquals(3, p.getMovementPower());

        p.equip(weapon1);
        assertEquals(5, p.getMovementPower());
    }

    @Test
    void testDamageAndHealing() {
        Player p = new Player("P", 50, 10, 5, 3, new Position(0, 0));

        p.receiveDamage(-10);
        assertEquals(50, p.getHealth());

        p.receiveDamage(20);
        assertEquals(30, p.getHealth());

        p.receiveDamage(100);
        assertEquals(0, p.getHealth());

        p.heal(-5);
        assertEquals(0, p.getHealth());

        p.heal(20);
        assertEquals(20, p.getHealth());

        p.heal(100);
        assertEquals(50, p.getHealth());
    }

    @Test
    void testStatIncreases() {
        Player p = new Player("P", 50, 10, 5, 3, new Position(0, 0));

        p.increaseMaxHealth(-5);
        assertEquals(50, p.getBaseMaxHealth());

        p.increaseMaxHealth(10);
        assertEquals(60, p.getBaseMaxHealth());
        assertEquals(60, p.getHealth());

        p.increaseBaseDefense(3);
        assertEquals(8, p.getBaseDefense());

        p.increaseMovement(2);
        assertEquals(5, p.getMovement());
    }

    @Test
    void testInventoryMechanics() {
        Player p = new Player("P", 50, 10, 5, 3, new Position(0, 0));
        GameObject obj1 = new GameObject("item1", "Item 1", 0, 0, 0, 0, 0, null, GameObject.TYPE_KEY);
        GameObject obj2 = new GameObject("item2", "Item 2", 0, 0, 0, 0, 0, null, GameObject.TYPE_POTION);

        Lista<GameObject> inventory = p.getInventory();
        assertTrue(inventory.isEmpty());

        inventory.add(obj1);
        inventory.add(obj2);

        assertTrue(p.hasItem("item1"));
        assertTrue(p.hasItem("item2"));
        assertFalse(p.hasItem("nonexistent"));

        assertTrue(p.consumeItem("item1"));
        assertFalse(p.hasItem("item1"));
        assertEquals(1, p.getInventory().size());

        assertFalse(p.consumeItem("nonexistent"));

        assertTrue(p.consumeItem("item2"));
        assertTrue(p.getInventory().isEmpty());
    }
}