package edu.duke.shared.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.net.Socket;
import java.util.ArrayList;

import edu.duke.shared.map.Territory;

public class PlayerTest {

    @Test
    public void upgradeCost() {
        assertEquals(60,Player.upgradeCost(2));
        assertEquals(100,Player.upgradeCost(3));
        assertEquals(200,Player.upgradeCost(4));
        assertEquals(300,Player.upgradeCost(5));
        assertEquals(400,Player.upgradeCost(6));
        assertEquals(60,Player.upgradeCost(7));
    }

    @Test
    public void updateResources() {
        Player p = new Player(0,new Socket());
        ArrayList<Territory> terrs = new ArrayList<>();
        for (int i=0;i<3;i++){
            Territory t = new Territory("terr"+i);
            t.setType("plain");
            terrs.add(t);
        }
        p.updateResources(terrs);
        assertEquals(30,p.getHorns());
//        assertEquals(150,p.getCoins());
    }


    @Test
    public void upgradeWorldLevel() {
        Player p = new Player(0,new Socket());
        p.setHorns(10000);
        p.upgradeWorldLevel();
        assertEquals(10000,p.getHorns());
    }

    @Test
    public void setExpenseHorns() {
        Player p = new Player(0,new Socket());
        p.setHorns(100);
        assertEquals(100,p.getHorns());
        p.setExpenseHorns(50);
        assertEquals(50,p.getHorns());
    }

    @Test
    public void setExpenseCoins() {
        Player p = new Player(0,new Socket());
        p.setCoins(100);
        assertEquals(100,p.getCoins());
        p.setExpenseCoins(50);
        assertEquals(50,p.getCoins());
    }

    @Test
    public void getCoins() {
        Player p = new Player(0,new Socket());
        assertEquals(0,p.getCoins());
    }

    @Test
    public void setCoins() {
        Player p = new Player(0,new Socket());
        p.setCoins(100);
        assertEquals(100,p.getCoins());
    }

    @Test
    public void getHorns() {
        Player p = new Player(0,new Socket());
        assertEquals(0,p.getHorns());
    }

    @Test
    public void setHorns() {
        Player p = new Player(0,new Socket());
        p.setHorns(100);
        assertEquals(100,p.getHorns());
    }

    @Test
    public void expandTerr() {
        Player p = new Player(0,new Socket());
        Territory t1 = new Territory("terr1");
        Territory t2 = new Territory("terr2");
        assertTrue(p.expandTerr(t1));
        assertFalse(p.expandTerr(t1));
        assertTrue(p.expandTerr(t2));
        assertFalse(p.expandTerr(t2));
    }

    @Test
    public void removeTerr() {
        Player p = new Player(0,new Socket());
        Territory t1 = new Territory("terr1");
        Territory t2 = new Territory("terr2");
        assertTrue(p.expandTerr(t1));
        assertFalse(p.expandTerr(t1));
        assertTrue(p.expandTerr(t2));
        assertFalse(p.expandTerr(t2));
        assertTrue(p.removeTerr(t1));
        assertFalse(p.removeTerr(t1));
        assertTrue(p.removeTerr(t2));
        assertFalse(p.removeTerr(t2));
    }
}