package edu.duke.shared.player;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

import edu.duke.shared.Game;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.Territory;
import edu.duke.shared.unit.Unit;

public class PlayerThread implements Runnable, Serializable {

    private final State state;
    private final Socket socket;
    private Game currGame;

    private final int playerId;

    public PlayerThread(Socket socket, int playerId) {
        this(State.WAITING_TO_JOIN, socket, playerId);
    }

    public PlayerThread(State state, Socket socket, int playerId) {
        this.state = state;
        this.socket = socket;
        this.playerId = playerId;
    }

    public Game getCurrGame() {
        return this.currGame;
    }


    @Override
    public void run() {
        if (state != State.WAITING_TO_JOIN) {
            GameObject obj = new GameObject(this.socket);
            this.currGame = (Game) obj.decodeObj();
        }

        switch (state) {
            case WAITING_TO_JOIN:
                break;
            case READY_TO_INIT_NAME: {
                System.out.println("Received player " + this.playerId + "'s name: " + this.currGame.getPlayerName());
                break;
            }
            case READY_TO_INIT_UNITS: {
                System.out.println("Received player " + this.playerId + "'s unit init info.");
                break;
            }
            case TURN_BEGIN:
                System.out.println("Received player " + this.playerId + "'s action list.");
                break;
            case TURN_END:
                break;
        }

        // TODO: Check game object correctness every time
    }
}
