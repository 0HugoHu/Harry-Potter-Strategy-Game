package edu.duke.risc.ui.model;

import edu.duke.risc.R;

public class UnitSpinnerDataModel {
    // Unit name
    String unitName;
    // Unit image
    int drawableId;

    /**
     * Constructor
     *
     * @param unitName unit name
     */
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

    /**
     * Get unit name
     *
     * @return unit name
     */
    public String getName() {
        return unitName;
    }

    /**
     * Get unit image
     *
     * @return unit image
     */
    public int getDrawableId() {
        return drawableId;
    }

}
