package edu.duke.shared.unit;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Unit implements Serializable {
    // Unit name
    private final UnitType type;
    // Unit attack power
    private final int bonus;
    private final int cost;

    /**
     * Initialize Unit by name
     *
     * @param name Unit name
     */
    public Unit(String name) {
        UnitType type = convertStringToUnitType(name);
        this.type = type;
        // Switch case for different types of units
        switch (type) {
            case GNOME:
                this.bonus = 0;
                this.cost = 0;
                break;
            case DWARF:
                this.bonus = 1;
                this.cost = 3;
                break;
            case HOUSE_ELF:
                this.bonus = 3;
                this.cost = 11;
                break;
            case GOBLIN:
                this.bonus = 5;
                this.cost = 30;
                break;
            case VAMPIRE:
                this.bonus = 8;
                this.cost = 55;
                break;
            case CENTAUR:
                this.bonus = 11;
                this.cost = 90;
                break;
            case WEREWOLF:
                this.bonus = 15;
                this.cost = 140;
                break;
            default:
                this.bonus = 0;
                this.cost = 0;
                break;
        }
    }

    /**
     * Initialize Unit by name and attack/defense/hit point power
     *
     * @param name    Unit name
     * @param attack  Unit attack power
     * @param defense Unit defense power
     */
    public Unit(String name, int attack, int defense, int hp, int bonus) {
        this.type = convertStringToUnitType(name);
        this.bonus = bonus;
        this.cost = 0;
    }

    /**
     * Get unit name
     *
     * @return unit name
     */
    public UnitType getType() {
        return this.type;
    }


    public int getBonus() {
        return this.bonus;
    }

    public int getCost() {
        return this.cost;
    }


    /**
     * Convert string to UnitType
     *
     * @param name Unit name
     * @return UnitType
     */
    public static UnitType convertStringToUnitType(String name) {
        switch (name) {
            case "Gnome":
                return UnitType.GNOME;
            case "Dwarf":
                return UnitType.DWARF;
            case "House-elf":
                return UnitType.HOUSE_ELF;
            case "Goblin":
                return UnitType.GOBLIN;
            case "Vampire":
                return UnitType.VAMPIRE;
            case "Centaur":
                return UnitType.CENTAUR;
            case "Werewolf":
                return UnitType.WEREWOLF;
            default:
                return UnitType.GNOME;
        }
    }

    public static String getName(UnitType type) {
        switch (type) {
            case GNOME:
                return "Gnome";
            case DWARF:
                return "Dwarf";
            case HOUSE_ELF:
                return "House-elf";
            case GOBLIN:
                return "Goblin";
            case VAMPIRE:
                return "Vampire";
            case CENTAUR:
                return "Centaur";
            case WEREWOLF:
                return "Werewolf";
            default:
                return "Gnome";
        }
    }

    public static ArrayList<String> getNextLevel(UnitType type, int level) {
        ArrayList<String> nextLevel = new ArrayList<>();
        switch (type) {
            case GNOME:
                nextLevel.add("Dwarf");
            case DWARF:
                if (level >= 2)
                    nextLevel.add("House-elf");
            case HOUSE_ELF:
                if (level >= 3)
                    nextLevel.add("Goblin");
            case GOBLIN:
                if (level >= 4)
                    nextLevel.add("Vampire");
            case VAMPIRE:
                if (level >= 5)
                    nextLevel.add("Centaur");
            case CENTAUR:
                if (level >= 6)
                    nextLevel.add("Werewolf");
                break;
            case WEREWOLF:
                return null;
        }
        return nextLevel;
    }
}
