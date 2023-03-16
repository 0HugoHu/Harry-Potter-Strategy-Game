package edu.duke.shared.turn;

import java.io.Serializable;

import edu.duke.shared.player.Player;

public abstract class Order implements Serializable {
    // Source territory
    protected final String from;
    // Destination territory
    protected final String to;
    // Number of units
    protected int numUnits;
    // Player name
    protected final String playerName;

    /**
     * Initialize Order
     *
     * @param from       Territory name
     * @param to         Territory name
     * @param numUnits   Number of units
     * @param playerName Player name
     */
    public Order(String from, String to, int numUnits, String playerName) {
        this.from = from;
        this.to = to;
        this.numUnits = numUnits;
        this.playerName = playerName;
    }

    /**
     * Get source territory
     *
     * @return Source territory
     */
    public String getFrom() {
        return from;
    }

    /**
     * Get destination territory
     *
     * @return Destination territory
     */
    public String getTo() {
        return to;
    }

    /**
     * Get number of units
     *
     * @return Number of units
     */
    public int getNumUnits() {
        return numUnits;
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
