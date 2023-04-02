package edu.duke.shared.turn;

import edu.duke.shared.player.Player;
import edu.duke.shared.unit.Unit;

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

    public Attack(String from, String to, ArrayList<Unit> unitList, String playerName) {
        super(from, to, unitList, playerName);
    }

    /**
     * Remove a unit from this territory
     */
    public void removeUnit(Unit unit) {
        this.unitList.remove(unit);
    }

    public void removeUnit(){
        numUnits--;
    }

}
