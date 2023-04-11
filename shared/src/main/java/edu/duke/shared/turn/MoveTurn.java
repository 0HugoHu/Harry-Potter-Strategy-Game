package edu.duke.shared.turn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

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
            for(Map.Entry<UnitType,Integer> entry:move.getUnitList().entrySet()){
                for(int i=0;i<entry.getValue();i++){
                    map.getTerritory(move.getFrom()).removeUnit(entry.getKey());
                    map.getTerritory(move.getTo()).addUnit(entry.getKey());
                }
            }
        }
    }

}
