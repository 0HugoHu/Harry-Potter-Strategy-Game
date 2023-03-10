package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.duke.shared.unit.Unit;

public class UnitTest {

    @Test
    public void constructorFullProperties(){
        Unit myunit1=new Unit("Normal",2,3,4);
        assertEquals("Normal", myunit1.getName());
        assertEquals(2, myunit1.getAttack());
        assertEquals(3, myunit1.getDefense());
        assertEquals(4, myunit1.getHp());
    }
    @Test
    public void getName() {
        String name1 = "Normal";
        String name2 = "Defense";
        String name3 = "Basic";
        Unit myunit1 = new Unit(name1);
        Unit myunit2 = new Unit(name2);
        Unit myunit3 = new Unit(name3);
        assertEquals("Normal", myunit1.getName());
        assertEquals("Defense", myunit2.getName());
        assertEquals("Basic", myunit3.getName());


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