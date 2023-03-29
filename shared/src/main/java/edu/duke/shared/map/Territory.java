package edu.duke.shared.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import edu.duke.shared.player.Player;
import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

public class Territory implements Serializable {
    // Territory name
    private final String name;
    // Player's name
    private String owner;
    // Player
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
    // Territory borders, up, down, right, left
    private int[] borders;

    /**
     * Initialize Territory by name
     *
     * @param name Territory name
     */
    public Territory(String name) {
        this(name, null, "", new ArrayList<>(), new HashSet<>(), new HashSet<>());
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
        // Initialize borders
        // This is not the actual borders, but the borders of the rectangle that contains all the coordinates
        this.borders = new int[] {Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE};
        for (int[] coord : this.coords) {
            if (coord[0] < this.borders[0]) {
                this.borders[0] = coord[0];
            }
            if (coord[0] > this.borders[1]) {
                this.borders[1] = coord[0];
            }
            if (coord[1] > this.borders[2]) {
                this.borders[2] = coord[1];
            }
            if (coord[1] < this.borders[3]) {
                this.borders[3] = coord[1];
            }
        }
    }

    /**
     * Check if a coordinate is inside this territory rectangle block
     * @param y y coordinate to be checked
     * @param x x coordinate to be checked
     * @return true if inside
     */
    public boolean checkInsideBorders(int y, int x) {
        return y < this.borders[0] || y > this.borders[1] || x > this.borders[2] || x < this.borders[3];
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
     * Get the adjacent territories of this territory
     *
     * @return adjacent territories
     */
    public HashSet<String> getAdjacents() {
        return this.adjs;
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
        for (Unit u : this.units) {
            if (u.getType().equals(unit.getType())) {
                this.units.remove(u);
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a unit from this territory
     *
     * @param unitType UnitType to be removed
     * @return true if successfully removed
     */
    public boolean removeUnit(UnitType unitType) {
        for (Unit u : this.units) {
            if (u.getType().equals(unitType)) {
                this.units.remove(u);
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a normal unit from this territory
     *
     * @return true if successfully removed
     */
    public boolean removeUnit() {
        for (Unit u : this.units) {
            if (u.getType().equals(UnitType.NORMAL)) {
                this.units.remove(u);
                return true;
            }
        }
        return false;
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
        if (this.playerOwner != null && this.playerOwner.equals(player))
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
     */
    public void removeAllUnits() {
        this.units.clear();
    }


    /**
     * Remove one unit by name from this territory
     *
     * @return true if successfully removed
     */
    public boolean removeUnitByName(String name) {
        UnitType unitType = Unit.convertStringToUnitType(name);
        for (Unit unit : this.units) {
            if (unit.getType() == unitType) {
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
     * Test if a coordinate is in this territory
     *
     * @param y y coordinate
     * @param x x coordinate
     * @return territory contains the coordinate
     */
    public boolean contains(int y, int x) {
        for (int[] c : coords) {
            if (c[0] == y && c[1] == x) return true;
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
