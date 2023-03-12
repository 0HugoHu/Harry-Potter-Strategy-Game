package edu.duke.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.duke.shared.helper.Header;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.MapFactory;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.turn.Turn;

public class Game implements Serializable {
    // Game meta data
    private final Header header;
    // Number of players
    private final int numPlayers;
    private final ArrayList<Player> playerList;
    // Map
    private final GameMap gameMap;
    // TurnMap<playerId, ArrayList<Turn>>
    private ArrayList<HashMap<Integer, ArrayList<Turn>>> turnList;

    /**
     * Initialize Game by number of players
     *
     * @param numPlayers Number of players
     */
    public Game(int numPlayers) {
        this(numPlayers, new MapFactory(30, 60, 12).createRandomMap());
    }

    /**
     * Initialize Game by number of players and Map
     *
     * @param numPlayers num of players
     * @param gameMap    Map
     */
    public Game(int numPlayers, GameMap gameMap) {
        this.numPlayers = numPlayers;
        this.gameMap = gameMap;
        this.playerList = new ArrayList<>();
        this.turnList = new ArrayList<>();
        this.header = new Header();
    }

    /**
     * Add New player to the playerList of this game
     *
     * @param p player to add
     * @return true if success
     */
    public boolean addPlayer(Player p) {
        if (playerList.contains(p)) {
            return false;
        }
        playerList.add(p);
        return true;
    }

    /**
     * get the player list
     *
     * @return player list
     */
    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    /**
     * get the player by name
     *
     * @param name player name
     * @return Player
     */
    public Player getPlayer(String name) {
        for (Player p : playerList) {
            if (p.getPlayerName().equals(name)) {
                return p;
            }
        }
        return null;
    }


    /**
     * Print some details of this Game Basic Info,
     * including player Name and Territory Name
     *
     * @return string of info
     */
    public String GameDetail() {
        StringBuilder sb = new StringBuilder();
        for (Player p : playerList) {
            sb.append("--------------------\n");
            sb.append(p.getPlayerName()).append("\n");
            for (Territory t : p.getPlayerTerrs()) {
                sb.append(t.getName());
                sb.append("\n");
            }
            sb.append("--------------------\n");
        }
        return sb.toString();
    }


    /**
     * Get number of players
     *
     * @return number of players
     */
    public int getNumPlayers() {
        return this.numPlayers;
    }

    /**
     * Get map
     *
     * @return map
     */
    public GameMap getMap() {
        return this.gameMap;
    }

    public void setPlayerId(int id) {
        this.header.setPlayerId(id);
    }

    public int getPlayerId() {
        return this.header.getPlayerId();
    }

    public void setPlayerName(String name) {
        this.header.setPlayerName(name);
    }

    public String getPlayerName() {
        return this.header.getPlayerName();
    }

    public void setGameState(State state) {
        this.header.setState(state);
    }

    public State getGameState() {
        return this.header.getState();
    }

    public void turnComplete() {
        this.header.turnComplete();
    }

    public int getTurn() {
        return this.header.getTurn();
    }

    public void addToTurnMap(int playerId, MoveTurn moveTurn, AttackTurn attackTurn) {
        HashMap<Integer, ArrayList<Turn>> turnMap = new HashMap<>();
        ArrayList<Turn> newTurn=new ArrayList<>();
        newTurn.add(moveTurn);
        newTurn.add(attackTurn);
        turnMap.put(playerId, newTurn);
        this.turnList.add(turnMap);
    }


    public ArrayList<HashMap<Integer, ArrayList<Turn>>> getTurnList() {
        return this.turnList;
    }

}
