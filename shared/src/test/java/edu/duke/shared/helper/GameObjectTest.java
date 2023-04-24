package edu.duke.shared.helper;

import static org.junit.Assert.assertNull;
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

public class GameObjectTest {

    @Test
    public void decodeObj() throws IOException {
        GameObject gameObject = new GameObject(mock(Socket.class));
        assertNull(gameObject.decodeObj());

        Game game = new Game(3, 24);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(game);
        oos.flush();
        oos.close();
        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        when(gameObject.socket.getInputStream()).thenReturn(is);
        gameObject.decodeObj();
    }

    @Test
    public void encodeObj() throws IOException {
        GameObject gameObject = new GameObject(mock(Socket.class));
        gameObject.encodeObj(new Object());

        Game game = new Game(3, 24);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(game);
        oos.flush();
        oos.close();

        when(gameObject.socket.getOutputStream()).thenReturn(baos);
        gameObject.encodeObj(game);
    }
}