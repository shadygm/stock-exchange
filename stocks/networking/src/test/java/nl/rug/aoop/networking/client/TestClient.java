package nl.rug.aoop.networking.client;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.messagehandlers.MessageHandler;
import nl.rug.aoop.networking.server.Server;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the client class.
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestClient {

    private static final int PORT = 0;
    private static final String HOSTNAME = "localhost";
    private Client client;
    private Thread clientThread;
    private MessageHandler mockMessageHandler;
    private static Thread serverThread;
    private static Server server;

    /**
     * Start a server on a random port.
     * @throws IOException if the server could not be started.
     */
    @BeforeAll
    public static void startServer() throws IOException {
        server = new Server(PORT);
        serverThread = new Thread(server);
        serverThread.start();
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> server.isRunning());
    }

    /**
     * Set up a client and a message handler for the client.
     */
    @BeforeEach
    public void setUp()  {
        mockMessageHandler = Mockito.mock(MessageHandler.class);
        try {
            client = new Client(new InetSocketAddress(HOSTNAME, server.getPort()), mockMessageHandler);
            log.info("Connected to server successfully");
        } catch (IOException e) {
            log.error("Could not connect to server", e);
        }
        clientThread = new Thread(client);
        clientThread.start();
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(client::isRunning);
    }

    /**
     * Tear down the client after each test.
     */
    @AfterEach
    public void tearDown() {
        if (client != null && (client.isConnected() || client.isRunning())) {
            client.terminate();
            try {
                clientThread.join(100);
            } catch (InterruptedException e) {
                log.error("Interrupted while waiting for client thread to terminate", e);
            }
        }
    }

    /**
     * Stop the server after all tests.
     */
    @AfterAll
    public static void stopServer() {
        if (server != null) {
            server.terminate();
        }
    }

    /**
     * Testing the constructor.
     */
    @Test
    public void testConstructor() throws InterruptedException {
        assertNotNull(client);
        assertTrue(client.isConnected());
        assertTrue(client.isRunning());
    }

    /**
     * Test that a message can be sent successfully.
     */
    @Test
    public void testSendMessage() throws InterruptedException {;
        assertTrue(client.sendMessageAsync("message"));
    }

    /**
     * Test with a null message.
     */
    @Test
    public void testNullMessage() throws InterruptedException {
        client.sendMessageAsync(null);
        verifyNoInteractions(mockMessageHandler);
    }

    /**
     * Test that the client can connect to a server.
     */
    @Test
    public void testConnectionFailure() throws InterruptedException {
        Client tempClient = null;
        try {
            tempClient = new Client(new InetSocketAddress("invalidhost", 9999), mockMessageHandler);
        } catch (IOException e) {
            assertNull(tempClient);
        }
    }

    /**
     * Test that the client can be terminated
     */
    @Test
    public void testTerminate() {
        client.terminate();
        assertFalse(client.isConnected());
        assertFalse(client.isRunning());
    }

    /**
     * Test that the client can send a message after it has been terminated.
     *
     * @throws InterruptedException if the client thread is interrupted
     * while waiting for it to terminate.
     */
    @Test
    public void testSendMessageAfterTermination() throws InterruptedException {
        client.terminate();
        clientThread.join(5000);
        assertFalse(client.isConnected());
        assertFalse(client.isRunning());

        assertFalse(client.sendMessageAsync("message"));
    }
}
