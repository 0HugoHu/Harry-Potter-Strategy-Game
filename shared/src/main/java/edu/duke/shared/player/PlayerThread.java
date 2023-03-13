package edu.duke.shared.player;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

import edu.duke.shared.Game;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.Territory;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.unit.Unit;

public class PlayerThread implements Runnable, Serializable {

    private final State state;
    private final Socket socket;
    private Game currGame;

    private Game serverGame;

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

    public void setServerGame(Game serverGame) {
        this.serverGame = serverGame;
    }

    @Override
    public void run() {
        if (state != State.WAITING_TO_JOIN) {
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
                // TODO: WUYU Check on the server side again for both move and attack

                break;
            }
            case TURN_BEGIN:
                System.out.println("Received player " + this.playerId + "'s action list.");

                int turnIndex = this.currGame.getTurn();
                MoveTurn moveTurn = (MoveTurn) this.currGame.getTurnList().get(turnIndex).get(this.playerId).get(0);
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

                // Attack cannot be done here

                break;
            case TURN_END:
                break;
        }

    }
}
