package edu.duke.shared.turn;

import java.io.Serializable;

public class Move implements Serializable {
    private final String from;
    private final String to;
    private final int numUnits;

    public Move(String from, String to, int numUnits) {
        this.from = from;
        this.to = to;
        this.numUnits = numUnits;
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
}
