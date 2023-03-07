package edu.duke.shared.turn;

import java.util.HashMap;

import edu.duke.shared.GameMap;
import edu.duke.shared.Territory;

public abstract class Turn {
    protected int index;
    protected String playerName;
    protected boolean isDone = false;
    protected final GameMap map;
    HashMap<String, Integer> unitFromMap = new HashMap<>();
    HashMap<String, Integer> unitToMap = new HashMap<>();

    public Turn(GameMap map, int index, String playerName) {
        this.map = map;
        this.index = index;
        this.playerName = playerName;
    }

    protected boolean checkTerritory(String territoryName, String playerName) {
        for (Territory t : map.getTerritoriesByOwner(playerName)) {
            if (t.getName().equals(territoryName)) {
                return true;
            }
        }
        return false;
    }

    protected boolean checkAdjacency(String territoryName1, String territoryName2) {
        return map.getTerritory(territoryName1).isAdjacent(territoryName2);
    }
}
