package edu.duke.risc.client;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;

import edu.duke.shared.Game;
import edu.duke.shared.helper.DisplayMap;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.Territory;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Move;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.unit.Unit;

public class Client {
    // Host name
//    private String HOST = "vcm-30577.vm.duke.edu";
    private final static String HOST = "xueyideMacBook-Air.local";
    // Port number
    private final static int PORT = 5410;
    // Number of units at the beginning
    private final static int numUnits = 24;
    // Player Id on server
    private int playerID;
    // Player name
    private String playerName;
    // Gameplay controller
    private Game game;
    // Client socket
    private Socket clientSocket;
    // Scanner
    Scanner scanner = new Scanner(System.in);

    /*
     * Initialize Client
     */
    public Client() {
        this(null);
    }

    /*
     * Initialize Client by player name
     * @param playerName Player name
     */
    public Client(String playerName) {
        this.playerName = playerName;
        System.out.println("Created a player.\n");
        try {
            this.clientSocket = new Socket(HOST, PORT);
        } catch (IOException e) {
            System.out.println("Failed to set up client socket.\n");
            e.printStackTrace();
        }
        this.game = new Game(0);
    }

    /**
     * main method for the client
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException {
        // Create new client
        Client client = new Client();
        System.out.println("Currently waiting for other players.....");

        // Wait until all players have joined the game
        client.waitForPlayers();

        // Initialize game
        client.initGame();

        // Start game
        client.playOneTurn();

        // Receive turn result
        client.receiveTurnResult();

        // End Game
        System.out.println("End Game");
        client.safeClose();
    }


    /*
     * Get player name
     * @return Player name
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /*
     * Get game object
     * @return Game object
     */
    public Game getGame() {
        if (this.clientSocket == null) {
            System.out.println("Client socket is not set up.\n");
            return null;
        }
        GameObject obj = new GameObject(this.clientSocket);
        this.game = (Game) obj.decodeObj();
        return this.game;
    }


    /*
     * Close client socket
     * @return true if server socket is closed successfully, false otherwise
     */
    public void safeClose() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupUnits() {
        System.out.println("Please set up your units.\n");
        int totalUnits = 0;
        HashSet<Territory> terrs = this.game.getPlayer(playerName).getPlayerTerrs();
        // TODO: Replace with UnitCheck
        while (totalUnits != numUnits) {
            if (totalUnits != 0)
                System.out.println("Total units placed: " + totalUnits + ". But you must place exactly " + numUnits + " units.");
            totalUnits = 0;
            for (Territory t : terrs) {
                System.out.println("Please enter the number of units you want to place on territory " + t.getName() + ":");
                int numUnits = scanner.nextInt();
                this.game.getMap().getTerritory(t.getName()).removeAllUnits();
                totalUnits += numUnits;
                for (int i = 0; i < numUnits; i++)
                    this.game.getMap().getTerritory(t.getName()).addUnit(new Unit("Normal"));
            }
        }
        System.out.println("Total units placed: " + totalUnits + ". You have placed exactly " + numUnits + " units.");
        GameObject obj = new GameObject(this.clientSocket);
        obj.encodeObj(this.game);
    }

    private void sendPlayerName() {
        // Read player name
        readPlayerName();
        this.game.setPlayerName(this.playerName);
        GameObject obj = new GameObject(this.clientSocket);
        obj.encodeObj(this.game);
    }

    // Read from command line
    private void readPlayerName() {
        // Reading data using readLine
        System.out.println("Please enter your player name:\n");
        this.playerName = scanner.nextLine();
        while (this.playerName == null || this.playerName.isEmpty()) {
            System.out.println("Player name cannot be empty. Please enter again:");
            this.playerName = scanner.nextLine();
        }
    }

    private void waitForPlayers() {
        // Client receive game from the server
        Game currGame = getGame();
        if (currGame.getGameState() != State.READY_TO_INIT_NAME) {
            // TODO: throw exception
        }
    }

    private void initGame() {
        // Send player name
        sendPlayerName();

        // Client receive game from the server
        Game currGame = getGame();

        // Read and set Id
        this.playerID = currGame.getPlayerId();
        System.out.println("ID: " + this.playerID);

        // Units initialization
        setupUnits();
    }

    private void playOneTurn() {
        // Client receive game from the server
        Game currGame = getGame();
        DisplayMap displayMap = new DisplayMap(currGame, this.playerID);
        this.game = currGame;
        System.out.println(displayMap.showMap());

        // Read instructions
        MoveTurn moveTurn = new MoveTurn(this.game.getMap(), this.game.getTurn(), this.playerName);
        AttackTurn attackTurn=new AttackTurn(this.game.getMap(), this.game.getTurn(), this.playerName);
        String order;
        while (true) {
            System.out.println(displayMap.showUnits());
            order = scanner.nextLine();
            if (order.equals("D"))
                break;
            while (!(order.equals("M") || order.equals("A") || order.equals("D"))) {
                System.out.println("Please enter M to move, A to attack, D to done:\n");
                order = scanner.nextLine();
            }
            if (order.equals("D"))
                break;
            switch (order) {
                case "M":
                    orderMove(moveTurn);
                    break;
                case "A":
                    orderAttack(attackTurn);
                    break;
            }
        }
        this.game.addTurn(this.game.getPlayer(this.playerName).getPlayerId(),moveTurn);
        this.game.addTurn(this.game.getPlayer(this.playerName).getPlayerId(),attackTurn);
        //attackTurn.mergeAttacks();
        // Done
        //moveTurn.doMovePhrase();
        GameObject obj = new GameObject(this.clientSocket);
        obj.encodeObj(this.game);
    }

    private void receiveTurnResult() {
        // Client receive game from the server
        Game currGame = getGame();
        //DisplayMap displayMap = new DisplayMap(currGame, this.playerID);
        this.game = currGame;
        String output=this.game.getString();
        System.out.println(output);
        //System.out.println(displayMap.showUnits());
    }

    private void orderMove(MoveTurn moveTurn) {
        // TODO: Input check
        System.out.println("Please enter the name of the territory you want to move from:\n");
        String from = scanner.nextLine();
        System.out.println("Please enter the name of the territory you want to move to:\n");
        String to = scanner.nextLine();
        System.out.println("Please enter the number of units you want to move:\n");
        int numUnits = scanner.nextInt();

        moveTurn.addMove(new Move(from, to, numUnits));

    }

    private void orderAttack(AttackTurn attackTurn) {
        System.out.println("Please enter the name of the territory you want to attack from:\n");
        String from = scanner.nextLine();
        System.out.println("Please enter the name of the territory you want to attack to:\n");
        String to = scanner.nextLine();
        System.out.println("Please enter the number of units you want to use in attack:\n");
        int numUnits = scanner.nextInt();

        attackTurn.addAttack(new Attack(from, to, numUnits,this.game.getPlayer(this.playerName)));
    }

}
