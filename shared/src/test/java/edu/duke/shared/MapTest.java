package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

public class MapTest {

    /**
     * test method for basic Map functions
     */
    @Test
    public void getHeight() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Map map = new Map(15, 10, 2, terrList);
        assertEquals(15, map.getHeight());
    }

    @Test
    public void getWidth() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Map map = new Map(10, 15, 2, terrList);
        assertEquals(15, map.getWidth());
    }

    @Test
    public void getNumTerritories() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Map map = new Map(10, 10, 5, terrList);
        assertEquals(5, map.getNumTerritories());
    }

    @Test
    public void getTerritories() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        Territory t2 = new Territory("B");
        terrList.add(t1);
        terrList.add(t2);
        Map map = new Map(10, 10, 2, terrList);
        assertEquals(terrList, map.getTerritories());
    }

    @Test
    public void getTerritory() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("A");
        Territory t2 = new Territory("B");
        terrList.add(t1);
        terrList.add(t2);
        Map map = new Map(10, 10, 2, terrList);
        assertEquals(t1, map.getTerritory("A"));
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
        Map map = new Map(10, 10, 2, terrList);
        assertEquals(terrList, map.getTerritoriesByOwner("C"));
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