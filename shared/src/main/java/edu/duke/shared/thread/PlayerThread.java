package edu.duke.shared.thread;

import java.net.Socket;

import edu.duke.shared.Game;
import edu.duke.shared.thread.BaseThread;

public class PlayerThread extends BaseThread implements Runnable {
    // Index for the client socket
    private final int index;

    public PlayerThread(Socket socket, Game game, int index) {
        super(socket, game);
        this.index = index;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }
}
