package edu.duke.shared.turn;

import java.io.Serializable;
import java.util.HashMap;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;

public abstract class Turn implements Serializable {
    // Player's index
    protected int index;
    // Player name
    protected String playerName;
    // Game map
    protected final GameMap map;
    // Turn type
    protected String type;

    /**
     * Initialize Turn
     *
     * @param map        Game map
     * @param index      Turn index
     * @param playerName Player name
     */
    public Turn(GameMap map, int index, String playerName, String type) {
        this.map = map;
        this.index = index;
        this.playerName = playerName;
        this.type = type;
    }


    /**
     * Get game map
     *
     * @return Game map
     */
    public GameMap getMap() {
        return map;
    }

    /**
     * Get player's index
     *
     * @return Player's index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get player name
     *
     * @return Player name
     */
    public String getPlayerName() {
        return playerName;
    }

}
