package edu.duke.shared.turn;

import java.io.Serializable;
import java.util.HashMap;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;

public abstract class Turn implements Serializable {
    protected int index;
    protected String playerName;
    protected boolean isDone = false;
    protected final GameMap map;
    protected String type;
    HashMap<String, Integer> unitFromMap = new HashMap<>();
    HashMap<String, Integer> unitToMap = new HashMap<>();

    public Turn(GameMap map, int index, String playerName,String type) {
        this.map = map;
        this.index = index;
        this.playerName = playerName;
        this.type=type;
    }
    public String getType(){
        return type;
    }


    public GameMap getMap() {
        return map;
    }

    public int getIndex() {
        return index;
    }

    public String getPlayerName() {
        return playerName;
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
