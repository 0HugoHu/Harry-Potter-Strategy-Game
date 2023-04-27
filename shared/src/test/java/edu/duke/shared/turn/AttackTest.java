package edu.duke.shared.turn;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;

import edu.duke.shared.player.House;
import edu.duke.shared.unit.UnitType;

public class AttackTest {

    @Test
    public void testHighestWType() {
        HashMap<UnitType, Integer> unitlist = new HashMap<>();
        unitlist.put(UnitType.WEREWOLF, 1);
        Attack att = new Attack("a", "b", unitlist, "p", House.GRYFFINDOR);
        assertEquals(UnitType.WEREWOLF, att.getHighestType());
        assertEquals(UnitType.WEREWOLF, att.getLowestType());
    }

    @Test
    public void testHighestCType() {
        HashMap<UnitType, Integer> unitlist = new HashMap<>();
        unitlist.put(UnitType.CENTAUR, 1);
        Attack att = new Attack("a", "b", unitlist, "p", House.GRYFFINDOR);
        assertEquals(UnitType.CENTAUR, att.getHighestType());
        assertEquals(UnitType.CENTAUR, att.getLowestType());
    }

    @Test
    public void testHighestVType() {
        HashMap<UnitType, Integer> unitlist = new HashMap<>();
        unitlist.put(UnitType.VAMPIRE, 1);
        Attack att = new Attack("a", "b", unitlist, "p", House.GRYFFINDOR);
        assertEquals(UnitType.VAMPIRE, att.getHighestType());
        assertEquals(UnitType.VAMPIRE, att.getLowestType());
    }

    @Test
    public void testHighestCGType() {
        HashMap<UnitType, Integer> unitlist = new HashMap<>();
        unitlist.put(UnitType.GOBLIN, 1);
        Attack att = new Attack("a", "b", unitlist, "p", House.GRYFFINDOR);
        assertEquals(UnitType.GOBLIN, att.getHighestType());
        assertEquals(UnitType.GOBLIN, att.getLowestType());
    }

    @Test
    public void testHighestHType() {
        HashMap<UnitType, Integer> unitlist = new HashMap<>();
        unitlist.put(UnitType.HOUSE_ELF, 1);
        Attack att = new Attack("a", "b", unitlist, "p", House.GRYFFINDOR);
        assertEquals(UnitType.HOUSE_ELF, att.getHighestType());
        assertEquals(UnitType.HOUSE_ELF, att.getLowestType());
    }

    @Test
    public void testHighestDType() {
        HashMap<UnitType, Integer> unitlist = new HashMap<>();
        unitlist.put(UnitType.DWARF, 1);
        Attack att = new Attack("a", "b", unitlist, "p", House.GRYFFINDOR);
        assertEquals(UnitType.DWARF, att.getHighestType());
        assertEquals(UnitType.DWARF, att.getLowestType());
    }

    @Test
    public void testHighestMType() {
        HashMap<UnitType, Integer> unitlist = new HashMap<>();
        unitlist.put(UnitType.GNOME, 1);
        Attack att = new Attack("a", "b", unitlist, "p", House.GRYFFINDOR);
        assertEquals(UnitType.GNOME, att.getHighestType());
        assertEquals(UnitType.GNOME, att.getLowestType());
    }

    @Test
    public void testGetBonus() {
        HashMap<UnitType, Integer> unitlist = new HashMap<>();
        unitlist.put(UnitType.GNOME, 1);
        unitlist.put(UnitType.WEREWOLF, 1);
        Attack att = new Attack("a", "b", unitlist, "p", House.GRYFFINDOR);
        assertEquals(2, att.getAllUnitNums());
        att.removeUnit(UnitType.GNOME);

        Attack att2 = new Attack("s", "s", 3, "p", House.GRYFFINDOR);

        assertEquals(0, att.getBonus(UnitType.GNOME));
        assertEquals(1, att.getBonus(UnitType.DWARF));
        assertEquals(3, att.getBonus(UnitType.HOUSE_ELF));
        assertEquals(5, att.getBonus(UnitType.GOBLIN));
        assertEquals(8, att.getBonus(UnitType.VAMPIRE));
        assertEquals(11, att.getBonus(UnitType.CENTAUR));
        assertEquals(15, att.getBonus(UnitType.WEREWOLF));
    }


}