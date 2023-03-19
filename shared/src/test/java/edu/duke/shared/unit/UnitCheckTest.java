package edu.duke.shared.unit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class UnitCheckTest {

    @Test
    public void checkUnitNumInput() {
        UnitCheck uc = new UnitCheck();
        Assertions.assertTrue(uc.checkUnitNumInput(0));
        Assertions.assertTrue(uc.checkUnitNumInput(24));
        Assertions.assertFalse(uc.checkUnitNumInput(-1));
        Assertions.assertFalse(uc.checkUnitNumInput(25));
    }

    @Test
    public void checkTotalUnitNum() {
        UnitCheck uc = new UnitCheck();
        Assertions.assertTrue(uc.checkTotalUnitNum(0));
        Assertions.assertTrue(uc.checkTotalUnitNum(24));
        Assertions.assertFalse(uc.checkTotalUnitNum(-1));
        Assertions.assertFalse(uc.checkTotalUnitNum(25));
    }
}