package edu.duke.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Game implements Serializable {
    // Number of players
    private final int numPlayers;
    ArrayList<Player> playerList;
    // Map
    private final Map map;

    /**
     * Initialize Game by number of players
     * @param numPlayers Number of players
     */
    public Game(int numPlayers) {
        this.numPlayers = numPlayers;
        this.playerList=new ArrayList<Player>();
        // Initialize map
        MapFactory map = new MapFactory(30, 30, 18,playerList);
        this.map = map.myLogic();
    }

    /**
     * Initialize Game by number of players and Map
     * @param numPlayers num of players
     * @param map Map
     */
    public Game(int numPlayers,Map map){
        this.numPlayers = numPlayers;
        this.map=map;
    }

    /**
     * Add New player to the playerList of this game
     * @param p player to add
     * @return true if success
     */
    public boolean addPlayer(Player p){
        if(playerList.contains(p)){
            return false;
        }
        playerList.add(p);
        return true;
    }

    /**
     * get the player list
     * @return player list
     */
    public ArrayList<Player> getPlayerList(){
        return playerList;
    }

    /**
     * get the player by name
     * @param name player name
     * @return Player
     */
    public Player getPlayer(String name){
        for(Player p:playerList){
            if(p.getPlayerName().equals(name)){
                return p;
            }
        }
        return null;
    }


    /**
     * Print some details of this Game Basic Info,
     * including player Name and Territory Name
     * @return string of info
     */
    public String GameDetail(){
        StringBuilder sb=new StringBuilder();
        for(Player p:playerList){
            sb.append("--------------------\n");
            sb.append(p.getPlayerName()+"\n");
            for(Territory t:p.getPlayerTerrs()){
                sb.append(t.getName());
                sb.append("\n");
            }
            sb.append("--------------------\n");
        }
        return sb.toString();
    }


    /**
     * Get number of players
     * @return number of players
     */
    public int getNumPlayers() {
        return this.numPlayers;
    }

    /**
     * Get map
     * @return map
     */
    public Map getMap() {
        return this.map;
    }
}
