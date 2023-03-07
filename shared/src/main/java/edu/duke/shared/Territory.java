package edu.duke.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class Territory implements Serializable {
    // Territory name
    private final String name;
    // Player's name
    private String owner;
    private Player playerOwner;
    // All types of units on this territory
    /*
        For extendability, as units can have different attack and defense
        abilities in the following evolutions.
     */
    private final ArrayList<Unit> units;
    // Coordinates inside this territory
    private final HashSet<int[]> coords;
    // Adjacent Territory name
    private final HashSet<String> adjs;

    /**
     * Initialize Territory by name
     *
     * @param name Territory name
     */
    public Territory(String name) {
        this.name = name;
        this.owner = "";
        this.playerOwner = null;
        this.units = new ArrayList<>();
        this.coords = new HashSet<>();
        this.adjs = new HashSet<>();
    }

    /**
     * Initialize Territory by name, owner, playerOwner,units, coordinates, and adjacent territories
     *
     * @param name        Territory name
     * @param playerOwner player
     * @param owner       Territory owner
     * @param units       Units on this territory
     * @param coords      Coordinates inside this territory
     * @param adjs        Adjacent territories
     */
    public Territory(String name, Player playerOwner, String owner, ArrayList<Unit> units, HashSet<int[]> coords, HashSet<String> adjs) {
        this.playerOwner = playerOwner;
        this.name = name;
        this.owner = owner;
        this.units = units;
        this.coords = coords;
        this.adjs = adjs;
    }


    /**
     * Add a coordinate to this territory
     *
     * @param coord Coordinate to be added
     * @return true if successfully added
     */
    public boolean addCoordinate(int[] coord) {
        if (this.coords.contains(coord))
            return false;
        this.coords.add(coord);
        return true;
    }


    /**
     * Add an adjacent territory to this territory
     *
     * @param adj Adjacent territory to be added
     * @return true if successfully added
     */
    public boolean addAdjacent(String adj) {
        if (this.adjs.contains(adj))
            return false;
        this.adjs.add(adj);
        return true;
    }

    /**
     * Add a unit to this territory
     *
     * @param unit Unit to be added
     * @return true if successfully added
     */
    public boolean addUnit(Unit unit) {
        this.units.add(unit);
        return true;
    }

    /**
     * Remove a unit from this territory
     *
     * @param unit Unit to be removed
     * @return true if successfully removed
     */
    public boolean removeUnit(Unit unit) {
        this.units.remove(unit);
        return true;
    }

    /**
     * Get the name of this territory
     *
     * @return name of this territory
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the owner of this territory
     *
     * @return owner of this territory
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Change the owner of this territory
     *
     * @return true if successfully changed
     */
    public boolean changeOwner(String owner) {
        if (this.owner.equals(owner))
            return false;
        this.owner = owner;
        return true;
    }

    /**
     * change the playerowner of this territory
     *
     * @param player player
     */
    public boolean changePlayerOwner(Player player) {
        if (this.playerOwner!=null&&this.playerOwner.equals(player))
            return false;
        this.playerOwner = player;
        return true;
    }

    /**
     * return the playerowner of this territory
     *
     * @return player
     */
    public Player getPlayerOwner() {
        return playerOwner;
    }

    /**
     * Get the units of this territory
     *
     * @return units of this territory
     */
    public ArrayList<Unit> getUnits() {
        return this.units;
    }

    /**
     * Remove all units from this territory
     *
     * @return true if successfully removed
     */
    public boolean removeAllUnits() {
        this.units.clear();
        return true;
    }

    /**
     * Remove one unit by name from this territory
     *
     * @return true if successfully removed
     */
    public boolean removeUnitByName(String name) {
        for (Unit unit : this.units) {
            if (unit.getName().equals(name)) {
                this.units.remove(unit);
                return true;
            }
        }
        return false;
    }

    /**
     * Test if a coordinate is in this territory
     *
     * @param coord Coordinate to be tested
     * @return territory contains the coordinate
     */
    public boolean contains(int[] coord) {
        for (int[] c : coords) {
            if (c[0] == coord[0] && c[1] == coord[1]) return true;
        }
        return false;
    }

    /**
     * Test if a territory is adjacent to this territory
     *
     * @param adj Territory to be tested
     * @return territory is adjacent to this territory
     */
    public boolean isAdjacent(String adj) {
        return this.adjs.contains(adj);
    }

    /**
     * Get the number of units on this territory
     *
     * @return number of units on this territory
     */
    public int getNumUnits() {
        return this.units.size();
    }

    /**
     * Get all coordinates on this territory
     *
     * @return all coordinates on this territory
     */
    public HashSet<int[]> getCoords() {
        return this.coords;
    }

}
