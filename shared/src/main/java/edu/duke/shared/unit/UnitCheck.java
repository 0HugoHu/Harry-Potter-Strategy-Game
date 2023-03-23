package edu.duke.shared.unit;

public abstract class UnitCheck {

    /**
     * Check if the unit name is valid
     *
     * @param num Unit number in the territory
     * @return True if the unit number is valid, false otherwise
     */
    public static boolean checkUnitNumInput(int num) {
        return num >= 0 && num <= 24;
    }

    /**
     * Check if the total unit number is valid
     *
     * @param num Unit number in total
     * @return True if the unit number is valid, false otherwise
     */
    public static boolean checkTotalUnitNum(int num) {
        return num >= 0 && num <= 24;
    }


}
