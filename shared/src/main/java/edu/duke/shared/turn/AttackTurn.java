package edu.duke.shared.turn;

import edu.duke.shared.map.GameMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttackTurn extends Turn{
    private ArrayList<Attack> attacks=new ArrayList<>();
    public AttackTurn(GameMap map, int index, String playerName) {
        super(map, index, playerName,"attack");
    }

    public boolean checkAttack() {

        for (Attack att : attacks) {
            //add all attack moves of same source together
            unitFromMap.put(att.getFrom(), unitFromMap.getOrDefault(att.getFrom(), 0) + att.getNumUnits());
            //check if the attacker is the user
            if (!checkTerritory(att.getFrom(), playerName)) {
                return false;
            }
            //check if the attacker is attacking a different player
            if (checkTerritory(att.getTo(), playerName)) {
                return false;
            }
            //check if the source place and destination place is adjacent
            if (!checkAdjacency(att.getFrom(), att.getTo())) {
                return false;
            }
            //if the units number is not enough,fail
            if (map.getTerritory(att.getFrom()).getNumUnits() < att.getNumUnits()) {
                return false;
            }
        }

        // Check units with multiple attacks
        for (String territoryName : unitFromMap.keySet()) {
            if (map.getTerritory(territoryName).getNumUnits() < unitFromMap.get(territoryName)) {
                return false;
            }
        }
        return true;
    }


    public void addAttack(Attack att) {
        attacks.add(att);
    }

    public ArrayList<Attack> getAttacks() {
        return attacks;
    }
}

