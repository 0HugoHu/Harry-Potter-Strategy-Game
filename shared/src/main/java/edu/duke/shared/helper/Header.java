package edu.duke.shared.helper;

import java.io.Serializable;

public class Header implements Serializable {
    private int playerId;
    private String playerName;
    private State state;

    private int turnIndex;

    public Header() {
        this.state = State.WAITING_TO_JOIN;
        this.turnIndex = 0;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return this.state;
    }

    public void turnComplete() {
        this.turnIndex++;
    }

    public int getTurn() {
        return this.turnIndex;
    }
}
