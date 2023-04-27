package edu.duke.shared.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import edu.duke.shared.map.Territory;

public class PlayerTest {

    @Test
    public void upgradeCost() {
        assertEquals(60, Player.upgradeCost(2));
        assertEquals(100, Player.upgradeCost(3));
        assertEquals(200, Player.upgradeCost(4));
        assertEquals(300, Player.upgradeCost(5));
        assertEquals(400, Player.upgradeCost(6));
        assertEquals(60, Player.upgradeCost(7));
    }

    @Test
    public void updateResources() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        ArrayList<Territory> terrs = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Territory t = new Territory("terr" + i);
            t.setType("plain");
            terrs.add(t);
        }
        p.updateResources(terrs);
        assertEquals(30, p.getHorns());
        p.setHouse(House.RAVENCLAW);
        p.updateResources(terrs);
        assertEquals(75, p.getHorns());
//        assertEquals(150,p.getCoins());
    }


    @Test
    public void upgradeWorldLevel() {
        Player p = new Player(0, new Socket());
        p.setHorns(10000);
        p.upgradeWorldLevel();
        assertEquals(10000, p.getHorns());
    }

    @Test
    public void setExpenseHorns() {
        Player p = new Player(0, new Socket());
        p.setHorns(100);
        assertEquals(100, p.getHorns());
        p.setExpenseHorns(50);
        assertEquals(50, p.getHorns());
    }

    @Test
    public void setExpenseCoins() {
        Player p = new Player(0, new Socket());
        p.setCoins(100);
        assertEquals(100, p.getCoins());
        p.setExpenseCoins(50);
        assertEquals(50, p.getCoins());
    }

    @Test
    public void getCoins() {
        Player p = new Player(0, new Socket());
        assertEquals(0, p.getCoins());
    }

    @Test
    public void setCoins() {
        Player p = new Player(0, new Socket());
        p.setCoins(100);
        assertEquals(100, p.getCoins());
    }

    @Test
    public void getHorns() {
        Player p = new Player(0, new Socket());
        assertEquals(0, p.getHorns());
    }

    @Test
    public void setHorns() {
        Player p = new Player(0, new Socket());
        p.setHorns(100);
        assertEquals(100, p.getHorns());
    }

    @Test
    public void expandTerr() {
        Player p = new Player(0, new Socket());
        Territory t1 = new Territory("terr1");
        Territory t2 = new Territory("terr2");
        assertTrue(p.expandTerr(t1));
        assertFalse(p.expandTerr(t1));
        assertTrue(p.expandTerr(t2));
        assertFalse(p.expandTerr(t2));
    }

    @Test
    public void removeTerr() {
        Player p = new Player(0, new Socket());
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

    @Test
    public void updateSkill() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        p.updateSkill();
        assertEquals(1, p.getSkillUsed());
    }

    @Test
    public void addSkill() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        p.addSkill();
        assertEquals(2, p.getSkillUsed());
    }


    @Test
    public void addToHorcruxStorage() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertEquals(0, p.getHorcruxStorage(Horcrux.CUP));
        p.addToHorcruxStorage(Horcrux.CUP, 2);
        assertEquals(2, p.getHorcruxStorage(Horcrux.CUP));
    }

    @Test
    public void removeFromHorcruxStorage() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertEquals(0, p.getHorcruxStorage(Horcrux.CUP));
        p.addToHorcruxStorage(Horcrux.CUP, 2);
        assertEquals(2, p.getHorcruxStorage(Horcrux.CUP));
        p.removeFromHorcruxStorage(Horcrux.CUP, 1);
        assertEquals(1, p.getHorcruxStorage(Horcrux.CUP));
    }

    @Test
    public void getHorcruxUsage() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertEquals(0, p.getHorcruxUsage(Horcrux.CUP));
    }

    @Test
    public void getHorcruxesList() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        HashMap<Horcrux, Integer> h = p.getHorcruxesList();
        assertEquals(0, h.get(Horcrux.CUP).intValue());
    }

    @Test
    public void setHorcruxesList() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        HashMap<Horcrux, Integer> h = new HashMap<>();
        p.setHorcruxesList(h);
    }

    @Test
    public void getHorcruxesStorage() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        HashMap<Horcrux, Integer> h = p.getHorcruxesStorage();
        assertEquals(0, h.get(Horcrux.CUP).intValue());
    }

    @Test
    public void setHorcruxesStorage() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        HashMap<Horcrux, Integer> h = new HashMap<>();
        p.setHorcruxesStorage(h);
    }

    @Test
    public void addToHorcruxUsage() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertEquals(0, p.getHorcruxUsage(Horcrux.CUP));
        p.addToHorcruxUsage(Horcrux.CUP, 1);
        assertEquals(1, p.getHorcruxUsage(Horcrux.CUP));

    }

    @Test
    public void removeFromHorcruxUsage() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertEquals(0, p.getHorcruxUsage(Horcrux.CUP));
        p.addToHorcruxUsage(Horcrux.CUP, 1);
        assertEquals(1, p.getHorcruxUsage(Horcrux.CUP));
        p.removeFromHorcruxUsage(Horcrux.CUP, 1);
        assertEquals(0, p.getHorcruxUsage(Horcrux.CUP));
    }

    @Test
    public void isDiaryTarget() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertFalse(p.isDiaryTarget());
    }

    @Test
    public void isSnakeTarget() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertFalse(p.isSnakeTarget());
    }

    @Test
    public void isLocketTarget() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertFalse(p.isLocketTarget());
    }

    @Test
    public void getSkillName() {
        Player p = new Player(0, new Socket());
        assertEquals("Unknown", p.getSkillName());
        p.setHouse(House.GRYFFINDOR);
        assertEquals("Lion's Courage", p.getSkillName());
        p.setHouse(House.RAVENCLAW);
        assertEquals("Wings of Wisdom", p.getSkillName());
        p.setHouse(House.HUFFLEPUFF);
        assertEquals("Steadfast Roots", p.getSkillName());
        p.setHouse(House.SLYTHERIN);
        assertEquals("Serpent's Strategy", p.getSkillName());
    }

    @Test
    public void buffHufflepuff() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertFalse(p.buffHufflepuff());
        p.setHouse(House.HUFFLEPUFF);
        assertTrue(p.buffHufflepuff());
    }

    @Test
    public void buffRavenclaw() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertFalse(p.buffRavenclaw());
        p.setHouse(House.RAVENCLAW);
        assertTrue(p.buffRavenclaw());
    }

    @Test
    public void skillHufflepuff() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertFalse(p.skillHufflepuff());
        p.setHouse(House.HUFFLEPUFF);
        assertFalse(p.skillHufflepuff());
        p.setHouse(House.GRYFFINDOR);
        p.setSkillState(SkillState.IN_EFFECT);
        assertFalse(p.skillHufflepuff());
        p.setHouse(House.HUFFLEPUFF);
        assertTrue(p.skillHufflepuff());
    }

    @Test
    public void skillRavenclaw() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertFalse(p.skillRavenclaw());
        p.setHouse(House.RAVENCLAW);
        assertFalse(p.skillRavenclaw());
        p.setHouse(House.GRYFFINDOR);
        p.setSkillState(SkillState.IN_EFFECT);
        assertFalse(p.skillRavenclaw());
        p.setHouse(House.RAVENCLAW);
        assertTrue(p.skillRavenclaw());
    }

    @Test
    public void getSkillState() {
        Player p = new Player(0, new Socket(), House.GRYFFINDOR);
        assertEquals(SkillState.NOT_USED, p.getSkillState());
    }
}