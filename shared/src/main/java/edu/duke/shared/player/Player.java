package edu.duke.shared.player;


import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.duke.shared.Game;
import edu.duke.shared.map.Territory;


public class Player implements Serializable {
    // Abandoned!! Territories owned by this player
    private final HashSet<Territory> playerTerrs;
    private final HashMap<House, Integer> skill = new HashMap<>();
    // Player's thread
    public transient PlayerThread playerThread;
    // Player's thread
    public transient Thread thread;
    // Coins
    public int coins;
    // Horns
    public int horns;
    public boolean willUpgradeWorldLevel = false;
    // Horcrux storage
    private HashMap<Horcrux, Integer> horcruxes = new HashMap<>();
    // Horcrux usage, <name, num>
    private HashMap<Horcrux, Integer> horcruxUsage = new HashMap<>();
    // Horcrux usage target, <name, isTarget>
    private HashMap<Horcrux, Boolean> horcruxTarget = new HashMap<>();
    // Player's name
    private String playerName;
    // Player's id
    private int playerId;
    // Player's socket
    private transient Socket socket;
    // World level
    private int worldLevel;
    // Player's house
    private House house;
    // Player's skill
    private SkillState skillState = SkillState.NOT_USED;


    /**
     * Initialize the Player by name
     *
     * @param playerId player id
     * @param socket   player socket
     */
    public Player(int playerId, Socket socket) {
        this.playerName = null;
        this.playerId = playerId;
        this.playerTerrs = new HashSet<>();
        this.socket = socket;
        // Start the thread
        this.playerThread = new PlayerThread(this.socket, this.playerId);
        this.thread = new Thread(this.playerThread);
        this.coins = 0;
        this.horns = 0;
        this.worldLevel = 1;
        this.thread.start();
        this.horcruxes.put(Horcrux.CUP, 0);
        this.horcruxes.put(Horcrux.HAT, 0);
        this.horcruxes.put(Horcrux.RING, 0);
        this.horcruxes.put(Horcrux.SNAKE, 0);
        this.horcruxes.put(Horcrux.LOCKET, 0);
        this.horcruxes.put(Horcrux.DIARY, 0);
        this.horcruxUsage.put(Horcrux.CUP, 0);
        this.horcruxUsage.put(Horcrux.HAT, 0);
        this.horcruxUsage.put(Horcrux.RING, 0);
        this.horcruxUsage.put(Horcrux.SNAKE, 0);
        this.horcruxUsage.put(Horcrux.LOCKET, 0);
        this.horcruxUsage.put(Horcrux.DIARY, 0);
        this.skill.put(House.GRYFFINDOR, 0);
        this.skill.put(House.RAVENCLAW, 0);
        this.skill.put(House.HUFFLEPUFF, 0);
        this.skill.put(House.SLYTHERIN, 0);
    }

    /**
     * Initialize the Player by id, socket and house
     *
     * @param playerId player id
     * @param socket   player socket
     * @param house    player house
     */
    public Player(int playerId, Socket socket, House house) {
        this(playerId, socket);
        this.house = house;
    }

    /**
     * This function return the cost for each level of upgrade goal
     *
     * @param goal the level of upgrade goal
     * @return the cost for each level of upgrade goal
     */
    public static int upgradeCost(int goal) {
        switch (goal) {
            case 2:
                return 60;
            case 3:
                return 120;
            case 4:
                return 300;
            case 5:
                return 500;
            case 6:
                return 1000;
            default:
                return 60;
        }
    }

    /**
     * Initialize the Player by name
     *
     * @param serverGame server game
     */
    public void start(Game serverGame) {
        this.playerThread = new PlayerThread(serverGame.getGameState(), this.socket, this.playerId);
        this.playerThread.setServerGame(serverGame);
        if (this.thread != null) {
            this.thread.interrupt();
        }
        this.thread = new Thread(this.playerThread);
        this.thread.start();
    }

    /**
     * get player thread
     *
     * @return player thread
     */
    public PlayerThread getPlayerThread() {
        return this.playerThread;
    }

    /**
     * Wait for the thread to finish
     */
    public void threadJoin() {
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * get player name
     *
     * @return player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * change player name
     *
     * @param playerName new player name
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * get player id
     *
     * @return player id
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * change player id
     *
     * @param playerId new player id
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * add Territory to the territory list
     *
     * @param terr territory to add
     * @return true if success
     */
    public boolean expandTerr(Territory terr) {
        if (playerTerrs.contains(terr)) {
            return false;
        }
        playerTerrs.add(terr);
        return true;
    }

    /**
     * remove Territory from the territory list
     *
     * @param terr territory to remove
     * @return true if success
     */
    public boolean removeTerr(Territory terr) {
        if (!playerTerrs.contains(terr)) {
            return false;
        }
        playerTerrs.remove(terr);
        return true;
    }

    /**
     * return Territory list of this player
     *
     * @return territory list
     */
    public HashSet<Territory> getPlayerTerrs() {
        return playerTerrs;
    }

    /**
     * return world level of this player
     *
     * @return world level
     */
    public int getWorldLevel() {
        return worldLevel;
    }

    public void updateSkill() {
        skill.put(house, 1);
    }

    public int getSkillUsed() {
        return skill.get(house);
    }

    public void addSkill() {
        skill.put(house, 2);
    }


    /**
     * return connection socket of this player
     *
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * change connection socket of this player
     *
     * @param socket new socket
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }


    /**
     * return the corresponding resources for this player, with the Integer[] array's
     * first index being coin resources, second index being horn resources.
     */
    public void updateResources(ArrayList<Territory> territories) {
        int coins = 0;
        int horns = 0;
        for (Territory t : territories) {
            coins += t.getCoins();
            horns += t.getHorns();
        }
        this.coins += coins;
        this.horns += horns;
        if (house.equals(House.RAVENCLAW)) {
            this.horns += 15;
        }
    }

    /**
     * Get coins
     *
     * @return coins
     */
    public int getCoins() {
        return this.coins;
    }

    /**
     * Set coins
     *
     * @param coins coins
     */
    public void setCoins(int coins) {
        this.coins = coins;
    }

    /**
     * Get horns
     *
     * @return horns
     */
    public int getHorns() {
        return this.horns;
    }

    /**
     * Set horns
     */
    public void setHorns(int horns) {
        this.horns = horns;
    }

    public void upgradeWorldLevel() {
        this.worldLevel++;
    }

    /**
     * Set horns expense
     *
     * @param horns horns
     */
    public void setExpenseHorns(int horns) {
        this.horns -= horns;
    }

    /**
     * Set coins expense
     *
     * @param coins coins
     */
    public void setExpenseCoins(int coins) {
        this.coins -= coins;
    }

    /**
     * Get horcrux storage
     *
     * @return horcrux storage
     */
    public int getHorcruxStorage(Horcrux horcrux) {
        return this.horcruxes.get(horcrux);
    }

    /**
     * Set horcrux storage
     *
     * @param horcrux horcrux
     * @param amount  amount
     */
    public void addToHorcruxStorage(Horcrux horcrux, int amount) {
        this.horcruxes.put(horcrux, this.horcruxes.getOrDefault(horcrux, 0) + amount);
    }

    /**
     * Remove horcrux storage
     */
    public void removeFromHorcruxStorage(Horcrux horcrux, int amount) {
        this.horcruxes.put(horcrux, this.horcruxes.get(horcrux) - amount);
    }

    /**
     * Get horcrux usage
     */
    public int getHorcruxUsage(Horcrux horcrux) {
        return this.horcruxUsage.get(horcrux);
    }


    public HashMap<Horcrux, Integer> getHorcruxesList() {
        return this.horcruxUsage;
    }

    public void setHorcruxesList(HashMap<Horcrux, Integer> HorcruxesList) {
        this.horcruxUsage = HorcruxesList;
    }

    public HashMap<Horcrux, Integer> getHorcruxesStorage() {
        return this.horcruxes;
    }

    public void setHorcruxesStorage(HashMap<Horcrux, Integer> HorcruxesStorage) {
        this.horcruxes = HorcruxesStorage;
    }

    public HashMap<Horcrux, Boolean> getHorcruxTarget() {
        return this.horcruxTarget;
    }

    public void setHorcruxTarget(HashMap<Horcrux, Boolean> horcruxtarget) {
        this.horcruxTarget = horcruxtarget;
    }

    /**
     * Set horcrux usage
     *
     * @param horcrux horcrux
     * @param amount  amount
     */
    public void addToHorcruxUsage(Horcrux horcrux, int amount) {
        this.horcruxUsage.put(horcrux, this.horcruxUsage.getOrDefault(horcrux, 0) + amount);
    }

    /**
     * Remove horcrux usage
     */
    public void removeFromHorcruxUsage(Horcrux horcrux, int amount) {
        this.horcruxUsage.put(horcrux, this.horcruxUsage.get(horcrux) - amount);
    }

    /**
     * If is diary target, return true
     */
    public boolean isDiaryTarget() {
        return this.horcruxTarget.getOrDefault(Horcrux.DIARY, false);
    }

    public void setDiaryTarget() {
        this.horcruxTarget.put(Horcrux.DIARY, true);
    }

    public void muteDiaryTarget() {
        this.horcruxTarget.put(Horcrux.DIARY, false);
    }

    public void muteTarget(Horcrux horcrux) {
        this.horcruxTarget.put(horcrux, false);
    }


    /**
     * If is snake
     * , return true
     */
    public boolean isSnakeTarget() {
        return this.horcruxTarget.getOrDefault(Horcrux.SNAKE, false);
    }

    public void setSnakeTarget() {
        this.horcruxTarget.put(Horcrux.SNAKE, true);
    }

    public void muteSnakeTarget() {
        this.horcruxTarget.put(Horcrux.SNAKE, false);
    }

    /**
     * If is locket target, return true
     */
    public boolean isLocketTarget() {
        return this.horcruxTarget.getOrDefault(Horcrux.LOCKET, false);
    }

    public void setLocketTarget() {
        this.horcruxTarget.put(Horcrux.LOCKET, true);
    }

    public void muteLocketTarget() {
        this.horcruxTarget.put(Horcrux.LOCKET, false);
    }

    /**
     * Get player's skill name
     */
    public String getSkillName() {
        if (this.house == null) return "Unknown";
        switch (this.house) {
            case SLYTHERIN:
                return "Serpent's Strategy";
            case HUFFLEPUFF:
                return "Steadfast Roots";
            case RAVENCLAW:
                return "Wings of Wisdom";
            default:
                return "Lion's Courage";
        }
    }

    /**
     * If player buff Gryffindor, return true
     */
    public boolean buffGryffindor() {
        return this.house == House.GRYFFINDOR;
    }

    /**
     * If player buff Slytherin, return true
     */
    public boolean buffSlytherin() {
        return this.house == House.SLYTHERIN;
    }

    /**
     * If player buff Hufflepuff, return true
     */
    public boolean buffHufflepuff() {
        return this.house.equals(House.HUFFLEPUFF);
    }

    /**
     * If player buff Ravenclaw, return true
     */
    public boolean buffRavenclaw() {
        return this.house == House.RAVENCLAW;
    }

    /**
     * If player skill Gryffindor, return true
     */
    public boolean skillGryffindor() {
        return buffGryffindor() && this.skillState == SkillState.IN_EFFECT;
    }

    /**
     * If player skill Slytherin, return true
     */
    public boolean skillSlytherin() {
        return buffSlytherin() && this.skillState == SkillState.IN_EFFECT;
    }

    /**
     * If player skill Hufflepuff, return true
     */
    public boolean skillHufflepuff() {
        return buffHufflepuff() && this.skillState == SkillState.IN_EFFECT;
    }

    /**
     * If player skill Ravenclaw, return true
     */
    public boolean skillRavenclaw() {
        return buffRavenclaw() && this.skillState == SkillState.IN_EFFECT;
    }

    /**
     * Get player house
     */
    public House getHouse() {
        return this.house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    /**
     * Get player's skill state
     */
    public SkillState getSkillState() {
        return this.skillState;
    }

    /**
     * Set player's skill state
     */
    public void setSkillState(SkillState skillState) {
        this.skillState = skillState;
    }


}
