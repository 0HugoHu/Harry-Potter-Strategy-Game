package edu.duke.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import edu.duke.shared.helper.State;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Horcrux;
import edu.duke.shared.player.House;
import edu.duke.shared.player.Player;
import edu.duke.shared.player.SkillState;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.turn.Turn;
import edu.duke.shared.unit.UnitType;

public class GameTest {

    @Test
    public void testCal() {
        Game newGame = new Game(3, 24);
        assertEquals(2, newGame.calculateOrderCost(2, 2));
    }

    @Test
    public void getBonusFromType() {
        Game newGame = new Game(3, 24);
        assertEquals(0, newGame.getBonusFromType(UnitType.GNOME));
        assertEquals(1, newGame.getBonusFromType(UnitType.DWARF));
        assertEquals(3, newGame.getBonusFromType(UnitType.HOUSE_ELF));
        assertEquals(5, newGame.getBonusFromType(UnitType.GOBLIN));
        assertEquals(8, newGame.getBonusFromType(UnitType.VAMPIRE));
        assertEquals(11, newGame.getBonusFromType(UnitType.CENTAUR));
        assertEquals(15, newGame.getBonusFromType(UnitType.WEREWOLF));
    }

    @Test
    public void testCompare() {
        Game newGame = new Game(3, 24);
        assertEquals(1, newGame.compareUnitHigh(UnitType.WEREWOLF, UnitType.DWARF));
    }


    /**
     * test method for getNumPlayers
     */
    @Test
    public void testGetNumPlayers() {
        Game newGame = new Game(3, 24);
        assertEquals(3, newGame.getNumPlayers());
    }

    /**
     * Test method for getMap
     */
    @Test
    public void testGetMap() {
        Game newGame = new Game(3, 24);
        GameMap m = newGame.getMap();
        assertEquals(30, m.getHeight());
        assertEquals(60, m.getWidth());
    }

    @Test
    public void testPlayerId() {
        Game newGame = new Game(2, 24);
        newGame.setPlayerId(1);
        assertEquals(1, newGame.getPlayerId());
    }

    @Test
    public void testPlayerName() {
        Game newGame = new Game(2, 24);
        newGame.setPlayerName("A");
        assertEquals("A", newGame.getPlayerName());
    }

    @Test
    public void testGamestate() {
        Game newGame = new Game(2, 24);
        newGame.setGameState(State.TURN_BEGIN);
        assertEquals(State.TURN_BEGIN, newGame.getGameState());
    }

    @Test
    public void testTurnComplete() {
        Game newGame = new Game(2, 24);
        newGame.turnComplete();
        assertEquals(1, newGame.getTurn());
    }

    @Test
    public void testWinner() {
        Game newGame = new Game(2, 24);
        newGame.setWinnerId(1);
        assertEquals(1, newGame.getWinnerId());
    }

    @Test
    public void testLoser() {
        Game newGame = new Game(2, 24);
        newGame.addLoserId(0);
        assertTrue(newGame.isLoser(0));
    }

    @Test
    public void addPlayer() {
        Game newGame = new Game(2, 24);
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        newGame.addPlayer(p1);
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        newGame.addPlayer(p2);
        assertEquals(2, newGame.getNumPlayers());
    }


    @Test
    public void doAttack() {
        Game newGame = new Game(3, 24);
        GameMap m = newGame.getMap();
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        Player p3 = new Player(2, new Socket());
        p3.setPlayerName("C");
        newGame.addPlayer(p1);
        newGame.addPlayer(p2);
        newGame.addPlayer(p3);

        ArrayList<Territory> terrs = m.getTerritories();
        ArrayList<Player> players = newGame.getPlayerList();
        int numTerrs = m.getNumTerritories();
        int numPlayers = newGame.getNumPlayers();
        for (int i = 0; i < numTerrs; i++) {
            players.get(i / (numTerrs / numPlayers)).expandTerr(terrs.get(i));
            terrs.get(i).changePlayerOwner(players.get(i / (numTerrs / numPlayers)));
            terrs.get(i).changeOwner(players.get(i / (numTerrs / numPlayers)).getPlayerName());
        }

        for (Territory t : terrs) {
            for (int i = 0; i < 6; i++)
                newGame.getMap().getTerritory(t.getName()).addUnit(UnitType.GNOME);
        }

        AttackTurn attTurn1 = new AttackTurn(m, 0, "A");
        MoveTurn moveTurn1 = new MoveTurn(m, 0, "A");
        HashMap<UnitType, Integer> unitlist1 = new HashMap<>();
        unitlist1.put(UnitType.GNOME, 2);
        Attack attack3 = new Attack("Hogwarts", "Beauxbatons", unitlist1, "A", House.GRYFFINDOR);
        attTurn1.addAttack(attack3);
        ArrayList<Turn> newTurn1 = new ArrayList<>();
        newTurn1.add(moveTurn1);
        newTurn1.add(attTurn1);

        AttackTurn attTurn2 = new AttackTurn(m, 0, "B");
        MoveTurn moveTurn2 = new MoveTurn(m, 0, "B");
        HashMap<UnitType, Integer> unitlist2 = new HashMap<>();
        unitlist2.put(UnitType.GNOME, 2);
        Attack attack4 = new Attack("Beauxbatons", "Grimmauld Place", unitlist2, "B",House.HUFFLEPUFF);
        attTurn2.addAttack(attack4);
        ArrayList<Turn> newTurn2 = new ArrayList<>();
        newTurn2.add(moveTurn2);
        newTurn2.add(attTurn2);

        AttackTurn attTurn3 = new AttackTurn(m, 0, "C");
        MoveTurn moveTurn3 = new MoveTurn(m, 0, "C");
        HashMap<UnitType, Integer> unitlist3 = new HashMap<>();
        unitlist3.put(UnitType.GNOME, 2);
        HashMap<UnitType, Integer> unitlist4 = new HashMap<>();
        unitlist4.put(UnitType.GNOME, 2);
        Attack attack1 = new Attack("Little Whinging", "Beauxbatons", unitlist3, "C",House.RAVENCLAW);
        Attack attack2 = new Attack("Ministry of Magic", "Beauxbatons", unitlist4, "C",House.RAVENCLAW);
        attTurn3.addAttack(attack1);
        attTurn3.addAttack(attack2);
        ArrayList<Turn> newTurn3 = new ArrayList<>();
        newTurn3.add(moveTurn1);
        newTurn3.add(attTurn1);
        //newGame.addToTurnMap(2,moveTurn3,attTurn3);

        HashMap<Integer, ArrayList<Turn>> turnList = newGame.getTurnList();
        HashMap<Integer, ArrayList<Turn>> turns = new HashMap<>();
        turns.put(0, newTurn1);
        turns.put(1, newTurn2);
        turns.put(2, newTurn3);

        newGame.makeAttackList(attTurn1);
        newGame.makeAttackList(attTurn2);
        newGame.makeAttackList(attTurn3);

        HashMap<Integer, ArrayList<Turn>> turnlist = newGame.getTurnList();
//        newGame.doAttack();
        newGame.getString();


    }


    @Test
    public void TestAddToTurnMap() {
        Game newGame = new Game(3, 24);
        GameMap m = newGame.getMap();
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        Player p3 = new Player(2, new Socket());
        p3.setPlayerName("C");
        newGame.addPlayer(p1);
        newGame.addPlayer(p2);
        newGame.addPlayer(p3);

        AttackTurn attTurn1 = new AttackTurn(m, 0, "A");
        MoveTurn moveTurn1 = new MoveTurn(m, 0, "A");
        Attack attack3 = new Attack("B", "G", 2, "A",House.GRYFFINDOR);
        attTurn1.addAttack(attack3);
        ArrayList<Turn> newTurn1 = new ArrayList<>();
        newTurn1.add(moveTurn1);
        newTurn1.add(attTurn1);

        newGame.addToTurnMap(0, moveTurn1, attTurn1);
        assertEquals(1, newGame.getTurnList().size());

    }

    @Test
    public void getPlayer() {
        Game newGame = new Game(3, 24);
        Player p1 = new Player(0, new Socket());
        p1.setPlayerName("A");
        Player p2 = new Player(1, new Socket());
        p2.setPlayerName("B");
        newGame.addPlayer(p1);
        newGame.addPlayer(p2);
        assertSame(newGame.getPlayer("A"), p1);
        assertSame(newGame.getPlayer("B"), p2);
        assertNull(newGame.getPlayer("C"));
    }

    @Test
    public void getString() {
        Game g = new Game(2,24);
        g.getString();
    }

    @Test
    public void useSnake() {
        Game g = new Game(2,24);
        Player p1 = new Player(0,new Socket());
        p1.setPlayerName("a");

        Player p2 = new Player(1,new Socket());
        p2.setPlayerName("b");
        g.addPlayer(p1);
        g.addPlayer(p2);
        g.allocateTerritories();
        g.useSnake(p1);
    }

    @Test
    public void useLocket() {
        Game g = new Game(2,24);
        Player p1 = new Player(0,new Socket());
        p1.setPlayerName("a");

        Player p2 = new Player(1,new Socket());
        p2.setPlayerName("b");
        g.addPlayer(p1);
        g.addPlayer(p2);
        g.allocateTerritories();
        for (Territory t:p1.getPlayerTerrs()){
            t.addUnit(UnitType.DWARF);
        }
        for (Territory t:p2.getPlayerTerrs()){
            t.addUnit(UnitType.DWARF);
        }
        g.useLocket(p1);
    }

    @Test
    public void useSkillSytherin() {
        Game g = new Game(2,24);
        Player p1 = new Player(0,new Socket(),House.SLYTHERIN);
        p1.setPlayerName("a");
        p1.setSkillState(SkillState.IN_EFFECT);
        Player p2 = new Player(1,new Socket(),House.GRYFFINDOR);
        p2.setPlayerName("b");
        g.addPlayer(p1);
        g.addPlayer(p2);
        g.allocateTerritories();
        for (Territory t:p1.getPlayerTerrs()){
            t.addUnit(UnitType.WEREWOLF);
        }
        for (Territory t:p2.getPlayerTerrs()){
            t.addUnit(UnitType.WEREWOLF);
        }
        g.UseSkillSytherin(p1);
    }

    @Test
    public void useRing() {
        Game g = new Game(2,24);
        Player p1 = new Player(0,new Socket());
        p1.setPlayerName("a");

        Player p2 = new Player(1,new Socket());
        p2.setPlayerName("b");
        g.addPlayer(p1);
        g.addPlayer(p2);
        g.allocateTerritories();
        for (Territory t:p1.getPlayerTerrs()){
            t.addUnit(UnitType.DWARF);
        }
        for (Territory t:p2.getPlayerTerrs()){
            t.addUnit(UnitType.DWARF);
        }
        g.useRing(p1);
        for (Territory t:p1.getPlayerTerrs()) {
            t.changeOwner(p2.getPlayerName());
            p2.expandTerr(t);
        }
        p1.getPlayerTerrs().clear();
        g.useRing(p1);
    }

    @Test
    public void useSkillGryffindor() {
        Game g = new Game(2,24);
        Player p1 = new Player(0,new Socket(),House.SLYTHERIN);
        p1.setPlayerName("a");
        p1.setSkillState(SkillState.IN_EFFECT);
        Player p2 = new Player(1,new Socket(),House.GRYFFINDOR);
        p2.setPlayerName("b");
        p2.setSkillState(SkillState.IN_EFFECT);
        g.addPlayer(p1);
        g.addPlayer(p2);
        g.allocateTerritories();
        for (Territory t:p1.getPlayerTerrs()){
            t.addUnit(UnitType.WEREWOLF);
        }
        for (Territory t:p2.getPlayerTerrs()){
            t.addUnit(UnitType.WEREWOLF);
        }
        g.UseSkillGryffindor(p2);
    }

    @Test
    public void setUpDefense() {
    }

    @Test
    public void printList() {
        Game g = new Game(2,24);
        ArrayList<ArrayList<Attack>> att = new ArrayList<>();
        for (int i=0;i<2;i++){
            ArrayList<Attack> a = new ArrayList<>();
            for (int j=0;j<5;j++){
                HashMap<UnitType,Integer> unitlist = new HashMap<>();
                unitlist.put(UnitType.GNOME,5);
                Attack at = new Attack(i+"from"+j,i+"to"+j,unitlist,"player"+i,House.GRYFFINDOR);
                a.add(at);
            }
            att.add(a);
        }
        g.printList(att,0,1,1,2);
    }

    @Test
    public void battleStage() {
        Game g = new Game(2,24);
        ArrayList<ArrayList<Attack>> att = new ArrayList<>();
        for (int i=0;i<2;i++){
            ArrayList<Attack> a = new ArrayList<>();
            for (int j=0;j<5;j++){
                HashMap<UnitType,Integer> unitlist = new HashMap<>();
                unitlist.put(UnitType.GNOME,5);
                Attack at = new Attack(i+"from"+j,i+"to"+j,unitlist,"player"+i,House.GRYFFINDOR);
                a.add(at);
            }
            att.add(a);
        }
        g.printList(att,0,1,1,2);
        g.battleStage(att,new Territory("a"),0,1,1,2,UnitType.CENTAUR,UnitType.GNOME);
        g.battleStage(att,new Territory("a"),0,1,1,2,UnitType.CENTAUR,UnitType.GNOME);
    }

    @Test
    public void announceHuffSituation() {
        Game g =new Game(2,24);
        Territory t = new Territory("a");
        Player p = new Player(0,new Socket());
        p.setPlayerName("a");
        t.changePlayerOwner(p);
        g.announceHuffSituation(t);
    }


    @Test
    public void setNewHorcrux() {
        Game g = new Game(2,24);
        g.setNewHorcrux(Horcrux.CUP,0);
    }

    @Test
    public void setNoHorcrux() {
        Game g = new Game(2,24);
        g.setNoHorcrux();
    }

    @Test
    public void getNewHorcrux() {
        Game g = new Game(2,24);
        g.getNewHorcrux();
    }

    @Test
    public void getHorcruxAffect() {
        Game g = new Game(2,24);
        g.getHorcruxAffect();
    }

    @Test
    public void compareUnitHigh() {
        Game g = new Game(2,24);
        g.compareUnitHigh(UnitType.DWARF,UnitType.WEREWOLF);
        g.compareUnitHigh(UnitType.WEREWOLF,UnitType.DWARF);
    }

    @Test
    public void isForceEndGame() {
        Game g = new Game(2,24);
        assertFalse(g.isForceEndGame());
        g.forceEndGame();
        assertTrue(g.isForceEndGame());
    }

    @Test
    public void getLoserId() {
        Game g = new Game(2,24);
        g.getLoserId();
    }
}