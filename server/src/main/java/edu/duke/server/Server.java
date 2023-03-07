package edu.duke.server;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;

import edu.duke.shared.Game;
import edu.duke.shared.GameMap;
import edu.duke.shared.Player;
import edu.duke.shared.Territory;
import edu.duke.shared.Unit;
import edu.duke.shared.thread.BaseThread;
import edu.duke.shared.thread.PlayerThread;

public class Server {
    // Port number
    private final static int PORT = 5410;
    // Gameplay controller
    private final Game game;
    // Server socket
    private ServerSocket server;

    private static final Logger logger=Logger.getLogger("serverLog.txt");

    /**
     * Main method
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        while (true) {
            // Get IP address and hostname
            InetAddress ip;
            String hostname;
            try {
                ip = InetAddress.getLocalHost();
                hostname = ip.getHostName();
                System.out.println("Your current IP address : " + ip);
                System.out.println("Your current Hostname : " + hostname);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            //TODO : user decide how many players
            //
            //
            //change the number of players here
            final int numOfPlayers = 1;
            Server server = new Server(numOfPlayers);
            System.out.println("Created a new game of " + numOfPlayers + " players.\nWaiting for players to join...\n");

            // Accept connections from players
            if (!server.acceptConnection(numOfPlayers)) {
                System.out.println("Failed to accept connections from players.\n");
                return;
            }
            //TODO : Allocate territories
            //Allocate territories
            server.receivePlayerName();
            server.allocateTerrtories();


            // Send message to all players
            if (server.sendToAllPlayers()) {
                System.out.println("Message sent to all players.\n");
            } else {
                System.out.println("Failed to send to all players.\n");
            }

            // Receive Units info from all players
            if (server.receiveUnitsInfo()){
                System.out.println("Received all units info.\n");
            }
            else{
                System.out.println("Failed to receive units from all players");
            }
            // Start Game

            if (server.startPlayingTurn()){
                System.out.println("Received all units info.\n");
            }
            else{
                System.out.println("Failed to receive units from all players");
            }

            // Close server socket
            if (server.safeClose()) {
                System.out.println("Server shut down.\n");
            } else {
                System.out.println("Failed to shut down server.\n");
            }
        }
    }

    /**
     * Initialize Server by number of players
     *
     * @param numOfPlayers Number of players
     */
    public Server(int numOfPlayers) {
        this.game = new Game(numOfPlayers);
        // Listen to the port
        try {
            this.server = new ServerSocket(PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("New Game Created.\n");
    }

    /**
     * wait for connection from all users
     *
     * @param num number of players
     * @return true if all players have joined the game, false otherwise
     */
    public boolean acceptConnection(int num) {
        // Wait until all players have joined the game
        for (int i = 0; i < num; i++) {
            try {
                // Accept connection from client
                Socket socket = this.server.accept();
                // Create a new thread for each player with index
                PlayerThread newPlayerThread = new PlayerThread(socket, this.game, i);
                // Start the thread
                Thread thread = new Thread(newPlayerThread);
                thread.start();
                //thread join in
                thread.join();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    /**
     * Send message to all players
     *
     * @return true if message is sent successfully, false otherwise
     */
    public boolean sendToAllPlayers() {
        for (int i = 0; i < this.getNumOfPlayers(); i++) {
            Socket player_socket = this.game.getPlayerList().get(i).getSocket();
            BaseThread thread = new BaseThread(player_socket, this.game);
            thread.encodeObj(this.game);
            thread.encodeObj(i);
        }
        return true;
    }

    /**
     * Close server socket
     *
     * @return true if server socket is closed successfully, false otherwise
     */
    public boolean safeClose() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Get number of players
     *
     * @return Number of players
     */
    public int getNumOfPlayers() {
        return this.game.getNumPlayers();
    }


    /**
     * Receive units placement from client
     * @return true if receive info successfully, false otherwise
     */
    private boolean receiveUnitsInfo(){
        for (int i = 0; i < this.getNumOfPlayers(); i++) {
            Player p = this.game.getPlayerList().get(i);
            Socket player_socket = p.getSocket();
            BaseThread thread = new BaseThread(player_socket, this.game);
            HashSet<Territory> terr_set=p.getPlayerTerrs();
            for (int j=0;j<terr_set.size();j++) {
                Territory terr = (Territory) thread.decodeObj();
                for (Territory t:terr_set){
                    if (terr.getName().equals(t.getName())){
                        for (Unit u:terr.getUnits()){
                            t.addUnit(u);
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean startPlayingTurn(){

        return true;
    }

    public void allocateTerrtories(){
        GameMap gameMap=this.game.getMap();
        int numTerrs=gameMap.getNumTerritories();
        int numPlayers=this.game.getNumPlayers();
        ArrayList<Territory> terrs=gameMap.getTerritories();
        ArrayList<Player> players=game.getPlayerList();
        for (int i=0;i<numTerrs;i++){
            players.get(i/numPlayers).expandTerr(terrs.get(i));
            terrs.get(i).changePlayerOwner(players.get(i/numPlayers));
            terrs.get(i).changeOwner(players.get(i/numPlayers).getPlayerName());
        }
    }

    public void receivePlayerName(){
        for (int i=0;i<getNumOfPlayers();i++){
            Player p = this.game.getPlayerList().get(i);
            BaseThread thread = new BaseThread(p.getSocket());
            p.setPlayerName((String)thread.decodeObj());
        }
    }

}