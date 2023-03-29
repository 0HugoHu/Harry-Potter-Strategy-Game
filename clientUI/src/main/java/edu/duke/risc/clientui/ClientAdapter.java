package edu.duke.risc.clientui;

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

    public Game getGame() {
        return this.client.accessGame();
    }

    public void close() {
        this.client.safeClose();
    }

    public void playOneTurn(boolean isMock) {
        if (isMock) {
            this.client.playOneTurnMock();
        } else {
            this.client.playOneTurn();
        }
    }

}
