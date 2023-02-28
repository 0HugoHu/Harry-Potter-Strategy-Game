package duke.edu.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.duke.server.Server;

class ServerTest {

    @Test
    void main() {
        Server server = new Server(1);
        assertEquals(1, server.getNumOfPlayers());
    }

    @Test
    void safeClose() {
    }

    @Test
    void getNumOfPlayers() {

    }
}