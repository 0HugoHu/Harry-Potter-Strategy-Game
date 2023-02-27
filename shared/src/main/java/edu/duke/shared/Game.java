package edu.duke.shared;

import java.io.Serializable;

public class Game implements Serializable {
    // Number of players
    private final int numPlayers;
    // Map
    private final Map map;

    /*
     * Initialize Game by number of players
     * @param numPlayers Number of players
     */
    public Game(int numPlayers) {
        this.numPlayers = numPlayers;
        // Initialize map
        MapFactory map = new MapFactory(18, 30, 18);
        this.map = map.myTemplateMap();
    }

    public Game(int numPlayers,Map map){
        this.numPlayers = numPlayers;
        this.map=map;
    }


    /*
     * Get number of players
     * @return number of players
     */
    public int getNumPlayers() {
        return this.numPlayers;
    }

    /*
     * Get map
     * @return map
     */
    public Map getMap() {
        return this.map;
    }
}
