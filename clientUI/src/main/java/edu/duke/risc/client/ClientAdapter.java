package edu.duke.risc.client;


import edu.duke.shared.Game;

public class ClientAdapter {
    // Host vcm-30577.vm.duke.edu/67.159.94.90
    private static final String HOST = "67.159.94.90";
//        private static final String HOST = "10.198.110.177";
    // Client
    private final Client client;

    /**
     * Constructor
     *
     * @param username username
     */
    public ClientAdapter(String username) {
        // Create new client
        this.client = new Client(HOST, username);
    }

    /**
     * Initialize client
     *
     * @param isMock is mock
     */
    public void init(boolean isMock) {
        this.client.initClient(isMock);
    }

    /**
     * Get game
     *
     * @return game
     */
    public Game getNewGame() {
        return this.client.getGame();
    }

    /**
     * Update game
     *
     * @param game game
     */
    public void updateGame(Game game) {
        this.client.setGame(game);
    }

    /**
     * Close client
     */
    public void close() {
        this.client.safeClose();
    }

    /**
     * Send unit init
     */
    public void sendUnitInit() {
        this.client.playOneTurn();
    }

    /**
     * Play one turn
     *
     * @param isMock is mock
     * @return game
     */
    public Game playOneTurn(boolean isMock) {
        if (isMock) {
            this.client.playOneTurnMock();
        } else {
            this.client.playOneTurn();
        }
        return this.client.receiveTurnResult();
    }

    /**
     * Get player name
     *
     * @return player name
     */
    public String getPlayerName() {
        return this.client.getPlayerName();
    }


}
