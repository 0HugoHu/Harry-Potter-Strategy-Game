package edu.duke.risc.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.method.Touch;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.duke.risc.R;
import edu.duke.risc.client.ClientIntentService;
import edu.duke.risc.client.ClientResultReceiver;
import edu.duke.risc.ui.adapter.UnitDataAdapter;
import edu.duke.risc.ui.model.UnitDataModel;
import edu.duke.risc.ui.state.TouchEvent;
import edu.duke.risc.ui.view.GameView;
import edu.duke.shared.Game;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.map.Territory;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Move;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.unit.Unit;

public class GameFragment extends Fragment implements ClientResultReceiver.AppReceiver {

    private GameView mGameView;

    private Game mGame;

    private TouchEvent mTouchEvent;

    // if setup color mapping
    private boolean isColorMapping = false;

    ArrayList<UnitDataModel> dataModels;
    ListView listView;
    private UnitDataAdapter adapter;

    private String orderTerrFrom;

    private String orderTerrTo;

    ConstraintLayout shadow_view;
    ConstraintLayout base_view;
    LinearLayout order_view;
    ViewGroup global_prompt;


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
            global_prompt.setVisibility(View.INVISIBLE);
            order_view.setVisibility(View.VISIBLE);
            base_view.setVisibility(View.INVISIBLE);
            if (!isColorMapping) {
                mGameView.initColorMapping(this.mGame.getPlayerList());
                isColorMapping = true;
            }
            if (this.mGame != null) {
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
        mGameView = new GameView(context, this.mGame);
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
        adapter = new UnitDataAdapter(dataModels, context);
        listView.setAdapter(adapter);


        Button commit_btn = move_order.findViewById(R.id.attack_btn);
        TextView cost_error_prompt = move_order.findViewById(R.id.cost_error_prompt);
        TextView view_title = move_order.findViewById(R.id.view_title);
        TextView cost_title = move_order.findViewById(R.id.cost_title);
        TextView total_cost = move_order.findViewById(R.id.total_cost);
        shadow_view = move_order.findViewById(R.id.shadow_view);
        base_view = move_order.findViewById(R.id.base_view);
        order_view = move_order.findViewById(R.id.order_view);
        global_prompt = move_order.findViewById(R.id.global_prompt);

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
            // Read instructions
            MoveTurn moveTurn = new MoveTurn(this.mGame.getMap(), this.mGame.getTurn(), this.mGame.getPlayerName());
            AttackTurn attackTurn = new AttackTurn(this.mGame.getMap(), this.mGame.getTurn(), this.mGame.getPlayerName());

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
            this.mGame.addToTurnMap(this.mGame.getPlayerId(), moveTurn, attackTurn);
            order_view.setVisibility(View.INVISIBLE);
            global_prompt.setVisibility(View.VISIBLE);
            // Send the game object to ClientIntentService
            Intent intent = new Intent();
            intent.setAction("RISC_SEND_TO_SERVER");
            intent.putExtra("game", this.mGame);
            context.sendBroadcast(intent);

        });

        shadow_view.setOnClickListener(v -> base_view.setVisibility(View.INVISIBLE));

        mGameView.setEventListener(new GameView.EventListener() {
            @Override
            public void onMoveOrder(String terrFrom, String terrTo) {
                // Update
                orderTerrFrom = terrFrom;
                orderTerrTo = terrTo;
                updateTerrInfo(terrFrom);

                base_view.setVisibility(View.VISIBLE);
                String title = "Move: " + terrFrom + " to " + terrTo;
                view_title.setText(title);
                listView.setVisibility(View.VISIBLE);
                cost_title.setVisibility(View.VISIBLE);
                total_cost.setVisibility(View.VISIBLE);
                commit_btn.setText(context.getResources().getString(R.string.move));
                mTouchEvent = TouchEvent.MOVE;
            }

            @Override
            public void onAttackOrder(String terrFrom, String terrTo) {
                orderTerrFrom = terrFrom;
                orderTerrTo = terrTo;
                updateTerrInfo(terrFrom);

                base_view.setVisibility(View.VISIBLE);
                String title = "Attack: " + terrFrom + " to " + terrTo;
                view_title.setText(title);
                listView.setVisibility(View.VISIBLE);
                cost_title.setVisibility(View.VISIBLE);
                total_cost.setVisibility(View.VISIBLE);
                commit_btn.setText(context.getResources().getString(R.string.attack));
                mTouchEvent = TouchEvent.ATTACK;
            }

            @Override
            public void onPropOrder(String territoryName) {
                updateTerrInfo(territoryName);

                base_view.setVisibility(View.VISIBLE);
                String title = "Prop: " + territoryName;
                view_title.setText(title);
                listView.setVisibility(View.GONE);
                cost_title.setVisibility(View.INVISIBLE);
                total_cost.setVisibility(View.INVISIBLE);
                commit_btn.setText(context.getResources().getString(R.string.back));
                mTouchEvent = TouchEvent.PROP;
            }

            @Override
            public void onUnitOrder(String territoryName) {
                updateTerrInfo(territoryName);

                base_view.setVisibility(View.VISIBLE);
                String title = "Unit: " + territoryName;
                view_title.setText(title);
                listView.setVisibility(View.VISIBLE);
                cost_title.setVisibility(View.INVISIBLE);
                total_cost.setVisibility(View.INVISIBLE);
                commit_btn.setText(context.getResources().getString(R.string.back));
                mTouchEvent = TouchEvent.UNIT;
            }
        });


        framelayout.addView(mGameView);
        framelayout.addView(move_order, params);

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