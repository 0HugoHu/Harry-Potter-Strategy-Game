package edu.duke.shared.helper;

import java.io.Serializable;
import java.util.ArrayList;

import edu.duke.shared.player.Horcrux;

public class Header implements Serializable {
    // Losers' id
    public final ArrayList<Integer> loserIds;
    // Player's id
    private int playerId;
    // Player's name
    private String playerName;
    // Game state
    private State state;
    // Turn index
    private int turnIndex;
    // Winner's id
    private int winnerId;
    // Force end game
    private boolean FORCE_END_GAME = false;
    // New horcrux
    private boolean NEW_HORCRUX = false;
    // Horcrux assign to
    private int HORCRUX_ASSIGN_TO = -1;
    // Horcrux item
    private Horcrux HORCRUX_ITEM;

    /**
     * Initialize Header
     */
    public Header() {
        this.state = State.WAITING_TO_JOIN;
        this.turnIndex = 0;
        this.loserIds = new ArrayList<>();
    }

    /**
     * Get winner id
     *
     * @return Winner's id
     */
    public int getWinnerId() {
        return this.winnerId;
    }

    /**
     * Set winner id
     *
     * @param winnerId Winner's id
     */
    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    /**
     * Add loser id
     *
     * @param loserId Loser's id
     */
    public void addLoserId(int loserId) {
        this.loserIds.add(loserId);
    }

    /**
     * Check if the player is loser
     *
     * @param playerId Player's id
     * @return True if the player is loser, false otherwise
     */
    public boolean isLoser(int playerId) {
        return this.loserIds.contains(playerId);
    }

    /*  Get player id
     *  @return Player's id
     */
    public int getPlayerId() {
        return this.playerId;
    }

    /**
     * Set player id
     *
     * @param playerId Player's id
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Get player name
     *
     * @return Player's name
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Set player name
     *
     * @param playerName Player's name
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Get game state
     *
     * @return Game state
     */
    public State getState() {
        return this.state;
    }

    /**
     * Set game state
     *
     * @param state Game state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Increase turn index by one
     */
    public void turnComplete() {
        this.turnIndex++;
    }

    /**
     * Get turn index
     *
     * @return Turn index
     */
    public int getTurn() {
        return this.turnIndex;
    }

    /**
     * Force end game
     */
    public void forceEndGame() {
        this.FORCE_END_GAME = true;
    }

    /**
     * Check if the game is forced to end
     *
     * @return True if the game is forced to end, false otherwise
     */
    public boolean isForceEndGame() {
        return this.FORCE_END_GAME;
    }

    /**
     * Set new horcrux
     */
    public void setNewHorcrux(Horcrux horcrux, int assignTo) {
        this.NEW_HORCRUX = true;
        this.HORCRUX_ITEM = horcrux;
        this.HORCRUX_ASSIGN_TO = assignTo;
    }

    /**
     * Set no horcrux
     */
    public void setNoHorcrux() {
        this.NEW_HORCRUX = false;
    }

    /**
     * Check if there is a new horcrux
     *
     * @return True if there is a new horcrux, false otherwise
     */
    public String getNewHorcrux() {
        if (!this.NEW_HORCRUX) return null;
        switch (this.HORCRUX_ITEM) {
            case DIARY:
                return "Riddle's Diary" + "%" + this.HORCRUX_ASSIGN_TO;
            case LOCKET:
                return "Slytherin's Locket" + "%" + this.HORCRUX_ASSIGN_TO;
            case RING:
                return "Gaunt's Ring" + "%" + this.HORCRUX_ASSIGN_TO;
            case CUP:
                return "Hufflepuff's Cup" + "%" + this.HORCRUX_ASSIGN_TO;
            case SNAKE:
                return "Nagini" + "%" + this.HORCRUX_ASSIGN_TO;
            default:
                return "Ravenclaw's Diadem" + "%" + this.HORCRUX_ASSIGN_TO;
        }
    }
}
