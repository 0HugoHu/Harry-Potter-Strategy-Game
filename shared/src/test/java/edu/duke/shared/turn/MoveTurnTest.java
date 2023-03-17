package edu.duke.shared.turn;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.duke.shared.map.GameMap;

class MoveTurnTest {

    @Test
    void addMove() {
        GameMap gm = new GameMap(20, 30, 6);
        MoveTurn mt = new MoveTurn(gm, 0, "Hello");
        Move m = new Move("Terr0", "Terr1", 1, "Hello");
        mt.addMove(m);
        assertEquals(mt.getMoves().get(0), m);
        assertEquals(mt.getIndex(), 0);
        assertEquals(mt.getPlayerName(), "Hello");
        assertEquals(mt.getMap(), gm);
    }

    @Test
    void getMoves() {
    }

    @Test
    void doMovePhrase() {
    }

}