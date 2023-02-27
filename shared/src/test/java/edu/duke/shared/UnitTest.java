package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnitTest {

    /**
     * test method for unit getAttack(),getDefense() and getHp() methods
     */
    @Test
    public void test_UnitFunc() {
        String name1="Normal";
        String name2="Defense";
        String name3="Basic";
        Unit myunit1=new Unit(name1);
        Unit myunit2=new Unit(name2);
        Unit myunit3=new Unit(name3);
        assertEquals("Normal",myunit1.getName());
        assertEquals(2,myunit1.getAttack());
        assertEquals(1,myunit1.getDefense());
        assertEquals(2,myunit1.getHp());

        assertEquals("Defense",myunit2.getName());
        assertEquals(1,myunit2.getAttack());
        assertEquals(3,myunit2.getDefense());
        assertEquals(3,myunit2.getHp());

        assertEquals("Basic",myunit3.getName());
        assertEquals(0,myunit3.getAttack());
        assertEquals(0,myunit3.getDefense());
        assertEquals(0,myunit3.getHp());

    }


}