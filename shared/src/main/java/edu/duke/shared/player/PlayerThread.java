package edu.duke.shared.player;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import edu.duke.shared.Game;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.helper.State;
import edu.duke.shared.helper.Validation;
import edu.duke.shared.map.Territory;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.turn.Turn;
import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

public class PlayerThread implements Runnable, Serializable {
    // Global game state of the game
    private final State state;
    // Player's socket
    public Socket socket;
    // Player's game
    public Game currGame;
    // Server's game
    private Game serverGame;
    // Player's id
    private final int playerId;
    // For testing
    public int forTesting;
    // Lock for server game
    private final ReentrantLock serverGameLock = new ReentrantLock();

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
        this.forTesting = 0;
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

    public HashMap<UnitType, Integer> convertToMap(ArrayList<Unit> units) {
        HashMap<UnitType, Integer> map = new HashMap<>();
        for (Unit unit : units) {
            map.put(unit.getType(), map.getOrDefault(unit.getType(), 0) + 1);
        }
        return map;
    }

    /**
     * Run thread to receive game object based on game state
     */
    @Override
    public void run() {
        if (state != State.WAITING_TO_JOIN && state != State.GAME_OVER && forTesting == 0) {
            GameObject obj = new GameObject(this.socket);
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
                    for (Map.Entry<UnitType, Integer> entry : convertToMap(this.currGame.getMap().getTerritory(t.getName()).getUnits()).entrySet()) {
                        for (int i = 0; i < entry.getValue(); i++) {
                            this.serverGame.getMap().getTerritory(t.getName()).addUnit(entry.getKey());
                        }
                    }
//                    for (int j = 0; j < p.getPlayerThread().getCurrGame().getMap().getTerritory(t.getName()).getNumUnits(); j++)
//                        this.serverGame.getMap().getTerritory(t.getName()).addUnit(UnitType.GNOME);
                    totalUnits += t.getNumUnits();
                }

                break;


            }
            case TURN_BEGIN:
                System.out.println("Received player " + this.playerId + "'s action list.");

                if (this.currGame.isForceEndGame()) {
                    for (Territory t : this.serverGame.getMap().getTerritories()) {
                        if (t.getOwner().equals(this.serverGame.getPlayerList().get(this.playerId).getPlayerName())) {
                            t.removeAllUnits();
                        }
                    }
                    this.serverGame.addLoserId(this.playerId);
                } else {
                    int turnIndex = this.currGame.getTurn();
                    MoveTurn moveTurn = (MoveTurn) this.currGame.getTurnList().get(turnIndex).get(this.playerId).get(0);
                    AttackTurn attackTurn = (AttackTurn) this.currGame.getTurnList().get(turnIndex).get(this.playerId).get(1);
                    if (moveTurn == null || attackTurn == null) {
                        System.out.println("The move turn or attack turn from player " + this.playerId + " is null.");
                    }
                    if (!Validation.checkMoves(moveTurn)) {
                        System.out.println("The move turn from player " + this.playerId + " is illegal.");
                    }
                    moveTurn.doMovePhase();
                    if (!Validation.checkAttacks(attackTurn)) {
                        System.out.println("The attack turn from player " + this.playerId + " is illegal.");
                    }

                    // Copy the units from the current game to the server game
                    Player p = this.serverGame.getPlayerList().get(this.playerId);
                    for (Territory t : this.serverGame.getMap().getTerritories()) {
                        if (t.getOwner().equals(p.getPlayerName())) {
                            t.removeAllUnits();
                            for (Unit unit : this.currGame.getMap().getTerritory(t.getName()).getUnits()) {
                                t.addUnit(unit);
                            }
                        }
                    }

                    // Merge all turns from different players
                    ArrayList<Turn> newTurns = this.currGame.getTurnList().get(turnIndex).get(this.playerId);
                    // For the first player, add a new turn
                    if (this.serverGame.getTurnList().size() == turnIndex || forTesting == 2) {
                        HashMap<Integer, ArrayList<Turn>> newTurnsMap = new HashMap<>();
                        newTurnsMap.put(this.playerId, newTurns);
                        this.serverGame.getTurnList().add(newTurnsMap);
                    } else {
                        this.serverGame.getTurnList().get(turnIndex).put(this.playerId, newTurns);
                    }

                    // Copy the player's property
                    this.serverGame.getPlayerList().get(this.playerId).coins = this.currGame.getPlayerList().get(this.playerId).coins;
                    this.serverGame.getPlayerList().get(this.playerId).horns = this.currGame.getPlayerList().get(this.playerId).horns;

                    // Copy the player's property
                    if (this.currGame.getPlayerList().get(this.playerId).willUpgradeWorldLevel) {
                        this.serverGame.getPlayerList().get(this.playerId).upgradeWorldLevel();
                    }
                }
                break;
            case TURN_END:
                System.out.println("Received player " + this.playerId + "'s turn end confirmation.");
            case GAME_OVER:
                break;
        }

    }
}
