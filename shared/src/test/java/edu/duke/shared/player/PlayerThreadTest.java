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
//        PlayerThread playerThread = new PlayerThread(State.READY_TO_INIT_NAME, new Socket(), 0);
//        playerThread.socket = mock(Socket.class);
//        Player player = new Player(0, new Socket());
//        player.setPlayerName("test");
//
//        Game game = new Game(3);
//        game.addPlayer(player);
//        game.setPlayerName("test");
//        playerThread.setServerGame(game);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(baos);
//        oos.writeObject(game);
//        oos.flush();
//        oos.close();
//        InputStream is = new ByteArrayInputStream(baos.toByteArray());
//
//        when(playerThread.socket.getInputStream()).thenReturn(is);
//        player.start(State.READY_TO_INIT_NAME);
    }

    @Test
    public void testInitUnit() throws IOException {
        // UNITS
//        Socket socket = mock(Socket.class);
//        Player player = new Player(0, socket);
//        player.setPlayerName("test");
//
//        Territory t = new Territory("Terr0");
//        t.addUnit(new Unit("Normal"));
//        t.changeOwner(player.getPlayerName());
//        player.expandTerr(t);
//        Game game = new Game(3);
//        game.addPlayer(player);
//        game.getMap().addTerritory(t);
//        game.setPlayerName("test");
//        player.getPlayerThread().setServerGame(game);
//
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(baos);
//        oos.writeObject(game);
//        oos.flush();
//        oos.close();
//        InputStream is = new ByteArrayInputStream(baos.toByteArray());
//
//        when(player.getPlayerThread().socket.getInputStream()).thenReturn(is);
//        player.start(State.READY_TO_INIT_UNITS);
    }


}