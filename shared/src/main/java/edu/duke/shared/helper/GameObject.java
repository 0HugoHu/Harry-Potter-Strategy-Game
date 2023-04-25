package edu.duke.shared.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import edu.duke.shared.Game;

public class GameObject {
    // Gson object
    private final Gson gson;
    // The client socket
    public Socket socket;

    /**
     * Constructor for the GameObject
     *
     * @param socket The client socket
     */
    public GameObject(Socket socket) {
        this.socket = socket;
        this.gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .create();
    }

    /**
     * Get socket
     */
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * Set socket
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Decode the object from the client (which is what is received from player)
     *
     * @return The decoded object from the client
     */
    public Game decodeObj() {
        try {
            // Get the input stream from the client
            InputStream socketStream = this.socket.getInputStream();
            // Wrap the input stream with a buffered stream
            BufferedInputStream socketBufferedStream = new BufferedInputStream(socketStream);
            // Wrap the buffered stream with an object stream
            ObjectInputStream ObjStream = new ObjectInputStream(socketBufferedStream);
            // Read the object from the object stream
            return (Game) ObjStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encode the object to the client(which is what sent out to player)
     *
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
