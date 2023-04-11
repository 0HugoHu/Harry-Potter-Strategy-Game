package edu.duke.risc.ui.model;

public class TerrDataModel {
    String terrName;
    int number;

    public TerrDataModel(String terrName) {
        this.terrName = terrName;
        this.number = 0;
    }

    public String getName() {
        return terrName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
