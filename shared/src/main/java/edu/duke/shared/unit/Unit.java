package edu.duke.shared.unit;

import java.io.Serializable;

public class Unit implements Serializable {
    // Unit name
    private final UnitType type;
    // Unit attack power
    private final int attack;
    // Unit defense power
    private final int defense;
    // Unit hit points
    private final int hp;

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
            case NORMAL:
                this.attack = 2;
                this.defense = 1;
                this.hp = 2;
                break;
            case DEFENSE:
                this.attack = 1;
                this.defense = 3;
                this.hp = 3;
                break;
            default:
                this.attack = 0;
                this.defense = 0;
                this.hp = 0;
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
    public Unit(String name, int attack, int defense, int hp) {
        this.type = convertStringToUnitType(name);
        this.attack = attack;
        this.defense = defense;
        this.hp = hp;
    }

    /**
     * Get unit name
     *
     * @return unit name
     */
    public UnitType getType() {
        return this.type;
    }

    /**
     * Get unit attack power
     *
     * @return unit attack power
     */
    public int getAttack() {
        return this.attack;
    }

    /**
     * Get unit defense power
     *
     * @return unit defense power
     */
    public int getDefense() {
        return this.defense;
    }

    /**
     * Get unit hp value
     *
     * @return unit hp value
     */
    public int getHp() {
        return this.hp;
    }

    /**
     * Convert string to UnitType
     *
     * @param name Unit name
     * @return UnitType
     */
    public static UnitType convertStringToUnitType(String name) {
        switch (name) {
            case "Normal":
                return UnitType.NORMAL;
            case "Defense":
                return UnitType.DEFENSE;
            case "Basic":
                return UnitType.BASIC;
            default:
                return UnitType.NORMAL;
        }
    }
}
