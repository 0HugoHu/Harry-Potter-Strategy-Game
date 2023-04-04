package edu.duke.shared.helper;

import edu.duke.shared.Game;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Move;
import edu.duke.shared.turn.MoveTurn;

public class DisplayMap {
    // Map
    private final Game game;
    // Map array
    private final int[][] mapArray;
    // Player id
    private final int playerId;


    /*
     * Initialize DisplayMap by map
     * @param map Map
     * @param playerId Player id
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
        sb.append("\n\nGame Map:\n");
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

    /*
     * Get String representation of units
     * @param isSetup Whether it is setup phase
     * @return String representation of units
     */
    public String showUnits(boolean isSetup, MoveTurn moveTurn, AttackTurn attackTurn) {
        StringBuilder sb = new StringBuilder();
        if (!isSetup)
            sb.append("Turn ").append(this.game.getTurn()).append(":\n\n");
        for (Player p : this.game.getPlayerList()) {
            sb.append(p.getPlayerName()).append(":\n").append("------------------------------------\n");
            for (Territory t : this.game.getMap().getTerritoriesByOwner(p.getPlayerName())) {
                int numUnits=Validation.numOfChangeUnits(moveTurn,attackTurn,t.getName());
                sb.append(t.getNumUnits()+numUnits).append(" units in ").append(t.getName()).append(" (next to: ");
                for (String adjName : t.getAdjacents()) {
                    sb.append(adjName).append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                sb.append(")");
                sb.append(" [");
                sb.append("Unicorn Horns: ").append(t.getHorns()).append(", ");
                sb.append("Silver Coins: ").append(t.getCoins());
                sb.append("]\n");
            }
            sb.append("\n\n");
        }

        sb.append("You: ").append(this.game.getPlayerList().get(this.playerId).getPlayerName()).append(" (").append(getColorRep(this.playerId)).append(")");
        if (!isSetup) {
            sb.append(", what would you like to do?\n");
            sb.append("(M)ove\n(A)ttack\n(D)one\n\n");
        } else {
            sb.append("\n\n");
        }
        return sb.toString();
    }

    /*
     * Get text representation of owner name
     * @param ownerName Owner name
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

    /*
     * Get color representation of player id
     * @param playerId Player id
     * @return color representation of player id
     */
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
