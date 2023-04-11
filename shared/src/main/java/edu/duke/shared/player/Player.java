package edu.duke.shared.player;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

import edu.duke.shared.Game;
import edu.duke.shared.map.Territory;

public class Player implements Serializable {
    // Player's name
    private String playerName;
    // Abandoned!! Territories owned by this player
    private final HashSet<Territory> playerTerrs;
    // Player's id
    private int playerId;
    // Player's socket
    private transient Socket socket;
    // Player's thread
    public transient PlayerThread playerThread;
    // Player's thread
    public transient Thread thread;
    // World level
    private int worldLevel;
    // Coins
    public int coins;
    // Horns
    public int horns;
    // Will upgrade world level
    public boolean willUpgradeWorldLevel = false;


    /**
     * Initialize the Player by name
     *
     * @param playerId player id
     * @param socket   player socket
     */
    public Player(int playerId, Socket socket) {
        this.playerName = null;
        this.playerId = playerId;
        this.playerTerrs = new HashSet<>();
        this.socket = socket;
        // Start the thread
        this.playerThread = new PlayerThread(this.socket, this.playerId);
        this.thread = new Thread(this.playerThread);
        this.thread.start();
        this.worldLevel = 1;
    }

    /**
     * Initialize the Player by name
     *
     * @param serverGame server game
     */
    public void start(Game serverGame) {
        this.playerThread = new PlayerThread(serverGame.getGameState(), this.socket, this.playerId);
        this.playerThread.setServerGame(serverGame);
        if (this.thread != null) {
            this.thread.interrupt();
        }
        this.thread = new Thread(this.playerThread);
        this.thread.start();
    }

    /**
     * get player thread
     *
     * @return player thread
     */
    public PlayerThread getPlayerThread() {
        return this.playerThread;
    }

    /**
     * Wait for the thread to finish
     */
    public void threadJoin() {
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * get player name
     *
     * @return player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * change player name
     *
     * @param playerName new player name
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * get player id
     *
     * @return player id
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * change player id
     *
     * @param playerId new player id
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * add Territory to the territory list
     *
     * @param terr territory to add
     * @return true if success
     */
    public boolean expandTerr(Territory terr) {
        if (playerTerrs.contains(terr)) {
            return false;
        }
        playerTerrs.add(terr);
        return true;
    }

    /**
     * remove Territory from the territory list
     *
     * @param terr territory to remove
     * @return true if success
     */
    public boolean removeTerr(Territory terr) {
        if (!playerTerrs.contains(terr)) {
            return false;
        }
        playerTerrs.remove(terr);
        return true;
    }

    /**
     * return Territory list of this player
     *
     * @return territory list
     */
    public HashSet<Territory> getPlayerTerrs() {
        return playerTerrs;
    }


    /**
     * This function return the cost for each level of upgrade goal
     *
     * @param goal the level of upgrade goal
     * @return the cost for each level of upgrade goal
     */
    public static int upgradeCost(int goal) {
        switch (goal) {
            case 2:
                return 60;
            case 3:
                return 100;
            case 4:
                return 200;
            case 5:
                return 300;
            case 6:
                return 400;
            default:
                return 60;
        }
    }

    /**
     * return world level of this player
     *
     * @return world level
     */
    public int getWorldLevel() {
        return worldLevel;
    }

    /**
     * return connection socket of this player
     *
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * change connection socket of this player
     *
     * @param socket new socket
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }


    /**
     * return the corresponding resources for this player, with the Integer[] array's
     * first index being coin resources, second index being horn resources.
     */
    public void updateResources(ArrayList<Territory> territories) {
        int coins = 0;
        int horns = 0;
        for (Territory t : territories) {
            coins += t.getCoins();
            horns += t.getHorns();
        }
        this.coins += coins;
        this.horns += horns;
    }

    /**
     * Get coins
     *
     * @return coins
     */
    public int getCoins() {
        return this.coins;
    }

    /**
     * Get horns
     *
     * @return horns
     */
    public int getHorns() {
        return this.horns;
    }

    /**
     * Upgrade world level
     */
    public void upgradeWorldLevel() {
        this.worldLevel++;
        this.horns -= upgradeCost(this.worldLevel);
    }

    /**
     * Set horns expense
     *
     * @param horns horns
     */
    public void setExpenseHorns(int horns) {
        this.horns -= horns;
    }

    /**
     * Set coins expense
     *
     * @param coins coins
     */
    public void setExpenseCoins(int coins) {
        this.coins -= coins;
    }

}
