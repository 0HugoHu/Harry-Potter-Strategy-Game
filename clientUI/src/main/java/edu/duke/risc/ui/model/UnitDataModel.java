package edu.duke.risc.ui.model;

import edu.duke.shared.unit.UnitType;

public class UnitDataModel {
    String name;
    int max_number;
    int number;

    public UnitDataModel(String name, int max_number) {
        this.name = name;
        this.max_number = max_number;
    }

    public String getName() {
        return name;
    }

    public int getMax() {
        return max_number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setMax(int max) {
        this.max_number = max;
    }

    public UnitType getType() {
        return UnitType.valueOf(name.toUpperCase());
    }
}
