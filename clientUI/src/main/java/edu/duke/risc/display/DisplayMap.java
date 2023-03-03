package edu.duke.risc.display;

import androidx.annotation.NonNull;

import java.util.HashMap;

import edu.duke.shared.GameMap;
import edu.duke.shared.Territory;

public class DisplayMap {
    // Map
    private final GameMap gameMap;
    // Map array
    private final int[][] mapArray;

    private final HashMap<String, Character> ownerNames;

    /*
     * Initialize DisplayMap by map
     * @param map Map
     */
    public DisplayMap(GameMap gameMap) {
        this.gameMap = gameMap;
        this.mapArray = new int[this.gameMap.getHeight()][this.gameMap.getWidth()];
        this.ownerNames = new HashMap<>();
    }

    /*
     * Get String representation of map
     * @return String representation of map
     */
    @NonNull
    public String toString() {
        // Initialize map array
        StringBuilder sb = new StringBuilder();
        for (Territory t : this.gameMap.getTerritories()) {
            for (int[] coord : t.getCoords()) {
                this.mapArray[coord[0]][coord[1]] = getTextRep(t.getOwner());
            }
        }
        // Convert map array to String
        for (int i = 0; i < this.gameMap.getHeight(); i++) {
            for (int j = 0; j < this.gameMap.getWidth(); j++) {
                sb.append((char) this.mapArray[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /*
     * Get text representation of owner name
     * @return text representation of owner name
     */
    private char getTextRep(String ownerName) {
        if (this.ownerNames.containsKey(ownerName)) {
            return this.ownerNames.get(ownerName);
        } else {
            // Use mono-space font here
            switch (this.ownerNames.size()) {
                case 0:
                    this.ownerNames.put(ownerName, 'A');
                    return 'A';
                case 1:
                    this.ownerNames.put(ownerName, 'C');
                    return 'C';
                case 2:
                    this.ownerNames.put(ownerName, 'D');
                    return 'D';
                case 3:
                    this.ownerNames.put(ownerName, 'E');
                    return 'E';
                default:
                    return ' ';
            }
        }
    }
}
