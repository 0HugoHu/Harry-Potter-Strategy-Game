package edu.duke.shared;

import java.io.Serializable;

public class Unit implements Serializable {
    // Unit name
    private final String name;
    // Unit attack power
    private final int attack;
    // Unit defense power
    private final int defense;
    // Unit hit points
    private final int hp;

    /**
     * Initialize Unit by name
     * @param name Unit name
     */
    public Unit(String name) {
        this.name = name;
        // Switch case for different types of units
        switch (name) {
            case "Normal":
                this.attack = 2;
                this.defense = 1;
                this.hp = 2;
                break;
            case "Defense":
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
     * @param name Unit name
     * @param attack Unit attack power
     * @param defense Unit defense power
     */
    public Unit(String name, int attack, int defense, int hp) {
        this.name = name;
        this.attack = attack;
        this.defense = defense;
        this.hp = hp;
    }

    /**
     * Get unit name
     * @return unit name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get unit attack power
     * @return unit attack power
     */
    public int getAttack() {
        return this.attack;
    }

    /**
     * Get unit defense power
     * @return unit defense power
     */
    public int getDefense() {
        return this.defense;
    }

    /**
     * Get unit hp value
     * @return unit hp value
     */
    public int getHp(){return this.hp;}
}
