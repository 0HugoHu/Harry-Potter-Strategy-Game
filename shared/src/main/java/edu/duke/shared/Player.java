package edu.duke.shared;

import java.net.Socket;
import java.util.HashSet;
import java.io.Serializable;

public class Player implements Serializable {

    private String playerName;
    private HashSet<Territory> playerTerrs;
    private int playerId;
    private transient Socket socket;

    /**
     * Initialize the Player by name
     * @param name player name
     */
    public Player(String name) {
        this.playerName=name;
        this.playerTerrs=new HashSet<Territory>();
        this.socket=null;
    }


    /**
     * get player name
     * @return player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * change player name
     * @param playerName new player name
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * get player id
     * @return player id
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * change player id
     * @param playerId new player id
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * add Territory to the territory list
     * @param terr territory to add
     * @return true if success
     */
    public boolean expandTerr(Territory terr){
        if(playerTerrs.contains(terr)){
            return false;
        }
        playerTerrs.add(terr);
        return true;
    }

    /**
     * remove Territory from the territory list
     * @param terr territory to remove
     * @return true if success
     */
    public boolean removeTerr(Territory terr){
        if(!playerTerrs.contains(terr)){
            return false;
        }
        playerTerrs.remove(terr);
        return true;
    }

    /**
     * return Territory list of this player
     * @return territory list
     */
    public HashSet<Territory> getPlayerTerrs(){
        return playerTerrs;
    }

    /**
     * return connection socket of this player
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * change connection socket of this player
     * @param socket new socket
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }


}
