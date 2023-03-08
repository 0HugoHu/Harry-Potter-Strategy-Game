package edu.duke.server;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;

import edu.duke.shared.Game;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.player.Player;
import edu.duke.shared.map.Territory;
import edu.duke.shared.unit.Unit;

public class Server {
    // Port number
    private final static int PORT = 5410;
    // Number of players
    private final static int numPlayers = 3;
    // Number of units at the beginning
    private final static int numUnits = 24;
    // Gameplay controller
    private final Game game;
    // Server socket
    private ServerSocket serverSocket;
    // Logger
    private static final Logger logger = Logger.getLogger("serverLog.txt");

    /**
     * Main method
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        while (true) {
            // Create a new server
            Server server = new Server(numPlayers);
            server.printHostInfo();
            System.out.println("Created a new game of " + numPlayers + " players.\nWaiting for players to join...\n");

            // Accept connections from players
            server.acceptConnection(numPlayers);

            // Change game state to allow players to send their names
            System.out.println("All players have joined the game. Now waiting for their names...\n");
            server.game.setGameState(State.READY_TO_INIT_NAME);
            server.sendToAllPlayers();

            //TODO : Allocate territories
            server.receivePlayerName();
            System.out.println("Received all names info.\n");
            server.allocateTerritories();

            // Send message to all players
            server.game.setGameState(State.READY_TO_INIT_UNITS);
            server.sendToAllPlayers();
            System.out.println("Message sent to all players.\n");

            // Receive Units setup from all players
            server.receiveUnitsInfo();
            System.out.println("Received all units info.\n");

            // Start Game
            server.startOneTurn();
            System.out.println("Starts turn.\n");

            // Close server socket
            server.safeClose();
            System.out.println("Server shut down.\n");
        }
    }

    private void printHostInfo() {
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
    }

    /**
     * Initialize Server by number of players
     *
     * @param numOfPlayers Number of players
     */
    public Server(int numOfPlayers) {
        this.game = new Game(numOfPlayers);
        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("New Game Created.\n");
    }

    /**
     * wait for connection from all users
     *
     * @param num number of players
     */
    public void acceptConnection(int num) {
        // Wait until all players have joined the game
        for (int i = 0; i < num; i++) {
            try {
                // Accept connection from client
                Socket socket = this.serverSocket.accept();
                // Add player to the game
                // i means player_id
                this.game.addPlayer(new Player(i, socket));
                //thread join in
                this.game.getPlayerList().get(i).threadJoin();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Player " + i + " has joined the game.");
        }
    }


    /**
     * Send message to all players
     *
     */
    public void sendToAllPlayers() {
        for (int i = 0; i < this.getNumOfPlayers(); i++) {
            Socket clientSocket = this.game.getPlayerList().get(i).getSocket();
            GameObject obj = new GameObject(clientSocket);
            // Encode player specific Id to client
            this.game.setPlayerId(i);
            obj.encodeObj(this.game);
        }
    }

    /**
     * Close server socket
     *
     */
    public void safeClose() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get number of players
     *
     * @return Number of players
     */
    public int getNumOfPlayers() {
        return this.game.getNumPlayers();
    }


    /**
     * Receive units placement from client
     *
     */
    private void receiveUnitsInfo() {
        for (int i = 0; i < this.getNumOfPlayers(); i++) {
            Player p = this.game.getPlayerList().get(i);
            GameObject obj = new GameObject(p.getSocket());
            Game clientGame = (Game) obj.decodeObj();
            HashSet<Territory> terr_set = p.getPlayerTerrs();
            int totalUnits = 0;
            for (Territory t : terr_set) {
                this.game.getMap().getTerritory(t.getName()).removeAllUnits();
                for (int j = 0; j < clientGame.getMap().getTerritory(t.getName()).getNumUnits(); j++)
                    this.game.getMap().getTerritory(t.getName()).addUnit(new Unit("Normal"));
                totalUnits += t.getNumUnits();
            }
            if (totalUnits != numUnits) {
                System.out.println("Fatal Error: Player " + i + " has placed " + totalUnits + " units, which is not equal to " + numUnits + ".\n");
                // TODO: Handle error
            }
            System.out.println("Received player " + i + "'s unit init info.");
        }
    }

    private void startOneTurn() {
        sendToAllPlayers();
    }

    public void allocateTerritories() {
        GameMap gameMap = this.game.getMap();
        int numTerrs = gameMap.getNumTerritories();
        int numPlayers = this.game.getNumPlayers();
        ArrayList<Territory> terrs = gameMap.getTerritories();
        ArrayList<Player> players = game.getPlayerList();
        for (int i = 0; i < numTerrs; i++) {
            players.get(i / (numTerrs / numPlayers)).expandTerr(terrs.get(i));
            terrs.get(i).changePlayerOwner(players.get(i / (numTerrs / numPlayers)));
            terrs.get(i).changeOwner(players.get(i / (numTerrs / numPlayers)).getPlayerName());
        }
    }

    public void receivePlayerName() {
        for (int i = 0; i < getNumOfPlayers(); i++) {
            Player p = this.game.getPlayerList().get(i);
            GameObject obj = new GameObject(p.getSocket());
            Game currGame = (Game) obj.decodeObj();
            p.setPlayerName(currGame.getPlayerName());
            System.out.println("Received player " + i + "'s name: " + p.getPlayerName());
        }
    }

}