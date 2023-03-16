package edu.duke.shared.turn;

import edu.duke.shared.player.Player;

import java.io.Serializable;
import java.util.ArrayList;

public class Attack extends Order {
    /**
     * Initialize Attack
     *
     * @param from       Territory name
     * @param to         Territory name
     * @param numUnits   Number of units
     * @param playerName Player name
     */
    public Attack(String from, String to, int numUnits, String playerName) {
        super(from, to, numUnits, playerName);
    }

    /**
     * Remove a unit from this territory
     */
    public void removeUnit() {
        this.numUnits--;
    }

}
