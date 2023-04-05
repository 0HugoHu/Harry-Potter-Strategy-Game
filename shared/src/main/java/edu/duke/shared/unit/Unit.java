package edu.duke.shared.unit;

import java.io.Serializable;

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
                this.bonus=0;
                this.cost=0;
                break;
            case DWARF:
                this.bonus=1;
                this.cost=3;
                break;
            case HOUSE_ELF:
                this.bonus=3;
                this.cost=11;
                break;
            case GOBLIN:
                this.bonus=5;
                this.cost=30;
                break;
            case VAMPIRE:
                this.bonus=8;
                this.cost=55;
                break;
            case CENTAUR:
                this.bonus=11;
                this.cost=90;
                break;
            case WEREWOLF:
                this.bonus=15;
                this.cost=140;
                break;
            default:
                this.bonus=0;
                this.cost=0;
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
    public Unit(String name, int attack, int defense, int hp,int bonus) {
        this.type = convertStringToUnitType(name);
        this.bonus=bonus;
        this.cost=0;
    }

    /**
     * Get unit name
     *
     * @return unit name
     */
    public UnitType getType() {
        return this.type;
    }


    public int getBonus(){
        return this.bonus;
    }

    public int getCost(){
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
}
