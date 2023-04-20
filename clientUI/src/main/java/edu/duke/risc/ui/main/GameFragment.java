package edu.duke.risc.ui.main;

import static edu.duke.risc.ui.state.TouchEvent.ATTACK;
import static edu.duke.risc.ui.state.TouchEvent.MOVE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import edu.duke.risc.R;
import edu.duke.risc.client.ClientIntentService;
import edu.duke.risc.client.ClientResultReceiver;
import edu.duke.risc.ui.adapter.HorcruxDataAdapter;
import edu.duke.risc.ui.adapter.TerrDataAdapter;
import edu.duke.risc.ui.adapter.UnitDataAdapter;
import edu.duke.risc.ui.adapter.UnitSpinnerAdapter;
import edu.duke.risc.ui.adapter.UnitUpgradeDataAdapter;
import edu.duke.risc.ui.model.HorcruxDataModel;
import edu.duke.risc.ui.model.TerrDataModel;
import edu.duke.risc.ui.model.UnitDataModel;
import edu.duke.risc.ui.model.UnitSpinnerDataModel;
import edu.duke.risc.ui.model.UnitUpgradeDataModel;
import edu.duke.risc.ui.state.TouchEvent;
import edu.duke.risc.ui.view.GameView;
import edu.duke.shared.Game;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Horcrux;
import edu.duke.shared.player.Player;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Move;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

public class GameFragment extends Fragment implements ClientResultReceiver.AppReceiver {
    // GameView
    private GameView mGameView;
    // Game
    private Game mGame;
    // Player
    private Player mPlayer;
    // Context
    private Context context;
    // Player color
    private int playerColor;
    // TouchEvent
    private TouchEvent mTouchEvent;
    // Units in territory data model
    ArrayList<UnitDataModel> unitDataModels;
    // Territories data model
    ArrayList<TerrDataModel> terrDataModels;
    // Unit upgrade data model
    ArrayList<UnitUpgradeDataModel> unitUpgradeDataModels;
    // Unit spinner data model source territory
    ArrayList<UnitSpinnerDataModel> unitSpinnerDataModels;
    // Unit spinner data model destination territory
    ArrayList<UnitSpinnerDataModel> unitSpinnerToDataModels;
    // Horcrux data model
    ArrayList<HorcruxDataModel> horcruxDataModels;
    // List of units to put in attack and move order
    ListView move_attack_listview;
    // List of units to put in upgrade order
    ListView unit_listview;
    // List of territories
    ListView unit_init_listview;
    // List of horcruxes
    ListView horcrux_listview;
    // Unit data adapter
    private UnitDataAdapter unitAdapter;
    // Territory data adapter
    private TerrDataAdapter terrAdapter;
    // Unit upgrade data adapter
    private UnitUpgradeDataAdapter unitUpgradeAdapter;
    // Unit spinner adapter source
    private UnitSpinnerAdapter unitSpinnerAdapter;
    // Unit spinner adapter destination
    private UnitSpinnerAdapter unitSpinnerToAdapter;
    // Horcrux data adapter
    private HorcruxDataAdapter horcruxAdapter;
    // Touch event source territory
    private String orderTerrFrom;
    // Touch event destination territory
    private String orderTerrTo;
    // Shadow view
    ConstraintLayout shadow_view;
    // Base view
    ConstraintLayout base_view;
    // Order view
    View order_view;
    // Move attack view
    View inner_order_view;
    // Init view
    View init_view;
    // Winner view
    View winner_view;
    // UI view
    View ui_view;
    // Global prompt view
    ViewGroup global_prompt;
    // Global prompt dialog view
    ViewGroup global_dialog;
    // Move attack view
    ViewGroup move_attack_view;
    // Property view
    ViewGroup prop_view;
    // Unit view
    ViewGroup unit_view;
    // Unit init view
    ViewGroup unit_init_view;
    // Technology view
    ViewGroup tech_view;
    // Item view
    ViewGroup item_view;
    // Init base view
    ViewGroup init_base_view;
    // Winner base view
    ViewGroup winner_base_view;
    // Source territory spinner
    Spinner unit_from_spinner;
    // Destination territory spinner
    Spinner unit_to_spinner;
    // Number of unit selected
    SeekBar unit_num;
    // Upgrade button
    Button unit_upgrade_btn;
    // Spinner selected unit from
    String selected_from;
    // Spinner selected unit to
    String selected_to;
    // If the player has lost
    boolean isLost = false;
    // Current turn coins expense
    int currentCoinExpense = 0;
    // Current turn horns expense
    int currentHornExpense = 0;
    // Move turn list
    private MoveTurn moveTurn;
    // Attack turn list
    private AttackTurn attackTurn;
    // If the player has upgraded the world level
    private boolean isUpgradedWorldLevel = false;
    // Move attack list
    private HashMap<String, HashMap<String, Integer>> unitMoveAttackMap;
    // Upgrade list
    private HashMap<String, HashMap<String, Integer>> unitUpgradeMap;
    // If has shown the horcrux result
    private boolean hasHorcruxResult = false;
    // If has shown the horcrux affect result
    private boolean hasHorcruxAffectResult = false;

    /**
     * Setup the frame layout
     *
     * @return FrameLayout
     */
    public static GameFragment newInstance() {
        return new GameFragment();
    }

    /**
     * onCreate
     *
     * @param savedInstanceState If the fragment is being re-created from
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * onCreateView
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return setupFrameLayout(requireActivity().getApplicationContext());
    }

    /**
     * onViewCreated
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        registerService();
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        super.onPause();
        mGameView.pause();
    }

    /**
     * onResume
     */
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
            // Clear the flag
            this.isUpgradedWorldLevel = false;
            this.hasHorcruxResult = false;
            this.hasHorcruxAffectResult = false;
            // Get new game object
            this.mGame = (Game) resultData.getSerializable("game");
            this.mPlayer = mGame.getPlayer(mGame.getPlayerName());
            System.out.println("Game state: " + mGame.getGameState());
            switch (mGame.getGameState()) {
                // The first two states should be done in the login page
                case WAITING_TO_JOIN:
                case READY_TO_INIT_NAME:
                case READY_TO_INIT_UNITS:
                    // Set player's color
                    switch (mPlayer.getHouse()) {
                        case GRYFFINDOR:
                            this.playerColor = getResources().getColor(R.color.ui_gryffindor);
                            break;
                        case HUFFLEPUFF:
                            this.playerColor = getResources().getColor(R.color.ui_hufflepuff);
                            break;
                        case RAVENCLAW:
                            this.playerColor = getResources().getColor(R.color.ui_ravenclaw);
                            break;
                        case SLYTHERIN:
                            this.playerColor = getResources().getColor(R.color.ui_slytherin);
                            break;
                    }
                    mGameView.initColorMapping(this.mGame.getPlayerList());
                    mGameView.updateMap(this.mGame.getMap());
                    mGameView.updateGame(this.mGame);
                    // Assign text
                    assignText();
                    // Assign units
                    assignUnits();
                    global_prompt.setVisibility(View.GONE);
                    global_dialog.setVisibility(View.GONE);
                    break;
                case TURN_BEGIN:
                    currentCoinExpense = 0;
                    currentHornExpense = 0;
                    unitMoveAttackMap = new HashMap<>();
                    unitUpgradeMap = new HashMap<>();
                    // Update the game view
                    moveTurn = new MoveTurn(this.mGame.getMap(), this.mGame.getTurn(), this.mGame.getPlayerName());
                    attackTurn = new AttackTurn(this.mGame.getMap(), this.mGame.getTurn(), this.mGame.getPlayerName());
                    mGameView.updateMap(this.mGame.getMap());
                    mGameView.updateGame(this.mGame);
                    if (isLost) {
                        commit();
                        this.ui_view.findViewById(R.id.ui_side_bar).setVisibility(View.GONE);
                        TextView is_watching = this.ui_view.findViewById(R.id.ui_watching);
                        String text = getResources().getString(R.string.you_are_watching) + " Turn: " + this.mGame.getTurn();
                        is_watching.setText(text);
                    } else {
                        updatePlayerValues();
                    }
                    ui_view.findViewById(R.id.ui_side_bar_init).setVisibility(View.GONE);
                    ui_view.findViewById(R.id.ui_side_bar).setVisibility(View.VISIBLE);
                    showDialog(getResources().getString(R.string.turn), String.valueOf(this.mGame.getTurn()), getResources().getString(R.string.begins));
                    break;
                case TURN_END:
                    if (!this.isLost && this.mGame.isLoser(this.mGame.getPlayerId())) {
                        assignWinner(false);
                        this.isLost = true;
                    }
                    break;
                case GAME_OVER:
                    assignWinner(this.mGame.getWinnerId() == this.mGame.getPlayerId());
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

    /**
     * Close the intent service
     */
    private void closeService() {
        Intent intent = new Intent(requireActivity().getApplicationContext(), ClientIntentService.class);
        requireActivity().stopService(intent);
    }

    /**
     * Setup the frame layout
     *
     * @param context The context
     * @return The frame layout
     */
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
        item_view = order_view.findViewById(R.id.inflate_item_view);

        // Load the global prompt view
        LayoutInflater inflater_ui = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ui_view = inflater_ui.inflate(R.layout.game_ui, nullParent, false);

        // Load the init view
        LayoutInflater inflater_init = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init_view = inflater_init.inflate(R.layout.init_view, nullParent, false);

        // Load the end game view
        LayoutInflater inflater_end = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        winner_view = inflater_end.inflate(R.layout.winner_view, nullParent, false);

        // Initialize the list view and adapter
        move_attack_listview = move_attack_view.findViewById(R.id.list);
        unit_listview = unit_view.findViewById(R.id.unit_list);
        horcrux_listview = item_view.findViewById(R.id.item_view_list);
        unitDataModels = new ArrayList<>();
        unitUpgradeDataModels = new ArrayList<>();
        horcruxDataModels = new ArrayList<>();
        unitAdapter = new UnitDataAdapter(unitDataModels, context);
        unitUpgradeAdapter = new UnitUpgradeDataAdapter(unitUpgradeDataModels, context);
        horcruxAdapter = new HorcruxDataAdapter(horcruxDataModels, context);
        move_attack_listview.setAdapter(unitAdapter);
        unit_listview.setAdapter(unitUpgradeAdapter);
        horcrux_listview.setAdapter(horcruxAdapter);


        // Initialize widgets in the move_attack view
        Button commit_btn = move_attack_view.findViewById(R.id.attack_btn);
        TextView cost_error_prompt = move_attack_view.findViewById(R.id.cost_error_prompt);
        TextView view_title = move_attack_view.findViewById(R.id.view_title);
        TextView total_cost = move_attack_view.findViewById(R.id.total_cost);
        // Prop
        TextView prop_title = prop_view.findViewById(R.id.prop_title);
        TextView prop_owner = prop_view.findViewById(R.id.prop_owner);
        TextView prop_desc = prop_view.findViewById(R.id.prop_desc);
        TextView prop_type = prop_view.findViewById(R.id.prop_type);
        ImageView prop_img = prop_view.findViewById(R.id.prop_img);
        Button prop_btn = prop_view.findViewById(R.id.prop_back_btn);
        // Unit
        TextView unit_title = unit_view.findViewById(R.id.unit_title);
        TextView unit_selected_num = unit_view.findViewById(R.id.unit_num_text);
        unit_num = unit_view.findViewById(R.id.unit_upgrade_num_input);
        Button unit_btn = unit_view.findViewById(R.id.unit_back_btn);
        unit_upgrade_btn = unit_view.findViewById(R.id.unit_upgrade_btn);
        unit_from_spinner = unit_view.findViewById(R.id.unit_from_spinner);
        unit_to_spinner = unit_view.findViewById(R.id.unit_to_spinner);

        unitSpinnerDataModels = new ArrayList<>();
        unitSpinnerToDataModels = new ArrayList<>();
        unitSpinnerAdapter = new UnitSpinnerAdapter(unitSpinnerDataModels, context);
        unitSpinnerToAdapter = new UnitSpinnerAdapter(unitSpinnerToDataModels, context);
        unit_from_spinner.setAdapter(unitSpinnerAdapter);
        unit_to_spinner.setAdapter(unitSpinnerToAdapter);

        // Initialize widgets in common use
        shadow_view = order_view.findViewById(R.id.shadow_view);
        base_view = order_view.findViewById(R.id.base_view);
        global_prompt = order_view.findViewById(R.id.global_prompt);
        global_dialog = order_view.findViewById(R.id.global_dialog);
        inner_order_view = order_view.findViewById(R.id.inner_order_view);

        // Initialize widgets in ui surface
        Button chat_btn = ui_view.findViewById(R.id.ui_chat);
        Button end_turn_btn = ui_view.findViewById(R.id.end_turn);
        Button item_btn = ui_view.findViewById(R.id.item);
        Button tech_btn = ui_view.findViewById(R.id.tech);
        ui_view.findViewById(R.id.ui_side_bar).setVisibility(View.GONE);

        // Initialize widgets in init view
        Button init_btn = init_view.findViewById(R.id.init_view_accept);
        init_base_view = init_view.findViewById(R.id.init_base);
        init_base_view.setVisibility(View.GONE);

        // Initialize widgets in winner view
        Button winner_btn = winner_view.findViewById(R.id.winner_btn);
        winner_base_view = winner_view.findViewById(R.id.winner_base);
        winner_base_view.setVisibility(View.GONE);

        // Initialize widgets in tech view
        TextView tech_error_prompt = tech_view.findViewById(R.id.tech_error_prompt);
        TextView tech_level_value = tech_view.findViewById(R.id.tech_level_value);
        TextView tech_level_desc = tech_view.findViewById(R.id.tech_level_desc);
        Button tech_upgrade_btn = tech_view.findViewById(R.id.tech_upgrade_btn);
        Button tech_back_btn = tech_view.findViewById(R.id.tech_back_btn);
        ImageView tech_img = tech_view.findViewById(R.id.tech_img);

        // Initialize widgets in item view
        Button item_back_btn = item_view.findViewById(R.id.item_view_back_btn);

        unit_from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                UnitSpinnerDataModel unitSpinnerDataModel = unitSpinnerDataModels.get(i);
                selected_from = unitSpinnerDataModel.getName();
                unitSpinnerToDataModels.clear();

                int worldLevel = mPlayer.getWorldLevel();
                ArrayList<String> nextLevel = Unit.getNextLevel(Unit.convertStringToUnitType(selected_from), worldLevel);
                if (nextLevel != null) {
                    for (String next : nextLevel) {
                        unitSpinnerToDataModels.add(new UnitSpinnerDataModel(next));
                    }
                }
                unitSpinnerToAdapter.notifyDataSetChanged();
                if (unitSpinnerToDataModels.size() > 0) {
                    // Update seek bar range
                    for (int j = 0; j < unitDataModels.size(); j++) {
                        if (unitDataModels.get(j).getName().equals(selected_from)) {
                            selected_to = unitSpinnerToDataModels.get(0).getName();
                            unit_num.setMax(unitDataModels.get(j).getMax());
                            unit_num.setMin(1);
                            break;
                        }
                    }
                    unit_to_spinner.setSelection(0);
                } else {
                    unit_upgrade_btn.setEnabled(false);
                    unit_num.setEnabled(false);
                    unit_upgrade_btn.setText(getResources().getString(R.string.tech_fault3));
                    unit_upgrade_btn.setTextColor(getResources().getColor(R.color.error_prompt));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        unit_to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                UnitSpinnerDataModel unitSpinnerDataModel = unitSpinnerToDataModels.get(i);
                selected_to = unitSpinnerDataModel.getName();
                // Update seek bar range
                for (int j = 0; j < unitDataModels.size(); j++) {
                    if (unitDataModels.get(j).getName().equals(selected_from)) {
                        unit_num.setMax(unitDataModels.get(j).getMax());
                        unit_num.setMin(1);
                        break;
                    }
                }
                unit_num.setProgress(1);
                int cost = mGame.getMap().getTerritory(orderTerrFrom).getUpdateValue(selected_from, selected_to);
                String cost_s = "Upgrade: " + cost + " horns";
                unit_upgrade_btn.setText(cost_s);
                boolean flag = false;
                if (cost <= mPlayer.getHorns() - currentHornExpense) {
                    if (mGame.getMap().getTerritory(orderTerrFrom).getOwner().equals(mGame.getPlayerName())) {
                        unit_upgrade_btn.setEnabled(true);
                        unit_num.setEnabled(true);
                        unit_upgrade_btn.setTextColor(getResources().getColor(R.color.order_text));
                    } else {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
                if (flag) {
                    unit_upgrade_btn.setEnabled(false);
                    unit_num.setEnabled(false);
                    unit_upgrade_btn.setTextColor(getResources().getColor(R.color.error_prompt));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
            base_view.findViewById(R.id.inflate_item_view).setVisibility(View.GONE);
            base_view.findViewById(R.id.global_prompt).setVisibility(View.GONE);
            base_view.findViewById(R.id.global_dialog).setVisibility(View.GONE);
        });

        winner_btn.setOnClickListener(v -> winner_base_view.setVisibility(View.GONE));

        item_btn.setOnClickListener(v -> {
            move_attack_view.setVisibility(View.GONE);
            prop_view.setVisibility(View.GONE);
            unit_view.setVisibility(View.GONE);
            unit_init_view.setVisibility(View.GONE);
            tech_view.setVisibility(View.GONE);
            item_view.setVisibility(View.VISIBLE);
            base_view.setVisibility(View.VISIBLE);
            // Traverse all items in the player
            horcruxDataModels.clear();
            for (Horcrux horcrux : Horcrux.values()) {
                int num = mPlayer.getHorcruxStorage(horcrux);
                if (num > 0) {
                    horcruxDataModels.add(new HorcruxDataModel(horcrux, num));
                }
            }
            horcruxAdapter.notifyDataSetChanged();
        });

        tech_btn.setOnClickListener(v -> {
            move_attack_view.setVisibility(View.GONE);
            prop_view.setVisibility(View.GONE);
            unit_view.setVisibility(View.GONE);
            unit_init_view.setVisibility(View.GONE);
            tech_view.setVisibility(View.VISIBLE);
            item_view.setVisibility(View.GONE);
            base_view.setVisibility(View.VISIBLE);
            int tech_level = mPlayer.getWorldLevel();
            String upgrade = "Upgrade: " + Player.upgradeCost(tech_level + 1) + " horns";
            tech_upgrade_btn.setText(upgrade);
            if ((Player.upgradeCost(tech_level + 1) > mPlayer.getHorns() - currentHornExpense) || this.isUpgradedWorldLevel) {
                tech_error_prompt.setVisibility(View.VISIBLE);
                tech_upgrade_btn.setEnabled(false);
                tech_upgrade_btn.setTextColor(getResources().getColor(R.color.error_prompt));
                if (this.isUpgradedWorldLevel) {
                    tech_error_prompt.setText(getResources().getString(R.string.tech_fault2));
                    tech_upgrade_btn.setText(getResources().getString(R.string.tech_fault3));
                }
            } else if (tech_level == 6) {
                tech_error_prompt.setText(getResources().getString(R.string.tech_fault4));
                tech_upgrade_btn.setText(getResources().getString(R.string.tech_fault3));
                tech_upgrade_btn.setTextColor(getResources().getColor(R.color.error_prompt));
            } else {
                tech_error_prompt.setVisibility(View.INVISIBLE);
                tech_upgrade_btn.setEnabled(true);
                tech_upgrade_btn.setTextColor(getResources().getColor(R.color.order_text));
            }
            switch (tech_level) {
                case 1:
                    tech_level_value.setText(getResources().getString(R.string.one));
                    tech_img.setImageResource(R.drawable.wand_1);
                    tech_level_desc.setText(getResources().getString(R.string.tech_bonus_1));
                    break;
                case 2:
                    tech_level_value.setText(getResources().getString(R.string.two));
                    tech_img.setImageResource(R.drawable.wand_2);
                    tech_level_desc.setText(getResources().getString(R.string.tech_bonus_2));
                    break;
                case 3:
                    tech_level_value.setText(getResources().getString(R.string.three));
                    tech_img.setImageResource(R.drawable.wand_3);
                    tech_level_desc.setText(getResources().getString(R.string.tech_bonus_3));
                    break;
                case 4:
                    tech_level_value.setText(getResources().getString(R.string.four));
                    tech_img.setImageResource(R.drawable.wand_4);
                    tech_level_desc.setText(getResources().getString(R.string.tech_bonus_4));
                    break;
                case 5:
                    tech_level_value.setText(getResources().getString(R.string.five));
                    tech_img.setImageResource(R.drawable.wand_5);
                    tech_level_desc.setText(getResources().getString(R.string.tech_bonus_5));
                    break;
                case 6:
                    tech_level_value.setText(getResources().getString(R.string.six));
                    tech_img.setImageResource(R.drawable.wand_6);
                    tech_level_desc.setText(getResources().getString(R.string.tech_bonus_6));
                    break;
                default:
                    tech_level_value.setText(getResources().getString(R.string.zero));
                    tech_img.setImageResource(R.drawable.wand_101);
                    break;
            }
        });

        tech_back_btn.setOnClickListener(v -> base_view.setVisibility(View.GONE));

        item_back_btn.setOnClickListener(v -> base_view.setVisibility(View.GONE));

        tech_upgrade_btn.setOnClickListener(v -> {
            showDialog(getResources().getString(R.string.tech_upgrade_recorded), "", "");
            this.isUpgradedWorldLevel = true;
            this.currentHornExpense += Player.upgradeCost(mPlayer.getWorldLevel() + 1);
            updatePlayerValues();
        });

        // Force end game
        ui_view.findViewById(R.id.ui_player_name_label).setOnClickListener(v -> {
            this.mGame.forceEndGame();
            showDialog(getResources().getString(R.string.surrendered), "", "");
        });

        unit_num.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                unit_selected_num.setText(String.valueOf(i));
                System.out.println("Upgrade in terr: " + orderTerrFrom);
                int cost = mGame.getMap().getTerritory(orderTerrFrom).getUpdateValue(selected_from, selected_to) * i;
                String cost_s = "Upgrade: " + cost + " horns";
                unit_upgrade_btn.setText(cost_s);
                boolean flag = false;
                if (mPlayer.getCoins() > cost) {
                    if (mGame.getMap().getTerritory(orderTerrFrom).getOwner().equals(mGame.getPlayerName())) {
                        unit_upgrade_btn.setEnabled(true);
                        unit_num.setEnabled(true);
                        unit_upgrade_btn.setTextColor(getResources().getColor(R.color.order_text));
                    } else {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
                if (flag) {
                    unit_upgrade_btn.setEnabled(false);
                    unit_upgrade_btn.setTextColor(getResources().getColor(R.color.error_prompt));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        unit_upgrade_btn.setOnClickListener(v -> {
            int num = unit_num.getProgress();
            assert selected_from != null;
            assert selected_to != null;
            // Upgrade the unit
            int cost = this.mGame.getMap().getTerritory(orderTerrFrom).upgradeUnit(selected_from, selected_to, num);
            this.mPlayer.setExpenseHorns(cost);

            // Update cost on top bar display
            updatePlayerValues();
            // Update the unit number
            updateUnitUpgradeMap(-num, selected_from);
            updateUnitUpgradeMap(num, selected_to);
            showDialog(getResources().getString(R.string.unit_upgrade_success), "", "");
        });


        prop_btn.setOnClickListener(v -> base_view.setVisibility(View.GONE));

        unit_btn.setOnClickListener(v -> base_view.setVisibility(View.GONE));


        // Update the cost when the number of units change
        unitAdapter.setCostListener(() -> {
            int distance = this.mGame.getMap().getShortestDistance(orderTerrFrom, orderTerrTo);
            if (distance == -1) {
                distance = this.mGame.getMap().getDistance(orderTerrTo, orderTerrFrom);
            }
            int cost = this.mGame.calculateOrderCost(distance, unitAdapter.getTotalNumber());
            String cost_s = cost + " coins";
            total_cost.setText(cost_s);
            if (cost > mPlayer.getCoins()) {
                total_cost.setTextColor(getResources().getColor(R.color.error_prompt));
                commit_btn.setEnabled(false);
                cost_error_prompt.setVisibility(View.VISIBLE);
            } else {
                total_cost.setTextColor(getResources().getColor(R.color.order_text_white));
                commit_btn.setEnabled(true);
                cost_error_prompt.setVisibility(View.INVISIBLE);
            }
        });

        // Update the item used listener
        horcruxAdapter.setUseListener((item, num) -> {
            switch (item) {
                case HAT:
                    showDialog(getResources().getString(R.string.use_diadem_p1), getResources().getString(R.string.use_diadem_p2), getResources().getString(R.string.use_diadem_p3));
                    mPlayer.addToHorcruxUsage(Horcrux.HAT, 1);
                    mPlayer.removeFromHorcruxStorage(Horcrux.HAT, 1);
                    break;
                case CUP:
                    showDialog("", getResources().getString(R.string.use_cup_p1), getResources().getString(R.string.use_cup_p2));
                    mPlayer.addToHorcruxUsage(Horcrux.CUP, 1);
                    mPlayer.removeFromHorcruxStorage(Horcrux.CUP, 1);
                    break;
                case RING:
                    showDialog(getResources().getString(R.string.use_stone_p1), getResources().getString(R.string.use_stone_p2), getResources().getString(R.string.use_stone_p3));
                    mPlayer.addToHorcruxUsage(Horcrux.RING, 1);
                    mPlayer.removeFromHorcruxStorage(Horcrux.RING, 1);
                    break;
                case DIARY:
                    showDialog(getResources().getString(R.string.use_diary_p1), getResources().getString(R.string.use_diary_p2), "");
                    mPlayer.addToHorcruxUsage(Horcrux.DIARY, 1);
                    mPlayer.removeFromHorcruxStorage(Horcrux.DIARY, 1);
                    break;
                case LOCKET:
                    showDialog("", getResources().getString(R.string.use_locket_p1), getResources().getString(R.string.use_locket_p2));
                    mPlayer.addToHorcruxUsage(Horcrux.LOCKET, 1);
                    mPlayer.removeFromHorcruxStorage(Horcrux.LOCKET, 1);
                    break;
                case SNAKE:
                    showDialog(getResources().getString(R.string.use_snake_p1), getResources().getString(R.string.use_snake_p2), "");
                    mPlayer.addToHorcruxUsage(Horcrux.SNAKE, 1);
                    mPlayer.removeFromHorcruxStorage(Horcrux.SNAKE, 1);
                    break;
            }
        });

        // Commit move or attack orders
        commit_btn.setOnClickListener(v -> {
            if (mTouchEvent == MOVE) {
                for (UnitDataModel unit : unitDataModels) {
                    String name = unit.getName();
                    int number = unit.getNumber();
                    UnitType type = Unit.convertStringToUnitType(name);
                    HashMap<UnitType, Integer> list = new HashMap<>();
                    list.put(type, number);
                    if (number > 0) {
                        moveTurn.addMove(new Move(orderTerrFrom, orderTerrTo, list, this.mGame.getPlayerName()));
                        updateUnitMoveAttackMap(number, unit);
                        int cost = this.mGame.calculateOrderCost(this.mGame.getMap().getShortestDistance(orderTerrFrom, orderTerrTo), number);
                        this.mPlayer.setExpenseCoins(cost);
                    }
                }
                updatePlayerValues();
            } else if (mTouchEvent == ATTACK) {
                for (UnitDataModel unit : unitDataModels) {
                    String name = unit.getName();
                    int number = unit.getNumber();
                    UnitType type = Unit.convertStringToUnitType(name);
                    HashMap<UnitType, Integer> list = new HashMap<>();
                    list.put(type, number);
                    if (number > 0) {
                        attackTurn.addAttack(new Attack(orderTerrFrom, orderTerrTo, list, this.mGame.getPlayerName()));
                        updateUnitMoveAttackMap(number, unit);
                        int cost = this.mGame.calculateOrderCost(this.mGame.getMap().getDistance(orderTerrFrom, orderTerrTo), number);
                        this.mPlayer.setExpenseCoins(cost);
                    }
                }
                updatePlayerValues();
            } else {
                return;
            }
            // Done
            showDialog(getResources().getString(R.string.order_recorded), "", "");
        });

        // Click the shadow area to close the order view
        shadow_view.setOnClickListener(v -> base_view.setVisibility(View.GONE));

        mGameView.setEventListener(new GameView.EventListener() {
            @Override
            public void onMoveOrder(String terrFrom, String terrTo) {
                // Set touch event
                mTouchEvent = MOVE;
                // Update
                move_attack_view.setVisibility(View.VISIBLE);
                prop_view.setVisibility(View.GONE);
                unit_view.setVisibility(View.GONE);
                unit_init_view.setVisibility(View.GONE);
                tech_view.setVisibility(View.GONE);
                item_view.setVisibility(View.GONE);
                base_view.setVisibility(View.VISIBLE);
                orderTerrFrom = terrFrom;
                orderTerrTo = terrTo;
                updateTerrInfo(terrFrom);
                String title = "Move from " + terrFrom + " to " + terrTo;
                view_title.setText(title);
                commit_btn.setText(context.getResources().getString(R.string.move));
            }


            @Override
            public void onAttackOrder(String terrFrom, String terrTo) {
                mTouchEvent = ATTACK;
                move_attack_view.setVisibility(View.VISIBLE);
                prop_view.setVisibility(View.GONE);
                unit_view.setVisibility(View.GONE);
                unit_init_view.setVisibility(View.GONE);
                tech_view.setVisibility(View.GONE);
                item_view.setVisibility(View.GONE);
                base_view.setVisibility(View.VISIBLE);
                orderTerrFrom = terrFrom;
                orderTerrTo = terrTo;
                updateTerrInfo(terrFrom);
                String title = "Attack from " + terrFrom + " to " + terrTo;
                view_title.setText(title);
                commit_btn.setText(context.getResources().getString(R.string.attack));
            }

            @Override
            public void onPropOrder(String territoryName) {
                mTouchEvent = TouchEvent.PROP;
                move_attack_view.setVisibility(View.GONE);
                prop_view.setVisibility(View.VISIBLE);
                unit_view.setVisibility(View.GONE);
                unit_init_view.setVisibility(View.GONE);
                tech_view.setVisibility(View.GONE);
                item_view.setVisibility(View.GONE);
                base_view.setVisibility(View.VISIBLE);
                orderTerrFrom = territoryName;
                updateTerrInfo(territoryName);
                prop_title.setText(territoryName);
                Territory t = mGame.getMap().getTerritory(territoryName);
                String owner = "Owner: " + t.getOwner();
                prop_owner.setText(owner);
                String type = "Type: " + t.getType_HPStyle();
                prop_type.setText(type);
                prop_desc.setText(t.getDetails_HPStyle());
                switch (t.getType()) {
                    case "plain":
                        prop_img.setImageResource(R.drawable.terr_plain);
                        break;
                    case "cliff":
                        prop_img.setImageResource(R.drawable.terr_cliff);
                        break;
                    case "canyon":
                        prop_img.setImageResource(R.drawable.terr_canyon);
                        break;
                    case "desert":
                        prop_img.setImageResource(R.drawable.terr_desert);
                        break;
                    case "forest":
                        prop_img.setImageResource(R.drawable.terr_forest);
                        break;
                    case "wetland":
                        prop_img.setImageResource(R.drawable.terr_wetland);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onUnitOrder(String territoryName) {
                mTouchEvent = TouchEvent.UNIT;
                move_attack_view.setVisibility(View.GONE);
                prop_view.setVisibility(View.GONE);
                unit_view.setVisibility(View.VISIBLE);
                unit_init_view.setVisibility(View.GONE);
                tech_view.setVisibility(View.GONE);
                item_view.setVisibility(View.GONE);
                base_view.setVisibility(View.VISIBLE);
                orderTerrFrom = territoryName;
                updateTerrInfo(territoryName);
                updateUnitUpgradeInfo(territoryName);
                System.out.println("Player name: " + mGame.getPlayerName() + ", Territory owner: " + mGame.getMap().getTerritory(territoryName).getOwner());
                if (!mGame.getMap().getTerritory(territoryName).getOwner().equals(mGame.getPlayerName())) {
                    unit_upgrade_btn.setText(getResources().getString(R.string.tech_fault3));
                    unit_upgrade_btn.setTextColor(getResources().getColor(R.color.error_prompt));
                    unit_upgrade_btn.setEnabled(false);
                    unit_num.setEnabled(false);
                }
                String title = "Unit in " + territoryName;
                unit_title.setText(title);
            }
        });

        end_turn_btn.setOnClickListener(v -> {
            showWaitTexts();
            // execute world level events
            if (this.isUpgradedWorldLevel) {
                mPlayer.willUpgradeWorldLevel = true;
            }
            commit();
        });

        // Add views to the layout
        framelayout.addView(mGameView);
        framelayout.addView(ui_view, params);
        framelayout.addView(order_view, params);
        framelayout.addView(init_view, params);
        framelayout.addView(winner_view, params);

        showWaitTexts();

        return framelayout;
    }

    /**
     * Commit all moves and attacks
     */
    private void commit() {
        // Commit all moves and attacks
        this.mGame.addToTurnMap(this.mGame.getPlayerId(), moveTurn, attackTurn);
        // Send the game object to ClientIntentService
        Intent intent = new Intent();
        intent.setAction("RISC_SEND_TO_SERVER");
        intent.putExtra("game", this.mGame);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * Update units when a move or attack is made
     *
     * @param number number of units
     * @param unit   unit data model
     */
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

    /**
     * Update units when a unit is upgraded
     *
     * @param number   number of units
     * @param unitName unit name
     */
    private void updateUnitUpgradeMap(int number, String unitName) {
        if (unitUpgradeMap.containsKey(orderTerrFrom)) {
            HashMap<String, Integer> map = unitUpgradeMap.get(orderTerrFrom);
            if (map.containsKey(unitName)) {
                map.put(unitName, map.get(unitName) + number);
            } else {
                map.put(unitName, number);
            }
        } else {
            HashMap<String, Integer> map = new HashMap<>();
            map.put(unitName, number);
            unitUpgradeMap.put(orderTerrFrom, map);
        }
    }

    /**
     * Update spinner
     *
     * @param terrName territory name
     */
    private void updateUnitUpgradeInfo(String terrName) {
        unitSpinnerDataModels.clear();

        for (UnitDataModel unit : unitDataModels) {
            unitSpinnerDataModels.add(new UnitSpinnerDataModel(unit.getName()));
        }

        if (unitSpinnerDataModels.size() != 0) {
            UnitSpinnerDataModel unitSpinnerDataModel = unitSpinnerDataModels.get(0);
            selected_from = unitSpinnerDataModel.getName();
            unitSpinnerToDataModels.clear();

            int worldLevel = mPlayer.getWorldLevel();
            ArrayList<String> nextLevel = Unit.getNextLevel(Unit.convertStringToUnitType(selected_from), worldLevel);
            if (nextLevel != null) {
                for (String next : nextLevel) {
                    unitSpinnerToDataModels.add(new UnitSpinnerDataModel(next));
                }
            }
            unitSpinnerToAdapter.notifyDataSetChanged();
            if (unitSpinnerToDataModels.size() > 0) {
                UnitSpinnerDataModel unitSpinnerToDataModel = unitSpinnerToDataModels.get(0);
                selected_to = unitSpinnerToDataModel.getName();
                // Update seek bar range
                for (int j = 0; j < unitDataModels.size(); j++) {
                    if (unitDataModels.get(j).getName().equals(selected_from)) {
                        unit_num.setMax(unitDataModels.get(j).getMax());
                        unit_num.setMin(1);
                        break;
                    }
                }
                unit_num.setProgress(1);
                int cost = mGame.getMap().getTerritory(orderTerrFrom).getUpdateValue(selected_from, selected_to);
                String cost_s = "Upgrade: " + cost + " horns";
                unit_upgrade_btn.setText(cost_s);
                boolean flag = false;
                if (cost <= mPlayer.getHorns() - currentHornExpense) {
                    if (mGame.getMap().getTerritory(terrName).getOwner().equals(mGame.getPlayerName())) {
                        unit_upgrade_btn.setEnabled(true);
                        unit_num.setEnabled(true);
                        unit_upgrade_btn.setTextColor(getResources().getColor(R.color.order_text));
                    } else {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
                if (flag) {
                    unit_upgrade_btn.setEnabled(false);
                    unit_num.setEnabled(false);
                    unit_upgrade_btn.setTextColor(getResources().getColor(R.color.error_prompt));
                }
            } else {
                unit_upgrade_btn.setEnabled(false);
                unit_num.setEnabled(false);
                unit_upgrade_btn.setText(getResources().getString(R.string.tech_fault3));
                unit_upgrade_btn.setTextColor(getResources().getColor(R.color.error_prompt));
            }
        } else {
            unitSpinnerToDataModels.clear();
            unitSpinnerToAdapter.notifyDataSetChanged();
            unit_upgrade_btn.setEnabled(false);
            unit_num.setEnabled(false);
            unit_upgrade_btn.setText(getResources().getString(R.string.tech_fault3));
            unit_upgrade_btn.setTextColor(getResources().getColor(R.color.error_prompt));
        }

        unitSpinnerAdapter.notifyDataSetChanged();
    }

    /**
     * Update unit info
     *
     * @param terrName territory name
     */
    private void updateTerrInfo(String terrName) {
        unitDataModels.clear();
        unitUpgradeDataModels.clear();

        Territory territory = mGame.getMap().getTerritory(terrName);
        if (territory == null) {
            return;
        }

        HashMap<String, Integer> minusMap = unitMoveAttackMap.get(terrName);
        HashMap<String, Integer> upgradeMap = unitUpgradeMap.get(terrName);
        HashMap<String, Integer> map = new HashMap<>();
        for (Unit unit : territory.getUnits()) {
            map.put(Unit.getName(unit.getType()), map.getOrDefault(Unit.getName(unit.getType()), 0) + 1);
        }
        for (String type : map.keySet()) {
            if (minusMap != null && minusMap.containsKey(type)) {
                map.put(type, map.get(type) - minusMap.get(type));
            }
            System.out.println("Type: " + type + " Num: " + map.get(type) + "");
            unitDataModels.add(new UnitDataModel(type, map.get(type)));
            if (upgradeMap != null && upgradeMap.containsKey(type)) {
                UnitUpgradeDataModel unitUpgradeDataModel = new UnitUpgradeDataModel(type, map.get(type));
                int delta = upgradeMap.get(type);
                if (delta >= 0) {
                    unitUpgradeDataModel.setDelta("+" + delta);
                } else {
                    unitUpgradeDataModel.setDelta("" + delta);
                }
                unitUpgradeDataModels.add(unitUpgradeDataModel);
            } else {
                unitUpgradeDataModels.add(new UnitUpgradeDataModel(type, map.get(type)));
            }
        }

        if (mTouchEvent == ATTACK || mTouchEvent == MOVE) {
            if (unitDataModels.size() == 0) {
                TextView error_view = move_attack_view.findViewById(R.id.cost_error_prompt);
                error_view.setVisibility(View.VISIBLE);
                error_view.setText(getResources().getString(R.string.no_unit));
            } else {
                TextView error_view = move_attack_view.findViewById(R.id.cost_error_prompt);
                error_view.setVisibility(View.INVISIBLE);
            }
            String cost_s = 0 + " coins";
            TextView total_cost = move_attack_view.findViewById(R.id.total_cost);
            total_cost.setText(cost_s);
        }

        unitAdapter.notifyDataSetChanged();
        unitUpgradeAdapter.notifyDataSetChanged();
    }

    /**
     * Update unit info
     */
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

    /**
     * Show the wait texts
     */
    private void showWaitTexts() {
        base_view.setVisibility(View.VISIBLE);
        inner_order_view.setVisibility(View.GONE);
        global_prompt.setVisibility(View.VISIBLE);
        global_dialog.setVisibility(View.GONE);
    }

    /**
     * Show the dialog
     */
    private void showDialog(String text1, String text2, String text3) {
        base_view.setVisibility(View.VISIBLE);
        inner_order_view.setVisibility(View.GONE);
        global_prompt.setVisibility(View.GONE);
        global_dialog.setVisibility(View.VISIBLE);
        final View shadow_view = global_dialog.findViewById(R.id.global_dialog_shadow);
        final View text_group = global_dialog.findViewById(R.id.global_dialog_text_group);
        final TextView text1_view = global_dialog.findViewById(R.id.global_dialog_text1);
        final TextView text2_view = global_dialog.findViewById(R.id.global_dialog_text2);
        final TextView text3_view = global_dialog.findViewById(R.id.global_dialog_text3);
        text1_view.setText(text1);
        text2_view.setText(text2);
        text3_view.setText(text3);
        shadow_view.setAlpha(0f);
        text_group.setAlpha(0f);
        text2_view.setTextColor(this.playerColor);
        int window_width = requireActivity().getWindowManager().getDefaultDisplay().getWidth();

        // Translation shadow
        ObjectAnimator shadow_trans = ObjectAnimator.ofFloat(shadow_view, "translationX", -window_width, 0);
        shadow_trans.setDuration(500);
        // Alpha shadow
        ObjectAnimator shadow_alpha = ObjectAnimator.ofFloat(shadow_view, "alpha", 0f, 1f);
        shadow_alpha.setDuration(500);
        // Translation text group
        ObjectAnimator text_group_trans = ObjectAnimator.ofFloat(text_group, "translationX", -100, 0);
        text_group_trans.setDuration(800);
        text_group_trans.setStartDelay(200);
        // Alpha text group
        ObjectAnimator text_group_alpha = ObjectAnimator.ofFloat(text_group, "alpha", 0f, 1f);
        text_group_alpha.setDuration(800);
        text_group_alpha.setStartDelay(200);

        // Start the animation
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(shadow_trans).with(shadow_alpha).with(text_group_trans).with(text_group_alpha);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Fade out animation
                // Translation shadow
                ObjectAnimator shadow_trans2 = ObjectAnimator.ofFloat(shadow_view, "translationX", 0, window_width);
                shadow_trans2.setDuration(500);
                shadow_trans2.setStartDelay(1500);
                // Alpha shadow
                ObjectAnimator shadow_alpha2 = ObjectAnimator.ofFloat(shadow_view, "alpha", 1f, 0f);
                shadow_alpha2.setDuration(500);
                shadow_alpha2.setStartDelay(1500);
                // Translation text group
                ObjectAnimator text_group_trans2 = ObjectAnimator.ofFloat(text_group, "translationX", 0, 100);
                text_group_trans2.setDuration(700);
                text_group_trans2.setStartDelay(1000);
                // Alpha text group
                ObjectAnimator text_group_alpha2 = ObjectAnimator.ofFloat(text_group, "alpha", 1f, 0f);
                text_group_alpha2.setDuration(700);
                text_group_alpha2.setStartDelay(1000);

                // Start the animation
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.play(shadow_trans2).with(shadow_alpha2).with(text_group_trans2).with(text_group_alpha2);
                animatorSet2.start();

                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!hasHorcruxResult && mGame.getNewHorcrux() != null) {
                            showDialog("Player: " + mGame.getPlayerList().get(Integer.parseInt(mGame.getNewHorcrux().split("%")[1])).getPlayerName() + " has found a new ", mGame.getNewHorcrux().split("%")[0], "");
                            hasHorcruxResult = true;
                        } else if (!hasHorcruxAffectResult && mGame.getHorcruxAffect() != null) {

                            hasHorcruxAffectResult = true;
                        } else {
                            // update the game view
                            global_prompt.setVisibility(View.GONE);
                            global_dialog.setVisibility(View.GONE);
                            inner_order_view.setVisibility(View.VISIBLE);
                            base_view.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }

    /**
     * Assign values in init view
     */
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
        System.out.println("Player House: " + mPlayer.getHouse() + "");
        switch (mPlayer.getHouse()) {
            case RAVENCLAW:
                init_house.setText(context.getResources().getString(R.string.ravenclaw));
                init_house.setTextColor(getResources().getColor(R.color.ui_ravenclaw));
                init_house_img.setImageResource(R.drawable.house_rauenclaw);
                ui_house.setText(context.getResources().getString(R.string.ravenclaw));
                ui_house.setTextColor(getResources().getColor(R.color.ui_ravenclaw));
                break;
            case HUFFLEPUFF:
                init_house.setText(context.getResources().getString(R.string.hufflepuff));
                init_house.setTextColor(getResources().getColor(R.color.ui_hufflepuff));
                init_house_img.setImageResource(R.drawable.house_hufflepuff);
                ui_house.setText(context.getResources().getString(R.string.hufflepuff));
                ui_house.setTextColor(getResources().getColor(R.color.ui_hufflepuff));
                break;
            case GRYFFINDOR:
                init_house.setText(context.getResources().getString(R.string.gryffindor));
                init_house.setTextColor(getResources().getColor(R.color.ui_gryffindor));
                init_house_img.setImageResource(R.drawable.house_gryffindor);
                ui_house.setText(context.getResources().getString(R.string.gryffindor));
                ui_house.setTextColor(getResources().getColor(R.color.ui_gryffindor));
                break;
            case SLYTHERIN:
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

    /**
     * Assign values in winner view
     */
    private void assignWinner(boolean isWin) {
        if (!this.isLost) {
            // Send the game object to ClientIntentService
            Intent intent = new Intent();
            intent.setAction("RISC_GAME_END");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
        ui_view.findViewById(R.id.end_turn).setEnabled(false);
        base_view.setVisibility(View.GONE);
        inner_order_view.setVisibility(View.GONE);
        global_prompt.setVisibility(View.GONE);
        global_dialog.setVisibility(View.GONE);
        winner_base_view.setVisibility(View.VISIBLE);
        // Assign the text and image
        TextView winner_player_name = winner_view.findViewById(R.id.winner_player_name);
        TextView winner_house = winner_view.findViewById(R.id.winner_house);
        TextView winner_text = winner_view.findViewById(R.id.winner_text1);
        ImageView winner_house_img = winner_view.findViewById(R.id.winner_house_img);

        winner_player_name.setText(this.mGame.getPlayerName());
        String house_text = "";
        if (isWin) {
            winner_text.setText(context.getResources().getString(R.string.winner_words));
        } else {
            winner_text.setText(context.getResources().getString(R.string.loser_words));
            winner_house_img.setImageResource(R.drawable.triwizard_cup_lost);
            ui_view.findViewById(R.id.ui_watching).setVisibility(View.VISIBLE);
            ui_view.findViewById(R.id.ui_side_bar).setVisibility(View.GONE);
        }

        switch (mPlayer.getHouse()) {
            case RAVENCLAW:
                house_text = context.getResources().getString(R.string.ravenclaw);
                winner_house.setTextColor(getResources().getColor(R.color.ui_ravenclaw));
                if (isWin) {
                    winner_house_img.setImageResource(R.drawable.triwizard_cup_b);
                }
                break;
            case HUFFLEPUFF:
                house_text = context.getResources().getString(R.string.hufflepuff);
                winner_house.setTextColor(getResources().getColor(R.color.ui_hufflepuff));
                if (isWin) {
                    winner_house_img.setImageResource(R.drawable.triwizard_cup_y);
                }
                break;
            case GRYFFINDOR:
                house_text = context.getResources().getString(R.string.gryffindor);
                winner_house.setTextColor(getResources().getColor(R.color.ui_gryffindor));
                if (isWin) {
                    winner_house_img.setImageResource(R.drawable.triwizard_cup_r);
                }
                break;
            case SLYTHERIN:
                house_text = context.getResources().getString(R.string.slytherin);
                winner_house.setTextColor(getResources().getColor(R.color.ui_slytherin));
                if (isWin) {
                    winner_house_img.setImageResource(R.drawable.triwizard_cup_g);
                }
                break;
        }
        if (isWin) {
            house_text += "Wins!";
        } else {
            house_text += "Loses!";
        }
        winner_house.setText(house_text);
    }

    /**
     * Update the player values
     */
    private void updatePlayerValues() {
        TextView ui_horn = ui_view.findViewById(R.id.ui_horn);
        TextView ui_coin = ui_view.findViewById(R.id.ui_coin);
        TextView ui_world_level = ui_view.findViewById(R.id.ui_world_level);

        Player player = this.mGame.getPlayer(this.mGame.getPlayerName());

        int shown_coins = player.getCoins() - this.currentCoinExpense;
        int shown_horns = player.getHorns() - this.currentHornExpense;

        ui_coin.setText(String.valueOf(shown_coins));
        ui_horn.setText(String.valueOf(shown_horns));
        ui_world_level.setText(String.valueOf(player.getWorldLevel()));
    }

    /**
     * Update the unit initialization info
     */
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

        ui_view.findViewById(R.id.unit_init_btn).setOnClickListener(v -> base_view.setVisibility(View.VISIBLE));

        base_view.findViewById(R.id.inflate_init_unit).findViewById(R.id.init_unit_back_btn).setOnClickListener(v -> base_view.setVisibility(View.GONE));

        commit_init_btn.setOnClickListener(v -> {
            // Check if the number of units placed is correct
            if (terrAdapter.getTotalNum() != 24) {
                showDialog(context.getResources().getString(R.string.unit_placed_fault_p1), context.getResources().getString(R.string.unit_placed_fault_p2), context.getResources().getString(R.string.unit_placed_fault_p3));
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
            showWaitTexts();
            // Send the game object to ClientIntentService
            Intent intent = new Intent();
            intent.setAction("RISC_SEND_TO_SERVER");
            intent.putExtra("game", this.mGame);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
    }

}