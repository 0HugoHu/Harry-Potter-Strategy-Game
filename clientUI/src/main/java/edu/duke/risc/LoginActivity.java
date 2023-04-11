package edu.duke.risc;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import edu.duke.risc.audio.BackgroundSoundService;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        hideActionBar();

        TextView username = findViewById(R.id.username_input);
        TextView password = findViewById(R.id.password_input);
        TextView roomid = findViewById(R.id.roomid_input);
        Button join = findViewById(R.id.join_btn);
        Button newGame = findViewById(R.id.create_btn);

        join.setOnClickListener(v -> {
            boolean register = false;
            if (username.getText().toString().equals("") || password.getText().toString().equals("") || roomid.getText().toString().equals("")) {
                return;
            }
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", username.getText().toString());
            intent.putExtra("password", password.getText().toString());
            intent.putExtra("roomid", roomid.getText().toString());
            intent.putExtra("join", true);
            startActivity(intent);
        });

        newGame.setOnClickListener(v -> {
            boolean register = false;
            if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
                return;
            }
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", username.getText().toString());
            intent.putExtra("password", password.getText().toString());
            intent.putExtra("roomid", roomid.getText().toString());
            intent.putExtra("join", false);
            startActivity(intent);
        });

        VideoView background_video = findViewById(R.id.videoView);
        Random rand = new Random();
        switch (rand.nextInt(6)) {
            case 0:
                background_video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.login_audi);
                break;
            case 1:
                background_video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.login_b);
                break;
            case 2:
                background_video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.login_g);
                break;
            case 3:
                background_video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.login_r);
                break;
            case 4:
                background_video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.login_y);
                break;
            case 5:
                background_video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.login_hogwarts);
                break;
        }

        background_video.setOnPreparedListener(mp -> {
            mp.setVolume(100f, 100f);
            mp.setLooping(true);
        });

        background_video.start();
        PlayBackgroundSound();

    }


    @Override
    protected void onResume() {
        super.onResume();
        hideActionBar();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideActionBar();
    }

    private void hideActionBar() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void PlayBackgroundSound() {
        Intent intent = new Intent(LoginActivity.this, BackgroundSoundService.class);
        startService(intent);
    }

}

