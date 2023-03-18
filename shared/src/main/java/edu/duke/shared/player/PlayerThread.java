package edu.duke.shared.player;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.duke.shared.Game;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.helper.State;
import edu.duke.shared.helper.Validation;
import edu.duke.shared.map.Territory;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.turn.Turn;
import edu.duke.shared.unit.Unit;

public class PlayerThread implements Runnable, Serializable {
    // Global game state of the game
    private final State state;
    // Player's socket
    public Socket socket;
    // Player's game
    private Game currGame;
    // Server's game
    private Game serverGame;
    // Player's id
    private final int playerId;

    /**
     * Initialize the PlayerThread
     *
     * @param socket   player socket
     * @param playerId player id
     */
    public PlayerThread(Socket socket, int playerId) {
        this(State.WAITING_TO_JOIN, socket, playerId);
    }

    /**
     * Initialize the PlayerThread
     *
     * @param state    game state
     * @param socket   player socket
     * @param playerId player id
     */
    public PlayerThread(State state, Socket socket, int playerId) {
        this.state = state;
        this.socket = socket;
        this.playerId = playerId;
    }

    /**
     * get player id
     *
     * @return player id
     */
    public Game getCurrGame() {
        return this.currGame;
    }

    /**
     * set server game
     */
    public void setServerGame(Game serverGame) {
        this.serverGame = serverGame;
    }

    /**
     * Run thread to receive game object based on game state
     */
    @Override
    public void run() {
        if (state != State.WAITING_TO_JOIN && state != State.TURN_END && state != State.GAME_OVER) {
            GameObject obj = new GameObject(this.socket);
            // TODO: WUYU Check game object correctness every time, that if (Game) conversion fails, then continue to wait for the next object
            this.currGame = (Game) obj.decodeObj();
        }

        switch (state) {
            case WAITING_TO_JOIN:
                break;
            case READY_TO_INIT_NAME: {
                System.out.println("Received player " + this.playerId + "'s name: " + this.currGame.getPlayerName());

                Player p = this.serverGame.getPlayerList().get(this.playerId);
                p.setPlayerName(this.currGame.getPlayerName());
                break;
            }
            case READY_TO_INIT_UNITS: {
                System.out.println("Received player " + this.playerId + "'s unit init info.");
                Player p = this.serverGame.getPlayerList().get(this.playerId);
                HashSet<Territory> terr_set = p.getPlayerTerrs();
                int totalUnits = 0;
                for (Territory t : terr_set) {
                    this.serverGame.getMap().getTerritory(t.getName()).removeAllUnits();
                    for (int j = 0; j < p.getPlayerThread().getCurrGame().getMap().getTerritory(t.getName()).getNumUnits(); j++)
                        this.serverGame.getMap().getTerritory(t.getName()).addUnit(new Unit("Normal"));
                    totalUnits += t.getNumUnits();
                }

                break;
            }
            case TURN_BEGIN:
                // TODO: WUYU Check on the server side again for both move and attack
                System.out.println("Received player " + this.playerId + "'s action list.");

                int turnIndex = this.currGame.getTurn();
                MoveTurn moveTurn = (MoveTurn) this.currGame.getTurnList().get(turnIndex).get(this.playerId).get(0);
                if (!Validation.checkMoves(moveTurn)) {
                    System.out.println("The move turn from player " + this.playerId + " is illegal.");
                }
                moveTurn.doMovePhrase();

                // Merge Unit
                Player p = this.serverGame.getPlayerList().get(this.playerId);
                for (Territory t : this.serverGame.getMap().getTerritories()) {
                    if (t.getOwner().equals(p.getPlayerName())) {
                        t.removeAllUnits();
                        for (int j = 0; j < this.currGame.getMap().getTerritory(t.getName()).getNumUnits(); j++)
                            t.addUnit(new Unit("Normal"));
                    }
                }

                // Merge all turns from different players
                ArrayList<Turn> newTurns = this.currGame.getTurnList().get(turnIndex).get(this.playerId);
                // For the first player, add a new turn
                if (this.serverGame.getTurnList().size() == turnIndex) {
                    HashMap<Integer, ArrayList<Turn>> newTurnsMap = new HashMap<>();
                    newTurnsMap.put(this.playerId, newTurns);
                    this.serverGame.getTurnList().add(newTurnsMap);
                } else {
                    this.serverGame.getTurnList().get(turnIndex).put(this.playerId, newTurns);
                }

                // Attack cannot be done here
                break;
            case TURN_END:
            case GAME_OVER:
                break;
        }

    }
}
