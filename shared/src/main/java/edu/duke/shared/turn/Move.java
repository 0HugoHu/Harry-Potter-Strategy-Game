package edu.duke.shared.turn;


import edu.duke.shared.player.House;
import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

import java.util.ArrayList;
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
    public Move(String from, String to, HashMap<UnitType, Integer> unitList, String playerName, House house) {
        super(from, to, unitList, playerName,house);
    }

    /**
     * Initialize Move
     *
     * @param from       Territory name
     * @param to         Territory name
     * @param numUnits   Number of units
     * @param playerName Player name
     */
    public Move(String from, String to, int numUnits, String playerName,House house) {
        super(from, to, numUnits, playerName,house);
    }

}
