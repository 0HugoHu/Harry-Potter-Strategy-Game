package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;
import edu.duke.shared.unit.Unit;

public class TerritoryTest {

    @Test
    public void addCoordinate() {
        Territory t = new Territory("Terr0");
        int[] c= new int[]{0,0};
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
        Unit u = new Unit("Normal");
        assertTrue(t.addUnit(u));
    }

    @Test
    public void removeUnit() {
        Territory t = new Territory("Terr0");
        Unit u = new Unit("Normal");
        assertTrue(t.addUnit(u));
        assertTrue(t.removeUnit(u));
    }

    @Test
    public void getName() {
        Territory t = new Territory("Terr0");
        assertEquals("Terr0",t.getName());
    }

    @Test
    public void getOwner() {
        Territory t = new Territory("Terr0");
        assertTrue(t.changeOwner("Alice"));
        assertFalse(t.changeOwner("Alice"));
        assertEquals("Alice",t.getOwner());
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
        Unit u1=new Unit("Unit1",1,2,3);
        Unit u2=new Unit("Unit2",2,3,4);
        Unit u3=new Unit("Unit3",3,4,5);
        t.addUnit(u1);
        t.addUnit(u2);
        t.addUnit(u3);
        ArrayList<Unit> units=t.getUnits();
        assertTrue(units.contains(u1));
        assertTrue(units.contains(u2));
        assertTrue(units.contains(u3));
        assertEquals(3,units.size());
    }

    @Test
    public void removeAllUnits() {
        Territory t = new Territory("Terr0");
        Unit u1=new Unit("Unit1",1,2,3);
        Unit u2=new Unit("Unit2",2,3,4);
        Unit u3=new Unit("Unit3",3,4,5);
        t.addUnit(u1);
        t.addUnit(u2);
        t.addUnit(u3);
        ArrayList<Unit> units=t.getUnits();
        assertTrue(units.contains(u1));
        assertTrue(units.contains(u2));
        assertTrue(units.contains(u3));
        assertEquals(3,units.size());
        t.removeAllUnits();
        assertEquals(0,t.getUnits().size());
    }

    @Test
    public void removeUnitByName() {
        Territory t = new Territory("Terr0");
        Unit u1=new Unit("Unit1",1,2,3);
        Unit u2=new Unit("Unit2",2,3,4);
        Unit u3=new Unit("Unit3",3,4,5);
        t.addUnit(u1);
        t.addUnit(u2);
        t.addUnit(u3);
        ArrayList<Unit> units=t.getUnits();
        assertTrue(units.contains(u1));
        assertTrue(units.contains(u2));
        assertTrue(units.contains(u3));
        assertEquals(3, units.size());
        assertTrue(t.removeUnitByName("Unit1"));
        assertFalse(t.getUnits().contains(u1));
    }

    @Test
    public void contains() {
        Territory t = new Territory("Terr0");
        int[] c1=new int[]{0,0};
        int[] c2=new int[]{0,1};
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
        Unit u1=new Unit("Unit1",1,2,3);
        Unit u2=new Unit("Unit2",2,3,4);
        Unit u3=new Unit("Unit3",3,4,5);
        t.addUnit(u1);
        t.addUnit(u2);
        t.addUnit(u3);
        assertEquals(3,t.getNumUnits());
    }

    @Test
    public void getCoords() {
        Territory t = new Territory("Terr0");
        int[] c1=new int[]{0,0};
        int[] c2=new int[]{0,1};
        t.addCoordinate(c1);
        HashSet<int[]> h= t.getCoords();
        assertTrue(h.contains(c1));
        assertFalse(h.contains(c2));
        assertEquals(1,h.size());
    }

    @Test
    public void changePlayerOwner() {
        Territory t = new Territory("Terr0");
//        Player p=new Player("Alice");
//        assertTrue(t.changePlayerOwner(p));
//        assertFalse(t.changePlayerOwner(p));
    }

    @Test
    public void getPlayerOwner() {
        Territory t = new Territory("Terr0");
//        Player p=new Player("Alice");
//        t.changePlayerOwner(p);
//        assertEquals(t.getPlayerOwner(),p);
    }

    @Test
    public void testTerritory(){
        String owner="Alice";
//        Player p =new Player(owner);
//        String name="Terr0";
//        ArrayList<Unit> units=new ArrayList<>();
//        HashSet<int[]> coords=new HashSet<>();
//        HashSet<String> adjs=new HashSet<>();
//        Unit u1=new Unit("u1");
//        Unit u2=new Unit("u2");
//        int[] c1=new int[]{0,0};
//        int[] c2 =new int[]{1,1};
//        String adj1="adj1";
//        String adj2="adj2";
//        units.add(u1);
//        units.add(u2);
//        coords.add(c1);
//        coords.add(c2);
//        adjs.add(adj1);
//        Territory t=new Territory(name,p,owner,units,coords,adjs);
//        assertEquals(owner,t.getOwner());
//        assertEquals(p,t.getPlayerOwner());
//        assertEquals(name,t.getName());
//        assertEquals(units,t.getUnits());
//        assertEquals(coords,t.getCoords());
//        assertTrue(t.isAdjacent(adj1));
//        assertFalse(t.isAdjacent(adj2));

    }

}