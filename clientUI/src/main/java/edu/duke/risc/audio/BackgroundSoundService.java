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
import edu.duke.shared.Game;

public class BackgroundSoundService extends Service {
    MediaPlayer mediaPlayer;

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(context, R.raw.bgm_end);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(100, 100);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Register Broadcast Receiver
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(br, new IntentFilter("RISC_GAME_END"));
        mediaPlayer = MediaPlayer.create(this, R.raw.harry_potter_bgm);
        mediaPlayer.setLooping(true); // Set looping
        mediaPlayer.setVolume(100, 100);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        return startId;
    }

    public void onStart(Intent intent, int startId) {
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void onLowMemory() {
    }
}