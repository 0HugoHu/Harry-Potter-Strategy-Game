package edu.duke.risc.client;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import edu.duke.shared.Game;
import edu.duke.shared.helper.DisplayMap;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.helper.State;
import edu.duke.shared.helper.Validation;
import edu.duke.shared.map.Territory;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Move;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

public class Client {
    // Host name
    //private String HOST = "vcm-30577.vm.duke.edu";
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
    private final Socket clientSocket;
    // Scanner
    Scanner scanner = new Scanner(System.in);
    // Flag for mock client
    private boolean isMock = false;

    // Flag for client who lost the game
    private boolean isLoser = false;

    /**
     * main method for the client
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Create new client
        Client client = new Client(args[0]);
        client.isMock = args[1].equals("mock");

        // Init client
        client.initClient(client.isMock);

        // Start game
        while (client.game.getGameState() != State.GAME_OVER) {
            if (client.isMock) {
                client.playOneTurnMock();
            } else {
                client.playOneTurn();
            }
        }

        // End Game
        System.out.println("Game End.\n");
        client.safeClose();
    }

    /*
     * Initialize Client
     */
    public Client(String HOST) {
        this(HOST, null);
    }

    /*
     * Initialize Client by player name
     * @param playerName Player name
     */
    public Client(String HOST, String playerName) {
        this.playerName = playerName;
        System.out.println("Created a player.\n");
        this.clientSocket = connectSocket(HOST, PORT);
        this.game = new Game(0, 48);
    }

    /*
     * Connect to server
     * @param HOST Host name
     * @param PORT Port number
     */
    public Socket connectSocket(String HOST, int PORT) {
        try {
            return new Socket(HOST, PORT);
        } catch (IOException e) {
            System.out.println("Failed to set up client socket. Retry connecting\n");
            //e.printStackTrace();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            return connectSocket(HOST, PORT);
        }
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

    /*
     * Initialize client
     */
    private void initClient(boolean isMock) {
        System.out.println("Currently waiting for other players...");
        // Wait until all players have joined the game
        waitForPlayers();

        // Send player name
        sendPlayerName(isMock);

        // Client receive game from the server
        Game currGame = getGame();
        this.game = currGame;

        // Read and set Id
        this.playerID = currGame.getPlayerId();
        System.out.println("Your game ID is: " + this.playerID + "\n");

        // Units initialization
        if (isMock) {
            setupUnitsMock();
        } else {
            //BuiltInSetUpUnits();
            setupUnits();
        }
        //setupUnits();

    }

    /*
     * Set up units
     */
    private void setupUnits() {
        DisplayMap displayMap = new DisplayMap(this.game, this.playerID);
        System.out.println(displayMap.showMap());
        System.out.println(displayMap.showUnits(true, null, null));

        System.out.println("Please set up your units. You have " + numUnits + " units in total.\n");
        int totalUnits = 0;
        HashSet<Territory> terrs = this.game.getPlayer(playerName).getPlayerTerrs();
        for (Territory t : terrs) this.game.getMap().getTerritory(t.getName()).removeAllUnits();
        while (totalUnits < numUnits) {
            int numRemainingUnits = Client.numUnits - totalUnits;
            System.out.println("Please enter the name of the territory you want to add units to:\n");
            String source = scanner.nextLine();
            System.out.println("Please enter the number of units you want to add(" + numRemainingUnits + " Remaining):\n");
            int numUnits = Validation.getValidNumber(scanner);
            try {
                Validation.checkUnit(this.game.getMap(), source, numUnits, Client.numUnits - totalUnits, this.playerName);
                totalUnits += numUnits;
                for (int i = 0; i < numUnits; i++)
                    this.game.getMap().getTerritory(source).addUnit(UnitType.GNOME);
            } catch (Exception e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
        System.out.println("Total units placed: " + totalUnits + ". You have placed exactly " + numUnits + " units.");
        GameObject obj = new GameObject(this.clientSocket);
        obj.encodeObj(this.game);
    }

//    public void BuiltInSetUpUnits() {
//        DisplayMap displayMap = new DisplayMap(this.game, this.playerID);
//        System.out.println(displayMap.showMap());
//        System.out.println(displayMap.showUnits(true, null, null));
//
//        HashSet<Territory> terrs = this.game.getPlayer(playerName).getPlayerTerrs();
//        for (Territory t : terrs) this.game.getMap().getTerritory(t.getName()).removeAllUnits();
//        for (Territory t : terrs) {
//            this.game.getMap().getTerritory(t.getName()).addUnit(UnitType.GNOME);
//            this.game.getMap().getTerritory(t.getName()).addUnit(UnitType.GNOME);
//            this.game.getMap().getTerritory(t.getName()).addUnit(UnitType.DWARF);
//            this.game.getMap().getTerritory(t.getName()).addUnit(UnitType.DWARF);
//            this.game.getMap().getTerritory(t.getName()).addUnit(UnitType.HOUSE_ELF);
//            this.game.getMap().getTerritory(t.getName()).addUnit(UnitType.HOUSE_ELF);
//        }
////        System.out.println("Please enter the name of the territory you want to add units to:\n");
////        String source = scanner.nextLine();
//        GameObject obj = new GameObject(this.clientSocket);
//        obj.encodeObj(this.game);
//
//    }

    /*
     * Set up units for mock
     */
    private void setupUnitsMock() {
        for (int i = 0; i < numUnits; i++)
            this.game.getMap().getTerritoriesByOwner(this.playerName).get(0).addUnit(new Unit("Normal"));
        GameObject obj = new GameObject(this.clientSocket);
        obj.encodeObj(this.game);
    }

    /*
     * Send player name to server
     */
    private void sendPlayerName(boolean isMock) {
        // Read player name
        if (isMock) {
            readPlayerNameMock();
        } else {
            readPlayerName();
        }
        this.game.setPlayerName(this.playerName);
        GameObject obj = new GameObject(this.clientSocket);
        obj.encodeObj(this.game);
    }

    /*
     * Read player name from console
     */
    private void readPlayerName() {
        // Reading data using readLine
        System.out.println("Please enter your player name:\n");
        this.playerName = scanner.nextLine();
        while (this.playerName == null || this.playerName.isEmpty()) {
            System.out.println("Player name cannot be empty. Please enter again:");
            this.playerName = scanner.nextLine();
        }
        System.out.println("Set player name to " + this.playerName + ".\nWaiting for other players...\n");
    }

    /*
     * Read player name for mock
     */
    private void readPlayerNameMock() {
        this.playerName = "Player" + (int) (Math.random() * 10000);
    }

    /*
     * Wait for other players to join the game
     */
    private void waitForPlayers() {
        // Client receive game from the server
        Game currGame = getGame();
        assert (currGame.getGameState() != State.READY_TO_INIT_NAME);
    }

    /*
     * Play one turn
     */
    private void playOneTurn() {
        // Client receive game from the server
        Game currGame = getGame();
        this.game = currGame;
        DisplayMap displayMap = new DisplayMap(currGame, this.playerID);

        System.out.println(displayMap.showMap());

        // Read instructions
        MoveTurn moveTurn = new MoveTurn(this.game.getMap(), this.game.getTurn(), this.playerName);
        AttackTurn attackTurn = new AttackTurn(this.game.getMap(), this.game.getTurn(), this.playerName);
        if (!this.isLoser) {
            String order;
            while (true) {
                System.out.println(displayMap.showUnits(false, moveTurn, attackTurn));
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
                        orderMove(moveTurn, attackTurn);
                        break;
                    case "A":
                        orderAttack2(attackTurn, moveTurn);
                        break;
                }
            }
        }

        // Done
        this.game.addToTurnMap(this.playerID, moveTurn, attackTurn);
        GameObject obj = new GameObject(this.clientSocket);
        obj.encodeObj(this.game);

        System.out.println("Waiting for other players...\n");

        // Receive turn result
        receiveTurnResult();
    }

    /*
     * Order move for mock
     */
    public void playOneTurnMock() {
        // Client receive game from the server
        this.game = getGame();

        // Read instructions
        MoveTurn moveTurn = new MoveTurn(this.game.getMap(), this.game.getTurn(), this.playerName);
        AttackTurn attackTurn = new AttackTurn(this.game.getMap(), this.game.getTurn(), this.playerName);

        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(5000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Done
        this.game.addToTurnMap(this.playerID, moveTurn, attackTurn);
        GameObject obj = new GameObject(this.clientSocket);
        obj.encodeObj(this.game);

        // Receive turn result
        receiveTurnResult();
    }

    /*
     * Receive turn result
     */
    private void receiveTurnResult() {
        // Client receive game from the server
        this.game = getGame();
        if (this.game.getGameState() == State.GAME_OVER) {
            String winner = this.game.getPlayerList().get(this.game.getWinnerId()).getPlayerName();
            System.out.println("Game over. The winner is " + winner + ".\n");
        } else if (this.game.isLoser(this.playerID)) {
            System.out.println("You have lost. Now you are watching the game.\n");
            isLoser = true;
        }
        // Confirm turn
        GameObject obj = new GameObject(this.clientSocket);
        obj.encodeObj(this.game);
    }

    /*
     * Order move from player's console
     */
    private void orderMove(MoveTurn moveTurn, AttackTurn attackTurn) {
        System.out.println("Please enter the name of the territory you want to move from:\n");
        String from = scanner.nextLine();
        System.out.println("Please enter the name of the territory you want to move to:\n");
        String to = scanner.nextLine();
        System.out.println("Please enter the number of units you want to move:\n");
        int numUnits = Validation.getValidNumber(scanner);
        try {
            Validation.checkMove(moveTurn, attackTurn, from, to, numUnits);
            moveTurn.addMove(new Move(from, to, numUnits, this.playerName));
        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
            while (true) {
                System.out.println("Please enter X to return to menu, or enter C to continue\n");
                String operation = scanner.nextLine();
                if (operation.equals("X")) return;
                if (operation.equals("C")) {
                    orderMove(moveTurn, attackTurn);
                    break;
                }
            }
        }
    }

    /*
     * Order attack from player's console
     */
//    private void orderAttack(AttackTurn attackTurn, MoveTurn moveTurn) {
//        System.out.println("Please enter the name of the territory you want to attack from:\n");
//        String from = scanner.nextLine();
//        System.out.println("Please enter the name of the territory you want to attack to:\n");
//        String to = scanner.nextLine();
//        System.out.println("Please enter the number of units you want to use in attack:\n");
//        int numUnits = Validation.getValidNumber(scanner);
//        try {
//            Validation.checkAttack(attackTurn, moveTurn, from, to, numUnits);
//            attackTurn.addAttack(new Attack(from, to, numUnits, this.playerName));
//        } catch (Exception e) {
//            System.out.println("Invalid input: " + e.getMessage());
//            while (true) {
//                System.out.println("Please enter X to return to menu, or enter C to continue\n");
//                String operation = scanner.nextLine();
//                if (operation.equals("X")) return;
//                if (operation.equals("C")) {
//                    orderAttack(attackTurn, moveTurn);
//                    break;
//                }
//            }
//        }
//    }


    /*
     * Order attack from player's console
     */
    private void orderAttack2(AttackTurn attackTurn, MoveTurn moveTurn) {
        System.out.println("Please enter the name of the territory you want to attack from:\n");
        String from = scanner.nextLine();
        System.out.println("Please enter the name of the territory you want to attack to:\n");
        String to = scanner.nextLine();
        System.out.println("Please enter the number of Gnomes you want to use in attack:\n");
        int GnomesNumUnits = Validation.getValidNumber(scanner);
        System.out.println("Please enter the number of Dwarfs you want to use in attack:\n");
        int DwarfsNumUnits = Validation.getValidNumber(scanner);
        System.out.println("Please enter the number of House-elf you want to use in attack:\n");
        int HouseElfNumUnits = Validation.getValidNumber(scanner);

        HashMap<UnitType, Integer> unitList = new HashMap<>();
        unitList.put(UnitType.GNOME, GnomesNumUnits);
        unitList.put(UnitType.DWARF, DwarfsNumUnits);
        unitList.put(UnitType.HOUSE_ELF, HouseElfNumUnits);
        attackTurn.addAttack(new Attack(from, to, unitList, this.playerName));
//        try {
//            //Validation.checkAttack(attackTurn, moveTurn, from, to, numUnits);
//            attackTurn.addAttack(new Attack(from, to, numUnits, this.playerName));
//        } catch (Exception e) {
//            System.out.println("Invalid input: " + e.getMessage());
//            while (true) {
//                System.out.println("Please enter X to return to menu, or enter C to continue\n");
//                String operation = scanner.nextLine();
//                if (operation.equals("X")) return;
//                if (operation.equals("C")) {
//                    orderAttack(attackTurn,moveTurn);
//                    break;
//                }
//            }
//        }

    }

}
