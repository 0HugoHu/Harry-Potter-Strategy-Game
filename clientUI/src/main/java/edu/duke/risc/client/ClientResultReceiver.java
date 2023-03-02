package edu.duke.risc.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ClientResultReceiver extends ResultReceiver {

    /*
     * Step 1: The AppReceiver is just a custom interface class we created.
     * This interface is implemented by the activity
     */
    private final AppReceiver appReceiver;

    /*
     * ClientResultReceiver constructor
     * @param handler Handler
     * @param receiver AppReceiver
     */
    public ClientResultReceiver(Handler handler, AppReceiver receiver) {
        super(handler);
        appReceiver = receiver;
    }

    /*
     * onReceiveResult
     * @param resultCode Result code
     * @param resultData Result data
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (appReceiver != null) {
            /*
             * Step 2: We pass the resulting data from the service to the activity
             * using the AppReceiver interface
             */
            appReceiver.onReceiveResult(resultCode, resultData);
        }
    }

    /*
     * AppReceiver interface
     */
    public interface AppReceiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }
}
