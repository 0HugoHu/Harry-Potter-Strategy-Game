package edu.duke.shared.turn;

import java.io.Serializable;

public class Move extends Order{

    public Move(String from, String to, int numUnits,String playerName) {
        super(from,to,numUnits,playerName);
    }

}
