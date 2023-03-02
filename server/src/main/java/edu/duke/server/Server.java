package edu.duke.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.duke.shared.Game;
import edu.duke.shared.thread.BaseThread;
import edu.duke.shared.thread.PlayerThread;

public class Server {
    // Port number
    private final int PORT = 5410;
    // Gameplay controller
    private final Game game;
    // Server socket
    private ServerSocket server;

    // ONLY For testing
    private Socket socketForTesting;

    /**
     * Main method
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Get IP address and hostname
        InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            System.out.println("Your current IP address : " + ip);
            System.out.println("Your current Hostname : " + hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //TODO : user decide how many players
        //
        //
        //change the number of players here
        final int numOfPlayers = 3;
        Server server = new Server(numOfPlayers);
        System.out.println("Created a new game of " + numOfPlayers + " players.\nWaiting for players to join...\n");

        // Accept connections from players
        if (!server.acceptConnection(numOfPlayers)) {
            System.out.println("Failed to accept connections from players.\n");
            return;
        }

        // Send message to all players
        if (server.sendToAllPlayers()) {
            System.out.println("Message sent to all players.\n");
        } else {
            System.out.println("Failed to send to all players.\n");
        }

        // Close server socket
        if (server.safeClose()) {
            System.out.println("Server shut down.\n");
        } else {
            System.out.println("Failed to shut down server.\n");
        }
    }

    /**
     * Initialize Server by number of players
     * @param numOfPlayers Number of players
     */
    public Server(int numOfPlayers) {
        this.game = new Game(numOfPlayers);
        // Listen to the port
        try {
            this.server = new ServerSocket(this.PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * wait for connection from all users
     * @param num
     * @return
     */
    private boolean acceptConnection(int num) {
        // Wait until all players have joined the game
        for (int i = 0; i < num; i++) {
            try {
                // Accept connection from client
                Socket socket = this.server.accept();
                // Create a new thread for each player with index
                  PlayerThread newPlayerThread = new PlayerThread(socket, this.game, i);
                // Start the thread
                 Thread thread = new Thread(newPlayerThread);
                 thread.start();
                 //thread join in
                  thread.join();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    /**
     * Send message to all players
     * @return true if message is sent successfully, false otherwise
     */
    private boolean sendToAllPlayers() {
        for (int i = 0; i < this.getNumOfPlayers(); i++) {
            Socket player_socket=this.game.getPlayerList().get(i).getSocket();
            BaseThread thread = new BaseThread(player_socket, this.game);
            thread.encodeObj(this.game);
        }
        return true;
    }

    /**
     * Close server socket
     * @return true if server socket is closed successfully, false otherwise
     */
    public boolean safeClose() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Get number of players
     * @return Number of players
     */
    public int getNumOfPlayers() {
        return this.game.getNumPlayers();
    }

}