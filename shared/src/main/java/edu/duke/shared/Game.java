package edu.duke.shared;

import java.io.Serializable;
import java.util.*;

import edu.duke.shared.helper.Dice;
import edu.duke.shared.helper.Header;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.MapFactory;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.Turn;
import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

public class Game implements Serializable {
    // Game meta data
    private final Header header;
    // Number of players
    private final int numPlayers;
    private final ArrayList<Player> playerList;
    // Map
    private final GameMap gameMap;
    // TurnMap<playerId, ArrayList<Turn>>
    private final ArrayList<HashMap<Integer, ArrayList<Turn>>> turnList;

    //AttackList for all attackers aiming at the same destination;
    //String is the destination name, ArrayList is the list for all attackers.
    //If attacks come from the same player,then they will be put into the same inner arrayList
    private final HashMap<String, ArrayList<ArrayList<Attack>>> attackList;

    //map for recording all units that should be deducted after the battle
    //(which are units lost in the battle)
    private final HashMap<Territory, HashMap<UnitType,Integer>> unitMinusMap;

    //map for recording all units that should be added after the battle
    //(which are the winning units occupying the new land)
    private final HashMap<Territory, HashMap<UnitType,Integer>> unitAddMap;

    //StringBuilder for recording the details of the battle
    private final StringBuilder attackDetailsSB;


    /**
     * Initialize Game by number of players
     *
     * @param numPlayers Number of players
     */
    public Game(int numPlayers, int numUnits) {
        this(numPlayers, numUnits, new MapFactory(30, 60, 24).createRandomMap());
    }

    /**
     * Initialize Game by number of players and Map
     *
     * @param numPlayers num of players
     * @param gameMap    Map
     */
    public Game(int numPlayers, int numUnits, GameMap gameMap) {
        this.numPlayers = numPlayers;
        // Number of Units for each player
        this.gameMap = gameMap;
        this.playerList = new ArrayList<>();
        this.turnList = new ArrayList<>();
        this.header = new Header();
        this.attackList = new HashMap<>();
        this.unitMinusMap = new HashMap<>();
        this.unitAddMap = new HashMap<>();
        this.attackDetailsSB = new StringBuilder();
    }

    /**
     * Return attack details in String
     *
     * @return string of attack details
     */
    public String getString() {
        return attackDetailsSB.toString();
    }

    public int calculateDis(int dis,int num){
        return dis*num/2;
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
                assert att != null;
                for (ArrayList<Attack> attArr : att) {
                    //If the attack come from the same player, then we should put it into the same inner list
                    if (attArr.get(0).getPlayerName().equals(attacks.get(i).getPlayerName())) {
                        attArr.add(new Attack(attacks.get(i).getFrom(), attacks.get(i).getTo(), attacks.get(i).getUnitList(), attacks.get(i).getPlayerName()));
                        flag = true;
                    }
                }
                //if there's no other attacks from the same player, then we should put it into a new inner list
                if (!flag) {
                    ArrayList<Attack> att2 = new ArrayList<>();
                    att2.add(new Attack(attacks.get(i).getFrom(), attacks.get(i).getTo(), attacks.get(i).getUnitList(), attacks.get(i).getPlayerName()));
                    att.add(att2);
                }
                attackList.put(attacks.get(i).getTo(), att);
            } else {
                //if the attack destination is a new one, put it on the list with different destination key
                ArrayList<Attack> att = new ArrayList<>();
                att.add(new Attack(attacks.get(i).getFrom(), attacks.get(i).getTo(), attacks.get(i).getUnitList(), attacks.get(i).getPlayerName()));
                ArrayList<ArrayList<Attack>> attArr = new ArrayList<>();
                attArr.add(att);
                attackList.put(attacks.get(i).getTo(), attArr);
            }
        }
    }

    public int getBonusFromType(UnitType type){
        switch (type){
            case GNOME:
                return 0;
            case DWARF:
                return 1;
            case HOUSE_ELF:
                return 3;
            case GOBLIN:
                return 5;
            case VAMPIRE:
                return 8;
            case CENTAUR:
                return 11;
            case WEREWOLF:
                return 15;
            default:
                return 0;
        }
    }

    public int compareUnitHigh(UnitType type1, UnitType type2){
        int bonus1=getBonusFromType(type1);
        int bonus2=getBonusFromType(type2);
        if(bonus1>bonus2){
            return 1;
        }else{
            return 0;
        }
    }



    /**
     * This is the method for all the attacks on the attackList to be executed.
     * If multiple attacks, they will have one unit fight with the next on the list
     * in sequence, from 0 to the last, and last fight with 0 all over again.
     */
    public void doAttack() {
        for (Map.Entry<String, ArrayList<ArrayList<Attack>>> entry : attackList.entrySet()) {
            String destination = entry.getKey();
            ArrayList<ArrayList<Attack>> att = entry.getValue();
            Territory desTerr = gameMap.getTerritory(destination);
            setUpDefense(destination, att);

            int i = 0;
            int j;
            //This is the while loop for all attacks on the attack list to
            //have a unit fight with each other, one by one in sequence
            while (att.size() > 1) {
                j = i + 1;
                if (j == att.size()) {
                    j = 0;
                }
                //This is the attacker on the list order
                UnitType type1=att.get(i).get(0).getHighestType();
                int index1=0;
                for(int k=0;k<att.get(i).size();k++){
                    int output=compareUnitHigh(att.get(i).get(k).getHighestType(),type1);
                    if(output==1){
                        index1=k;
                        type1=att.get(i).get(k).getHighestType();
                    }
                }
                Territory attackTerr1 = gameMap.getTerritory(att.get(i).get(index1).getFrom());
                int bonus1=att.get(i).get(0).getBonus(type1);
                System.out.println("The highest unit level for attacker "+attackTerr1.getOwner()+" is "+type1);
                //This is the defender on the list order


                UnitType type2=att.get(j).get(0).getLowestType();
                int index2=0;
                for(int k=0;k<att.get(j).size();k++){
                    int output=compareUnitHigh(att.get(j).get(k).getHighestType(),type2);
                    if(output==0){
                        index2=k;
                        type2=att.get(j).get(k).getHighestType();
                    }
                }
                Territory attackTerr2 = gameMap.getTerritory(att.get(j).get(index2).getFrom());

                System.out.println("The lowest unit level for defender "+attackTerr2.getOwner()+" is "+type2);
                int bonus2=att.get(j).get(0).getBonus(type2);

                int dice1 = new Dice(20+bonus1).getDice();
                int dice2 = new Dice(20+bonus2).getDice();
                if (dice1 > dice2) {
                    battleStage(att, attackTerr2, i, j,index1,index2,type1,type2);
                } else {
                    battleStage(att, attackTerr1, j, i,index2,index1,type2,type1);
                }
                i++;
                if (i >= att.size()) {
                    i = 0;
                }
            }
            //After the fighting stage, announce the winner
            announceWinner(att, desTerr);
        }
        // Update the unit
        changeUnit();
        this.attackList.clear();
        this.unitAddMap.clear();
        this.unitMinusMap.clear();
    }

    public HashMap<UnitType,Integer> convertToMap(ArrayList<Unit> units){
        HashMap<UnitType,Integer> map=new HashMap<>();
        for(Unit unit:units){
            map.put(unit.getType(),map.getOrDefault(unit.getType(),0)+1);
        }
        return map;
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
    public void setUpDefense(String destination, ArrayList<ArrayList<Attack>> att) {

        Territory desTerr = gameMap.getTerritory(destination);
        int desPlayerId = gameMap.getTerritory(destination).getPlayerOwner().getPlayerId();
        ArrayList<Turn> desTurn = turnList.get(getTurn()).get(desPlayerId);
        assert desTurn != null;
        AttackTurn attackTurn = (AttackTurn) (desTurn.get(1));
        ArrayList<Attack> atts = attackTurn.getAttacks();
        HashMap<UnitType,Integer> defenseForces=convertToMap(desTerr.getUnits());
        HashMap<UnitType,Integer> defenseForce=new HashMap<>();
        defenseForce.putAll(defenseForces);
        //int defenseForce = desTerr.getNumUnits();
        String s = "Defend Territory: " + desTerr.getName() + "\n";
        attackDetailsSB.append(s);
        System.out.print(s);
        //The defenseForce should deduct all the attacking units coming from the same territory
        if (atts.size() > 0) {
            for (Attack desAtt : atts) {
                if (desAtt.getFrom().equals(destination)) {
                    for(Map.Entry<UnitType,Integer> entry: desAtt.getUnitList().entrySet()){
                        UnitType type=entry.getKey();
                        int nums=entry.getValue();
                        defenseForce.put(type,defenseForce.get(type)-nums);
                    }
                    //defenseForce = defenseForce - desAtt.getNumUnits();
                }
            }
        }
        int totalRemainForces=0;
        for(Map.Entry<UnitType,Integer> entry:defenseForce.entrySet()){
            totalRemainForces+=entry.getValue();
        }
        //If defender territory still has units to defend, put it on the attack list
        if (totalRemainForces > 0) {
            Attack defenderAtt = new Attack(destination, destination, defenseForce, desTerr.getOwner());
            ArrayList<Attack> attTOAdd = new ArrayList<>();
            attTOAdd.add(defenderAtt);
            att.add(attTOAdd);
        }
    }

    public void printList(ArrayList<ArrayList<Attack>> att,int i, int j,int index1,int index2){
        HashMap<UnitType,Integer> playerIList=new HashMap<>();
        HashMap<UnitType,Integer> playerJList=new HashMap<>();
        for (int k = 0; k < att.get(i).size(); k++) {
            for(Map.Entry<UnitType,Integer> entry:att.get(i).get(k).getUnitList().entrySet()){
                playerIList.put(entry.getKey(),playerIList.getOrDefault(entry.getKey(),0)+ entry.getValue());
            }
        }
        for (int k = 0; k < att.get(j).size(); k++) {
            for(Map.Entry<UnitType,Integer> entry:att.get(j).get(k).getUnitList().entrySet()){
                playerJList.put(entry.getKey(),playerJList.getOrDefault(entry.getKey(),0)+ entry.getValue());
            }
        }
        StringBuilder sb=new StringBuilder();
        sb.append("Attack from " ).append(att.get(i).get(index1).getPlayerName()).append(" has the following units: [ ");
        for(Map.Entry<UnitType,Integer> entry:playerIList.entrySet()){
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("; ");
        }
        sb.append("]\n");

        sb.append("Attack from " ).append(att.get(j).get(index2).getPlayerName()).append(" has the following units: [ ");
        for(Map.Entry<UnitType,Integer> entry:playerJList.entrySet()){
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("; ");
        }
        sb.append("]\n");
        System.out.print(sb.toString());
    }


    /**
     * This is method for the battle progress, which will record the deducting units in each fight,
     * and report winners as well as deleted failure attacks
     *
     *     private final HashMap<Territory, HashMap<UnitType,Integer>> unitAddMap;
     *
     * @param att        attackList
     * @param attackTerr the territory that lose in this unit fight
     * @param i          winner
     * @param j          loser
     */
    public void battleStage(ArrayList<ArrayList<Attack>> att, Territory attackTerr, int i, int j,int index1, int index2,UnitType type1,UnitType type2) {
        printList(att,i,j,index1,index2);
        //If the lost attack still have more than one unit, it will not be deleted from the list,
        //only minus one unit
        if (att.get(j).get(index2).getAllUnitNums() > 1) {
            att.get(j).get(index2).removeUnit(type2);
            if(unitMinusMap.containsKey(attackTerr)){
                HashMap<UnitType,Integer> map=unitMinusMap.get(attackTerr);
                map.put(type2,map.getOrDefault(type2,0)+1);
                unitMinusMap.put(attackTerr,map);
            }else{
                HashMap<UnitType,Integer> map=new HashMap<>();
                map.put(type2,1);
                unitMinusMap.put(attackTerr,map);
            }
            //unitMinusMap.put(attackTerr, unitMinusMap.getOrDefault(attackTerr, 0) + 1);
            String announce1 = "Attacker " + att.get(i).get(index1).getPlayerName() + " wins in this turn!\n";
            attackDetailsSB.append(announce1);
            System.out.print(announce1);
        }
        //If the lost attack now only have one unit, then it will be deleted after deducting this unit.
        else if (att.get(j).get(index2).getAllUnitNums()== 1) {
            att.get(j).get(index2).removeUnit(type2);
            if(unitMinusMap.containsKey(attackTerr)){
                HashMap<UnitType,Integer> map=unitMinusMap.get(attackTerr);
                map.put(type2,map.getOrDefault(type2,0)+1);
                unitMinusMap.put(attackTerr,map);
            }else{
                HashMap<UnitType,Integer> map=new HashMap<>();
                map.put(type2,1);
                unitMinusMap.put(attackTerr,map);
            }
            //unitMinusMap.put(attackTerr, unitMinusMap.getOrDefault(attackTerr, 0) + 1);
            if (att.get(j).size() == 1) {
                //If this attack has no other alliance units from other territories,
                // then it will be deleted and the player ends his/her attack now
                String announce2 = "Attacker " + att.get(j).get(index2).getPlayerName() + " failed! Deleted from this list.\n";
                attackDetailsSB.append(announce2);
                System.out.print(announce2);
            } else {
                //If this attack still has other alliance units from other territories, the player's attack will go on,
                //so we will not announce he/she being deleted from the list.
                String announce3 = "Attacker " + att.get(i).get(index1).getPlayerName() + " wins in this turn!\n";
                attackDetailsSB.append(announce3);
                System.out.print(announce3);
            }
            att.get(j).remove(att.get(j).get(index2));
            if (att.get(j).size() == 0) {
                att.remove(att.get(j));
            }
        }
        System.out.println("----------------------------------------------------------------------------");

    }


    /**
     * This method announce the winner of this battle, change the destination owner after fights,
     * and counting the units that should be moving into the new land as winner.
     *
     * private final HashMap<Territory, HashMap<UnitType,Integer>> unitMinusMap;
     *
     * @param att     attackList
     * @param desTerr destination territory
     */
    public void announceWinner(ArrayList<ArrayList<Attack>> att, Territory desTerr) {
        String s1 = "Attacker " + att.get(0).get(0).getPlayerName() + " has won this battle!\n";
        String s2 = "----------------------------------------------------------------------------\n\n";
        attackDetailsSB.append(s1);
        attackDetailsSB.append(s2);
        System.out.print(s1);
        System.out.print(s2);
        Territory finalTerr = gameMap.getTerritory(att.get(0).get(0).getTo());
        //record all units that remains after the fight,
        //which are units that will occupy the new land.
        HashMap<UnitType,Integer> remainAtt=new HashMap<>();
        for (int k = 0; k < att.get(0).size(); k++) {
            HashMap<UnitType,Integer> remains=att.get(0).get(k).getUnitList();
            HashMap<UnitType,Integer> record=unitMinusMap.get(gameMap.getTerritory(att.get(0).get(k).getFrom()));
            if(record==null){
                record=new HashMap<>();
            }
            for(Map.Entry<UnitType,Integer> entry:remains.entrySet()){
                record.put(entry.getKey(),record.getOrDefault(entry.getKey(),0)+ entry.getValue());
            }
            unitMinusMap.put(gameMap.getTerritory(att.get(0).get(k).getFrom()),record);

            for(Map.Entry<UnitType,Integer> entry:remains.entrySet()){
                int l=remainAtt.getOrDefault(entry.getKey(),0)+ entry.getValue();
                remainAtt.put(entry.getKey(),remainAtt.getOrDefault(entry.getKey(),0)+ entry.getValue());
            }
            //remainAtt += att.get(0).get(k).getNumUnits();
        }
        unitAddMap.put(finalTerr, remainAtt);
        Player winner=getPlayer(att.get(0).get(0).getPlayerName());
        winner.getPlayerTerrs().add(desTerr);
        Player loser=desTerr.getPlayerOwner();
        loser.getPlayerTerrs().remove(desTerr);
        //change the owner of the territory
        desTerr.changePlayerOwner(getPlayer(att.get(0).get(0).getPlayerName()));
        desTerr.changeOwner(att.get(0).get(0).getPlayerName());
    }


    /**
     * change the units number according to the hashMaps.
     * This stage should be executed after the fights, as all battles happen simultaneously
     *
     * HashMap<Territory, HashMap<UnitType,Integer>> unitMinusMap;
     */
    public void changeUnit() {

        for(Map.Entry<Territory, HashMap<UnitType,Integer>> entry: unitMinusMap.entrySet()){
            Territory currTerr = entry.getKey();
            HashMap<UnitType,Integer> map=entry.getValue();
            for(Map.Entry<UnitType,Integer> innerEntry:map.entrySet()){
                for(int i=0;i<innerEntry.getValue();i++){
                    currTerr.removeUnit(innerEntry.getKey());
                }
            }
        }

        for(Map.Entry<Territory, HashMap<UnitType,Integer>> entry: unitAddMap.entrySet()){
            Territory currTerr = entry.getKey();
            HashMap<UnitType,Integer> map=entry.getValue();
            for(Map.Entry<UnitType,Integer> innerEntry:map.entrySet()){
                for(int i=0;i<innerEntry.getValue();i++) {
                    currTerr.addUnit(innerEntry.getKey());
                }
            }
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
     */
    public void addPlayer(Player p) {
        playerList.add(p);
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

    /**
     * Set the player id
     *
     * @param id player id
     */
    public void setPlayerId(int id) {
        this.header.setPlayerId(id);
    }

    /**
     * Get the player id
     *
     * @return player id
     */
    public int getPlayerId() {
        return this.header.getPlayerId();
    }

    /**
     * Set the player name
     *
     * @param name player name
     */
    public void setPlayerName(String name) {
        this.header.setPlayerName(name);
    }

    /**
     * Get the player name
     *
     * @return player name
     */
    public String getPlayerName() {
        return this.header.getPlayerName();
    }

    /**
     * Set the game state
     *
     * @param state game state
     */
    public void setGameState(State state) {
        this.header.setState(state);
    }

    /**
     * Get the game state
     *
     * @return game state
     */
    public State getGameState() {
        return this.header.getState();
    }

    /**
     * Increase the turn number by 1
     */
    public void turnComplete() {
        this.header.turnComplete();
    }

    /**
     * Set the winner id
     *
     * @param playerId winner id
     */
    public void setWinnerId(int playerId) {
        this.header.setWinnerId(playerId);
    }

    /**
     * Get the winner id
     *
     * @return winner id
     */
    public int getWinnerId() {
        return this.header.getWinnerId();
    }

    /**
     * Add the loser id
     *
     * @param playerId loser id
     */
    public void addLoserId(int playerId) {
        this.header.addLoserId(playerId);
    }

    /**
     * Get the loser id
     *
     * @return loser id
     */
    public boolean isLoser(int playerId) {
        return this.header.isLoser(playerId);
    }

    /**
     * Get the turn number
     *
     * @return turn number
     */
    public int getTurn() {
        return this.header.getTurn();
    }

    /**
     * Add the move and attack collections to the turn list
     * private final ArrayList<HashMap<Integer, ArrayList<Turn>>> turnList;
     *
     * @param playerId   player id
     * @param moveTurn   move turn
     * @param attackTurn attack turn
     */
    public void addToTurnMap(int playerId, MoveTurn moveTurn, AttackTurn attackTurn) {
        HashMap<Integer, ArrayList<Turn>> turnMap = new HashMap<>();
        ArrayList<Turn> newTurn = new ArrayList<>();
        newTurn.add(moveTurn);
        newTurn.add(attackTurn);
        turnMap.put(playerId, newTurn);
        this.turnList.add(turnMap);
    }

    /**
     * Get the turn list
     *
     * @return turn list
     */
    public ArrayList<HashMap<Integer, ArrayList<Turn>>> getTurnList() {
        return this.turnList;
    }


//    public void allocateTerritories() {
//        GameMap gameMap = this.getMap();
//        int numTerrs = gameMap.getNumTerritories();
//        int numPlayers = this.getNumPlayers();
//        ArrayList<Territory> terrs = gameMap.getTerritories();
//        ArrayList<Player> players = this.getPlayerList();
//        for (int i = 0; i < numTerrs; i++) {
//            players.get(i / (numTerrs / numPlayers)).expandTerr(terrs.get(i));
//            terrs.get(i).changePlayerOwner(players.get(i / (numTerrs / numPlayers)));
//            terrs.get(i).changeOwner(players.get(i / (numTerrs / numPlayers)).getPlayerName());
//        }
//    }



    /**
     * Allocate territories to players,
     * and allocate corresponding resources to each territory.
     *
     * Each territory could be one of these six types: Plain, Cliff, Canyon, Desert, Forest, Wetland;
     * With different type, they have different initial resources.
     * Each player will be equally assigned with various types of territories.
     */
    public void allocateTerritories(){
        GameMap gameMap = this.getMap();
        int numTerrs = gameMap.getNumTerritories();
        int numPlayers = this.getNumPlayers();
        ArrayList<Territory> terrs = gameMap.getTerritories();
        ArrayList<Player> players = this.getPlayerList();

        for (int i = 0; i < numTerrs; i++){
            players.get(i / (numTerrs / numPlayers)).expandTerr(terrs.get(i));
            terrs.get(i).changePlayerOwner(players.get(i / (numTerrs / numPlayers)));
            terrs.get(i).changeOwner(players.get(i / (numTerrs / numPlayers)).getPlayerName());
        }
        switch (numPlayers){
            case(2):
                ArrayList<String> typeNames1=new ArrayList<String>(Arrays.asList("plain", "plain", "cliff","cliff",
                        "canyon","canyon","desert","desert","forest","forest","wetland","wetland"));
                Collections.shuffle(typeNames1);
                for(int i = 0; i < 12; i++){
                    terrs.get(i).setType(typeNames1.get(i));
                }
                Collections.shuffle(typeNames1);
                for(int i = 12; i < 24; i++){
                    terrs.get(i).setType(typeNames1.get(i-12));
                }
                break;
            case(3):
                ArrayList<String> typeNames2=new ArrayList<String>(Arrays.asList("plain", "plain", "cliff",
                        "canyon","desert","forest","forest","wetland"));
                Collections.shuffle(typeNames2);
                for(int i = 0; i < 8; i++){
                    terrs.get(i).setType(typeNames2.get(i));
                }
                Collections.shuffle(typeNames2);
                for(int i = 8; i < 16; i++){
                    terrs.get(i).setType(typeNames2.get(i-8));
                }
                Collections.shuffle(typeNames2);
                for(int i = 16; i < 24; i++){
                    terrs.get(i).setType(typeNames2.get(i-16));
                }
                break;
            case(4):
                ArrayList<String> typeNames3=new ArrayList<String>(Arrays.asList("plain", "cliff",
                        "canyon","desert","forest","wetland"));
                Collections.shuffle(typeNames3);
                for(int i = 0; i < 6; i++){
                    terrs.get(i).setType(typeNames3.get(i));
                }
                Collections.shuffle(typeNames3);
                for(int i = 6; i < 12; i++){
                    terrs.get(i).setType(typeNames3.get(i-6));
                }
                Collections.shuffle(typeNames3);
                for(int i = 12; i < 18; i++){
                    terrs.get(i).setType(typeNames3.get(i-12));
                }
                Collections.shuffle(typeNames3);
                for(int i = 18; i < 24; i++){
                    terrs.get(i).setType(typeNames3.get(i-18));
                }
                break;
        }


    }


}
