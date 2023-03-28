package edu.duke.risc.clientui;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;

import edu.duke.shared.Game;
import edu.duke.shared.helper.State;

public class ClientIntentService extends IntentService {

    // Status codes
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public ClientIntentService() {
        super(ClientIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        ClientAdapter client1 = new ClientAdapter();
        client1.init(true);

        // Mock other 2 players
        ClientAdapter client2 = new ClientAdapter();
        ClientAdapter client3 = new ClientAdapter();
        client2.init(true);
        client3.init(true);

        while (client1.getGame().getGameState() != State.GAME_OVER) {
            client1.playOneTurn(false);

            // Mock other players
            client2.playOneTurn(true);
            client3.playOneTurn(true);

            Game game = client1.getGame();
            Bundle b = new Bundle();
            System.out.println("Game received");
            b.putSerializable("game", game);
            receiver.send(STATUS_FINISHED, b);
        }

        // End Game
        System.out.println("Game End.\n");
        client1.close();
        client2.close();
        client3.close();
    }
}
