package edu.duke.risc.display;

import org.junit.Test;

import edu.duke.shared.GameMap;

public class DisplayGameMapTest {

    @Test
    public void testToString() {
        DisplayMap dm = new DisplayMap(new GameMap(10, 10, 18));
        dm.toString();
    }
}