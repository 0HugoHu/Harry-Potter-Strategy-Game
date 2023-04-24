package edu.duke.risc.ui.action;

import java.util.HashMap;
import java.util.HashSet;

import edu.duke.risc.ui.state.TouchEvent;
import edu.duke.shared.Game;
import edu.duke.shared.helper.Validation;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;

public class TouchEventMapping {
    // Mapping from territory name to center point
    private final HashMap<String, int[]> territoryMapping = new HashMap<>();
    // Mapping from action name to selection panel center point and radius
    private final HashMap<String, int[]> selectionPanelMapping = new HashMap<>();
    // Center points of territories
    private final HashSet<String> centerPoints = new HashSet<>();
    // Map border for up, right, down, left
    private int[] mapBorder = new int[4];
    // Map view width and height
    private float scale = 1.0f;

    /**
     * Constructor
     *
     * @param gameMap GameMap
     */
    public TouchEventMapping(GameMap gameMap) {
        for (Territory t : gameMap.getTerritories()) {
            territoryMapping.put(t.getName(), new int[]{this.getCenterPoint(t)[0], this.getCenterPoint(t)[1]});
        }
        for (String action : new String[]{TouchEvent.PROP.name(), TouchEvent.ORDER.name(), TouchEvent.UNIT.name()}) {
            selectionPanelMapping.put(action, new int[]{0, 0, 0});
        }
    }

    /**
     * Check if the string is a valid action
     *
     * @param string string to be checked
     * @return if the string is a valid action
     */
    public static boolean checkIsAction(String string) {
        for (TouchEvent event : TouchEvent.values()) {
            if (event.name().equals(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the touch event based on the selected territories
     *
     * @param selected1 selected territory 1
     * @param selected2 selected territory 2
     * @param terrFrom  territory from
     * @param map       game map
     * @return the touch event based on the selected territories
     */
    public static TouchEvent getAction(String selected1, String selected2, String terrFrom, GameMap map) {
        if (selected1 == null || selected2 == null) return null;
        // Property and Unit info
        if (!TouchEventMapping.checkIsAction(selected1) && TouchEventMapping.checkIsAction(selected2)) {
            switch (TouchEvent.valueOf(selected2)) {
                case PROP:
                    return TouchEvent.PROP;
                case UNIT:
                    return TouchEvent.UNIT;
            }
        }
        // Attack or Move
        if (TouchEventMapping.checkIsAction(selected1) && !TouchEventMapping.checkIsAction(selected2)) {
            if (selected1.equals(TouchEvent.ORDER.name()) && terrFrom != null) {
                // Target territory is not self
                if (Validation.checkAdjacent(map, terrFrom, selected2) && !map.getOwnerByTerrName(selected2).equals(map.getOwnerByTerrName(terrFrom))) {
                    return TouchEvent.ATTACK;
                }
                // Target territory is self's, but not itself
                if (Validation.checkPathExist(map, terrFrom, selected2) && !terrFrom.equals(selected2)) {
                    return TouchEvent.MOVE;
                }
            }
        }
        return null;
    }

    /**
     * Get the center point of a territory
     *
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
                centerPoint = new int[]{coord[0], coord[1]};
            }
        }
        this.centerPoints.add(centerPoint[0] * 100000 + centerPoint[1] + "");
        return centerPoint;
    }

    /**
     * Check if the coordinate is a center point of a territory
     *
     * @param y y coordinate
     * @param x x coordinate
     * @return true if the coordinate is a center point of a territory, false otherwise
     */
    public boolean isCenterPoint(int y, int x) {
        return this.centerPoints.contains(y * 100000 + x + "");
    }

    /**
     * Update the territory mapping
     *
     * @param territoryName territory name
     * @param coord         coordinate of the territory
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
     * Update the selection panel mapping
     *
     * @param actionName action name
     * @param coord      coordinate of the selection panel
     * @param radius     radius of the selection panel
     * @return true if the selection panel mapping is updated successfully, false otherwise
     */
    public boolean updateSelectionPanelMapping(String actionName, int[] coord, int radius) {
        if (selectionPanelMapping.containsKey(actionName)) {
            selectionPanelMapping.put(actionName, new int[]{coord[0], coord[1], radius});
            return true;
        }
        return false;
    }

    /**
     * Update the map border
     *
     * @param boundary map border
     */
    public void updateBoundary(int[] boundary) {
        this.mapBorder = boundary;
    }

    /**
     * Set the scale of the map
     *
     * @param scale scale of the map
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Get the territory name that is closest to the touch point
     *
     * @param y y coordinate of the touch point
     * @param x x coordinate of the touch point
     * @return the territory name that is closest to the touch point
     */
    public String getOnTouchObject(int y, int x, String selected2, Game game) {
        // First check if it is on the selection panel
        if (selected2 == null || !TouchEventMapping.checkIsAction(selected2)) {
            for (String actionName : selectionPanelMapping.keySet()) {
                // coord[0] is y coordinate, coord[1] is x coordinate, coord[2] is radius
                int[] coord = selectionPanelMapping.get(actionName);
                assert coord != null;
                if (Math.sqrt(Math.pow(coord[0] - y, 2) + Math.pow(coord[1] - x, 2)) < coord[2]) {
                    // Can only execute the action if the territory belongs to the player
                    if (actionName.equals(TouchEvent.ORDER.name())) {
                        Territory t = game.getMap().getTerritory(selected2);
                        if (t == null || !t.getOwner().equals(game.getPlayerName())) {
                            continue;
                        }
                    }
                    return actionName;
                }
            }
        }
        // Check if the touch point is inside a territory
        if (isNotInsideMap(y, x)) return TouchEvent.OUTSIDE.name();
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
     *
     * @param y y coordinate
     * @param x x coordinate
     * @return true if the coordinate is not inside the map, false otherwise
     */
    private boolean isNotInsideMap(int y, int x) {
        return y < this.mapBorder[0] || y > this.mapBorder[2] || x < this.mapBorder[3] || x > this.mapBorder[1];
    }
}
