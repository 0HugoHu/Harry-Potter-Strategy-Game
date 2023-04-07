package edu.duke.shared.player;

import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

import edu.duke.shared.Game;
import edu.duke.shared.helper.State;
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

    private int worldLevel;


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
     * @param goal
     * @return
     */
    public int upgradeCost(int goal) {
        switch (goal) {
            case 2:
                return 20;
            case 3:
                return 40;
            case 4:
                return 80;
            case 5:
                return 160;
            case 6:
                return 320;
            default:
                return 20;
        }
    }

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
     *
     * @return
     */
    private int[] getAllRes() {
        int[] count = new int[]{0, 0};
        for (Territory terr : playerTerrs) {
            count[0] += terr.getCoins();
            count[1] += terr.getHorns();
        }
        return count;
    }

    public int getCoins() {
        return getAllRes()[0];
    }

    public int getHorns() {
        return getAllRes()[1];
    }

    public void upgradeWorldLevel() {
        this.worldLevel++;
        // TODO: Minus the cost

    }


}
