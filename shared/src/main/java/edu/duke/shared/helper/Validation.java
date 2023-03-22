package edu.duke.shared.helper;

import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import edu.duke.shared.Game;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;
import edu.duke.shared.turn.Attack;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Move;
import edu.duke.shared.turn.MoveTurn;

public class Validation {
    /**
     * Check if the territory belongs to the player and
     * the player has enough units to attack or move
     *
     * @param map            GameMap
     * @param territoryName1 Source territory name
     * @param territoryName2 Destination territory name
     * @param numUnits       Number of units
     * @param playerName     Player name
     * @throws IllegalArgumentException if the order is invalid
     */
    public static void checkIllegalOrderInput(GameMap map, String territoryName1, String territoryName2, int numUnits, String playerName) throws IllegalArgumentException {
        if (territoryName1!=null && map.getTerritory(territoryName1) == null)
            throw new IllegalArgumentException("The source territory does not exist\n");
        if (territoryName2!=null && map.getTerritory(territoryName2) == null)
            throw new IllegalArgumentException("The destination territory does not exist\n");
        if (numUnits < 0)
            throw new IllegalArgumentException("The number must be greater than or equal to 0\n");
        if (!checkTerritory(map, territoryName1, playerName))
            throw new IllegalArgumentException("The source territory does not belong to you\n");
    }

    /**
     * Check if the territory belongs to the player
     *
     * @param moveTurn Collection of moves
     * @return true if the territory belongs to the player
     */
    public static boolean checkMoves(MoveTurn moveTurn) {
        MoveTurn newTurn = new MoveTurn(moveTurn.getMap(), moveTurn.getIndex(), moveTurn.getPlayerName());
        for (Move m : moveTurn.getMoves()) {
            try {
                checkMove(newTurn, null, m.getFrom(), m.getTo(), m.getNumUnits());
                newTurn.addMove(m);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the territory belongs to the player
     *
     * @param moveTurn       Collection of moves
     * @param territoryName1 Source territory name
     * @param territoryName2 Destination territory name
     * @param numUnits       Number of units
     */
    public static void checkMove(MoveTurn moveTurn, AttackTurn attackTurn,String territoryName1, String territoryName2, int numUnits) throws IllegalArgumentException {
        GameMap map = moveTurn.getMap();
        checkIllegalOrderInput(map, territoryName1, territoryName2, numUnits, moveTurn.getPlayerName());
        int totalUnits = map.getTerritory(territoryName1).getUnits().size();
        /*
        for (Move m : moveTurn.getMoves()) {
            if (m.getFrom().equals(territoryName1)) totalUnits -= m.getNumUnits();
            if (m.getTo().equals(territoryName1)) totalUnits += m.getNumUnits();
        }
        */
        totalUnits+=numOfChangeUnits(moveTurn,attackTurn,territoryName1);
        if (totalUnits < numUnits)
            throw new IllegalArgumentException("The usable units in source are only " + totalUnits + " units\n");
        if (!checkPathExist(map, territoryName1, territoryName2))
            throw new IllegalArgumentException("There is no path from source to destination\n");
    }

    /**
     * Check if the territory belongs to the player
     *
     * @param attackTurn Collection of attacks
     * @return true if the territory belongs to the player
     */
    public static boolean checkAttacks(AttackTurn attackTurn) {
        AttackTurn newTurn = new AttackTurn(attackTurn.getMap(), attackTurn.getIndex(), attackTurn.getPlayerName());
        for (Attack m : attackTurn.getAttacks()) {
            try {
                checkAttack(newTurn, null, m.getFrom(), m.getTo(), m.getNumUnits());
                newTurn.addAttack(m);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the territory belongs to the player
     *
     * @param attackTurn     Collection of attacks
     * @param territoryName1 Source territory name
     * @param territoryName2 Destination territory name
     * @param numUnits       Number of units
     */
    public static void checkAttack(AttackTurn attackTurn, MoveTurn moveTurn, String territoryName1, String territoryName2, int numUnits) throws IllegalArgumentException {
        GameMap map = attackTurn.getMap();
        checkIllegalOrderInput(map, territoryName1, territoryName2, numUnits, attackTurn.getPlayerName());
        if (checkTerritory(map, territoryName2, attackTurn.getPlayerName()))
            throw new IllegalArgumentException("You should not attack your territory\n");
        int totalUnits = map.getTerritory(territoryName1).getUnits().size();
        /*
        for (Attack m : attackTurn.getAttacks()) {
            if (m.getFrom().equals(territoryName1)) totalUnits -= m.getNumUnits();
        }
        */
        totalUnits+=numOfChangeUnits(moveTurn,attackTurn,territoryName1);
        if (totalUnits < numUnits)
            throw new IllegalArgumentException("The usable units in source are only " + totalUnits + " units\n");
        if (!checkAdjacent(map, territoryName1, territoryName2))
            throw new IllegalArgumentException("The source and destination is not adjacent\n");
    }

    /**
     * Check if the territory belongs to the player
     *
     * @param map            GameMap
     * @param territoryName1 Source territory name
     * @param territoryName2 Destination territory name
     * @return true if the territory belongs to the player
     */
    public static boolean checkPathExist(GameMap map, String territoryName1, String territoryName2) {
        LinkedBlockingQueue<String> q = new LinkedBlockingQueue<>();
        HashSet<String> visited = new HashSet<>();
        q.add(territoryName1);
        visited.add(territoryName1);
        while (!q.isEmpty()) {
            String terrName = q.remove();
            Territory terr = map.getTerritory(terrName);
            if (terrName.equals(territoryName2)) return true;
            for (String adjTerrName : terr.getAdjacents()) {
                Territory adjTerr = map.getTerritory(adjTerrName);
                if (!terr.getOwner().equals(adjTerr.getOwner())) continue;
                if (visited.contains(adjTerrName)) continue;
                q.add(adjTerrName);
                visited.add(adjTerrName);
            }
        }
        return false;
    }

    /**
     * Check if the territory belongs to the player
     *
     * @param map            GameMap
     * @param territoryName1 Source territory name
     * @param territoryName2 Destination territory name
     * @return true if the territories are adjacent
     */
    public static boolean checkAdjacent(GameMap map, String territoryName1, String territoryName2) {
        return map.getTerritory(territoryName1).isAdjacent(territoryName2);
    }

    /**
     * Check if the territory belongs to the player
     *
     * @param map           GameMap
     * @param territoryName Territory name
     * @param playerName    Player name
     * @return true if the territory belongs to the player
     */
    public static boolean checkTerritory(GameMap map, String territoryName, String playerName) {
        return map.getTerritory(territoryName).getOwner().equals(playerName);
    }
    /**
     * Check if the territory belongs to the player
     *
     * @param map           GameMap
     * @param territoryName Territory name
     * @param playerName    Player name
     * @param numUnits      Number of units
     * @param totalUnits    Total number of usable units
     */
    public static void checkUnit(GameMap map,String territoryName,int numUnits,int totalUnits,String playerName) throws IllegalArgumentException{
        checkIllegalOrderInput(map,territoryName,null,numUnits,playerName);
        if (numUnits>totalUnits) throw new IllegalArgumentException("You only have "+totalUnits+" units remaining\n");
    }
    /**
     * Check if the territory belongs to the player
     *
     * @param scanner       Scanner to read
     * @return an integer read from scanner
     */
    public static int getValidNumber(Scanner scanner){
        int numUnits;
        try {
            numUnits = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: Please enter a valid number\n");
            return getValidNumber(scanner);
        }
        return numUnits;
    }
    /**
     * Return the changing units on territory
     *
     * @param moveTurn          moveTurn
     * @param attackTurn        attackTurn
     * @param territoryName     Name of territory
     * @return an integer of changing units
     */
    public static int numOfChangeUnits(MoveTurn moveTurn,AttackTurn attackTurn,String territoryName){
        int totalUnits=0;
        if (moveTurn!=null) {
            for (Move m : moveTurn.getMoves()) {
                if (m.getFrom().equals(territoryName)) totalUnits -= m.getNumUnits();
                if (m.getTo().equals(territoryName)) totalUnits += m.getNumUnits();
            }
        }
        if (attackTurn!=null) {
            for (Attack m : attackTurn.getAttacks()) {
                if (m.getFrom().equals(territoryName)) totalUnits -= m.getNumUnits();
            }
        }
        return totalUnits;
    }
}
