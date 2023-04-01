package edu.duke.risc.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ClientResultReceiver extends ResultReceiver {

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
