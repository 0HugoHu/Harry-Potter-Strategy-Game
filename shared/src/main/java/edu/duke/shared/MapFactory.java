package edu.duke.shared;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MapFactory {
    private final Map map;
    private final int numTerritories;
    private ArrayList<Player> playerList;

    /**
     * Initialize Map by height and width
     * @param height Map height
     * @param width Map width
     * @param numTerritories Number of territories on this map
     * @param playerList the list of players
     */
    public MapFactory(int height, int width, int numTerritories,ArrayList<Player> playerList) {
        this.numTerritories = numTerritories;
        this.map = new Map(height, width, numTerritories);
        this.playerList=playerList;
    }

    /**
     * Check if all coords in the map are allocated to territories
     * @param m map to be checked
     * @return true if map is full-filled, false otherwise
     */
    public boolean isFullFilledMap(Map m) {
        int num_coords = 0;
        for (Territory t : m.getTerritories()) {
            num_coords += t.getCoords().size();
        }
        return num_coords == (m.getHeight() * m.getWidth());
    }

    /**
     * Check if the coord does not belong to any of the territories
     * @param coord the Coordinate to be checked
     * @return true if the coord does not belong to any of the territories, false otherwise
     */
    public boolean isEmptyCoord(int[] coord) {
        for (Territory t : map.getTerritories()) {
            if (t.contains(coord)) return false;
        }
        return true;
    }

    /**
     * Check if the coord is within the map
     * @param coord the Coordinate to be checked
     * @return true if the coord is not out of the boundary of map, false otherwise
     */
    public boolean isValidCoord(int[] coord) {
        return coord[0] >= 0 && coord[1] >= 0 && coord[0] < map.getHeight() && coord[1] < map.getWidth();
    }

    /**
     * Get the adjcent coords of current coord
     * @param coord the Coordinate
     * @return ArrayList of coords that are adjcent to coord passed in
     */
    public ArrayList<int[]> getAdjcentCoords(int[] coord) {
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
    public Map myLogic() {
        //Initialize the dice
        Dice dice = new Dice(20);
        //Initialize the array of Queues for each territory
        LinkedBlockingQueue<int[]>[] q = new LinkedBlockingQueue[this.numTerritories];
        for (int i=0;i<this.numTerritories;i++) q[i]=new LinkedBlockingQueue<>();
        //Initialize the visited map with false for conducting BFS with Queue
        boolean[][][] visited =new boolean[this.numTerritories][map.getHeight()][map.getWidth()];
        for (int i=0;i<this.numTerritories;i++)
            for (int j=0;j<map.getHeight();j++)
                for (int k=0;k<map.getWidth();k++)
                    visited[i][j][k]=false;
        //Initialize the array of territories
        Territory[] t = new Territory[this.numTerritories];
        for (int i = 0; i < this.numTerritories; i++) {
            t[i] = new Territory("Terr" + i);
            //create a new player
            Player player=new Player("Player"+((Integer)(i/6)).toString());
            //give player the ownership of this land
            player.expandTerr(t[i]);
            //add player to playerList
            playerList.add(player);
            t[i].changeOwner("Player"+ (i/6));
            //set ownership to this territory
            t[i].changePlayerOwner(player);
            int coord_1d = map.getHeight() * map.getWidth() / this.numTerritories * i;
            int[] coord = new int[]{coord_1d / map.getWidth(), coord_1d % map.getWidth()};
            q[i].add(coord);
            visited[i][coord[0]][coord[1]]=true;
            map.addTerritory(t[i]);
        }
        //Loop through each territory to expand their coords
        while (!isFullFilledMap(map)) {
            for (int i = 0; i < this.numTerritories; i++) {
                if (q[i].isEmpty()) continue;
                int[] curr_coord = q[i].remove();
                while ((!q[i].isEmpty()) && (!isEmptyCoord(curr_coord))) {
                    curr_coord = q[i].remove();
                }
                if ((q[i].isEmpty()) && (!isEmptyCoord(curr_coord))) {
                    continue;
                }
                if (dice.getDice() > (int)(0.8*dice.getMaxPoint())) {
                    t[i].addCoordinate(curr_coord);
                    for (int[] new_coord : getAdjcentCoords(curr_coord)) {
                        if (isEmptyCoord(new_coord) && (!visited[i][new_coord[0]][new_coord[1]])) {
                            q[i].add(new_coord);
                            visited[i][new_coord[0]][new_coord[1]]=true;
                        }
                    }
                } else {
                    q[i].add(curr_coord);
                }
            }
        }
        //Adding the adjcent relationships between territories
        for (int i=0;i<map.getHeight();i++)
            for (int j=0;j<map.getWidth();j++){
                int[] curr_coord=new int[]{i,j};
                for (int[] new_coord:getAdjcentCoords(curr_coord)){
                    Territory t1=map.getTerritoryByCoord(curr_coord);
                    Territory t2=map.getTerritoryByCoord(new_coord);
                    if (t1!=t2){
                        t1.addAdjacent(t2.getName());
                        t2.addAdjacent(t1.getName());
                    }
                }
            }
        return this.map;
    }

    /*
     * Initialize Map by template
     * ONLY USED FOR TESTING
     */
    public Map myTemplateMap() {
        for (int i = 0; i < this.numTerritories; i++) {
            // Create a territory
            Territory t;
            // Add territory owner
            if (i < 6) {
                t = new Territory("A" + i);
                t.changeOwner("green");
            } else if (i < 12) {
                t = new Territory("B" + (i - 6));
                t.changeOwner("blue");
            } else {
                t = new Territory("C" + (i - 12));
                t.changeOwner("orange");
            }
            // Add 6 NORMAL units
            for (int j = 0; j < 6; j++) {
                t.addUnit(new Unit("Normal"));
            }
            // Add coordinates and adjacent territories
            switch (i) {
                case 0:
                    t.addCoordinateHelper(t, 3, 3, 0, 0);
                    t.addCoordinateHelper(t, 3, 3, 1, 0);
                    t.addCoordinateHelper(t, 3, 3, 2, 0);
                    t.addCoordinateHelper(t, 3, 3, 0, 1);
                    t.addAdjacent("A1");
                    break;
                case 1:
                    t.addCoordinateHelper(t, 3, 3, 0, 2);
                    t.addCoordinateHelper(t, 3, 3, 1, 1);
                    t.addCoordinateHelper(t, 3, 3, 1, 2);
                    t.addCoordinateHelper(t, 3, 3, 2, 1);
                    t.addCoordinateHelper(t, 3, 3, 2, 2);
                    t.addCoordinateHelper(t, 3, 3, 3, 0);
                    t.addCoordinateHelper(t, 3, 3, 3, 1);
                    t.addAdjacent("A2");
                    t.addAdjacent("C0");
                    t.addAdjacent("C1");
                    t.addAdjacent("A5");
                    t.addAdjacent("A0");
                    break;
                case 2:
                    t.addCoordinateHelper(t, 3, 3, 4, 0);
                    t.addCoordinateHelper(t, 3, 3, 4, 1);
                    t.addCoordinateHelper(t, 3, 3, 5, 1);
                    t.addCoordinateHelper(t, 3, 3, 6, 1);
                    t.addCoordinateHelper(t, 3, 3, 7, 1);
                    t.addAdjacent("A1");
                    t.addAdjacent("C0");
                    t.addAdjacent("A3");
                    t.addAdjacent("A4");
                    t.addAdjacent("B0");
                    break;
                case 3:
                    t.addCoordinateHelper(t, 3, 3, 5, 0);
                    t.addCoordinateHelper(t, 3, 3, 6, 0);
                    t.addAdjacent("A2");
                    t.addAdjacent("B0");
                    break;
                case 4:
                    t.addCoordinateHelper(t, 3, 3, 8, 1);
                    t.addAdjacent("A2");
                    t.addAdjacent("B0");
                    t.addAdjacent("B1");
                    break;
                case 5:
                    t.addCoordinateHelper(t, 3, 3, 0, 3);
                    t.addCoordinateHelper(t, 3, 3, 1, 3);
                    t.addCoordinateHelper(t, 3, 3, 0, 4);
                    t.addCoordinateHelper(t, 3, 3, 1, 4);
                    t.addAdjacent("A1");
                    t.addAdjacent("C1");
                    t.addAdjacent("C2");
                    break;
                case 6:
                    t.addCoordinateHelper(t, 3, 3, 3, 2);
                    t.addCoordinateHelper(t, 3, 3, 3, 3);
                    t.addCoordinateHelper(t, 3, 3, 3, 4);
                    t.addCoordinateHelper(t, 3, 3, 4, 2);
                    t.addCoordinateHelper(t, 3, 3, 5, 2);
                    t.addCoordinateHelper(t, 3, 3, 6, 2);
                    t.addCoordinateHelper(t, 3, 3, 6, 3);
                    t.addCoordinateHelper(t, 3, 3, 7, 2);
                    t.addAdjacent("A3");
                    t.addAdjacent("A2");
                    t.addAdjacent("A4");
                    t.addAdjacent("B1");
                    break;
                case 7:
                    t.addCoordinateHelper(t, 3, 3, 2, 3);
                    t.addCoordinateHelper(t, 3, 3, 2, 4);
                    t.addAdjacent("B0");
                    t.addAdjacent("A4");
                    t.addAdjacent("C0");
                    t.addAdjacent("B4");
                    t.addAdjacent("C5");
                    break;
                case 8:
                    t.addCoordinateHelper(t, 3, 3, 0, 5);
                    t.addCoordinateHelper(t, 3, 3, 1, 5);
                    t.addCoordinateHelper(t, 3, 3, 2, 5);
                    t.addAdjacent("C0");
                    t.addAdjacent("C3");
                    t.addAdjacent("B3");
                    break;
                case 9:
                    t.addCoordinateHelper(t, 3, 3, 3, 5);
                    t.addCoordinateHelper(t, 3, 3, 4, 5);
                    t.addCoordinateHelper(t, 3, 3, 5, 5);
                    t.addCoordinateHelper(t, 3, 3, 6, 5);
                    t.addAdjacent("C0");
                    t.addAdjacent("B2");
                    t.addAdjacent("C3");
                    t.addAdjacent("B4");
                    break;
                case 10:
                    t.addCoordinateHelper(t, 3, 3, 7, 5);
                    t.addCoordinateHelper(t, 3, 3, 8, 5);
                    t.addCoordinateHelper(t, 3, 3, 9, 5);
                    t.addCoordinateHelper(t, 3, 3, 9, 4);
                    t.addAdjacent("B3");
                    t.addAdjacent("B5");
                    t.addAdjacent("B1");
                    t.addAdjacent("C0");
                    t.addAdjacent("C3");
                    t.addAdjacent("C4");
                    t.addAdjacent("C5");
                    break;
                case 11:
                    t.addCoordinateHelper(t, 3, 3, 9, 3);
                    t.addAdjacent("C0");
                    t.addAdjacent("B1");
                    t.addAdjacent("B4");
                    break;
                case 12:
                    t.addCoordinateHelper(t, 3, 3, 7, 0);
                    t.addCoordinateHelper(t, 3, 3, 8, 0);
                    t.addAdjacent("A1");
                    t.addAdjacent("A2");
                    t.addAdjacent("B2");
                    t.addAdjacent("B3");
                    t.addAdjacent("B4");
                    t.addAdjacent("B5");
                    t.addAdjacent("C1");
                    t.addAdjacent("C3");
                    break;
                case 13:
                    t.addCoordinateHelper(t, 3, 3, 9, 0);
                    t.addCoordinateHelper(t, 3, 3, 9, 1);
                    t.addCoordinateHelper(t, 3, 3, 9, 2);
                    t.addCoordinateHelper(t, 3, 3, 8, 2);
                    t.addAdjacent("A1");
                    t.addAdjacent("A5");
                    t.addAdjacent("C0");
                    t.addAdjacent("C2");
                    break;
                case 14:
                    t.addCoordinateHelper(t, 3, 3, 4, 3);
                    t.addCoordinateHelper(t, 3, 3, 4, 4);
                    t.addAdjacent("A5");
                    t.addAdjacent("C1");
                    t.addAdjacent("C3");
                    break;
                case 15:
                    t.addCoordinateHelper(t, 3, 3, 5, 3);
                    t.addCoordinateHelper(t, 3, 3, 5, 4);
                    t.addAdjacent("B2");
                    t.addAdjacent("B3");
                    t.addAdjacent("B4");
                    t.addAdjacent("C0");
                    t.addAdjacent("C2");
                    t.addAdjacent("C4");
                    break;
                case 16:
                    t.addCoordinateHelper(t, 3, 3, 6, 4);
                    t.addCoordinateHelper(t, 3, 3, 7, 4);
                    t.addCoordinateHelper(t, 3, 3, 8, 4);
                    t.addCoordinateHelper(t, 3, 3, 8, 3);
                    t.addAdjacent("B4");
                    t.addAdjacent("C3");
                    t.addAdjacent("C5");
                    break;
                case 17:
                    t.addCoordinateHelper(t, 3, 3, 7, 3);
                    t.addAdjacent("B1");
                    t.addAdjacent("B4");
                    t.addAdjacent("C4");
                    break;
                default:
                    break;
            }
            // add the territory to the map
            map.addTerritory(t);
        }
        return this.map;
    }

}
