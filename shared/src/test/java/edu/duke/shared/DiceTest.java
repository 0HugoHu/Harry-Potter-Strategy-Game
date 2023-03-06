package edu.duke.shared;

import static org.junit.Assert.*;

import org.junit.Test;

public class DiceTest {

    @Test
    public void getDice() {
        int max_point=10;
        Dice d=new Dice(max_point);
        for (int i=0;i<50;i++){
            int r=d.getDice();
            assertTrue(r>=1&&r<=max_point);
        }
    }

    @Test
    public void getMaxPoint() {
        int max_point=10;
        Dice d=new Dice(max_point);
        assertEquals(max_point,d.getMaxPoint());
    }
}