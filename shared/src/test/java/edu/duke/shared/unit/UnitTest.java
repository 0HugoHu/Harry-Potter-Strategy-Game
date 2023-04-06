package edu.duke.shared.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class UnitTest {

    @Test
    public void constructorFullProperties() {
        Unit myunit1 = new Unit("Gnome", 2, 3, 4,0);
        assertEquals(UnitType.GNOME, myunit1.getType());
    }

    @Test
    public void getName() {
        String name1 = "Gnome";
        String name2 = "Dwarf";
        String name3 = "House-elf";
        String name4 = "XXx";
        Unit myunit1 = new Unit(name1);
        Unit myunit2 = new Unit(name2);
        Unit myunit3 = new Unit(name3);
        Unit myunit4 = new Unit(name4);
        assertEquals(UnitType.GNOME, myunit1.getType());
        assertEquals(UnitType.DWARF, myunit2.getType());
        assertEquals(UnitType.HOUSE_ELF, myunit3.getType());
        assertEquals(UnitType.GNOME, myunit4.getType());
    }

    @Test
    public void getAttack() {
        String name1 = "Gnome";
        String name2 = "Dwarf";
        String name3 = "House-elf";
        Unit myunit1 = new Unit(name1);
        Unit myunit2 = new Unit(name2);
        Unit myunit3 = new Unit(name3);
    }

    @Test
    public void getDefense() {
        String name1 = "Gnome";
        String name2 = "Dwarf";
        String name3 = "House-elf";
        Unit myunit1 = new Unit(name1);
        Unit myunit2 = new Unit(name2);
        Unit myunit3 = new Unit(name3);
    }

    @Test
    public void getHp() {
        String name1 = "Gnome";
        String name2 = "Dwarf";
        String name3 = "House-elf";
        Unit myunit1 = new Unit(name1);
        Unit myunit2 = new Unit(name2);
        Unit myunit3 = new Unit(name3);
    }
}