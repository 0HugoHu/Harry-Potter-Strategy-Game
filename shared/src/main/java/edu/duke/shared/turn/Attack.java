package edu.duke.shared.turn;

import java.util.HashMap;

import edu.duke.shared.player.House;
import edu.duke.shared.unit.UnitType;

public class Attack extends Order {
    /**
     * Initialize Attack
     *
     * @param from       Territory name
     * @param to         Territory name
     * @param numUnits   Number of units
     * @param playerName Player name
     */
    public Attack(String from, String to, int numUnits, String playerName, House house) {
        super(from, to, numUnits, playerName, house);
    }

    /**
     * Initialize Attack
     *
     * @param from       Territory name
     * @param to         Territory name
     * @param unitList   Unit list
     * @param playerName Player name
     */
    public Attack(String from, String to, HashMap<UnitType, Integer> unitList, String playerName, House house) {
        super(from, to, unitList, playerName, house);
    }

    /**
     * Get the highest type of unit
     *
     * @return Highest type of unit
     */
    public UnitType getHighestType() {
        if (unitList.get(UnitType.WEREWOLF) != null && unitList.get(UnitType.WEREWOLF) != 0) {
            return UnitType.WEREWOLF;
        } else if (unitList.get(UnitType.CENTAUR) != null && unitList.get(UnitType.CENTAUR) != 0) {
            return UnitType.CENTAUR;
        } else if (unitList.get(UnitType.VAMPIRE) != null && unitList.get(UnitType.VAMPIRE) != 0) {
            return UnitType.VAMPIRE;
        } else if (unitList.get(UnitType.GOBLIN) != null && unitList.get(UnitType.GOBLIN) != 0) {
            return UnitType.GOBLIN;
        } else if (unitList.get(UnitType.HOUSE_ELF) != null && unitList.get(UnitType.HOUSE_ELF) != 0) {
            return UnitType.HOUSE_ELF;
        } else if (unitList.get(UnitType.DWARF) != null && unitList.get(UnitType.DWARF) != 0) {
            return UnitType.DWARF;
        } else {
            return UnitType.GNOME;
        }
    }

    /**
     * Get the lowest type of unit
     *
     * @return Lowest type of unit
     */
    public UnitType getLowestType() {
        if (unitList.get(UnitType.GNOME) != null && unitList.get(UnitType.GNOME) != 0) {
            return UnitType.GNOME;
        } else if (unitList.get(UnitType.DWARF) != null && unitList.get(UnitType.DWARF) != 0) {
            return UnitType.DWARF;
        } else if (unitList.get(UnitType.HOUSE_ELF) != null && unitList.get(UnitType.HOUSE_ELF) != 0) {
            return UnitType.HOUSE_ELF;
        } else if (unitList.get(UnitType.GOBLIN) != null && unitList.get(UnitType.GOBLIN) != 0) {
            return UnitType.GOBLIN;
        } else if (unitList.get(UnitType.VAMPIRE) != null && unitList.get(UnitType.VAMPIRE) != 0) {
            return UnitType.VAMPIRE;
        } else if (unitList.get(UnitType.CENTAUR) != null && unitList.get(UnitType.CENTAUR) != 0) {
            return UnitType.CENTAUR;
        } else {
            return UnitType.WEREWOLF;
        }
    }

    /**
     * Get the bonus of a unit type
     *
     * @param type Unit type
     * @return Bonus
     */
    public int getBonus(UnitType type) {
        switch (type) {
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
     * Remove a unit from this territory
     */
    public void removeUnit(UnitType type) {
        unitList.put(type, unitList.get(type) - 1);
    }

}
