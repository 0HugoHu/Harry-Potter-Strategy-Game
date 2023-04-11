package edu.duke.shared.turn;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.HashMap;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;
import edu.duke.shared.unit.UnitType;

public class MoveTurnTest {
    @Test
    public void testMove(){
        HashMap<UnitType,Integer> unitlist=new HashMap<>();
        unitlist.put(UnitType.CENTAUR,1);
        Move move=new Move("a","b",unitlist,"p");
        assertEquals(unitlist,move.getUnitList());
    }

    @Test
    public void addMove() {
        GameMap gm = new GameMap(20, 30, 6);
        MoveTurn mt = new MoveTurn(gm, 0, "Hello");
        HashMap<UnitType,Integer> unitlist=new HashMap<>();
        unitlist.put(UnitType.CENTAUR,1);
        Move move=new Move("Terr0","Terr1",unitlist,"p");
        mt.addMove(move);
        Territory t = new Territory("Terr0");
        Territory t1 = new Territory("Terr1");
        gm.addTerritory(t);
        gm.addTerritory(t1);
        mt.doMovePhase();
//        assertEquals(mt.getMoves().get(0), m);
//        assertEquals(mt.getIndex(), 0);
//        assertEquals(mt.getPlayerName(), "Hello");
//        assertEquals(mt.getMap(), gm);
    }

    @Test
    public void getMoves() {
    }

    @Test
    public void doMovePhrase() {
    }
}