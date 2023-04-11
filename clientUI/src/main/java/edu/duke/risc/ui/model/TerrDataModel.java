package edu.duke.risc.ui.model;

public class TerrDataModel {
    // Territory name
    String terrName;
    // Number of units
    int number;

    /**
     * Constructor
     *
     * @param terrName territory name
     */
    public TerrDataModel(String terrName) {
        this.terrName = terrName;
        this.number = 0;
    }

    /**
     * Get territory name
     *
     * @return territory name
     */
    public String getName() {
        return terrName;
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
}
