package edu.duke.risc.ui.model;

import edu.duke.shared.player.Horcrux;

public class HorcruxDataModel {
    // Horcrux name
    Horcrux name;
    // Max number of horcrux
    int max_number;
    // Number of horcrux selected
    int number;

    /**
     * Constructor
     *
     * @param name       Horcrux name
     * @param max_number Max number of horcrux
     */
    public HorcruxDataModel(Horcrux name, int max_number) {
        this.name = name;
        this.max_number = max_number;
    }

    /**
     * Get horcrux name
     *
     * @return horcrux name
     */
    public Horcrux getName() {
        return name;
    }

    /**
     * Get horcrux name
     *
     * @return horcrux name
     */
    public String getNameString() {
        switch (name) {
            case HAT:
                return "Ravenclaw's Diadem";
            case DIARY:
                return "Riddle's Diary";
            case LOCKET:
                return "Slytherin's Locket";
            case RING:
                return "Resurrection Stone";
            case CUP:
                return "Hufflepuff's Cup";
            case SNAKE:
                return "Nagini";
            default:
                return "Unknown";
        }
    }

    /**
     * Get horcrux description
     */
    public String getDesc() {
        switch (name) {
            case HAT:
                return "Immediately increase the number of unicorn horns by 150.";
            case DIARY:
                return "Randomly disable one opponent's actions for their next turn.";
            case LOCKET:
                return "Halve the number of Gnome troops for a random opponent in their next turn.";
            case RING:
                return "Instantly revive the last 10 fallen creatures.";
            case CUP:
                return "Instantly increase the number of gold coins by 300.";
            case SNAKE:
                return "Immediately randomly capture an adjacent enemy territory (troops on the territory become yours).";
            default:
                return "Unknown";
        }
    }

    /**
     * Get max number of horcurx
     *
     * @return max number of horcrux
     */
    public int getMax() {
        return max_number;
    }

    /**
     * Get number of horcrux
     *
     * @return number of horcrux
     */
    public int getNumber() {
        return number;
    }

    /**
     * Set number of horcrux
     *
     */
    public void addNumber() {
        this.number++;
    }

    /**
     * Set max number of horcrux
     *
     * @param max max number of horcrux
     */
    public void setMax(int max) {
        this.max_number = max;
    }

}
