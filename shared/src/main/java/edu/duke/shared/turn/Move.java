package edu.duke.shared.turn;


import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

import java.util.ArrayList;
import java.util.HashMap;

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
