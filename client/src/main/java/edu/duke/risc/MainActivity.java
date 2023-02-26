package edu.duke.risc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import edu.duke.risc.client.ClientIntentService;
import edu.duke.risc.client.ClientResultReceiver;
import edu.duke.risc.display.DisplayMap;
import edu.duke.shared.Game;

public class MainActivity extends AppCompatActivity implements ClientResultReceiver.AppReceiver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textmap);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, GameFragment.newInstance())
//                    .commitNow();
//        }
        registerService();
    }

    /*
     * Step 1: Register the intent service in the activity
     */
    private void registerService() {
        Intent intent = new Intent(getApplicationContext(), ClientIntentService.class);
        /*
         * Step 2: We pass the ResultReceiver via the intent to the intent service
         *
         */
        // Result receiver
        ClientResultReceiver resultReceiver = new ClientResultReceiver(new Handler(), this);
        intent.putExtra("receiver", resultReceiver);
        startService(intent);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        /*
         * Step 3: Handle the results from the intent service here!
         *
         */
        Game game = (Game) resultData.getSerializable("game");
        String message = new DisplayMap(game.getMap()).toString();
        Log.e("XXX", message);
        // Text view
        TextView myTextView = (TextView) findViewById(R.id.message);
        myTextView.setText(message);
    }


    @Override
    protected void onStop() {
        super.onStop();
        /*
         * Step 4: don't forget to clear receiver in order to avoid leaks.
         *
         */
//        if(resultReceiver != null) {
//            resultReceiver.setAppReceiver(null);
//        }
    }
}