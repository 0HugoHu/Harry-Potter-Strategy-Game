package edu.duke.risc.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.duke.risc.R;
import edu.duke.risc.client.ClientIntentService;
import edu.duke.risc.client.ClientResultReceiver;
import edu.duke.risc.ui.adapter.UnitDataAdapter;
import edu.duke.risc.ui.model.UnitDataModel;
import edu.duke.risc.ui.state.TouchEvent;
import edu.duke.risc.ui.view.GameView;
import edu.duke.shared.Game;
import edu.duke.shared.map.Territory;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Move;
import edu.duke.shared.turn.MoveTurn;

public class GameFragment extends Fragment implements ClientResultReceiver.AppReceiver {

    private GameView mGameView;

    private Game mGame;

    private TouchEvent mTouchEvent;

    // if setup color mapping
    private boolean isColorMapping = false;

    ArrayList<UnitDataModel> dataModels;
    ListView move_attack_listview;
    ListView unit_listview;
    private UnitDataAdapter adapter;

    private String orderTerrFrom;

    private String orderTerrTo;

    ConstraintLayout shadow_view;
    ConstraintLayout base_view;
    View order_view;
    View inner_order_view;
    ViewGroup global_prompt;

    ViewGroup move_attack_view;
    ViewGroup prop_view;
    ViewGroup unit_view;


    private MoveTurn moveTurn;
    private AttackTurn attackTurn;


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
            // update the game view
            global_prompt.setVisibility(View.GONE);
            inner_order_view.setVisibility(View.VISIBLE);
            base_view.setVisibility(View.GONE);
            if (!isColorMapping) {
                mGameView.initColorMapping(this.mGame.getPlayerList());
                isColorMapping = true;
            }
            if (this.mGame != null) {
                moveTurn = new MoveTurn(this.mGame.getMap(), this.mGame.getTurn(), this.mGame.getPlayerName());
                attackTurn = new AttackTurn(this.mGame.getMap(), this.mGame.getTurn(), this.mGame.getPlayerName());
                mGameView.updateMap(this.mGame.getMap());
                mGameView.updateGame(this.mGame);
            }
        }
    }

    /**
     * Register the intent service in the activity
     */
    private void registerService() {
        Intent intent = new Intent(requireActivity().getApplicationContext(), ClientIntentService.class);
        ClientResultReceiver resultReceiver = new ClientResultReceiver(new Handler(), this);
        intent.putExtra("RISC_FETCH_FROM_SERVER", resultReceiver);
        requireActivity().startService(intent);
    }

    private void closeService() {
        Intent intent = new Intent(requireActivity().getApplicationContext(), ClientIntentService.class);
        requireActivity().stopService(intent);
    }

    private FrameLayout setupFrameLayout(Context context) {
        // FrameLayout: Hold the game view and the UI
        FrameLayout framelayout = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        // SurfaceView: Draw game map and units
        mGameView = new GameView(context, this.mGame);
        mGameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Load the main order view
        LayoutInflater inflater_order = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup nullParent = null;
        order_view = inflater_order.inflate(R.layout.order_view, nullParent, false);

        // Prepare for three fragments
        move_attack_view = order_view.findViewById(R.id.inflate_move_attack);
        prop_view = order_view.findViewById(R.id.inflate_prop);
        unit_view = order_view.findViewById(R.id.inflate_unit);

        // Load the global prompt view
        LayoutInflater inflater_ui = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ui = inflater_ui.inflate(R.layout.game_ui, nullParent, false);

        // Initialize the list view and adapter
        move_attack_listview = move_attack_view.findViewById(R.id.list);
        unit_listview = unit_view.findViewById(R.id.unit_list);
        dataModels = new ArrayList<>();
        adapter = new UnitDataAdapter(dataModels, context);
        move_attack_listview.setAdapter(adapter);
        unit_listview.setAdapter(adapter);

        // Initialize widgets in the move_attack view
        Button commit_btn = move_attack_view.findViewById(R.id.attack_btn);
        Button end_turn_btn = ui.findViewById(R.id.end_turn);
        TextView cost_error_prompt = move_attack_view.findViewById(R.id.cost_error_prompt);
        TextView view_title = move_attack_view.findViewById(R.id.view_title);
        TextView cost_title = move_attack_view.findViewById(R.id.cost_title);
        TextView total_cost = move_attack_view.findViewById(R.id.total_cost);
        // Prop
        TextView prop_title = prop_view.findViewById(R.id.prop_title);
        TextView prop_owner = prop_view.findViewById(R.id.prop_owner);
        TextView prop_desc = prop_view.findViewById(R.id.prop_desc);
        Button prop_btn = prop_view.findViewById(R.id.prop_back_btn);
        // Unit
        TextView unit_title = unit_view.findViewById(R.id.unit_title);
        Button unit_btn = unit_view.findViewById(R.id.unit_back_btn);

        // Initialize widgets in common use
        shadow_view = order_view.findViewById(R.id.shadow_view);
        base_view = order_view.findViewById(R.id.base_view);
        global_prompt = order_view.findViewById(R.id.global_prompt);
        inner_order_view = order_view.findViewById(R.id.inner_order_view);


        prop_btn.setOnClickListener(v -> {
            base_view.setVisibility(View.GONE);
        });

        unit_btn.setOnClickListener(v -> {
            base_view.setVisibility(View.GONE);
        });


        // TODO: Use for test only
        int max_cost = 10;

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
            if (mTouchEvent == TouchEvent.MOVE) {
                for (UnitDataModel unit : dataModels) {
                    int number = unit.getNumber();
                    if (number > 0) {
                        moveTurn.addMove(new Move(orderTerrFrom, orderTerrTo, unit.getNumber(), this.mGame.getPlayerName()));
                    }
                }
            } else if (mTouchEvent == TouchEvent.ATTACK) {
                for (UnitDataModel unit : dataModels) {
                    int number = unit.getNumber();
                    if (number > 0) {
                        attackTurn.addAttack(new Attack(orderTerrFrom, orderTerrTo, unit.getNumber(), this.mGame.getPlayerName()));
                    }
                }
            } else {
                return;
            }
            // Done
            base_view.setVisibility(View.GONE);
        });

        // Click the shadow area to close the order view
        shadow_view.setOnClickListener(v -> base_view.setVisibility(View.GONE));

        mGameView.setEventListener(new GameView.EventListener() {
            @Override
            public void onMoveOrder(String terrFrom, String terrTo) {
                // Update
                move_attack_view.setVisibility(View.VISIBLE);
                prop_view.setVisibility(View.GONE);
                unit_view.setVisibility(View.GONE);
                base_view.setVisibility(View.VISIBLE);
                orderTerrFrom = terrFrom;
                orderTerrTo = terrTo;
                updateTerrInfo(terrFrom);
                String title = "Move: " + terrFrom + " to " + terrTo;
                view_title.setText(title);
                commit_btn.setText(context.getResources().getString(R.string.move));
                // Set touch event
                mTouchEvent = TouchEvent.MOVE;
            }

            @Override
            public void onAttackOrder(String terrFrom, String terrTo) {
                move_attack_view.setVisibility(View.VISIBLE);
                prop_view.setVisibility(View.GONE);
                unit_view.setVisibility(View.GONE);
                base_view.setVisibility(View.VISIBLE);
                orderTerrFrom = terrFrom;
                orderTerrTo = terrTo;
                updateTerrInfo(terrFrom);
                String title = "Attack: " + terrFrom + " to " + terrTo;
                view_title.setText(title);
                commit_btn.setText(context.getResources().getString(R.string.attack));
                mTouchEvent = TouchEvent.ATTACK;
            }

            @Override
            public void onPropOrder(String territoryName) {
                move_attack_view.setVisibility(View.GONE);
                prop_view.setVisibility(View.VISIBLE);
                unit_view.setVisibility(View.GONE);
                base_view.setVisibility(View.VISIBLE);
                updateTerrInfo(territoryName);
                String title = "Prop: " + territoryName;
                prop_title.setText(title);
                prop_owner.setText("Owner: " + mGame.getMap().getTerritory(territoryName).getOwner());
                prop_desc.setText("Description: \n" + context.getResources().getString(R.string.terr_desc_example));
                mTouchEvent = TouchEvent.PROP;
            }

            @Override
            public void onUnitOrder(String territoryName) {
                move_attack_view.setVisibility(View.GONE);
                prop_view.setVisibility(View.GONE);
                unit_view.setVisibility(View.VISIBLE);
                base_view.setVisibility(View.VISIBLE);
                updateTerrInfo(territoryName);
                String title = "Unit: " + territoryName;
                unit_title.setText(title);
                mTouchEvent = TouchEvent.UNIT;
            }
        });

        end_turn_btn.setOnClickListener(v -> {
            base_view.setVisibility(View.VISIBLE);
            inner_order_view.setVisibility(View.GONE);
            global_prompt.setVisibility(View.VISIBLE);
            // Commit all moves and attacks
            this.mGame.addToTurnMap(this.mGame.getPlayerId(), moveTurn, attackTurn);
            // Send the game object to ClientIntentService
            Intent intent = new Intent();
            intent.setAction("RISC_SEND_TO_SERVER");
            intent.putExtra("game", this.mGame);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });


        framelayout.addView(mGameView);
        framelayout.addView(ui, params);
        framelayout.addView(order_view, params);

        return framelayout;
    }


    private void updateTerrInfo(String terrName) {
        dataModels.clear();

        Territory territory = mGame.getMap().getTerritory(terrName);
        if (territory == null) {
            return;
        }

        // TODO: Wait for Unit class implementation
        int unitNum = territory.getUnits().size();
        if (unitNum > 0) {
            dataModels.add(new UnitDataModel(requireActivity().getResources().getString(R.string.example_unit), unitNum));
        }
        adapter.notifyDataSetChanged();
    }

}