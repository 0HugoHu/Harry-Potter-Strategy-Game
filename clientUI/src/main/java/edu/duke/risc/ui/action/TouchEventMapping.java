package edu.duke.risc.ui.action;

import java.util.HashMap;
import java.util.HashSet;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;

public class TouchEventMapping {
    // Mapping from territory name to center point
    private final HashMap<String, int[]> territoryMapping = new HashMap<>();
    // Center points of territories
    private final HashSet<String> centerPoints = new HashSet<>();
    // Map border for up, right, down, left
    private int[] mapBorder = new int[4];
    // Map view width and height
    private float scale = 1.0f;

    /**
     * Constructor
     * @param gameMap GameMap
     */
    public TouchEventMapping(GameMap gameMap) {
        for (Territory t : gameMap.getTerritories()) {
            territoryMapping.put(t.getName(), new int[] {this.getCenterPoint(t)[0], this.getCenterPoint(t)[1]});
        }
    }

    /**
     * Get the center point of a territory
     * @param t Territory
     * @return int[] center point
     */
    public int[] getCenterPoint(Territory t) {
        float minDistance = Float.MAX_VALUE;
        int[] centerPoint = new int[2];
        for (int[] coord : t.getCoords()) {
            float distance = 0.0f;
            for (int[] coord2 : t.getCoords()) {
                distance += Math.sqrt(Math.pow(coord[0] - coord2[0], 2) + Math.pow(coord[1] - coord2[1], 2));
            }
            if (distance < minDistance) {
                minDistance = distance;
                centerPoint = new int[] {coord[0], coord[1]};
            }
        }
        this.centerPoints.add(centerPoint[0] * 100000 + centerPoint[1] + "");
        return centerPoint;
    }

    /**
     * Check if the coordinate is a center point of a territory
     * @param y y coordinate
     * @param x x coordinate
     * @return true if the coordinate is a center point of a territory, false otherwise
     */
    public boolean isCenterPoint(int y, int x) {
        return this.centerPoints.contains(y * 100000 + x + "");
    }

    /**
     * Update the territory mapping
     * @param territoryName territory name
     * @param coord coordinate of the territory
     * @return true if the territory mapping is updated successfully, false otherwise
     */
    public boolean updateTerritoryMapping(String territoryName, int[] coord) {
        if (territoryMapping.containsKey(territoryName)) {
            territoryMapping.put(territoryName, coord);
            return true;
        }
        return false;
    }

    /**
     * Update the map border
     * @param boundary map border
     */
    public void updateBoundary(int[] boundary) {
        System.out.println("Update boundary: " + boundary[0] + " " + boundary[1] + " " + boundary[2] + " " + boundary[3]);
        this.mapBorder = boundary;
    }

    /**
     * Set the scale of the map
     * @param scale scale of the map
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Get the territory name that is closest to the touch point
     * @param y y coordinate of the touch point
     * @param x x coordinate of the touch point
     * @return the territory name that is closest to the touch point
     */
    public String getOnTouchObject(int y, int x) {
        // Check if the touch point is inside a territory
        if (isNotInsideMap(y, x)) return "Outside";
        // Convert to original coordinate
        y = (int) (y / this.scale);
        x = (int) (x / this.scale);
        // Find the closest territory
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

    /**
     * Check if the coordinate is not inside the map
     * @param y y coordinate
     * @param x x coordinate
     * @return true if the coordinate is not inside the map, false otherwise
     */
    private boolean isNotInsideMap(int y, int x) {
        return y < this.mapBorder[0] || y > this.mapBorder[2] || x < this.mapBorder[3] || x > this.mapBorder[1];
    }
}
