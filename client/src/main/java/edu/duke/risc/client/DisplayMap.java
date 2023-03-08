package edu.duke.risc.client;

import java.util.HashMap;

import edu.duke.shared.Game;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;

public class DisplayMap {
    // Map
    private final Game game;
    // Map array
    private final int[][] mapArray;

    private final int playerId;


    /*
     * Initialize DisplayMap by map
     * @param map Map
     */
    public DisplayMap(Game game, int playerId) {
        this.game = game;
        this.mapArray = new int[this.game.getMap().getHeight()][this.game.getMap().getWidth()];
        this.playerId = playerId;
    }

    /*
     * Get String representation of map
     * @return String representation of map
     */
    public String showMap() {
        // Initialize map array
        StringBuilder sb = new StringBuilder();
        for (Territory t : this.game.getMap().getTerritories()) {
            for (int[] coord : t.getCoords()) {
                this.mapArray[coord[0]][coord[1]] = getTextRep(t.getOwner());
            }
        }
        // Convert map array to String
        for (int i = 0; i < this.game.getMap().getHeight(); i++) {
            for (int j = 0; j < this.game.getMap().getWidth(); j++) {
                sb.append((char) this.mapArray[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String showUnits() {
        StringBuilder sb = new StringBuilder();
        for (Player p : this.game.getPlayerList()) {
            sb.append(getColorRep(p.getPlayerId())).append(" player:\n").append("-------------\n");
            for (Territory t : this.game.getMap().getTerritoriesByOwner(p.getPlayerName())) {
                sb.append(t.getNumUnits()).append(" units in ").append(t.getName()).append(" (next to: ");
                for (String adjName : t.getAdjacents()) {
                    sb.append(adjName).append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                sb.append(")\n");
            }
            sb.append("\n\n");
        }

        sb.append("You are the ").append(getColorRep(this.playerId)).append(" player, what would you like to do?\n");
        sb.append("(M)ove\n(A)ttack\n(D)one\n\n");
        return sb.toString();
    }

    /*
     * Get text representation of owner name
     * @return text representation of owner name
     */
    private char getTextRep(String ownerName) {
        int playerId = this.game.getPlayer(ownerName).getPlayerId();
        switch (playerId) {
            case 0:
                return 'G';
            case 1:
                return 'B';
            case 2:
                return 'R';
            case 3:
                return 'Y';
            default:
                return ' ';
        }
    }

    private String getColorRep(int playerId) {
        switch (playerId) {
            case 0:
                return "Green";
            case 1:
                return "Blue";
            case 2:
                return "Red";
            case 3:
                return "Yellow";
            default:
                return "black";
        }
    }
}
