package edu.duke.risc.client;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;

import edu.duke.shared.Game;

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
        /*
         * Step 1: We pass the ResultReceiver from the activity to the intent service via intent.
         */
        assert intent != null;
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        Game game = null;
        ClientAdapter client = new ClientAdapter();
        game = client.getGame();

        Bundle b = new Bundle();
        b.putSerializable("game", game);
        receiver.send(STATUS_FINISHED, b);
        client.close();
    }
}
