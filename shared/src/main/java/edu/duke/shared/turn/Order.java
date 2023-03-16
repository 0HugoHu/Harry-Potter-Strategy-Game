package edu.duke.shared.turn;

import java.io.Serializable;

import edu.duke.shared.player.Player;

public abstract class Order implements Serializable {

    protected final String from;
    protected final String to;
    protected int numUnits;
    protected final String playerName;
    public Order(String from, String to, int numUnits, String playerName) {
        this.from = from;
        this.to = to;
        this.numUnits = numUnits;
        this.playerName=playerName;
    }
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getNumUnits() {
        return numUnits;
    }

    public String getPlayerName(){return playerName;}
}
