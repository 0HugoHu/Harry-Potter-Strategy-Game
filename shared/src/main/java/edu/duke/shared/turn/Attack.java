package edu.duke.shared.turn;

import edu.duke.shared.player.Player;

import java.io.Serializable;
import java.util.ArrayList;

public class Attack implements Serializable {
    private final String from;
    private final String to;
    private  int numUnits;
    private Player player;


    public Attack(String from, String to, int numUnits, Player player) {
        this.from = from;
        this.to = to;
        this.numUnits = numUnits;
        this.player=player;
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

    public void removeUnit(){
        this.numUnits--;
    }

    public Player getplayer(){
        return player;
    }

    public void changeUnits(int newUnits){
        numUnits=newUnits;
    }


}
