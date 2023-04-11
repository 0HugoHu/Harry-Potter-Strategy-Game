package edu.duke.risc.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.net.Socket;

import edu.duke.shared.Game;
import edu.duke.shared.helper.GameObject;

public class ClientBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String log = "Action: " + intent.getAction() + "\n" +
                "URI: " + intent.toUri(Intent.URI_INTENT_SCHEME) + "\n";
        System.out.println(log);

        int playerid = intent.getIntExtra("playerid", -1);
        assert playerid == -1;

        Game game = (Game) intent.getSerializableExtra("game");
        Socket socket = game.getPlayerList().get(playerid).getSocket();
        assert socket == null;

        GameObject obj = new GameObject(socket);
        obj.encodeObj(game);
    }
}
