package edu.duke.shared.helper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StateTest {

    @Test
    public void values() {
        assertEquals(State.WAITING_TO_JOIN, State.valueOf("WAITING_TO_JOIN"));
        assertEquals(State.READY_TO_INIT_NAME, State.valueOf("READY_TO_INIT_NAME"));
        assertEquals(State.READY_TO_INIT_UNITS, State.valueOf("READY_TO_INIT_UNITS"));
        assertEquals(State.TURN_BEGIN, State.valueOf("TURN_BEGIN"));
        assertEquals(State.TURN_END, State.valueOf("TURN_END"));
        assertEquals(State.GAME_OVER, State.valueOf("GAME_OVER"));
    }

    @Test
    public void valueOf() {
    }
}