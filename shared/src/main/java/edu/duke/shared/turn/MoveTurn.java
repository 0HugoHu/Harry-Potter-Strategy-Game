package edu.duke.shared.turn;

import java.util.ArrayList;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.unit.Unit;

public class MoveTurn extends Turn{
    private final ArrayList<Move> moves = new ArrayList<>();

    public MoveTurn(GameMap map, int index, String playerName) {
        super(map, index, playerName,"move");
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public ArrayList<Move> getMoves() {
        return moves;
    }

    public void doMovePhrase() {
        Unit unit = new Unit("Normal");
        for (Move move : moves) {
            for (int i = 0; i < move.getNumUnits(); i++) {
                map.getTerritory(move.getFrom()).removeUnit(unit);
                map.getTerritory(move.getTo()).addUnit(unit);
            }
        }
    }

    public GameMap updateMap() {
        return map;
    }

}
