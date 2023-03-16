package edu.duke.shared.turn;


public class Move extends Order {
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
