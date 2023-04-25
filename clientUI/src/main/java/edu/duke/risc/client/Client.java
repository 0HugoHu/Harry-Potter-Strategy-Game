package edu.duke.risc.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import edu.duke.shared.Game;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.helper.State;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.unit.Unit;

public class Client {
    // Port number
    private final static int PORT = 5410;
    // Number of units at the beginning
    private final static int numUnits = 24;
    // Host name
    //private String HOST = "vcm-30577.vm.duke.edu";
    //
    private final String HOST;
    // Client socket
    private final Socket clientSocket;
    // Scanner
    Scanner scanner = new Scanner(System.in);
    // Player Id on server
    private int playerID;
    // Player name
    private String playerName;
    // Gameplay controller
    private Game game;
    // Singleton method of GameObject
    private final GameObject gameObject = new GameObject(null);

    /*
     * Initialize Client
     */
    public Client(String HOST) {
        this(HOST, "");
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
        this.gameObject.setSocket(this.clientSocket);
        this.game = new Game(0, 24);
    }

    /*
     * Connect to server
     * @param HOST Host name
     * @param PORT Port number
     * @return Client socket
     */
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
        this.game = gameObject.decodeObj();
        this.game.setPlayerName(this.playerName);
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
        sendPlayerName(false);
    }

    /*
     * Initialize client
     */
    private void setupUnitsMock() {
        for (int i = 0; i < numUnits; i++)
            this.game.getMap().getTerritoriesByOwner(this.playerName).get(0).addUnit(new Unit("Normal"));
        gameObject.encodeObj(this.game);
    }

    /*
     * Send player name to server
     */
    private void sendPlayerName(boolean isMock) {
        // Read player name
        if (isMock) {
            readPlayerNameMock();
        }
        this.game.setPlayerName(this.playerName);
        gameObject.encodeObj(this.game);
    }

    /*
     * Read player name from console
     */
    private void readPlayerName() {

    }

    /*
     * Read player name from console
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
        this.playerID = currGame.getPlayerId();
        assert (currGame.getGameState().equals(State.READY_TO_INIT_NAME));
    }

    /*
     * Play one turn
     */
    public void playOneTurn() {
        gameObject.encodeObj(this.game);
        System.out.println("Game object sent to server.\n");
    }

    /*
     * Play one turn
     */
    public void playOneTurnMock() {
        // Read instructions
        MoveTurn moveTurn = new MoveTurn(this.game.getMap(), this.game.getTurn(), this.playerName);
        AttackTurn attackTurn = new AttackTurn(this.game.getMap(), this.game.getTurn(), this.playerName);

        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Done
        this.game.addToTurnMap(this.playerID, moveTurn, attackTurn);
        gameObject.encodeObj(this.game);
    }

    /*
     * Receive turn result
     */
    public Game receiveTurnResult() {
        // Client receive game from the server
        this.game = getGame();
        if (this.game.getGameState() == State.GAME_OVER) {
            String winner = this.game.getPlayerList().get(this.game.getWinnerId()).getPlayerName();
            System.out.println("Game over. The winner is " + winner + ".\n");
        } else if (this.game.isLoser(this.playerID)) {
            System.out.println("You have lost. Now you are watching the game.\n");
        }
        // Confirm turn
        gameObject.encodeObj(this.game);
        return this.game;
    }


}
