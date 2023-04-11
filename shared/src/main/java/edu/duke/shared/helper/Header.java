package edu.duke.shared.helper;

import java.io.Serializable;
import java.util.ArrayList;

public class Header implements Serializable {
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
    // Losers' id
    public final ArrayList<Integer> loserIds;

    private boolean FORCE_END_GAME = false;

    /**
     * Initialize Header
     */
    public Header() {
        this.state = State.WAITING_TO_JOIN;
        this.turnIndex = 0;
        this.loserIds = new ArrayList<>();
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
     * Get winner id
     *
     * @return Winner's id
     */
    public int getWinnerId() {
        return this.winnerId;
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

    /**
     * Set player id
     *
     * @param playerId Player's id
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /*  Get player id
     *  @return Player's id
     */
    public int getPlayerId() {
        return this.playerId;
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
     * Get player name
     *
     * @return Player's name
     */
    public String getPlayerName() {
        return this.playerName;
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
     * Get game state
     *
     * @return Game state
     */
    public State getState() {
        return this.state;
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

    public void forceEndGame() {
        this.FORCE_END_GAME = true;
    }

    public boolean isForceEndGame() {
        return this.FORCE_END_GAME;
    }
}
