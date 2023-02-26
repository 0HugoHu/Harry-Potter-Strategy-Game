package edu.duke.risc.client;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

import edu.duke.shared.Game;
import edu.duke.shared.thread.BaseThread;

public class Client {
    // Host name
    /*
     * TODO: Change the host name to your computer name
     */
    private final String HOST = "Assassin";
    // Port number
    private final int PORT = 5410;
    // Player name
    String name;
    // Gameplay controller
    private Game game;
    // Client socket
    private Socket client;

    /*
     * Initialize Client
     */
    public Client() {
        this("Test Player");
    }

    /*
     * Initialize Client by player name
     * @param playerName Player name
     */
    public Client(String playerName) {
        this.name = playerName;
        System.out.println("Created a player with name: " + this.name);
        this.game = new Game(1);
        try {
            this.client = new Socket(HOST, PORT);
        } catch (IOException e) {
            System.out.println("Failed to set up client socket.\n");
            e.printStackTrace();
        }
    }

    /*
     * Get player name
     * @return Player name
     */
    public String getName() {
        return this.name;
    }

    /*
     * Get game object
     * @return Game object
     */
    public Game getGame() {
        if (this.client == null) {
            System.out.println("Client socket is not set up.\n");
            return null;
        }
        BaseThread thread = new BaseThread(this.client);
        this.game = (Game) thread.decodeObj();
        return this.game;
    }

    /*
     * Close client socket
     * @return true if server socket is closed successfully, false otherwise
     */
    public boolean safeClose() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
