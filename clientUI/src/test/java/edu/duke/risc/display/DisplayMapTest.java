package edu.duke.risc.display;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.duke.shared.Map;

public class DisplayMapTest {

    @Test
    public void testToString() {
        DisplayMap dm = new DisplayMap(new Map(10, 10, 18));
        dm.toString();
    }
}