package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

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
        Territory t2 = new Territory("B");
        terrList.add(t1);
        terrList.add(t2);
        GameMap gameMap = new GameMap(10, 10, 2, terrList);
        assertEquals(t1, gameMap.getTerritory("A"));
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

    }

    @Test
    public void removeTerritory() {
    }

    @Test
    public void getTerritoryByCoord() {
    }
}