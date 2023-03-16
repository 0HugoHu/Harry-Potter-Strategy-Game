package edu.duke.shared.turn;

import edu.duke.shared.map.GameMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttackTurn extends Turn {
    private final ArrayList<Attack> attacks = new ArrayList<>();

    public AttackTurn(GameMap map, int index, String playerName) {
        super(map, index, playerName, "attack");
    }

    public void addAttack(Attack att) {
        attacks.add(att);
    }

    public ArrayList<Attack> getAttacks() {
        return attacks;
    }
}

