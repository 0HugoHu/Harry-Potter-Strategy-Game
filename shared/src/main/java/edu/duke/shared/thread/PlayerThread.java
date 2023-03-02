package edu.duke.shared.thread;

import java.net.Socket;
import java.util.ArrayList;

import edu.duke.shared.Game;
import edu.duke.shared.Player;
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
        // TODO receive orders from player
        //
        //receive the playerList of this game
        ArrayList<Player> playerList=this.game.getPlayerList();
        //the current player can be traced by the index
        Player player=playerList.get(index);
        //set playerId by index
        player.setPlayerId(index);
        //set player socket
        player.setSocket(socket);
    }
}
