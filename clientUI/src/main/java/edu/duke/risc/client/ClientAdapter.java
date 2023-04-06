package edu.duke.risc.client;


import edu.duke.shared.Game;

public class ClientAdapter {
    private static final String HOST = "vcm-30577.vm.duke.edu";

    private final Client client;
    public ClientAdapter() {
        // Create new client
        this.client = new Client(HOST);
    }


    public void init(boolean isMock) {
        this.client.initClient(isMock);
    }

    public Game getNewGame() {
        return this.client.getGame();
    }

    public void updateGame(Game game) {
        this.client.setGame(game);
    }

    public void close() {
        this.client.safeClose();
    }

    public void sendUnitInit() {
        this.client.playOneTurn();
    }

    public void playOneTurn(boolean isMock) {
        if (isMock) {
            this.client.playOneTurnMock();
        } else {
            this.client.playOneTurn();
        }
        this.client.receiveTurnResult();
    }

    public String getPlayerName() {
        return this.client.getPlayerName();
    }

    public int getPlayerId() {
        return this.client.getPlayerId();
    }

}
