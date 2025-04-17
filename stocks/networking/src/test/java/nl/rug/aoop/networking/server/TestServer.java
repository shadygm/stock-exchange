package nl.rug.aoop.networking.server;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.client.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Server class.
 */
@Slf4j
class TestServer {

    public static final int PORT = 0;

    public static final String HOST = "localhost";
    public static final int PORT1 = 0;
    private Server server;
    private ServerSocket serverSocket;
    private Thread serverThread;

    /**
     * Sets up the server and starts it with a new thread before each test.
     *
     * @throws IOException if the serverSocket cannot be created.
     */
    @BeforeEach
    public void setUp() throws IOException {
        server = new Server(PORT);
        serverThread = new Thread(server);
        serverThread.start();
    }

    /**
     * Tears down the server after each test.
     */
    @AfterEach
    public void tearDown() {
        server.terminate();
        try {
            serverThread.join(1000);
        } catch (InterruptedException e) {
            log.error("Could not wait for server thread to terminate.", e);
        }
    }

    /**
     * Tests whether the server is running and can be stopped.
     *
     * @throws IOException if the serverSocket cannot be created.
     */
    @Test
    public void testRunAndStop() throws IOException {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(server.isRunning());

        server.terminate();
        try {
            serverThread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertFalse(server.isRunning());
    }

    /**
     * Tests whether the server can accept a client connection.
     */
    @Test
    public void testClientConnection() {
        InetSocketAddress isa = new InetSocketAddress(HOST, server.getPort());
        Client client;
        try {
            client = new Client(isa, null);
        } catch (IOException e) {
            log.error("Could not connect to server.", e);
            return;
        }
        Thread clientThread = new Thread(client);
        clientThread.start();

        assertTrue(client.isConnected());
        assertTrue(server.isRunning());

        client.terminate();
    }

    /**
     * Tests whether the server can accept a client connection and
     * disconnect the client successfully.
     */
    @Test
    public void testClientConnectionDisconnection() {
        InetSocketAddress isa = new InetSocketAddress(HOST, PORT);
        Client client;
        try {
            client = new Client(isa, null);
        } catch (IOException e) {
            log.error("Could not connect to server.", e);
            return;
        }

        Thread clientThread = new Thread(client);
        clientThread.start();

        assertTrue(client.isConnected());
        assertTrue(server.isRunning());

        client.terminate();

        assertFalse(client.isConnected());
        assertTrue(server.isRunning());
    }

    @Test
    public void testManyConnections() {
        InetSocketAddress isa = new InetSocketAddress(HOST, PORT);
        Client client1;
        Client client2;
        Client client3;
        try {
            client1 = new Client(isa, null);
            client2 = new Client(isa, null);
            client3 = new Client(isa, null);
        } catch (IOException e) {
            log.error("Could not connect to server.", e);
            return;
        }
        assertTrue(server.isRunning());

        assertEquals(0, server.getNumConnectedClient());
        Thread clientThread = new Thread(client1);
        clientThread.start();
        assertTrue(client1.isConnected());

        assertEquals(1, server.getNumConnectedClient());
        Thread clientThread2 = new Thread(client2);
        clientThread2.start();
        assertTrue(client2.isConnected());

        assertEquals(2, server.getNumConnectedClient());
        Thread clientThread3 = new Thread(client3);
        clientThread3.start();
        assertTrue(client3.isConnected());

        assertEquals(3, server.getNumConnectedClient());

        client1.terminate();
        assertEquals(2, server.getNumConnectedClient());

        client2.terminate();
        assertEquals(1, server.getNumConnectedClient());

        client3.terminate();
        assertEquals(0, server.getNumConnectedClient());
    }
}