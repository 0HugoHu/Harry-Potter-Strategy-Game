package edu.duke.shared.map;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class GameMap implements Serializable {
    private static final long serialVersionUID = 2346246420757690L;
    // All territories on this map
    private final ArrayList<Territory> territories;
    // Number of territories on this map
    private final int numTerritories;
    // Map height
    private final int height;
    // Map width
    private final int width;
    // Territory border coordinates
    // byte pattern code: 0001: top, 0010: right, 0100: bottom, 1000: left
    private final HashMap<String, Byte> borderPoints;
    private final HashMap<String, Integer> distances;

    /**
     * Get distance between two territories
     *
     * @param first  first territory
     * @param second second territory
     * @return distance between two territories
     */
    public int getDistance(String first, String second) {
        return distances.get(first + "&" + second);
    }

    /**
     * Put distance between two territories
     *
     * @param first  first territory
     * @param second second territory
     * @param dis    distance between two territories
     */
    public void putDistance(String first, String second, int dis) {
        distances.put(first + "&" + second, dis);
    }

    /**
     * Get the shortest distance between two territories
     *
     * @param first  first territory
     * @param second second territory
     * @return the shortest distance between two territories
     */
    public int getShortestDistance(String first, String second) {
        HashMap<String, List<String>> graph = new HashMap<>();
        for (Map.Entry<String, Integer> entry : distances.entrySet()) {
            String[] nodes = entry.getKey().split("&");
            String from = nodes[0];
            String to = nodes[1];
            if (!getTerritory(from).getOwner().equals(getTerritory(first).getOwner())) continue;
            if (!getTerritory(to).getOwner().equals(getTerritory(first).getOwner())) continue;
            if (!graph.containsKey(from)) {
                graph.put(from, new ArrayList<>());
            }
            graph.get(from).add(to);
            if (!graph.containsKey(to)) {
                graph.put(to, new ArrayList<>());
            }
            graph.get(to).add(from);
        }

        HashMap<String, Integer> dist = new HashMap<>();
        for (String node : graph.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(first, 0);

        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        priorityQueue.offer(first);

        while (!priorityQueue.isEmpty()) {
            String node = priorityQueue.poll();
            int distance = dist.get(node);
            if (node.equals(second)) {
                return distance;
            }
            if (distance == Integer.MAX_VALUE) {
                break;
            }
            for (String neighbor : graph.get(node)) {
                int newDistance = distance + distances.get(node + "&" + neighbor);
                if (newDistance < dist.get(neighbor)) {
                    dist.put(neighbor, newDistance);
                    priorityQueue.offer(neighbor);
                }
            }
        }

        return -1;
    }


    /**
     * Initialize Map by height and width
     *
     * @param height         Map height
     * @param width          Map height
     * @param numTerritories Number of territories on this map
     */
    public GameMap(int height, int width, int numTerritories) {
        this(height, width, numTerritories, new ArrayList<>());
    }

    /**
     * Initialize Map by height, width, and territories
     *
     * @param height         Map height
     * @param width          Map width
     * @param numTerritories Number of territories on this map
     * @param territories    Territories on this map
     */
    public GameMap(int height, int width, int numTerritories, ArrayList<Territory> territories) {
        this.height = height;
        this.width = width;
        this.numTerritories = numTerritories;
        this.territories = territories;
        this.borderPoints = new HashMap<>();
        this.distances = new HashMap<>();
    }

    /**
     * Get map height
     *
     * @return map height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Get map width
     *
     * @return map width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Get number of territories on this map
     *
     * @return number of territories on this map
     */
    public int getNumTerritories() {
        return this.numTerritories;
    }

    /**
     * Get territories on this map
     *
     * @return territories on this map
     */
    public ArrayList<Territory> getTerritories() {
        return this.territories;
    }

    /**
     * Get a territory by name
     *
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

    /**
     * Get a territory by owner name
     *
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

    /**
     * Get a territory by coordinate
     *
     * @param y y coordinate
     * @param x x coordinate
     * @return territory with the given coordinate
     */
    public String getOwnerByCoord(int y, int x) {
        for (Territory t : this.territories) {
            if (t.contains(new int[]{y, x}))
                return t.getOwner();
        }
        return null;
    }

    public String getOwnerByTerrName(String name) {
        for (Territory t : this.territories) {
            if (t.getName().equals(name))
                return t.getOwner();
        }
        return null;
    }

    /**
     * Get a territory by coordinate
     *
     * @param y y coordinate
     * @param x x coordinate
     * @return territory with the given coordinate
     */
    public String getTerritoryNameByCoord(int y, int x) {
        for (Territory t : this.territories) {
            if (t.contains(new int[]{y, x}))
                return t.getName();
        }
        return null;
    }

    /**
     * Add a territory to this map
     *
     * @param t Territory to be added
     * @return true if successfully added
     */
    public boolean addTerritory(Territory t) {
        if (this.territories.contains(t))
            return false;
        this.territories.add(t);
        return true;
    }

    /**
     * Check if a coordinate is a border point
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return 0 if not a border point, otherwise return the pattern code
     */
    public byte isBorderPoint(int y, int x) {
        return this.borderPoints.getOrDefault(y * 100000 + x + "", (byte) 0b0);
    }

    /**
     * Remove a territory by name from this map
     *
     * @param name Territory name
     * @return true if successfully removed
     */
    public boolean removeTerritory(String name) {
        for (Territory t : this.territories) {
            if (t.getName().equals(name)) {
                this.territories.remove(t);
                return true;
            }
        }
        return false;
    }

    /**
     * Get a territory by the coord it has
     *
     * @param coord The coordinate
     * @return Territory object if coord belongs to any of the territories, null otherwise
     */
    public Territory getTerritoryByCoord(int[] coord) {
        for (Territory t : territories) {
            if (t.contains(coord)) return t;
        }
        return null;
    }

    /**
     * Check if the map generation is completed
     * Then update border points
     *
     * @return true if the map is completed
     */
    public boolean completed() {
        for (Territory t : this.territories) {
            String tName = t.getName();
            // Traverse the territory and update border points
            for (int[] coord : t.getCoords()) {
                int x = coord[1];
                int y = coord[0];
                // Pattern code: 0001: top, 0010: right, 0100: bottom, 1000: left
                byte pattern = 0;
                if (this.getTerritoryNameByCoord(y, x - 1) == null || !this.getTerritoryNameByCoord(y, x - 1).equals(tName))
                    pattern += 8;
                if (this.getTerritoryNameByCoord(y, x + 1) == null || !this.getTerritoryNameByCoord(y, x + 1).equals(tName))
                    pattern += 2;
                if (this.getTerritoryNameByCoord(y - 1, x) == null || !this.getTerritoryNameByCoord(y - 1, x).equals(tName))
                    pattern += 1;
                if (this.getTerritoryNameByCoord(y + 1, x) == null || !this.getTerritoryNameByCoord(y + 1, x).equals(tName))
                    pattern += 4;
                this.borderPoints.put(y * 100000 + x + "", pattern);
            }
        }
        return true;
    }

    /**
     * return the corresponding resources for the certain player, with the Integer[] array's
     * first index being coin resources, second index being horn resources.
     *
     * @param playerID the player's ID
     * @return the resources for the player
     */
    public Integer[] getResources(int playerID) {
        Integer[] count = new Integer[]{0, 0};
        for (Territory terr : territories) {
            if (terr.getPlayerOwner().getPlayerId() == playerID) {
                count[0] += terr.getCoins();
                count[1] += terr.getHorns();
            }
        }
        return count;
    }


}
