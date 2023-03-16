package edu.duke.shared.unit;

public class UnitCheck {

    /**
     * Check if the unit name is valid
     *
     * @param num Unit number in the territory
     * @return True if the unit number is valid, false otherwise
     */
    public boolean checkUnitNumInput(int num) {
        if (num < 0 || num > 24) {
            return false;
        }
        return true;
    }

    /**
     * Check if the total unit number is valid
     *
     * @param num Unit number in total
     * @return True if the unit number is valid, false otherwise
     */
    public boolean checkTotalUnitNum(int num) {
        if (num < 0 || num > 24) {
            return false;
        }
        return true;
    }


}
