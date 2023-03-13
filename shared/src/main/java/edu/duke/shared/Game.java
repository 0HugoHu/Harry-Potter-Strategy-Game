package edu.duke.shared;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.duke.shared.helper.Dice;
import edu.duke.shared.helper.Header;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.MapFactory;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.turn.Turn;
import edu.duke.shared.unit.Unit;

public class Game implements Serializable {
    // Game meta data
    private final Header header;
    // Number of players
    private final int numPlayers;
    private final ArrayList<Player> playerList;
    // Map
    private final GameMap gameMap;

    //Turn Map for recording all players' turns: Integer is the PLayer's id, ArrayList<Turn>
    //has two contents, first being MoveTurn and second being AttackTurn
    private HashMap<Integer, ArrayList<Turn>> turnMap;
    // TurnMap<playerId, ArrayList<Turn>>
    private ArrayList<HashMap<Integer, ArrayList<Turn>>> turnList;

    //AttackList for all attackers aiming at the same destination;
    //String is the destination name, ArrayList is the list for all attackers.
    //If attacks come from the same player,then they will be put into the same inner arrayList
    private HashMap<String, ArrayList<ArrayList<Attack>>> attackList;

    //map for recording all units that should be deducted after the battle
    //(which are units lost in the battle)
    private HashMap<Territory, Integer> unitMinusMap;

    //map for recording all units that should be added after the battle
    //(which are the winning units occupying the new land)
    private HashMap<Territory, Integer> unitAddMap;

    private StringBuilder bf;


    /**
     * Initialize Game by number of players
     *
     * @param numPlayers Number of players
     */
    public Game(int numPlayers) {
        this(numPlayers, new MapFactory(30, 60, 12).createRandomMap());
    }

    /**
     * Initialize Game by number of players and Map
     *
     * @param numPlayers num of players
     * @param gameMap    Map
     */
    public Game(int numPlayers, GameMap gameMap) {
        this.numPlayers = numPlayers;
        this.gameMap = gameMap;
        this.playerList = new ArrayList<>();
        this.turnMap = new HashMap<>();
        this.turnList = new ArrayList<>();
        this.header = new Header();
        this.attackList = new HashMap<>();
        this.unitMinusMap = new HashMap<>();
        this.unitAddMap = new HashMap<>();
        this.bf=new StringBuilder();
    }

    public String getString(){
        return bf.toString();
    }

    /**
     * This is the method for putting all turns into the turnMap
     *
     * @param id   player ID
     * @param turn players'turn
     */
    public void addTurn(int id, Turn turn) {
        if (turnMap.containsKey(id)) {
            ArrayList<Turn> list = turnMap.get(id);
            list.add(turn);
            turnMap.put(id, list);
        } else {
            ArrayList<Turn> list = new ArrayList<>();
            list.add(turn);
            turnMap.put(id, list);
        }
    }


    /**
     * This is the method for making attackList and do the Attack
     * by calling the function doAttackPhase()
     */
    public void makeTurns() {
        for (Map.Entry<Integer, ArrayList<Turn>> entry : turnMap.entrySet()) {
            for (Turn turn : entry.getValue()) {
                if (turn.getType().equals("attack")) {
                    makeAttackList((AttackTurn) turn);
                }
            }
        }
        doAttackPhase();
        changeUnit();
    }

    /**
     * Build the attackList of the structure HashMap<String, ArrayList<ArrayList<Attack>>>
     * String is the same destination, ArrayList collects all attacks aiming at the same destination.
     * If attacks come from the same player, they will be put into the same inner List.
     *
     * @param turn AttackTurn
     */
    public void makeAttackList(AttackTurn turn) {
        ArrayList<Attack> attacks = turn.getAttacks();
        for (int i = 0; i < attacks.size(); i++) {
            //If the attack destination already exists, put into the arrayList
            if (attackList.containsKey(attacks.get(i).getTo())) {
                ArrayList<ArrayList<Attack>> att = attackList.get(attacks.get(i).getTo());
                boolean flag = false;
                for (ArrayList<Attack> attArr : att) {
                    //If the attack come from the same player, then we should put it into the same inner list
                    if (attArr.get(0).getplayer().getPlayerName().equals(attacks.get(i).getplayer().getPlayerName())) {
                        attArr.add(new Attack(attacks.get(i).getFrom(), attacks.get(i).getTo(), attacks.get(i).getNumUnits(), attacks.get(i).getplayer()));
                        flag = true;
                    }
                }
                //if there's no other attacks from the same player, then we should put it into a new inner list
                if (flag == false) {
                    ArrayList<Attack> att2 = new ArrayList<>();
                    att2.add(new Attack(attacks.get(i).getFrom(), attacks.get(i).getTo(), attacks.get(i).getNumUnits(), attacks.get(i).getplayer()));
                    att.add(att2);
                }
                attackList.put(attacks.get(i).getTo(), att);
            } else {
                //if the attack destination is a new one, put it on the list with different destination key
                ArrayList<Attack> att = new ArrayList<>();
                att.add(new Attack(attacks.get(i).getFrom(), attacks.get(i).getTo(), attacks.get(i).getNumUnits(), attacks.get(i).getplayer()));
                ArrayList<ArrayList<Attack>> attArr = new ArrayList<>();
                attArr.add(att);
                attackList.put(attacks.get(i).getTo(), attArr);
            }
        }
    }

    /**
     * This is the method for all the attacks on the attackList to be executed.
     * If multiple attacks, they will have one unit fight with the next on the list
     * in sequence, from 0 to the last, and last fight with 0 all over again.
     */
    public void doAttackPhase() {
        for (Map.Entry<String, ArrayList<ArrayList<Attack>>> entry : attackList.entrySet()) {
            String destination = entry.getKey();
            ArrayList<ArrayList<Attack>> att = entry.getValue();
            Territory desTerr = gameMap.getTerritory(destination);
            SetUpDefense(destination, att);
            int i = 0;
            int j = 0;
            //This is the while loop for all attacks on the attack list to
            //have a unit fight with each other, one by one in sequence
            while (att.size() > 1) {
                j = i + 1;
                if (j == att.size()) {
                    j = 0;
                }
                //This is the attacker on the list order
                Territory attackTerr1 = gameMap.getTerritory(att.get(i).get(0).getFrom());
                //This is the defender on the list order
                Territory attackTerr2 = gameMap.getTerritory(att.get(j).get(0).getFrom());
                int dice1 = new Dice(20).getDice();
                int dice2 = new Dice(20).getDice();
                if (dice1 > dice2) {
                    battleStage(att, attackTerr2, i, j);
                } else {
                    battleStage(att, attackTerr1, j, i);
                }
                i++;
                if (i >= att.size()) {
                    i = 0;
                }
            }
            //After the fighting stage, announce the winner
            announceWinner(att, desTerr);
        }
    }


    /**
     * This is the method for setting up defense forces for each defending territory.
     * For the defender, its defence force should deduct all its attacks coming out from this territory.
     * If after deducting all the attacking forces, there still are some defending units left,
     * then the defender should be added as a new attack into the Attacklist.
     *
     * @param destination destination land
     * @param att         attackList
     */
    public void SetUpDefense(String destination, ArrayList<ArrayList<Attack>> att) {
        Territory desTerr = gameMap.getTerritory(destination);
        int desPlayer = gameMap.getTerritory(destination).getPlayerOwner().getPlayerId();
        ArrayList<Turn> desTurn = turnMap.get(desPlayer);
        AttackTurn attackTurn = (AttackTurn) (desTurn.get(1));
        ArrayList<Attack> atts = attackTurn.getAttacks();
        int defenseForce = desTerr.getNumUnits();
        String s="Defend Territory: " + desTerr.getName()+"\n";
        bf.append(s);
        System.out.print(s);
        //The defenseForce should deduct all the attacking units coming from the same territory
        if (atts.size() > 0) {
            for (Attack desAtt : atts) {
                if (desAtt.getFrom().equals(destination)) {
                    defenseForce = defenseForce - desAtt.getNumUnits();
                }
            }
        }
        //If defender territory still has units to defend, put it on the attack list
        if (defenseForce > 0) {
            Attack defenderAtt = new Attack(destination, destination, defenseForce, desTerr.getPlayerOwner());
            ArrayList<Attack> attTOAdd = new ArrayList<>();
            attTOAdd.add(defenderAtt);
            att.add(attTOAdd);
        }
    }


    /**
     * This is method for the battle progress, which will record the deducting units in each fight,
     * and report winners as well as deleted failure attacks
     *
     * @param att        attackList
     * @param attackTerr the territory that lose in this unit fight
     * @param i          winner
     * @param j          loser
     */
    public void battleStage(ArrayList<ArrayList<Attack>> att, Territory attackTerr, int i, int j) {
        int playerIunits = 0;
        for (int k = 0; k < att.get(i).size(); k++) {
            playerIunits += att.get(i).get(k).getNumUnits();
        }
        int playerJunits = 0;
        for (int k = 0; k < att.get(j).size(); k++) {
            playerJunits += att.get(j).get(k).getNumUnits();
        }
        String outputUnits1="Attack " + att.get(i).get(0).getplayer().getPlayerName() + " still has " + playerIunits + " units!\n";
        String outputUnits2="Attack " + att.get(j).get(0).getplayer().getPlayerName() + " still has " + playerJunits + " units!\n";
        bf.append(outputUnits1);
        bf.append(outputUnits2);
        System.out.print(outputUnits1);
        System.out.print(outputUnits2);
        //If the lost attack still have more than one unit, it will not be deleted from the list,
        //only minus one unit
        if (att.get(j).get(0).getNumUnits() > 1) {
            att.get(j).get(0).removeUnit();
            unitMinusMap.put(attackTerr, unitMinusMap.getOrDefault(attackTerr, 0) + 1);
            String announce1="Attacker " + att.get(i).get(0).getplayer().getPlayerName() + " wins in this turn!\n";
            bf.append(announce1);
            System.out.print(announce1);
        }
        //If the lost attack now only have one unit, then it will be deleted after deducting this unit.
        else if (att.get(j).get(0).getNumUnits() == 1) {
            att.get(j).get(0).removeUnit();
            unitMinusMap.put(attackTerr, unitMinusMap.getOrDefault(attackTerr, 0) + 1);
            if (att.get(j).size() == 1) {
                //If this attack has no other alliance units from other territories,
                // then it will be deleted and the player ends his/her attack now
                String announce2="Attacker " + att.get(j).get(0).getplayer().getPlayerName() + " failed! Deleted from this list.\n";
                bf.append(announce2);
                System.out.print(announce2);
            } else {
                //If this attack still has other alliance units from other territories, the player's attack will go on,
                //so we will not announce he/she being deleted from the list.
                String announce3="Attacker " + att.get(i).get(0).getplayer().getPlayerName() + " wins in this turn!\n";
                bf.append(announce3);
                System.out.print(announce3);
            }
            att.get(j).remove(att.get(j).get(0));
            if (att.get(j).size() == 0) {
                att.remove(att.get(j));
            }
        }
    }

    /**
     * This method announce the winner of this battle, change the destination owner after fights,
     * and counting the units that should be moving into the new land as winner.
     *
     * @param att     attackList
     * @param desTerr destination territory
     */
    public void announceWinner(ArrayList<ArrayList<Attack>> att, Territory desTerr) {
        String s1="Attacker " + att.get(0).get(0).getplayer().getPlayerName() + " has won this battle!\n";
        String s2="----------------------------------------------------------------------------\n";
        bf.append(s1);
        bf.append(s2);
        System.out.print(s1);
        System.out.print(s2);
        Territory finalTerr = gameMap.getTerritory(att.get(0).get(0).getTo());
        //record all units that remains after the fight,
        //which are units that will occupy the new land.
        int remainAtt = 0;
        for (int k = 0; k < att.get(0).size(); k++) {
            //remaining units should be first deducted from the original territory
            unitMinusMap.put(gameMap.getTerritory(att.get(0).get(k).getFrom()),
                    unitMinusMap.getOrDefault(gameMap.getTerritory(att.get(0).get(k).getFrom()), 0)
                            + att.get(0).get(k).getNumUnits());
            remainAtt += att.get(0).get(k).getNumUnits();
        }
        unitAddMap.put(finalTerr, remainAtt);
        //change the owner of the territory
        desTerr.changePlayerOwner(att.get(0).get(0).getplayer());
        desTerr.changeOwner(att.get(0).get(0).getplayer().getPlayerName());
    }


    /**
     * change the units number according to the hashMaps.
     * This stage should be executed after the fights, as all battles happen simultaneously
     */
    public void changeUnit() {
        for (Map.Entry<Territory, Integer> entry : unitMinusMap.entrySet()) {
            Territory currTerr = entry.getKey();
            for (int i = 0; i < entry.getValue(); i++) {
                currTerr.removeUnit(new Unit("Normal"));
            }
        }
        for (Map.Entry<Territory, Integer> entry : unitAddMap.entrySet()) {
            Territory currTerr = entry.getKey();
            for (int i = 0; i < entry.getValue(); i++) {
                currTerr.addUnit(new Unit("Normal"));
            }
        }
        //After the battles, all territories should add one new unit
        for (Territory t : gameMap.getTerritories()) {
            t.addUnit(new Unit("Normal"));
        }
    }

    /**
     * print the current situation of territories, owners, and units
     */
    public void printUnit() {
        for (Territory terr : gameMap.getTerritories()) {
            System.out.println("----------------");
            System.out.println(terr.getName());
            System.out.println("Owner: " + terr.getOwner());
            System.out.println("Units: " + terr.getNumUnits());
            System.out.println("----------------");
        }
    }


    /**
     * Add New player to the playerList of this game
     *
     * @param p player to add
     * @return true if success
     */
    public boolean addPlayer(Player p) {
        if (playerList.contains(p)) {
            return false;
        }
        playerList.add(p);
        return true;
    }

    /**
     * get the player list
     *
     * @return player list
     */
    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    /**
     * get the player by name
     *
     * @param name player name
     * @return Player
     */
    public Player getPlayer(String name) {
        for (Player p : playerList) {
            if (p.getPlayerName().equals(name)) {
                return p;
            }
        }
        return null;
    }


    /**
     * Print some details of this Game Basic Info,
     * including player Name and Territory Name
     *
     * @return string of info
     */
    public String GameDetail() {
        StringBuilder sb = new StringBuilder();
        for (Player p : playerList) {
            sb.append("--------------------\n");
            sb.append(p.getPlayerName()).append("\n");
            for (Territory t : p.getPlayerTerrs()) {
                sb.append(t.getName());
                sb.append("\n");
            }
            sb.append("--------------------\n");
        }
        return sb.toString();
    }

    public HashMap<Integer, ArrayList<Turn>> getTurnMap() {
        return this.turnMap;
    }

    /**
     * Get number of players
     *
     * @return number of players
     */
    public int getNumPlayers() {
        return this.numPlayers;
    }

    /**
     * Get map
     *
     * @return map
     */
    public GameMap getMap() {
        return this.gameMap;
    }

    public void setPlayerId(int id) {
        this.header.setPlayerId(id);
    }

    public int getPlayerId() {
        return this.header.getPlayerId();
    }

    public void setPlayerName(String name) {
        this.header.setPlayerName(name);
    }

    public String getPlayerName() {
        return this.header.getPlayerName();
    }

    public void setGameState(State state) {
        this.header.setState(state);
    }

    public State getGameState() {
        return this.header.getState();
    }

    public void turnComplete() {
        this.header.turnComplete();
    }

    public int getTurn() {
        return this.header.getTurn();
    }

    public void addToTurnMap(int playerId, MoveTurn moveTurn, AttackTurn attackTurn) {
        HashMap<Integer, ArrayList<Turn>> turnMap = new HashMap<>();
        ArrayList<Turn> newTurn=new ArrayList<>();
        newTurn.add(moveTurn);
        newTurn.add(attackTurn);
        turnMap.put(playerId, newTurn);
        this.turnList.add(turnMap);
    }


    public ArrayList<HashMap<Integer, ArrayList<Turn>>> getTurnList() {
        return this.turnList;
    }

}
