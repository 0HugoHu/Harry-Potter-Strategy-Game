package edu.duke.shared.turn;

import java.util.ArrayList;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.unit.Unit;

public class MoveTurn extends Turn {
    // List of moves
    private final ArrayList<Move> moves = new ArrayList<>();
    // List of attacks

    /**
     * Initialize MoveTurn
     *
     * @param map        Game map
     * @param index      Turn index
     * @param playerName Player name
     */
    public MoveTurn(GameMap map, int index, String playerName) {
        super(map, index, playerName, "move");
    }

    /**
     * Add a move to this turn
     *
     * @param move Move
     */
    public void addMove(Move move) {
        moves.add(move);
    }

    /**
     * Get moves
     *
     * @return moves
     */
    public ArrayList<Move> getMoves() {
        return moves;
    }

    /**
     * Merge move orders
     */
    public void doMovePhase() {
        for (Move move : moves) {
            for (int i = 0; i < move.getNumUnits(); i++) {
                map.getTerritory(move.getFrom()).removeUnit();
                map.getTerritory(move.getTo()).addUnit(new Unit("Normal"));
            }
        }
    }

}
