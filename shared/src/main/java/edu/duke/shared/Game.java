package edu.duke.shared;

public class Game {
    private final int numPlayers;

    /*
     * Initialize Game by number of players
     * @param numPlayers Number of players
     */
    public Game(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    /*
     * Get number of players
     * @return number of players
     */
    public int getNumPlayers() {
        return this.numPlayers;
    }
}
