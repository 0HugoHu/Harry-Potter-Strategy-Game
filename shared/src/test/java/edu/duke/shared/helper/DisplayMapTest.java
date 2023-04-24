package edu.duke.shared.helper;

import org.junit.Test;

import java.net.Socket;
import java.util.ArrayList;

import edu.duke.shared.Game;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;
import edu.duke.shared.unit.Unit;

public class DisplayMapTest {

    @Test
    public void showMap() {
        GameMap map = new GameMap(30, 30, 6);
        Territory territory = new Territory("A");
        territory.changeOwner("Player1");
        territory.addCoordinate(new int[]{0, 0});
        map.addTerritory(territory);
        Territory territory2 = new Territory("B");
        territory2.changeOwner("Player2");
        territory2.addCoordinate(new int[]{1, 0});
        map.addTerritory(territory2);
        Territory territory3 = new Territory("C");
        territory3.changeOwner("Player3");
        territory3.addCoordinate(new int[]{2, 0});
        map.addTerritory(territory3);
        Territory territory4 = new Territory("D");
        territory4.changeOwner("Player4");
        territory4.addCoordinate(new int[]{3, 0});
        map.addTerritory(territory4);
        Territory territory5 = new Territory("E");
        territory5.changeOwner("Player5");
        territory5.addCoordinate(new int[]{4, 0});
        map.addTerritory(territory5);

        territory.addAdjacent("E");

        Game game = new Game(5, 24, map);
        Player player = new Player(0, new Socket());
        player.setPlayerName("Player1");
        Player player1 = new Player(1, new Socket());
        player1.setPlayerName("Player2");
        Player player2 = new Player(2, new Socket());
        player2.setPlayerName("Player3");
        Player player3 = new Player(3, new Socket());
        player3.setPlayerName("Player4");
        Player player4 = new Player(4, new Socket());
        player4.setPlayerName("Player5");

        game.addPlayer(player);
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.addPlayer(player4);


        DisplayMap displayMap = new DisplayMap(game, 0);
        DisplayMap displayMap1 = new DisplayMap(game, 1);
        DisplayMap displayMap2 = new DisplayMap(game, 2);
        DisplayMap displayMap3 = new DisplayMap(game, 3);
        DisplayMap displayMap4 = new DisplayMap(game, 4);
        displayMap.showMap();
        displayMap1.showMap();
        displayMap2.showMap();
        displayMap3.showMap();
        displayMap4.showMap();
        displayMap.showUnits(false, null, null);
        displayMap.showUnits(true, null, null);
        displayMap1.showUnits(true, null, null);
        displayMap2.showUnits(true, null, null);
        displayMap3.showUnits(true, null, null);
        displayMap4.showUnits(true, null, null);
    }

    @Test
    public void showUnits() {
    }

    @Test
    public void convertToMap() {
        GameMap map = new GameMap(30, 30, 6);
        Game game = new Game(5, 24, map);
        DisplayMap displayMap = new DisplayMap(game, 0);
        ArrayList<Unit> units = new ArrayList<>();
        units.add(new Unit("A"));
        displayMap.convertToMap(units);
    }
}