package edu.duke.server;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.duke.shared.Game;
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
            Server server = new Server(numPlayers, numUnits);
            server.initServer();

            // Start Game
            while (server.game.getGameState() != State.GAME_OVER) {
                server.startOneTurn();
            }

            // Close server socket
            server.safeClose();
            System.out.println("Server shut down.\n");
        }
    }

    /**
     * Print host info
     */
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
     * Initialize server
     */
    private void initServer() {
        // Create a new server
        printHostInfo();
        System.out.println("Created a new game of " + numPlayers + " players.\nWaiting for players to join...\n");

        // Accept connections from players
        acceptConnection(numPlayers);

        // Change game state to allow players to send their names
        System.out.println("All players have joined the game. Now waiting for their names...\n");
        this.game.setGameState(State.READY_TO_INIT_NAME);
        sendToAllPlayers();

        // Receive names from all players
        waitForThreadJoin();
        System.out.println("Received all names info.\n");
        allocateTerritories();

        // Send message to all players
        this.game.setGameState(State.READY_TO_INIT_UNITS);
        sendToAllPlayers();
        System.out.println("Message sent to all players.\n");

        // Receive Units setup from all players
        waitForThreadJoin();
        System.out.println("Received all units info.\n");
    }

    /**
     * Initialize Server by number of players
     *
     * @param numOfPlayers Number of players
     * @param numUnits     Number of units for each player
     */
    public Server(int numOfPlayers, int numUnits) {
        this.game = new Game(numOfPlayers, numUnits);
        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("New Game Created.\n");
    }

    /**
     * Wait until all threads are finished
     */
    private void waitForThreadJoin() {
        for (int i = 0; i < this.getNumOfPlayers(); i++) {
            this.game.getPlayerList().get(i).threadJoin();
        }
    }

    /**
     * Wait for connection from all users
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
                System.out.println("Player " + i + " has joined the game.");
            } catch (IOException e) {
                e.printStackTrace();
                i--;
            }
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


    /**
     * Start one turn
     */
    private void startOneTurn() {
        this.game.setGameState(State.TURN_BEGIN);
        sendToAllPlayers();
        System.out.println("Start turn " + this.game.getTurn() + ".\n");

        // Receive action list from all players
        waitForThreadJoin();
        System.out.println("Received all action lists.\n");

        // Do attack
        doAttackPhase();
        if (this.game.getGameState() == State.GAME_OVER) {
            return;
        }
        growUnits();
        growResources();
        this.game.setGameState(State.TURN_END);
        sendToAllPlayers();

        // Turn end
        waitForThreadJoin();
        System.out.println("End turn " + this.game.getTurn() + ".\n");
        this.game.turnComplete();

        // Sleep for a while or two objects will be sent at the same time
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Allocate territories to players
     */
    public void allocateTerritories() {
        this.game.allocateTerritories();
        /*
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
        */
    }

    /**
     * Do attack phase
     */
    public void doAttackPhase() {
        // Make Attack List
        int turnIndex = this.game.getTurn();
        for (Map.Entry<Integer, ArrayList<Turn>> entry : this.game.getTurnList().get(turnIndex).entrySet()) {
            this.game.makeAttackList((AttackTurn) entry.getValue().get(1));
        }
        this.game.doAttack();
        //this.game.printUnit();

        // Check if game is over
        for (Player p : this.game.getPlayerList()) {
            int numTerrs = this.game.getMap().getTerritoriesByOwner(p.getPlayerName()).size();
            // If one player has all territories, game over
            if (numTerrs == this.game.getMap().getNumTerritories()) {
                this.game.setGameState(State.GAME_OVER);
                this.game.setWinnerId(p.getPlayerId());
                System.out.println("Game Over. Player " + p.getPlayerName() + " wins.\n");
                sendToAllPlayers();
            }
            if (numTerrs == 0) {
                this.game.addLoserId(p.getPlayerId());
                System.out.println("Player " + p.getPlayerName() + " has lost the game. Game continues.\n");
            }
        }
    }

    /**
     * Grow unit of each territory by 1
     */
    public void growUnits() {
        //After each turn, all territories should add one new unit
        for (Territory t : this.game.getMap().getTerritories()) {
            t.addUnit(new Unit("Gnome"));
        }
    }

    public void growResources() {
        //After each turn, all territories should add one new unit
        for (Territory t : this.game.getMap().getTerritories()) {
            if(t.checkUnicornLand()){
                t.addHorns(70);
            }
            if(t.checkNifflerLand()){
                t.addCoins(50);
            }
        }
    }

}