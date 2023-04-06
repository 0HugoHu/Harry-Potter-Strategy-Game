package edu.duke.risc.client;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.duke.shared.Game;
import edu.duke.shared.helper.State;

public class ClientIntentService extends IntentService {

    // Status codes
    public static final int STATUS_FINISHED = 1;

    private Game game;
    private static final Object receivedPlayerOrder = new Object();

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String log = "Action: " + intent.getAction() + "\n" +
                    "URI: " + intent.toUri(Intent.URI_INTENT_SCHEME) + "\n";
            System.out.println(log);

            game = (Game) intent.getSerializableExtra("game");
            synchronized (receivedPlayerOrder) {
                receivedPlayerOrder.notifyAll();
            }
        }
    };

    public ClientIntentService() {
        super(ClientIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Register Broadcast Receiver
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(br, new IntentFilter("RISC_SEND_TO_SERVER"));

        // Fetch game from server
        assert intent != null;
        final ResultReceiver receiver = intent.getParcelableExtra("RISC_FETCH_FROM_SERVER");

        ClientAdapter clientAdapter = new ClientAdapter();
        clientAdapter.init(false);

        // Initialize unit
        initUnit(clientAdapter, receiver);

        while (this.game.getGameState() != State.GAME_OVER) {
            fetchResult(clientAdapter, receiver);
            System.out.println("Start waiting for player order");
            // Wait until action is committed
            try {
                synchronized (receivedPlayerOrder) {
                    receivedPlayerOrder.wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Player order received");
            clientAdapter.updateGame(this.game);
            // Send action to server
            clientAdapter.playOneTurn(false);
        }

        // End Game
        System.out.println("Game End.\n");
        clientAdapter.close();
    }

    private void fetchResult(ClientAdapter clientAdapter, ResultReceiver receiver) {
        this.game = clientAdapter.getNewGame();
        this.game.setPlayerName(clientAdapter.getPlayerName());
        this.game.setPlayerId(clientAdapter.getPlayerId());
        System.out.println("Game received");
        Bundle b = new Bundle();
        b.putSerializable("game", this.game);
        receiver.send(STATUS_FINISHED, b);
    }

    private void initUnit(ClientAdapter clientAdapter, ResultReceiver receiver) {
        fetchResult(clientAdapter, receiver);
        try {
            synchronized (receivedPlayerOrder) {
                receivedPlayerOrder.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Player unit list received");
        clientAdapter.updateGame(this.game);
        clientAdapter.sendUnitInit();
    }

}
