package edu.duke.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.duke.shared.Game;
import edu.duke.shared.helper.GameObject;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Horcrux;
import edu.duke.shared.player.House;
import edu.duke.shared.player.Player;
import edu.duke.shared.player.SkillState;
import edu.duke.shared.turn.AttackTurn;
import edu.duke.shared.turn.Turn;
import edu.duke.shared.unit.UnitType;

public class Server {
    // Port number
    private final static int PORT = 5410;
    // Number of players
    private final static int numPlayers = 4;
    // Number of units at the beginning
    private final static int numUnits = 24;
    // Gameplay controller
    private final Game game;
    // Server house mapping
    private final Map<Integer, House> serverHouseMapping = new HashMap<>();
    // Singleton method of GameObject
    private final GameObject gameObject = new GameObject(null);
    // Server socket
    private ServerSocket serverSocket;



    /**
     * Initialize Server by number of players
     *
     * @param numOfPlayers Number of players
     * @param numUnits     Number of units for each player
     */
    public Server(int numOfPlayers, int numUnits) {
        this.game = new Game(numOfPlayers, numUnits);

        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("New Game Created.\n");
    }

    /**
     * Main method
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        while (true) {
            // Create a new server
            Server server = new Server(numPlayers, numUnits);
            server.initServer();

            // Start Game
            while (server.game.getGameState() != State.GAME_OVER) {
                server.startOneTurn();
            }

            // Close server socket
            server.safeClose();
            System.out.println("Server shut down.\n");
        }
    }



    /**
     * Print host info
     */
    private void printHostInfo() {
        // Get IP address and hostname
        InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            System.out.println("------------------------------------");
            System.out.println("Your current IP address : " + ip);
            System.out.println("Your current Hostname : " + hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize server
     */
    private void initServer() {
        // Create a new server
        printHostInfo();
        System.out.println("Created a new game of " + numPlayers + " players.\nWaiting for players to join...\n");

        // Accept connections from players
        acceptConnection(numPlayers);

        // Change game state to allow players to send their names
        System.out.println("All players have joined the game. Now waiting for their names...\n");
        this.game.setGameState(State.READY_TO_INIT_NAME);
        sendToAllPlayers();

        // Receive names from all players
        waitForThreadJoin();
        System.out.println("Received all names info.\n");
        allocateTerritories();

        // Send message to all players
        this.game.setGameState(State.READY_TO_INIT_UNITS);
        System.out.println("Message sent to all players.\n");
        sendToAllPlayers();

        // Receive Units setup from all players
        waitForThreadJoin();
        System.out.println("Received all units info.\n");

        // Initialize default resources
        growResources();
    }

    /**
     * House allocation
     */
    private void houseAllocation() {
        Random random = new Random();
        while (this.serverHouseMapping.size() < this.getNumOfPlayers()) {
            int randomHouse = random.nextInt(4);
            House house = null;
            switch (randomHouse) {
                case 0:
                    house = House.GRYFFINDOR;
                    break;
                case 1:
                    house = House.HUFFLEPUFF;
                    break;
                case 2:
                    house = House.RAVENCLAW;
                    break;
                case 3:
                    house = House.SLYTHERIN;
                    break;
            }
            if (!this.serverHouseMapping.containsValue(house)) {
                this.serverHouseMapping.put(this.serverHouseMapping.size(), house);
            }
        }
    }

    /**
     * Wait until all threads are finished
     */
    private void waitForThreadJoin() {
        for (int i = 0; i < this.getNumOfPlayers(); i++) {
            this.game.getPlayerList().get(i).threadJoin();
        }
    }

    /**
     * Wait for connection from all users
     *
     * @param num number of players
     */
    public void acceptConnection(int num) {
        // House allocation
        houseAllocation();
        // Wait until all players have joined the game
        for (int i = 0; i < num; i++) {
            try {
                // Accept connection from client
                Socket socket = this.serverSocket.accept();
                // Add player to the game, i means player_id
                // Create an object, and a thread is started
                Player player = new Player(i, socket, this.serverHouseMapping.get(i));
                this.game.addPlayer(player);
                System.out.println("Player " + i + " has joined the game.");
            } catch (IOException e) {
                e.printStackTrace();
                i--;
            }
        }
        waitForThreadJoin();
    }


    /**
     * Send message to all players
     */
    public void sendToAllPlayers() {
        for (int i = 0; i < this.getNumOfPlayers(); i++) {
            // Start thread for each player
            this.game.getPlayerList().get(i).start(this.game);

            // Send message to client
            Socket clientSocket = this.game.getPlayerList().get(i).getSocket();
            this.gameObject.setSocket(clientSocket);
            // Encode player specific Id to client
            this.game.setPlayerId(i);
            this.gameObject.encodeObj(this.game);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Close server socket
     */
    public void safeClose() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * Start one turn
     */
    private void startOneTurn() {
        this.game.setGameState(State.TURN_BEGIN);
        System.out.println("Start turn " + this.game.getTurn() + ".\n");

        // Send random horcrux to players
        assignHorcrux();

        sendToAllPlayers();

        // Receive action list from all players
        waitForThreadJoin();
        System.out.println("Received all action lists.\n");


        // Do attack
        doAttackPhase();
        if (this.game.getGameState() == State.GAME_OVER) {
            return;
        }

        useHorcrux();

        detectSkill();
        useSkill();



        growUnits();
        growResources();
        this.game.setGameState(State.TURN_END);
        sendToAllPlayers();

        // Turn end
        waitForThreadJoin();
        System.out.println("End turn " + this.game.getTurn() + ".\n");
        this.game.turnComplete();

        // Sleep for a while or two objects will be sent at the same time
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public void useHorcrux() {
        int ringCount=0;
        int loketCount=0;
        int snakeCount=0;
        for(int i=0;i<this.getNumOfPlayers();i++) {
            Player p=this.game.getPlayerList().get(i).playerThread.currGame.getPlayerList().get(i);
                for (Map.Entry<Horcrux, Integer> entry : p.getHorcruxesList().entrySet()) {
                    for (int k = 0; k < entry.getValue(); k++) {
                        if (entry.getKey().equals(Horcrux.SNAKE)) {
                            this.game.useSnake(p);
                            snakeCount++;
                        }
                        if (entry.getKey().equals(Horcrux.LOCKET)) {
                            this.game.useLocket(p);
                            loketCount++;
                        }
                        if (entry.getKey().equals(Horcrux.RING)) {
                            this.game.useRing(p);
                            ringCount++;
                        }
                    }
                }
        }

        for(int i=0;i<this.getNumOfPlayers();i++) {
            Player p=this.game.getPlayerList().get(i).playerThread.currGame.getPlayerList().get(i);
            for (Map.Entry<Horcrux, Integer> entry : p.getHorcruxesList().entrySet()) {
                    if (entry.getKey().equals(Horcrux.SNAKE)) {
                        p.removeFromHorcruxUsage(Horcrux.SNAKE,snakeCount);
                    }

                    if (entry.getKey().equals(Horcrux.LOCKET)) {
                        p.removeFromHorcruxUsage(Horcrux.LOCKET,loketCount);
                    }

                    if (entry.getKey().equals(Horcrux.RING)) {
                        p.removeFromHorcruxUsage(Horcrux.RING,ringCount);
                    }

            }
        }


    }


    public void detectSkill(){
        for(int i=0;i<this.getNumOfPlayers();i++){
            Player p=this.game.getPlayerList().get(i).playerThread.currGame.getPlayerList().get(i);
            System.out.println("skill used: "+p.getSkillUsed());
            if(p.getSkillState().equals(SkillState.IN_EFFECT)){
                this.game.getPlayerList().get(i).setSkillState(SkillState.USED);
                p.setSkillState(SkillState.USED);
            }
        }
    }


    public void useSkill() {
        for(int i=0;i<this.getNumOfPlayers();i++){
            Player p=this.game.getPlayerList().get(i).playerThread.currGame.getPlayerList().get(i);
                if (p.getHouse().equals(House.GRYFFINDOR)) {
                    if(p.getSkillUsed()==1){
                        this.game.getPlayerList().get(i).setSkillState(SkillState.IN_EFFECT);
                        p.setSkillState(SkillState.IN_EFFECT);
                        this.game.UseSkillGryffindor(p);
                    }
                    p.addSkill();
                }
                if (p.getHouse().equals(House.RAVENCLAW)) {
                    if(p.getSkillUsed()==1){
                        this.game.getPlayerList().get(i).setSkillState(SkillState.IN_EFFECT);
                        p.setSkillState(SkillState.IN_EFFECT);
                    }
                    p.addSkill();
                }
                if (p.getHouse().equals(House.HUFFLEPUFF)) {
                    if(p.getSkillUsed()==1){
                        this.game.getPlayerList().get(i).setSkillState(SkillState.IN_EFFECT);
                        p.setSkillState(SkillState.IN_EFFECT);
                    }
                    p.addSkill();
                }
                if(p.getHouse().equals(House.SLYTHERIN)){
                    if(p.getSkillUsed()==1){
                        this.game.getPlayerList().get(i).setSkillState(SkillState.IN_EFFECT);
                        p.setSkillState(SkillState.IN_EFFECT);
                        this.game.UseSkillSytherin(p);
                    }
                    p.addSkill();
                }
        }
    }


    private void assignHorcrux() {
        if (this.game.getTurn() % 2 == 0) {
            Random random = new Random();
            int randomPlayer = random.nextInt(this.game.getNumPlayers());
            int randomHorcrux = random.nextInt(6);
            switch (randomHorcrux) {
                case 0:
                    this.game.getPlayerList().get(randomPlayer).addToHorcruxStorage(Horcrux.LOCKET, 1);
                    System.out.println(this.game.getPlayerList().get(randomPlayer).getPlayerName() + " get the Locket!");
                    this.game.setNewHorcrux(Horcrux.LOCKET, randomPlayer);
                    break;
                case 1:
                    this.game.getPlayerList().get(randomPlayer).addToHorcruxStorage(Horcrux.HAT, 1);
                    System.out.println(this.game.getPlayerList().get(randomPlayer).getPlayerName() + " get the Hat!");
                    this.game.setNewHorcrux(Horcrux.HAT, randomPlayer);
                    break;
                case 2:
                    this.game.getPlayerList().get(randomPlayer).addToHorcruxStorage(Horcrux.DIARY, 1);
                    System.out.println(this.game.getPlayerList().get(randomPlayer).getPlayerName() + " get the Diary!");
                    this.game.setNewHorcrux(Horcrux.DIARY, randomPlayer);
                    break;
                case 3:
                    this.game.getPlayerList().get(randomPlayer).addToHorcruxStorage(Horcrux.RING, 1);
                    System.out.println(this.game.getPlayerList().get(randomPlayer).getPlayerName() + " get the Ring!");
                    this.game.setNewHorcrux(Horcrux.RING, 0);
                    break;
                case 4:
                    this.game.getPlayerList().get(randomPlayer).addToHorcruxStorage(Horcrux.CUP, 1);
                    System.out.println(this.game.getPlayerList().get(randomPlayer).getPlayerName() + " get the Cup!");
                    this.game.setNewHorcrux(Horcrux.CUP, randomPlayer);
                    break;
                case 5:
                    this.game.getPlayerList().get(randomPlayer).addToHorcruxStorage(Horcrux.SNAKE, 1);
                    System.out.println(this.game.getPlayerList().get(randomPlayer).getPlayerName() + " get the Snake!");
                    this.game.setNewHorcrux(Horcrux.SNAKE, randomPlayer);
                    break;
            }
        } else {
            this.game.setNoHorcrux();
        }
    }

    /**
     * Allocate territories to players
     */
    public void allocateTerritories() {
        this.game.allocateTerritories();
    }

    /**
     * Do attack phase
     */
    public void doAttackPhase() {
        // Make Attack List
        int turnIndex = this.game.getTurn();
        for (Map.Entry<Integer, ArrayList<Turn>> entry : this.game.getTurnList().entrySet()) {
            this.game.makeAttackList((AttackTurn) entry.getValue().get(1));
        }
        this.game.doAttack();
        //this.game.printUnit();

        // Check if game is over
        for (Player p : this.game.getPlayerList()) {
            int numTerrs = this.game.getMap().getTerritoriesByOwner(p.getPlayerName()).size();
            // If one player has all territories, game over
            if (numTerrs == this.game.getMap().getNumTerritories()) {
                this.game.setGameState(State.GAME_OVER);
                this.game.setWinnerId(p.getPlayerId());
                System.out.println("Game Over. Player " + p.getPlayerName() + " wins.\n");
                sendToAllPlayers();
            }
            if (numTerrs == 0) {
                this.game.addLoserId(p.getPlayerId());
                System.out.println("Player " + p.getPlayerName() + " has lost the game. Game continues.\n");
            }
        }
        if (this.game.getLoserId().size() == this.game.getNumPlayers() - 1) {
            this.game.setGameState(State.GAME_OVER);
            for (Player p : this.game.getPlayerList()) {
                if (!this.game.isLoser(p.getPlayerId())) {
                    this.game.setWinnerId(p.getPlayerId());
                    System.out.println("Game Over. Player " + p.getPlayerName() + " wins.\n");
                    sendToAllPlayers();
                }
            }
        }
    }

    /**
     * Grow unit of each territory by 1
     */
    public void growUnits() {
        //After each turn, all territories should add one new unit
        for (Territory t : this.game.getMap().getTerritories()) {
            t.addUnit(UnitType.GNOME);
        }
    }



    /**
     * Grow resources for each corresponding territory
     */
    public void growResources() {
        for (Player p : this.game.getPlayerList()) {
            // Update the player's resources
            p.updateResources(this.game.getMap().getTerritoriesByOwner(p.getPlayerName()));
        }
    }

}