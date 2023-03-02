package edu.duke.risc.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

import edu.duke.risc.R;
import edu.duke.shared.Game;
import edu.duke.shared.thread.BaseThread;

public class Client {
    // Host name
    private String HOST = "vcm-30577.vm.duke.edu";
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
    public Client() throws IOException {
        this("Test Player");
    }

    /*
     * Initialize Client by player name
     * @param playerName Player name
     */
    public Client(String playerName) throws IOException {
        this.name = playerName;
        System.out.println("Created a player with name: " + this.name);
        this.game = new Game(1);
        // TODO: Read from local file
//        this.HOST = readHost();
        try {
            this.client = new Socket(HOST, PORT);
        } catch (IOException e) {
            System.out.println("Failed to set up client socket.\n");
            e.printStackTrace();
        }
    }

    private String readHost() throws IOException {
        // Get host name from assets/keys.txt
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        File file = new File("../assets/keys.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        return br.readLine();
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
    private boolean safeClose() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
