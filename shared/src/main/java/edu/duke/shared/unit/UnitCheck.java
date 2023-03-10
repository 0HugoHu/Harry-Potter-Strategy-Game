package edu.duke.shared.unit;

public class UnitCheck {

    public boolean checkUnitNumInput(int num) {
        if (num < 0 || num > 24) {
            return false;
        }
        return true;
    }

    public boolean checkTotalUnitNum(int num) {
        if (num < 0 || num > 24) {
            return false;
        }
        return true;
    }



}
