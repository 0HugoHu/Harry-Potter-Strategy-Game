package edu.duke.risc.client;


import edu.duke.shared.Game;
import edu.duke.shared.helper.State;

public class ClientAdapter {
    private static final String HOST = "192.168.1.134";

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

    public Game playOneTurn(boolean isMock) {
        if (isMock) {
            this.client.playOneTurnMock();
        } else {
            this.client.playOneTurn();
        }
        return this.client.receiveTurnResult();
    }

    public String getPlayerName() {
        return this.client.getPlayerName();
    }


}
