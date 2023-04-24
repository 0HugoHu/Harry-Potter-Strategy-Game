package edu.duke.shared.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;


public class UnitTest {

    @Test
    public void constructorFullProperties() {
        Unit myunit1 = new Unit("Gnome", 0);
        assertEquals(UnitType.GNOME, myunit1.getType());
    }

    @Test
    public void getName2() {
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

    @Test
    public void unit() {
        Unit myunit1 = new Unit("Gnome");
        Unit myunit2 = new Unit("Dwarf");
        Unit myunit3 = new Unit("House-elf");
        Unit myunit4 = new Unit("Goblin");
        Unit myunit5 = new Unit("Vampire");
        Unit myunit6 = new Unit("Centaur");
        Unit myunit7 = new Unit("Werewolf");
        Unit myunit8 = new Unit("XXx");
        assertEquals(UnitType.GNOME, myunit1.getType());
        assertEquals(UnitType.DWARF, myunit2.getType());
        assertEquals(UnitType.HOUSE_ELF, myunit3.getType());
        assertEquals(UnitType.GOBLIN, myunit4.getType());
    }

    @Test
    public void getCost() {
        Unit myunit1 = new Unit("Gnome");
        int cost = myunit1.getCost();
        int bonus = myunit1.getBonus();
    }

    @Test
    public void convertType() {
        Unit myunit1 = new Unit("Gnome");
        Unit myunit2 = new Unit("Dwarf");
        Unit myunit3 = new Unit("House-elf");
        Unit myunit4 = new Unit("Goblin");
        Unit myunit5 = new Unit("Vampire");
        Unit myunit6 = new Unit("Centaur");
        Unit myunit7 = new Unit("Werewolf");
        Unit myunit8 = new Unit("XXx");
        String name = Unit.convertUnitTypeToString(myunit1.getType());
        String name2 = Unit.convertUnitTypeToString(myunit2.getType());
        String name3 = Unit.convertUnitTypeToString(myunit3.getType());
        String name4 = Unit.convertUnitTypeToString(myunit4.getType());
        String name5 = Unit.convertUnitTypeToString(myunit5.getType());
        String name6 = Unit.convertUnitTypeToString(myunit6.getType());
        String name7 = Unit.convertUnitTypeToString(myunit7.getType());
        String name8 = Unit.convertUnitTypeToString(myunit8.getType());
    }

    @Test
    public void getName() {
        Unit myunit1 = new Unit("Gnome");
        Unit myunit2 = new Unit("Dwarf");
        Unit myunit3 = new Unit("House-elf");
        Unit myunit4 = new Unit("Goblin");
        Unit myunit5 = new Unit("Vampire");
        Unit myunit6 = new Unit("Centaur");
        Unit myunit7 = new Unit("Werewolf");
        Unit myunit8 = new Unit("XXx");
        String name = Unit.getName(UnitType.GNOME);
        String name2 = Unit.getName(UnitType.DWARF);
        String name3 = Unit.getName(myunit3.getType());
        String name4 = Unit.getName(myunit4.getType());
        String name5 = Unit.getName(myunit5.getType());
        String name6 = Unit.getName(myunit6.getType());
        String name7 = Unit.getName(myunit7.getType());
        String name8 = Unit.getName(myunit8.getType());
    }

    @Test
    public void getNextLevel() {
        Unit myunit1 = new Unit("Gnome");
        Unit myunit2 = new Unit("Dwarf");
        Unit myunit3 = new Unit("House-elf");
        Unit myunit4 = new Unit("Goblin");
        Unit myunit5 = new Unit("Vampire");
        Unit myunit6 = new Unit("Centaur");
        Unit myunit7 = new Unit("Werewolf");
        Unit myunit8 = new Unit("XXx");
        ArrayList<String> units = Unit.getNextLevel(myunit1.getType(), 1);
        ArrayList<String> units2 = Unit.getNextLevel(myunit2.getType(), 2);
        ArrayList<String> units3 = Unit.getNextLevel(myunit3.getType(), 3);
        ArrayList<String> units4 = Unit.getNextLevel(myunit4.getType(), 4);
        ArrayList<String> units5 = Unit.getNextLevel(myunit5.getType(), 5);
        ArrayList<String> units6 = Unit.getNextLevel(myunit6.getType(), 6);
        ArrayList<String> units7 = Unit.getNextLevel(myunit7.getType(), 7);
    }
}