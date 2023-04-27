package edu.duke.shared.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.duke.shared.player.Horcrux;

public class HeaderTest {

    @Test
    public void setWinnerId() {
        Header header = new Header();
        header.setWinnerId(1);
        assertEquals(1, header.getWinnerId());
    }

    @Test
    public void getWinnerId() {
    }

    @Test
    public void addLoserId() {
        Header header = new Header();
        header.addLoserId(1);
        assertTrue(header.isLoser(1));
        assertFalse(header.isLoser(2));
    }

    @Test
    public void isLoser() {
    }

    @Test
    public void setPlayerId() {
        Header header = new Header();
        header.setPlayerId(1);
        assertEquals(1, header.getPlayerId());
    }

    @Test
    public void getPlayerId() {
    }

    @Test
    public void setPlayerName() {
        Header header = new Header();
        header.setPlayerName("test");
        assertEquals("test", header.getPlayerName());
    }

    @Test
    public void getPlayerName() {
    }

    @Test
    public void setState() {
        Header header = new Header();
        header.setState(State.WAITING_TO_JOIN);
        assertEquals(State.WAITING_TO_JOIN, header.getState());
    }

    @Test
    public void getState() {
    }

    @Test
    public void turnComplete() {
        Header header = new Header();
        header.turnComplete();
        assertEquals(1, header.getTurn());
    }

    @Test
    public void getTurn() {
    }

    @Test
    public void forceEndGame() {
        Header header = new Header();
        header.forceEndGame();
        assertTrue(header.isForceEndGame());

    }

    @Test
    public void setNewHorcrux() {
        Header h = new Header();
        assertNull(h.getNewHorcrux());
        h.setNewHorcrux(Horcrux.CUP, 1);
        assertEquals("Hufflepuff's Cup%1", h.getNewHorcrux());

    }

    @Test
    public void setNoHorcrux() {
        Header h = new Header();
        assertNull(h.getNewHorcrux());
        h.setNewHorcrux(Horcrux.CUP, 1);
        assertEquals("Hufflepuff's Cup%1", h.getNewHorcrux());
        h.setNoHorcrux();
        assertNull(h.getNewHorcrux());
    }

    @Test
    public void getNewHorcrux() {
        Header h = new Header();

        h.setNewHorcrux(Horcrux.HAT, 1);
        assertEquals("Ravenclaw's Diadem%1", h.getNewHorcrux());

        h.setNewHorcrux(Horcrux.DIARY, 1);
        assertEquals("Riddle's Diary%1", h.getNewHorcrux());

        h.setNewHorcrux(Horcrux.LOCKET, 1);
        assertEquals("Slytherin's Locket%1", h.getNewHorcrux());

        h.setNewHorcrux(Horcrux.RING, 1);
        assertEquals("Gaunt's Ring%1", h.getNewHorcrux());

        h.setNewHorcrux(Horcrux.CUP, 1);
        assertEquals("Hufflepuff's Cup%1", h.getNewHorcrux());

        h.setNewHorcrux(Horcrux.SNAKE, 1);
        assertEquals("Nagini%1", h.getNewHorcrux());

    }
}