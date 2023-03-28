package edu.duke.risc.client;

import edu.duke.shared.Game;
import edu.duke.shared.helper.State;

public class ClientAdapter {
    private static final String HOST = "vcm-30577.vm.duke.edu";

    private Client client;
    public ClientAdapter() {
        init();
    }

    private void init() {
        // Create new client
        this.client = new Client(HOST);

        // Init client
        this.client.initClient();

        // Start game
        while (this.client.accessGame().getGameState() != State.GAME_OVER) {
            this.client.playOneTurn();
        }

        // End Game
        System.out.println("Game End.\n");
        this.client.safeClose();
    }

    public Game getGame() {
        return this.client.accessGame();
    }

    public void close() {
        this.client.safeClose();
    }
}
