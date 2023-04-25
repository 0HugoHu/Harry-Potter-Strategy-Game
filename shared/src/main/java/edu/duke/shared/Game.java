package edu.duke.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import edu.duke.shared.helper.Dice;
import edu.duke.shared.helper.Header;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.MapFactory;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Horcrux;
import edu.duke.shared.player.House;
import edu.duke.shared.player.Player;
import edu.duke.shared.player.SkillState;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.MoveTurn;
import edu.duke.shared.turn.Turn;
import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

public class Game implements Serializable {
    // Number of units at the beginning
    private final static int numTerritories = 24;
    // Map width and height
    private final static int mapWidth = 60;
    private final static int mapHeight = 30;

    // Game meta data
    private final Header header;
    // Number of players
    private final int numPlayers;
    private final ArrayList<Player> playerList;
    // Map
    private final GameMap gameMap;
    // TurnMap<playerId, ArrayList<Turn>>
    private final HashMap<Integer, ArrayList<Turn>> turnList;

    //AttackList for all attackers aiming at the same destination;
    //String is the destination name, ArrayList is the list for all attackers.
    //If attacks come from the same player,then they will be put into the same inner arrayList
    private final HashMap<String, ArrayList<ArrayList<Attack>>> attackList;

    //map for recording all units that should be deducted after the battle
    //(which are units lost in the battle)
    private final HashMap<Territory, HashMap<UnitType, Integer>> unitMinusMap;

    //map for recording all units that should be added after the battle
    //(which are the winning units occupying the new land)
    private final HashMap<Territory, HashMap<UnitType, Integer>> unitAddMap;

    //StringBuilder for recording the details of the battle
    private final StringBuilder attackDetailsSB;


    /**
     * Initialize Game by number of players
     *
     * @param numPlayers Number of players
     */
    public Game(int numPlayers, int numUnits) {
        this(numPlayers, numUnits, new MapFactory(Game.mapHeight, Game.mapWidth, Game.numTerritories).createRandomMap());
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
        this.turnList = new HashMap<>();
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

    // TODO: calculate cost
    public int calculateOrderCost(int dis, int num) {
        return dis * num / 2;
    }

    public void useHorcrux() {
        for (Player p : playerList) {
            for (Map.Entry<Horcrux, Integer> entry : p.getHorcruxesList().entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    if (entry.getKey().equals(Horcrux.SNAKE)) {
                        useSnake(p);
                    }
                    if (entry.getKey().equals(Horcrux.LOCKET)) {
                        useLocket(p);
                    }
                    if (entry.getKey().equals(Horcrux.RING)) {
                        useRing(p);
                    }
                }
            }
        }
    }

    public void useSkill() {
        for (Player p : playerList) {
            if (p.getSkillState().equals(SkillState.IN_EFFECT)) {
                if (p.getHouse().equals(House.GRYFFINDOR)) {
                    UseSkillGryffindor(p);
                }
                if (p.getHouse().equals(House.GRYFFINDOR)) {
                    UseSkillGryffindor(p);
                }
                if (p.getHouse().equals(House.SLYTHERIN)) {
                    UseSkillSytherin(p);
                }
            }
        }
    }

    /**
     * Allow player to use the snake Horcrux, and get one random territory from
     * another player
     *
     * @param p
     */
    public void useSnake(Player p) {
        for (Territory t : p.getPlayerTerrs()) {
            if (t.getAdjacents().size() > 0) {
                for (String nearBy : t.getAdjacents()) {
                    String nearLand = nearBy;
                    Territory terr = gameMap.getTerritory(nearLand);
                    Player formerOwner = terr.getPlayerOwner();
                    formerOwner.setSnakeTarget();
                    terr.changeOwner(p.getPlayerName());
                    terr.changePlayerOwner(p);
                    p.getPlayerTerrs().add(terr);
                    formerOwner.getPlayerTerrs().remove(terr);
                    return;
                }
            }
        }
    }


    /**
     * Allow player to use the Locket, which will randomly
     * clear one of its enemies' Gnomes.
     *
     * @param p
     */
    public void useLocket(Player p) {
        for (Territory t : p.getPlayerTerrs()) {
            HashSet<String> nearLand = t.getAdjacents();
            for (String near : nearLand) {
                Territory desTerr = gameMap.getTerritory(near);
                ArrayList<Unit> units = desTerr.getUnits();
                if (convertToMap(units).get(UnitType.DWARF) > 0) {
                    units.remove(UnitType.DWARF);
                }
                return;
            }
        }
    }


    /**
     * Allow player to use the ring, which will add 10 Gnomes to its own land.
     */
    public void useRing(Player p) {
        p.removeFromHorcruxStorage(Horcrux.RING, 1);
        p.addToHorcruxUsage(Horcrux.RING, 1);
        int count = 0;
        for (Territory t : p.getPlayerTerrs()) {
            while (count < 10) {
                t.addUnit(UnitType.GNOME);
                count++;
            }
            return;
        }

    }


    /**
     * Allow player to use the Skill of SLYTHERIN, which kills all the
     * WereWolf on other players' territories
     *
     * @param p
     */
    public void UseSkillSytherin(Player p) {
        if (p.skillSlytherin()) {
            for (Territory terr : gameMap.getTerritories()) {
                if (!terr.getPlayerOwner().getHouse().equals(House.SLYTHERIN)) {
                    if (convertToMap(terr.getUnits()).get(UnitType.WEREWOLF) > 0) {
                        int k = convertToMap(terr.getUnits()).get(UnitType.WEREWOLF);
                        for (int i = 0; i < k; i++) {
                            terr.getUnits().remove(UnitType.WEREWOLF);
                        }
                    }
                }
            }
        }
    }


    /**
     * Allow player to use the skill of GRYFFINDOR,
     * which is add 30 Gnomes to its own territory
     *
     * @param p
     */
    public void UseSkillGryffindor(Player p) {
        if (p.skillGryffindor()) {
            int count = 0;
            while (count < 30) {
                for (Territory t : p.getPlayerTerrs()) {
                    if (count < 30) {
                        t.addUnit(UnitType.GNOME);
                        count++;
                    }
                }
            }
        }
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
                        attArr.add(new Attack(attacks.get(i).getFrom(), attacks.get(i).getTo(),
                                attacks.get(i).getUnitList(), attacks.get(i).getPlayerName(), attacks.get(i).getHouse()));
                        flag = true;
                    }
                }
                //if there's no other attacks from the same player, then we should put it into a new inner list
                if (!flag) {
                    ArrayList<Attack> att2 = new ArrayList<>();
                    att2.add(new Attack(attacks.get(i).getFrom(), attacks.get(i).getTo(),
                            attacks.get(i).getUnitList(), attacks.get(i).getPlayerName(), attacks.get(i).getHouse()));
                    att.add(att2);
                }
                attackList.put(attacks.get(i).getTo(), att);
            } else {
                //if the attack destination is a new one, put it on the list with different destination key
                ArrayList<Attack> att = new ArrayList<>();
                att.add(new Attack(attacks.get(i).getFrom(), attacks.get(i).getTo(),
                        attacks.get(i).getUnitList(), attacks.get(i).getPlayerName(), attacks.get(i).getHouse()));
                ArrayList<ArrayList<Attack>> attArr = new ArrayList<>();
                attArr.add(att);
                attackList.put(attacks.get(i).getTo(), attArr);
            }
        }
    }

    /**
     * Get the bonus from the unit type
     *
     * @param type unit type
     * @return bonus
     */
    public int getBonusFromType(UnitType type) {
        switch (type) {
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

    /**
     * Compare the unit type and return the higher one
     *
     * @param type1 unit type 1
     * @param type2 unit type 2
     * @return 1 if type1 is higher, 0 if type2 is higher
     */
    public int compareUnitHigh(UnitType type1, UnitType type2) {
        int bonus1 = getBonusFromType(type1);
        int bonus2 = getBonusFromType(type2);
        if (bonus1 > bonus2) {
            return 1;
        } else {
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
            if (desTerr.getPlayerOwner().getHouse().equals(House.HUFFLEPUFF) && desTerr.getPlayerOwner().skillHufflepuff()) {
                announceHuffSituation(desTerr);
                continue;
            }

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
                System.out.println("unlist: " + att.get(i).get(0).getFrom() + " " + att.get(i).get(0).getTo() + " " + att.get(i).get(0).getUnitList().size());
                UnitType type1 = att.get(i).get(0).getHighestType();
                int index1 = 0;
                for (int k = 0; k < att.get(i).size(); k++) {
                    int output = compareUnitHigh(att.get(i).get(k).getHighestType(), type1);
                    if (output == 1) {
                        index1 = k;
                        type1 = att.get(i).get(k).getHighestType();
                    }
                }
                Territory attackTerr1 = gameMap.getTerritory(att.get(i).get(index1).getFrom());
                int bonus1 = att.get(i).get(0).getBonus(type1);
                System.out.println("The highest unit level for attacker " + attackTerr1.getOwner() + " is " + type1);
                //This is the defender on the list order

                int count1 = 20 + bonus1;

                Random random = new Random();
                int chance = random.nextInt(2);
                if (chance == 1 && att.get(j).get(0).getHouse().equals(House.GRYFFINDOR)) {
                    count1 = (int) (count1 * 1.5);
                }

                UnitType type2 = att.get(j).get(0).getLowestType();
                int index2 = 0;
                for (int k = 0; k < att.get(j).size(); k++) {
                    int output = compareUnitHigh(att.get(j).get(k).getHighestType(), type2);
                    if (output == 0) {
                        index2 = k;
                        type2 = att.get(j).get(k).getHighestType();
                    }
                }
                Territory attackTerr2 = gameMap.getTerritory(att.get(j).get(index2).getFrom());

                System.out.println("The lowest unit level for defender " + attackTerr2.getOwner() + " is " + type2);
                int bonus2 = att.get(j).get(0).getBonus(type2);
                int count2 = bonus2 + 20;

                int dice1 = new Dice(count1).getDice();
                int dice2 = new Dice(count2).getDice();

                Random random2 = new Random();
                int chance2 = random2.nextInt(5);
                if ((dice2 <= dice1) && chance2 == 1 && att.get(j).get(0).getHouse().equals(House.HUFFLEPUFF)) {
                    dice2 = dice1 + 1;
                }

                if (dice1 > dice2) {
                    battleStage(att, attackTerr2, i, j, index1, index2, type1, type2);
                } else {
                    battleStage(att, attackTerr1, j, i, index2, index1, type2, type1);
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

    /**
     * Convert the unit list to a map
     *
     * @param units unit list
     * @return unit map
     */
    public HashMap<UnitType, Integer> convertToMap(ArrayList<Unit> units) {
        HashMap<UnitType, Integer> map = new HashMap<>();
        for (Unit unit : units) {
            map.put(unit.getType(), map.getOrDefault(unit.getType(), 0) + 1);
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
        int desPlayerId = desTerr.getPlayerOwner().getPlayerId();
        ArrayList<Turn> desTurn = turnList.get(desPlayerId);
        assert desTurn != null;
        AttackTurn attackTurn = (AttackTurn) (desTurn.get(1));
        ArrayList<Attack> atts = attackTurn.getAttacks();
        HashMap<UnitType, Integer> defenseForces = convertToMap(desTerr.getUnits());
        HashMap<UnitType, Integer> defenseForce = new HashMap<>(defenseForces);
        //int defenseForce = desTerr.getNumUnits();
        String s = "Defend Territory: " + desTerr.getName() + "\n";
        attackDetailsSB.append(s);
        System.out.print(s);
        //The defenseForce should deduct all the attacking units coming from the same territory
        if (atts.size() > 0) {
            for (Attack desAtt : atts) {
                if (desAtt.getFrom().equals(destination)) {
                    for (Map.Entry<UnitType, Integer> entry : desAtt.getUnitList().entrySet()) {
                        UnitType type = entry.getKey();
                        int nums = entry.getValue();
                        defenseForce.put(type, defenseForce.get(type) - nums);
                    }
                    //defenseForce = defenseForce - desAtt.getNumUnits();
                }
            }
        }
        int totalRemainForces = 0;
        for (Map.Entry<UnitType, Integer> entry : defenseForce.entrySet()) {
            totalRemainForces += entry.getValue();
        }
        //If defender territory still has units to defend, put it on the attack list
        if (totalRemainForces > 0) {
            Attack defenderAtt = new Attack(destination, destination, defenseForce, desTerr.getOwner(), desTerr.getPlayerOwner().getHouse());
            ArrayList<Attack> attTOAdd = new ArrayList<>();
            attTOAdd.add(defenderAtt);
            att.add(attTOAdd);
        }
    }

    /**
     * Print the attack list
     *
     * @param att    attack list
     * @param i      player i
     * @param j      player j
     * @param index1 index1
     * @param index2 index2
     */
    public void printList(ArrayList<ArrayList<Attack>> att, int i, int j, int index1, int index2) {
        HashMap<UnitType, Integer> playerIList = new HashMap<>();
        HashMap<UnitType, Integer> playerJList = new HashMap<>();
        for (int k = 0; k < att.get(i).size(); k++) {
            for (Map.Entry<UnitType, Integer> entry : att.get(i).get(k).getUnitList().entrySet()) {
                playerIList.put(entry.getKey(), playerIList.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        for (int k = 0; k < att.get(j).size(); k++) {
            for (Map.Entry<UnitType, Integer> entry : att.get(j).get(k).getUnitList().entrySet()) {
                playerJList.put(entry.getKey(), playerJList.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Attack from ").append(att.get(i).get(index1).getPlayerName()).append(" has the following units: [ ");
        for (Map.Entry<UnitType, Integer> entry : playerIList.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("; ");
        }
        sb.append("]\n");

        sb.append("Attack from ").append(att.get(j).get(index2).getPlayerName()).append(" has the following units: [ ");
        for (Map.Entry<UnitType, Integer> entry : playerJList.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("; ");
        }
        sb.append("]\n");
        System.out.print(sb);
    }


    /**
     * This is method for the battle progress, which will record the deducting units in each fight,
     * and report winners as well as deleted failure attacks
     * <p>
     * private final HashMap<Territory, HashMap<UnitType,Integer>> unitAddMap;
     *
     * @param att        attackList
     * @param attackTerr the territory that lose in this unit fight
     * @param i          winner
     * @param j          loser
     */
    public void battleStage(ArrayList<ArrayList<Attack>> att, Territory attackTerr, int i, int j, int index1, int index2, UnitType type1, UnitType type2) {
        printList(att, i, j, index1, index2);
        //If the lost attack still have more than one unit, it will not be deleted from the list,
        //only minus one unit
        if (att.get(j).get(index2).getAllUnitNums() > 1) {
            att.get(j).get(index2).removeUnit(type2);
            if (unitMinusMap.containsKey(attackTerr)) {
                HashMap<UnitType, Integer> map = unitMinusMap.get(attackTerr);
                map.put(type2, map.getOrDefault(type2, 0) + 1);
                unitMinusMap.put(attackTerr, map);
            } else {
                HashMap<UnitType, Integer> map = new HashMap<>();
                map.put(type2, 1);
                unitMinusMap.put(attackTerr, map);
            }
            //unitMinusMap.put(attackTerr, unitMinusMap.getOrDefault(attackTerr, 0) + 1);
            String announce1 = "Attacker " + att.get(i).get(index1).getPlayerName() + " wins in this turn!\n";
            attackDetailsSB.append(announce1);
            System.out.print(announce1);
        }
        //If the lost attack now only have one unit, then it will be deleted after deducting this unit.
        else if (att.get(j).get(index2).getAllUnitNums() == 1) {
            att.get(j).get(index2).removeUnit(type2);
            if (unitMinusMap.containsKey(attackTerr)) {
                HashMap<UnitType, Integer> map = unitMinusMap.get(attackTerr);
                map.put(type2, map.getOrDefault(type2, 0) + 1);
                unitMinusMap.put(attackTerr, map);
            } else {
                HashMap<UnitType, Integer> map = new HashMap<>();
                map.put(type2, 1);
                unitMinusMap.put(attackTerr, map);
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

    public void announceHuffSituation(Territory desTerr) {
        Player winner = desTerr.getPlayerOwner();
        String s1 = "Player " + winner.getPlayerName() + " has used the HUFFLEPUFF Skill: Steadfast Roots, " +
                "all attacks will be defended! ";
        System.out.println(s1);

    }


    /**
     * This method announce the winner of this battle, change the destination owner after fights,
     * and counting the units that should be moving into the new land as winner.
     * <p>
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
        HashMap<UnitType, Integer> remainAtt = new HashMap<>();
        for (int k = 0; k < att.get(0).size(); k++) {
            HashMap<UnitType, Integer> remains = att.get(0).get(k).getUnitList();
            HashMap<UnitType, Integer> record = unitMinusMap.get(gameMap.getTerritory(att.get(0).get(k).getFrom()));
            if (record == null) {
                record = new HashMap<>();
            }
            for (Map.Entry<UnitType, Integer> entry : remains.entrySet()) {
                record.put(entry.getKey(), record.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
            unitMinusMap.put(gameMap.getTerritory(att.get(0).get(k).getFrom()), record);

            for (Map.Entry<UnitType, Integer> entry : remains.entrySet()) {
                int l = remainAtt.getOrDefault(entry.getKey(), 0) + entry.getValue();
                remainAtt.put(entry.getKey(), remainAtt.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
            //remainAtt += att.get(0).get(k).getNumUnits();
        }
        unitAddMap.put(finalTerr, remainAtt);
        Player winner = getPlayer(att.get(0).get(0).getPlayerName());
        winner.getPlayerTerrs().add(desTerr);
        Player loser = desTerr.getPlayerOwner();
        loser.getPlayerTerrs().remove(desTerr);
        //change the owner of the territory
        desTerr.changePlayerOwner(getPlayer(att.get(0).get(0).getPlayerName()));
        desTerr.changeOwner(att.get(0).get(0).getPlayerName());
    }


    /**
     * change the units number according to the hashMaps.
     * This stage should be executed after the fights, as all battles happen simultaneously
     * <p>
     * HashMap<Territory, HashMap<UnitType,Integer>> unitMinusMap;
     */
    public void changeUnit() {

        for (Map.Entry<Territory, HashMap<UnitType, Integer>> entry : unitMinusMap.entrySet()) {
            Territory currTerr = entry.getKey();
            HashMap<UnitType, Integer> map = entry.getValue();
            for (Map.Entry<UnitType, Integer> innerEntry : map.entrySet()) {
                for (int i = 0; i < innerEntry.getValue(); i++) {
                    currTerr.removeUnit(innerEntry.getKey());
                }
            }
        }

        for (Map.Entry<Territory, HashMap<UnitType, Integer>> entry : unitAddMap.entrySet()) {
            Territory currTerr = entry.getKey();
            HashMap<UnitType, Integer> map = entry.getValue();
            for (Map.Entry<UnitType, Integer> innerEntry : map.entrySet()) {
                for (int i = 0; i < innerEntry.getValue(); i++) {
                    currTerr.addUnit(innerEntry.getKey());
                }
            }
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
     * Get the player id
     *
     * @return player id
     */
    public int getPlayerId() {
        return this.header.getPlayerId();
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
     * Get the player name
     *
     * @return player name
     */
    public String getPlayerName() {
        return this.header.getPlayerName();
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
     * Get the game state
     *
     * @return game state
     */
    public State getGameState() {
        return this.header.getState();
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
     * Increase the turn number by 1
     */
    public void turnComplete() {
        this.header.turnComplete();
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
     * Set the winner id
     *
     * @param playerId winner id
     */
    public void setWinnerId(int playerId) {
        this.header.setWinnerId(playerId);
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
    public ArrayList<Integer> getLoserId() {
        return this.header.loserIds;
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
        ArrayList<Turn> newTurn = new ArrayList<>();
        newTurn.add(moveTurn);
        newTurn.add(attackTurn);
        this.turnList.put(playerId, newTurn);
    }

    /**
     * Get the turn list
     *
     * @return turn list
     */
    public HashMap<Integer, ArrayList<Turn>> getTurnList() {
        return this.turnList;
    }

    public void expandPlayerTerrs(Player p, ArrayList<Territory> terrs, ArrayList<Integer> ids) {
        for (int i : ids) {
            p.expandTerr(terrs.get(i));
            terrs.get(i).changePlayerOwner(p);
            terrs.get(i).changeOwner(p.getPlayerName());
        }
    }

    /**
     * Allocate territories to players,
     * and allocate corresponding resources to each territory.
     * <p>
     * Each territory could be one of these six types: Plain, Cliff, Canyon, Desert, Forest, Wetland;
     * With different type, they have different initial resources.
     * Each player will be equally assigned with various types of territories.
     */
    public void allocateTerritories() {
        GameMap gameMap = this.getMap();
        int numTerrs = gameMap.getNumTerritories();
        int numPlayers = this.getNumPlayers();
        ArrayList<Territory> terrs = gameMap.getTerritories();
        ArrayList<Player> players = this.getPlayerList();
        // Adjacent allocations

        switch (numPlayers) {
            case (2):
                expandPlayerTerrs(players.get(0), terrs, new ArrayList<>(Arrays.asList(0, 1, 4, 5, 8, 9, 12, 13, 16, 17, 20, 21)));
                expandPlayerTerrs(players.get(1), terrs, new ArrayList<>(Arrays.asList(2, 3, 6, 7, 10, 11, 14, 15, 18, 19, 22, 23)));
                break;
            case (3):
                expandPlayerTerrs(players.get(0), terrs, new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7)));
                expandPlayerTerrs(players.get(1), terrs, new ArrayList<>(Arrays.asList(8, 9, 12, 13, 16, 17, 20, 21)));
                expandPlayerTerrs(players.get(2), terrs, new ArrayList<>(Arrays.asList(10, 11, 14, 15, 18, 19, 22, 23)));
                break;
            case (4):
                expandPlayerTerrs(players.get(0), terrs, new ArrayList<>(Arrays.asList(0, 1, 4, 5, 8, 9)));
                expandPlayerTerrs(players.get(1), terrs, new ArrayList<>(Arrays.asList(2, 3, 6, 7, 10, 11)));
                expandPlayerTerrs(players.get(2), terrs, new ArrayList<>(Arrays.asList(12, 13, 16, 17, 20, 21)));
                expandPlayerTerrs(players.get(3), terrs, new ArrayList<>(Arrays.asList(14, 15, 18, 19, 22, 23)));
                break;
        }

        //Random Allocations
        /*
        ArrayList<Integer> tarr= new ArrayList<>();
        for (int i=0;i<24;i++) tarr.add(i);
        Collections.shuffle(tarr);
        for (int i=0;i<numPlayers;i++){
            ArrayList<Integer> ti= new ArrayList<>();
            for (int j=0;j<numTerrs/numPlayers;j++) ti.add(tarr.get(numTerrs/numPlayers*i+j));
            expandPlayerTerrs(players.get(i),terrs,ti);
        }
        */
        /*
        for (int i = 0; i < numTerrs; i++) {
            players.get(i / (numTerrs / numPlayers)).expandTerr(terrs.get(i));
            terrs.get(i).changePlayerOwner(players.get(i / (numTerrs / numPlayers)));
            terrs.get(i).changeOwner(players.get(i / (numTerrs / numPlayers)).getPlayerName());
        }
        */
        ArrayList<Territory> ts;
        switch (numPlayers) {
            case (2):
                ArrayList<String> typeNames1 = new ArrayList<>(Arrays.asList("plain", "plain", "cliff", "cliff",
                        "canyon", "canyon", "desert", "desert", "forest", "forest", "wetland", "wetland"));
                Collections.shuffle(typeNames1);
                ts = new ArrayList<>(players.get(0).getPlayerTerrs());
                for (int i = 0; i < 12; i++) {
                    ts.get(i).setType(typeNames1.get(i));
                }
                Collections.shuffle(typeNames1);
                ts = new ArrayList<>(players.get(1).getPlayerTerrs());
                for (int i = 0; i < 12; i++) {
                    ts.get(i).setType(typeNames1.get(i));
                }
                break;
            case (3):
                ArrayList<String> typeNames2 = new ArrayList<>(Arrays.asList("plain", "plain", "cliff",
                        "canyon", "desert", "forest", "forest", "wetland"));
                Collections.shuffle(typeNames2);
                ts = new ArrayList<>(players.get(0).getPlayerTerrs());
                for (int i = 0; i < 8; i++) {
                    ts.get(i).setType(typeNames2.get(i));
                }
                Collections.shuffle(typeNames2);
                ts = new ArrayList<>(players.get(1).getPlayerTerrs());
                for (int i = 0; i < 8; i++) {
                    ts.get(i).setType(typeNames2.get(i));
                }
                Collections.shuffle(typeNames2);
                ts = new ArrayList<>(players.get(2).getPlayerTerrs());
                for (int i = 0; i < 8; i++) {
                    ts.get(i).setType(typeNames2.get(i));
                }
                break;
            case (4):
                ArrayList<String> typeNames3 = new ArrayList<>(Arrays.asList("plain", "cliff",
                        "canyon", "desert", "forest", "wetland"));
                Collections.shuffle(typeNames3);
                ts = new ArrayList<>(players.get(0).getPlayerTerrs());
                for (int i = 0; i < 6; i++) {
                    ts.get(i).setType(typeNames3.get(i));
                }
                Collections.shuffle(typeNames3);
                ts = new ArrayList<>(players.get(1).getPlayerTerrs());
                for (int i = 0; i < 6; i++) {
                    ts.get(i).setType(typeNames3.get(i));
                }
                Collections.shuffle(typeNames3);
                ts = new ArrayList<>(players.get(2).getPlayerTerrs());
                for (int i = 0; i < 6; i++) {
                    ts.get(i).setType(typeNames3.get(i));
                }
                Collections.shuffle(typeNames3);
                ts = new ArrayList<>(players.get(3).getPlayerTerrs());
                for (int i = 0; i < 6; i++) {
                    ts.get(i).setType(typeNames3.get(i));
                }
                break;
        }


    }

    /**
     * If force end game is true, end the game
     */
    public void forceEndGame() {
        this.header.forceEndGame();
    }

    /**
     * Check if the game is forced to end
     *
     * @return true if the game is forced to end
     */
    public boolean isForceEndGame() {
        return this.header.isForceEndGame();
    }

    /**
     * Set new horcrux
     */
    public void setNewHorcrux(Horcrux horcrux, int playerId) {
        this.header.setNewHorcrux(horcrux, playerId);
    }

    /**
     * Set no horcrux
     */
    public void setNoHorcrux() {
        this.header.setNoHorcrux();
    }

    /**
     * Get the horcrux
     *
     * @return horcrux
     */
    public String getNewHorcrux() {
        return this.header.getNewHorcrux();
    }

    /**
     * Get the horcrux affect
     */
    public String getHorcruxAffect() {
        // TODO: Test
        return null;
    }

}
