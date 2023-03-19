package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.MapFactory;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;

public class GameTest {

    /**
     * test method for getNumPlayers
     */
    @Test
    public void getNumPlayers() {
        Game newGame = new Game(3,24);
        assertEquals(3, newGame.getNumPlayers());
    }

    /**
     * Test method for getMap
     */
    @Test
    public void getMap() {
        Game newGame = new Game(3,24);
        GameMap m =newGame.getMap();
        assertEquals(30, m.getHeight());
        assertEquals(60,m.getWidth());
    }

    @Test
    public void addPlayer() {
        Game newGame = new Game(3,24);
//        Player p = new Player("Alice");
//        assertTrue(newGame.addPlayer(p));
//        assertFalse(newGame.addPlayer(p));
    }

    @Test
    public void getPlayerList() {
        MapFactory map = new MapFactory(30, 60, 24);
        Game newGame = new Game(3,24, map.createRandomMap());
//        Player p1 = new Player("A");
//        Player p2 = new Player("B");
//        newGame.addPlayer(p1);
//        newGame.addPlayer(p2);
//        ArrayList<Player> t = newGame.getPlayerList();
//        assertTrue(t.contains(newGame.getPlayer("A")));
//        assertTrue(t.contains(newGame.getPlayer("B")));
//        assertFalse(t.contains(newGame.getPlayer("C")));
    }

    @Test
    public void getPlayer() {
        MapFactory map = new MapFactory(30, 60, 24);
        Game newGame = new Game(3,24, map.createRandomMap());
//        Player p1 = new Player("A");
//        Player p2 = new Player("B");
//        newGame.addPlayer(p1);
//        newGame.addPlayer(p2);
//        ArrayList<Player> t = newGame.getPlayerList();
//        assertSame(newGame.getPlayer("A"), p1);
//        assertSame(newGame.getPlayer("B"), p2);
//        assertNull(newGame.getPlayer("C"));
    }

    @Test
    public void gameDetail() {
        ArrayList<Territory> terrList = new ArrayList<>();
        Territory t1 = new Territory("Terr0");
//        Player p1 = new Player("A");
//        terrList.add(t1);
//        p1.expandTerr(t1);
//        GameMap gameMap = new GameMap(10, 10, 1, terrList);
//        Game newGame = new Game(1,gameMap);
//        newGame.addPlayer(p1);
//        assertEquals("--------------------\nA\nTerr0\n--------------------\n",newGame.GameDetail());
    }
}