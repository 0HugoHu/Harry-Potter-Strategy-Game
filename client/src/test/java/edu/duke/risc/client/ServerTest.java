package edu.duke.risc.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.duke.server.Server;

public class ServerTest {

    /**
     * This testing method tests functions
     * and simulates the process in the main method
     *
     * @throws Exception
     */
    @Test
    public void serverTest() throws Exception {
        Server newSer = new Server(1,24);

        //create the server thread
        Thread serverThread = new Thread(() -> {
            try {
                newSer.acceptConnection(1);
                assertEquals(1, newSer.getNumOfPlayers());
                System.out.println("Waiting for connection from client...");
                newSer.sendToAllPlayers();
                System.out.println("Sent message to all clients...");
                newSer.safeClose();
                System.out.println("Server closed.");
            } catch (Exception except) {
                except.printStackTrace();
            }
        });

        //start the server thread
        serverThread.start();
        Thread.sleep(5000);

        //create the client thread
        Thread clientThread = new Thread(() -> {
            try {
                Client cli = new Client("0.0.0.0");
                System.out.println("Client created and connected...");
            } catch (Exception except) {
                except.printStackTrace();
            }
        });
        //start the client thread
        clientThread.start();
        Thread.sleep(1000);
    }


    /**
     * This method test the methods
     * and simulates the process of multiple-clients connection
     *
     * @throws Exception
     */
    @Test
    public void server_multi_client_Test() throws Exception {
        Server newSer = new Server(2,24);
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
        Thread.sleep(5000);

        //create the first client thread
        Thread clientThread1 = new Thread(() -> {
            try {
                Client cli1 = new Client("0.0.0.0");
                System.out.println("Client created and connected...");
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
                Client cli2 = new Client("0.0.0.0");
                System.out.println("Client created and connected...");
            } catch (Exception except) {
                except.printStackTrace();
            }
        });
        //start the second client thread
        clientThread2.start();
        Thread.sleep(1000);

    }

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

}
