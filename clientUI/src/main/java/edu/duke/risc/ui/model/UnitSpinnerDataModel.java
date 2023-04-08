package edu.duke.risc.ui.model;

import edu.duke.risc.R;
import edu.duke.shared.unit.UnitType;

public class UnitSpinnerDataModel {
    String unitName;
    int drawableId;

    public UnitSpinnerDataModel(String unitName) {
        this.unitName = unitName;
        switch (unitName) {
            case "Gnome":
                drawableId = R.drawable.gnome;
                break;
            case "Dwarf":
                drawableId = R.drawable.dwarf;
                break;
            case "House-elf":
                drawableId = R.drawable.house_elf;
                break;
            case "Goblin":
                drawableId = R.drawable.goblin;
                break;
            case "Vampire":
                drawableId = R.drawable.vampire;
                break;
            case "Centaur":
                drawableId = R.drawable.centaur;
                break;
            case "Werewolf":
                drawableId = R.drawable.werewolf;
                break;
            default:
                drawableId = R.drawable.gnome;
                break;
        }
    }

    public String getName() {
        return unitName;
    }

    public int getDrawableId() {
        return drawableId;
    }

}
