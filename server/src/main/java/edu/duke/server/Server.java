package edu.duke.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;

import edu.duke.shared.Game;
import edu.duke.shared.helper.DisplayMap;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.player.Player;
import edu.duke.shared.map.Territory;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Turn;
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

            // Receive names from all players
            server.waitForThreadJoin();
            System.out.println("Received all names info.\n");
            server.allocateTerritories();

            // Send message to all players
            server.game.setGameState(State.READY_TO_INIT_UNITS);
            server.sendToAllPlayers();
            System.out.println("Message sent to all players.\n");

            // Receive Units setup from all players
            server.waitForThreadJoin();
            System.out.println("Received all units info.\n");

            // Start Game
            server.game.setGameState(State.TURN_BEGIN);
            server.startOneTurn();
            // TODO: XUEYI move all below into startOneTurn() and continue play one turn until game ends
            /* **************Move this****************/
            System.out.println("Starts turn.\n");

            // Receive action list from all players
            server.waitForThreadJoin();
            System.out.println("Received all action lists.\n");
            // Do attack
            server.doAttackPhase();
            server.game.setGameState(State.TURN_END);
            server.sendToAllPlayers();
            server.game.turnComplete();
            /* **************Move this****************/

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
            System.out.println("------------------------------------");
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

    private void waitForThreadJoin() {
        for (int i = 0; i < this.getNumOfPlayers(); i++) {
            this.game.getPlayerList().get(i).threadJoin();
        }
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
                // Add player to the game, i means player_id
                // Create an object, and a thread is started
                this.game.addPlayer(new Player(i, socket));
            } catch (IOException e) {
                // TODO: WUYU Handle this
                e.printStackTrace();
            }
            System.out.println("Player " + i + " has joined the game.");
        }
        waitForThreadJoin();
    }


    /**
     * Send message to all players
     */
    public void sendToAllPlayers() {
        for (int i = 0; i < this.getNumOfPlayers(); i++) {
            // Start thread for each player
            this.game.getPlayerList().get(i).start(this.game.getGameState());
            // Also send server game to player thread
            this.game.getPlayerList().get(i).getPlayerThread().setServerGame(this.game);

            // Send message to client
            Socket clientSocket = this.game.getPlayerList().get(i).getSocket();
            GameObject obj = new GameObject(clientSocket);
            // Encode player specific Id to client
            this.game.setPlayerId(i);
            obj.encodeObj(this.game);
        }
    }

    /**
     * Close server socket
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


    private void startOneTurn() {
        sendToAllPlayers();
    }

    public void startAttack(){
        waitForThreadJoin();
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

    public void doAttackPhase() {
        HashMap<Integer, ArrayList<Turn>> turnMap=this.game.getTurnMap();
        for (int i = 0; i < getNumOfPlayers(); i++) {
            Player p = this.game.getPlayerList().get(i);
            Game currGame = p.getPlayerThread().getCurrGame();
            HashMap<Integer, ArrayList<Turn>> playerTurnMap = currGame.getTurnMap();
            turnMap.putAll(playerTurnMap);
        }
        this.game.makeTurns();
        this.game.printUnit();
    }

}