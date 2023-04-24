package edu.duke.shared.turn;


import java.util.HashMap;

import edu.duke.shared.unit.UnitType;

public class Move extends Order {
    /**
     * Initialize Move
     *
     * @param from       Territory name
     * @param to         Territory name
     * @param playerName Player name
     */
    public Move(String from, String to, HashMap<UnitType, Integer> unitList, String playerName) {
        super(from, to, unitList, playerName);
    }

    /**
     * Initialize Move
     *
     * @param from       Territory name
     * @param to         Territory name
     * @param numUnits   Number of units
     * @param playerName Player name
     */
    public Move(String from, String to, int numUnits, String playerName) {
        super(from, to, numUnits, playerName);
    }

}
