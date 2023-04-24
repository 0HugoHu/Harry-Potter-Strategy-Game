package edu.duke.risc.client;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import edu.duke.shared.Game;
import edu.duke.shared.helper.State;

public class ClientIntentService extends IntentService {
    // Status codes
    public static final int STATUS_FINISHED = 1;
    // Lock for player order
    private static final Object receivedPlayerOrder = new Object();
    // Result codes
    private Game game;
    // Broadcast Receiver
    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            game = (Game) intent.getSerializableExtra("game");
            synchronized (receivedPlayerOrder) {
                receivedPlayerOrder.notifyAll();
            }
        }
    };

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ClientIntentService() {
        super(ClientIntentService.class.getName());
    }

    /**
     * onHandleIntent
     *
     * @param intent intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Register Broadcast Receiver
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(br, new IntentFilter("RISC_SEND_TO_SERVER"));

        // Fetch game from server
        assert intent != null;
        final ResultReceiver receiver = intent.getParcelableExtra("RISC_FETCH_FROM_SERVER");

        // Read from shared preference
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("edu.duke.risc", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");
        String roomid = sharedPref.getString("roomid", "");
        Boolean join = sharedPref.getBoolean("join", false);


        ClientAdapter clientAdapter = new ClientAdapter(username);
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
            this.game = clientAdapter.playOneTurn(false);
            sendTurnEnd(receiver);
        }

        // End Game
        System.out.println("Game End.\n");
        clientAdapter.close();
    }

    /**
     * Fetch result from server
     *
     * @param clientAdapter clientAdapter
     * @param receiver      receiver
     */
    private void fetchResult(ClientAdapter clientAdapter, ResultReceiver receiver) {
        this.game = clientAdapter.getNewGame();
        this.game.setPlayerName(clientAdapter.getPlayerName());
        System.out.println("Game received");
        Bundle b = new Bundle();
        b.putSerializable("game", this.game);
        receiver.send(STATUS_FINISHED, b);
    }

    /**
     * Send turn end to server
     *
     * @param receiver receiver
     */
    private void sendTurnEnd(ResultReceiver receiver) {
        System.out.println("Turn End received");
        Bundle b = new Bundle();
        b.putSerializable("game", this.game);
        receiver.send(STATUS_FINISHED, b);
    }

    /**
     * Initialize unit
     *
     * @param clientAdapter clientAdapter
     * @param receiver      receiver
     */
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
