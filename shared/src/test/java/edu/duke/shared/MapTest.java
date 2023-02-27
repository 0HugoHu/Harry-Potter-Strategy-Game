package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

public class MapTest {

    /**
     * test method for basic Map functions
     */
    @Test
    public void test_map_funcs(){
        Territory t1=new Territory("A");
        Territory t2=new Territory("B");
        ArrayList<Territory> terrList=new ArrayList<>();
        terrList.add(t1);
        terrList.add(t2);
        Map map=new Map(10,10,2,terrList);
        assertEquals(10,map.getHeight());
        assertEquals(10,map.getWidth());
        assertEquals(2,map.getNumTerritories());
        assertEquals(t1,map.getTerritory("A"));
    }
}