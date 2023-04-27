package edu.duke.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.Socket;
import java.util.HashSet;

import edu.duke.shared.helper.State;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;

@ExtendWith(MockitoExtension.class)
public class PlayerTest {

    @Test
    public void getPlayerName() {
        Player p = new Player(0, new Socket());
        p.setPlayerName("Alice");
        assertSame("Alice", p.getPlayerName());
    }

    @Test
    public void setPlayerName() {
        Player p = new Player(0, new Socket());
        p.setPlayerName("Bob");
        assertSame("Bob", p.getPlayerName());
    }

    @Test
    public void getPlayerId() {
        Player p = new Player(0, new Socket());
        p.setPlayerId(1);
        assertEquals(1, p.getPlayerId());
    }

    @Test
    public void setPlayerId() {
        Player p = new Player(0, new Socket());
        p.setPlayerId(1);
        assertEquals(1, p.getPlayerId());
    }

    @Test
    public void expandTerr() {
        Player p = new Player(0, new Socket());
        Territory t = new Territory("Terr0");
        assertTrue(p.expandTerr(t));
        assertFalse(p.expandTerr(t));
    }

    @Test
    public void removeTerr() {
        Player p = new Player(0, new Socket());
        Territory t = new Territory("Terr0");
        p.expandTerr(t);
        assertTrue(p.removeTerr(t));
        assertFalse(p.removeTerr(t));
    }

    @Test
    public void getPlayerTerrs() {
        Player p = new Player(0, new Socket());
        Territory t = new Territory("Terr0");
        p.expandTerr(t);
        HashSet<Territory> a = p.getPlayerTerrs();
        assertEquals(1, a.size());
        assertTrue(a.contains(t));
    }

    @Test
    public void getSocket() {
        Player p = new Player(0, new Socket());
        Socket socket = new Socket();
        p.setSocket(socket);
        assertSame(p.getSocket(), socket);
    }

    @Test
    public void setSocket() {
        Player p = new Player(0, new Socket());
        Socket socket = new Socket();
        p.setSocket(socket);
        assertSame(p.getSocket(), socket);
    }


    @Test
    public void getPlayerThread() {
//        Player p = new Player(0, new Socket());
//        p.start(State.WAITING_TO_JOIN);
//        assertNotEquals(p.getPlayerThread(), new PlayerThread(State.WAITING_TO_JOIN, p.getSocket(), p.getPlayerId()));
//        p.threadJoin();
//
//        Player player = new Player(0, new Socket());
//        player.thread = mock(Thread.class);
//        try {
//            doThrow(new InterruptedException()).when(player.thread).join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        p.start(State.WAITING_TO_JOIN);
//        player.threadJoin();
    }

    @Test
    public void testWorldLevel() {
        Player p = new Player(0, new Socket());
        assertEquals(1, p.getWorldLevel());
    }

    @Test
    public void TestupgradeCost() {
//        Player p = new Player(0, new Socket());
//        assertEquals(20,p.upgradeCost(2));
//        assertEquals(40,p.upgradeCost(3));
//        assertEquals(80,p.upgradeCost(4));
//        assertEquals(160,p.upgradeCost(5));
//        assertEquals(320,p.upgradeCost(6));
    }

    @Test
    public void testGetResources() {
        Territory t1 = new Territory("A");
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("Alice");

        t1.changePlayerOwner(p1);
        t1.addCoins(100);
        t1.addHorns(40);

        p1.expandTerr(t1);
//
//        assertEquals(100,(int)p1.getAllRes()[0]);
//        assertEquals(40,(int)p1.getAllRes()[1]);
//
//        t1.addCoins(20);
//        assertEquals(120,(int)p1.getAllRes()[0]);
    }

    @Test
    public void start() {
        Player p = new Player(0, new Socket());
        Game g = new Game(2, 24);
        g.setGameState(State.GAME_OVER);
        p.start(g);
        p.threadJoin();
        p.getPlayerThread();
    }

}