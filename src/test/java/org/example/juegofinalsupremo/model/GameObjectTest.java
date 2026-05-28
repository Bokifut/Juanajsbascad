package org.example.juegofinalsupremo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameObjectTest {

    @Test
    void testConstructorsAndGetters() {
        GameObject empty = new GameObject();
        assertEquals("default_id", empty.getId());
        assertEquals("Objeto por defecto", empty.getName());
        assertEquals(0, empty.getHealing());
        assertEquals(0, empty.getMaxHealthBonus());
        assertEquals(0, empty.getDamageBonus());
        assertEquals(0, empty.getDefenseBonus());
        assertEquals(0, empty.getMovementBonus());
        assertNull(empty.getWeaponCombatType());
        assertEquals(GameObject.TYPE_POTION, empty.getType());

        GameObject coin = new GameObject("coin_1", "Moneda de Oro", 15);
        assertEquals("coin_1", coin.getId());
        assertEquals("Moneda de Oro", coin.getName());
        assertEquals(15, coin.getHealing());
        assertEquals(GameObject.TYPE_COIN, coin.getType());

        GameObject basic = new GameObject("basic_1", "Basico", 5, 2, 3, 1);
        assertEquals(5, basic.getHealing());
        assertEquals(2, basic.getDamageBonus());
        assertEquals(3, basic.getDefenseBonus());
        assertEquals(1, basic.getMovementBonus());
        assertNull(basic.getWeaponCombatType());
        assertEquals(GameObject.TYPE_WEAPON, basic.getType());
    }

    @Test
    void testTypeInferenceAndCheckers() {
        GameObject explicitKey = new GameObject("key_1", "Llave", 0, 0, 0, 0, 0, null, GameObject.TYPE_KEY);
        assertTrue(explicitKey.isKey());
        assertFalse(explicitKey.isWeapon());
        assertFalse(explicitKey.isArmor());
        assertFalse(explicitKey.isPotion());
        assertFalse(explicitKey.isCoin());

        GameObject armor = new GameObject("armor_1", "Armadura", 0, 0, 5, 0);
        assertTrue(armor.isArmor());
        assertEquals(GameObject.TYPE_ARMOR, armor.getType());

        GameObject weaponWithDmg = new GameObject("w_1", "Arma 1", 0, 5, 0, 0);
        assertTrue(weaponWithDmg.isWeapon());
        assertEquals(GameObject.TYPE_WEAPON, weaponWithDmg.getType());

        GameObject weaponWithCombatType = new GameObject("w_2", "Arma 2", 0, 0, 0, 0, "Magico");
        assertTrue(weaponWithCombatType.isWeapon());
        assertEquals(GameObject.TYPE_WEAPON, weaponWithCombatType.getType());

        GameObject potion = new GameObject("p_1", "Pocion", 10, 0, 0, 0);
        assertTrue(potion.isPotion());
        assertEquals(GameObject.TYPE_POTION, potion.getType());

        GameObject coin = new GameObject("c_1", "Moneda", 10);
        assertTrue(coin.isCoin());
    }

    @Test
    void testCoinValue() {
        GameObject coin1 = new GameObject("c1", "Moneda", 1);
        assertEquals(1, coin1.getCoinValue());

        GameObject coin5 = new GameObject("c5", "Moneda Grande", 5);
        assertEquals(5, coin5.getCoinValue());

        GameObject negativeCoin = new GameObject("cn", "Moneda Falsa", -5);
        assertEquals(0, negativeCoin.getCoinValue());

        GameObject notACoin = new GameObject();
        assertEquals(0, notACoin.getCoinValue());
    }

    @Test
    void testForgeable() {
        GameObject armor = new GameObject("a_1", "Armadura", 0, 0, 5, 0);
        assertTrue(armor.isForgeable());

        GameObject weapon = new GameObject("w_1", "Arma", 0, 5, 0, 0);
        assertTrue(weapon.isForgeable());

        GameObject forgedWeapon = weapon.forged(1);
        assertFalse(forgedWeapon.isForgeable());

        GameObject potion = new GameObject();
        assertFalse(potion.isForgeable());
    }

    @Test
    void testForgingArmor() {
        GameObject armor = new GameObject("a_1", "Armadura", 0, 0, 5, 0);

        GameObject forged1 = armor.forged(0);
        assertEquals(6, forged1.getDefenseBonus());
        assertTrue(forged1.getName().contains("Nvl 1"));
        assertTrue(forged1.getId().contains("-forged-1"));

        GameObject forged2 = armor.forged(2);
        assertEquals(7, forged2.getDefenseBonus());
        assertTrue(forged2.getName().contains("Nvl 2"));

        GameObject forged3 = armor.forged(5);
        assertEquals(9, forged3.getDefenseBonus());
        assertTrue(forged3.getName().contains("Nvl 3"));
    }

    @Test
    void testForgingRangedWeapon() {
        GameObject ranged = new GameObject("r_1", "Arco", 0, 5, 0, 0, "A distancia");

        GameObject forged1 = ranged.forged(1);
        assertEquals(6, forged1.getDamageBonus());

        GameObject forged2 = ranged.forged(2);
        assertEquals(7, forged2.getDamageBonus());

        GameObject forged3 = ranged.forged(3);
        assertEquals(9, forged3.getDamageBonus());
    }

    @Test
    void testForgingMeleeWeapon() {
        GameObject melee = new GameObject("m_1", "Espada", 0, 5, 0, 0, "Cuerpo a cuerpo");

        GameObject forged1 = melee.forged(1);
        assertEquals(6, forged1.getDamageBonus());

        GameObject forged2 = melee.forged(2);
        assertEquals(8, forged2.getDamageBonus());

        GameObject forged3 = melee.forged(3);
        assertEquals(11, forged3.getDamageBonus());
    }

    @Test
    void testBonusDescriptionAndToString() {
        GameObject coin1 = new GameObject("c1", "Moneda", 1);
        assertEquals("1 moneda", coin1.bonusDescription());
        assertEquals("Moneda", coin1.toString());

        GameObject coin5 = new GameObject("c5", "Monedas", 5);
        assertEquals("5 monedas", coin5.bonusDescription());

        GameObject empty = new GameObject();
        assertEquals("Sin modificadores", empty.bonusDescription());

        GameObject fullPositive = new GameObject("f_1", "Poderoso", 10, 5, 2, 3, 1, "Magia", GameObject.TYPE_WEAPON);
        String desc = fullPositive.bonusDescription();
        assertTrue(desc.contains("+10 vida"));
        assertTrue(desc.contains("+5 vida maxima"));
        assertTrue(desc.contains("+2 ataque"));
        assertTrue(desc.contains("+3 defensa"));
        assertTrue(desc.contains("+1 velocidad"));
        assertTrue(desc.contains("Magia"));

        GameObject fullNegative = new GameObject("f_2", "Maldito", 0, 0, -2, -3, -1, null, GameObject.TYPE_WEAPON);
        String descNeg = fullNegative.bonusDescription();
        assertTrue(descNeg.contains("-2 ataque"));
        assertTrue(descNeg.contains("-3 defensa"));
        assertTrue(descNeg.contains("-1 velocidad"));
    }

    @Test
    void testExhaustiveTypeAndBranches() {
        GameObject infArmor = new GameObject("id", "infA", 0, 0, 5, 0, null);
        assertEquals(GameObject.TYPE_ARMOR, infArmor.getType());

        GameObject infWeap1 = new GameObject("id", "infW1", 0, 0, 5, 0, "Melee");
        assertEquals(GameObject.TYPE_WEAPON, infWeap1.getType());

        GameObject infWeap2 = new GameObject("id", "infW2", 0, 5, 5, 0, null);
        assertEquals(GameObject.TYPE_WEAPON, infWeap2.getType());

        GameObject infWeap3 = new GameObject("id", "infW3", 0, 0, 0, 0, "Melee");
        assertEquals(GameObject.TYPE_WEAPON, infWeap3.getType());

        GameObject infWeap4 = new GameObject("id", "infW4", 0, 5, 0, 0, null);
        assertEquals(GameObject.TYPE_WEAPON, infWeap4.getType());

        GameObject infPotion = new GameObject("id", "infP", 0, 0, 0, 0, null);
        assertEquals(GameObject.TYPE_POTION, infPotion.getType());

        GameObject w1 = new GameObject("id", "w1", 0, 0, 0, 0, 0, null, GameObject.TYPE_WEAPON);
        assertTrue(w1.isWeapon());

        GameObject w2 = new GameObject("id", "w2", 0, 0, 0, 0, 0, "Melee", "custom");
        assertTrue(w2.isWeapon());

        GameObject w3 = new GameObject("id", "w3", 0, 0, 5, 0, 0, null, "custom");
        assertTrue(w3.isWeapon());

        GameObject w4 = new GameObject("id", "w4", 0, 0, 0, 0, 0, null, "custom");
        assertFalse(w4.isWeapon());

        GameObject a1 = new GameObject("id", "a1", 0, 0, 0, 0, 0, null, GameObject.TYPE_ARMOR);
        assertTrue(a1.isArmor());

        GameObject a2 = new GameObject("id", "a2", 0, 0, 0, 5, 0, null, "custom");
        assertTrue(a2.isArmor());

        GameObject a3 = new GameObject("id", "a3", 0, 0, 0, 0, 0, null, "custom");
        assertFalse(a3.isArmor());

        assertEquals("w1", w1.toString());
        assertEquals(GameObject.TYPE_WEAPON, w1.getType());
    }
}