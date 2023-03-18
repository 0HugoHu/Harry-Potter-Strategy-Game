package edu.duke.shared.player;

import static org.junit.Assert.*;

import org.junit.Test;

import java.net.Socket;

import edu.duke.shared.helper.State;

public class PlayerThreadTest {

    @Test
    public void run() {
        PlayerThread playerThread = new PlayerThread(State.WAITING_TO_JOIN, new Socket(), 0);
        playerThread.setServerGame(null);
        assertNull(playerThread.getCurrGame());
        playerThread.run();

        playerThread = new PlayerThread(State.GAME_OVER, new Socket(), 0);
        playerThread.run();

        playerThread = new PlayerThread(State.TURN_END, new Socket(), 0);
        playerThread.run();


    }
}