package edu.duke.shared.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import edu.duke.shared.player.House;
import edu.duke.shared.unit.UnitType;
import org.junit.Test;

import java.net.Socket;
import java.util.Scanner;

import edu.duke.shared.Game;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Move;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.unit.UnitType;

public class ValidationTest {

    @Test
    public void checkIllegalOrderInput() {
        Game game = new Game(3, 24);
        GameMap map = game.getMap();
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        Player p3 = new Player(2, new Socket());
        p3.setPlayerName("C");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.allocateTerritories();
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> Validation.checkIllegalOrderInput(map, "0", map.getTerritories().get(0).getName(), 0, game.getPlayerList().get(0).getPlayerName()));
        assertEquals("The source territory does not exist\n", thrown.getMessage());

        thrown = assertThrows(IllegalArgumentException.class, () -> Validation.checkIllegalOrderInput(map, map.getTerritories().get(0).getName(), "0", 0, game.getPlayerList().get(0).getPlayerName()));
        assertEquals("The destination territory does not exist\n", thrown.getMessage());

        thrown = assertThrows(IllegalArgumentException.class, () -> Validation.checkIllegalOrderInput(map, map.getTerritories().get(1).getName(), map.getTerritories().get(0).getName(), -1, game.getPlayerList().get(0).getPlayerName()));
        assertEquals("The number must be greater than or equal to 0\n", thrown.getMessage());


        thrown = assertThrows(IllegalArgumentException.class, () -> Validation.checkIllegalOrderInput(map, map.getTerritoriesByOwner(p1.getPlayerName()).get(0).getName(), map.getTerritoriesByOwner(p1.getPlayerName()).get(1).getName(), 0, p2.getPlayerName()));
        assertEquals("The source territory does not belong to you\n", thrown.getMessage());


    }

    @Test
    public void checkMoves() {
        Game game = new Game(3, 24);
        GameMap map = game.getMap();
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        p1.setHouse(House.GRYFFINDOR);
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        p2.setHouse(House.RAVENCLAW);
        Player p3 = new Player(2, new Socket());
        p3.setPlayerName("C");
        p3.setHouse(House.SLYTHERIN);
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.allocateTerritories();
        for (Territory t : map.getTerritories()) {
            for (int i = 0; i < 4; i++)
                t.addUnit(UnitType.GNOME);
        }
        MoveTurn mt = new MoveTurn(map, 0, p1.getPlayerName());
        String from = map.getTerritoriesByOwner(p1.getPlayerName()).get(0).getName();
        String to = map.getTerritoriesByOwner(p1.getPlayerName()).get(1).getName();
        mt.addMove(new Move(from, to, 2, p1.getPlayerName(),p1.getHouse()));
        from = map.getTerritoriesByOwner(p1.getPlayerName()).get(1).getName();
        to = map.getTerritoriesByOwner(p1.getPlayerName()).get(2).getName();
        mt.addMove(new Move(from, to, 2, p1.getPlayerName(),p2.getHouse()));
        assertTrue(Validation.checkMoves(mt));
        mt.addMove(new Move(map.getTerritoriesByOwner(p1.getPlayerName()).get(1).getName(),
                map.getTerritoriesByOwner(p1.getPlayerName()).get(3).getName(), 5, p1.getPlayerName(),p1.getHouse()));
        assertFalse(Validation.checkMoves(mt));
    }

    @Test
    public void testAllocateTwo() {
        Game game = new Game(2, 24);
        GameMap map = game.getMap();
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.allocateTerritories();
    }

    @Test
    public void testAllocateFour() {
        Game game = new Game(4, 24);
        GameMap map = game.getMap();
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        Player p3 = new Player(2, new Socket());
        p3.setPlayerName("C");
        Player p4 = new Player(3, new Socket());
        p3.setPlayerName("D");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.addPlayer(p4);
        game.allocateTerritories();
    }

    @Test
    public void checkMove() {
        Game game = new Game(3, 24);
        GameMap map = game.getMap();
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        Player p3 = new Player(2, new Socket());
        p3.setPlayerName("C");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.allocateTerritories();
        for (Territory t : map.getTerritories()) {
            for (int i = 0; i < 4; i++)
                t.addUnit(UnitType.GNOME);
        }
        MoveTurn mt = new MoveTurn(map, 0, p1.getPlayerName());
        String from = map.getTerritoriesByOwner(p1.getPlayerName()).get(0).getName();
        String to = map.getTerritoriesByOwner(p1.getPlayerName()).get(1).getName();
        mt.addMove(new Move(from, to, 2, p1.getPlayerName(),p1.getHouse()));
        from = map.getTerritoriesByOwner(p1.getPlayerName()).get(1).getName();
        to = map.getTerritoriesByOwner(p1.getPlayerName()).get(2).getName();
        mt.addMove(new Move(from, to, 2, p1.getPlayerName(),p1.getHouse()));
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> Validation.checkMove(mt, null, map.getTerritoriesByOwner(p1.getPlayerName()).get(1).getName(), map.getTerritoriesByOwner(p1.getPlayerName()).get(3).getName(), 5));
        assertEquals("The usable units in source are only 4 units\n", thrown.getMessage());

        thrown = assertThrows(IllegalArgumentException.class, () -> Validation.checkMove(mt, null, map.getTerritoriesByOwner(p1.getPlayerName()).get(1).getName(), map.getTerritoriesByOwner(p2.getPlayerName()).get(0).getName(), 3));
        assertEquals("There is no path from source to destination\n", thrown.getMessage());

    }

    @Test
    public void checkAttacks() {
        Game game = new Game(3, 24);
        GameMap map = game.getMap();
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        Player p3 = new Player(2, new Socket());
        p3.setPlayerName("C");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.allocateTerritories();
        for (Territory t : map.getTerritories()) {
            for (int i = 0; i < 4; i++)
                t.addUnit(UnitType.GNOME);
        }
        AttackTurn at = new AttackTurn(map, 0, p2.getPlayerName());
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> Validation.checkAttack(at, null, map.getTerritoriesByOwner(p2.getPlayerName()).get(0).getName(),
                map.getTerritoriesByOwner(p2.getPlayerName()).get(1).getName(), 2));
        assertEquals("You should not attack your territory\n", thrown.getMessage());
        String from1 = null;
        String to1 = null;
        String from2 = null;
        String to2 = null;
        boolean first = true;
        for (Territory t : map.getTerritoriesByOwner(p2.getPlayerName())) {
            for (String t2 : t.getAdjacents()) {
                if (!Validation.checkTerritory(map, t2, p2.getPlayerName())) {
                    if (first) {
                        from1 = t.getName();
                        to1 = t2;
                        first = false;
                    } else {
                        from2 = t.getName();
                        to2 = t2;
                    }
                }
            }
        }
        at.addAttack(new Attack(from1, to1, 2, p2.getPlayerName(),p2.getHouse()));
        at.addAttack(new Attack(from2, to2, 2, p2.getPlayerName(),p2.getHouse()));
        assertTrue(Validation.checkAttacks(at));
        at.addAttack(new Attack(from1, to1, 3, p2.getPlayerName(),p2.getHouse()));
        assertFalse(Validation.checkAttacks(at));
    }

    @Test
    public void checkAttack() {
        Game game = new Game(3, 24);
        GameMap map = game.getMap();
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        Player p3 = new Player(2, new Socket());
        p3.setPlayerName("C");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.allocateTerritories();
        for (Territory t : map.getTerritories()) {
            for (int i = 0; i < 4; i++)
                t.addUnit(UnitType.GNOME);
        }
        AttackTurn at = new AttackTurn(map, 0, p2.getPlayerName());
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> Validation.checkAttack(at, null, map.getTerritoriesByOwner(p2.getPlayerName()).get(0).getName(),
                map.getTerritoriesByOwner(p2.getPlayerName()).get(1).getName(), 2));
        assertEquals("You should not attack your territory\n", thrown.getMessage());
        String from = null;
        String to = null;
        for (Territory t : map.getTerritoriesByOwner(p2.getPlayerName())) {
            for (String t2 : t.getAdjacents()) {
                if (!Validation.checkTerritory(map, t2, p2.getPlayerName())) {
                    from = t.getName();
                    to = t2;
                }
            }
        }
        at.addAttack(new Attack(from, to, 2, p2.getPlayerName(),p2.getHouse()));
        try {
            Validation.checkAttack(at, null, from, to, 3);
        } catch (IllegalArgumentException e) {
            assertEquals("The usable units in source are only 2 units\n", e.getMessage());
        }
        try {
            Validation.checkAttack(at, null, from, map.getTerritoriesByOwner(p1.getPlayerName()).get(0).getName(), 1);
        } catch (IllegalArgumentException e) {
            assertEquals("The source and destination is not adjacent\n", e.getMessage());
        }
    }

    @Test
    public void checkPathExist() {
        Game game = new Game(3, 24);
        GameMap map = game.getMap();
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        Player p3 = new Player(2, new Socket());
        p3.setPlayerName("C");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.allocateTerritories();
        assertTrue(Validation.checkPathExist(map, map.getTerritoriesByOwner(p1.getPlayerName()).get(0).getName(), map.getTerritoriesByOwner(p1.getPlayerName()).get(1).getName()));
        assertFalse(Validation.checkPathExist(map, map.getTerritoriesByOwner(p1.getPlayerName()).get(0).getName(), map.getTerritoriesByOwner(p2.getPlayerName()).get(0).getName()));

    }

    @Test
    public void checkUnit() {
        Game game = new Game(3, 24);
        GameMap map = game.getMap();
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        Player p3 = new Player(2, new Socket());
        p3.setPlayerName("C");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.allocateTerritories();
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> Validation.checkUnit(map, map.getTerritoriesByOwner(p1.getPlayerName()).get(0).getName(), 3, 2, p1.getPlayerName()));
        assertEquals("You only have 2 units remaining\n", thrown.getMessage());
        Validation.checkUnit(map, map.getTerritoriesByOwner(p1.getPlayerName()).get(0).getName(), 2, 3, p1.getPlayerName());

    }

    @Test
    public void getValidNumber() {
        String source = "11a\n-1";
        Scanner scanner = new Scanner(source);
        assertEquals(-1, Validation.getValidNumber(scanner));
    }
}