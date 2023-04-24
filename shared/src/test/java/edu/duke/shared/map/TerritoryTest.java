package edu.duke.shared.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

import edu.duke.shared.player.Player;
import edu.duke.shared.unit.Unit;
import edu.duke.shared.unit.UnitType;

public class TerritoryTest {

    @Test
    public void addUnit() {
    }

    @Test
    public void constructor2() {
        String name = "Terr";
        Player p = new Player(0, new Socket());
        String pn = "Player";
        p.setPlayerName(pn);
        ArrayList<Unit> ua = new ArrayList<>();
        ua.add(new Unit("Gnome"));
        HashSet<int[]> coords = new HashSet<>();
        coords.add(new int[]{0, 1});
        HashSet<String> adjs = new HashSet<>();
        adjs.add("adj1");
        String detail = "detail";
        String type = "plain";
        Territory t = new Territory(name, p, pn, ua, coords, adjs, detail, type);

    }

    @Test
    public void checkInsideBorders() {
        String name = "Terr";
        Player p = new Player(0, new Socket());
        String pn = "Player";
        p.setPlayerName(pn);
        ArrayList<Unit> ua = new ArrayList<>();
        ua.add(new Unit("Gnome"));
        HashSet<int[]> coords = new HashSet<>();
        coords.add(new int[]{0, 0});
        coords.add(new int[]{3, 3});
        HashSet<String> adjs = new HashSet<>();
        adjs.add("adj1");
        String detail = "detail";
        String type = "plain";
        Territory t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertTrue(t.checkInsideBorders(2, 2));
        assertFalse(t.checkInsideBorders(4, 2));
    }

    @Test
    public void removeUnitByName() {
        Territory t = new Territory("t");
        t.addUnit(UnitType.GNOME);
        t.addUnit(UnitType.WEREWOLF);
        assertTrue(t.removeUnitByName(Unit.convertUnitTypeToString(UnitType.GNOME)));
        assertFalse(t.removeUnitByName(Unit.convertUnitTypeToString(UnitType.GNOME)));
    }

    @Test
    public void getDetails() {
        String name = "Terr";
        Player p = new Player(0, new Socket());
        String pn = "Player";
        p.setPlayerName(pn);
        ArrayList<Unit> ua = new ArrayList<>();
        ua.add(new Unit("Gnome"));
        HashSet<int[]> coords = new HashSet<>();
        coords.add(new int[]{0, 0});
        coords.add(new int[]{3, 3});
        HashSet<String> adjs = new HashSet<>();
        adjs.add("adj1");
        String detail = "detail";
        String type = "plain";
        Territory t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals(detail, t.getDetails());
    }

    @Test
    public void minusCoins() {
        Territory t = new Territory("t");
        t.addCoins(100);
        t.minusCoins(50);
        assertEquals(50, t.getCoins());
    }

    @Test
    public void minusHorns() {
        Territory t = new Territory("t");
        t.addHorns(100);
        t.minusHorns(50);
        assertEquals(50, t.getHorns());
    }

    @Test
    public void getReources() {
        Territory t = new Territory("t");
        assertFalse(t.getReources()[0]);
        assertFalse(t.getReources()[1]);
    }

    @Test
    public void checkUnicornLand() {
        Territory t = new Territory("t");
        assertFalse(t.checkUnicornLand());
        t.setUnicornLand();
        assertTrue(t.checkUnicornLand());

    }

    @Test
    public void getType() {
        String name = "Terr";
        Player p = new Player(0, new Socket());
        String pn = "Player";
        p.setPlayerName(pn);
        ArrayList<Unit> ua = new ArrayList<>();
        ua.add(new Unit("Gnome"));
        HashSet<int[]> coords = new HashSet<>();
        coords.add(new int[]{0, 0});
        coords.add(new int[]{3, 3});
        HashSet<String> adjs = new HashSet<>();
        adjs.add("adj1");
        String detail = "detail";
        String type = "plain";
        Territory t = new Territory(name, p, pn, ua, coords, adjs, detail, type);

        assertEquals("plain", t.getType());
    }

    @Test
    public void getType_HPStyle() {
        String name = "Terr";
        Player p = new Player(0, new Socket());
        String pn = "Player";
        p.setPlayerName(pn);
        ArrayList<Unit> ua = new ArrayList<>();
        ua.add(new Unit("Gnome"));
        HashSet<int[]> coords = new HashSet<>();
        coords.add(new int[]{0, 0});
        coords.add(new int[]{3, 3});
        HashSet<String> adjs = new HashSet<>();
        adjs.add("adj1");
        String detail = "detail";
        String type = "plain";
        Territory t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("Enchanted Plain", t.getType_HPStyle());
        type = "cliff";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("Spellbound Cliff", t.getType_HPStyle());
        type = "canyon";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("Arcane Canyon", t.getType_HPStyle());
        type = "desert";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("Bewitched Desert", t.getType_HPStyle());
        type = "forest";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("Forbidden Forest", t.getType_HPStyle());
        type = "mountain";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("Enchanted Wetland", t.getType_HPStyle());
        type = "asdad";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("Not Defined", t.getType_HPStyle());
    }

    @Test
    public void getDetails_HPStyle() {
        String name = "Terr";
        Player p = new Player(0, new Socket());
        String pn = "Player";
        p.setPlayerName(pn);
        ArrayList<Unit> ua = new ArrayList<>();
        ua.add(new Unit("Gnome"));
        HashSet<int[]> coords = new HashSet<>();
        coords.add(new int[]{0, 0});
        coords.add(new int[]{3, 3});
        HashSet<String> adjs = new HashSet<>();
        adjs.add("adj1");
        String detail = "detail";
        String type = "plain";
        Territory t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("The grass would sway and whisper ancient secrets, occasionally concealing rare magical creatures and hidden portals to other realms. The plain would be the site of ancient magical battles, with the possibility of discovering enchanted artifacts.", t.getDetails_HPStyle());
        type = "cliff";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("These towering cliffs would have an almost magnetic pull, enticing daring magical creatures to live on the cliff face. Flying broomsticks and magical transportation methods would be necessary for access, and the cliffs might even change shape or location to prevent trespassers.", t.getDetails_HPStyle());
        type = "canyon";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("A vast, mysterious canyon that contains peculiar magical ecosystems and is home to an array of magical creatures. The canyon walls might be etched with ancient runes, while the wind carries the faint echoes of long-forgotten spells.", t.getDetails_HPStyle());
        type = "desert";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("A seemingly endless expanse of sand with magical mirages, hidden oases, and rare magical flora that can survive the harsh conditions. Sandstorms in this desert are not only natural but could also be the result of ancient magical curses or protective enchantments.", t.getDetails_HPStyle());
        type = "forest";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("An already magical terrain in the Harry Potter universe, the Forbidden Forest is a dense and dangerous place filled with magical creatures, some friendly and others hostile. Dark magic, ancient secrets, and hidden paths abound, making it a place of wonder and fear.", t.getDetails_HPStyle());
        type = "mountain";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("A magical wetland where the water's surface shimmers with an otherworldly glow, hiding countless magical creatures below. The flora would have magical properties, and the area would be a haven for potion ingredients and rare creatures like magical amphibians and insects.", t.getDetails_HPStyle());
        type = "wetland";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("", t.getDetails_HPStyle());
        type = "asdfgasd";
        t = new Territory(name, p, pn, ua, coords, adjs, detail, type);
        assertEquals("Not Defined", t.getDetails_HPStyle());
    }

    @Test
    public void checkNifflerLand() {
        Territory t = new Territory("t");
        assertFalse(t.checkNifflerLand());
        t.setNifflerLand();
        assertTrue(t.checkNifflerLand());

    }

    @Test
    public void getUpdateValue() {
        String s1 = Unit.convertUnitTypeToString(UnitType.GNOME);
        String s2 = Unit.convertUnitTypeToString(UnitType.WEREWOLF);
        Territory t = new Territory("t");
        t.getUpdateValue(s1, s2);

    }

    @Test
    public void upgradeUnit() {
        String s1 = Unit.convertUnitTypeToString(UnitType.GNOME);
        String s2 = Unit.convertUnitTypeToString(UnitType.WEREWOLF);
        Territory t = new Territory("t");
        for (int i = 0; i < 10; i++) t.addUnit(UnitType.GNOME);
        t.addHorns(1000);
        t.upgradeUnit(s1, s2, 3);

    }
}