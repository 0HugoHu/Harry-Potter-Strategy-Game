package edu.duke.shared.unit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UnitCheckTest {

    @Test
    void checkUnitNumInput() {
        UnitCheck uc = new UnitCheck();
        assertTrue(uc.checkUnitNumInput(0));
        assertTrue(uc.checkUnitNumInput(24));
        assertFalse(uc.checkUnitNumInput(-1));
        assertFalse(uc.checkUnitNumInput(25));
    }

    @Test
    void checkTotalUnitNum() {
        UnitCheck uc = new UnitCheck();
        assertTrue(uc.checkTotalUnitNum(0));
        assertTrue(uc.checkTotalUnitNum(24));
        assertFalse(uc.checkTotalUnitNum(-1));
        assertFalse(uc.checkTotalUnitNum(25));
    }
}