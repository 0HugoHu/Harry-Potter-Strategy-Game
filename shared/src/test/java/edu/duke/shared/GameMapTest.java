package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;

public class GameMapTest {

    /**
     * test method for basic Map functions
     */
    @Test
    public void getHeight() {
        ArrayList<Territory> terrList = new ArrayList<>();
        GameMap gameMap = new GameMap(15, 10, 2, terrList);
        assertEquals(15, gameMap.getHeight());
    }

    @Test
    public void getWidth() {
        ArrayList<Territory> terrList = new ArrayList<>();
        GameMap gameMap = new GameMap(10, 15, 2, terrList);
        assertEquals(15, gameMap.getWidth());
    }

    @Test
    public void getNumTerritories() {
        ArrayList<Territory> terrList = new ArrayList<>();
        GameMap gameMap = new GameMap(10, 10, 5, terrList);
        assertEquals(5, gameMap.getNumTerritories());
    }

    @Test
    public void getTerritories() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        Territory t2 = new Territory("B");
        terrList.add(t1);
        terrList.add(t2);
        GameMap gameMap = new GameMap(10, 10, 2, terrList);
        assertEquals(terrList, gameMap.getTerritories());
    }

    @Test
    public void getTerritory() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        terrList.add(t1);
        GameMap gameMap = new GameMap(10, 10, 2, terrList);
        assertEquals(t1, gameMap.getTerritory("A"));
        assertNull(gameMap.getTerritory("B"));
    }

    @Test
    public void getTerritoriesByOwner() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        t1.changeOwner("C");
        Territory t2 = new Territory("B");
        t2.changeOwner("C");
        terrList.add(t1);
        terrList.add(t2);
        GameMap gameMap = new GameMap(10, 10, 2, terrList);
        assertEquals(terrList, gameMap.getTerritoriesByOwner("C"));
    }

    @Test
    public void addTerritory() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        Territory t2 = new Territory("B");
        terrList.add(t1);
        GameMap gameMap = new GameMap(10, 10, 1, terrList);
        assertFalse(gameMap.addTerritory(t1));
        assertTrue(gameMap.addTerritory(t2));
        assertFalse(gameMap.addTerritory(t2));
    }

    @Test
    public void removeTerritory() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        Territory t2 = new Territory("B");
        terrList.add(t1);
        terrList.add(t2);
        GameMap gameMap = new GameMap(10, 10, 1, terrList);
        assertTrue(gameMap.removeTerritory("A"));
        assertFalse(gameMap.removeTerritory("A"));
    }

    @Test
    public void getTerritoryByCoord() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        int[] c1 = new int[]{0, 0};
        int[] c2 = new int[]{0, 1};
        t1.addCoordinate(c1);
        terrList.add(t1);
        GameMap gameMap = new GameMap(10, 10, 1, terrList);
        assertEquals(t1, gameMap.getTerritoryByCoord(c1));
        assertNull(gameMap.getTerritoryByCoord(c2));
    }

    @Test
    public void getOwnerByCoord() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        String owner = "Alice";
        int[] c1 = new int[]{0, 0};
        t1.addCoordinate(c1);
        t1.changeOwner(owner);
        terrList.add(t1);
        GameMap gameMap = new GameMap(10, 10, 1, terrList);
        assertEquals(owner, gameMap.getOwnerByCoord(0, 0));
        assertNull(gameMap.getOwnerByCoord(0, 1));
    }

    @Test
    public void getNameByCoord() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        int[] c1 = new int[]{0, 0};
        t1.addCoordinate(c1);
        terrList.add(t1);
        GameMap gameMap = new GameMap(10, 10, 1, terrList);
//        assertEquals("A", gameMap.getNameByCoord(0, 0));
    }


    @Test
    public void isBorderPoint() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        int[] c1 = new int[]{1, 1};
        int[] c2 = new int[]{1, 0};
        int[] c3 = new int[]{0, 1};
        int[] c4 = new int[]{2, 1};
        int[] c5 = new int[]{1, 2};
        t1.addCoordinate(c1);
        t1.addCoordinate(c2);
        t1.addCoordinate(c3);
        t1.addCoordinate(c4);
        t1.addCoordinate(c5);
        terrList.add(t1);
        GameMap gameMap = new GameMap(10, 10, 1, terrList);
        assertTrue(gameMap.completed());
        assertEquals(11,gameMap.isBorderPoint(0,1));
        assertEquals(13,gameMap.isBorderPoint(1,0));
        assertEquals(7,gameMap.isBorderPoint(1,2));
        assertEquals(14,gameMap.isBorderPoint(2,1));
        assertEquals(0,gameMap.isBorderPoint(1,1));
    }


    @Test
    public void completed() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        int[] c1 = new int[]{1, 1};
        int[] c2 = new int[]{1, 0};
        int[] c3 = new int[]{0, 1};
        int[] c4 = new int[]{2, 1};
        int[] c5 = new int[]{1, 2};
        t1.addCoordinate(c1);
        t1.addCoordinate(c2);
        t1.addCoordinate(c3);
        t1.addCoordinate(c4);
        t1.addCoordinate(c5);
        terrList.add(t1);
        GameMap gameMap = new GameMap(10, 10, 1, terrList);
        assertTrue(gameMap.completed());
    }
}