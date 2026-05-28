package org.example.juegofinalsupremo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterClassTest {

    @Test
    void getDisplayName() {
        assertEquals("Asesino", CharacterClass.LADRON.getDisplayName());
        assertEquals("Vikingo", CharacterClass.VIKINGO.getDisplayName());
        assertEquals("Napoleon", CharacterClass.NAPOLEON.getDisplayName());
    }

    @Test
    void getHealth() {
        assertEquals(15, CharacterClass.LADRON.getHealth());
        assertEquals(20, CharacterClass.GUERRERO.getHealth());
        assertEquals(25, CharacterClass.ACORAZADO.getHealth());
        assertEquals(1000, CharacterClass.NAPOLEON.getHealth());
    }

    @Test
    void getAttack() {
        assertEquals(4, CharacterClass.LADRON.getAttack());
        assertEquals(9, CharacterClass.OBISPO.getAttack());
        assertEquals(20, CharacterClass.NAPOLEON.getAttack());
    }

    @Test
    void getDefense() {
        assertEquals(0, CharacterClass.LADRON.getDefense());
        assertEquals(2, CharacterClass.ACORAZADO.getDefense());
        assertEquals(4, CharacterClass.NAPOLEON.getDefense());
    }

    @Test
    void getSpeed() {
        assertEquals(2, CharacterClass.LADRON.getSpeed());
        assertEquals(1, CharacterClass.GUERRERO.getSpeed());
        assertEquals(3, CharacterClass.NAPOLEON.getSpeed());
    }

    @Test
    void getWeaponName() {
        assertEquals("Daga", CharacterClass.LADRON.getWeaponName());
        assertEquals("Escudo", CharacterClass.ACORAZADO.getWeaponName());
        assertEquals("Cetro", CharacterClass.OBISPO.getWeaponName());
    }

    @Test
    void getWeaponDamageBonus() {
        assertEquals(1, CharacterClass.VIKINGO.getWeaponDamageBonus());
        assertEquals(0, CharacterClass.ACORAZADO.getWeaponDamageBonus());
        assertEquals(-2, CharacterClass.OBISPO.getWeaponDamageBonus());
    }

    @Test
    void getWeaponDefenseBonus() {
        assertEquals(0, CharacterClass.LADRON.getWeaponDefenseBonus());
        assertEquals(1, CharacterClass.ACORAZADO.getWeaponDefenseBonus());
    }

    @Test
    void getWeaponCombatType() {
        assertEquals(Enemy.MELEE, CharacterClass.GUERRERO.getWeaponCombatType());
        assertEquals(Enemy.RANGED, CharacterClass.OBISPO.getWeaponCombatType());
    }

    @Test
    void createStarterItem() {
        GameObject item = CharacterClass.VIKINGO.createStarterItem();
        assertNotNull(item);
        assertEquals("starter-vikingo", item.getId());
        assertEquals("Tomahawk", item.getName());
        assertEquals(1, item.getDamageBonus());
        assertEquals(0, item.getDefenseBonus());
        assertEquals(Enemy.MELEE, item.getWeaponCombatType());
    }

    @Test
    void values() {
        CharacterClass[] classes = CharacterClass.values();
        assertEquals(6, classes.length);
        assertEquals(CharacterClass.LADRON, classes[0]);
        assertEquals(CharacterClass.NAPOLEON, classes[5]);
    }

    @Test
    void valueOf() {
        assertEquals(CharacterClass.GUERRERO, CharacterClass.valueOf("GUERRERO"));
        assertEquals(CharacterClass.OBISPO, CharacterClass.valueOf("OBISPO"));
    }
}
