package edu.duke.risc.ui.model;

import edu.duke.shared.unit.UnitType;

public class UnitDataModel {
    // Unit name
    String name;
    // Max number of units
    int max_number;
    // Number of units
    int number;

    /**
     * Constructor
     *
     * @param name       unit name
     * @param max_number max number of units
     */
    public UnitDataModel(String name, int max_number) {
        this.name = name;
        this.max_number = max_number;
    }

    /**
     * Get unit name
     *
     * @return unit name
     */
    public String getName() {
        return name;
    }

    /**
     * Get max number of units
     *
     * @return max number of units
     */
    public int getMax() {
        return max_number;
    }

    /**
     * Set max number of units
     *
     * @param max max number of units
     */
    public void setMax(int max) {
        this.max_number = max;
    }

    /**
     * Get number of units
     *
     * @return number of units
     */
    public int getNumber() {
        return number;
    }

    /**
     * Set number of units
     *
     * @param number number of units
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Get unit type
     *
     * @return unit type
     */
    public UnitType getType() {
        return UnitType.valueOf(name.toUpperCase());
    }
}
