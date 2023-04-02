package edu.duke.shared.turn;


import edu.duke.shared.unit.Unit;

import java.util.ArrayList;

public class Move extends Order {
    /**
     * Initialize Move
     *
     * @param from       Territory name
     * @param to         Territory name
     * @param numUnits   Number of units
     * @param playerName Player name
     */
    public Move(String from, String to, ArrayList<Unit> unitList, String playerName) {
        super(from, to, unitList, playerName);
    }
    public Move(String from, String to, int numUnits, String playerName) {
        super(from, to, numUnits, playerName);
    }

}
