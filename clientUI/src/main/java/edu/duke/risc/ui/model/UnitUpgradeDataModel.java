package edu.duke.risc.ui.model;

import edu.duke.shared.unit.UnitType;

public class UnitUpgradeDataModel {
    String name;
    int number;
    String delta;

    public UnitUpgradeDataModel(String name, int number) {
        this.name = name;
        this.number = number;
        this.delta = "0";
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public String getDelta() {
        return delta;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setDelta(String delta) {
        this.delta = delta;
    }

    public UnitType getType() {
        return UnitType.valueOf(name.toUpperCase());
    }
}
