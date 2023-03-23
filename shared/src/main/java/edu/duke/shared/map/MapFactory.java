package edu.duke.shared.map;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import edu.duke.shared.helper.Dice;

public class MapFactory {
    // Game map
    private final GameMap gameMap;
    // Number of territories on this map
    private final int numTerritories;

    /**
     * Initialize Map by height and width
     *
     * @param height         Map height
     * @param width          Map width
     * @param numTerritories Number of territories on this map
     */
    public MapFactory(int height, int width, int numTerritories) {
        this.numTerritories = numTerritories;
        this.gameMap = new GameMap(height, width, numTerritories);
    }

    /**
     * Check if all coords in the map are allocated to territories
     *
     * @param m map to be checked
     * @return true if map is full-filled, false otherwise
     */
    public boolean isFullfilledMap(GameMap m) {
        int num_coords = 0;
        for (Territory t : m.getTerritories()) {
            num_coords += t.getCoords().size();
        }
        return num_coords == (m.getHeight() * m.getWidth());
    }

    /**
     * Check if the coord does not belong to any of the territories
     *
     * @param coord the Coordinate to be checked
     * @return true if the coord does not belong to any of the territories, false otherwise
     */
    public boolean isEmptyCoord(int[] coord) {
        for (Territory t : gameMap.getTerritories()) {
            if (t.contains(coord)) return false;
        }
        return true;
    }

    /**
     * Check if the coord is within the map
     *
     * @param coord the Coordinate to be checked
     * @return true if the coord is not out of the boundary of map, false otherwise
     */
    public boolean isValidCoord(int[] coord) {
        return coord[0] >= 0 && coord[1] >= 0 && coord[0] < gameMap.getHeight() && coord[1] < gameMap.getWidth();
    }

    /**
     * Get the adjcent coords of current coord
     *
     * @param coord the Coordinate
     * @return ArrayList of coords that are adjcent to coord passed in
     */
    public ArrayList<int[]> getAdjacentCoords(int[] coord) {
        ArrayList<int[]> adj_coords = new ArrayList<>();
        int[] dx = {0, 1, 0, -1};
        int[] dy = {1, 0, -1, 0};
        for (int i = 0; i < 4; i++) {
            int[] new_coord = new int[]{coord[0] + dy[i], coord[1] + dx[i]};
            if (isValidCoord(new_coord)) {
                adj_coords.add(new_coord);
            }
        }
        return adj_coords;
    }

    /**
     * Initialize Map by logic
     */
    public GameMap createRandomMap() {
        //Initialize the dice
        Dice dice = new Dice(20);
        //Initialize the array of Queues for each territory
        LinkedBlockingQueue<int[]>[] q = new LinkedBlockingQueue[this.numTerritories];
        for (int i = 0; i < this.numTerritories; i++) q[i] = new LinkedBlockingQueue<>();
        //Initialize the visited map with false for conducting BFS with Queue
        boolean[][][] visited = new boolean[this.numTerritories][gameMap.getHeight()][gameMap.getWidth()];
        for (int i = 0; i < this.numTerritories; i++)
            for (int j = 0; j < gameMap.getHeight(); j++)
                for (int k = 0; k < gameMap.getWidth(); k++)
                    visited[i][j][k] = false;
        //Initialize the array of territories
        Territory[] t = new Territory[this.numTerritories];
        for (int i = 0; i < this.numTerritories; i++) {
            char name = (char) ('A' + i);
            t[i] = new Territory(Character.toString(name));
            int coord_1d = gameMap.getHeight() * gameMap.getWidth() / this.numTerritories * i;
            int[] coord = new int[]{coord_1d / gameMap.getWidth(), coord_1d % gameMap.getWidth()};
            q[i].add(coord);
            visited[i][coord[0]][coord[1]] = true;
            gameMap.addTerritory(t[i]);
        }
        //Loop through each territory to expand their coords
        while (!isFullfilledMap(gameMap)) {
            for (int i = 0; i < this.numTerritories; i++) {
                if (q[i].isEmpty()) continue;
                int[] curr_coord = q[i].remove();
                while ((!q[i].isEmpty()) && (!isEmptyCoord(curr_coord))) {
                    curr_coord = q[i].remove();
                }
                if ((q[i].isEmpty()) && (!isEmptyCoord(curr_coord))) {
                    continue;
                }
                if (dice.getDice() > (int) (0.8 * dice.getMaxPoint())) {
                    t[i].addCoordinate(curr_coord);
                    for (int[] new_coord : getAdjacentCoords(curr_coord)) {
                        if (isEmptyCoord(new_coord) && (!visited[i][new_coord[0]][new_coord[1]])) {
                            q[i].add(new_coord);
                            visited[i][new_coord[0]][new_coord[1]] = true;
                        }
                    }
                } else {
                    q[i].add(curr_coord);
                }
            }
        }
        //Adding the adjcent relationships between territories
        for (int i = 0; i < gameMap.getHeight(); i++)
            for (int j = 0; j < gameMap.getWidth(); j++) {
                int[] curr_coord = new int[]{i, j};
                for (int[] new_coord : getAdjacentCoords(curr_coord)) {
                    Territory t1 = gameMap.getTerritoryByCoord(curr_coord);
                    Territory t2 = gameMap.getTerritoryByCoord(new_coord);
                    if (t1 != t2) {
                        t1.addAdjacent(t2.getName());
                        t2.addAdjacent(t1.getName());
                    }
                }
            }
        // Notify the map is generated
        this.gameMap.completed();
        return this.gameMap;
    }

}
