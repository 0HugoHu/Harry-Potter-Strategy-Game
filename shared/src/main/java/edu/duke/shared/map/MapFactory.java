package edu.duke.shared.map;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import edu.duke.shared.helper.Dice;

public class MapFactory {
    // Game map
    private final GameMap gameMap;
    // Number of territories on this map
    private final int numTerritories;

    private final String[] territoryNames =
            {"Hogwarts", "Godric's Hollow", "Grimmauld Place", "Diagon Alley",
            "Spinner's End", "Hogsmeade", "Azkaban", "Durmstrang",
            "Beauxbatons", "Forbidden Forest", "Albania", "Little Hangleton",
            "Malfoy Manor", "Knockturn Alley", "Nurmengard", "St Mungo",
            "Little Whinging", "Shell Cottage", "Gringotts", "Ministry of Magic",
            "Uagadou", "Castelobruxo", "Ilvermorny", "Mahoutokoro"};

    private final String[] territoryDetails={
            "Hogwarts: A British school of magic for students aged eleven to seventeen, founded around the 9th century and 10th century.",
            "Godric's Hollow: A village in the West Country of England, which was inhabited by a number of notable wizarding families.",
            "Grimmauld Place: The ancestral home of the Black family, which was protected by a Fidelius Charm.",
            "Diagon Alley: A wizarding alley and shopping area located in London, hidden from the Muggle world.",
            "Spinner's End: The place where the childhood home of Severus Snape was located.",
            "Hogsmeade: A picturesque little village of cottages and shops, also the only all-wizarding village in Britain.",
            "Azkaban: A fortress on an island in the middle of the North Sea, guarded by Dementors.",
            "Durmstrang: A magic school known for placing an emphasis on the study of the Dark Arts, located in North.",
            "Beauxbatons: A boarding school located in the Pyrenees mountains of southern France full of ice sculptures and forest nymphs.",
            "Forbidden Forest: A forest that bordered the edges of the grounds of Hogwarts School of Witchcraft and Wizardry.",
            "Albania: The place where Lord Voldemort used to hide, also where Helena Ravenclaw chose to hide her mother's stolen diadem.",
            "Little Hangleton: A Muggle village some 200 miles from Little Whinging, notable as the place of origin of Voldemort's maternal and paternal ancestors.",
            "Malfoy Manor: The home of the wealthy pure-blood Malfoy family, located in Wiltshire, England.",
            "Knockturn Alley: A dark and seedy alleyway diagonal to Diagon Alley, frequently populated by Dark Wizards.",
            "Nurmengard: The prison that Gellert Grindelwald built to keep his enemies and Muggles in, with entrance marked with the symbol of the Deathly Hallows.",
            "St Mungo: A hospital established to treat magical maladies, injuries or illnesses endemic to the Wizarding World.",
            "Little Whinging: A small town located to the south of London, where Harry Potter used to live before going to Hogwarts.",
            "Shell Cottage: The home of Bill Weasley and Fleur Delacour after they get married in Deathly Hallows.",
            "Gringotts: The well-known bank of the wizarding world and it is operated primarily by goblins.",
            "Ministry of Magic: The government of the Magical community of Britain, requires special code to get in.",
            "Uagadou: The oldest of several African wizarding schools, and the largest in the entire world.",
            "Castelobruxo: The South American school of magic, based in Brazil, which resembles a golden temple.",
            "Ilvermorny: an American school of magic, which serves as the school for the North American continent.",
            "Mahoutokoro: Mahoutokoro is the smallest wizarding school, and is situated in Japan. "
    };



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
//            char name = (char) ('A' + i);
            t[i] = new Territory(territoryNames[i]);
            t[i].addDetails(territoryDetails[i]);
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
        //Setting distance attribute
        for (int i = 0; i < numTerritories; i++)
            for (int j = i; j < numTerritories; j++) {
                if (i == j) {
                    gameMap.putDistance(t[i].getName(), t[j].getName(), 0);
                    continue;
                }
                if (t[i].isAdjacent(t[j].getName())) {
                    int dis = calDistance(t[i].getCentralPoint(), t[j].getCentralPoint()) + new Dice(10).getDice();
                    gameMap.putDistance(t[i].getName(), t[j].getName(), dis);
                    gameMap.putDistance(t[j].getName(), t[i].getName(), dis);
                } else {
                    gameMap.putDistance(t[i].getName(), t[j].getName(), -1);
                    gameMap.putDistance(t[j].getName(), t[i].getName(), -1);
                }
            }


        // Notify the map is generated
        this.gameMap.completed();
        return this.gameMap;
    }

    public int calDistance(int[] fir, int[] sec) {
        return (int) Math.sqrt((fir[0] - sec[0]) * (fir[0] - sec[0]) + (fir[1] - sec[1]) * (fir[1] - sec[1]));
    }
}
