package edu.duke.risc.ui.model;

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
}
