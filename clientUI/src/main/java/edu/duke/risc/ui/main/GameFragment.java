package edu.duke.risc.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.duke.risc.R;
import edu.duke.risc.client.ClientIntentService;
import edu.duke.risc.client.ClientResultReceiver;
import edu.duke.risc.ui.adapter.UnitDataAdapter;
import edu.duke.risc.ui.model.UnitDataModel;
import edu.duke.risc.ui.view.GameView;
import edu.duke.shared.Game;

public class GameFragment extends Fragment implements ClientResultReceiver.AppReceiver {

    private GameView mGameView;

    private Game mGame;

    // if setup color mapping
    private boolean isColorMapping = false;

    ArrayList<UnitDataModel> dataModels;
    ListView listView;
    private UnitDataAdapter adapter;


    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return setupFrameLayout(requireActivity().getApplicationContext());
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
            this.mGame = (Game) resultData.getSerializable("game");
            if (!isColorMapping) {
                mGameView.initColorMapping(this.mGame.getPlayerList());
                isColorMapping = true;
            }
            if (this.mGame != null) {
                mGameView.updateMap(this.mGame.getMap());
            }
        }
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

    private FrameLayout setupFrameLayout(Context context) {
        mGameView = new GameView(context);
        mGameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);


        FrameLayout framelayout = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        LayoutInflater inflater_move = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup nullParent = null;
        View move_order = inflater_move.inflate(R.layout.move_order, nullParent, false);


        listView = move_order.findViewById(R.id.list);

        dataModels = new ArrayList<>();

        dataModels.add(new UnitDataModel(getResources().getString(R.string.example_unit), 5));
        dataModels.add(new UnitDataModel(getResources().getString(R.string.example_unit2), 1));
        dataModels.add(new UnitDataModel(getResources().getString(R.string.example_unit3), 12));
        dataModels.add(new UnitDataModel(getResources().getString(R.string.example_unit4), 2));

        adapter = new UnitDataAdapter(dataModels, context);
        listView.setAdapter(adapter);


        Button commit_btn = move_order.findViewById(R.id.attack_btn);
        TextView cost_error_prompt = move_order.findViewById(R.id.cost_error_prompt);
        TextView view_title = move_order.findViewById(R.id.view_title);
        TextView cost_title = move_order.findViewById(R.id.cost_title);
        TextView total_cost = move_order.findViewById(R.id.total_cost);
        ConstraintLayout shadow_view = move_order.findViewById(R.id.shadow_view);
        ConstraintLayout base_view = move_order.findViewById(R.id.base_view);

        // TODO: Use for test only
        int max_cost = 7;

        // Update the cost when the number of units change
        adapter.setCostListener(() -> {
            int cost = adapter.getTotalCost();
            total_cost.setText(String.valueOf(cost));
            if (cost > max_cost) {
                total_cost.setTextColor(getResources().getColor(R.color.error_prompt));
                commit_btn.setEnabled(false);
                cost_error_prompt.setVisibility(View.VISIBLE);
            } else {
                total_cost.setTextColor(getResources().getColor(R.color.order_text));
                commit_btn.setEnabled(true);
                cost_error_prompt.setVisibility(View.INVISIBLE);
            }
        });

        commit_btn.setOnClickListener(v -> {

        });

        shadow_view.setOnClickListener(v -> base_view.setVisibility(View.INVISIBLE));

        mGameView.setEventListener(new GameView.EventListener() {
            @Override
            public void onMoveOrder(String terrFrom, String terrTo) {
                base_view.setVisibility(View.VISIBLE);
                String title = "Move: " + terrFrom + " to " + terrTo;
                view_title.setText(title);
                listView.setVisibility(View.VISIBLE);
                cost_title.setVisibility(View.VISIBLE);
                total_cost.setVisibility(View.VISIBLE);
                commit_btn.setText(context.getResources().getString(R.string.move));
            }

            @Override
            public void onAttackOrder(String terrFrom, String terrTo) {
                base_view.setVisibility(View.VISIBLE);
                String title = "Attack: " + terrFrom + " to " + terrTo;
                view_title.setText(title);
                listView.setVisibility(View.VISIBLE);
                cost_title.setVisibility(View.VISIBLE);
                total_cost.setVisibility(View.VISIBLE);
                commit_btn.setText(context.getResources().getString(R.string.attack));
            }

            @Override
            public void onPropOrder(String territoryName) {
                base_view.setVisibility(View.VISIBLE);
                String title = "Prop: " + territoryName;
                view_title.setText(title);
                listView.setVisibility(View.GONE);
                cost_title.setVisibility(View.INVISIBLE);
                total_cost.setVisibility(View.INVISIBLE);
                commit_btn.setText(context.getResources().getString(R.string.back));
            }

            @Override
            public void onUnitOrder(String territoryName) {
                base_view.setVisibility(View.VISIBLE);
                String title = "Unit: " + territoryName;
                view_title.setText(title);
                listView.setVisibility(View.VISIBLE);
                cost_title.setVisibility(View.INVISIBLE);
                total_cost.setVisibility(View.INVISIBLE);
                commit_btn.setText(context.getResources().getString(R.string.back));
            }
        });


        framelayout.addView(mGameView);
        framelayout.addView(move_order, params);

        return framelayout;
    }

}