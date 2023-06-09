package edu.duke.risc.audio;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import edu.duke.risc.R;

public class BackgroundSoundService extends Service {
    // Media Player
    MediaPlayer mediaPlayer;
    // Media Player
    MediaPlayer mediaPlayer2;
    // Context
    Context mContext;

    /**
     * Broadcast Receiver
     */
    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mediaPlayer.stop();
            mediaPlayer2 = MediaPlayer.create(mContext, R.raw.bgm_end);
            mediaPlayer2.setLooping(true);
            mediaPlayer2.setVolume(100, 100);
            mediaPlayer2.start();
        }
    };

    /**
     * Constructor
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * On Create
     */
    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        // Register Broadcast Receiver
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(br, new IntentFilter("RISC_GAME_END"));
        mediaPlayer = MediaPlayer.create(this, R.raw.harry_potter_bgm);
        mediaPlayer.setLooping(true); // Set looping
        mediaPlayer.setVolume(100, 100);
    }

    /**
     * When the service is started
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        return startId;
    }

    /**
     * Start
     */
    public void onStart(Intent intent, int startId) {
    }

    /**
     * On Destroy
     */
    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    /**
     * On Low Memory
     */
    @Override
    public void onLowMemory() {
    }
}
