package edu.duke.risc.client;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.duke.server.Server;

public class ClientTest {

    /*
    @Test
    @Disabled
    @ResourceLock(value = Resources.SYSTEM_OUT, mode = ResourceAccessMode.READ_WRITE)
    public void test_main() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes, true);

        //asks the current class to give us its ClassLoader
        InputStream input = getClass().getClassLoader().getResourceAsStream("input.txt");
        assertNotNull(input);

        InputStream expectedStream = getClass().getClassLoader().getResourceAsStream("output.txt");
        assertNotNull(expectedStream);

        InputStream oldIn = System.in;
        PrintStream oldOut = System.out;

        try {
            System.setIn(input);
            System.setOut(out);
            Client.main(new String[0]);
        } finally {
            System.setIn(oldIn);
            System.setOut(oldOut);
        }

        String expected = new String(expectedStream.readAllBytes());
        String actual = bytes.toString();
        assertEquals(expected, actual);
    }
    */

    @Test
    public void main() {

    }

    @Test
    public void getPlayerName() {
    }

    /**
     * This testing method tests functions
     * and simulates the process in the main method
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @Test
    public void getGame() throws InterruptedException {
        Server newSer = new Server(1);

        //create the server thread
        Thread serverThread = new Thread(() -> {
            try {
                newSer.acceptConnection(1);
                assertEquals(1, newSer.getNumOfPlayers());
                System.out.println("Waiting for connection from client...");
                newSer.sendToAllPlayers();
                System.out.println("Sent message to all clients...");
            } catch (Exception except) {
                except.printStackTrace();
            }
        });

        //start the server thread
        serverThread.start();
        Thread.sleep(1000);


        //create the first client thread
        Thread clientThread1 = new Thread(() -> {
            try {
                Client cli = new Client();
                System.out.println("Client created and connected...");
                assertEquals("A", cli.getPlayerName());
                cli.getGame();
                System.out.println("Client receive game info from server...");
                cli.safeClose();
                System.out.println("Client closed.");
            } catch (Exception except) {
                except.printStackTrace();
            }
        });
        //start the first client thread
        clientThread1.start();
        Thread.sleep(1000);
    }

    /**
     * This method test the methods
     * and simulates the process of multiple-clients connection
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @Test
    public void safeClose() throws InterruptedException {
        Server newSer = new Server(2);
        Thread serverThread = new Thread(() -> {
            try {
                newSer.acceptConnection(2);
                assertEquals(2, newSer.getNumOfPlayers());
                System.out.println("Waiting for connection from client...");
                newSer.sendToAllPlayers();
                System.out.println("Sent message to all clients...");
                newSer.safeClose();
                System.out.println("Server closed.");
            } catch (Exception except) {
                except.printStackTrace();
            }
        });

        serverThread.start();
        Thread.sleep(1000);

        //create the first client thread
        Thread clientThread1 = new Thread(() -> {
            try {
                Client cli1 = new Client("Allen");
                System.out.println("Client created and connected...");
                assertEquals("Allen", cli1.getPlayerName());
                cli1.getGame();
            } catch (Exception except) {
                except.printStackTrace();
            }
        });
        //start the first client thread
        clientThread1.start();
        Thread.sleep(1000);

        //create the second client thread
        Thread clientThread2 = new Thread(() -> {
            try {
                Client cli2 = new Client("Jennie");
                System.out.println("Client created and connected...");
                assertEquals("Jennie", cli2.getPlayerName());
                cli2.getGame();
            } catch (Exception except) {
                except.printStackTrace();
            }
        });
        //start the second client thread
        clientThread2.start();
        Thread.sleep(1000);
    }
}