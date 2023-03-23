package edu.duke.shared.player;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.duke.shared.Game;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Move;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.unit.Unit;

public class PlayerThreadTest {

    @Test
    public void run() {
        PlayerThread playerThread = new PlayerThread(State.WAITING_TO_JOIN, new Socket(), 0);
        playerThread.setServerGame(null);
        assertNull(playerThread.getCurrGame());
        playerThread.run();

        playerThread = new PlayerThread(State.GAME_OVER, new Socket(), 0);
        playerThread.run();

        playerThread = new PlayerThread(State.TURN_END, new Socket(), 0);
        playerThread.run();
    }

    @Test
    public void testInitName() throws IOException {
        // NAME
        PlayerThread playerThread = new PlayerThread(State.READY_TO_INIT_NAME, new Socket(), 0);
        playerThread.socket = mock(Socket.class);
        Player player = new Player(0, new Socket());
        player.setPlayerName("test");

        Game game = new Game(3, 3);
        game.addPlayer(player);
        game.setPlayerName("test");
        playerThread.setServerGame(game);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(game);
        oos.flush();
        oos.close();
        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        when(playerThread.socket.getInputStream()).thenReturn(is);
        player.start(State.READY_TO_INIT_NAME);
    }

    @Test
    public void testInitName2() {
        // NAME

    }

    @Test
    public void testInitUnit() throws InterruptedException {
        // UNITS
        PlayerThread playerThread = new PlayerThread(State.READY_TO_INIT_NAME, new Socket(), 0);
        Game game = new Game(3, 3);
        game.addPlayer(new Player(0, new Socket()));
        game.setPlayerName("test");
        playerThread.setServerGame(game);
        playerThread.currGame = game;
        playerThread.forTesting = 1;
        Thread thread = new Thread(playerThread);
        thread.start();
        thread.join();

        playerThread = new PlayerThread(State.TURN_END, new Socket(), 0);
        game = new Game(3, 3);
        game.addPlayer(new Player(0, new Socket()));
        game.setPlayerName("test");
        playerThread.setServerGame(game);
        playerThread.currGame = game;
        playerThread.forTesting = 1;
        thread = new Thread(playerThread);
        thread.start();
        thread.join();

        playerThread = new PlayerThread(State.READY_TO_INIT_UNITS, new Socket(), 0);
        game = new Game(3, 3);
        Player player = new Player(0, new Socket());
        player.playerThread = playerThread;
        Territory terr = new Territory("terr1");
        terr.changeOwner("test");
        terr.addUnit(new Unit("Normal"));
        player.expandTerr(terr);
        game.addPlayer(player);
        game.setPlayerName("test");
        game.getMap().addTerritory(terr);
        playerThread.setServerGame(game);
        playerThread.currGame = game;
        playerThread.forTesting = 1;
        thread = new Thread(playerThread);
        thread.start();
        thread.join();

        playerThread = new PlayerThread(State.TURN_BEGIN, new Socket(), 0);
        game = new Game(3, 3);
        player = new Player(0, new Socket());
        player.playerThread = playerThread;
        player.setPlayerName("test");
        terr = new Territory("terr1");
        terr.changeOwner("test");
        for (int i = 0; i < 3; i++) {
            terr.addUnit(new Unit("Normal"));
        }
        player.expandTerr(terr);
        Territory terr2 = new Territory("terr2");
        terr2.changeOwner("test");
        Territory terr3 = new Territory("terr3");
        terr3.changeOwner("test2");
        player.expandTerr(terr2);
        player.expandTerr(terr3);
        terr.addAdjacent("terr2");
        terr.addAdjacent("terr3");
        terr2.addAdjacent("terr1");
        terr3.addAdjacent("terr1");
        game.addPlayer(player);
        game.setPlayerName("test");
        game.getMap().addTerritory(terr);
        game.getMap().addTerritory(terr2);
        game.getMap().addTerritory(terr3);
        MoveTurn moveTurn = new MoveTurn(game.getMap(), 1, "terr1");
        Move m = new Move("terr1", "terr2", 2, "test");
        moveTurn.addMove(m);
        AttackTurn attackTurn = new AttackTurn(game.getMap(), 1, "terr1");
        Attack m2 = new Attack("terr1", "terr3", 1, "test");
        attackTurn.addAttack(m2);
        game.addToTurnMap(0, moveTurn, attackTurn);
        playerThread.setServerGame(game);
        playerThread.currGame = game;
        playerThread.forTesting = 1;
        thread = new Thread(playerThread);
        thread.start();
        thread.join();

        playerThread = new PlayerThread(State.TURN_BEGIN, new Socket(), 0);
        game = new Game(3, 3);
        player = new Player(0, new Socket());
        player.playerThread = playerThread;
        player.setPlayerName("test");
        terr = new Territory("terr1");
        terr.changeOwner("test");
        for (int i = 0; i < 3; i++) {
            terr.addUnit(new Unit("Normal"));
        }
        player.expandTerr(terr);
        terr2 = new Territory("terr2");
        terr2.changeOwner("test");
        terr3 = new Territory("terr3");
        terr3.changeOwner("test2");
        player.expandTerr(terr2);
        player.expandTerr(terr3);
        terr.addAdjacent("terr2");
        terr.addAdjacent("terr3");
        terr2.addAdjacent("terr1");
        terr3.addAdjacent("terr1");
        game.addPlayer(player);
        game.setPlayerName("test");
        game.getMap().addTerritory(terr);
        game.getMap().addTerritory(terr2);
        game.getMap().addTerritory(terr3);
        moveTurn = new MoveTurn(game.getMap(), 1, "terr1");
        m = new Move("terr1", "terr2", 2, "test");
        moveTurn.addMove(m);
        attackTurn = new AttackTurn(game.getMap(), 1, "terr1");
        m2 = new Attack("terr1", "terr3", 1, "test");
        attackTurn.addAttack(m2);
        game.addToTurnMap(0, moveTurn, attackTurn);
        playerThread.setServerGame(game);
        playerThread.currGame = game;
        playerThread.forTesting = 2;
        thread = new Thread(playerThread);
        thread.start();
        thread.join();
    }


}