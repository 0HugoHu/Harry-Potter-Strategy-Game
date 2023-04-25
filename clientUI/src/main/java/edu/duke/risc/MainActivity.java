package edu.duke.risc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import edu.duke.risc.ui.main.GameFragment;

public class MainActivity extends AppCompatActivity {

    /**
     * onCreate
     *
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        hideActionBar();

        // Receive bundle from LoginActivity
        Bundle bundle = getIntent().getExtras();

        // Write bundle to SharedPreferences
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("edu.duke.risc", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", bundle.getString("username"));
        editor.putString("password", bundle.getString("password"));
        editor.putString("roomid", bundle.getString("roomid"));
        editor.putBoolean("join", bundle.getBoolean("join"));
        editor.apply();


        // Start the game fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, GameFragment.newInstance())
                    .commitNow();
        }

    }

    /**
     * onStop
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * onPause
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        hideActionBar();
    }

    /**
     * onWindowFocusChanged
     *
     * @param hasFocus has focus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideActionBar();
    }

    /**
     * hideActionBar
     */
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

}