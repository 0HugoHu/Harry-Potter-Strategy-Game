package edu.duke.shared.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class UnitTest {

    @Test
    public void constructorFullProperties() {
        Unit myunit1 = new Unit("Normal", 2, 3, 4);
        assertEquals(UnitType.NORMAL, myunit1.getType());
        assertEquals(2, myunit1.getAttack());
        assertEquals(3, myunit1.getDefense());
        assertEquals(4, myunit1.getHp());
    }

    @Test
    public void getName() {
        String name1 = "Normal";
        String name2 = "Defense";
        String name3 = "Basic";
        String name4 = "XXx";
        Unit myunit1 = new Unit(name1);
        Unit myunit2 = new Unit(name2);
        Unit myunit3 = new Unit(name3);
        Unit myunit4 = new Unit(name4);
        assertEquals(UnitType.NORMAL, myunit1.getType());
        assertEquals(UnitType.DEFENSE, myunit2.getType());
        assertEquals(UnitType.BASIC, myunit3.getType());
        assertEquals(UnitType.NORMAL, myunit4.getType());
    }

    @Test
    public void getAttack() {
        String name1 = "Normal";
        String name2 = "Defense";
        String name3 = "Basic";
        Unit myunit1 = new Unit(name1);
        Unit myunit2 = new Unit(name2);
        Unit myunit3 = new Unit(name3);
        assertEquals(2, myunit1.getAttack());
        assertEquals(1, myunit2.getAttack());
        assertEquals(0, myunit3.getAttack());
    }

    @Test
    public void getDefense() {
        String name1 = "Normal";
        String name2 = "Defense";
        String name3 = "Basic";
        Unit myunit1 = new Unit(name1);
        Unit myunit2 = new Unit(name2);
        Unit myunit3 = new Unit(name3);
        assertEquals(1, myunit1.getDefense());
        assertEquals(3, myunit2.getDefense());
        assertEquals(0, myunit3.getDefense());
    }

    @Test
    public void getHp() {
        String name1 = "Normal";
        String name2 = "Defense";
        String name3 = "Basic";
        Unit myunit1 = new Unit(name1);
        Unit myunit2 = new Unit(name2);
        Unit myunit3 = new Unit(name3);
        assertEquals(2, myunit1.getHp());
        assertEquals(3, myunit2.getHp());
        assertEquals(0, myunit3.getHp());
    }
}