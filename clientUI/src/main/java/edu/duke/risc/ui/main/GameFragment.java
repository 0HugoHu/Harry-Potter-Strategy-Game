package edu.duke.risc.ui.main;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;

import edu.duke.risc.R;
import edu.duke.risc.client.ClientIntentService;
import edu.duke.risc.client.ClientResultReceiver;
import edu.duke.risc.ui.view.GameView;
import edu.duke.shared.Game;

public class GameFragment extends Fragment implements ClientResultReceiver.AppReceiver {

    private GameView mGameView;

    // if setup color mapping
    private boolean isColorMapping = false;


    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameViewModel mViewModel = new ViewModelProvider(this).get(GameViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mGameView = new GameView(requireActivity().getApplicationContext());
        mGameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        FrameLayout framelayout = new FrameLayout(requireActivity().getApplicationContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater_move = (LayoutInflater)requireActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup nullParent = null;
        View move_order = inflater_move.inflate(R.layout.move_order, nullParent, false);


        Button button = new Button(requireActivity().getApplicationContext());
        button.setText("Test");
        button.setWidth(400);
        button.setHeight(200);

        framelayout.addView(button, params);
        framelayout.addView(move_order, params);
        framelayout.addView(mGameView, params);

        return framelayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        registerService();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGameView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGameView.resume();
    }

    /**
     * Handle the results from the intent service here
     *
     * @param resultCode Result code
     * @param resultData Result data
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == ClientIntentService.STATUS_FINISHED) {
            Game game = (Game) resultData.getSerializable("game");
            if (!isColorMapping) {
                mGameView.initColorMapping(game.getPlayerList());
                isColorMapping = true;
            }
//        String message = new DisplayMap(game.getMap()).toString();
//        Log.v("XXX", message);
            if (game != null) {
                mGameView.updateMap(game.getMap());
            }
        }

        // Text view
//        myTextView.setText(message);
    }

    /**
     * Register the intent service in the activity
     */
    private void registerService() {
        Intent intent = new Intent(requireActivity().getApplicationContext(), ClientIntentService.class);
        ClientResultReceiver resultReceiver = new ClientResultReceiver(new Handler(), this);
        intent.putExtra("receiver", resultReceiver);
        requireActivity().startService(intent);
    }

    private void closeService() {
        Intent intent = new Intent(requireActivity().getApplicationContext(), ClientIntentService.class);
        requireActivity().stopService(intent);
    }

}