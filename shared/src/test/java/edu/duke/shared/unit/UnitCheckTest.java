package edu.duke.shared.unit;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class UnitCheckTest {

    @Test
    public void checkUnitNumInput() {
        Assertions.assertTrue(UnitCheck.checkUnitNumInput(0));
        Assertions.assertTrue(UnitCheck.checkUnitNumInput(24));
        Assertions.assertFalse(UnitCheck.checkUnitNumInput(-1));
        Assertions.assertFalse(UnitCheck.checkUnitNumInput(25));
    }

    @Test
    public void checkTotalUnitNum() {
        Assertions.assertTrue(UnitCheck.checkTotalUnitNum(0));
        Assertions.assertTrue(UnitCheck.checkTotalUnitNum(24));
        Assertions.assertFalse(UnitCheck.checkTotalUnitNum(-1));
        Assertions.assertFalse(UnitCheck.checkTotalUnitNum(25));
    }
}