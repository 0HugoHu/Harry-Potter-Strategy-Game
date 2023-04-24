package edu.duke.shared.turn;

import java.util.ArrayList;

import edu.duke.shared.map.GameMap;

public class AttackTurn extends Turn {
    // List of attacks
    private final ArrayList<Attack> attacks = new ArrayList<>();

    /**
     * Initialize AttackTurn
     *
     * @param map        Game map
     * @param index      Turn index
     * @param playerName Player name
     */
    public AttackTurn(GameMap map, int index, String playerName) {
        super(map, index, playerName, "attack");
    }

    /**
     * Add an attack to this turn
     *
     * @param att Attack
     */
    public void addAttack(Attack att) {
        attacks.add(att);
    }

    /**
     * Get attacks
     *
     * @return attacks
     */
    public ArrayList<Attack> getAttacks() {
        return attacks;
    }
}

