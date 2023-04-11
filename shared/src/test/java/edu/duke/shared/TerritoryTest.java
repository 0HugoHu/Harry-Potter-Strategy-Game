package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;
import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

public class TerritoryTest {

    @Test
    public void addCoordinate() {
        Territory t = new Territory("Terr0");
        int[] c = new int[]{0, 0};
        assertTrue(t.addCoordinate(c));
        assertFalse(t.addCoordinate(c));
    }


    @Test
    public void addAdjacent() {
        Territory t = new Territory("Terr0");
        assertTrue(t.addAdjacent("Terr1"));
        assertFalse(t.addAdjacent("Terr1"));
    }

    @Test
    public void addUnit() {
        Territory t = new Territory("Terr0");
        Unit u = new Unit("Gnome");
        assertTrue(t.addUnit(UnitType.GNOME));
    }

    @Test
    public void removeUnit() {
        Territory t = new Territory("Terr0");
        Unit u = new Unit("Gnome");
        assertTrue(t.addUnit(UnitType.GNOME));
        assertTrue(t.removeUnit(UnitType.GNOME));
    }

    @Test
    public void getName() {
        Territory t = new Territory("Terr0");
        assertEquals("Terr0", t.getName());
    }

    @Test
    public void getOwner() {
        Territory t = new Territory("Terr0");
        assertTrue(t.changeOwner("Alice"));
        assertFalse(t.changeOwner("Alice"));
        assertEquals("Alice", t.getOwner());
    }

    @Test
    public void changeOwner() {
        Territory t = new Territory("Terr0");
        assertTrue(t.changeOwner("Alice"));
        assertFalse(t.changeOwner("Alice"));
    }

    @Test
    public void getUnits() {
        Territory t = new Territory("Terr0");
        Unit u1 = new Unit("Gnome", 0);
        t.addUnit(UnitType.GNOME);
        t.addUnit(UnitType.GNOME);
        t.addUnit(UnitType.WEREWOLF);
        HashMap<UnitType,Integer> units = t.getUnits();
        assertEquals(2, units.size());
        assertEquals(2,(int)units.get(UnitType.GNOME));
    }

    @Test
    public void removeAllUnits() {
        Territory t = new Territory("Terr0");
        Unit u1 = new Unit("Gnome",0);
        t.addUnit(UnitType.GNOME);
        HashMap<UnitType,Integer> units = t.getUnits();
        assertEquals(1, units.size());
        t.removeAllUnits();
        assertEquals(0, t.getUnits().size());
    }

    @Test
    public void removeUnitByName() {
        Territory t = new Territory("Terr0");
        Unit u1 = new Unit("Gnome",0);
        t.addUnit(UnitType.GNOME);
        HashMap<UnitType,Integer> units = t.getUnits();
        assertEquals(1, units.size());
        assertTrue(t.removeUnitByName("Gnome"));
    }

    @Test
    public void contains() {
        Territory t = new Territory("Terr0");
        int[] c1 = new int[]{0, 0};
        int[] c2 = new int[]{0, 1};
        t.addCoordinate(c1);
        assertTrue(t.contains(c1));
        assertFalse(t.contains(c2));
    }

    @Test
    public void isAdjacent() {
        Territory t = new Territory("Terr0");
        t.addAdjacent("Test");
        assertTrue(t.isAdjacent("Test"));
        assertFalse(t.isAdjacent("Test2"));
    }

    @Test
    public void getNumUnits() {
        Territory t = new Territory("Terr0");
        Unit u1 = new Unit("Gnome", 0);
        t.addUnit(UnitType.GNOME);
        assertEquals(1, t.getNumUnits());
    }

    @Test
    public void getCoords() {
        Territory t = new Territory("Terr0");
        int[] c1 = new int[]{0, 0};
        int[] c2 = new int[]{0, 1};
        t.addCoordinate(c1);
        HashSet<int[]> h = t.getCoords();
        assertTrue(h.contains(c1));
        assertFalse(h.contains(c2));
        assertEquals(1, h.size());
    }

    @Test
    public void changePlayerOwner() {
        Territory t = new Territory("Terr0");
        Player p = new Player(0, new Socket());
        assertTrue(t.changePlayerOwner(p));
        assertFalse(t.changePlayerOwner(p));
    }

    @Test
    public void getPlayerOwner() {
        Territory t = new Territory("Terr0");
        Player p = new Player(0, new Socket());
        t.changePlayerOwner(p);
        assertEquals(t.getPlayerOwner(), p);
    }

    @Test
    public void testTerritory() {
        String owner = "Alice";
        Player p = new Player(0, new Socket());
        String name = "Terr0";
        HashMap<UnitType,Integer> units = new HashMap<>();
        HashSet<int[]> coords = new HashSet<>();
        HashSet<String> adjs = new HashSet<>();
        Unit u1 = new Unit("u1");
        Unit u2 = new Unit("u2");
        int[] c1 = new int[]{0, 0};
        int[] c2 = new int[]{1, 1};
        String adj1 = "adj1";
        String adj2 = "adj2";
        units.put(UnitType.GNOME,2);
        coords.add(c1);
        coords.add(c2);
        adjs.add(adj1);
        Territory t = new Territory(name, p, owner, units, coords, adjs,"lll","plain");
        assertEquals(owner, t.getOwner());
        assertEquals(p, t.getPlayerOwner());
        assertEquals(name, t.getName());
        assertEquals(units, t.getUnits());
        assertEquals(coords, t.getCoords());
        assertTrue(t.isAdjacent(adj1));
        assertFalse(t.isAdjacent(adj2));
        assertEquals("lll",t.getDetails());
        assertEquals("plain",t.getType());

    }
    
    @Test
    public void testAdjacent() {
        Territory t = new Territory("Terr0");
        t.addAdjacent("Test");
        assertTrue(t.isAdjacent("Test"));
        assertFalse(t.isAdjacent("Test2"));
        assertTrue(t.getAdjacents().contains("Test"));
        Unit u1 = new Unit("Dwarf");
        Unit u2 = new Unit("Gnome");
        t.addUnit(UnitType.GNOME);
        assertFalse(t.removeUnit(u2));

        assertFalse(t.removeUnit(UnitType.GNOME));
        assertTrue(t.removeUnit(UnitType.DWARF));
        t.addUnit(UnitType.GNOME);
        assertFalse(t.removeUnit());
        assertFalse(t.removeUnitByName("Gnome"));
    }

}