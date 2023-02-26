package edu.duke.shared;

import edu.duke.shared.Map;
import edu.duke.shared.Territory;
import edu.duke.shared.Unit;

public class InitMap {
    private final Map map;
    private final int numTerritories;
    /*
     * Initialize Map by height and width
     * @param height Map height
     * @param width Map width
     * @param numTerritories Number of territories on this map
     */
    public InitMap(int height, int width, int numTerritories) {
        this.numTerritories = numTerritories;
        this.map = new Map(height, width, numTerritories);
    }

    /*
     * Initialize Map by logic
     */
    public Map myLogic() {
        for (int i = 0; i < this.numTerritories; i++) {
            Territory t = new Territory("YOUR_NAME");
            // Add your logic here
            // Then add territories to this.map
            map.addTerritory(t);
        }
        return this.map;
    }

    /*
     * Initialize Map by template
     * ONLY USED FOR TESTING
     */
    public Map myTemplateMap() {
        for (int i = 0; i < this.numTerritories; i++) {
            // Create a territory
            Territory t;
            // Add territory owner
            if (i < 6) {
                t = new Territory("A" + i);
                t.changeOwner("green");
            } else if (i < 12) {
                t = new Territory("B" + (i - 6));
                t.changeOwner("blue");
            } else {
                t = new Territory("C" + (i - 12));
                t.changeOwner("orange");
            }
            // Add 6 NORMAL units
            for (int j = 0; j < 6; j++) {
                t.addUnit(new Unit("Normal"));
            }
            // Add coordinates and adjacent territories
            switch (i) {
                case 0:
                    t.addCoordinateHelper(t, 3, 3, 0, 0);
                    t.addCoordinateHelper(t, 3, 3, 1, 0);
                    t.addCoordinateHelper(t, 3, 3, 2, 0);
                    t.addCoordinateHelper(t, 3, 3, 0, 1);
                    t.addAdjacent("A1");
                    break;
                case 1:
                    t.addCoordinateHelper(t, 3, 3, 0, 2);
                    t.addCoordinateHelper(t, 3, 3, 1, 1);
                    t.addCoordinateHelper(t, 3, 3, 1, 2);
                    t.addCoordinateHelper(t, 3, 3, 2, 1);
                    t.addCoordinateHelper(t, 3, 3, 2, 2);
                    t.addCoordinateHelper(t, 3, 3, 3, 0);
                    t.addCoordinateHelper(t, 3, 3, 3, 1);
                    t.addAdjacent("A2");
                    t.addAdjacent("C0");
                    t.addAdjacent("C1");
                    t.addAdjacent("A5");
                    t.addAdjacent("A0");
                    break;
                case 2:
                    t.addCoordinateHelper(t, 3, 3, 4, 0);
                    t.addCoordinateHelper(t, 3, 3, 4, 1);
                    t.addCoordinateHelper(t, 3, 3, 5, 1);
                    t.addCoordinateHelper(t, 3, 3, 6, 1);
                    t.addCoordinateHelper(t, 3, 3, 7, 1);
                    t.addAdjacent("A1");
                    t.addAdjacent("C0");
                    t.addAdjacent("A3");
                    t.addAdjacent("A4");
                    t.addAdjacent("B0");
                    break;
                case 3:
                    t.addCoordinateHelper(t, 3, 3, 5, 0);
                    t.addCoordinateHelper(t, 3, 3, 6, 0);
                    t.addAdjacent("A2");
                    t.addAdjacent("B0");
                    break;
                case 4:
                    t.addCoordinateHelper(t, 3, 3, 8, 1);
                    t.addAdjacent("A2");
                    t.addAdjacent("B0");
                    t.addAdjacent("B1");
                    break;
                case 5:
                    t.addCoordinateHelper(t, 3, 3, 0, 3);
                    t.addCoordinateHelper(t, 3, 3, 1, 3);
                    t.addCoordinateHelper(t, 3, 3, 0, 4);
                    t.addCoordinateHelper(t, 3, 3, 1, 4);
                    t.addAdjacent("A1");
                    t.addAdjacent("C1");
                    t.addAdjacent("C2");
                    break;
                case 6:
                    t.addCoordinateHelper(t, 3, 3, 3, 2);
                    t.addCoordinateHelper(t, 3, 3, 3, 3);
                    t.addCoordinateHelper(t, 3, 3, 3, 4);
                    t.addCoordinateHelper(t, 3, 3, 4, 2);
                    t.addCoordinateHelper(t, 3, 3, 5, 2);
                    t.addCoordinateHelper(t, 3, 3, 6, 2);
                    t.addCoordinateHelper(t, 3, 3, 6, 3);
                    t.addCoordinateHelper(t, 3, 3, 7, 2);
                    t.addAdjacent("A3");
                    t.addAdjacent("A2");
                    t.addAdjacent("A4");
                    t.addAdjacent("B1");
                    break;
                case 7:
                    t.addCoordinateHelper(t, 3, 3, 2, 3);
                    t.addCoordinateHelper(t, 3, 3, 2, 4);
                    t.addAdjacent("B0");
                    t.addAdjacent("A4");
                    t.addAdjacent("C0");
                    t.addAdjacent("B4");
                    t.addAdjacent("C5");
                    break;
                case 8:
                    t.addCoordinateHelper(t, 3, 3, 0, 5);
                    t.addCoordinateHelper(t, 3, 3, 1, 5);
                    t.addCoordinateHelper(t, 3, 3, 2, 5);
                    t.addAdjacent("C0");
                    t.addAdjacent("C3");
                    t.addAdjacent("B3");
                    break;
                case 9:
                    t.addCoordinateHelper(t, 3, 3, 3, 5);
                    t.addCoordinateHelper(t, 3, 3, 4, 5);
                    t.addCoordinateHelper(t, 3, 3, 5, 5);
                    t.addCoordinateHelper(t, 3, 3, 6, 5);
                    t.addAdjacent("C0");
                    t.addAdjacent("B2");
                    t.addAdjacent("C3");
                    t.addAdjacent("B4");
                    break;
                case 10:
                    t.addCoordinateHelper(t, 3, 3, 7, 5);
                    t.addCoordinateHelper(t, 3, 3, 8, 5);
                    t.addCoordinateHelper(t, 3, 3, 9, 5);
                    t.addCoordinateHelper(t, 3, 3, 9, 4);
                    t.addAdjacent("B3");
                    t.addAdjacent("B5");
                    t.addAdjacent("B1");
                    t.addAdjacent("C0");
                    t.addAdjacent("C3");
                    t.addAdjacent("C4");
                    t.addAdjacent("C5");
                    break;
                case 11:
                    t.addCoordinateHelper(t, 3, 3, 9, 3);
                    t.addAdjacent("C0");
                    t.addAdjacent("B1");
                    t.addAdjacent("B4");
                    break;
                case 12:
                    t.addCoordinateHelper(t, 3, 3, 7, 0);
                    t.addCoordinateHelper(t, 3, 3, 8, 0);
                    t.addAdjacent("A1");
                    t.addAdjacent("A2");
                    t.addAdjacent("B2");
                    t.addAdjacent("B3");
                    t.addAdjacent("B4");
                    t.addAdjacent("B5");
                    t.addAdjacent("C1");
                    t.addAdjacent("C3");
                    break;
                case 13:
                    t.addCoordinateHelper(t, 3, 3, 9, 0);
                    t.addCoordinateHelper(t, 3, 3, 9, 1);
                    t.addCoordinateHelper(t, 3, 3, 9, 2);
                    t.addCoordinateHelper(t, 3, 3, 8, 2);
                    t.addAdjacent("A1");
                    t.addAdjacent("A5");
                    t.addAdjacent("C0");
                    t.addAdjacent("C2");
                    break;
                case 14:
                    t.addCoordinateHelper(t, 3, 3, 4, 3);
                    t.addCoordinateHelper(t, 3, 3, 4, 4);
                    t.addAdjacent("A5");
                    t.addAdjacent("C1");
                    t.addAdjacent("C3");
                    break;
                case 15:
                    t.addCoordinateHelper(t, 3, 3, 5, 3);
                    t.addCoordinateHelper(t, 3, 3, 5, 4);
                    t.addAdjacent("B2");
                    t.addAdjacent("B3");
                    t.addAdjacent("B4");
                    t.addAdjacent("C0");
                    t.addAdjacent("C2");
                    t.addAdjacent("C4");
                    break;
                case 16:
                    t.addCoordinateHelper(t, 3, 3, 6, 4);
                    t.addCoordinateHelper(t, 3, 3, 7, 4);
                    t.addCoordinateHelper(t, 3, 3, 8, 4);
                    t.addCoordinateHelper(t, 3, 3, 8, 3);
                    t.addAdjacent("B4");
                    t.addAdjacent("C3");
                    t.addAdjacent("C5");
                    break;
                case 17:
                    t.addCoordinateHelper(t, 3, 3, 7, 3);
                    t.addAdjacent("B1");
                    t.addAdjacent("B4");
                    t.addAdjacent("C4");
                    break;
                default:
                    break;
            }
            // add the territory to the map
            map.addTerritory(t);
        }
        return this.map;
    }

}
