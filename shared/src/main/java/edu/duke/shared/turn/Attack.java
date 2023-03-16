package edu.duke.shared.turn;

import edu.duke.shared.player.Player;

import java.io.Serializable;
import java.util.ArrayList;

public class Attack extends Order{

    public Attack(String from, String to, int numUnits, String playerName) {
        super(from,to,numUnits,playerName);
    }

    public void removeUnit(){
        this.numUnits--;
    }

}
