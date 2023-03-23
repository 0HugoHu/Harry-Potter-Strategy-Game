package edu.duke.shared.turn;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;

public class MoveTurnTest {

    @Test
    public void addMove() {
        GameMap gm = new GameMap(20, 30, 6);
        MoveTurn mt = new MoveTurn(gm, 0, "Hello");
        Move m = new Move("Terr0", "Terr1", 1, "Hello");
        mt.addMove(m);
        Territory t = new Territory("Terr0");
        Territory t1 = new Territory("Terr1");
        gm.addTerritory(t);
        gm.addTerritory(t1);
        mt.doMovePhase();
        assertEquals(mt.getMoves().get(0), m);
        assertEquals(mt.getIndex(), 0);
        assertEquals(mt.getPlayerName(), "Hello");
        assertEquals(mt.getMap(), gm);
    }

    @Test
    public void getMoves() {
    }

    @Test
    public void doMovePhrase() {
    }
}