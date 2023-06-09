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
    // Resource production
    private final boolean[] ResourcePro;
    // Player's name
    private String owner;
    // Player
    private Player playerOwner;
    // Territory borders, up, down, right, left
    private int[] borders;
    /**
     * This is the corresponding food resources in our design
     */
    private int coins;
    /**
     * This is the corresponding tech resources in our design
     */
    private int horns;
    // Type of territory
    private String type;
    // Details of territory
    private String details;

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
        this.coins = 0;
        this.horns = 0;
        this.ResourcePro = new boolean[]{false, false};
        this.details = "";
        this.type = "";
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
    public Territory(String name, Player playerOwner, String owner, ArrayList<Unit> units, HashSet<int[]> coords,
                     HashSet<String> adjs, String details, String type) {
        this.playerOwner = playerOwner;
        this.name = name;
        this.owner = owner;
        this.units = units;
        this.coords = coords;
        this.adjs = adjs;
        this.coins = 0;
        this.horns = 0;
        this.ResourcePro = new boolean[]{false, false};
        this.details = details;
        this.type = type;
        // Initialize borders
        // This is not the actual borders, but the borders of the rectangle that contains all the coordinates
        this.borders = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE};
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
     *
     * @param y y coordinate to be checked
     * @param x x coordinate to be checked
     * @return true if inside
     */
    public boolean checkInsideBorders(int y, int x) {
        return y > this.borders[0] && y < this.borders[1] && x < this.borders[2] && x > this.borders[3];
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
     * @return true if successfully added
     */
    public boolean addUnit(UnitType type) {
        this.units.add(new Unit(Unit.convertUnitTypeToString(type)));
        return true;
    }

    /**
     * Add a unit to this territory
     *
     * @return true if successfully added
     */
    public boolean addUnit(Unit unit) {
        this.units.add(unit);
        return true;
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
        return removeUnit(unitType);
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
     * set territory details
     *
     * @param details details
     */
    public void addDetails(String details) {
        this.details = details;
    }

    /**
     * return the details of a territory
     *
     * @return details
     */
    public String getDetails() {
        return details;
    }

    /**
     * return the number of coins
     *
     * @return coins
     */
    public int getCoins() {
        return coins;
    }

    /**
     * add coins to the territory
     *
     * @param coins coins
     */
    public void addCoins(int coins) {
        this.coins += coins;
    }

    /**
     * minus coins from the territory
     *
     * @param coins coins
     */
    public void minusCoins(int coins) {
        this.coins -= coins;
    }

    /**
     * return the number of unicorn horns
     *
     * @return horns
     */
    public int getHorns() {
        return horns;
    }

    /**
     * add unicorn horns to the territory
     *
     * @param horns horns
     */
    public void addHorns(int horns) {
        this.horns += horns;
    }

    /**
     * minus unicorn horns from the territory
     *
     * @param horns horns
     */
    public void minusHorns(int horns) {
        this.horns -= horns;
    }

    /**
     * return the number of nifflers
     *
     * @return nifflers
     */
    public boolean[] getReources() {
        return this.ResourcePro;
    }

    /**
     * set the land to be able to produce unicorn horns
     */
    public void setUnicornLand() {
        ResourcePro[0] = true;
    }

    /**
     * set the land to be able to produce silver coins
     */
    public void setNifflerLand() {
        ResourcePro[1] = true;
    }

    /**
     * check if this land produces Unicorn horn
     *
     * @return true if it produces unicorn horn
     */
    public boolean checkUnicornLand() {
        return ResourcePro[0];
    }

    /**
     * Get the type of this territory
     *
     * @return type
     */
    public String getType() {
        return this.type;
    }

    /**
     * set different and assign initial resources to different territories
     *
     * @param typeName type of the territory
     */
    public void setType(String typeName) {
        switch (typeName) {
            case "cliff":
                this.type = "cliff";
                setUnicornLand();
                addHorns(10);
                break;
            case "canyon":
                this.type = "canyon";
                setUnicornLand();
                setNifflerLand();
                addHorns(12);
                addCoins(15);
                break;
            case "desert":
                this.type = "desert";
                setNifflerLand();
                addCoins(5);
                break;
            case "forest":
                this.type = "forest";
                setUnicornLand();
                setNifflerLand();
                addHorns(12);
                addCoins(15);
                break;
            case "wetland":
                this.type = "wetland";
                setUnicornLand();
                setNifflerLand();
                addHorns(40);
                addCoins(15);
                break;
            default:
                this.type = "plain";
                setUnicornLand();
                setNifflerLand();
                addHorns(10);
                addCoins(20);
                break;
        }
    }

    /**
     * Get the type of this territory
     *
     * @return type in Harry Potter style
     */
    public String getType_HPStyle() {
        switch (this.type) {
            case "plain":
                return "Enchanted Plain";
            case "cliff":
                return "Spellbound Cliff";
            case "canyon":
                return "Arcane Canyon";
            case "desert":
                return "Bewitched Desert";
            case "forest":
                return "Forbidden Forest";
            case "mountain":
                return "Enchanted Wetland";
            default:
                return "Not Defined";
        }
    }

    /**
     * Get the details of this territory
     *
     * @return details in Harry Potter style
     */
    public String getDetails_HPStyle() {
        switch (this.type) {
            case "plain":
                return "The grass would sway and whisper ancient secrets, occasionally concealing rare magical creatures and hidden portals to other realms. The plain would be the site of ancient magical battles, with the possibility of discovering enchanted artifacts.";
            case "cliff":
                return "These towering cliffs would have an almost magnetic pull, enticing daring magical creatures to live on the cliff face. Flying broomsticks and magical transportation methods would be necessary for access, and the cliffs might even change shape or location to prevent trespassers.";
            case "canyon":
                return "A vast, mysterious canyon that contains peculiar magical ecosystems and is home to an array of magical creatures. The canyon walls might be etched with ancient runes, while the wind carries the faint echoes of long-forgotten spells.";
            case "desert":
                return "A seemingly endless expanse of sand with magical mirages, hidden oases, and rare magical flora that can survive the harsh conditions. Sandstorms in this desert are not only natural but could also be the result of ancient magical curses or protective enchantments.";
            case "forest":
                return "An already magical terrain in the Harry Potter universe, the Forbidden Forest is a dense and dangerous place filled with magical creatures, some friendly and others hostile. Dark magic, ancient secrets, and hidden paths abound, making it a place of wonder and fear.";
            case "mountain":
                return "A magical wetland where the water's surface shimmers with an otherworldly glow, hiding countless magical creatures below. The flora would have magical properties, and the area would be a haven for potion ingredients and rare creatures like magical amphibians and insects.";
            case "wetland":
                return "";
            default:
                return "Not Defined";
        }
    }

    /**
     * check if this land produces silver coin
     *
     * @return true if it produces silver coin
     */
    public boolean checkNifflerLand() {
        return ResourcePro[1];
    }

    /**
     * Get all coordinates on this territory
     *
     * @return all coordinates on this territory
     */
    public HashSet<int[]> getCoords() {
        return this.coords;
    }

    /**
     * Get the central point of this territory
     *
     * @return the central point of this territory
     */
    public int[] getCentralPoint() {
        int s1 = 0;
        int s2 = 0;
        for (int[] coord : coords) {
            s1 += coord[0];
            s2 += coord[1];
        }
        return new int[]{s1 / coords.size(), s2 / coords.size()};
    }

    /**
     * If the player want to upgrade from one type to another,
     * this function will return the corresponding costs
     *
     * @param type1 The unit type that the player want to upgrade from
     * @param type2 The unit type that the player want to upgrade to
     * @return The cost of upgrading from type1 to type2
     */
    public int getUpdateValue(String type1, String type2) {
        int cost1 = new Unit(type1).getCost();
        int cost2 = new Unit(type2).getCost();
        return cost2 - cost1;
    }

    /**
     * If the player want to upgrade from one type to another,
     * this function will return the corresponding costs
     *
     * @param type1 The unit type that the player want to upgrade from
     * @param type2 The unit type that the player want to upgrade to
     * @param num   The number of units that the player want to upgrade
     * @return 1 if the player has enough coins to upgrade, 0 otherwise
     */
    public int upgradeUnit(String type1, String type2, int num) {
        ArrayList<Integer> deleteIndex = new ArrayList<>();
        int i = 0;
        for (Unit unit : units) {
            if (unit.getType().equals(Unit.convertStringToUnitType(type1))) {
                deleteIndex.add(i);
                num--;
            }
            i++;
            if (num == 0) break;
        }
        assert num == 0;
        for (int index : deleteIndex) {
            units.remove(index - num++);
            units.add(new Unit(type2));
        }
        return getUpdateValue(type1, type2) * num;
    }

}
