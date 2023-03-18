package edu.duke.shared.helper;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Move;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.unit.Unit;

public class ValidationTest {

    @Test
    public void checkIllegalOrderInput() {
        GameMap map = new GameMap(30, 30, 6);
        Territory t1 = new Territory("t1");
        t1.addUnit(new Unit("Normal"));
        Territory t2 = new Territory("t2");
        assertThrows(IllegalArgumentException.class, () -> Validation.checkIllegalOrderInput(map, "t1", "t2", 0, ""));
        map.addTerritory(t1);
        assertThrows(IllegalArgumentException.class, () -> Validation.checkIllegalOrderInput(map, "t1", "t2", 0, ""));
        map.addTerritory(t2);
        assertThrows(IllegalArgumentException.class, () -> Validation.checkIllegalOrderInput(map, "t1", "t2", -5, ""));
        assertThrows(IllegalArgumentException.class, () -> Validation.checkIllegalOrderInput(map, "t1", "t2", 1, "00"));
    }

    @Test
    public void checkMoves() {
        GameMap map = new GameMap(30, 30, 6);
        Territory t1 = new Territory("t1");
        t1.addUnit(new Unit("Normal"));
        t1.addUnit(new Unit("Normal"));
        t1.changeOwner("P1");
        Territory t2 = new Territory("t2");
        t2.changeOwner("P1");
        map.addTerritory(t1);
        map.addTerritory(t2);
        MoveTurn moveTurn = new MoveTurn(map, 0, "P1");
        Move move = new Move("t1", "t2", 1, "P1");
        moveTurn.addMove(move);
        Move move3 = new Move("t1", "t2", 1, "P1");
        moveTurn.addMove(move3);
        assertFalse(Validation.checkMoves(moveTurn));
        t1.addAdjacent(t2.getName());
        assertTrue(Validation.checkMoves(moveTurn));

        MoveTurn moveTurn2 = new MoveTurn(map, 0, "P1");
        Move move2 = new Move("t1", "t2", 10, "P1");
        moveTurn2.addMove(move2);
        assertFalse(Validation.checkMoves(moveTurn2));
        assertTrue(Validation.checkAdjacent(map, "t1", "t2"));
    }

    @Test
    public void checkMove() {

    }

    @Test
    public void checkAttacks() {
    }

    @Test
    public void checkAttack() {
        GameMap map = new GameMap(30, 30, 6);
        Territory t1 = new Territory("t1");
        t1.addUnit(new Unit("Normal"));
        t1.addUnit(new Unit("Normal"));
        t1.changeOwner("P1");
        Territory t2 = new Territory("t2");
        t2.changeOwner("P2");
        map.addTerritory(t1);
        map.addTerritory(t2);
        AttackTurn attackTurn = new AttackTurn(map, 0, "P1");
        Attack attack = new Attack("t1", "t2", 1, "P1");
        attackTurn.addAttack(attack);
        Attack attack1 = new Attack("t1", "t2", 1, "P1");
        attackTurn.addAttack(attack1);
        assertFalse(Validation.checkAttacks(attackTurn));
        t1.addAdjacent(t2.getName());
        assertTrue(Validation.checkAttacks(attackTurn));
        t2.changeOwner("P1");
        assertFalse(Validation.checkAttacks(attackTurn));

        AttackTurn attackTurn2 = new AttackTurn(map, 0, "P1");
        t2.changeOwner("P2");
        Attack attack2 = new Attack("t1", "t2", 10, "P1");
        attackTurn2.addAttack(attack2);
        assertFalse(Validation.checkAttacks(attackTurn2));
    }

    @Test
    public void checkPathExist() {
    }

    @Test
    public void checkAdjacent() {
    }

    @Test
    public void checkTerritory() {
    }
}