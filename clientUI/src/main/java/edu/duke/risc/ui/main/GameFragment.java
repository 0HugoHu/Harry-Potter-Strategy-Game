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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import edu.duke.risc.R;
import edu.duke.risc.client.ClientIntentService;
import edu.duke.risc.client.ClientResultReceiver;
import edu.duke.risc.ui.adapter.TerrDataAdapter;
import edu.duke.risc.ui.adapter.UnitDataAdapter;
import edu.duke.risc.ui.model.TerrDataModel;
import edu.duke.risc.ui.model.UnitDataModel;
import edu.duke.risc.ui.state.TouchEvent;
import edu.duke.risc.ui.view.GameView;
import edu.duke.shared.Game;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Move;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

public class GameFragment extends Fragment implements ClientResultReceiver.AppReceiver {

    private GameView mGameView;

    private Game mGame;

    private Context context;

    private TouchEvent mTouchEvent;

    ArrayList<UnitDataModel> unitDataModels;
    ArrayList<TerrDataModel> terrDataModels;
    ListView move_attack_listview;
    ListView unit_listview;

    ListView unit_init_listview;
    private UnitDataAdapter unitAdapter;
    private TerrDataAdapter terrAdapter;

    private String orderTerrFrom;

    private String orderTerrTo;

    ConstraintLayout shadow_view;
    ConstraintLayout base_view;
    View order_view;
    View inner_order_view;

    View init_view;
    View ui_view;


    ViewGroup global_prompt;
    ViewGroup move_attack_view;
    ViewGroup prop_view;
    ViewGroup unit_view;
    ViewGroup unit_init_view;
    ViewGroup tech_view;
    ViewGroup init_base_view;


    private MoveTurn moveTurn;
    private AttackTurn attackTurn;

    private HashMap<String, HashMap<String, Integer>> unitMoveAttackMap;


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
            System.out.println("Game state: " + mGame.getGameState());
            switch (mGame.getGameState()) {
                // The first two states should be done in the login page
                case WAITING_TO_JOIN:
                case READY_TO_INIT_NAME:
                case READY_TO_INIT_UNITS:
                    mGameView.initColorMapping(this.mGame.getPlayerList());
                    mGameView.updateMap(this.mGame.getMap());
                    mGameView.updateGame(this.mGame);
                    // Assign text
                    assignText();
                    // Assign units
                    assignUnits();
                    break;
                case TURN_BEGIN:
                    updatePlayerValues();
                    unitMoveAttackMap = new HashMap<>();
                    // Update the game view
                    ui_view.findViewById(R.id.ui_side_bar_init).setVisibility(View.GONE);
                    ui_view.findViewById(R.id.ui_side_bar).setVisibility(View.VISIBLE);
                    moveTurn = new MoveTurn(this.mGame.getMap(), this.mGame.getTurn(), this.mGame.getPlayerName());
                    attackTurn = new AttackTurn(this.mGame.getMap(), this.mGame.getTurn(), this.mGame.getPlayerName());
                    mGameView.updateMap(this.mGame.getMap());
                    mGameView.updateGame(this.mGame);
                    break;
                case TURN_END:
                    // TODO: Prompt next turn
                    break;
                case GAME_OVER:
                    // TODO: Prompt game over
                    break;
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
        this.context = context;
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
        unit_init_view = order_view.findViewById(R.id.inflate_init_unit);
        tech_view = order_view.findViewById(R.id.inflate_tech_view);

        // Load the global prompt view
        LayoutInflater inflater_ui = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ui_view = inflater_ui.inflate(R.layout.game_ui, nullParent, false);

        // Load the init view
        LayoutInflater inflater_init = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init_view = inflater_init.inflate(R.layout.init_view, nullParent, false);

        // Initialize the list view and adapter
        move_attack_listview = move_attack_view.findViewById(R.id.list);
        unit_listview = unit_view.findViewById(R.id.unit_list);
        unitDataModels = new ArrayList<>();
        unitAdapter = new UnitDataAdapter(unitDataModels, context);
        move_attack_listview.setAdapter(unitAdapter);
        unit_listview.setAdapter(unitAdapter);

        // Initialize widgets in the move_attack view
        Button commit_btn = move_attack_view.findViewById(R.id.attack_btn);
        TextView cost_error_prompt = move_attack_view.findViewById(R.id.cost_error_prompt);
        TextView view_title = move_attack_view.findViewById(R.id.view_title);
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

        // Initialize widgets in ui surface
        Button chat_btn = ui_view.findViewById(R.id.ui_chat);
        Button end_turn_btn = ui_view.findViewById(R.id.end_turn);
        Button rank_btn = ui_view.findViewById(R.id.rank);
        Button tech_btn = ui_view.findViewById(R.id.tech);
        ui_view.findViewById(R.id.ui_side_bar).setVisibility(View.GONE);

        // Initialize widgets in init view
        Button init_btn = init_view.findViewById(R.id.init_view_accept);
        init_base_view = init_view.findViewById(R.id.init_base);
        init_base_view.setVisibility(View.GONE);

        // Initialize widgets in tech view
        TextView tech_error_prompt = tech_view.findViewById(R.id.tech_error_prompt);
        Button tech_upgrade_btn = tech_view.findViewById(R.id.tech_upgrade_btn);
        Button tech_back_btn = tech_view.findViewById(R.id.tech_back_btn);

        init_btn.setOnClickListener(v -> {
            init_base_view.setVisibility(View.GONE);
            base_view.setVisibility(View.VISIBLE);
            inner_order_view.setVisibility(View.VISIBLE);
            shadow_view.setVisibility(View.VISIBLE);
            base_view.findViewById(R.id.inflate_move_attack).setVisibility(View.GONE);
            base_view.findViewById(R.id.inflate_unit).setVisibility(View.GONE);
            base_view.findViewById(R.id.inflate_prop).setVisibility(View.GONE);
            base_view.findViewById(R.id.inflate_init_unit).setVisibility(View.VISIBLE);
            base_view.findViewById(R.id.inflate_tech_view).setVisibility(View.GONE);
            base_view.findViewById(R.id.global_prompt).setVisibility(View.GONE);
        });

        tech_btn.setOnClickListener(v -> {
            move_attack_view.setVisibility(View.GONE);
            prop_view.setVisibility(View.GONE);
            unit_view.setVisibility(View.GONE);
            unit_init_view.setVisibility(View.GONE);
            tech_view.setVisibility(View.VISIBLE);
            base_view.setVisibility(View.VISIBLE);
            int tech_level = mGame.getPlayer(mGame.getPlayerName()).getWorldLevel();
            String upgrade = "Upgrade: " + (tech_level + 1) * 300 + " horns";
            tech_upgrade_btn.setText(upgrade);
            // TODO: only for text
            if ((tech_level + 1 ) * 300 > mGame.getPlayer(mGame.getPlayerName()).getHorns()) {
                tech_error_prompt.setVisibility(View.VISIBLE);
                tech_upgrade_btn.setEnabled(false);
                tech_upgrade_btn.setTextColor(getResources().getColor(R.color.error_prompt));
            } else {
                tech_error_prompt.setVisibility(View.INVISIBLE);
                tech_upgrade_btn.setEnabled(true);
                tech_upgrade_btn.setTextColor(getResources().getColor(R.color.order_text));
            }
        });

        tech_back_btn.setOnClickListener(v -> {
            base_view.setVisibility(View.GONE);
        });

        tech_upgrade_btn.setOnClickListener(v -> {
            mGame.getPlayer(mGame.getPlayerName()).upgradeWorldLevel();
            Toast.makeText(context, "Upgrade success!", Toast.LENGTH_SHORT).show();
            base_view.setVisibility(View.GONE);
        });


        prop_btn.setOnClickListener(v -> {
            base_view.setVisibility(View.GONE);
        });

        unit_btn.setOnClickListener(v -> {
            base_view.setVisibility(View.GONE);
        });


        // Update the cost when the number of units change
        unitAdapter.setCostListener(() -> {
            int cost = unitAdapter.getTotalCost();
            total_cost.setText(String.valueOf(cost));
            if (cost > mGame.getPlayer(mGame.getPlayerName()).getCoins()) {
                total_cost.setTextColor(getResources().getColor(R.color.error_prompt));
                commit_btn.setEnabled(false);
                cost_error_prompt.setVisibility(View.VISIBLE);
            } else {
                total_cost.setTextColor(getResources().getColor(R.color.order_text));
                commit_btn.setEnabled(true);
                cost_error_prompt.setVisibility(View.INVISIBLE);
            }
        });

        // Commit move or attack orders
        commit_btn.setOnClickListener(v -> {
            if (mTouchEvent == TouchEvent.MOVE) {
                for (UnitDataModel unit : unitDataModels) {
                    int number = unit.getNumber();
                    if (number > 0) {
                        moveTurn.addMove(new Move(orderTerrFrom, orderTerrTo, unit.getNumber(), this.mGame.getPlayerName()));
                        updateUnitMoveAttackMap(number, unit);
                    }
                }
            } else if (mTouchEvent == TouchEvent.ATTACK) {
                for (UnitDataModel unit : unitDataModels) {
                    int number = unit.getNumber();
                    if (number > 0) {
                        attackTurn.addAttack(new Attack(orderTerrFrom, orderTerrTo, unit.getNumber(), this.mGame.getPlayerName()));
                        updateUnitMoveAttackMap(number, unit);
                    }
                }
            } else {
                return;
            }
            // Done
            base_view.setVisibility(View.GONE);
            Toast.makeText(context, "Order recorded!", Toast.LENGTH_SHORT).show();
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
                unit_init_view.setVisibility(View.GONE);
                tech_view.setVisibility(View.GONE);
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
                unit_init_view.setVisibility(View.GONE);
                tech_view.setVisibility(View.GONE);
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
                unit_init_view.setVisibility(View.GONE);
                tech_view.setVisibility(View.GONE);
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
                unit_init_view.setVisibility(View.GONE);
                tech_view.setVisibility(View.GONE);
                base_view.setVisibility(View.VISIBLE);
                updateTerrInfo(territoryName);
                String title = "Unit: " + territoryName;
                unit_title.setText(title);
                mTouchEvent = TouchEvent.UNIT;
            }
        });

        end_turn_btn.setOnClickListener(v -> {
            waitForOthers();
            // Commit all moves and attacks
            this.mGame.addToTurnMap(this.mGame.getPlayerId(), moveTurn, attackTurn);
            // Send the game object to ClientIntentService
            Intent intent = new Intent();
            intent.setAction("RISC_SEND_TO_SERVER");
            intent.putExtra("game", this.mGame);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });

        // Add views to the layout
        framelayout.addView(mGameView);
        framelayout.addView(ui_view, params);
        framelayout.addView(order_view, params);
        framelayout.addView(init_view, params);

        waitForOthers();

        return framelayout;
    }

    private void updateUnitMoveAttackMap(int number, UnitDataModel unit) {
        if (unitMoveAttackMap.containsKey(orderTerrFrom)) {
            HashMap<String, Integer> map = unitMoveAttackMap.get(orderTerrFrom);
            if (map.containsKey(unit.getName())) {
                map.put(unit.getName(), map.get(unit.getName()) + number);
            } else {
                map.put(unit.getName(), number);
            }
        } else {
            HashMap<String, Integer> map = new HashMap<>();
            map.put(unit.getName(), number);
            unitMoveAttackMap.put(orderTerrFrom, map);
        }
    }


    private void updateTerrInfo(String terrName) {
        unitDataModels.clear();

        Territory territory = mGame.getMap().getTerritory(terrName);
        if (territory == null) {
            return;
        }

        HashMap<String, Integer> minusMap = unitMoveAttackMap.get(terrName);
        HashMap<String, Integer> map = new HashMap<>();
        for (Unit unit : territory.getUnits()) {
            map.put(Unit.getName(unit.getType()), map.getOrDefault(Unit.getName(unit.getType()), 0) + 1);
        }
        for (String type : map.keySet()) {
            if (minusMap != null && minusMap.containsKey(type)) {
                map.put(type, map.get(type) - minusMap.get(type));
            }
            unitDataModels.add(new UnitDataModel(type, map.get(type)));
        }

        unitAdapter.notifyDataSetChanged();
    }

    private void updateUnitInitInfo() {
        terrDataModels.clear();

        String playerName = mGame.getPlayerName();
        for (Territory territory : mGame.getMap().getTerritories()) {
            if (territory.getOwner().equals(playerName)) {
                terrDataModels.add(new TerrDataModel(territory.getName()));
            }
        }
        terrAdapter.notifyDataSetChanged();
    }

    private void waitForOthers() {
        base_view.setVisibility(View.VISIBLE);
        inner_order_view.setVisibility(View.GONE);
        global_prompt.setVisibility(View.VISIBLE);
    }

    private void assignText() {
        ui_view.findViewById(R.id.ui_side_bar_init).setVisibility(View.VISIBLE);
        init_base_view.setVisibility(View.VISIBLE);
        // Assign the text and image
        TextView init_player_name = init_view.findViewById(R.id.init_player_name);
        TextView init_house = init_view.findViewById(R.id.init_view_house);
        TextView ui_house = ui_view.findViewById(R.id.ui_player_school);
        ImageView init_house_img = init_view.findViewById(R.id.init_view_house_img);
        TextView ui_player_name = ui_view.findViewById(R.id.ui_player_name);

        init_player_name.setText(this.mGame.getPlayerName());
        ui_player_name.setText(this.mGame.getPlayerName());
        System.out.println("Player ID: " + this.mGame.getPlayerId());
        switch (this.mGame.getPlayerId()) {
            case 0:
                init_house.setText(context.getResources().getString(R.string.ravenclaw));
                init_house.setTextColor(getResources().getColor(R.color.ui_ravenclaw));
                init_house_img.setImageResource(R.drawable.house_rauenclaw);
                ui_house.setText(context.getResources().getString(R.string.ravenclaw));
                ui_house.setTextColor(getResources().getColor(R.color.ui_ravenclaw));
                break;
            case 3:
                init_house.setText(context.getResources().getString(R.string.hufflepuff));
                init_house.setTextColor(getResources().getColor(R.color.ui_hufflepuff));
                init_house_img.setImageResource(R.drawable.house_hufflepuff);
                ui_house.setText(context.getResources().getString(R.string.hufflepuff));
                ui_house.setTextColor(getResources().getColor(R.color.ui_hufflepuff));
                break;
            case 1:
                init_house.setText(context.getResources().getString(R.string.gryffindor));
                init_house.setTextColor(getResources().getColor(R.color.ui_gryffindor));
                init_house_img.setImageResource(R.drawable.house_gryffindor);
                ui_house.setText(context.getResources().getString(R.string.gryffindor));
                ui_house.setTextColor(getResources().getColor(R.color.ui_gryffindor));
                break;
            case 2:
                init_house.setText(context.getResources().getString(R.string.slytherin));
                init_house.setTextColor(getResources().getColor(R.color.ui_slytherin));
                init_house_img.setImageResource(R.drawable.house_slytherin);
                ui_house.setText(context.getResources().getString(R.string.slytherin));
                ui_house.setTextColor(getResources().getColor(R.color.ui_slytherin));
                break;
        }
        // Update the player values
        updatePlayerValues();
    }

    private void updatePlayerValues() {
        TextView ui_horn = ui_view.findViewById(R.id.ui_horn);
        TextView ui_coin = ui_view.findViewById(R.id.ui_coin);
        TextView ui_world_level = ui_view.findViewById(R.id.ui_world_level);

        Player player = this.mGame.getPlayer(this.mGame.getPlayerName());

        ui_coin.setText(String.valueOf(player.getCoins()));
        ui_horn.setText(String.valueOf(player.getHorns()));
        ui_world_level.setText(String.valueOf(player.getWorldLevel()));
    }

    private void assignUnits() {
        // Initialize the list view and adapter
        unit_init_listview = unit_init_view.findViewById(R.id.terr_list);
        terrDataModels = new ArrayList<>();
        terrAdapter = new TerrDataAdapter(terrDataModels, context);
        unit_init_listview.setAdapter(terrAdapter);

        TextView total_num = unit_init_view.findViewById(R.id.total_num_placed);
        TextView unit_placed_error_prompt = unit_init_view.findViewById(R.id.unit_placed_error_prompt);
        Button commit_init_btn = ui_view.findViewById(R.id.commit_init_btn);

        // Get player's territories list
        updateUnitInitInfo();

        // Update the cost when the number of units change
        terrAdapter.setTotalNumListener(() -> {
            int totalNum = terrAdapter.getTotalNum();
            total_num.setText(String.valueOf(totalNum));
            if (totalNum > 24) {
                total_num.setTextColor(getResources().getColor(R.color.error_prompt));
                commit_init_btn.setEnabled(false);
                unit_placed_error_prompt.setText(context.getResources().getString(R.string.unit_placed_fault2));
                unit_placed_error_prompt.setVisibility(View.VISIBLE);
            } else if (totalNum < 24) {
                total_num.setTextColor(getResources().getColor(R.color.error_prompt));
                commit_init_btn.setEnabled(false);
                unit_placed_error_prompt.setText(context.getResources().getString(R.string.unit_placed_fault));
                unit_placed_error_prompt.setVisibility(View.VISIBLE);
            } else {
                total_num.setTextColor(getResources().getColor(R.color.order_text_white));
                commit_init_btn.setEnabled(true);
                unit_placed_error_prompt.setVisibility(View.INVISIBLE);
            }
        });

        ui_view.findViewById(R.id.unit_init_btn).setOnClickListener(v -> {
            base_view.setVisibility(View.VISIBLE);
        });

        base_view.findViewById(R.id.inflate_init_unit).findViewById(R.id.init_unit_back_btn).setOnClickListener(v -> {
            base_view.setVisibility(View.GONE);
        });

        commit_init_btn.setOnClickListener(v -> {
            // Check if the number of units placed is correct
            if (terrAdapter.getTotalNum() != 24) {
                Toast.makeText(context, context.getResources().getString(R.string.unit_placed_fault), Toast.LENGTH_SHORT).show();
                return;
            }
            for (TerrDataModel terr : terrDataModels) {
                if (terr.getNumber() > 0) {
                    for (int i = 0; i < terr.getNumber(); i++) {
                        this.mGame.getMap().getTerritory(terr.getName()).addUnit(new Unit("Gnome"));
                    }
                }
            }

            // Submit game object
            waitForOthers();
            // Send the game object to ClientIntentService
            Intent intent = new Intent();
            intent.setAction("RISC_SEND_TO_SERVER");
            intent.putExtra("game", this.mGame);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });


    }

}