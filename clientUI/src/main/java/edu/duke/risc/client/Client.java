package edu.duke.risc.client;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
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

public class Client {
    // Host name
    //private String HOST = "vcm-30577.vm.duke.edu";
    //
    private final String HOST;
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

    // Flag for client who lost the game
    private boolean isLoser = false;

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
        this.HOST = HOST;
        this.playerName = playerName;
        System.out.println("Created a player.\n");
        this.clientSocket = connectSocket(HOST, PORT);
        this.game = new Game(0, 24);
    }

    public Socket connectSocket(String HOST, int PORT) {
        try {
            return new Socket(HOST, PORT);
        } catch (IOException e) {
            System.out.println("Failed to set up client socket. Retry connecting\n");
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

    public int getPlayerId() {
        return this.playerID;
    }

    /*
     * Get game object
     */
    public Game accessGame() {
        return this.game;
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
        this.game.setPlayerName(this.playerName);
        this.game.setPlayerId(this.playerID);
        return this.game;
    }

    /*
     * Send game object
     * @param game Game object
     */
    public void setGame(Game game) {
        this.game = game;
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
    public void initClient(boolean isMock) {
        System.out.println("Currently waiting for other players...");
        // Wait until all players have joined the game
        waitForPlayers();

        // TODO: always mock
        // Send player name
        sendPlayerName(true);
    }


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

    private void readPlayerNameMock() {
        this.playerName = "Player" + (int) (Math.random() * 10000);
    }

    /*
     * Wait for other players to join the game
     */
    private void waitForPlayers() {
        // Client receive game from the server
        Game currGame = getGame();
        assert (currGame.getGameState().equals(State.READY_TO_INIT_NAME));
    }

    /*
     * Play one turn
     */
    public void playOneTurn() {
        GameObject obj = new GameObject(this.clientSocket);
        obj.encodeObj(this.game);
        System.out.println("Game objct sent to server.\n");
    }


    public void playOneTurnMock() {
        // Read instructions
        MoveTurn moveTurn = new MoveTurn(this.game.getMap(), this.game.getTurn(), this.playerName);
        AttackTurn attackTurn = new AttackTurn(this.game.getMap(), this.game.getTurn(), this.playerName);

        // Done
        this.game.addToTurnMap(this.playerID, moveTurn, attackTurn);
        GameObject obj = new GameObject(this.clientSocket);
        obj.encodeObj(this.game);
    }

    /*
     * Receive turn result
     */
    public void receiveTurnResult() {
        // Client receive game from the server
        this.game = getGame();
        if (this.game.getGameState() == State.GAME_OVER) {
            String winner = this.game.getPlayerList().get(this.game.getWinnerId()).getPlayerName();
            System.out.println("Game over. The winner is " + winner + ".\n");
        } else if (this.game.isLoser(this.playerID)) {
            System.out.println("You have lost. Now you are watching the game.\n");
            isLoser = true;
        }
    }




}
