package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class PlayerTest {

    @Test
    public void getPlayerName() {
        Player p = new Player("Alice");
        assertSame("Alice", p.getPlayerName());
    }

    @Test
    public void setPlayerName() {
        Player p = new Player("Alice");
        p.setPlayerName("Bob");
        assertSame("Bob", p.getPlayerName());
    }

    @Test
    public void getPlayerId() {
        Player p = new Player("Alice");
        p.setPlayerId(1);
        assertEquals(1, p.getPlayerId());
    }

    @Test
    public void setPlayerId() {
        Player p = new Player("Alice");
        p.setPlayerId(1);
        assertEquals(1, p.getPlayerId());
    }

    @Test
    public void expandTerr() {
        Player p = new Player("Alice");
        Territory t = new Territory("Terr0");
        assertTrue(p.expandTerr(t));
        assertFalse(p.expandTerr(t));
    }

    @Test
    public void removeTerr() {
        Player p = new Player("Alice");
        Territory t = new Territory("Terr0");
        p.expandTerr(t);
        assertTrue(p.removeTerr(t));
        assertFalse(p.removeTerr(t));
    }

    @Test
    public void getPlayerTerrs() {
        Player p = new Player("Alice");
        Territory t = new Territory("Terr0");
        p.expandTerr(t);
        HashSet<Territory> a = p.getPlayerTerrs();
        assertEquals(1, a.size());
        assertTrue(a.contains(t));
    }

    @Test
    public void getSocket() {
        Player p = new Player("Alice");
        Socket socket = new Socket();
        p.setSocket(socket);
        assertSame(p.getSocket(), socket);
    }

    @Test
    public void setSocket() {
        Player p = new Player("Alice");
        Socket socket = new Socket();
        p.setSocket(socket);
        assertSame(p.getSocket(), socket);
    }
}