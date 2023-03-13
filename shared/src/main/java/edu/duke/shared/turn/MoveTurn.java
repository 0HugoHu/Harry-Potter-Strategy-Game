package edu.duke.shared.turn;

import java.util.ArrayList;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.unit.Unit;

public class MoveTurn extends Turn{
    private final ArrayList<Move> moves = new ArrayList<>();

    public MoveTurn(GameMap map, int index, String playerName) {
        super(map, index, playerName,"move");
    }

    public boolean checkMove() {
        // Check single move is valid
        for (Move move : moves) {
            // Update unit hashmap
            unitFromMap.put(move.getFrom(), unitFromMap.getOrDefault(move.getFrom(), 0) + move.getNumUnits());

            if (!checkTerritory(move.getFrom(), playerName)) {
                return false;
            }
            if (!checkTerritory(move.getTo(), playerName)) {
                return false;
            }
            if (!checkAdjacency(move.getFrom(), move.getTo())) {
                return false;
            }
            if (map.getTerritory(move.getFrom()).getNumUnits() < move.getNumUnits()) {
                return false;
            }
        }

        // Check units with multiple moves
        for (String territoryName : unitFromMap.keySet()) {
            if (map.getTerritory(territoryName).getNumUnits() < unitFromMap.get(territoryName)) {
                return false;
            }
        }

        return true;
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

    public boolean checkPath() {
        // TODO: Search in BFS or using Union Find for connection
        checkAdjacency("A", "B");
        return false;
    }

    public GameMap updateMap() {
        return map;
    }

}
