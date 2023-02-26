package edu.duke.shared.thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import edu.duke.shared.Game;

public class BaseThread {
    // The game object
    protected final Game game;
    // The client socket
    protected final Socket socket;

    /*
     * Initialize BaseThread by socket and game
     * @param socket The client socket
     * @param game The game object
     */
    public BaseThread(Socket socket, Game game) {
        this.socket = socket;
        this.game = game;
    }

    /*
     * Initialize BaseThread by socket
     * @param socket The client socket
     */
    public BaseThread(Socket socket) {
        this(socket, null);
    }

    /*
     * Decode the object from the client
     * @return The decoded object from the client
     */
    public Object decodeObj() {
        try {
            // Get the input stream from the client
            InputStream socketStream = this.socket.getInputStream();
            // Wrap the input stream with a buffered stream
            BufferedInputStream socketBufferedStream = new BufferedInputStream(socketStream);
            // Wrap the buffered stream with an object stream
            ObjectInputStream ObjStream = new ObjectInputStream(socketBufferedStream);
            // Read the object from the object stream
            return ObjStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Encode the object to the client
     * @param obj The object to be encoded
     */
    public void encodeObj(Object obj) {
        try {
            // Get the output stream from the client
            OutputStream socketStream = this.socket.getOutputStream();
            // Wrap the output stream with a buffered stream
            BufferedOutputStream socketBufferedStream = new BufferedOutputStream(socketStream);
            // Wrap the buffered stream with an object stream
            ObjectOutputStream ObjStream = new ObjectOutputStream(socketBufferedStream);
            // Write the object to the object stream
            ObjStream.writeObject(obj);
            // Flush the object stream
            ObjStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
