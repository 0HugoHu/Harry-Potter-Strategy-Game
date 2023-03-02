package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

public class GameTest {

    /**
     * test method for getNumPlayers
     */
    @Test
    public void getNumPlayers() {
        Game newGame = new Game(3);
        assertEquals(3, newGame.getNumPlayers());
    }

    /**
     * Test method for getMap
     */
    @Test
    public void getMap() {
    }
}