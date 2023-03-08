package edu.duke.shared.turn;

import edu.duke.shared.map.GameMap;

public class AttackTurn extends Turn{
    public AttackTurn(GameMap map, int index, String playerName) {
        super(map, index, playerName);
    }
}
