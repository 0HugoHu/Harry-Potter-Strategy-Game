package edu.duke.shared.helper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HeaderTest {

    @Test
    void setWinnerId() {
        Header header = new Header();
        header.setWinnerId(1);
        assertEquals(1, header.getWinnerId());
    }

    @Test
    void getWinnerId() {
    }

    @Test
    void addLoserId() {
        Header header = new Header();
        header.addLoserId(1);
        assertTrue(header.isLoser(1));
        assertFalse(header.isLoser(2));
    }

    @Test
    void isLoser() {
    }

    @Test
    void setPlayerId() {
        Header header = new Header();
        header.setPlayerId(1);
        assertEquals(1, header.getPlayerId());
    }

    @Test
    void getPlayerId() {
    }

    @Test
    void setPlayerName() {
        Header header = new Header();
        header.setPlayerName("test");
        assertEquals("test", header.getPlayerName());
    }

    @Test
    void getPlayerName() {
    }

    @Test
    void setState() {
        Header header = new Header();
        header.setState(State.WAITING_TO_JOIN);
        assertEquals(State.WAITING_TO_JOIN, header.getState());
    }

    @Test
    void getState() {
    }

    @Test
    void turnComplete() {
        Header header = new Header();
        header.turnComplete();
        assertEquals(1, header.getTurn());
    }

    @Test
    void getTurn() {
    }
}