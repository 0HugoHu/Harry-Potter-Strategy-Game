package edu.duke.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Map implements Serializable {
    // All territories on this map
    private final ArrayList<Territory> territories;
    // Number of territories on this map
    private int numTerritories;
    // Map height
    private final int height;
    // Map width
    private final int width;

    /*
     * Initialize Map by height and width
     * @param height Map height
     * @param width Map width
     * @param numTerritories Number of territories on this map
     */
    public Map(int height, int width, int numTerritories) {
        this.height = height;
        this.width = width;
        this.numTerritories = numTerritories;
        this.territories = new ArrayList<>();
    }

    /*
     * Initialize Map by height, width, and territories
     * @param height Map height
     * @param width Map width
     * @param numTerritories Number of territories on this map
     * @param territories Territories on this map
     */
    public Map(int height, int width, int numTerritories, ArrayList<Territory> territories) {
        this.height = height;
        this.width = width;
        this.numTerritories = numTerritories;
        this.territories = territories;
    }

    /*
     * Get map height
     * @return map height
     */
    public int getHeight() {
        return this.height;
    }

    /*
     * Get map width
     * @return map width
     */
    public int getWidth() {
        return this.width;
    }

    /*
     * Get number of territories on this map
     * @return number of territories on this map
     */
    public int getNumTerritories() {
        return this.numTerritories;
    }

    /*
     * Get territories on this map
     * @return territories on this map
     */
    public ArrayList<Territory> getTerritories() {
        return this.territories;
    }

    /*
     * Get a territory by name
     * @param name Territory name
     * @return territory with the given name
     */
    public Territory getTerritory(String name) {
        for (Territory t : this.territories) {
            if (t.getName().equals(name))
                return t;
        }
        return null;
    }

    /*
     * Get a territory by owner name
     * @param name Territory owner name
     * @return territories owned by the given owner
     */
    public ArrayList<Territory> getTerritoriesByOwner(String name) {
        ArrayList<Territory> territories = new ArrayList<>();
        for (Territory t : this.territories) {
            if (t.getOwner().equals(name))
                territories.add(t);
        }
        return territories;
    }

    /*
     * Add a territory to this map
     * @param t Territory to be added
     * @return true if successfully added
     */
    public boolean addTerritory(Territory t) {
        if (this.territories.contains(t))
            return false;
        this.territories.add(t);
        this.numTerritories++;
        return true;
    }

    /*
     * Remove a territory by name from this map
     * @param name Territory name
     * @return true if successfully removed
     */
    public boolean removeTerritory(String name) {
        for (Territory t : this.territories) {
            if (t.getName().equals(name)) {
                this.territories.remove(t);
                this.numTerritories--;
                return true;
            }
        }
        return false;
    }

}
