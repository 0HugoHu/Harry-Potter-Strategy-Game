package edu.duke.risc.ui.action;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;

import edu.duke.shared.GameMap;
import edu.duke.shared.Territory;

public class TouchEventMapping {
    private HashMap<String, int[]> territoryMapping = new HashMap<>();
    private HashSet<String> centerPoints = new HashSet<>();

    public TouchEventMapping(GameMap gameMap) {
        for (Territory t : gameMap.getTerritories()) {
            int[] coord = new int[2];
            coord[0] = this.getCenterPoint(t)[0];
            coord[1] = this.getCenterPoint(t)[1];
            territoryMapping.put(t.getName(), coord);
        }
    }

    public int[] getCenterPoint(Territory t) {
        int top = Integer.MAX_VALUE, bottom = Integer.MIN_VALUE, left = Integer.MAX_VALUE, right = Integer.MIN_VALUE;
        for (int[] coord : t.getCoords()) {
            if (coord[0] < top) {
                top = coord[0];
            }
            if (coord[0] > bottom) {
                bottom = coord[0];
            }
            if (coord[1] < left) {
                left = coord[1];
            }
            if (coord[1] > right) {
                right = coord[1];
            }
        }
        int[] centerPoint = new int[] {(top + bottom) / 2, (left + right) / 2};
        this.centerPoints.add(centerPoint[0] * 100000 + centerPoint[1] + "");
        return centerPoint;
    }

    public boolean isCenterPoint(int y, int x) {
        return this.centerPoints.contains(y * 100000 + x + "");
    }

    public boolean updateTerritoryMapping(String territoryName, int[] coord) {
        if (territoryMapping.containsKey(territoryName)) {
            territoryMapping.put(territoryName, coord);
            return true;
        }
        return false;
    }

    public String getOnTouchObject(int y, int x) {
        double minDistance = Double.MAX_VALUE;
        String closestTerritory = null;
        for (String territoryName : territoryMapping.keySet()) {
            int[] coord = territoryMapping.get(territoryName);
            assert coord != null;
            double distance = Math.sqrt(Math.pow(coord[0] - y, 2) + Math.pow(coord[1] - x, 2));
            if (distance < minDistance) {
                minDistance = distance;
                closestTerritory = territoryName;
            }
        }
        return closestTerritory;
    }
}
