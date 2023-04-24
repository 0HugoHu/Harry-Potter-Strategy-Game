package edu.duke.risc.ui.model;

import edu.duke.shared.unit.UnitType;

public class UnitUpgradeDataModel {
    // Unit name
    String name;
    // Number of units
    int number;
    // Variation
    String delta;

    /**
     * Constructor
     *
     * @param name   unit name
     * @param number number of units
     */
    public UnitUpgradeDataModel(String name, int number) {
        this.name = name;
        this.number = number;
        this.delta = "0";
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
     * Get variation
     *
     * @return variation
     */
    public String getDelta() {
        return delta;
    }

    /**
     * Set variation
     *
     * @param delta variation
     */
    public void setDelta(String delta) {
        this.delta = delta;
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
